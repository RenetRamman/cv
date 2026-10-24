import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'

interface SortableSkillChipProps {
  id: number
  name: string
  highlighted: boolean
  onToggle: () => void
}

export function SortableSkillChip({
  id,
  name,
  highlighted,
  onToggle,
}: SortableSkillChipProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id })

  return (
    <li
      ref={setNodeRef}
      style={{
        transform: CSS.Transform.toString(transform),
        transition,
        zIndex: isDragging ? 1 : undefined,
        opacity: isDragging ? 0.85 : undefined,
      }}
    >
      <button
        type="button"
        className={
          highlighted
            ? 'cv-skill-chip is-highlighted'
            : 'cv-skill-chip'
        }
        aria-pressed={highlighted}
        onClick={onToggle}
        {...attributes}
        {...listeners}
      >
        {name}
      </button>
    </li>
  )
}
