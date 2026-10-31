# Skullbrine Isle

A cursed pirate island south-west of the mainland, the dungeon beneath it, and the quest
**Curse of Skullbrine** (2 QP), a quest for new players. The dungeon holds a dormant portal
to another world. The Void quest series opens it: see [VOID_QUESTS.md](VOID_QUESTS.md).

## The island

- **Skull Rock** (north): a giant bone-white skull half-buried in the island, its face tilted up
  to the sky. Its eyes are pools of green water with ghostfire burning in them, and the cave into
  Blackbrine's Hold is its mouth. It is drawn as a skull on the minimap. The terrain gives it its
  bulk (and keeps it climbable); a sculpted face laid over it gives it its bones: heavy brow ridges
  over black sockets, the nasal cavity, cheekbones and their arches, a row of great teeth over the
  cave mouth (a gap where the cave opens, one knocked out, one broken), the lower teeth, the jaw's
  two arms reaching down either side of the plaza, and a crack running up the left brow. The road
  from town crosses the jaw plaza as a trodden track and runs right up to the cave mouth.
- **Deadwater Cove** (south): a bay of sickly green water with the wreck of the *Drowned Maiden*,
  the masts of older wrecks, and sea stacks at its mouth. The jetty runs out into it, ghostfire torches
  on its planks.
- **Rattlebone Row**: the crew's shanty town around a plank deck with a gallows, stocks and a
  cage. The shacks are copied from Mos Le'Harmless. Mad Pete keeps shop here.
- **Graveyard of the drowned** (west), a **swamp** (east) and a **sea serpent's skeleton** on
  the east beach. The east road bends south round the serpent's ribcage and ends at its skull.
- **The west beach**: a net/bait (shrimp) fishing spot just offshore at (1978, 2895), a beached row boat, nets drying,
  and one bare dead tree you can chop for firewood.
- The *Drowned Maiden*'s black flag flies from a ship's spar stepped in a cairn: at the jetty end, in the town, in the
  graveyard and on the skull's crown.
- Black volcanic sand, ash and dead grass, withered palms, Morytanian trees and giant mushrooms.
  Green ghostfire torches and lamps are the island's signature light.

- **The Dragonvein cavern**: a side cave west of the portal chamber in Blackbrine's Hold, with six
  dragon rocks (level 60 Mining). See `docs/MINING_SMITHING.md`.

## Getting there

- **Bosun Grimsby** on the Port Sarim docks starts the quest and sails you to the island.
  He waits on the island jetty to take you back.
- Admins can use `::island` to go to the jetty, and `::skullbrine <stage>` to set the quest stage.

## Quest walkthrough (stages in `Player.skullbrine`)

| Stage | What happens |
|------:|--------------|
| 0 → 1 | Talk to Bosun Grimsby in Port Sarim and agree to help. |
| 1 → 2 | Sail to the island and talk to **Mad Pete** at the pirate camp. |
| 2 → 3 | Search the captain's skeleton by the bow of the *Drowned Maiden* (in the cove, west of the jetty) for **Blackbrine's log**, then read it. |
| 3 → 4 | Enter the cave through Skull Rock's mouth. Kill the **Cursed first mate** (lvl 20) in the west grotto for the **Captain's key**, then open the chest in the plunder cavern to get the **Void pearl**. |
| 4 → 5 | Use the pearl on the portal in the northern chamber, or just click the portal. |
| 5 → 6 | Talk to the **Ghost of Captain Blackbrine** to finish. Rewards: 2 QP, 2,500 Attack XP, 2,500 Strength XP, 3,000 coins and a pirate's hat. |

### Difficulty

Aimed at new players: there are no requirements, and nothing on the island attacks first.

| Monster | Level | Where |
|---|---:|---|
| Drowned deckhand (a drowned pirate in sea-teal) | 5 | the cove's beaches |
| Cursed pirate | 12 | around the town, graveyard and swamp; in the dungeon |
| Cursed first mate ('Smiling' Sam) | 20 | the dungeon's west grotto (quest boss) |

