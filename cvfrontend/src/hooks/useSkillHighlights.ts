import { useEffect, useState } from 'react'
import type { SkillCategory } from '../types/cv'

function highlightsFromSkills(
  skills: SkillCategory[],
): Record<number, boolean> {
  const next: Record<number, boolean> = {}
  for (const group of skills) {
    for (const skill of group.skills) {
      next[skill.id] = Boolean(skill.highlighted)
    }
  }
  return next
}

/**
 * Local-only skill highlight overrides for on-the-fly CV customization.
 * Resets when role (or skills payload) changes. Never persists to the API.
 */
export function useSkillHighlights(
  skills: SkillCategory[],
  selectedRoleId: string,
): {
  isHighlighted: (skillId: number) => boolean
  toggleSkill: (skillId: number) => void
} {
  const [highlights, setHighlights] = useState(() =>
    highlightsFromSkills(skills),
  )

  useEffect(() => {
    setHighlights(highlightsFromSkills(skills))
  }, [selectedRoleId, skills])

  return {
    isHighlighted: (skillId: number) => Boolean(highlights[skillId]),
    toggleSkill: (skillId: number) => {
      setHighlights((prev) => ({
        ...prev,
        [skillId]: !prev[skillId],
      }))
    },
  }
}
