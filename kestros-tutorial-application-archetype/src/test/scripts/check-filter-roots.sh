#!/usr/bin/env bash
#
# Checks the project the archetype just generated: every content filter root points at a
# node that exists, every UI framework node is named by a root, and no trace of the
# removed Services step survives.
#
# Filter drift - a <filter root> naming a node that is not there, or a node that no root
# names - is the defect that kept this module dormant, so it is checked rather than
# assumed. Reads what maven-archetype-plugin generates during the integration-test phase,
# and is run from the module pom by maven-antrun-plugin on the verify phase.
#
# Usage: check-filter-roots.sh <module-basedir>

set -uo pipefail

BASEDIR="${1:-}"
if [ -z "$BASEDIR" ]; then
  echo "check-filter-roots: usage: check-filter-roots.sh <module-basedir>" >&2
  exit 2
fi

# Step 10 was removed from the tutorial, so slot 10 is empty and eleven frameworks became
# ten. Step 11 is the tutorial finale and is named explicitly so that deleting the wrong
# line is caught rather than merely changing the count.
EXPECTED_FRAMEWORK_ROOTS=10
REQUIRED_FRAMEWORK=tutorial-step-11-framework
FORBIDDEN=(
  "tutorial-step-10-framework"
  "step-10-framework"
  "Step 10"
  "Step 10: Services"
)

failures=0
fail() {
  echo "check-filter-roots: FAIL - $*" >&2
  failures=$((failures + 1))
}

PROJECTS_DIR="$BASEDIR/target/test-classes/projects"
# The archetype IT generates into target/test-classes/projects/<it>/project/<artifactId>/.
mapfile -t projects < <(find "$PROJECTS_DIR" -mindepth 3 -maxdepth 3 -type d -path '*/project/*' 2>/dev/null | sort)

if [ "${#projects[@]}" -eq 0 ]; then
  echo "check-filter-roots: FAIL - no generated project under $PROJECTS_DIR" >&2
  echo "check-filter-roots: the archetype integration test did not run, so nothing was checked" >&2
  exit 1
fi

for project in "${projects[@]}"; do
  echo "check-filter-roots: checking $project"

  content="$project/src/content"
  filter="$content/META-INF/vault/filter.xml"
  jcr_root="$content/jcr_root"

  if [ ! -f "$filter" ]; then
    fail "no filter.xml at $filter"
    continue
  fi
  if [ ! -d "$jcr_root" ]; then
    fail "no jcr_root at $jcr_root"
    continue
  fi

  mapfile -t roots < <(grep -o 'root="[^"]*"' "$filter" | sed -e 's/^root="//' -e 's/"$//')
  if [ "${#roots[@]}" -eq 0 ]; then
    fail "$filter declares no filter roots"
    continue
  fi

  # Forward drift: a root naming a node that does not exist. Criterion 4 covers every
  # root, not only the /etc/ ones, so the /apps/ root and the three site roots are
  # checked too - they resolve today and nothing else would notice if they stopped.
  for root in "${roots[@]}"; do
    if [ ! -d "$jcr_root$root" ]; then
      fail "filter root $root has no directory at $jcr_root$root"
    fi
  done

  # Reverse drift: a UI framework node that no root names. This is what catches deleting
  # a framework's filter line without its directory, or the other way round.
  if [ -d "$jcr_root/etc/ui-frameworks" ]; then
    for dir in "$jcr_root/etc/ui-frameworks"/*/; do
      [ -d "$dir" ] || continue
      name="$(basename "$dir")"
      covered=false
      for root in "${roots[@]}"; do
        if [ "$root" = "/etc/ui-frameworks/$name" ]; then
          covered=true
          break
        fi
      done
      if [ "$covered" = false ]; then
        fail "ui-framework $name has no filter root naming it"
      fi
    done
  fi

  # The framework roots themselves: how many, and is the finale still listed.
  framework_roots=()
  for root in "${roots[@]}"; do
    case "$root" in
      /etc/ui-frameworks/*) framework_roots+=("$root") ;;
    esac
  done
  if [ "${#framework_roots[@]}" -ne "$EXPECTED_FRAMEWORK_ROOTS" ]; then
    fail "expected $EXPECTED_FRAMEWORK_ROOTS /etc/ui-frameworks/ roots, found ${#framework_roots[@]}"
  fi
  found_required=false
  for root in "${framework_roots[@]}"; do
    if [ "$root" = "/etc/ui-frameworks/$REQUIRED_FRAMEWORK" ]; then
      found_required=true
      break
    fi
  done
  if [ "$found_required" = false ]; then
    fail "$REQUIRED_FRAMEWORK is not listed in $filter - the tutorial finale was removed"
  fi

  # Nothing anywhere in the generated tree may still name the removed step, in a file's
  # contents or in its path.
  for needle in "${FORBIDDEN[@]}"; do
    while IFS= read -r hit; do
      [ -n "$hit" ] && fail "\"$needle\" still appears in $hit"
    done < <(grep -R -F -l -- "$needle" "$project" 2>/dev/null)
    while IFS= read -r hit; do
      [ -n "$hit" ] && fail "\"$needle\" still appears in the path $hit"
    done < <(find "$project" -path "*$needle*" 2>/dev/null)
  done
done

if [ "$failures" -ne 0 ]; then
  echo "check-filter-roots: $failures failure(s)" >&2
  exit 1
fi

echo "check-filter-roots: OK - ${#projects[@]} generated project(s), all filter roots resolve both ways"
