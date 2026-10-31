# Skullbrine Isle

A cursed pirate island south-west of the mainland, the dungeon beneath it, and the quest
**Curse of Skullbrine** (2 QP). The dungeon holds a dormant portal to another world, which
is not connected to anything yet.

## Getting there

- **Bosun Grimsby** on the Port Sarim docks starts the quest and sails you to the island.
  He waits on the island jetty to take you back.
- Admins can use `::island` to go to the jetty, and `::skullbrine <stage>` to set the quest stage.

## Quest walkthrough (stages in `Player.skullbrine`)

| Stage | What happens |
|------:|--------------|
| 0 → 1 | Talk to Bosun Grimsby in Port Sarim and agree to help. |
| 1 → 2 | Sail to the island and talk to **Mad Pete** at the pirate camp. |
| 2 → 3 | Search the captain's skeleton next to the beached *Drowned Maiden* (south beach) for **Blackbrine's log**, then read it. |
| 3 → 4 | Enter the cave under the skull hill. Kill the **Cursed first mate** (lvl 62) in the west grotto for the **Captain's key**, then open the chest in the plunder cavern to get the **Void pearl**. |
| 4 → 5 | Use the pearl on the portal in the northern chamber, or just click the portal. |
| 5 → 6 | Talk to the **Ghost of Captain Blackbrine** to finish. Rewards: 2 QP, 2,500 Attack XP, 2,500 Strength XP, 3,000 coins and a pirate's hat. |

## Where things live

| What | Where |
|------|-------|
| Ids and coordinates shared by the generator and the content | `src/main/java/com/rs2/game/content/skullbrine/SkullbrineConstants.java` |
| Dialogues, travel, objects, drops | `src/main/java/com/rs2/game/content/skullbrine/SkullbrineIsle.java` |
| Quest journal, log text, rewards | `src/main/java/com/rs2/game/content/quests/impl/CurseOfSkullbrine.java` |
| NPC stats, spawns, drops, shop | `data/cfg/npcDefinitions.json`, `npc.json`, `spawns.json`, `npcdrops.json`, `shops.json` (shop 349) |
| Map + cache generator | `src/main/java/com/rs2/tools/mapgen/` |

To hook up the portal later, edit `SkullbrineIsle.enterVoidPortal(Player)`. It is the only
place the portal leads anywhere.

## Regenerating the map

The island, dungeon, new NPCs (ids 3852-3857) and new items (7956-7958) are written into
`data/cache` by `CursedIslandGenerator`. To change the layout, edit that class, then rebuild
from a clean cache so the old map data doesn't pile up in `main_file_cache.dat`:

```bash
cd "2006Scape Server"
mvn -q -f ../pom.xml -pl "2006Scape Server" package -DskipTests
git checkout data/cache            # start from the original cache
java -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.CursedIslandGenerator
```

Clients download the changed files from the server automatically, because the CRCs change.
That only happens if the client runs **without** `-local`/`-offline`/`-no-crc`, which turn
off the CRC check (`-s localhost` is fine).

Handy tools in the same package:

- `MapDump <regionId> [objectId|planes]` prints the objects or upper planes of any region.
  Use it to find object ids or to copy existing scenery.
- Running the generator with `-Dgallery=id1,id2,...` lines up candidate objects near the camp,
  so you can compare them in game.
- `stamp(...)` in the generator copies scenery from an existing region. That's how the Port
  Sarim ship became the wreck.

## Known quirks

- The quest uses the unused "Creature of Fenkenstrain" slot in the quest list, so hovering it
  still shows that name as the tooltip. The tooltip text lives in the client's interface
  cache, not on the server.
- NPC option slot 2 (index 1) sends the *attack* packet in this client. Shop-style options
  go in slot 3, as `Talk-to, -, Trade`.
