#!/bin/bash
# The one way to rebuild the generated world: compile the server, put back the pristine cache, run the generators,
# and let the world gate judge the result. The cache is only written if the world passes; a pass also stamps
# tools/worldgate.stamp, which the Claude Code hooks require before world code can be committed.
#
#   tools/regen-world.sh                      build, gate, write
#   WORLDGATE=warn tools/regen-world.sh       report findings but write anyway (experimenting; writes no stamp)
#   GENOPTS="-Dnythera.spawns=f -Dpreview=p.png" tools/regen-world.sh   extra generator options
#
# Then restart the server to see it, and read target/worldgate/report.txt and the area maps in target/worldgate/.
set -eo pipefail
cd "$(dirname "$0")/.."
# (a build that fails stops here: the last good jar would build yesterday's world and judge that instead)
(cd .. && mvn -B -q -pl "2006Scape Server" package -DskipTests 2>&1 | { grep -v JAVA_TOOL || true; }) || { echo "BUILD FAILED: nothing generated" >&2; exit 2; }
git checkout 7f1dc47 -- data/cache && git reset -q data/cache   # the cache as it was before any of the generated world
java -Dworldgate="${WORLDGATE:-enforce}" $GENOPTS -cp target/server-1.0-jar-with-dependencies.jar \
	com.rs2.tools.mapgen.CursedIslandGenerator 2>&1 | grep -v -e JAVA_TOOL -e '^Wrote region'
# Creatures defined but not placed anywhere yet (model, frames, sequences and npc definition at fixed ids clear of
# everything above): written into the cache the world was just built into. Placing one is a generator change.
java -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.Shardback data/cache 2>&1 | grep -v JAVA_TOOL
