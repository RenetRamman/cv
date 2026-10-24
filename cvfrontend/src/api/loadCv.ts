import { apiGet } from './client'
import { mapCvResponse } from './mapCv'
import type { CvData } from '../types/cv'
import type { CvApiResponse } from './types'

const PREFERRED_DEFAULT_ROLE = 'fullstack'

export async function loadCv(roleProfileSlug?: string): Promise<CvData> {
  const slug = roleProfileSlug?.trim() || PREFERRED_DEFAULT_ROLE
  const path = `/api/cv?roleProfileSlug=${encodeURIComponent(slug)}`

  try {
    const api = await apiGet<CvApiResponse>(path)
    return mapCvResponse(api)
  } catch (error) {
    if (slug !== 'general') {
      const fallback = await apiGet<CvApiResponse>('/api/cv')
      return mapCvResponse(fallback)
    }
    throw error
  }
}
