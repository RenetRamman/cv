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

export interface SkillItem {
  id: number
  name: string
  highlighted?: boolean
}

export interface SkillCategory {
  name: string
  skills: SkillItem[]
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

/** View model for the public CV page (mapped from GET /api/cv). */
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
  selectedRoleId: RoleId
  selectedRoleName: string
  introduction: string
  /** Kept for compatibility; prefer `introduction` for the active role. */
  introductions: Record<RoleId, string>
  experience: ExperienceEntry[]
  skills: SkillCategory[]
  projects: ProjectEntry[]
  education: EducationEntry[]
}
