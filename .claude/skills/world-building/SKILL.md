---
name: world-building
description: How to design and build game world areas (islands, continents, camps, towns, dungeons, landmarks) so they read as real places rather than code-placed primitives. Use whenever adding to or changing the map generators in "2006Scape Server/src/main/java/com/rs2/tools/mapgen" (CursedIslandGenerator, VoidRealmGenerator, WorldProps, SkullRock, ModelForge scenery), placing objects/paths/rivers/walls/doors, or reviewing an area's look. Covers the world gate (the generator refuses to write a world that fails it, and hooks refuse commits of world code that hasn't passed it), the design rules, the generator helpers, the 317 client's quirks, and what each finding means.
---

# Building worlds that hold together

A map written as code fails in a particular way. Each line places one thing correctly by itself, and nobody stands
where the player stands to see whether the things add up to a place. The typical results:
- roads that stop in a field
- roads and rivers ruled dead straight between two waypoints
- two paths that touch only at a corner
- a doorway with no wall, a wall with no building, a cave mouth in flat grass
- a flag with no pole
- a tent hanging off a slope
- a rock planted inside a tree
- a giant model the client silently never draws

None of these are hard to avoid. They come from skipping three steps: **design the place before placing objects,
follow the rules below while placing, and verify from the map and from the player's eye before calling it done.**

## 0. The system: you can't ship a broken world by forgetting

Discipline alone kept failing, so the toolchain now makes the right way the only way through:

| Layer | What it does | Where |
|---|---|---|
| **Declare before building** | Every area is registered in `WorldAreas` with a `Brief` (purpose, arrival, landmark; each a real sentence, or it throws), its arrival tile(s) and its must-reach spots (npcs, doors, camps). Anything a generator places outside every declared area fails the gate as `UNREGISTERED`. | `WorldAreas.java` |
| **Helpers that can't do the wrong thing** | `road()` meanders and breathes on its own. `setOverlay()` blocks water as it paints it, so nobody walks on water by accident. Dungeon `hug()` checks an object's whole footprint and refuses any spot that would cut a cave in two (`keepsCaveWhole`). `footprintFree` checks footprints, not anchors. | the generators |
| **The gate** | After building, before a byte is written, `WorldGate` lints every declared area in memory with every WorldLint check, floods reach from each arrival, and checks every must-reach spot. Any unaccepted finding means **the cache is not written** (exit 3). | `WorldGate.java`, called from `CursedIslandGenerator.main` |
| **Findings point at code** | Every map object records the generator frames that placed it (`MapObject.origin`); every painted path tile records who painted it. Each finding prints `<- Door from CursedIslandGenerator.object:1514 < seededMistakes:687`, the line to change. | `Origin.java` |
| **Accepting is explicit and expires** | An accepted finding needs `KIND x,y  reason`; a line with no reason is `ACCEPT-UNREADABLE`, and a line that no longer matches anything is `ACCEPT-STALE`. Both fail the gate, so the accepted list can't rot into a blanket pardon. | `tools/worldlint-accepted.txt` |
| **The design gate** | Broken isn't the only failure: `DesignLint` refuses the tells of a place written as code. Every prop of a kind turned one way, props ruled into rows, the same camp pasted twice, ground the player walks straight up like a wall, one-tile spikes, floors cut in rectangles, named places no path serves, roads that join nothing, a brief's landmark that isn't built or that no path passes, npcs spawned inside rock, and options ("Search", "Shake") that no game code answers. | `DesignLint.java` |
| **The gate tests itself** | `GENOPTS=-Dworldgate.selftest=true tools/regen-world.sh` plants one of every amateur mistake into an empty meadow and fails unless the gate reports each one. It writes nothing. Run it after any change to the lint, the design checks or the gate: a check loosened for one false positive can quietly go blind. | `GateSelfTest.java` |
| **A second pair of eyes** | No rule sees ugliness. After the gate passes, a fresh reviewer who hasn't seen the code judges each changed area from its brief, its map and screenshots, using `design-review.md`. Anything it finds twice becomes a new DesignLint check. | `design-review.md` (this folder) |
| **Stamp and hooks** | A pass writes `tools/worldgate.stamp`, a hash of the world code listed in `tools/worldgate.files`. Claude Code hooks (`.claude/hooks/worldgate.sh`) put these rules in front of you the first time world code is edited in a session, **deny any `git commit` that includes world code without a matching, staged stamp**, and **block ending a session** with ungated world changes. | `.claude/settings.json` |

