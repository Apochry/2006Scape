# The Void quest series

The series continues from **Curse of Skullbrine**. The portal under Skull Rock wakes up. The player
gathers the three splinters of its broken seal, opens the door, and meets **the Hollow King**, a
boss who can't be beaten yet. He shatters the platform under them, and they fall to **Nythera**,
a new continent. There, Gatehold's watch only trusts them once they've helped **Hold the Gate** against a Hollowed
raid, and Kael saw something crowned watching it. In **Ashes of the Crown** the player learns who the King was and
steals his heart, the Ember, from the Vault beneath the Gate. **Heartforged** turns the Ember into the one blade
that can cut him. **The Hollow King** takes the fight back to his platform in the void, where the crown finally lets
Aurel go. Beyond it waits an optional mega boss, **the Starless**: the thing that crowned him.

| Quest | QP | Requirements | Stage field |
|---|---|---|---|
| Echoes of the Void | 2 | Curse of Skullbrine; a spade and a pickaxe; able to fight level 10-30 undead | `Player.voidEchoes` (0-3) |
| Beyond the Veil | 3 | Echoes of the Void | `Player.voidVeil` (0-4) |
| Hold the Gate | 2 | Beyond the Veil; recommended combat 30+ | `Player.voidGate` (0-7), `gateKills`, `gateFlags` |
| Ashes of the Crown | 3 | Hold the Gate; recommended combat 40+ | `Player.voidCrown` (0-7), `crownFlags` |
| Heartforged | 3 | Ashes of the Crown; 30 Mining; recommended combat 50+ | `Player.voidForge` (0-5), `forgeFlags` |
| The Hollow King | 4 | Heartforged; recommended combat 60+, the Emberbrand | `Player.voidKing` (0-5) |
| *The Starless* (optional boss) | - | The Hollow King; recommended combat 90+ | `Player.starlessKills` |

### The levelling ladder

Each quest is pitched a little above the last, and its combat XP reward lifts you towards the next:

| Step | Fights | Reward XP |
|---|---|---|
| Beyond the Veil → Nythera | land at about combat 30 | - |
| Hold the Gate | Hollowed wolves and husks (30-42 hp); the raid's **Hollow ravager** (80 hp, max 6) | 7k Att/Str/Def, 3k HP |
| Ashes of the Crown | **Thane's oathsworn** (55 hp), **broodmother** (70 hp), **Hollow Herald** (160 hp, max 8) | 8k Att/Str/Def + Slayer, Magic, Prayer |
| Heartforged | **Rimebound sentinel** (110 hp), **Sir Vaelric** (170 hp, max 9) and two hollow knights | 10k Att/Str/Def, Smithing, Mining |
| The Hollow King | **the Hollow King** (300 hp, max 8, plus void waves and falling sky) | 15k Att/Str/Def/HP, 8k Prayer |
| The Starless | 900 hp, max 16, starfall, eclipse, spawn | Starless cape / Eclipse amulet (1/12 each) |

Between quests Nythera's three dungeon bosses (combat 68, 92 and 110) and the Hollowed on the Scar fill the gaps.

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
| **Netwright's Landing** | lake's east shore (1459, 2712) | **Bram Netwright** fishes off a jetty. A shack, rowboats, nets drying, a spit-roast. He sells tackle and buys fish (shop 350). Net/bait spots in the lake, lure/bait spots along the river. |
| **Tollan's Croft** | hills between the crater and the Broken Gate (1522, 2662) | **Marta Tollan**'s farmhouse. Flax, wheat and cabbages you can pick in fenced fields, a well, hay, a scarecrow, a sheep pen (shearable), a cow pasture and chickens loose in the yard. She sells bread, eggs, milk, wool, shears and the like (shop 351). |
| **Hob's camp** | edge of the western forest (1446, 2652) | **Hob Ashby** the woodcutter: two tents facing a cookfire with logs to sit on, a woodpile and chopping block, a half-loaded cart, and stumps on the forest side. |
| **The Scar Watch** | west edge of the Hollow Scar (1574, 2741) | **Warden Kael**. She pays a bounty for every 10 **Hollowed wolves** (combat 34) and **Hollow husks** (combat 41) you kill in the Scar: 750 coins and 1,500 Slayer XP, repeatable. The tally (`Player.hollowKills`) is saved. |
| The roads | | **Wandering pilgrims** rest along the way. A signpost stands where the roads part at the Broken Gate. |

