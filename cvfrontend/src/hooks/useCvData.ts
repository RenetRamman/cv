import { useEffect, useState } from 'react'
import { loadCv } from '../api/loadCv'
import type { CvData } from '../types/cv'

type CvLoadState =
  | { status: 'loading' }
  | { status: 'ready'; data: CvData }
  | { status: 'error'; message: string }

export function useCvData(): CvLoadState {
  const [state, setState] = useState<CvLoadState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false

    loadCv()
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
  }, [])

  return state
}
