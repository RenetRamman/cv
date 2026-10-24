export type RoleProfileType = 'GENERAL' | 'PERMANENT' | 'VACANCY'

/** Aggregated role-aware CV from GET /api/cv */
export interface CvApiResponse {
  selectedRoleSlug: string
  selectedRoleName: string
  fullName: string
  headline: string
  email: string
  phone: string | null
  location: string | null
  contactLinks: { label: string; url: string }[]
  roles: { slug: string; name: string; sortOrder: number }[]
  introduction: string
  experiences: {
    id: number
    company: string
    title: string
    location: string | null
    startDate: string
    endDate: string | null
    relevanceScore: number
    bullets: { id: number; content: string; relevanceScore: number }[]
  }[]
  educations: {
    id: number
    institution: string
    degree: string
    field: string
    startDate: string | null
    endDate: string | null
    relevanceScore: number
    bullets: { id: number; content: string; relevanceScore: number }[]
  }[]
  skillCategories: {
    id: number
    name: string
    relevanceScore: number
    skills: {
      id: number
      name: string
      relevanceScore: number
      highlighted: boolean
    }[]
  }[]
  projects: {
    id: number
    title: string
    description: string | null
    githubUrl: string | null
    liveUrl: string | null
    relevanceScore: number
    bullets: { id: number; content: string; relevanceScore: number }[]
    technologies: string[]
  }[]
}

/** Legacy shapes kept for placeholders / older helpers if needed */
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
