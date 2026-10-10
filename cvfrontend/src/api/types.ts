export type RoleProfileType = 'GENERAL' | 'PERMANENT' | 'VACANCY'

export interface ProfileResponse {
  id: number
  fullName: string
  headline: string
  email: string
  phone: string | null
  location: string | null
  contactLinks: { id: number; label: string; url: string }[]
}

export interface RoleProfileResponse {
  id: number
  slug: string
  name: string
  type: RoleProfileType
  sortOrder: number
  tags: unknown[]
}

export interface IntroductionResponse {
  id: number
  roleProfileId: number
  roleProfileSlug: string
  roleProfileName: string
  roleProfileType: RoleProfileType
  content: string
}

export interface ExperienceResponse {
  id: number
  company: string
  title: string
  location: string | null
  startDate: string
  endDate: string | null
  bullets: { id: number; content: string }[]
}

export interface EducationResponse {
  id: number
  institution: string
  degree: string
  field: string
  startDate: string | null
  endDate: string | null
  bullets: { id: number; content: string }[]
}

export interface SkillCategoryResponse {
  id: number
  name: string
  skills: { id: number; name: string }[]
}

export interface ProjectResponse {
  id: number
  title: string
  description: string | null
  githubUrl: string | null
  liveUrl: string | null
  bullets: { id: number; content: string }[]
  technologies: { id: number; name: string }[]
}