### Places to visit

| Place | Where | What's there |
|---|---|---|
| **Gatehold** | west of the Broken Gate (1497, 2713) | The outpost. A watch-house and store-house, an open-air **bank** (three booths and a banker), a **furnace and anvil**, a range, a well, a market stall and guards. Two banners in the Wardens' night-blue and star flank the bank. **Captain Ysolde** points you at the three dungeons and pays for each boss you kill. **Pell the Trader** runs a general store (shop 352). **Dorrin Anvilhand** sells bars, hammers and mithril/adamant armour (shop 353). |
| **Tess's quarry** | foothills north-east of the Gate (1532, 2765) | A sunken pit, its ore (iron, coal, silver, gold and mithril) worked out of the walls; Tess camps on the rim. **Tess Quarryhand** sells pickaxes up to adamant (shop 354). |
| **The Last Hearth** | where the north road comes off the pass (1483, 2808) | An inn and a stable-house, with tables out front and a fire. **Morra** sells food and drink (shop 355) and passes on rumours about the dungeons. |
| **Smugglers' Cove** | south coast (1470, 2619) | A smugglers' ship run aground, tents, contraband, pirates and bandits. **Sly Nettle** fences rope, gems, runes and the odd bit of rune (shop 356). Lobster/swordfish (cage, harpoon) and shark (net, harpoon) spots in the shallows. |
| **Silkwood** | west of the river (1390, 2668) | Four **magic trees** and two yews in a hushed clearing, webs strung between them, guarded by giant spiders. A plank **bridge** crosses the river from Hob's camp (y 2653). |

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

### Loot

