import type { CvData, RoleId } from '../types/cv'
import type { CvApiResponse } from './types'

const SITE_TITLE = 'Dynamic CV'

export function mapCvResponse(api: CvApiResponse): CvData {
  const roles = [...api.roles]
    .sort((a, b) => a.sortOrder - b.sortOrder)
    .map((role) => ({
      id: role.slug,
      label: role.name,
    }))

  const introductions: Record<RoleId, string> = {
    [api.selectedRoleSlug]: api.introduction,
  }

  return {
    siteTitle: SITE_TITLE,
    brandMark: brandMarkFromName(api.fullName),
    fullName: api.fullName,
    headline: api.headline ?? '',
    location: api.location ?? '',
    email: api.email ?? '',
    contactLinks: api.contactLinks.map((link) => ({
      label: link.label,
      url: link.url,
    })),
    roles,
    defaultRoleId: pickDefaultRoleId(
      roles.map((role) => role.id),
      api.selectedRoleSlug,
    ),
    selectedRoleId: api.selectedRoleSlug,
    selectedRoleName: api.selectedRoleName,
    introduction: api.introduction,
    introductions,
    experience: api.experiences.map((experience) => ({
      id: experience.id,
      title: experience.title,
      company: experience.company,
      location: experience.location ?? '',
      dates: formatDateRange(experience.startDate, experience.endDate),
      bullets: experience.bullets.map((bullet) => bullet.content),
    })),
    skills: api.skillCategories.map((category) => ({
      name: category.name,
      skills: category.skills.map((skill) => ({
        name: skill.name,
        highlighted: skill.highlighted,
      })),
    })),
    projects: api.projects.map((project) => ({
      id: project.id,
      title: project.title,
      description: project.description ?? '',
      githubUrl: project.githubUrl ?? undefined,
      liveUrl: project.liveUrl ?? undefined,
      bullets: project.bullets.map((bullet) => bullet.content),
      technologies: project.technologies,
    })),
    education: api.educations.map((education) => {
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
    }),
  }
}

function pickDefaultRoleId(roleIds: RoleId[], selectedSlug: string): RoleId {
  if (roleIds.includes(selectedSlug)) {
    return selectedSlug
  }
  if (roleIds.includes('fullstack')) {
    return 'fullstack'
  }
  if (roleIds.length > 0) {
    return roleIds[0]
  }
  return selectedSlug || 'general'
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
