# The Void quest series

The series continues from **Curse of Skullbrine**. The portal under Skull Rock wakes up. The player
gathers the three splinters of its broken seal, opens the door, and meets **the Hollow King**, a
boss who can't be beaten yet. He shatters the platform under them, and they fall to **Nythera**,
a new continent. In **Ashes of the Crown**, the player learns who the King was, steals his heart from the Vault
beneath the Gate, and finds out he is coming to take it back ("To be continued...").

| Quest | QP | Requirements | Stage field |
|---|---|---|---|
| Echoes of the Void | 2 | Curse of Skullbrine; a spade and a pickaxe; able to fight level 10-30 undead | `Player.voidEchoes` (0-3) |
| Beyond the Veil | 3 | Echoes of the Void | `Player.voidVeil` (0-4) |
| Ashes of the Crown | 3 | Beyond the Veil; able to face a level-88 boss | `Player.voidCrown` (0-7), `crownFlags` |

All stage fields are saved in the character file. In the quest tab they take the slots of
Desert Treasure, Devious Minds and Enakhra's Lament, which this server never implemented.

## Echoes of the Void: walkthrough

1. **The ghost.** Talk to the Ghost of Captain Blackbrine in the portal chamber. The door has
   started to "sing your name", and a seer has come to the island.
2. **Seer Ilsabet** is camped by the jaw of Skull Rock, east of the plaza. She explains the seal
   and gives three riddles, one for each splinter. Ask "Where are the splinters?" to hear them again.
3. **"One weeps in the eye of the skull that cries."** Climb the skull's face. A green tear trail
   runs down from the *right-hand* (east) eye. *Reach-into* the ghostfire at its lower lip to get
   the **Eye splinter**. The other eye's fire just burns you (a few damage, nothing more).
4. **"One sleeps in the grave of the man who saw the far shore."** In the graveyard, six graves
   can be *Read*. Jory Vane's says he saw "a far shore under violet stars". *Dig* it with a spade.
   A **Hollow wraith** (combat 30) climbs out, and killing it drops the **Grave splinter**. Digging
   the wrong grave pulls out a Drowned deckhand instead.
5. **"One screams in the rock where the dragon-metal grows."** In the Dragonvein cavern, *Mine* the
   violet **Void-veined rock** (needs a pickaxe). It shrieks, and four **Voidlings** crawl out one
   after another, each a little tougher. When the last one dies the vein goes quiet. Mine it again
   for the **Vein splinter**.
6. Bring all three to Ilsabet. She binds them into the **Void sigil**.

**Rewards:** 2 QP, the Void sigil, the **Seer's lantern** (its *Gaze* option teleports you to
Skull Rock), 3,000 Magic XP, 2,500 Mining XP and 2,500 Prayer XP.

## Beyond the Veil: walkthrough

1. Talk to Ilsabet again. She has seen a king made of nothing beyond the door.
2. **Use the sigil on the portal.** It tears open and you choose to step through.
3. **The void.** You arrive on a broken black platform hanging over nothing. The Hollow King
   (combat 999) rises from his throne, speaks, then attacks:
   - every few seconds a wave of void hits you, but it never kills;
   - your hits pass through him like smoke, and he heals instantly;
   - everything he says also goes to the chatbox.
   After about half a minute, or once you are down to 40% HP (or 5 HP, for very weak characters),
   he decides you're more fun alive. The Seer's lantern doesn't work in the void.
   He closes his hand, the platform shatters, and you fall.
4. **Starfall Crater, Nythera.** You land in a smoking crater, and a violet **Rift** is left open
   in its heart. **Oren the Wanderer** is camped on the north-west rim. Talk to him to finish.

**Rewards:** 3 QP, the **Hollow-touched cape** (+4 attack, +6 defence in every style, +3
strength, +3 prayer), the **Hollowfang scimitar** (needs 50 Attack: +9 stab, +58 slash, +57
strength), 6,000 Attack, Strength and Defence XP, 4,000 Hitpoints XP, and access to Nythera.

### Getting back and forth

- **Rift** in Starfall Crater: takes you to the portal under Skull Rock.
- **Portal** under Skull Rock: once you've fallen through, it drops you straight into Starfall Crater.
- **Seer's lantern**: after Beyond the Veil it offers Skull Rock or Starfall Crater.
- If a player logs out in the middle of the void scene, they are put back beside the portal when they log in.

## Nythera

