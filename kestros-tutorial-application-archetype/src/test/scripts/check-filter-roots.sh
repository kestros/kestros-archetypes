#!/usr/bin/env bash
#
# Checks the project the archetype just generated: every content filter root points at a
# node that exists, every UI framework node is named by a root, no trace of the removed
# Services step survives, and the datasource step ships as class, node and filter.
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

# Slot 10 held the Services step, which #165 removed. #164 refilled the same slot with the
# datasources step; #166 then inserted the themes step at 6, which pushed the datasources
# step and everything else above 5 up by one. What must not come back is Services, and the
# two strings below are the Services step's own wording, so they stay as they were written
# however the ladder renumbers around them.
EXPECTED_FRAMEWORK_ROOTS=12
REQUIRED_FRAMEWORK=tutorial-step-12-framework
FORBIDDEN=(
  "Step 10: Services"
  "tutorial-step-10-services"
)

# Step 11 is the datasources step. Named explicitly so that deleting the wrong framework
# line is caught rather than merely changing the count.
DATASOURCE_STEP_FRAMEWORK=tutorial-step-11-framework
DATASOURCE_STEP_TITLE="Step 11: Datasources"
DATASOURCE_STEP_CODE=step-11-framework

# The datasource ships as three things that must agree, and each is checked against the
# generated project rather than against the archetype source: the class at its packaged
# path, the registration node naming that class, and a filter root covering the node.
DATASOURCE_CLASS=TutorialCardDataSource
DATASOURCE_FILTER_ROOT=/apps/kestros/commons/components

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

  # The datasources step: the framework node, its title, and the component view named by
  # the framework CODE (step-11-framework), not by the node name.
  found_datasource_step=false
  for root in "${framework_roots[@]}"; do
    if [ "$root" = "/etc/ui-frameworks/$DATASOURCE_STEP_FRAMEWORK" ]; then
      found_datasource_step=true
      break
    fi
  done
  if [ "$found_datasource_step" = false ]; then
    fail "$DATASOURCE_STEP_FRAMEWORK is not listed in $filter - the datasources step is missing"
  fi
  datasource_step_node="$jcr_root/etc/ui-frameworks/$DATASOURCE_STEP_FRAMEWORK/.content.xml"
  if [ ! -f "$datasource_step_node" ]; then
    fail "no framework node at $datasource_step_node"
  elif ! grep -q -F -- "jcr:title=\"$DATASOURCE_STEP_TITLE\"" "$datasource_step_node"; then
    fail "$datasource_step_node is not titled \"$DATASOURCE_STEP_TITLE\""
  elif ! grep -q -F -- "kes:uiFrameworkCode=\"$DATASOURCE_STEP_CODE\"" "$datasource_step_node"; then
    fail "$datasource_step_node does not keep kes:uiFrameworkCode=\"$DATASOURCE_STEP_CODE\""
  fi

  # The view directory is named by the framework code. A view under the node name instead
  # would never resolve, and nothing at runtime would say why.
  datasource_step_view=""
  while IFS= read -r dir; do
    datasource_step_view="$dir"
  done < <(find "$jcr_root/apps" -type d -name "$DATASOURCE_STEP_CODE" -path '*/tutorial-getting-started/*' 2>/dev/null)
  if [ -z "$datasource_step_view" ]; then
    fail "no $DATASOURCE_STEP_CODE view directory under tutorial-getting-started"
  elif [ ! -f "$datasource_step_view/layouts/default/content.html" ]; then
    fail "$datasource_step_view has no layouts/default/content.html"
  fi

  # The datasource, as three things that have to agree. The class is found by name so the
  # generated package does not have to be hard-coded here, but its path is then checked -
  # a class emitted with packaged="false" lands at the source root and still compiles
  # nowhere, because its package statement and its directory disagree.
  class_file=""
  while IFS= read -r hit; do
    class_file="$hit"
  done < <(find "$project/src/main/java" -type f -name "$DATASOURCE_CLASS.java" 2>/dev/null)
  if [ -z "$class_file" ]; then
    fail "no $DATASOURCE_CLASS.java under $project/src/main/java"
  else
    case "$class_file" in
      "$project/src/main/java/$DATASOURCE_CLASS.java")
        fail "$DATASOURCE_CLASS.java is at the source root - the fileSet is not packaged=\"true\""
        ;;
      */application/datasources/"$DATASOURCE_CLASS.java") ;;
      *)
        fail "$class_file is not under application/datasources"
        ;;
    esac
    package_dir="$(dirname "$class_file")"
    package_name="${package_dir#"$project/src/main/java/"}"
    package_name="${package_name//\//.}"
    if ! grep -q -F -- "package $package_name;" "$class_file"; then
      fail "$class_file does not declare \"package $package_name;\" - path and package disagree"
    fi
    if ! find "$project/src/test/java" -type f -name "${DATASOURCE_CLASS}Test.java" 2>/dev/null | grep -q .; then
      fail "no ${DATASOURCE_CLASS}Test.java under $project/src/test/java"
    fi

    # The registration node has to name the class by its fully qualified name, or the
    # datasource is never selectable on a Card however correct the class is.
    registration=""
    while IFS= read -r hit; do
      registration="$hit"
    done < <(find "$jcr_root/apps/kestros" -type f -name .content.xml \
      -path '*/content/card/datasources/*' 2>/dev/null)
    if [ -z "$registration" ]; then
      fail "no datasource registration node under $jcr_root/apps/kestros/commons/components/content/card/datasources"
    else
      if ! grep -q -F -- "classPath=\"$package_name.$DATASOURCE_CLASS\"" "$registration"; then
        fail "$registration does not carry classPath=\"$package_name.$DATASOURCE_CLASS\""
      fi
      if ! grep -q -F -- 'container="{Boolean}true"' "$registration"; then
        fail "$registration does not carry container=\"{Boolean}true\""
      fi
    fi
  fi

  # A filter root covering the registration, and a sling:Folder docview on the intermediate
  # datasources node. Without the docview FileVault skips the folder silently on a fresh
  # instance and the leaf below it never installs.
  found_ds_root=false
  for root in "${roots[@]}"; do
    if [ "$root" = "$DATASOURCE_FILTER_ROOT" ]; then
      found_ds_root=true
      break
    fi
  done
  if [ "$found_ds_root" = false ]; then
    fail "$filter has no $DATASOURCE_FILTER_ROOT root - the datasource registration is not packaged"
  fi
  intermediate="$jcr_root/apps/kestros/commons/components/content/card/datasources/.content.xml"
  if [ ! -f "$intermediate" ]; then
    fail "no sling:Folder docview at $intermediate"
  elif ! grep -q -F -- 'jcr:primaryType="sling:Folder"' "$intermediate"; then
    fail "$intermediate is not a sling:Folder"
  fi

  # The Card the learner sees at step 11 has to exist on the beginner site and be wired to
  # the registration by name. Beginner site only - the other two sites are #163's.
  beginner=""
  while IFS= read -r hit; do
    beginner="$hit"
  done < <(find "$jcr_root/content/sites" -maxdepth 2 -type f -name .content.xml \
    -path '*-beginner-tutorial/*' 2>/dev/null)
  if [ -z "$beginner" ]; then
    fail "no beginner tutorial site under $jcr_root/content/sites"
  elif ! grep -q -F -- 'kes:datasource=' "$beginner"; then
    fail "$beginner has no component wired to a datasource - step 11 has nothing to show"
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
