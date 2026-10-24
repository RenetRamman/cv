import { useCallback, useEffect, useState } from 'react'
import { loadCv } from '../api/loadCv'
import type { CvData, RoleId } from '../types/cv'

type CvLoadState =
  | { status: 'loading' }
  | { status: 'ready'; data: CvData }
  | { status: 'error'; message: string }

export function useCvData(): CvLoadState & {
  selectRole: (roleId: RoleId) => void
} {
  const [state, setState] = useState<CvLoadState>({ status: 'loading' })
  const [roleSlug, setRoleSlug] = useState<string | undefined>(undefined)

  useEffect(() => {
    let cancelled = false
    setState({ status: 'loading' })

    loadCv(roleSlug)
      .then((data) => {
        if (!cancelled) {
          setState({ status: 'ready', data })
        }
      })
      .catch((error: unknown) => {
        if (cancelled) {
          return
        }
        const message =
          error instanceof Error ? error.message : 'Failed to load CV data'
        setState({ status: 'error', message })
      })

    return () => {
      cancelled = true
    }
  }, [roleSlug])

  const selectRole = useCallback((roleId: RoleId) => {
    setRoleSlug(roleId)
  }, [])

  return { ...state, selectRole }
}
