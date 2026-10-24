#!/usr/bin/env bash
# Template: populate local CV data via the REST API (keeps personal data out of git).
#
# Usage:
#   1. Start Postgres + backend (see README).
#   2. Copy this file to a gitignored local script:
#        cp scripts/populate-cv.example.sh scripts/populate-cv.local.sh
#   3. Fill in the JSON placeholders below in the .local.sh copy.
#   4. Run:
#        chmod +x scripts/populate-cv.local.sh
#        ./scripts/populate-cv.local.sh
#
# By default the script DELETEs existing experiences / educations / skill categories /
# projects / introductions before creating yours, so it is safe to re-run.
# Profile is upserted with PUT (creates or replaces).
#
# Tags: seed (or create) tags first, then attach them with tagIds on bullets/skills.
#   TAG_JAVA=$(tag_id java)
#
# Requires: curl, python3

set -euo pipefail

API_BASE="${API_BASE:-http://localhost:8080}"
CLEAR_EXISTING="${CLEAR_EXISTING:-1}"

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

request() {
  local method="$1"
  local path="$2"
  local body="${3:-}"
  local url="${API_BASE}${path}"
  local tmp
  tmp="$(mktemp)"
  local http_code

  if [[ -n "$body" ]]; then
    http_code="$(curl -sS -o "$tmp" -w "%{http_code}" \
      -X "$method" "$url" \
      -H "Content-Type: application/json" \
      -d "$body")"
  else
    http_code="$(curl -sS -o "$tmp" -w "%{http_code}" \
      -X "$method" "$url")"
  fi

  if [[ "$http_code" -lt 200 || "$http_code" -ge 300 ]]; then
    echo "ERROR ${method} ${path} -> HTTP ${http_code}" >&2
    cat "$tmp" >&2
    echo >&2
    rm -f "$tmp"
    exit 1
  fi

  cat "$tmp"
  rm -f "$tmp"
  echo >&2 "OK ${method} ${path} (${http_code})"
}

delete_all() {
  local path="$1"
  python3 - "$API_BASE" "$path" <<'PY'
import json, sys, urllib.request

api_base, path = sys.argv[1], sys.argv[2]
url = api_base + path
with urllib.request.urlopen(url) as resp:
    items = json.load(resp)
for item in items:
    req = urllib.request.Request(
        f"{api_base}{path}/{item['id']}",
        method="DELETE",
    )
    with urllib.request.urlopen(req) as resp:
        resp.read()
    print(f"OK DELETE {path}/{item['id']}", file=sys.stderr)
PY
}

role_profile_id() {
  local slug="$1"
  request GET /api/role-profiles | python3 -c '
import json, sys
slug = sys.argv[1]
roles = json.load(sys.stdin)
match = next((r for r in roles if r["slug"] == slug), None)
if match is None:
    raise SystemExit(f"No role profile with slug={slug!r}. Seed tags/roles first.")
print(match["id"])
' "$slug"
}

tag_id() {
  local slug="$1"
  request GET /api/tags | python3 -c '
import json, sys
slug = sys.argv[1]
tags = json.load(sys.stdin)
match = next((t for t in tags if t["slug"] == slug), None)
if match is None:
    raise SystemExit(f"No tag with slug={slug!r}. Seed or create tags first.")
print(match["id"])
' "$slug"
}

# ---------------------------------------------------------------------------
# Optional clear (so re-runs replace content instead of duplicating)
# ---------------------------------------------------------------------------

if [[ "$CLEAR_EXISTING" == "1" ]]; then
  echo "Clearing existing list resources…"
  delete_all /api/introductions || true
  delete_all /api/experiences || true
  delete_all /api/educations || true
  delete_all /api/skill-categories || true
  delete_all /api/projects || true
fi

# Resolve tag IDs once (requires seeded tags)
TAG_JAVA="$(tag_id java)"
TAG_SPRING="$(tag_id spring)"
TAG_BACKEND="$(tag_id backend)"
TAG_FRONTEND="$(tag_id frontend)"
TAG_API="$(tag_id api)"
TAG_DATABASE="$(tag_id database)"
TAG_DOCKER="$(tag_id docker)"
TAG_PYTHON="$(tag_id python)"
TAG_ML="$(tag_id ml)"

# ---------------------------------------------------------------------------
# 1) Profile + contact links  (PUT upserts)
# ---------------------------------------------------------------------------

request PUT /api/profile "$(cat <<'JSON'
{
  "fullName": "Your Full Name",
  "headline": "Your headline — e.g. Full-stack developer",
  "email": "you@example.com",
  "phone": "+372 0000 0000",
  "location": "City, Country",
  "contactLinks": [
    { "label": "GitHub", "url": "https://github.com/your-username" },
    { "label": "LinkedIn", "url": "https://www.linkedin.com/in/your-username" },
    { "label": "Website", "url": "https://your-site.example" }
  ]
}
JSON
)" >/dev/null

# ---------------------------------------------------------------------------
# 2) Introductions (one per role profile slug)
# ---------------------------------------------------------------------------

