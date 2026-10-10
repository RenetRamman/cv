import { apiGet } from './client'
import { mapCvData, type CvApiBundle } from './mapCv'
import type { CvData } from '../types/cv'
import type {
  EducationResponse,
  ExperienceResponse,
  IntroductionResponse,
  ProfileResponse,
  ProjectResponse,
  RoleProfileResponse,
  SkillCategoryResponse,
} from './types'

export async function loadCv(): Promise<CvData> {
  const [
    profile,
    roleProfiles,
    introductions,
    experiences,
    educations,
    skillCategories,
    projects,
  ] = await Promise.all([
    apiGet<ProfileResponse>('/api/profile'),
    apiGet<RoleProfileResponse[]>('/api/role-profiles'),
    apiGet<IntroductionResponse[]>('/api/introductions'),
    apiGet<ExperienceResponse[]>('/api/experiences'),
    apiGet<EducationResponse[]>('/api/educations'),
    apiGet<SkillCategoryResponse[]>('/api/skill-categories'),
    apiGet<ProjectResponse[]>('/api/projects'),
  ])

  const bundle: CvApiBundle = {
    profile,
    roleProfiles,
    introductions,
    experiences,
    educations,
    skillCategories,
    projects,
  }

  return mapCvData(bundle)
}