They drop food, bronze and iron weapons and coins. Mad Pete sells cheap food, a bronze or iron
scimitar, a wooden shield and basic tools.

## Where things live

| What | Where |
|------|-------|
| Ids and coordinates shared by the generator and the content | `src/main/java/com/rs2/game/content/skullbrine/SkullbrineConstants.java` |
| Dialogues, travel, objects, drops | `src/main/java/com/rs2/game/content/skullbrine/SkullbrineIsle.java` |
| Quest journal, log text, rewards | `src/main/java/com/rs2/game/content/quests/impl/CurseOfSkullbrine.java` |
| NPC stats, spawns, drops, shop | `data/cfg/npcDefinitions.json`, `npc.json`, `spawns.json`, `npcdrops.json`, `shops.json` (shop 349) |
| Map + cache generator | `src/main/java/com/rs2/tools/mapgen/` |

After Curse of Skullbrine, the portal is handled by
`com.rs2.game.content.voidrealm.VoidRealm`, which `SkullbrineIsle` asks first for every click,
dialogue and kill. See [VOID_QUESTS.md](VOID_QUESTS.md).

## Regenerating the map

The island, dungeon, new NPCs (ids 3852-3857), new items (7956-7958, and dragon ore/bar 7959-7960)
and new scenery (objects 14974-14985: ghostfire, ghostfire torches and lamps, bone fangs, the serpent skull
and ribs, withered palms, dragon rocks; the black flag on its pole is object 15029, built in `WorldProps`; Skull
Rock's sculpted face is objects 15032-15079, built in `SkullRock`) are written into `data/cache` by
`CursedIslandGenerator`. To change the layout, edit that class, then rebuild from the
original cache so old map data doesn't pile up in `main_file_cache.dat`:

```bash
cd "2006Scape Server"
mvn -q -f ../pom.xml -pl "2006Scape Server" package -DskipTests
git checkout 7f1dc47 -- data/cache   # the cache as it was before Skullbrine Isle
java -Dpreview=island.png -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.CursedIslandGenerator
```

`-Dpreview=island.png` also writes a top-down picture of the island, so you can check a layout
change before logging in.

Clients download the changed files from the server automatically, because the CRCs change.
That only happens if the client runs **without** `-local`/`-offline`/`-no-crc`, which turn
off the CRC check (`-s localhost` is fine).

Handy tools in the same package:

- `MapDump <regionId> [objectId|planes]` prints the objects or upper planes of any region.
  Use it to find object ids or to copy existing scenery.
- `MapRender x0 y0 width height out.png [scale] [labels]` draws a top-down map of any part of
  the world, with a coordinate grid. With `labels`, objects are tagged with their ids.
- `DefDump loc|npc|obj` lists every object, NPC or item definition (id, name, size, models).
- `WorldLint x0 y0 x1 y1 [-from x,y] [-png out.png]` checks an area for slap-dash world-building: dead-end and
  pinched paths, ruler-straight roads and shores, stray walls and doors, unbacked cave mouths, scenery in roads or
  water, props on slopes, overlaps, and places you can't reach. See the world-building skill
  (`.claude/skills/world-building/SKILL.md`).
- Running the generator with `-Dgallery=id1,id2,...` lines up candidate objects near the camp,
  so you can compare them in game.
- `stamp(...)` in the generator copies scenery from an existing region. That's how the Port
  Sarim ship became the wreck. `stampBuilding(...)` copies a whole building (floor, walls,
  roof, furniture without options). That's how the Mos Le'Harmless shacks got there.

## Known quirks

- The quest uses the unused "Creature of Fenkenstrain" slot in the quest list, so hovering it
  still shows that name as the tooltip. The tooltip text lives in the client's interface
  cache, not on the server.
- NPC option slot 2 (index 1) sends the *attack* packet in this client. Shop-style options
  go in slot 3, as `Talk-to, -, Trade`.
