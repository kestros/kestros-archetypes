#!/usr/bin/env bash
#
# Checks the tutorial ladder in the project the archetype just generated: the themes step
# exists and carries two themes with different CSS, its view is a plain view rather than a
# layout set, and every step's title, directory and framework code agree on the same number
# with no gap or duplicate between 1 and 12.
#
# The lesson the themes step teaches is that the SAME framework with a DIFFERENT theme
# renders the same words with different CSS. That only holds if the two themes really do
# ship different stylesheets, so the CSS is compared by hash rather than assumed - a second
# theme whose less file was copied and never edited would teach the opposite of the point.
#
# Reads what maven-archetype-plugin generates during the integration-test phase, and is run
# from the module pom by maven-antrun-plugin on the verify phase, alongside
# check-filter-roots.sh.
#
# Usage: check-tutorial-content.sh <module-basedir>

set -uo pipefail

BASEDIR="${1:-}"
if [ -z "$BASEDIR" ]; then
  echo "check-tutorial-content: usage: check-tutorial-content.sh <module-basedir>" >&2
  exit 2
fi

# The themes step. Its framework code is its directory name without the tutorial- prefix,
# which is the convention steps 3 to 12 follow; steps 1 and 2 predate it and use bare
# "step-1"/"step-2", so the code check below is scoped to this framework alone.
THEMES_FRAMEWORK=tutorial-step-6-framework
THEMES_CODE=step-6-framework
THEMES_VIEW=step-6-framework

# The ladder runs 1 to 12 with no gaps once the themes step is inserted at 6.
EXPECTED_LAST_STEP=12

# "dark" and "light" are the names of the Color/Theme COMPONENT VARIATIONS taught two steps
# later. A UI framework theme sharing either word fuses the two concepts, which is the exact
# confusion this step exists to clear up.
FORBIDDEN_THEME_NAMES=(dark light)

# A view heading that names a step other than its own directory. #990: step 5's 3.0.0 view
# is headed "Step 6" where its sibling reads "Step 5a" - a defect that predates this check
# and is filed separately. Remove this entry when #990 lands; leaving it in only hides a
# heading that is by then correct.
KNOWN_HEADING_MISMATCHES=(
  "step-5-framework/versions/3.0.0/content.html"
)

failures=0
fail() {
  echo "check-tutorial-content: FAIL - $*" >&2
  failures=$((failures + 1))
}

# Reads one attribute out of a docview .content.xml. The files are hand-written and wrap
# attributes one per line, so a line-oriented read is enough and avoids needing an XML
# parser on the build agent.
attr() {
  local file="$1" name="$2"
  grep -o "$name=\"[^\"]*\"" "$file" 2>/dev/null | head -n 1 | sed -e "s/^$name=\"//" -e 's/"$//'
}

# The leading step number of a string: "Step 7a" and "Step 7: Variations" both give 7,
# "step-10-framework" gives 10. Empty when there is no step number to find.
step_number() {
  echo "$1" | grep -o -E '[Ss]tep[- ]([0-9]+)' | head -n 1 | grep -o -E '[0-9]+' | head -n 1
}

PROJECTS_DIR="$BASEDIR/target/test-classes/projects"
# The archetype IT generates into target/test-classes/projects/<it>/project/<artifactId>/.
mapfile -t projects < <(find "$PROJECTS_DIR" -mindepth 3 -maxdepth 3 -type d -path '*/project/*' 2>/dev/null | sort)

if [ "${#projects[@]}" -eq 0 ]; then
  echo "check-tutorial-content: FAIL - no generated project under $PROJECTS_DIR" >&2
  echo "check-tutorial-content: the archetype integration test did not run, so nothing was checked" >&2
  exit 1
fi

