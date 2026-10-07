-- Local-only CV seed data (issue #18).
-- Loaded when Flyway locations include classpath:db/seed (see application-local.properties.example).
--
-- How to use:
-- 1. Replace the placeholder values below with your real CV content.
-- 2. Add/remove rows as needed (copy a block and edit).
-- 3. Prefer looking up FKs by slug/name (as below) instead of hard-coding ids.
-- 4. Reset DB when you want a clean re-seed: docker compose down -v && docker compose up -d
--
-- This script is guarded so it only runs when no profile exists yet.

-- ---------------------------------------------------------------------------
-- Tags
-- ---------------------------------------------------------------------------

INSERT INTO tag (slug, name)
SELECT v.slug, v.name
FROM (VALUES
    ('java', 'Java'),
    ('spring', 'Spring'),
    ('backend', 'Backend'),
    ('frontend', 'Frontend'),
    ('fullstack', 'Full Stack'),
    ('python', 'Python'),
    ('ml', 'Machine Learning'),
    ('ai', 'AI'),
    ('data', 'Data'),
    ('docker', 'Docker'),
    ('api', 'API'),
    ('database', 'Database')
    -- ('your-slug', 'Your Tag Name')
) AS v(slug, name)
WHERE NOT EXISTS (SELECT 1 FROM tag t WHERE t.slug = v.slug);

-- ---------------------------------------------------------------------------
-- Role profiles
-- type: GENERAL | PERMANENT | VACANCY  (only one GENERAL allowed)
-- ---------------------------------------------------------------------------

INSERT INTO role_profile (slug, name, type, sort_order)
SELECT v.slug, v.name, v.type, v.sort_order
FROM (VALUES
    ('general', 'General', 'GENERAL', 0),
    ('backend', 'Backend Developer', 'PERMANENT', 1),
    ('java', 'Java Developer', 'PERMANENT', 2),
    ('fullstack', 'Full-Stack Developer', 'PERMANENT', 3),
    ('ml-ai', 'ML / AI', 'PERMANENT', 4)
    -- ('role', 'Role Name', 'PERMANENT', 5)
) AS v(slug, name, type, sort_order)
WHERE NOT EXISTS (SELECT 1 FROM role_profile rp WHERE rp.slug = v.slug);

-- Weighted tags per role profile (weight: 0.000 .. 1.000)
INSERT INTO role_profile_tag (role_profile_id, tag_id, weight)
SELECT rp.id, t.id, v.weight
FROM (VALUES
    -- general
    ('general', 'backend', 0.500),
    ('general', 'frontend', 0.500),
    -- java
    ('java', 'java', 1.000),
    ('java', 'spring', 1.000),
    ('java', 'backend', 0.900),
    ('java', 'api', 0.800),
    ('java', 'database', 0.700),
    ('java', 'docker', 0.400),
    -- backend
    ('backend', 'backend', 1.000),
    ('backend', 'api', 0.900),
    ('backend', 'database', 0.800),
    ('backend', 'java', 0.700),
    ('backend', 'spring', 0.700),
    ('backend', 'docker', 0.500)
    -- ('role-slug', 'tag-slug', 0.800)
) AS v(role_slug, tag_slug, weight)
JOIN role_profile rp ON rp.slug = v.role_slug
JOIN tag t ON t.slug = v.tag_slug
WHERE NOT EXISTS (
    SELECT 1
    FROM role_profile_tag rpt
    WHERE rpt.role_profile_id = rp.id
      AND rpt.tag_id = t.id
);

-- ---------------------------------------------------------------------------
-- Profile + contact links
-- Seed stops here if a profile already exists.
-- ---------------------------------------------------------------------------

INSERT INTO profile (full_name, headline, email, phone, location)
SELECT
    'Bob Bobson',
    'A master fullstack developer',
    'Bob@email.com',
    '5555 5555',
    'City, Nation'
WHERE NOT EXISTS (SELECT 1 FROM profile);

INSERT INTO contact_link (profile_id, label, url)
SELECT p.id, v.label, v.url
FROM profile p
CROSS JOIN (VALUES
    ('GitHub', 'https://github.com/bob'),
    ('LinkedIn', 'https://www.linkedin.com/in/bob'),
    ('Website', 'https://bob-site.example')
    -- ('Label', 'https://...')
) AS v(label, url)
WHERE NOT EXISTS (
    SELECT 1 FROM contact_link cl WHERE cl.profile_id = p.id AND cl.label = v.label
);

-- ---------------------------------------------------------------------------
-- Introductions (one per role profile)
-- ---------------------------------------------------------------------------

INSERT INTO introduction (profile_id, role_profile_id, content)
SELECT p.id, rp.id, v.content
FROM profile p
CROSS JOIN (VALUES
    ('general', 'General introduction about Bob'),
    ('java', 'Java-focused introduction about Bob.'),
    ('backend', 'Backend-focused introduction about Bob.')
    -- ('role-slug', 'Introduction text...')
) AS v(role_slug, content)
JOIN role_profile rp ON rp.slug = v.role_slug
WHERE NOT EXISTS (
    SELECT 1 FROM introduction i WHERE i.role_profile_id = rp.id
);

-- ---------------------------------------------------------------------------
-- Experience + bullets
-- ---------------------------------------------------------------------------

WITH new_experience AS (
    INSERT INTO experience (profile_id, company, title, location, start_date, end_date)
    SELECT p.id, v.company, v.title, v.location, v.start_date::date, v.end_date::date
    FROM profile p
    CROSS JOIN (VALUES
        (
            'Example Company',
            'Software Engineer',
            'City, Country',
            '2020-01-01',
            NULL                 -- NULL = current role
        )
        -- (
        --     'Another Company',
        --     'Intern',
        --     'Town, Country',
        --     '2018-06-01',
        --     '2018-08-31'
        -- )
    ) AS v(company, title, location, start_date, end_date)
    WHERE NOT EXISTS (
        SELECT 1
        FROM experience e
        WHERE e.profile_id = p.id
          AND e.company = v.company
          AND e.title = v.title
          AND e.start_date = v.start_date::date
    )
    RETURNING id, company, title
)
INSERT INTO experience_bullet (experience_id, content)
SELECT e.id, b.content
FROM new_experience e
JOIN (VALUES
    ('Example Company', 'Software Engineer', 'Built REST APIs with Java and Spring Boot.'),
    ('Example Company', 'Software Engineer', 'Worked with PostgreSQL and Docker.')
    -- ('Company', 'Title', 'Bullet text')
) AS b(company, title, content)
  ON e.company = b.company AND e.title = b.title;

-- ---------------------------------------------------------------------------
-- Education + bullets
-- ---------------------------------------------------------------------------

WITH new_education AS (
    INSERT INTO education (profile_id, institution, degree, field, start_date, end_date)
    SELECT p.id, v.institution, v.degree, v.field, v.start_date::date, v.end_date::date
    FROM profile p
    CROSS JOIN (VALUES
        (
            'University of Example',
            'BSc',
            'Computer Science',
            '2016-09-01',
            '2019-06-30'
        )
        -- (
        --     'Another School',
        --     'MSc',
        --     'Software Engineering',
        --     '2019-09-01',
        --     '2021-06-30'
        -- )
    ) AS v(institution, degree, field, start_date, end_date)
    WHERE NOT EXISTS (
        SELECT 1
        FROM education ed
        WHERE ed.profile_id = p.id
          AND ed.institution = v.institution
          AND ed.degree = v.degree
          AND ed.field = v.field
    )
    RETURNING id, institution, degree, field
)
INSERT INTO education_bullet (education_id, content)
SELECT e.id, b.content
FROM new_education e
JOIN (VALUES
    ('University of Example', 'BSc', 'Computer Science', 'Algorithms and data structures'),
    ('University of Example', 'BSc', 'Computer Science', 'Databases and software engineering')
    -- ('Institution', 'Degree', 'Field', 'Bullet text')
) AS b(institution, degree, field, content)
  ON e.institution = b.institution
 AND e.degree = b.degree
 AND e.field = b.field;

-- ---------------------------------------------------------------------------
-- Skill categories + skills
-- ---------------------------------------------------------------------------

WITH new_category AS (
    INSERT INTO skill_category (profile_id, name)
    SELECT p.id, v.name
    FROM profile p
    CROSS JOIN (VALUES
        ('Backend'),
        ('Frontend')
        -- ('DevOps')
    ) AS v(name)
    WHERE NOT EXISTS (
        SELECT 1 FROM skill_category sc WHERE sc.profile_id = p.id AND sc.name = v.name
    )
    RETURNING id, name
)
INSERT INTO skill (category_id, name)
SELECT c.id, s.name
FROM new_category c
JOIN (VALUES
    ('Backend', 'Java'),
    ('Backend', 'Spring Boot'),
    ('Backend', 'PostgreSQL'),
    ('Frontend', 'React'),
    ('Frontend', 'TypeScript')
    -- ('Category', 'Skill')
) AS s(category, name) ON c.name = s.category;

-- ---------------------------------------------------------------------------
-- Projects + bullets + technologies
-- ---------------------------------------------------------------------------

WITH new_project AS (
    INSERT INTO project (profile_id, title, description, github_url, live_url)
    SELECT p.id, v.title, v.description, v.github_url, v.live_url
    FROM profile p
    CROSS JOIN (VALUES
        (
            'Example Project',
            'Short description of the project.',
            'https://github.com/bob/example',
            'https://example.com'
        )
        -- (
        --     'Another Project',
        --     'Description...',
        --     'https://github.com/...',
        --     NULL
        -- )
    ) AS v(title, description, github_url, live_url)
    WHERE NOT EXISTS (
        SELECT 1 FROM project pr WHERE pr.profile_id = p.id AND pr.title = v.title
    )
    RETURNING id, title
)
INSERT INTO project_bullet (project_id, content)
SELECT p.id, b.content
FROM new_project p
JOIN (VALUES
    ('Example Project', 'Implemented a REST API with Spring Boot.'),
    ('Example Project', 'Stored data in PostgreSQL with Flyway migrations.')
    -- ('Project Title', 'Bullet text')
) AS b(title, content) ON p.title = b.title;

INSERT INTO project_technology (project_id, name)
SELECT p.id, t.name
FROM project p
JOIN (VALUES
    ('Example Project', 'Java'),
    ('Example Project', 'Spring Boot'),
    ('Example Project', 'PostgreSQL')
    -- ('Project Title', 'Technology')
) AS t(title, name) ON p.title = t.title
WHERE NOT EXISTS (
    SELECT 1
    FROM project_technology pt
    WHERE pt.project_id = p.id AND pt.name = t.name
);

-- Content tagging (experience_bullet_tag, education_bullet_tag, skill_tag,
-- project_bullet_tag) can be added later when you wire relevance (#34).
