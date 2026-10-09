import type { CvPlaceholderData } from '../types/cv'

/** Static placeholder content for layout work (issue #24). Replaced by API data in #25. */
export const placeholderCv: CvPlaceholderData = {
  siteTitle: 'Dynamic CV',
  brandMark: 'CV',
  fullName: 'Bob Bobson',
  headline:
    'Full-stack developer connecting APIs with clear interfaces.',
  location: 'City, Country',
  email: 'Bob@example.com',
  contactLinks: [
    { label: 'GitHub', url: 'https://github.com/bob' },
    { label: 'LinkedIn', url: 'https://www.linkedin.com/in/bob' },
  ],
  roles: [
    { id: 'java', label: 'Java' },
    { id: 'backend', label: 'Backend' },
    { id: 'fullstack', label: 'Full Stack' },
    { id: 'ml-ai', label: 'ML / AI' },
  ],
  defaultRoleId: 'fullstack',
  introductions: {
    java: 'Placeholder Java-focused introduction. Describe how you approach JVM services, APIs, and data.',
    backend:
      'Placeholder backend-focused introduction. Describe APIs, data models, and reliability work.',
    fullstack:
      'Placeholder full-stack introduction. Describe how you deliver features across API and UI layers.',
    'ml-ai':
      'Placeholder ML / AI introduction. Describe modeling, evaluation, and how it connects to product work.',
  },
  experience: [
    {
      title: 'Job Title',
      company: 'Example Company',
      location: 'City',
      dates: '2023 — Present',
      bullets: [
        'Placeholder bullet describing a concrete achievement or responsibility.',
        'Another placeholder bullet about tools, collaboration, or impact.',
        'Optional third bullet for depth on this role.',
      ],
    },
    {
      title: 'Earlier Job Title',
      company: 'Another Company',
      location: 'City',
      dates: '2021 — 2023',
      bullets: [
        'Placeholder bullet from an earlier role.',
        'Second placeholder bullet with measurable or clear outcome language.',
      ],
    },
  ],
  skills: [
    {
      name: 'Backend',
      skills: [
        { name: 'Skill A', highlighted: true },
        { name: 'Skill B', highlighted: true },
        { name: 'Skill C' },
        { name: 'Skill D' },
      ],
    },
    {
      name: 'Frontend',
      skills: [
        { name: 'Skill E', highlighted: true },
        { name: 'Skill F', highlighted: true },
        { name: 'Skill G' },
      ],
    },
    {
      name: 'Tools',
      skills: [
        { name: 'Skill H', highlighted: true },
        { name: 'Skill I' },
        { name: 'Skill J' },
      ],
    },
  ],
  projects: [
    {
      title: 'Example Project',
      description:
        'Short placeholder description of what the project does and why it matters.',
      githubUrl: 'https://github.com/bob/example',
      liveUrl: 'https://example.com',
      bullets: [
        'Placeholder project bullet about architecture or delivery.',
        'Second placeholder bullet about a notable detail.',
      ],
      technologies: ['Tech A', 'Tech B', 'Tech C', 'Tech D'],
    },
    {
      title: 'Second Project',
      description: 'Another short placeholder project description.',
      githubUrl: 'https://github.com/bob/second',
      bullets: [
        'Placeholder bullet for the second project.',
      ],
      technologies: ['Tech A', 'Tech B', 'Tech E'],
    },
  ],
  education: [
    {
      degree: 'Degree — Field of Study',
      institution: 'University Name',
      dates: '2019 — 2023',
      bullets: [
        'Placeholder education bullet (coursework, thesis, or focus areas).',
        'Second placeholder education bullet.',
      ],
    },
  ],
}