for project in "${projects[@]}"; do
  echo "check-tutorial-content: checking $project"

  jcr_root="$project/src/content/jcr_root"
  frameworks="$jcr_root/etc/ui-frameworks"

  if [ ! -d "$frameworks" ]; then
    fail "no ui-frameworks directory at $frameworks"
    continue
  fi

  # ---------------------------------------------------------------- criterion 1
  # The themes step exists, as a kes:UiFramework, with the code its directory name implies.
  themes_node="$frameworks/$THEMES_FRAMEWORK/.content.xml"
  if [ ! -f "$themes_node" ]; then
    fail "no themes step framework at $themes_node"
  else
    if [ "$(attr "$themes_node" 'jcr:primaryType')" != "kes:UiFramework" ]; then
      fail "$themes_node is not a kes:UiFramework"
    fi
    actual_code="$(attr "$themes_node" 'kes:uiFrameworkCode')"
    if [ "$actual_code" != "$THEMES_CODE" ]; then
      fail "$themes_node has kes:uiFrameworkCode=\"$actual_code\", expected \"$THEMES_CODE\" - code and directory name disagree"
    fi
  fi

  # ---------------------------------------------------------------- criterion 2
  # Two themes, differently titled, each with CSS, and the CSS actually differs.
  themes_dir="$frameworks/$THEMES_FRAMEWORK/themes"
  if [ ! -d "$themes_dir" ]; then
    fail "no themes directory at $themes_dir"
  else
    theme_count=0
    theme_titles=()
    for dir in "$themes_dir"/*/; do
      [ -d "$dir" ] || continue
      name="$(basename "$dir")"
      node="$dir/.content.xml"
      if [ ! -f "$node" ]; then
        fail "theme directory $name has no .content.xml"
        continue
      fi
      if [ "$(attr "$node" 'jcr:primaryType')" != "kes:Theme" ]; then
        fail "$node is not a kes:Theme"
        continue
      fi
      theme_count=$((theme_count + 1))
      theme_titles+=("$(attr "$node" 'jcr:title')")

      for forbidden in "${FORBIDDEN_THEME_NAMES[@]}"; do
        if [ "$name" = "$forbidden" ]; then
          fail "theme $name is named after a Color/Theme component variation - a framework theme and a component variation are different things and must not share a name"
        fi
      done

      # The css node names its stylesheets in include="[a.less,b.less]". Each has to be
      # there and have something in it: an empty stylesheet renders identically to no
      # stylesheet, so the step would demonstrate nothing.
      css_node="$dir/css/.content.xml"
      if [ ! -f "$css_node" ]; then
        fail "theme $name has no css/.content.xml"
        continue
      fi
      include="$(attr "$css_node" 'include')"
      include="${include#[}"
      include="${include%]}"
      if [ -z "$include" ]; then
        fail "theme $name has an empty include on $css_node"
        continue
      fi
      IFS=',' read -r -a includes <<< "$include"
      for entry in "${includes[@]}"; do
        entry="$(echo "$entry" | tr -d '[:space:]')"
        [ -n "$entry" ] || continue
        if [ ! -f "$dir/css/$entry" ]; then
          fail "theme $name includes $entry but $dir/css/$entry does not exist"
        elif [ ! -s "$dir/css/$entry" ]; then
          fail "theme $name includes $entry but it is zero bytes"
        fi
      done
    done

    if [ "$theme_count" -lt 2 ]; then
      fail "$THEMES_FRAMEWORK has $theme_count kes:Theme node(s), expected at least 2 - one theme cannot show that a theme changes anything"
    fi

    mapfile -t distinct_titles < <(printf '%s\n' "${theme_titles[@]}" | sort -u)
    if [ "${#distinct_titles[@]}" -ne "${#theme_titles[@]}" ]; then
      fail "$THEMES_FRAMEWORK has themes sharing a jcr:title - the learner picks a theme by its title and could not tell them apart"
    fi

    # Same hash means the two themes render identically, which is the opposite of the
    # lesson however correct the nodes around them are.
    mapfile -t less_hashes < <(find "$frameworks/$THEMES_FRAMEWORK" -type f -name '*.less' -exec sha256sum {} + 2>/dev/null | cut -d' ' -f1 | sort)
    mapfile -t distinct_hashes < <(printf '%s\n' "${less_hashes[@]}" | sort -u)
    if [ "${#less_hashes[@]}" -ne "${#distinct_hashes[@]}" ]; then
      fail "$THEMES_FRAMEWORK has two .less files with the same sha256 - its themes ship identical CSS"
    fi
  fi

  # ---------------------------------------------------------------- criterion 3
  # The lesson is a view, and it is not a copy of another step's view.
  themes_view_dir=""
  while IFS= read -r dir; do
    themes_view_dir="$dir"
  done < <(find "$jcr_root/apps" -type d -name "$THEMES_VIEW" -path '*/tutorial-getting-started/*' 2>/dev/null)

  if [ -z "$themes_view_dir" ]; then
    fail "no $THEMES_VIEW view directory under tutorial-getting-started"
  else
    themes_view="$themes_view_dir/content.html"
    if [ ! -f "$themes_view" ]; then
      fail "no content.html at $themes_view"
    elif [ ! -s "$themes_view" ]; then
      fail "$themes_view is zero bytes"
    else
      themes_hash="$(sha256sum "$themes_view" | cut -d' ' -f1)"
      while IFS= read -r other; do
        [ "$other" = "$themes_view" ] && continue
        [ -s "$other" ] || continue
        if [ "$(sha256sum "$other" | cut -d' ' -f1)" = "$themes_hash" ]; then
          fail "$themes_view is byte-identical to $other - the themes step is a copy of another step's view"
        fi
      done < <(find "$jcr_root/apps" -type f -name content.html -path '*/tutorial-getting-started/*' 2>/dev/null)
    fi

    # ------------------------------------------------------------- criterion 5
    # Layouts are the step AFTER this one. A layouts directory here would teach the next
    # lesson early and hide this one behind a layout switch.
    if [ -d "$themes_view_dir/layouts" ]; then
      fail "$themes_view_dir has a layouts/ directory - layouts are the next step's lesson, not this one's"
    fi
  fi

  # ---------------------------------------------------------------- criterion 6
  # Every framework's title, directory name and code agree on one number, and together the
  # numbers cover 1 to 12 exactly once each.
  seen_steps=()
  for dir in "$frameworks"/*/; do
    [ -d "$dir" ] || continue
    name="$(basename "$dir")"
    case "$name" in
      tutorial-*) ;;
      *) continue ;;
    esac
    node="$dir/.content.xml"
    if [ ! -f "$node" ]; then
      fail "framework $name has no .content.xml"
      continue
    fi

    title="$(attr "$node" 'jcr:title')"
    title_step="$(step_number "$title")"
    dir_step="$(step_number "$name")"
    code="$(attr "$node" 'kes:uiFrameworkCode')"
    code_step="$(step_number "$code")"

    if [ -z "$title_step" ]; then
      fail "framework $name is titled \"$title\", which names no step"
      continue
    fi
    if [ -n "$dir_step" ] && [ "$dir_step" != "$title_step" ]; then
      fail "framework $name is titled \"$title\" - directory says step $dir_step, title says step $title_step"
    fi
    if [ -n "$code_step" ] && [ "$code_step" != "$title_step" ]; then
      fail "framework $name has kes:uiFrameworkCode=\"$code\" - code says step $code_step, title says step $title_step"
    fi
    seen_steps+=("$title_step")
  done

  mapfile -t sorted_steps < <(printf '%s\n' "${seen_steps[@]}" | sort -n)
  expected=1
  for step in "${sorted_steps[@]}"; do
    if [ "$step" -ne "$expected" ]; then
      if [ "$step" -lt "$expected" ]; then
        fail "step $step appears more than once in the ladder"
      else
        fail "the ladder jumps from step $((expected - 1)) to step $step - step $expected is missing"
      fi
    fi
    expected=$((step + 1))
  done
  if [ "$((expected - 1))" -ne "$EXPECTED_LAST_STEP" ]; then
    fail "the ladder ends at step $((expected - 1)), expected $EXPECTED_LAST_STEP"
  fi

  # A view whose heading names a different step than the directory it sits in. This is what
  # catches a renumbering that moved the directories and left the headings behind.
  while IFS= read -r view; do
    heading="$(grep -o -E '<h2>Step [0-9]+[a-z]?</h2>' "$view" 2>/dev/null | head -n 1)"
    [ -n "$heading" ] || continue
    heading_step="$(step_number "$heading")"

    # The view directory is the tutorial-getting-started child the file sits under.
    rest="${view#*/tutorial-getting-started/}"
    view_dir="${rest%%/*}"
    dir_step="$(step_number "$view_dir")"
    [ -n "$dir_step" ] || continue
    [ "$heading_step" = "$dir_step" ] && continue

    known=false
    for entry in "${KNOWN_HEADING_MISMATCHES[@]}"; do
      case "$rest" in
        "$entry") known=true; break ;;
      esac
    done
    if [ "$known" = true ]; then
      echo "check-tutorial-content: known mismatch, see #990 - $rest is headed \"Step $heading_step\" in $view_dir"
      continue
    fi
    fail "$rest is headed \"Step $heading_step\" but sits in $view_dir"
  done < <(find "$jcr_root/apps" -type f -name content.html -path '*/tutorial-getting-started/*' 2>/dev/null | sort)
done

if [ "$failures" -ne 0 ]; then
  echo "check-tutorial-content: $failures failure(s)" >&2
  exit 1
fi

echo "check-tutorial-content: OK - ${#projects[@]} generated project(s), themes step present and the ladder runs 1 to $EXPECTED_LAST_STEP"