upsert_introduction() {
  local slug="$1"
  local content="$2"
  local role_id
  role_id="$(role_profile_id "$slug")"
  request POST /api/introductions "$(cat <<JSON
{
  "roleProfileId": ${role_id},
  "content": $(python3 -c 'import json,sys; print(json.dumps(sys.argv[1]))' "$content")
}
JSON
)" >/dev/null
}

upsert_introduction "general" "Write your general introduction here."
upsert_introduction "java" "Write your Java-focused introduction here."
upsert_introduction "backend" "Write your backend-focused introduction here."
upsert_introduction "fullstack" "Write your full-stack introduction here."
upsert_introduction "ml-ai" "Write your ML / AI introduction here."

# ---------------------------------------------------------------------------
# 3) Experience  (endDate: null = current role; tagIds on bullets)
# ---------------------------------------------------------------------------

request POST /api/experiences "$(cat <<JSON
{
  "company": "Example Company",
  "title": "Software Engineer",
  "location": "Tallinn, Estonia",
  "startDate": "2023-01-01",
  "endDate": null,
  "bullets": [
    {
      "content": "First achievement or responsibility.",
      "tagIds": [${TAG_JAVA}, ${TAG_SPRING}, ${TAG_BACKEND}]
    },
    {
      "content": "Second achievement or responsibility.",
      "tagIds": [${TAG_API}, ${TAG_DATABASE}]
    }
  ]
}
JSON
)" >/dev/null

request POST /api/experiences "$(cat <<JSON
{
  "company": "Earlier Company",
  "title": "Junior Developer",
  "location": "Tallinn, Estonia",
  "startDate": "2021-06-01",
  "endDate": "2022-12-31",
  "bullets": [
    {
      "content": "Earlier-role bullet.",
      "tagIds": [${TAG_PYTHON}, ${TAG_BACKEND}]
    }
  ]
}
JSON
)" >/dev/null

# ---------------------------------------------------------------------------
# 4) Education
# ---------------------------------------------------------------------------

request POST /api/educations "$(cat <<JSON
{
  "institution": "University Name",
  "degree": "BSc",
  "field": "Computer Science",
  "startDate": "2019-09-01",
  "endDate": "2023-06-30",
  "bullets": [
    {
      "content": "Coursework or focus area.",
      "tagIds": [${TAG_DATABASE}]
    },
    {
      "content": "Thesis or notable project.",
      "tagIds": [${TAG_ML}]
    }
  ]
}
JSON
)" >/dev/null

# ---------------------------------------------------------------------------
# 5) Skill categories + skills (tagIds on skills)
# ---------------------------------------------------------------------------

request POST /api/skill-categories "$(cat <<JSON
{
  "name": "Backend",
  "skills": [
    { "name": "Java", "tagIds": [${TAG_JAVA}] },
    { "name": "Spring Boot", "tagIds": [${TAG_SPRING}, ${TAG_JAVA}] },
    { "name": "PostgreSQL", "tagIds": [${TAG_DATABASE}] }
  ]
}
JSON
)" >/dev/null

request POST /api/skill-categories "$(cat <<JSON
{
  "name": "Frontend",
  "skills": [
    { "name": "React", "tagIds": [${TAG_FRONTEND}] },
    { "name": "TypeScript", "tagIds": [${TAG_FRONTEND}] }
  ]
}
JSON
)" >/dev/null

request POST /api/skill-categories "$(cat <<JSON
{
  "name": "Tools",
  "skills": [
    { "name": "Docker", "tagIds": [${TAG_DOCKER}] },
    { "name": "Git" }
  ]
}
JSON
)" >/dev/null

# ---------------------------------------------------------------------------
# 6) Projects (tagIds on bullets; technologies stay display-only)
# ---------------------------------------------------------------------------

request POST /api/projects "$(cat <<JSON
{
  "title": "Example Project",
  "description": "Short description of what the project does.",
  "githubUrl": "https://github.com/your-username/example",
  "liveUrl": "https://example.com",
  "bullets": [
    {
      "content": "Notable implementation detail.",
      "tagIds": [${TAG_JAVA}, ${TAG_SPRING}, ${TAG_API}]
    },
    {
      "content": "Another highlight.",
      "tagIds": [${TAG_FRONTEND}, ${TAG_DATABASE}]
    }
  ],
  "technologies": [
    { "name": "Java" },
    { "name": "Spring Boot" },
    { "name": "React" }
  ]
}
JSON
)" >/dev/null

request POST /api/projects "$(cat <<JSON
{
  "title": "Second Project",
  "description": "Another short description.",
  "githubUrl": "https://github.com/your-username/second",
  "liveUrl": null,
  "bullets": [
    {
      "content": "One highlight.",
      "tagIds": [${TAG_PYTHON}, ${TAG_ML}]
    }
  ],
  "technologies": [
    { "name": "TypeScript" },
    { "name": "PostgreSQL" }
  ]
}
JSON
)" >/dev/null

echo
echo "Done. Open the frontend (or GET ${API_BASE}/api/profile) to verify."
echo "Tip: keep filled data only in scripts/populate-cv.local.sh (gitignored)."
