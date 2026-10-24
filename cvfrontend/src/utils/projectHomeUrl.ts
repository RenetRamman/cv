/** Public GitHub repo for this CV project. */
export const PROJECT_GITHUB_URL = 'https://github.com/RenetRamman/cv'

/**
 * Prefer the live site when the app is deployed; if the current origin
 * contains "localhost" (e.g. http://localhost:5173/), use the GitHub repo
 * instead so public links never advertise a local URL.
 */
export function projectHomeUrl(): string {
  if (typeof window === 'undefined') {
    return PROJECT_GITHUB_URL
  }

  const origin = window.location.origin
  if (origin.includes('localhost')) {
    return PROJECT_GITHUB_URL
  }

  return origin
}

/** Rewrite stored URLs that point at a local dev server. */
export function publicUrl(url: string): string {
  if (url.includes('localhost')) {
    return projectHomeUrl()
  }
  return url
}
