import { useState } from 'react'
import { placeholderCv } from '../data/placeholders'
import type { RoleId } from '../types/cv'
import { DownloadIcon } from './DownloadIcon'
import '../styles/cv.css'

const roleLabels: Record<RoleId, string> = {
  java: 'Java',
  backend: 'Backend',
  fullstack: 'Full Stack',
  'ml-ai': 'ML / AI',
}

export function CvPage() {
  const data = placeholderCv
  const [selectedRoleId, setSelectedRoleId] = useState<RoleId>(
    data.defaultRoleId,
  )
  const selectedLabel = roleLabels[selectedRoleId]

  return (
    <div className="cv-page">
      <header className="cv-shell cv-nav">
        <div className="cv-brand">
          <span className="cv-brand__mark">{data.brandMark}</span>
          <span className="cv-brand__title">{data.siteTitle}</span>
        </div>
        <nav className="cv-nav__actions" aria-label="External links">
          {data.contactLinks.map((link) => (
            <a
              key={link.label}
              className="cv-nav__link"
              href={link.url}
              target="_blank"
              rel="noreferrer"
            >
              {link.label}
            </a>
          ))}
          <a className="cv-btn cv-btn--primary" href="#download-pdf">
            <DownloadIcon />
            Download PDF
          </a>
        </nav>
      </header>

      <section className="cv-shell cv-hero" aria-labelledby="cv-name">
        <div className="cv-hero__atmosphere" aria-hidden="true" />
        <div className="cv-hero__copy">
          <h1 id="cv-name" className="cv-hero__name">
            {data.fullName}
          </h1>
          <p className="cv-hero__headline">{data.headline}</p>
          <div className="cv-hero__meta">
            <span>{data.location}</span>
            <span className="cv-hero__dot" aria-hidden="true" />
            <a href={`mailto:${data.email}`}>{data.email}</a>
          </div>
        </div>

        <div className="cv-roles">
          <div className="cv-roles__label-row">
            <span className="cv-label">View CV as</span>
            <span className="cv-hint">
              Content reorders by role relevance
            </span>
          </div>
          <div
            className="cv-roles__chips"
            role="group"
            aria-label="Role profile"
          >
            {data.roles.map((role) => (
              <button
                key={role.id}
                type="button"
                className={
                  role.id === selectedRoleId
                    ? 'cv-role-chip is-active'
                    : 'cv-role-chip'
                }
                aria-pressed={role.id === selectedRoleId}
                onClick={() => setSelectedRoleId(role.id)}
              >
                {role.label}
              </button>
            ))}
          </div>
        </div>
      </section>

      <section
        className="cv-shell cv-section"
        aria-labelledby="introduction-heading"
      >
        <div className="cv-section__label-row">
          <h2 id="introduction-heading" className="cv-label">
            Introduction
          </h2>
          <span className="cv-badge">Tailored for {selectedLabel}</span>
        </div>
        <div className="cv-intro__body">
          <p className="cv-intro__text">
            {data.introductions[selectedRoleId]}
          </p>
        </div>
      </section>

      <section
        className="cv-shell cv-section"
        aria-labelledby="experience-heading"
      >
        <div className="cv-section__header">
          <h2 id="experience-heading" className="cv-label">
            Experience
          </h2>
          <span className="cv-hint cv-hint--accent">
            Ordered by {selectedLabel} relevance
          </span>
        </div>
        {data.experience.map((job) => (
          <article key={`${job.company}-${job.title}`} className="cv-entry">
            <div className="cv-entry__header">
              <div className="cv-entry__titles">
                <h3 className="cv-entry__title">{job.title}</h3>
                <p className="cv-entry__subtitle">{job.company}</p>
              </div>
              <div className="cv-entry__meta">
                <span className="cv-entry__dates">{job.dates}</span>
                <span className="cv-entry__location">{job.location}</span>
              </div>
            </div>
            <ul className="cv-bullets">
              {job.bullets.map((bullet) => (
                <li key={bullet}>
                  <span className="cv-bullets__mark" aria-hidden="true">
                    —
                  </span>
                  <span>{bullet}</span>
                </li>
              ))}
            </ul>
          </article>
        ))}
      </section>

      <section className="cv-shell cv-section" aria-labelledby="skills-heading">
        <div className="cv-section__header">
          <h2 id="skills-heading" className="cv-label">
            Skills
          </h2>
          <span className="cv-hint cv-hint--accent">
            Highlighted skills match {selectedLabel} tags
          </span>
        </div>
        <div className="cv-skills">
          {data.skills.map((group) => (
            <div key={group.name} className="cv-skill-group">
              <h3 className="cv-skill-group__name">{group.name}</h3>
              <ul className="cv-skill-chips">
                {group.skills.map((skill) => (
                  <li
                    key={skill.name}
                    className={
                      skill.highlighted
                        ? 'cv-skill-chip is-highlighted'
                        : 'cv-skill-chip'
                    }
                  >
                    {skill.name}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </section>

      <section
        className="cv-shell cv-section"
        aria-labelledby="projects-heading"
      >
        <div className="cv-section__header">
          <h2 id="projects-heading" className="cv-label">
            Projects
          </h2>
          <span className="cv-hint cv-hint--accent">Most relevant first</span>
        </div>
        <div className="cv-projects">
          {data.projects.map((project) => (
            <article key={project.title} className="cv-project">
              <div className="cv-project__header">
                <h3 className="cv-project__title">{project.title}</h3>
                <div className="cv-project__links">
                  {project.githubUrl ? (
                    <a
                      className="cv-project__link"
                      href={project.githubUrl}
                      target="_blank"
                      rel="noreferrer"
                    >
                      GitHub
                    </a>
                  ) : null}
                  {project.liveUrl ? (
                    <a
                      className="cv-project__link"
                      href={project.liveUrl}
                      target="_blank"
                      rel="noreferrer"
                    >
                      Live
                    </a>
                  ) : null}
                </div>
              </div>
              <p className="cv-project__description">{project.description}</p>
              <ul className="cv-bullets">
                {project.bullets.map((bullet) => (
                  <li key={bullet}>
                    <span className="cv-bullets__mark" aria-hidden="true">
                      —
                    </span>
                    <span>{bullet}</span>
                  </li>
                ))}
              </ul>
              <ul className="cv-tech-chips">
                {project.technologies.map((tech) => (
                  <li key={tech} className="cv-tech-chip">
                    {tech}
                  </li>
                ))}
              </ul>
            </article>
          ))}
        </div>
      </section>

      <section
        className="cv-shell cv-section"
        aria-labelledby="education-heading"
      >
        <div className="cv-section__header">
          <h2 id="education-heading" className="cv-label">
            Education
          </h2>
        </div>
        {data.education.map((edu) => (
          <article
            key={`${edu.institution}-${edu.degree}`}
            className="cv-entry"
          >
            <div className="cv-entry__header">
              <div className="cv-entry__titles">
                <h3 className="cv-entry__title">{edu.degree}</h3>
                <p className="cv-entry__subtitle">{edu.institution}</p>
              </div>
              <div className="cv-entry__meta">
                <span className="cv-entry__dates">{edu.dates}</span>
              </div>
            </div>
            <ul className="cv-bullets">
              {edu.bullets.map((bullet) => (
                <li key={bullet}>
                  <span className="cv-bullets__mark" aria-hidden="true">
                    —
                  </span>
                  <span>{bullet}</span>
                </li>
              ))}
            </ul>
          </article>
        ))}
      </section>

      <footer className="cv-footer">
        <div className="cv-shell cv-footer__inner">
          <div className="cv-footer__copy">
            <p className="cv-footer__name">{data.fullName}</p>
            <p className="cv-footer__note">
              Content served from PostgreSQL · Role relevance computed on
              request
            </p>
          </div>
          <div className="cv-footer__actions">
            <a className="cv-btn cv-btn--accent" href={`mailto:${data.email}`}>
              Contact
            </a>
            <a className="cv-btn cv-btn--ghost" href="#download-pdf">
              Download PDF
            </a>
          </div>
        </div>
      </footer>
    </div>
  )
}