Nythera is a 7×7 region continent (448×448 tiles) at regions (20-26, 39-45), centred on
(1504, 2720). It is built procedurally by `VoidRealmGenerator.nythera()` from value noise. It has:

- a ragged coastline;
- a ridge of snow-capped mountains with a single pass;
- a lake;
- a winding river (Catmull-Rom curves with noise wobble);
- roads that follow the land: stone from the crater to the **Broken Gate** ruins, then dirt
  north to the pass and east;
- the **Hollow Scar** in the east, a dying, blackened land;
- forests, ore clusters (copper through mithril), ruins and a standing-stone circle.

The inland settlement Oren hints at ("There are people inland who'll want to meet you") is left
for the next quests. Until then the countryside already has people and wildlife in it.

### Life on Nythera

| Place | Where | Who and what |
|---|---|---|
| **Netwright's Landing** | lake's east shore (1459, 2712) | **Bram Netwright** fishes off a jetty. A shack, rowboats, nets, a spit-roast. He sells tackle and buys fish (shop 350). Net/bait spots in the lake, lure/bait spots along the river. |
| **Tollan's Croft** | hills between the crater and the Broken Gate (1522, 2662) | **Marta Tollan**'s farmhouse. Flax, wheat and cabbages you can pick, a well, hay, a scarecrow, sheep (shearable), cows and chickens. She sells bread, eggs, milk, wool, shears and the like (shop 351). |
| **Hob's camp** | edge of the western forest (1446, 2652) | **Hob Ashby** the woodcutter: tents, a cookfire, stumps and stacked timber. |
| **The Scar Watch** | west edge of the Hollow Scar (1574, 2741) | **Warden Kael**. She pays a bounty for every 10 **Hollowed wolves** (combat 34) and **Hollow husks** (combat 41) you kill in the Scar: 750 coins and 1,500 Slayer XP, repeatable. The tally (`Player.hollowKills`) is saved. |
| The roads | | **Wandering pilgrims** rest along the way. A signpost stands at the Broken Gate. |

### Places to visit

| Place | Where | What's there |
|---|---|---|
| **Gatehold** | west of the Broken Gate (1497, 2713) | The outpost. A watch-house and store-house, an open-air **bank** (three booths and a banker), a **furnace and anvil**, a range, a well, a market stall and guards. **Captain Ysolde** points you at the three dungeons and pays for each boss you kill. **Pell the Trader** runs a general store (shop 352). **Dorrin Anvilhand** sells bars, hammers and mithril/adamant armour (shop 353). |
| **Tess's quarry** | foothills north-east of the Gate (1532, 2765) | A sunken pit with iron, coal, silver, gold and mithril. **Tess Quarryhand** sells pickaxes up to adamant (shop 354). |
| **The Last Hearth** | where the north road comes off the pass (1483, 2808) | An inn and a stable-house, with tables out front and a fire. **Morra** sells food and drink (shop 355) and passes on rumours about the dungeons. |
| **Smugglers' Cove** | south coast (1470, 2619) | A smugglers' ship run aground, tents, contraband, pirates and bandits. **Sly Nettle** fences rope, gems, runes and the odd bit of rune (shop 356). Lobster/swordfish (cage, harpoon) and shark (net, harpoon) spots in the shallows. |
| **Silkwood** | west of the river (1390, 2668) | Four **magic trees** and two yews in a hushed clearing guarded by giant spiders. A plank **bridge** crosses the river from Hob's camp (y 2653). |

### Dungeons

Each dungeon is a separate 64x64 region (y 9664+). Click the entrance to go in. The rock face
beside where you arrive takes you back out.

| Dungeon | Entrance | Inside | Boss |
|---|---|---|---|
| **The Sunken Barrow** | a stone door in a grassy mound west of the road, ringed by graves (1478, 2739) | Torch-lit processional hall, east and west crypts, an ossuary. Skeletons (combat 21-45) and ghosts. | **The Barrow Thane** (68, 110 HP). Below half health he raises two skeletons. |
| **Hollowdeep** | a fissure in the Hollow Scar's black heart (1601, 2742) | Black glass and violet crystal: the husk pit, the kennel (Hollowed wolves), a crystal grotto (voidlings). | **The Hollow Matriarch** (92, 150 HP). She drains your Prayer, and below half health she calls three voidlings. |
| **Frostfang Caves** | a cave on a ledge in the north pass's east wall (1489, 2789) | Ice halls with white wolves, ice warriors and ice giants. An **ore cavern** with mithril, adamantite and coal, and runite in the cavern and Rimeclaw's lair. | **Rimeclaw** (110, 180 HP). Its frost breath freezes and hits for 4-12. |

