import {
  DndContext,
  KeyboardSensor,
  PointerSensor,
  closestCenter,
  type DragEndEvent,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import {
  SortableContext,
  horizontalListSortingStrategy,
  sortableKeyboardCoordinates,
} from '@dnd-kit/sortable'
import type { SkillCategory } from '../types/cv'
import { SortableSkillChip } from './SortableSkillChip'

interface SkillCustomizerProps {
  groups: SkillCategory[]
  selectedLabel: string
  onToggle: (skillId: number) => void
  onReorder: (categoryName: string, activeId: number, overId: number) => void
}

export function SkillCustomizer({
  groups,
  selectedLabel,
  onToggle,
  onReorder,
}: SkillCustomizerProps) {
  const sensors = useSensors(
    useSensor(PointerSensor, {
      // Prefer click-to-toggle; start drag after a small move.
      activationConstraint: { distance: 8 },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  )

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event
    if (!over || active.id === over.id) {
      return
    }

    const activeId = Number(active.id)
    const overId = Number(over.id)
    const category = groups.find((group) =>
      group.skills.some((skill) => skill.id === activeId),
    )
    if (!category) {
      return
    }

    // Only reorder within the same category.
    if (!category.skills.some((skill) => skill.id === overId)) {
      return
    }

    onReorder(category.name, activeId, overId)
  }

  return (
    <section className="cv-shell cv-section" aria-labelledby="skills-heading">
      <div className="cv-section__header">
        <h2 id="skills-heading" className="cv-label">
          Skills
        </h2>
        <span className="cv-hint cv-hint--accent">
          Click to highlight · drag to reorder within a group ({selectedLabel})
        </span>
      </div>
      <DndContext
        sensors={sensors}
        collisionDetection={closestCenter}
        onDragEnd={handleDragEnd}
      >
        <div className="cv-skills">
          {groups.map((group) => (
            <div key={group.name} className="cv-skill-group">
              <h3 className="cv-skill-group__name">{group.name}</h3>
              <SortableContext
                items={group.skills.map((skill) => skill.id)}
                strategy={horizontalListSortingStrategy}
              >
                <ul className="cv-skill-chips">
                  {group.skills.map((skill) => (
                    <SortableSkillChip
                      key={skill.id}
                      id={skill.id}
                      name={skill.name}
                      highlighted={Boolean(skill.highlighted)}
                      onToggle={() => onToggle(skill.id)}
                    />
                  ))}
                </ul>
              </SortableContext>
            </div>
          ))}
        </div>
      </DndContext>
    </section>
  )
}
