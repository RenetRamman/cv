export type RoleId = 'java' | 'backend' | 'fullstack' | 'ml-ai'

export interface RoleOption {
  id: RoleId
  label: string
}

export interface ContactLink {
  label: string
  url: string
}

export interface ExperienceEntry {
  title: string
  company: string
  location: string
  dates: string
  bullets: string[]
}

export interface SkillCategory {
  name: string
  skills: { name: string; highlighted?: boolean }[]
}

export interface ProjectEntry {
  title: string
  description: string
  githubUrl?: string
  liveUrl?: string
  bullets: string[]
  technologies: string[]
}

export interface EducationEntry {
  degree: string
  institution: string
  dates: string
  bullets: string[]
}

export interface CvPlaceholderData {
  siteTitle: string
  brandMark: string
  fullName: string
  headline: string
  location: string
  email: string
  contactLinks: ContactLink[]
  roles: RoleOption[]
  defaultRoleId: RoleId
  introductions: Record<RoleId, string>
  experience: ExperienceEntry[]
  skills: SkillCategory[]
  projects: ProjectEntry[]
  education: EducationEntry[]
}