**Boss loot:** big bones and a pile of coins every time, plus one roll on a table of rune gear,
runes, potions, gems, sharks and runite. Each boss has a **1 in 12 unique**:

| Unique | From | Stats |
|---|---|---|
| **Barrowsteel longsword** (7969) | Barrow Thane | +44 stab, +54 slash, +56 strength. Needs 60 Attack. |
| **Hollowheart amulet** (7968) | Hollow Matriarch | +6 to every attack style, +3 to every defence, +12 strength, +2 prayer |
| **Frostbound boots** (7970) | Rimeclaw | +6 stab/slash/crush/range defence, +3 magic defence, +2 strength, +1 prayer. Needs 40 Defence. |

**Captain Ysolde's bounty:** the first time you kill each boss, report back to Ysolde for a
one-off reward. The Thane pays 2,500 coins and 4,000 Slayer XP. The Matriarch pays 4,000 coins
and 7,000 XP. Rimeclaw pays 6,000 coins and 10,000 XP. Clearing all three adds a 10,000-coin
purse. Progress is saved in `Player.nytheraBosses`: bits 0-2 for slain, bits 4-6 for reported.

**Wildlife:** rabbits and butterflies in the meadows, squirrels and the odd unicorn in the woods,
grizzly bears in the foothills, white wolves on the ridge, seagulls on the beaches, ducks and swans
on the lake, and crows on the edge of the Scar.

**Ambience:** people near a player go about their day. Bram casts his line, Hob swings his axe and
Marta stoops over her crops. Now and then they say something overhead. So do Oren, Ilsabet,
Kael, the pilgrims and the Hollowed. This all runs from a single loop in
`VoidRealm.startAmbience` (the `CHATTER` and `WORK` tables), and only near players. Oren's later
dialogue points newcomers towards the camps.

Hollowed drops: wolves drop wolf bones and coins, with a rare uncut diamond. Husks drop bones,
coins, grimy ranarr, uncut sapphires and emeralds, and very rarely a dragon dagger.

**Regenerating:** the camps' scenery is built by `VoidRealmGenerator.camps()`. The places and
dungeons are built by `places()` and `dungeons()`. Dungeon monsters go into the same spawn list.
`wildlife()` chooses the spawn tiles (meadow, wood, shore, Scar...) and writes them out when
`-Dnythera.spawns=path` is set. `tools/nythera_data.py path` then writes the people, creatures,
drops, shops and spawns into `data/cfg`.

Regenerate a top-down preview with
`-Dnythera=/path/nythera.png` when running `CursedIslandGenerator`.

## Ashes of the Crown: walkthrough

Part four, and the first quest on Nythera itself. Requires Beyond the Veil. 3 quest points.

1. **Captain Ysolde** (Gatehold) says Kael's watch saw something tall and crowned walk out of the Scar and stare at
   Gatehold for an hour. She sends you to **Archivist Maelis**, camped among the standing stones north of the Gate.
2. Maelis tells you about Nythera's last king, Aurel, and the Royal Chronicle that was torn apart when his city fell.
   He needs its three pages:
   - **The Thane's page**: in the coffer at the back of the Sunken Barrow. It only opens once the Barrow Thane
     has been put down.
   - **The Silkwood page**: in a silk cocoon in Silkwood. Cutting it brings down the **Silkwood broodmother**
     (level 58). Kill her, then cut the cocoon again.
   - **Nettle's page**: Sly Nettle in Smugglers' Cove sells it for 3,000 coins, or for a raw shark (he'd rather have
     the shark).
3. Maelis binds the pages into the **Royal chronicle**, which you can "Read" at any time. It reveals the twist: the
   Hollow King *is* Aurel. His champion cut out his heart, **the Ember**, and sealed it in the Vault beneath the Gate.
   The riddle: *"First the SUN that crowned him; then the STAR that guided him; last the MOON that mourned him."*
4. Climb down the **rusted hatch** in the Gate's rubble. In the hall of wards, light the braziers **Sun, Star, Moon**.
   - A wrong order puts every flame out and sends two **Hollow shades** at you.
   - The right order cracks the void-glass seal at the north end.
