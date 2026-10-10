import type { CvData } from '../types/cv'

/** Static sample kept as a shape reference. The app loads live data via the API. */
export const placeholderCv: CvData = {
  siteTitle: 'Dynamic CV',
  brandMark: 'YN',
  fullName: 'Your Full Name',
  headline:
    'Short headline — e.g. Full-stack developer connecting APIs with clear interfaces.',
  location: 'City, Country',
  email: 'you@example.com',
  contactLinks: [
    { label: 'GitHub', url: 'https://github.com/your-username' },
    { label: 'LinkedIn', url: 'https://www.linkedin.com/in/your-username' },
  ],
  roles: [
    { id: 'java', label: 'Java' },
    { id: 'backend', label: 'Backend' },
    { id: 'fullstack', label: 'Full Stack' },
    { id: 'ml-ai', label: 'ML / AI' },
  ],
  defaultRoleId: 'fullstack',
  introductions: {
    java: 'Placeholder Java-focused introduction.',
    backend: 'Placeholder backend-focused introduction.',
    fullstack: 'Placeholder full-stack introduction.',
    'ml-ai': 'Placeholder ML / AI introduction.',
  },
  experience: [
    {
      id: 1,
      title: 'Job Title',
      company: 'Example Company',
      location: 'City',
      dates: '2023 — Present',
      bullets: [
        'Placeholder bullet describing a concrete achievement or responsibility.',
        'Another placeholder bullet about tools, collaboration, or impact.',
      ],
    },
  ],
  skills: [
    {
      name: 'Backend',
      skills: [
        { name: 'Skill A', highlighted: true },
        { name: 'Skill B' },
      ],
    },
  ],
  projects: [
    {
      id: 1,
      title: 'Example Project',
      description: 'Short placeholder description of what the project does.',
      githubUrl: 'https://github.com/your-username/example',
      liveUrl: 'https://example.com',
      bullets: ['Placeholder project bullet about architecture or delivery.'],
      technologies: ['Tech A', 'Tech B'],
    },
  ],
  education: [
    {
      id: 1,
      degree: 'Degree — Field of Study',
      institution: 'University Name',
      dates: '2019 — 2023',
      bullets: [
        'Placeholder education bullet (coursework, thesis, or focus areas).',
      ],
    },
  ],
}
