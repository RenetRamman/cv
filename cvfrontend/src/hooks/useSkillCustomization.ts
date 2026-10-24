import { useEffect, useState } from 'react'
import type { SkillCategory } from '../types/cv'

function cloneSkills(skills: SkillCategory[]): SkillCategory[] {
  return skills.map((group) => ({
    name: group.name,
    skills: group.skills.map((skill) => ({ ...skill })),
  }))
}

/**
 * Local-only skill highlight + within-category order for on-the-fly CV
 * customization. Resets when role (or skills payload) changes. Never persists.
 */
export function useSkillCustomization(
  skills: SkillCategory[],
  selectedRoleId: string,
) {
  const [groups, setGroups] = useState(() => cloneSkills(skills))

  useEffect(() => {
    setGroups(cloneSkills(skills))
  }, [selectedRoleId, skills])

  const toggleSkill = (skillId: number) => {
    setGroups((prev) =>
      prev.map((group) => ({
        ...group,
        skills: group.skills.map((skill) =>
          skill.id === skillId
            ? { ...skill, highlighted: !skill.highlighted }
            : skill,
        ),
      })),
    )
  }

  const reorderInCategory = (
    categoryName: string,
    activeId: number,
    overId: number,
  ) => {
    if (activeId === overId) {
      return
    }

    setGroups((prev) =>
      prev.map((group) => {
        if (group.name !== categoryName) {
          return group
        }

        const oldIndex = group.skills.findIndex((s) => s.id === activeId)
        const newIndex = group.skills.findIndex((s) => s.id === overId)
        if (oldIndex < 0 || newIndex < 0) {
          return group
        }

        const nextSkills = [...group.skills]
        const [moved] = nextSkills.splice(oldIndex, 1)
        nextSkills.splice(newIndex, 0, moved)
        return { ...group, skills: nextSkills }
      }),
    )
  }

  return { groups, toggleSkill, reorderInCategory }
}
