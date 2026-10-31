# Independent design review

The gate catches what a rule can name. It can't see that a place is dull, cramped, or out of scale. It also can't see
that the landmark is lost against the hills, or that a camp looks set down rather than lived in. The person who built
an area is the worst judge of it: they see what they meant, not what is there. So every area you change gets a second
look from a reviewer who has seen neither the code nor your intentions.

## When

After `tools/regen-world.sh` says `WORLD GATE: PASSED`, before you commit, for every area you changed. Do it again
after a large rework.

## How

1. Gather, for each changed area:
   - the brief from `WorldAreas` (purpose, arrival, landmark);
   - `"2006Scape Server/target/worldgate/<area>.png"`, the top-down map;
   - that area's `design:` line from `target/worldgate/report.txt`;
   - in-game screenshots, if you have them: one from the arrival point and one of the landmark, at a moderate pitch.
     These are worth more than the map.
2. Spawn a **fresh** subagent (Agent tool, general-purpose). Don't fork, because a fork inherits your context, and
   your context is the bias being removed. Give it the prompt below, with the paths filled in. Don't add your design
   notes, the code, or what you were going for.
   Tell it the map's legend: what the dots, strips and colours are, and where the named places stand, by
   coordinates. A reviewer with only a bare map will misread things. The first Nythera review took a thin dirt track
   for "no path", and a pier for a ladder.
3. **Check every finding before you act on it.** A reviewer's report is a set of claims, written as confidently when
   it's wrong as when it's right. Look at the spot (`PathMap`, `Ascii` or a screenshot), and sort each finding into
   real, misread, or real-but-a-tradeoff (moving the crater would move every quest that uses it). Fix only what is
   real.
4. Treat the real findings as you would the gate's. Fix the real ones at the source, then regenerate and gate. If a finding
   is wrong because the reviewer lacked context (a quest needs it that way), note why in your summary.
5. **Ratchet.** If a reviewer reports the same kind of thing a second time, in any area, it's a rule. Write it into
   `DesignLint`, plant it in `GateSelfTest`, add it to the skill's findings table, and stop relying on reviewers for
   it.

## The prompt

```
You are reviewing a game area for a RuneScape-2006-style world, as an experienced level designer would review a
junior's work. You have not seen how it was made. Judge only what is in front of you.

The area's brief (what it is meant to be):
  Purpose:  <purpose>
  Arrival:  <arrival>
  Landmark: <landmark>

Its numbers: <the design: line from the report>

Look at these images (use the Read tool on each path):
  <path to target/worldgate/<area>.png>   top-down map, 6 px per tile; rings mark accepted findings
  <screenshot paths, if any>

List the amateur tells you can see: the signs that this was produced by code, or by someone who hasn't designed
places before. Look in particular for:
- anything ruled, gridded, evenly spaced or symmetrical that nature or real builders wouldn't make so;
- props scattered evenly everywhere, with no clusters, no gradients, and no reasons for where things are;
- empty stretches that read as unfinished rather than intentionally open, and areas so packed they read as noise;
- buildings, camps and props that don't relate to each other, the paths or the ground: no yards, no worn ground, no
  clutter where people work;
- a landmark that doesn't stand out, can't be seen on the way in, or doesn't match the brief;
- paths that don't go where a person would walk, or places a person would go that have no path;
- colours, floors or materials that clash, or edges between them that look cut rather than worn;
- scale problems: doors too small, tents bigger than houses, a "great hall" you cross in three steps;
- anything the brief promises that isn't there, and anything there that contradicts the brief.

For each tell give: where it is (tile coordinates if you can read them off the map's grid, otherwise a description),
what is wrong, why a player would notice, and the smallest change that fixes it. Order them worst first. Give at most
ten. Don't pad the list: if the area is genuinely good, say so and list only what is really wrong. Don't comment on
code. You haven't seen any.
```

## What a useful review looks like

> 1. **The quarry's spoil heaps are in a ruled row** (1528-1540, 2768). Five heaps the same size, evenly spaced,
>    read as placed by a loop. Cluster them toward the cut face, and vary their size.
> 2. **No worn ground round the inn door**. The grass runs clean up to the step, as if nobody uses it. Add a patch
>    of dirt and a bench by the door.

Not useful: "Consider adding more detail." "The area could be more interesting." Ask again for specific tiles and
specific changes.
