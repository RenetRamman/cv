import type { CvData, RoleId } from '../types/cv'
import type {
  EducationResponse,
  ExperienceResponse,
  IntroductionResponse,
  ProfileResponse,
  ProjectResponse,
  RoleProfileResponse,
  SkillCategoryResponse,
} from './types'

const SITE_TITLE = 'Dynamic CV'

export interface CvApiBundle {
  profile: ProfileResponse
  roleProfiles: RoleProfileResponse[]
  introductions: IntroductionResponse[]
  experiences: ExperienceResponse[]
  educations: EducationResponse[]
  skillCategories: SkillCategoryResponse[]
  projects: ProjectResponse[]
}

export function mapCvData(bundle: CvApiBundle): CvData {
  const permanentRoles = bundle.roleProfiles
    .filter((role) => role.type === 'PERMANENT')
    .sort((a, b) => a.sortOrder - b.sortOrder || a.id - b.id)

  const roles = permanentRoles.map((role) => ({
    id: role.slug,
    label: role.name,
  }))

  const introductions: Record<RoleId, string> = {}
  for (const intro of bundle.introductions) {
    introductions[intro.roleProfileSlug] = intro.content
  }

  const defaultRoleId = pickDefaultRoleId(roles.map((role) => role.id))

  return {
    siteTitle: SITE_TITLE,
    brandMark: brandMarkFromName(bundle.profile.fullName),
    fullName: bundle.profile.fullName,
    headline: bundle.profile.headline,
    location: bundle.profile.location ?? '',
    email: bundle.profile.email,
    contactLinks: bundle.profile.contactLinks.map((link) => ({
      label: link.label,
      url: link.url,
    })),
    roles,
    defaultRoleId,
    introductions,
    experience: bundle.experiences.map(mapExperience),
    skills: bundle.skillCategories.map((category) => ({
      name: category.name,
      skills: category.skills.map((skill) => ({
        name: skill.name,
        highlighted: false,
      })),
    })),
    projects: bundle.projects.map(mapProject),
    education: bundle.educations.map(mapEducation),
  }
}

function pickDefaultRoleId(roleIds: RoleId[]): RoleId {
  if (roleIds.includes('fullstack')) {
    return 'fullstack'
  }
  if (roleIds.length > 0) {
    return roleIds[0]
  }
  return 'general'
}

function brandMarkFromName(fullName: string): string {
  const parts = fullName.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) {
    return 'CV'
  }
  if (parts.length === 1) {
    return parts[0].slice(0, 2).toUpperCase()
  }
  return `${parts[0][0] ?? ''}${parts[parts.length - 1][0] ?? ''}`.toUpperCase()
}

function mapExperience(experience: ExperienceResponse) {
  return {
    id: experience.id,
    title: experience.title,
    company: experience.company,
    location: experience.location ?? '',
    dates: formatDateRange(experience.startDate, experience.endDate),
    bullets: experience.bullets.map((bullet) => bullet.content),
  }
}

function mapEducation(education: EducationResponse) {
  const degreeLabel = [education.degree, education.field]
    .map((part) => part?.trim())
    .filter(Boolean)
    .join(' ')

  return {
    id: education.id,
    degree: degreeLabel,
    institution: education.institution,
    dates: formatDateRange(education.startDate, education.endDate),
    bullets: education.bullets.map((bullet) => bullet.content),
  }
}

function mapProject(project: ProjectResponse) {
  return {
    id: project.id,
    title: project.title,
    description: project.description ?? '',
    githubUrl: project.githubUrl ?? undefined,
    liveUrl: project.liveUrl ?? undefined,
    bullets: project.bullets.map((bullet) => bullet.content),
    technologies: project.technologies.map((tech) => tech.name),
  }
}

function formatDateRange(
  startDate: string | null,
  endDate: string | null,
): string {
  const start = startDate ? formatYear(startDate) : null
  const end = endDate ? formatYear(endDate) : null

  if (start && end) {
    return `${start} — ${end}`
  }
  if (start && !end) {
    return `${start} — Present`
  }
  if (!start && end) {
    return end
  }
  return ''
}

function formatYear(isoDate: string): string {
  const year = isoDate.slice(0, 4)
  return /^\d{4}$/.test(year) ? year : isoDate
}
