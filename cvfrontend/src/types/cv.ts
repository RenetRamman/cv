/** Role profile slug from the API (e.g. java, backend, fullstack). */
export type RoleId = string

export interface RoleOption {
  id: RoleId
  label: string
}

export interface ContactLink {
  label: string
  url: string
}

export interface ExperienceEntry {
  id: number
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
  id: number
  title: string
  description: string
  githubUrl?: string
  liveUrl?: string
  bullets: string[]
  technologies: string[]
}

export interface EducationEntry {
  id: number
  degree: string
  institution: string
  dates: string
  bullets: string[]
}

/** View model for the public CV page (mapped from REST responses). */
export interface CvData {
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