Every ordinary monster on Nythera and in its dungeons (combat 15+, the stock wildlife included, but only while it's
on Nythera) drops by one rule, in `VoidLoot.java`. It always drops its own bones (or ashes, or a unicorn's horn),
and then rolls once:

| Roll | Drop | Under 30 | 30-49 | 50+ |
|---:|---|---|---|---|
| 40% | just the bones | | | |
| 30% | something ordinary | coins, logs, iron arrows, raw trout, flax | coins, oak/willow logs, steel arrows, raw salmon, iron ore | coins, maple logs, mithril arrows, raw lobster, coal |
| 20% | something useful | nature/chaos runes, low herbs, trout, attack/strength potions | nature/law/cosmic runes, harralander-irit, lobster, mithril ore, prayer/super attack | death/law/blood runes, ranarr-kwuarm, swordfish, adamantite, prayer/super strength |
| 5% | something valuable | uncut sapphire/emerald, a purse, a gold bar | uncut emerald/ruby, a purse, **starmetal** | uncut ruby/diamond, a fat purse, starmetal, runite ore |
| 5% | gear around its level | steel pieces, oak shortbow | mithril/adamant pieces, maple shortbow, **Warden's helm, boots, gauntlets** | adamant pieces, rune med helm/dagger, yew shortbow, **Warden's body, legs** |
| +1% | a separate bonus roll: unusually good gear | mithril scimitar/full helm, adamant sq shield | rune scimitar/med helm/chainbody, Warden's platebody | rune legs/full helm, dragon dagger/med helm |

Bosses and minibosses keep their own tables, and the adds summoned in boss fights (Hollow knights, Starless spawn)
drop only bones. `::vt loot rolls npcId` tallies the tiers over many rolls and drops ten for a look.

**Regenerating:** the camps' scenery is built by `VoidRealmGenerator.camps()`. The places and
dungeons are built by `places()` and `dungeons()`. Dungeon monsters go into the same spawn list.
`wildlife()` chooses the spawn tiles (meadow, wood, shore, Scar...) and writes them out when
`-Dnythera.spawns=path` is set. `tools/nythera_data.py path` then writes the people, creatures,
drops, shops and spawns into `data/cfg`.

Regenerate a top-down preview with
`-Dnythera=/path/nythera.png` when running `CursedIslandGenerator`.

## Hold the Gate: walkthrough

Part four, and the first quest on Nythera. Requires Beyond the Veil. 2 quest points.

1. **Captain Ysolde** won't trust someone who fell out of the sky until they've stood a watch. The Hollowed have been
   coming out of the Scar every night, and last night they smashed the barricades on the Scar road.
2. **Warden Kael** (the Scar Watch) sends you to put down **6 Hollowed** (wolves or husks) on the black ground. Every
   one of them dies crawling towards the Scar: something is calling them.
3. **Dorrin** forges spikes from **8 iron ore** (Tess's quarry, east face) and gives you three bundles.
4. **Repair** the three broken barricades just east of the Gate. Each one you fix becomes a spiked barricade (shown
   to you only).
5. Tell Ysolde you're ready and she rings the bell. Hold the barricades through three waves:
   - three wolves
   - three husks
   - a **Hollow ravager** with two more wolves

   Raiders that reach the spikes lose a fifth of their health. Stray more than 24 tiles from the barricades, or
   die, and the raid melts away. Ask Ysolde to try again. Gatehold is multi-combat.
6. Report back. Kael admits what she saw on the ridge as the raid broke: something tall and crowned, watching you.

**Rewards:** 2 quest points, the **Warden's kiteshield** (Defence 30), 7,000 Attack/Strength/Defence XP and 3,000
Hitpoints XP.

## Ashes of the Crown: walkthrough

Part five. Requires Hold the Gate. 3 quest points.

1. **Captain Ysolde** says the crowned thing has been back twice, and stares at Gatehold for an hour each time. She
   sends you to **Archivist Maelis**, camped among the standing stones north of the Gate.
2. Maelis tells you about Nythera's last king, Aurel, and the Royal Chronicle that was torn apart when his city fell.
   He needs its three pages:
   - **The Thane's page**: in the coffer at the back of the Sunken Barrow. If the Barrow Thane is still alive,
     touching it raises **the Thane's oathsworn** (level 40). Kill it, then open the coffer. If you've already
     killed the Thane, it just opens.
   - **The Silkwood page**: in a silk cocoon in Silkwood. Cutting it brings down the **Silkwood broodmother**
     (level 44). Kill her, then cut the cocoon again.
   - **Nettle's page**: Sly Nettle in Smugglers' Cove sells it for 3,000 coins, or for a raw shark (he'd rather have
     the shark).
3. Maelis binds the pages into the **Royal chronicle**, which you can "Read" at any time. It reveals the twist: the
   Hollow King *is* Aurel. His champion cut out his heart, **the Ember**, and sealed it in the Vault beneath the Gate.
   The riddle: *"First the SUN that crowned him; then the STAR that guided him; last the MOON that mourned him."*
4. Climb down the **rusted hatch** in the Gate's rubble. In the hall of wards, light the braziers **Sun, Star, Moon**.
   - A wrong order puts every flame out and sends two **Hollow shades** at you.
   - The right order cracks the void-glass seal at the north end.
5. Through the seal, the **Hollow Herald** (level 62, 160 hp) is waiting for you. At two-thirds and one-third health
   it wraps itself in void and calls two shades. It can't be hurt until they're dead; if they somehow can't be
   reached, the shroud gives out on its own after about thirty seconds. Every few seconds a lash of void hits you
   whatever you're doing. Bring food and armour.
6. Take the **Ember** from the pedestal and show it to Maelis. The Hollow King speaks through it: *"Keep it warm for
   me. I am coming to collect it."*

**Rewards:**
- 3 quest points
- the **Signet of Gatehold** ("Rub" to return to Gatehold's waystone)
- 8,000 Attack, Strength and Defence XP, 7,500 Slayer XP, plus 5,000 each in Magic and Prayer
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

## Heartforged: walkthrough

Part six. Requires Ashes of the Crown and 30 Mining. 3 quest points.

1. **Maelis** has worked out why nothing hurts the Hollow King: void closes over every wound, except one made by his
   own heart. A blade forged around the Ember would cut him. She sends you (and the Ember) to **Dorrin**.
2. Dorrin needs **starmetal** and a **Rimeheart**:
   - **Starmetal**: "Mine" the **fallen star** in Starfall Crater (a pickaxe and 30 Mining). The first swing cracks it
     and two voidlings boil out. Kill them, then mine it again.
   - **Rimeheart**: deep in the Frostfang Caves, a new tunnel off the frozen hall leads to the **rime shrine**.
     Reaching for the altar wakes the **Rimebound sentinel** (level 58, 110 hp). Shatter it and take the heart.
3. Give Dorrin all three. When the Ember goes into the furnace, every fire in Gatehold gutters, and **Sir Vaelric**,
   the King's last knight, steps out of the air:
   - 170 hp, max hit 9
   - every 20 ticks he raises a void shield for 5 ticks, and your blows slide off
   - at half health two **hollow knights** join him

   If you leave, he goes back into the dark. Ask Dorrin to try again.
4. Vaelric's dying words: the King is tired of waiting, and will open the Scar himself. Dorrin finishes the blade.

**Rewards:** 3 quest points, the **Emberbrand** longsword (Attack 50; slash +50, strength +52, and the only weapon
that can wound the Hollow King), 10,000 Attack/Strength/Defence XP, 5,000 Smithing XP and 4,000 Mining XP. Lost it?
Dorrin found it stuck in his anvil.

## The Hollow King: walkthrough

Part seven, the finale. Requires Heartforged. 4 quest points.

1. **Kael**: the Scar is splitting wider every hour; the King is cutting a road. Rather than wait for him, take the
   fight to him.
2. **Maelis**: the rift in Starfall Crater still leads to his platform. It only spat you out because you carried
   nothing it recognised. Now you carry his heart in a sword. Someone has to tune the rift, though.
3. **Oren** hums it into tune. The rift now offers two destinations: Skull Rock, or **the void platform** (only
   while you're wielding the Emberbrand).
4. The battle:
   - The King has 300 hp, max hit 8.
   - Only the Emberbrand's wounds stay open.
   - A wave of void every 15 ticks (3-6 damage).
   - **Falling sky** every 13 ticks: three tiles are marked, one of them yours, and a shard hits 10-14 three ticks
     later.
   - At half health, two hollow knights.
   - The platform is multi-combat.
5. At 15% the crown cracks. The darkness pours out of it, and **Aurel** is left standing there. He tells you the
   crown was never his: it belonged to *the thing that lives in the dark between the stars*. Its gate is on this
   platform.
6. Tell **Ysolde**. Nythera's last king is at rest.

**Rewards:** 4 quest points, the **Crown of Aurel** (Defence 45), 15,000 Attack/Strength/Defence/Hitpoints XP, 8,000
Prayer XP. Afterwards the platform has a small tear back to the crater, and the **Starless Gate** stands open.

## The Starless (optional mega boss)

Through the Starless Gate on the void platform is **the Starless Expanse**, and at its heart, a black sun with one
eye. It's repeatable, and it re-forms about twenty ticks after each kill. Recommended combat 90+, good food and
prayer.

- **Stats:** level 400, 900 hp, max hit 16 (melee).
- **Starfall** every 9 ticks (6 below a quarter health): three tiles flare, one of them yours. Step off before they
  land (14-25).
- **Eclipse** every ~40 ticks: the sky goes black and one of the four star pillars lights. Be within 2 tiles of it
  when the dark closes, or take 30-44.
- **Spawn**: at 70% and 35% it splits off three Starless spawn (60 hp, max 8). While they live it heals 3 hp per
  spawn every other tick.
- Leaving through the gate, logging out or dying resets the fight. Kills are counted (`Player.starlessKills`).
- **Drops:**
  - always: ashes, 8-20k coins and 2-4 starmetal
  - each **Royal guard** piece at 1/36
  - **Starless cape** (Defence 70; all-round +11 defence, +6 strength) at 1/12
  - **Eclipse amulet** (Defence 70; +12 attack, +8 strength) at 1/12
  - otherwise dragon med helms and daggers, a rare dragon square shield, dragonstones, runite bars, death runes,
    sharks

## The saga's gear

Hand-built armour, weapons and effects (drafted in `tools/art-drafts/`, now packed by `VoidRealmGenerator.gearItems`
and `effects()`). They're spread across the story so each tier arrives when the player is ready for it, and only
one of them is a straight quest reward. The rest come from shops, side tasks, recipes and rare drops.

| Gear | Wear | Stats (roughly) | Where from | When |
|---|---|---|---|---|
| **Warden's full helm / platebody / platelegs / boots** | Defence 30 | between adamant and rune, +1 prayer each | **Dorrin's "Warden's armoury"** (talk to Dorrin) | after Hold the Gate |
| **Warden's kiteshield** | Defence 30 | as before, new model | Hold the Gate's reward (also in the armoury) | after Hold the Gate |
| **Warden's gauntlets** | Defence 30 | +4 attack, +5 defence, +3 str | **drop** from Nythera's monsters (the gear roll, combat 30-49; see Loot) | after Hold the Gate |
| **Duskyew shortbow** | Ranged 50 | +72 ranged, magic shortbow speed and arrows | **Kael's side task "Fifty for the Wall"**: kill 50 Hollowed (wolves, husks, ravagers) | after Hold the Gate |
| **Starfall halberd** (two-handed) | Attack 60, Str 30 | +62 stab / +86 slash, +82 str | **Smithing 60 recipe** at Dorrin's: 3 starmetal + 2 rune bars + a hammer | after Heartforged |
| **Rimecleaver battleaxe** | Attack 55 | +60 slash / +55 crush, +74 str | **rare drop (1/12)** from the Rimebound sentinel, which re-forms whenever you touch the rime altar | after Heartforged |
| **Hollow King's mace** | Attack 60 | +58 crush, +52 str, +7 prayer | **Aurel gives it** at the end of The Hollow King (Maelis has it if you finished before this existed, or lose it) | The Hollow King |
| **Staff of the caged star** | Magic 60 | +16 magic attack and defence; **unlimited fire runes** | **Crafting 60 recipe** with Maelis: battlestaff + 2 starmetal + 50 cosmic runes | after The Hollow King |
| **Royal guard full helm / platebody / platelegs / kiteshield / boots / gauntlets** | Defence 50 | a step past rune, +2 prayer, a little strength | **Smithing 70 reforge** at Dorrin's (rune piece + starmetal: helm/boots 1, legs/kite 2, body 3; Warden's gauntlets + 1), or **rare drops from the Starless** (1/36 each) | after The Hollow King |

**Starmetal** after Heartforged: mine the fallen star in Starfall Crater with Mining 40 (a pickaxe, 160 XP a shard;
about one swing in ten at 40, one in five at 99). Sometimes the star cracks and lets out two voidlings. The Starless
drops 2-4 a kill and its spawn sometimes drop one. The Warden's armoury also sells rune pieces and rune bars for the
recipes.

**The Emberbrand's special, Heartfire** (50% energy): 120% damage, 125% accuracy, with an arc of Aurel's heart-fire
along the blade.

**Effects** (new spotanims 666-672 on existing animations; `VoidConstants.GFX_*`):
- the void bolt and impact are the Herald's lash and the King's wave of void
- the starfall marker and falling star are the King's falling sky and the Starless's starfall (the star lands on the
  tick the damage does)
- the eclipse ring marks the Starless's eclipse
- the rime burst is the sentinel's breath (every 10 ticks; it hits and freezes you if you're next to it)
- the Heartfire arc is the Emberbrand's special

The generator checks that `spotanim.dat` still has 666 entries before it appends them. Female characters wear the
male armour models for now; no female meshes were built.

Code: `GearQuest.java` (dialogue 20600+: Dorrin, Kael, Maelis, the star, the sentinel, effect helpers), `VoidGear.java`
(slots, levels, weapon tabs). Flags: `gateFlags` bit 3 = Kael's task taken, bit 4 = the bow handed over, and
`gateKills` counts the fifty. `forgeFlags` bit 8 = the mace handed over.

### Building the world

The sites are composed, not scattered: every camp has a reason for each thing in it, and pieces that never belonged
out in the open are replaced with hand-built ones (`tools/mapgen/WorldProps.java`, objects 15018-15031):

| Piece | Object | Replaces |
|---|---|---|
| **Canvas tent** (2x2): poles, sagging ridge, guy ropes and pegs, the door flaps tied back | 15018 | the stock "tent", which renders as a black slab |
| **Hearth stones** under every campfire, and a **cooking tripod** with a pot where someone cooks | 15019, 15020 | the bare stock fire and the indoor fireplace "cooking pot" |
| **Log seat** by the fires | 15021 | |
| **Gatehold banner**: night-blue with the Wardens' star, on a pole with a crossbar | 15022 | the stock flagpoles at the bank |
| **Standing stones** of the old circle (three variants) | 15023-15025 | stock stones that read as grey blobs |
| **The Broken Gate**: a full-height tower with the arch's springing, a tower broken at half height, the fallen arch half burying the vault hatch, and the curtain wall stepping down away from it | 15026-15028, 15030-15031 | three loose pillars and rubble |
| **Black flag** on a ship's spar in a cairn | 15029 | Skullbrine's flags, which had no pole |

Other rules the generator follows:
- `tent()`, `campfire()`, `seat()` and `fence()` place these pieces. Tents face their fire; seats face the fire.
- `fence()` runs a wall along each side of a rectangle (west and east sides on the inside tiles, north and south on the
  row just outside, so the corners never share a tile) and leaves a one-tile gap to walk in. Fenced areas go into
  `keepClear`, which keeps trees, rocks and wildlife out. The Gate and its approach and the barrow's approach are
  kept clear the same way.
- Loose rocks are the grey mossy rocks 478 (1x1) and 477 (2x2), as on Skullbrine, rather than 5604 (which carries a ring
  of grass round its foot) or the textured boulders 6950/444 (which render almost black).
- The barrow's graves stand in two rows either side of the approach, all facing the door.
- Thin hand-built pieces (flags, flaps) are two-sided. Where the client's painter's sort would paint one face over
  another (a tent's doorway against its end wall), face priorities fix the order (`WorldProps.priority`).
- **A model the client can't draw is silently skipped.** It depth-sorts a model's faces into 1500 buckets across the
  model's bounding sphere, so anything much over ~5 tiles across from its centre (counting its height) isn't drawn at
  all. Skull Rock's face (`SkullRock.java`) is built whole in world coordinates and then cut into 24 pieces, each a
  non-solid, unclickable object (opcodes 17, 19 = 0, 64) with its own footprint and anchor height. Objects
  15032-15079 are reserved for its pieces; the next new object is 15080.
- Roads wander (`meander`) and their width breathes; shores, the lake and the river get a ragged edge
  (`VoidRealmGenerator.ragged`). Free-space checks (`footprintFree`) count every object's whole footprint, not just
  its anchor tile, so rocks don't get planted inside trees. Tents, boats, coffins and graves are bedded into level
  ground. Scenery that smoothing leaves standing in water is cleared.
- **Every world passes the gate.** `tools/regen-world.sh` builds the world and `WorldGate` lints every area declared
  in `WorldAreas` (each with a brief, arrival tiles and must-reach spots) before a byte is written. It looks for:
  - dead-end and pinched paths, and ruler-straight roads and shores
  - stray walls and doors, and cave mouths with nothing behind them
  - scenery in roads or water, and water a player can walk on
  - props on slopes, and overlapping or overfull tiles
  - places and must-reach spots that can't be reached, and anything built outside a declared area
  - amateur design (`DesignLint`): props turned in step, ruled into rows or pasted in two places; walkable cliffs and
    one-tile spikes; floors cut in rectangles; named places no path serves and roads that join nothing; a brief's
    landmark that isn't built or that no path passes; npcs spawned inside things; and options no game code answers

  Any finding refuses the whole world and names the generator line behind it. Findings that are right as they are go
  in `tools/worldlint-accepted.txt` with the reason; a stale or unexplained line fails too. A pass stamps
  `tools/worldgate.stamp`, and the Claude Code hooks in `.claude/` refuse commits of world code without it. The gate
  tests itself: `GENOPTS=-Dworldgate.selftest=true tools/regen-world.sh` plants one of each mistake and fails unless
  every one is caught. After the gate, each changed area gets a review from a fresh reviewer who sees only its brief,
  map and screenshots (`.claude/skills/world-building/design-review.md`). See the world-building skill.
- Floors and objects share no id space. An object id passed as a floor (the dungeons once got the mossy rock, 478, as
  their wall underlay) wraps inside the map format's one-byte opcodes and scrambles the rest of the region;
  `MapRegion.encodeTerrain` now refuses to write it.

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

The floating platforms (the void platform and the Starless Expanse) work the same way. Their floor is underlay only
(the void stone, blended by the client); overlay is used only for the violet rings.
`VoidRealmGenerator.roundRim` rounds the edge: a floor tile with the void on two sides gets an invisible diagonal half,
and a void notch cupped by floor gets an all-but-corner overlay (shape 8, whose rotation is opposite to shape 7's).
The void itself has no underlay and sits level with the platform, so the edge is clean. Do not cover a whole floor in
overlay and then smooth it: every cut shows the underlay beneath, which looked like grey sawteeth.

### The look of the void

Everything the Scar has touched shares one palette, so nothing reads as bolted on:
- **Recolours** pass `ConfigDefinitions.VOID` as the hue to `tint`. That maps each colour through
  `ConfigDefinitions.voidRamp`: deep indigo shadows, muted violet mid-tones, cool lilac highlights, with saturation
  capped at 4. A flat hue-50 recolour at full saturation is the hot magenta this replaced. The hand-built models
  (ModelForge, QuestWeapons, VoidEffects, ArmourForge) use `voidRamp` for their violets too.
- **Floors**: `VoidRealmGenerator.floors()` appends void_pool, void_ash, void_glass, ember_scorch and skull_shadow to
  flo.dat (ids 175-179, **overlays only**: the map format can't store an underlay id above 174). The platforms' stone
  (voidstone 167 and voidstone_dark 168) takes over two stock floors that no map uses, so it can be an underlay and
  blend. Nothing uses the stock crypt magenta (116) or pure black (37) any more: black reads as a hole in the world.
- **Crystals** (`voidGlass`): lilac or black-glass shards on a dark ash disc that sits on grass and stone alike.
- **Skullbrine's dormant portal** is its own object (15017, the rift's twin), so the stock portal elsewhere in the
  world is untouched.
- Monsters built on the stock zombie rig looked broken: it has only five animations, which fling its limbs about. The
  Drowned deckhand (pirate), Hollow husk (man) and Hollow ravager (thug) now use human rigs.

## Where things live

| What | Where |
|---|---|
| Ids and coordinates (NPCs 3858-3878, items 7961-7970, objects 14986-14994) | `game/content/voidrealm/VoidConstants.java` |
| Dialogue, objects, the boss scene, lantern, drops | `game/content/voidrealm/VoidRealm.java` |
| Gatehold, the inn, the cove; dungeon entrances; boss mechanics and Ysolde's bounty | `game/content/voidrealm/NytheraPlaces.java` |
| Quest journals and rewards | `game/content/quests/impl/EchoesOfTheVoid.java`, `BeyondTheVeil.java`, `AshesOfTheCrown.java` |
| Ashes of the Crown: dialogue, the Vault, the Herald, the waystones | `game/content/voidrealm/CrownQuest.java` |
| Hold the Gate (20000+), Heartforged (20200+), The Hollow King (20400+): dialogue and fights | `GateQuest.java`, `ForgeQuest.java`, `KingQuest.java` |
| The Starless | `game/content/voidrealm/StarlessBoss.java` |
| The saga's gear: armoury, recipes, Kael's task, repeatable star and sentinel, effects | `GearQuest.java`, `VoidGear.java`; models in `tools/mapgen/ArmourForge.java`, `QuestWeapons.java`, `VoidEffects.java` |
| Hand-built models (Barrowsteel, Emberbrand, barricades, star, altar, gate, pillars, the Starless) | `tools/mapgen/ModelForge.java` |
| Hand-built scenery (tents, hearths, seats, banners, standing stones, the Broken Gate, the black flag) | `tools/mapgen/WorldProps.java` |
| Skull Rock's sculpted face | `tools/mapgen/SkullRock.java` |
| The world checker (and the findings judged right as they are) | `tools/mapgen/WorldLint.java`, `tools/worldlint-accepted.txt` |
| The world gate: declared areas, the gate, provenance, the one regen command, the stamp and hooks | `tools/mapgen/WorldAreas.java`, `WorldGate.java`, `Origin.java`, `tools/regen-world.sh`, `tools/worldgate.files`, `tools/worldgate.stamp`, `.claude/hooks/worldgate.sh` |
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
| `::vt saga gate forge king [gateFlags forgeFlags gateKills]` | set the later quests' stages (no arguments prints them) |
| `::vt journal 5/6/7` | Hold the Gate / Heartforged / The Hollow King journal |
| `::vt hp n [npcId]` | cap the hp of NPCs spawned for you; 0 = instant kill with quest credit, -1 = a real death with drops |
| `::vt list` | list NPCs spawned for you |
| `::vt loot rolls npcId` | tally Nythera's loot tiers over `rolls` rolls, and drop ten real rolls at your feet |
| `::vt head npcId anim` | show an NPC's chathead playing a chat animation |

## Ideas for what comes next

- The people inland Oren mentions: a settlement past the Broken Gate.
- The Starless has seen the player. Its spawn could start turning up on Nythera.
- Example armour (Gatehold Warden / Aurel's Royal Guard) and more quest weapons are drafted but not yet wired in.
