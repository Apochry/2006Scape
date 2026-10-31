#!/bin/bash
# The world gate's Claude Code hooks (wired up in .claude/settings.json). The world generators refuse to write a world
# that fails WorldGate; these make sure nobody - Claude included - can get round that by simply not running them:
#
#   worldgate.sh edit     PostToolUse on Edit/Write: the first time world code is touched in a session, puts the
#                         rules in front of Claude (load the world-building skill, design first, gate before done).
#   worldgate.sh commit   PreToolUse on Bash: refuses a git commit that includes world code unless the world it
#                         builds has passed the gate (tools/worldgate.stamp matches the code) and the stamp is staged.
#   worldgate.sh stop     Stop: won't let a session end with uncommitted world-code changes the gate hasn't passed.
#
# "World code" is the list in "2006Scape Server/tools/worldgate.files"; the stamp is a SHA-256 of those files, in that
# order, written by WorldGate only when the world passes (see WorldGate.sourceHash).
set -uo pipefail
mode="${1:-}"
input="$(cat)"
root="$(cd "$(dirname "$0")/../.." && pwd)"
server="$root/2006Scape Server"
list="$server/tools/worldgate.files"
stamp="$server/tools/worldgate.stamp"
[ -f "$list" ] || exit 0

world_files() { sed -e 's/#.*//' -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' "$list" | grep -v '^$'; }
current_hash() { (cd "$server" && world_files | while IFS= read -r f; do cat "$f"; done | sha256sum | cut -d' ' -f1); }
stamped_hash() { [ -f "$stamp" ] && head -1 "$stamp" | tr -d '[:space:]'; }
# world files (repo-relative) that git lists from the given command (diff --cached, status...)
world_paths() { world_files | sed 's|^|2006Scape Server/|'; }
touched() { # $@: git args producing repo-relative paths
	git -C "$root" "$@" 2>/dev/null | grep -Fx -f <(world_paths) || true
}
fresh() { [ "$(current_hash)" = "$(stamped_hash)" ]; }
json() { jq -cn "$@"; }

case "$mode" in
edit)
	f="$(jq -r '.tool_input.file_path // .tool_response.filePath // empty' <<<"$input")"
	[ -n "$f" ] || exit 0
	rel="${f#"$server"/}"
	world_files | grep -Fxq -- "$rel" || exit 0
	sid="$(jq -r '.session_id // "x"' <<<"$input")"
	marker="${TMPDIR:-/tmp}/worldgate-told-$sid"
	[ -e "$marker" ] && exit 0
	touch "$marker"
	json --arg c "You just changed world code ($rel). The world gate now governs this change:
1. Load the world-building skill (.claude/skills/world-building/SKILL.md) before going further, if it isn't loaded.
2. A new area must be declared in WorldAreas first, with its brief (purpose, arrival, landmark) and must-reach spots.
3. Rebuild with \"2006Scape Server/tools/regen-world.sh\". It refuses to write a world with any unaccepted finding and names the generator line behind each one: fix them there.
4. Look at the area maps in \"2006Scape Server/target/worldgate/\" and in game, not just the verdict.
Commits of world code, and ending the session with it changed, are blocked until the gate passes (tools/worldgate.stamp)." \
		'{hookSpecificOutput:{hookEventName:"PostToolUse",additionalContext:$c}}'
	;;
commit)
	cmd="$(jq -r '.tool_input.command // empty' <<<"$input")"
	grep -Eq '(^|[;&|[:space:]])git([[:space:]]+-[^[:space:]]+([[:space:]]+[^-[:space:]][^[:space:]]*)?)*[[:space:]]+commit([[:space:]]|$)' <<<"$cmd" || exit 0
	staged="$(touched diff --cached --name-only)"
	if grep -Eq '[[:space:]](-a|--all|-[a-zA-Z]*a[a-zA-Z]*)([[:space:]]|$)' <<<"$cmd"; then
		staged="$staged$(touched diff --name-only)"
	fi
	[ -n "$staged" ] || exit 0
	reason=""
	if ! fresh; then
		reason="This commit includes world code ($(echo $staged | tr '\n' ' ')) but the world it builds hasn't passed the world gate since it changed. Run \"2006Scape Server/tools/regen-world.sh\", fix every finding at the line it names (or accept one, with its reason, in tools/worldlint-accepted.txt), and commit again with the new tools/worldgate.stamp."
	elif [ -n "$(git -C "$root" diff --name-only -- "2006Scape Server/tools/worldgate.stamp")" ] && ! git -C "$root" diff --cached --name-only | grep -qx "2006Scape Server/tools/worldgate.stamp"; then
		reason="The world passed the gate, but its new stamp isn't staged: git add \"2006Scape Server/tools/worldgate.stamp\" (and the regenerated data/cache) along with the world code."
	fi
	[ -z "$reason" ] && exit 0
	json --arg r "$reason" '{hookSpecificOutput:{hookEventName:"PreToolUse",permissionDecision:"deny",permissionDecisionReason:$r}}'
	;;
stop)
	[ "$(jq -r '.stop_hook_active // false' <<<"$input")" = "true" ] && exit 0
	fresh && exit 0
	changed="$( (touched diff --name-only; touched diff --cached --name-only) | sort -u)"
	if [ -n "$changed" ]; then
		json --arg r "World code has changed ($(echo $changed | tr '\n' ' ')) and the world it builds hasn't passed the world gate. Before finishing: run \"2006Scape Server/tools/regen-world.sh\", fix what it reports, and look at the maps in target/worldgate/. If the change is abandoned, revert it instead." \
			'{decision:"block",reason:$r}'
	else
		json '{systemMessage:"World gate: the committed world code does not match tools/worldgate.stamp (it was changed without regenerating, or came in through a merge). Regenerate with 2006Scape Server/tools/regen-world.sh before the next world change."}'
	fi
	;;
esac
exit 0