5. Through the seal, the **Hollow Herald** (level 88, 220 hp) is waiting for you. At two-thirds and one-third health
   it wraps itself in void and calls two shades. It can't be hurt until they're dead; if they somehow can't be
   reached, the shroud gives out on its own after about thirty seconds. Every few seconds a lash of void hits you
   whatever you're doing. Bring food and armour.
6. Take the **Ember** from the pedestal and show it to Maelis. The Hollow King speaks through it: *"Keep it warm for
   me. I am coming to collect it."*

**Rewards:**
- 3 quest points
- the **Signet of Gatehold** ("Rub" to return to Gatehold's waystone)
- 7,500 Slayer XP, plus 5,000 each in Magic and Prayer
- the **waystone network**, described below

### The waystones

Five standing stones, each carved with a violet rune:
- Gatehold
- Starfall Crater
- the Last Hearth
- Smugglers' Cove
- the Scar Watch

After the quest, touching a stone **attunes** you to it; Gatehold's is attuned automatically. Touching any stone
offers travel to every other stone you've attuned. Before the quest, the runes only flicker.

### Terrain smoothing

All map generators share `CursedIslandGenerator.smoothGrid`. It softens overlay edges the way Jagex's maps do:
- lone one-tile speckles are removed
- outside corners and dead ends become diagonal halves (shape 1)
- inside corners get a small corner triangle (shape 7)
- stamped buildings are left alone

Shapes are 0-11 (the stored value; the client adds one). 12 would overflow the map opcode.

Dungeon floors keep their colour variation in the **underlay**, which the client blends, and use overlays only for
features. Their walls rise over three tiles from low-frequency noise instead of one-tile spikes. The dungeon row
(x 1280-1727, y 9664-9727) is multi-combat, so a boss's summoned helpers can be fought.

## Where things live

| What | Where |
|---|---|
| Ids and coordinates (NPCs 3858-3878, items 7961-7970, objects 14986-14994) | `game/content/voidrealm/VoidConstants.java` |
| Dialogue, objects, the boss scene, lantern, drops | `game/content/voidrealm/VoidRealm.java` |
| Gatehold, the inn, the cove; dungeon entrances; boss mechanics and Ysolde's bounty | `game/content/voidrealm/NytheraPlaces.java` |
| Quest journals and rewards | `game/content/quests/impl/EchoesOfTheVoid.java`, `BeyondTheVeil.java`, `AshesOfTheCrown.java` |
| Ashes of the Crown: dialogue, the Vault, the Herald, the waystones | `game/content/voidrealm/CrownQuest.java` |
| NPC / item / object definitions, island scenery, the void arena, Nythera | `tools/mapgen/VoidRealmGenerator.java` |
| Item stats | `data/cfg/ItemDefinitions.json` (7966-7970); slots in `ItemData`, level in `ItemAssistant.getRequirements` |
| NPC stats and spawns | `data/cfg/npcDefinitions.json`, `npc.json`, `spawns.json` |

Dialogue ids 9600-9999 belong to the series (9800+ are `NytheraPlaces`, 9900+ are `CrownQuest`). `VoidRealm.wrap` reflows every NPC and player line
to fit beside a chathead. Ilsabet and Oren borrow chatheads (from the Varrock witch and
Kaqemeex) that only have the talking animations, so their other emotes fall back to talking.

## Admin commands

| Command | Does |
|---|---|
| `::voidecho n` / `::voidveil n` | set a quest stage (no argument prints it) |
| `::nythera` | teleport to Starfall Crater |
| `::vt npc id` / `npc2 id` / `obj id x y` / `obj2 id x y` / `use objId x y itemId` / `item id` | fire the same handler a click would (testing) |
| `::vt journal 1/2/4` | open a journal (4 = Ashes of the Crown) |
| `::vt crown stage [flags] [veil]` | set Ashes of the Crown's stage, flags (bit 0 broodmother, bits 8+ waystones) and Beyond the Veil's stage |
| `::vt list` | list NPCs spawned for you |

## Ideas for what comes next

- The people inland Oren mentions: a settlement past the Broken Gate.
- The Hollow Scar spreading: the Hollowed already prowl it, and Kael is watching.
- The King is coming for the Ember. Whoever holds it can be found, and maybe it can be used against him.
- A way to actually wound the Hollow King: the cape's "mark" and Hollowfang's "hunger" are
  hooks for that rematch.