The one command is `"2006Scape Server/tools/regen-world.sh"`: it compiles, restores the pristine cache, generates, and
gates. It writes the report to `target/worldgate/report.txt`, with each area's brief beside its findings, and a map of
every area, findings ringed, to `target/worldgate/<area>.png`. Use `WORLDGATE=warn` only to look at a broken world
while experimenting. It writes the cache but no stamp, so it can't be committed.

When the gate refuses, fix the finding at the line it names. Don't widen an accept, and don't loosen a check to get
green. If a check is genuinely wrong, fix the check and re-prove it: seed the mistake on purpose, and confirm the gate
still catches it. The proof run that added these layers seeded:
- a straight path that ends in a field
- a door with no wall
- a lone wall stub
- a cave mouth on flat ground
- barrels across the jetty

All five were refused, with their lines. It also caught a real quest-breaker: a 3x3 crystal plugging the Crown Vault's
only tunnel to the Ember.

The design gate's first run over the finished world found 94 things, nearly all real: every Nythera destination but
three had no path that joined the road from the rift (the inn road and the east road started three tiles short of the
stone road's end, and Gatehold, the croft, the quarry, the barrow, the fissure and Silkwood had no track at all),
Skullbrine's east road was cut off from the town deck by a shack stamped across its start, the ridge pass had unblocked
a 66-level drop to the tarn beside the road, 21 npcs stood inside crystals, graves, boats and rocks (their placers
checked anchors, not footprints), the camps' fires and the graveyard's braziers all faced one way, boulders lay in
ruled rows, Hollowdeep had no void pool for its brief's landmark, and the withered palms offered a "Shake" that did
nothing. Each was fixed at its source. The self-test then caught that the terrain checks had been tested only with
seeds that never fired.

## 1. Design first: a place, not a list of objects

Before writing a coordinate, write down (a comment in the generator is fine):

1. **What happened here and who uses it.** "Pirates salvaged ship's planks into a deck and built shacks round it."
   "The kingdom's stone road ran through this gate; the gate fell in the war."
2. **How people arrive and where they go.** List the destinations (nodes): doors, the camp fire, the jetty, the cave
   mouth, the shop, the junction, the edge of the area that leads elsewhere. Then list the routes between them (edges).
   That graph is the path network. Every path is one of its edges.
3. **What each cluster is for.** A camp is a fire, with seats facing it, a tent facing the fire, firewood stacked
   handy, water within reach, and the trodden ground around it. If you can't say why an object is there, it shouldn't be.
4. **The landmark and the view.** What does the player see from the arrival point? In the 317 camera that is about
   15 tiles, and the camera can't zoom out. A landmark has to read from where you first see it.

Sketch it on a grid, with ASCII in a comment or the `-Dzoom` / `MapRender` top-down render, before generating details.

## 2. The rules, and why

### Paths and roads are a network
- **Every path has a meaningful thing at each end:** a door, gate, jetty, camp, landmark, junction with another
  path, or the edge of the area towards the next place. A path that ends in open ground reads as a mistake.
- **Paths join along an edge, never at a corner.** Make a branch wide enough (2.2+ tiles) and run it into the other
  path, so they overlap. Two tiles touching diagonally is a "pinched" join.
- **Paths go where feet would go.** Take the short comfortable line, bend round obstacles, slopes and water, and
  keep off the middle of the thing you're walking to (go round the serpent's ribcage, not through it).
- **Nothing walked is straight.** Use `VoidRealmGenerator.meander(points, amplitude, wavelength, seed)` (via either
  generator's `road(...)`): amplitude about 1-2.5 tiles, wavelength 7-14 tiles. Let the width vary by about ±10%.
  The meander fades at the waypoints, so put waypoints where the path must be and add a bend point between long
  straight pairs.
- **Built things may be straight**, because people built them that way: jetties, walls, a kingdom road at its gate,
  a boardwalk. Even those end at something, and follow the shore or terrain in short segments.
- **Nothing solid stands in the middle of a road.** Signs, lamps and torches go at its edge.

### Water obeys gravity and erosion
- **A river runs from a source to a sink:** a spring in the hills, a lake, the sea. It runs downhill, never ends in
  a field, and widens downstream. Its banks slope down to it.
- **Rivers meander,** and much more than roads. Use the river field (`buildRiver`) with amplitude about 3 tiles and
  wavelength about 16.
- **Shores and banks are ragged at more than one scale.** Add `VoidRealmGenerator.ragged(x, y, seed)` to any
  coast, lake or river distance function. Smooth sine-based shapes cut into tiles leave long staircase runs that the
  overlay smoothing then turns into ruler-straight diagonals.
- **Crossings sit where the river is narrow,** with land and a path at both ends (see the bridge finder in
  VoidRealmGenerator). Jetties start on the shore and reach water deep enough to fish or moor.

### Structures attach to something
- **A wall belongs to an enclosure or runs into something:** a building, cliff, rock, water, or another wall. A
  short run of wall standing alone in a field is the classic slap-dash error.
- **A door or gate exists only in a wall or fence,** with wall on both sides. A gap in a fence is fine as a field
  entrance. A door with no wall is not.
- **A cave mouth, tunnel or archway needs something to open into:** a cliff, a rock face or a rising hillside behind
  it, or a wall either side.
- **Flags have poles, lamps have posts, signs stand on the ground,** and ladders lead up to a floor or down into a
  hole. Bridges have land and a path at both ends.
- **Copying stock buildings (`stampBuilding`) drags along whatever was in the rectangle:** a corner of the next
  house, a panel of wall. And the server often spawns doors rather than storing them in the map, so the copy has an
  empty doorway. Look at each stamp in game, and remove strays in `stampBuilding`.

### Things rest on the ground, and don't overlap
- **The client draws an object flat at the mean height of its anchor tile's four corners.** Anything with a flat
  bottom (a tent, stall, boat, coffin, grave, table, cart) has one end floating and the other buried on a slope.
  Level the ground under it: `flatten`, which `tent()` and `bedFlatProps()` do for you. Hand-built models that lie
  over terrain sample the heightmap per vertex (see `SkullRock.at`).
- **Sink things into the ground.** A bone or log lying on top of the ground looks like a stick dropped there; half
  buried, it looks like it has always been there. Rocks sit in the ground, not on it.
- **Objects grow north-east from their anchor tile.** A free-space test that only looks at anchors plants rocks
  inside trees and boats. Use `CursedIslandGenerator.footprintFree` (called by `isFree` and `free`).
- **Solid objects don't share a tile, and no tile holds five or more objects:** the client stops drawing at the
  fifth.
- **Smoothing moves coasts after vegetation is placed.** Clear what ends up in the water (`drownedScenery`).

### Composition
- **Compose first, then scatter.** Place every meaningful object by hand, add the site to `keepClear` / `site()`,
  and only then let `vegetation()` and the scatterers fill the gaps.
- **Density has a gradient:** busy at the centre, thinning at the edges. There's trodden ground round camps and wear
  along paths.
- **Vegetation answers the terrain:** trees not on roads, steep cliffs or beaches, reeds and willows by water,
  nothing on the trodden ground.
- **Things face what they're used with.** Tents face their fire, seats face the fire, graves face their barrow's
  door, and shop stalls face the road.
- **Prefer a hand-built piece to a stock object that reads wrong.** WorldProps has the canvas tent, hearth stones,
  log seat, banners, standing stones, the Broken Gate and the black flag. Stock boulders 6950/444 render near-black,
  and stock tents render as black slabs.

### The client's hard limits (it fails silently)
- **A model is not drawn at all** if its depth range (radius across plus height, from its own centre) is more than
  about 1500 units. Keep each piece's radius under about 650 units, roughly 5 tiles. Cut anything bigger into
  pieces, as `SkullRock.pieces()` does: each piece has its own footprint and anchor, is non-solid (opcode 17), is
  unclickable (opcode 19 = 0), and has no shadow (opcode 64).
- **Map terrain values each ride in one byte:**
  - underlay as opcode `81 + id`, so floor ids above 174 can't be underlays
  - settings as `49 + flags`
  - overlay as a byte

  Passing an **object id as a floor** wraps silently and scrambles the rest of the region. This happened: a
  `ROCK = 478` object constant shadowed the floor `ROCK = 56`. `MapRegion.encodeTerrain` now refuses bad values.
  Never give a constant a name that already means something else in an imported class.
- Wall rotation: 0 = west edge, 1 = north, 2 = east, 3 = south.
- Wall types:
  - 0 = a straight wall
  - 1 and 3 = corner posts
  - 2 = an L-corner (edges `rot` and `rot + 1`)
  - 9 = diagonal

  Type 10 is scenery and 22 is ground decoration. Height on the client is `-8 × map height`, and a map height of 1
  is read as 0.
- A footprint outside the 104-tile loaded scene, or one on a tile already holding 4 objects, is dropped.
- A cloned loc keeps its source's options: the dark void crystal kept the stock crystal's dead "Search". `.remove(30)`
  options nothing answers. Scenery shouldn't offer an option that does nothing, and the gate's reach check counts any
  option as "usable".

## 3. Workflow

1. **Design** (section 1). Declare the area in `WorldAreas`: its brief, its arrival and its must-reach spots. Then
   write the site's story and its node/edge list as a comment where the site is generated.
2. **Generate in this order:**
   1. terrain
   2. water (coast, lake, river)
   3. the path network
   4. structures, on levelled ground
   5. composed props
   6. `keepClear` and sites
   7. scatter and vegetation
   8. overlay smoothing
   9. cleanup (`drownedScenery`, `bedFlatProps`)

   Placing paths after structures, or props after scatter, is how things end up inside each other.
3. **Regenerate and gate:** run `"2006Scape Server/tools/regen-world.sh"`, adding
   `GENOPTS="-Dnythera.spawns=FILE"` and then `python3 tools/nythera_data.py FILE` when Nythera's spawns changed. The
   gate covers every declared area, so there's no list of areas to remember. A refusal leaves the cache unwritten.
4. **Read the report and the maps** in `target/worldgate/`. Fix each finding at the line it names, or, only if it's
   right as it is, add it to `2006Scape Server/tools/worldlint-accepted.txt` with the reason. "The wreck is set
   dressing out in the water" is a reason. "It's fine" is not. For a closer look at one spot, WorldLint still runs on
   its own against the written cache:
   `WorldLint x0 y0 x1 y1 -from X,Y -png f.png`.
5. **Get an independent design review** of every area you changed (see `design-review.md`): hand a fresh subagent
   the area's brief, `target/worldgate/<area>.png` and the `design:` line from the report, plus screenshots if you
   have them, but no code and no account of what you meant. It reports the worst amateur tells first. Fix what's real at
   the source and regenerate. When a reviewer flags the same kind of thing twice, write it into `DesignLint` as a check,
   seed it in `GateSelfTest`, and it never needs a reviewer again.
6. **Look at it from the player's eye.** Log in, teleport to each arrival point and landmark, and take screenshots
   at a moderate pitch from two or three angles. Walk each route. The linter can't see ugliness, scale, colour, or a
   model that isn't drawn. Compare before and after shots side by side.
7. **Document** the place and its object ids in `docs/SKULLBRINE_ISLE.md` or `docs/VOID_QUESTS.md`, and keep the
   "next new object/item id" notes in `VoidConstants` current.
8. **Changed a check?** Run the self-test (`GENOPTS=-Dworldgate.selftest=true tools/regen-world.sh`). It must say
   `GATE SELF-TEST: PASSED` before you trust a green gate.

## 4. WorldLint findings and what to do about each

| Finding | Means | Usual fix |
|---|---|---|
| `MUST-REACH` | A spot the area's declaration says the player must reach can't be walked to from any arrival | Clear the way, unblock the floor, or declare the extra arrival the game really uses (`alsoArrives`) |
| `UNREGISTERED` | A generator placed something outside every declared area | Declare the area in `WorldAreas`, with its brief, or move the thing |
| `ACCEPT-STALE` / `ACCEPT-UNREADABLE` | An accepted-file line that matches nothing any more, or has no reason | Delete it, or write the reason |
| `WATER-WALKABLE` | Whole water tiles with no block flag, so the player walks on water | Paint water with `setOverlay` (it blocks); don't unblock rectangles that take in water |
| `UNREACHABLE` | A path, or a usable object, can't be walked to from the arrival point | Open a way in, move it, take a dead option off it, or accept it if it's set dressing (a wreck offshore) |
| `TILE-OVERFULL` | 5+ objects cover a tile, so the client drops some | Thin them out; check big footprints overlapping |
| `DOOR-NO-WALL` | A door or gate with no wall on one or both sides | Build the wall, or remove the door |
| `OPENING-UNBACKED` | A cave, tunnel or archway in open ground | Raise a cliff or rock face behind it, or set it in a wall |
| `PATH-DEAD-END` | A path stops with nothing to arrive at | Extend it to its destination, or join it to another path |
| `PATH-DIAGONAL-JOIN` | Path tiles meet only at a corner | Widen the branch, or move its end into the other path |
| `PATH-OBSTRUCTED` | Something solid in the middle of a road | Move it to the edge, or re-route the road round it |
| `STRAIGHT-PATH` / `STRAIGHT-WATER` | An edge runs dead straight for 9+ / 17+ tiles | Meander it, add a bend point, or add `ragged` to the shore function |
| `WALL-FRAGMENT` | A short run of wall standing alone | Join it to a building or enclosure, or remove it (often a stamp leftover) |
| `WALL-LOOSE-END` | A wall stops in open ground (one-to-two-tile gaps between ends count as doorways and aren't reported) | Run it into something, close the loop, or end it at a post |
| `ON-WATER` | Scenery standing in water | Move it onto the shore (or a jetty); clear drowned vegetation |
| `STEEP-FOOTPRINT` | A flat-bottomed thing on sloping ground | `flatten` under it (`LEVEL_BEDDED`, `tent()`) |
| `OVERLAP` | Two solid objects in one tile | Move one; fix the free-space test if it's scatter |
| `PATH-SCRAP` | A few path tiles on their own | Remove them, or connect them |
| **Design** (`DesignLint`) | | |
| `NPC-STUCK` | An npc spawns inside something solid, blocked ground, or water (fishing spots, ducks and swans may stand in water) | Move the spawn; make the placer test whole footprints (`footprintFree`, `dungeonSize`), not anchors |
| `NPC-UNREACHABLE` | An npc the player can't walk up to | Move it, or open the way |
| `UNSERVED` | A must-reach place has no path within 6 tiles, or only a path that never joins the one from the arrival | Lead a track to its door, gate or fire from the nearest road (`tracks()`); accept only if being hard to find is the point |
| `PATH-ISLAND` | A path of 8+ tiles that joins nothing the player arrives by (often a doorstep stamped too far from the street, or a road whose start was built over) | Connect it, or re-route what cut it off |
| `LANDMARK-UNPLACED` / `LANDMARK-MISSING` | The area doesn't say where its brief's landmark stands, or nothing of ours is built there | `landmarkAt(x, y)` in `WorldAreas`; then build the landmark the brief promises |
| `LANDMARK-UNSEEN` | No walkable path comes within 20 tiles of the landmark | Route a path past it: a landmark is for seeing |
| `CLIFF-WALKABLE` | Neighbouring walkable tiles 14+ height levels apart: the player strolls up a wall | Block the cliff (`slopes()` does on mountains) or grade it; never unblock a box that takes in a drop |
| `HEIGHT-SPIKE` | One walkable tile sticks up or drops 7+ levels from all four neighbours | Smooth the heightmap there |
| `COPY-PASTE` | The same arrangement of 4+ props in two places 10+ tiles apart | Compose each place for itself: vary pieces, spacing and turn (deliberate symmetry, like a face's two eyes, is acceptable with that reason) |
| `GRID-PATTERN` | 4+ of one prop at equal spacing on a line, standing clear of others of their kind | Scatter them; for scans, refuse a placement that would line up a third (`rock()` / `extendsRow`) |
| `UNIFORM-ROTATION` | 8+ of one prop, 90% turned the same way | `CursedIslandGenerator.turn(x, y)` for things with no front, or a random turn |
| `STRAIGHT-FLOOR-EDGE` | A ground (underlay) or decorative floor edge ruled straight along the grid for 14+ tiles (22+ on a slant): a rectangle showing | Shape it with noise or a curve; accept it only if it's built (a hall, a paved yard) |
| `DEAD-OPTION` | One of our objects offers an option ("Search", "Shake") that no game code handles, usually inherited from the stock object it was cloned from | `.remove(30)` (or the op's opcode) on its definition, or handle it |

The kinds of floor it treats as paths, water and built floors are listed at the top of `WorldLint.java`. Extend
those lists when you add new floors.

## 5. Before you say an area is finished

- [ ] The site has a written story, and a node/edge list for its paths.
- [ ] Every path ends at something on both ends, and joins along an edge.
- [ ] No road, river or shore is ruled straight, unless it's a built thing and that's deliberate.
- [ ] Every wall, door, gate, cave mouth, flag, lamp and ladder is attached to what makes sense of it.
- [ ] Flat-bottomed props stand on level ground, nothing overlaps, and nothing stands in water or in a road.
- [ ] Large hand-built models are split under the client's size limit, and are seen in game, not just in the cache.
- [ ] The area is declared in `WorldAreas` with a real brief and its must-reach spots.
- [ ] `tools/regen-world.sh` says `WORLD GATE: PASSED`, every accepted finding is explained, and the new
      `tools/worldgate.stamp` is committed with the code and the cache.
- [ ] You've looked at in-game screenshots from the arrival point and each landmark, at two angles each.
- [ ] Every named place is on the path network, and the brief's landmark is built where `landmarkAt` says, with a path
      passing within sight of it.
- [ ] Nothing is ruled, pasted or turned in step; no npc stands in anything; no prop offers an option that does nothing.
- [ ] A fresh reviewer has judged the area from its brief, map and screenshots (`design-review.md`), and what it found
      is fixed or answered.
- [ ] If you changed a check, the self-test passes.
- [ ] The docs and the object/item id notes are updated.
