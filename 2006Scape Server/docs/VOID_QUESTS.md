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
- **Starfall Crater** near the heart of the land (1556, 2702): a scorched bowl about 26 tiles
  across with a raised, rock-strewn rim, glassy violet round the rift and dead grass thrown out
  over the lip. Oren's camp is on the low west lip, where the old stone road climbs out;
- roads that follow the land: stone from the crater's lip to the **Broken Gate** ruins, entering
  between its towers, then dirt north to the pass and east, and worn tracks to every place the
  saga sends you (the gate requires it);
- the **old beacon**, a fallen tower on the east headland, reached by a track from the crater's
  east rim;
- the **Hollow Scar** in the east, a dying, blackened land;
- forests, ore clusters (copper through mithril), ruins and a standing-stone circle.

The inland settlement Oren hints at ("There are people inland who'll want to meet you") is left
for the next quests. Until then the countryside already has people and wildlife in it.

### Life on Nythera

| Place | Where | Who and what |
|---|---|---|
| **Netwright's Landing** | lake's east shore (1459, 2712) | **Bram Netwright** fishes off a jetty. A shack, rowboats, nets drying, a spit-roast. He sells tackle and buys fish (shop 350). Net/bait spots in the lake, lure/bait spots along the river. |
| **Tollan's Croft** | hills south of the Broken Gate, down a lane off the stone road (1522, 2662) | **Marta Tollan**'s farmhouse. Flax, wheat and cabbages you can pick in fenced fields, a well, hay, a scarecrow, a sheep pen (shearable), a cow pasture and chickens loose in the yard. She sells bread, eggs, milk, wool, shears and the like (shop 351). |
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

## The weapon ladder

Thirty hand-built weapons, each a step cooler than the last. They run from Bram's fish knife, through a blade the
Starless sheds like a scale, to the last light there was before the dark. The low rungs are plain iron, wood and twine in the old-school style. Gatehold's blue
and the Wardens' star come in through the middle. The first violet appears at the Scarglass dagger (35). From the Wand of the Scar (50) up, the
edges and cores glow. From 23 up most have special attacks that throw the saga's own spotanims. The last five
(26-30) leave violet behind for gold and white: the sun, the first star, a king's crown and the last light, the things
the Starless ate. Models are in
`tools/mapgen/LadderWeapons.java`. Rules (levels, tabs, recipes, `::vt weapons`) are in
`game/content/voidrealm/VoidArsenal.java`, and bonuses are in `tools/nythera_data.py`. Item ids run from 8001 to 8030 in rank order.
The god tier, ranks 31-45 (items 8053-8067), has its own section below.

| # | Weapon | Type | Wield | Key bonuses | Where from |
|---|---|---|---|---|---|
| 1 | Netwright's gutting dagger | dagger | Attack 1 | +5 stab, +4 str | Bram's tackle shop (350) |
| 2 | Hob's felling axe | axe | Attack 3 | +7 slash, +8 str | Pell's store (352) |
| 3 | Pilgrim's staff | staff | Magic 5 | +6 magic, +9 crush | Pell's store |
| 4 | Militia spear | spear | Attack 5 | +12 all, +11 str | Dorrin's forge (353) |
| 5 | Wolfbone shortbow | shortbow (iron arrows) | Ranged 5 | +12 ranged | Pell's store; Nythera gear drop, under 30 |
| 6 | Watchman's sword | sword | Attack 10 | +16 stab, +14 str | Dorrin's forge |
| 7 | Steelcap mace | mace | Attack 15 | +19 crush, +17 str, +2 prayer | Dorrin's forge |
| 8 | Smuggler's scimitar | scimitar | Attack 20 | +25 slash, +24 str | Sly Nettle (356) |
| 9 | Barrowstone warhammer | warhammer | Attack 25, Str 20 | +32 crush, +33 str | Nythera gear drop, under 30 (the Barrow's skeletons) |
| 10 | Silkwood wand | wand | Magic 25 | +12 magic | Nythera gear drop, 30-49 |
| 11 | Gatehold longsword | longsword | Attack 30 | +41 slash, +42 str | Warden's armoury (357) |
| 12 | Warden's halberd | halberd | Attack 30, Str 30 | +50 slash, +50 str | Warden's armoury |
| 13 | Warden's longbow | longbow (mithril arrows) | Ranged 30 | +38 ranged | Warden's armoury |
| 14 | Scarglass dagger | dagger | Attack 35 | +31 stab, +29 str | Nythera gear drop, 30-49 |
| 15 | Oathsworn battleaxe | battleaxe | Attack 40 | +52 slash, +62 str | the Barrow Thane, about 1/16 |
| 16 | Frostfang spear | spear | Attack 40 | +41 all, +45 str | Rimeclaw, about 1/14 |
| 17 | Gravewarden 2h sword | 2h sword | Attack 45, Str 40 | +74 slash, +76 str | Nythera bonus roll, 30-49 |
| 18 | Hollowheart shortbow | shortbow (adamant arrows) | Ranged 45 | +60 ranged | the Hollow Matriarch, about 1/14 |
| 19 | Wand of the Scar | wand | Magic 50 | +15 magic, +10 magic def | **recipe**: use a Silkwood wand on a waystone with 1 starmetal and 25 cosmic runes (Crafting 50, after Ashes of the Crown) |
| 20 | Gravestar warhammer | warhammer | Attack 55, Str 50 | +64 crush, +68 str | **recipe**: use a Barrowstone warhammer on Gatehold's anvil with 2 starmetal and a hammer (Smithing 55, after Heartforged) |
| 21 | Riftglass halberd | halberd | Attack 60, Str 40 | +90 slash, +88 str | Nythera bonus roll, 50+ |
| 22 | Rimeheart staff | staff | Magic 65, Attack 40 | +18 magic, +44 crush; **never needs water runes** | the Rimebound sentinel, about 1/16 |
| 23 | Nightglass scimitar | scimitar | Attack 70 | +72 slash, +72 str; special **Rend** | the Starless, about 1/24 |
| 24 | Crownbreaker mace | mace | Attack 75 | +84 crush, +78 str, +6 prayer; special **Shatter** | the Starless, about 1/32 |
| 25 | Starless longsword | longsword | Attack 80 | +90 slash, +70 stab, +92 str; special **Eclipse** | the Starless, about 1/64 |
| 26 | Eventide longbow | longbow (rune arrows) | Ranged 85 | +92 ranged; special **Starshot** | the Starless, about 1/80 |
| 27 | Sunspear | spear | Attack 85 | +86 all, +94 str; special **Sunburst** | the Starless, about 1/96 |
| 28 | Staff of the first star | staff | Magic 90, Attack 50 | +26 magic, +26 magic def, +50 crush; **never needs fire or air runes** | the Starless, about 1/112 |
| 29 | Kingsbane battleaxe | battleaxe | Attack 90, Str 80 | +104 slash, +96 crush, +118 str; special **Regicide** | the Starless, about 1/128 |
| 30 | Last Light 2h sword | 2h sword | Attack 99, Str 90 | +124 slash, +108 crush, +128 str, +6 prayer; special **Last Light** | **recipe**: use the Starless longsword on the Starless Gate with 5 Starless shards, after 25 Starless kills |

Boss drops roll in table order and stop at the first hit. The ladder weapon rolls straight after the boss's own
unique, so "about 1/N" is a little under 1/N.

**Specials.** These use the stock spec bars: the longsword/scimitar tab's bar, which the Emberbrand also uses, and the mace tab's bar.
- **Rend** (Nightglass scimitar, 50%): 115% damage, 135% accuracy, the dragon scimitar's swing. The target gets the
  void impact.
- **Shatter** (Crownbreaker mace, 50%): 140% damage, 120% accuracy, the dragon mace's blow. A falling star lands on
  the target.
- **Eclipse** (Starless longsword, 60%): 135% damage, 130% accuracy, the dragon longsword's cleave. The eclipse ring
  closes on the target.
- **Starshot** (Eventide longbow, 50%): 125% damage, 115% accuracy, through the magic longbow's own special path
  (one shot, its projectile and draw).
- **Sunburst** (Sunspear, 50%): 130% damage, 125% accuracy, the dragon spear's lunge; the heart-fire bursts on the
  target. It uses the spear tab's generic bar.
- **Regicide** (Kingsbane battleaxe, 50%): the dragon battleaxe's rampage, hotter: Strength +20% instead of +15%,
  with the heart-fire on the wielder and "No more kings!".
- **Last Light** (Last Light 2h sword, 60%): 150% damage, 120% accuracy, a 2h cleave; the eclipse falls on the target
  and the heart-fire flares on the wielder.
- **Twin fang** (voidsteel dagger, 25%): the dragon dagger's double stab, at 115% damage and 120% accuracy.

**How they're built.** Each weapon is one frame placed into the inventory and wielded pose of a comparable cache
item, as in `QuestWeapons`. The ladder adds nine poses read off the cache: longsword, sword, dagger, scimitar, warhammer,
spear, 2h sword, magic longbow and master wand. Their coordinates are in the class comment. The wielded pose keeps
the base's skin label: 27 for the longsword, spear, warhammer, mace and bows, and 50 for the dagger, sword, scimitar, 2h sword, battleaxe,
halberd, staff and wand. The stock attack, stand and walk animations then move them. Names keep the stock words
(dagger, scimitar, 2h sword, longbow...), because attack speed, animations and two-handedness come from the name.
`VoidArsenal` sets each one's tab explicitly. No crossbow: the engine only fires bolt racks from Karil's crossbow,
and stock bolts aren't wired up.

**Previewing without the game.** `tools/mapgen/ModelPreview` renders each rung offline: flat-shaded, painter-sorted,
with the client's item-sprite camera for the icon. It draws the base item's own icon beside it to check the fit, and the
wielded model on the default body with the hand ringed at (-30, -97, 0). It also writes a contact sheet. From
`2006Scape Server`:
`java -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.ModelPreview data/cache outDir [S1|S2|G] [rank...]`.

**Inventory icons are centred automatically.** Each custom item borrows a stock item's icon camera (opcodes 4-8) but
has a new model, so the borrowed camera can leave it off to one side or running past the edge of the slot. The last step
of `buildDefinitions` is `IconCentre`. For every item past the stock table, it projects the model exactly as the client's
`ItemDef.getSprite` does. It then slides the icon to the middle of the 32x32 sprite, sideways with opcode 7 and up or down
with opcode 8. If the icon is too big to leave room for the black outline, it first pulls the zoom (opcode 4) back. The
rotations are left alone. Opcode 8 also pushes the model away from the camera, and at pitch 256 (45 degrees) it can't
move the icon up or down at all, so it is only used where it changes the icon's size by 4% or less. Items it can't
centre to within 0.75 px are listed in the regen output. `GENOPTS=-Dicons=dir tools/regen-world.sh` also writes
`icons-moved.png`, which shows each moved icon before and after with a crosshair through the middle, and `icons-all.png`.
New items need nothing extra: the pass picks them up.

### The god tier (31-45)

Fifteen weapons above everything else. Each belongs to one of the world's powers: the stars and the First Star, the
Starless, the void and the Scar, Aurel's heart-fire, the Hollow King, the Wardens' founders and the old kingdom's
crown. They need 85-99 and are a clear step past Last Light and the voidsteel set. The numbers stay where the
engine's max-hit formulas stay sane: strength bonus tops out at 155, ranged at 112 and magic at 40. The silhouettes
are the biggest yet: oversized blades, parts that float free of the weapon (separate pieces in the same model), crowns
of spikes, glowing cores and layered bands, all on dark bodies so the bright accents read. Models are in
`tools/mapgen/GodWeapons.java`.

| # | Weapon | Type | Wield | Key bonuses | Special / effect | Where from |
|---|---|---|---|---|---|---|
| 31 | Wardensoath 2h sword | 2h sword | Attack 85, Str 80 | +130 slash, +112 crush, +132 str | **Oath of the Wall** (50%): 135% dmg, 135% acc, a falling star on the target; heals you by the damage dealt | the Starless, about 1/150 |
| 32 | Heart of Aurel longsword | longsword | Attack 86 | +104 slash, +82 stab, +108 str | **Kindling** (50%): 140% dmg, 125% acc, heart-fire on both; **on hit** (1 in 5): heart-fire mends you by a quarter of the hit | **recipe**: Emberbrand + voidsteel longsword + 6 shards (Smithing 80) |
| 33 | Scarmaw halberd | halberd | Attack 87, Str 80 | +124 slash, +96 stab, +120 str | **Devour** (50%): 135% dmg, 125% acc, void impact; heals you by the damage dealt | the Starless, about 1/180 |
| 34 | Hollow Crown warhammer | warhammer | Attack 88, Str 85 | +112 crush, +116 str, +4 prayer | **Kneel** (50%): 145% dmg, 120% acc, void impact | **recipe**: Hollow King's mace + voidsteel warhammer + 6 shards (Smithing 82) |
| 35 | Dawnbringer longbow | longbow (rune arrows) | Ranged 88 | +104 ranged | **First Light** (50%): the magic longbow's special path | the Starless, about 1/200 |
| 36 | Wand of the Unmade | wand | Magic 88 | +32 magic, +18 magic def | **never needs chaos or death runes** | **recipe**: Wand of the Scar + voidsteel dagger + 6 shards (Crafting 85) |
| 37 | Spear of the First Star | spear | Attack 90 | +100 all, +110 str | **Starfall** (50%): 140% dmg, 130% acc, a falling star | **recipe**: Sunspear + voidsteel spear + 8 shards (Smithing 85) |
| 38 | Crownfire scimitar | scimitar | Attack 90 | +112 slash, +112 str | **Coronation** (50%): 130% dmg, 145% acc, heart-fire; **on hit** (1 in 5): restores prayer | the Starless, about 1/220 |
| 39 | Gatebreaker battleaxe | battleaxe | Attack 91, Str 85 | +122 slash, +112 crush, +136 str | **Breach** (50%): the rampage, Strength +20% and Attack +10% | **recipe**: Kingsbane battleaxe + voidsteel battleaxe + 8 shards (Smithing 88) |
| 40 | Nightfall shortbow | shortbow (rune arrows) | Ranged 92 | +112 ranged | **Nightfall** (55%): the magic shortbow's special path | the Starless, about 1/250 |
| 41 | Riftcaller staff | staff | Magic 93, Attack 50 | +34 magic, +30 magic def, +56 crush | **never needs earth or water runes** | **recipe**: Riftglass halberd + Rimeheart staff + 8 shards (Crafting 90, 50 Starless kills) |
| 42 | Sunfall 2h sword | 2h sword | Attack 94, Str 90 | +140 slash, +122 crush, +144 str | **Sunfall** (60%): 155% dmg, 120% acc, a falling star on the target, heart-fire on you | the Starless, about 1/280 |
| 43 | Black Sun warhammer | warhammer | Attack 95, Str 92 | +128 crush, +134 str, +6 prayer | **Black Sun** (60%): 150% dmg, 120% acc, the eclipse; heals you by the damage dealt; **on hit** (1 in 6): the dark feeds you a third of the hit | **recipe**: Crownbreaker mace + voidsteel mace + 10 shards (Smithing 90, 50 kills) |
| 44 | Crown of Stars staff | staff | Magic 96, Attack 60 | +40 magic, +36 magic def, +60 crush | **never needs any elemental rune** | **reward**: use the Staff of the first star on the Starless Gate with every quest in the saga finished, 50 Starless kills and 10 shards |
| 45 | Godsfall 2h sword | 2h sword | Attack 99, Str 99 | +150 slash, +132 crush, +155 str, +10 all defence, +8 prayer | **Godsfall** (70%): 160% dmg, 130% acc, the eclipse on the target, heart-fire on you; heals you by the damage dealt; **on hit** (1 in 5): eclipse, a quarter of the hit as hitpoints and some prayer | **the final recipe**: Last Light + Wardensoath + Sunfall + 25 shards (Smithing 90, 100 Starless kills) |

**Getting them.** Everything here is endgame:
- **Drops.** Six come from the Starless's table (`npcdrops.json`, 3889), after its own uniques and the ladder's top
  rungs. Rolls stop at the first hit, so each "about 1/N" is a little rarer than 1/N in practice.
- **Recipes.** Seven are recipes at the Starless Gate, after The Hollow King. Each combines a voidsteel weapon (or
  another rare drop) with the ladder weapon it grows out of and Starless shards (`VoidArsenal.GOD_RECIPES`). Use the
  first item named on the gate.
- **The Crown of Stars staff** is the reward for finishing everything: Echoes of the Void, Beyond the Veil, Ashes of the
  Crown, Hold the Gate, Heartforged and The Hollow King, plus 50 Starless kills.
- **Godsfall** ends the chain. It eats Last Light (itself a recipe) and two god-tier greatswords.

**How the effects work.**
- **Specials** use the existing system: `Specials.specialAttack` entries, the stock buttons and bars, and the saga's
  spotanims 667-671. No new spotanims, so the spotanim count is unchanged.
- **The heal-on-hit effect** is the special system's own effect 4. The saga's specials now carry their effect into
  combat; stock specials are untouched.
- **Warhammers and halberds** sit on the mace and spear tabs, because those tabs have spec buttons.
- **On-hit effects** are melee against monsters only (`VoidArsenal.onMeleeHit`, called from `applyNpcMeleeDamage`).
- **Free runes** are `VoidArsenal.FREE_RUNES`, asked by `MagicRequirements.wearingStaff`.

## Starmetal and voidsteel: two new tiers

Rune and dragon are sets: every shape in one metal, one look, and a clear step between them. Nythera adds two more,
each a complete set of ten (dagger, sword, longsword, scimitar, mace, warhammer, battleaxe, 2h sword, spear and
halberd), and voidsteel adds a longbow. No crossbows (see above), and no starmetal bow: the Duskyew shortbow already
fills that tier.

- **Starmetal (Attack 50), between rune and dragon.** The fallen star that Dorrin learns to work in Heartforged. It is
  harder than rune, but it is still metal from *this* sky, so it stops short of dragon. Pale silver-lilac blades with
  bright honed edges, night-blue grips wound with starmetal, star-pointed pommels and guards, and a violet star-shard
  fuller or gem as the one accent. It is clean, straight and heraldic: the Wardens' look. **Source:** use a rune weapon on
  Gatehold's anvil with starmetal and a hammer (Smithing 60, after Heartforged). It takes 1 starmetal for the dagger,
  sword, mace and spear, 2 for the longsword and scimitar, and 3 for the warhammer, battleaxe, 2h sword and halberd.
  The weapon and the starmetal are used up. Also from Nythera's high-band bonus roll (dagger, sword, spear) and the
  Starless (longsword, about 1/30).
- **Voidsteel (Attack 70), above dragon.** Starmetal pushed through the Starless Gate and bitten by the dark behind
  it. The Starless is what crowned Aurel and what ate the stars, so its metal outranks anything a dragon breathes on.
  It is near-black violet whose every edge burns. Guards sweep forward like horns, barbs run down the backs of the blades,
  pommels are hooked, and a single white eye sits where starmetal had its shard. It is menacing and asymmetric: the
  Starless's look. **Source:** use a starmetal weapon (or, for the longbow, a magic longbow) on the Starless Gate with
  Starless shards, in the same counts as above (3 for the bow), at Smithing 75, after The Hollow King. Starless shards
  come from the Starless (1-2 on every kill) and its spawn (about 1/4). The voidsteel dagger also drops from the
  Starless (about 1/40).

Within each set the stats mirror rune and dragon. The dagger is weakest and quickest, the sword and scimitar come
next, and the longsword, mace and spear sit in the middle. The warhammer and battleaxe hit harder and slower, and the
2h sword and halberd are strongest and slowest. Each voidsteel piece is about as far past its starmetal twin as dragon
is past rune. Attack speed comes from the name, as for the ladder.

| Piece | Starmetal (8032-8041): stab / slash / crush, str | Voidsteel (8042-8052): stab / slash / crush, str |
|---|---|---|
| dagger | 33 / 18 / -4, 32 | 48 / 32 / -4, 47 (special **Twin fang**) |
| sword | 46 / 34 / -2, 47 | 62 / 46 / -2, 63 |
| longsword | 48 / 58 / -2, 60 | 66 / 78 / -2, 80 |
| scimitar | 8 / 56 / -2, 55 | 10 / 76 / -2, 75 |
| mace | 30 / -2 / 50, 46, +4 prayer | 48 / -2 / 70, 64, +6 prayer |
| warhammer | -4 / -4 / 64, 60 | -4 / -4 / 82, 80 |
| battleaxe | -2 / 59 / 54, 74 | -2 / 80 / 74, 95 |
| 2h sword | -4 / 80 / 65, 82 | -4 / 102 / 90, 104 |
| spear | 46 / 46 / 46, 51 | 62 / 62 / 62, 68 |
| halberd (also Str 45 / Str 60) | 58 / 80 / 0, 78 | 80 / 106 / 0, 99 |
| longbow (Ranged 70, rune arrows) | - | +82 ranged |

Both sets are built from one parametric family in `tools/mapgen/WeaponSets.java`. Every shape is written once and
dressed by a `Style` (palette plus design flags), so a starmetal scimitar and a voidsteel scimitar are visibly the same
weapon in two metals. Previews: `ModelPreview data/cache outDir S1` (or `S2`) writes `S1-NN-name.png` and
`S1-sheet.png`.

## Armour sets

Twenty-four matched sets climb with the weapons: six for the saga (Defence 40 and up), eight humbler ones for the
early game (levels 10-40) and ten for the middle levels (20-60), below. Each is tied to a place or a chapter of the story. Plate has a full helm,
platebody, platelegs, kiteshield, boots and gauntlets. Hide has a coif, body, chaps, boots and vambraces. Robes have a
hat, robe top, robe bottom, boots and gloves. Hide and robes have no shield. Every piece is cut from the stock piece
of the same kind (rune plate, green d'hide, mystic; for the low sets also the med helm, chainbody and sq shield, and
the leather cowl, body and chaps) on both the male and female bodies, so it sits and animates like
the stock item. Each set then adds its own decoration (horns, fur, crystal, pauldrons, emblems) on top. On the female
tops the bare-midriff band of the stock meshes is filled in. Whether a body hides the arms, or a helm the face, comes
from the name, as for stock items. Defence bonuses below are stab / slash / crush / magic / ranged.

| # | Set (ids) | Wear | Look and story | Pieces: key bonuses | Source |
|---|---|---|---|---|---|
| 1 | **Oathsworn plate** (8068-8073) | Defence 40 | Old-kingdom bronze from the barrows: ram's-horn helm, oath-rune on the chest. Rune-equivalent | helm 32/34/29/-1/31; body 86/84/76/-6/84 +2 prayer; legs 54/52/50/-4/52; kite 50/52/46/-1/50; boots 13/14/15; gauntlets +4 attack, +2 str | Thane's oathsworn (the Ashes of the Crown guard: boots and gauntlets 1/20, helm 1/25, legs and kite 1/30, body 1/40); the **Barrow Thane** (helm 1/20, legs and kite 1/25, body 1/35); boots and gauntlets in the 30-49 gear roll |
| 2 | **Duskstalker hide** (8074-8078) | Ranged 50 (body and chaps also Defence 40) | Hunter's leathers from the Hollow Scar's wolves: wolf-eared coif, fur mantle and fangs. Between green and blue d'hide | coif +7 ranged; body +28 ranged, 46/40/52/50/48; chaps +16 ranged; boots +4; vambraces +11 ranged | **Sly Nettle** (shop 356, Smugglers' Cove) sells every piece; the Hollow Matriarch drops the body (1/25) |
| 3 | **Scarweave robes** (8079-8083) | Magic 60, Defence 20 | The Hollow Herald's weave: crystal-tipped hat, shard mantle, a burning scar down the robe. Just past mystic | hat +6 magic; top +25 magic; bottom +19 magic; boots +6; gloves +6 | The **Hollow Herald** (top and bottom 1/4, hat 1/3); hat, boots and gloves in the 50+ gear roll; top and bottom in the 50+ bonus roll |
| 4 | **Starmetal plate** (8084-8089) | Defence 60 | The fallen star, faced over rune. Crested helm with a star spray, star pauldrons. Dragon-equivalent | helm 40/42/37/0/40; body 100/96/90/-4/98; legs 66/64/60/-2/64; kite 62/64/58/0/62; boots 16/17/18; gauntlets +6 attack, +4 str | Use a **rune** piece (leather gloves for the gauntlets) on **Gatehold's anvil** with a hammer and starmetal (2 helm, 4 body, 3 legs, 3 kite, 1 boots, 1 gauntlets), Smithing 65, after Heartforged |
| 5 | **Voidsteel plate** (8090-8095) | Defence 75 | Starmetal pushed through the Starless Gate. Horned helm, the white eye on the chest, burning violet edges | helm 50/52/46/2/50 +2 str; body 120/116/110/4/118 +4 str, +3 prayer; legs 80/78/74/2/78; kite 76/78/72/4/76; boots 20/21/22 +2 str; gauntlets +9 attack, +7 str | Use a **starmetal** piece on the **Starless Gate** with Starless shards (same counts), Smithing 80, after The Hollow King |
| 6 | **Heartfire regalia** (8096-8101) | Defence 90 | Aurel's Royal Guard plate, re-lit: white and gold, a crown of flame with a halo, winged pauldrons, the sun on the chest. The best armour in the game | helm 60/62/56/6/60 +4 str, +4 prayer; body 140/136/130/10/138 +6 str, +6 prayer; legs 94/92/88/6/92; kite 90/92/86/8/90; boots 24/25/26 +3 str; gauntlets +12 attack, +10 str | Use a **Royal guard** piece on the **Starless Gate** with its matching voidsteel piece and twice the shards. Needs every quest in the saga, 25 Starless kills and Smithing 90 |

Plate weighs on the caster's hand (a magic penalty, and a third of it to ranged); hide and robes do not.

### The low sets (levels 10-40)

Four melee, two ranged, two magic, between the stock bronze-to-adamant tiers. They are plainer than the saga's sets:
cloth, leather, chain and a single emblem rather than wings and fire. Chain is lighter than plate, with less defence
and a smaller magic penalty. Chain sets have a med helm, chainbody, platelegs, sq shield, boots and gloves; the
Warden initiate's is full plate. The models are in `tools/mapgen/LowArmour.java`.

| # | Set (ids) | Wear | Look and story | Pieces: key bonuses | Source |
|---|---|---|---|---|---|
| 7 | **Gatehold recruit** (8102-8107) | Defence 10 | The Wardens' issue for new hands on the wall: iron mail, a night-blue tabard with the gate's star, a blue sq shield. Iron-to-steel | med helm 9/10/8; chainbody 22/31/37; legs 20/19/18; shield 14/15/13; boots 3/3/4; gloves +1 attack, 2/2/2 | **Dorrin's Forge** (353) sells it all. Or use an **iron** med helm, chainbody, platelegs or sq shield on **Gatehold's anvil** with a hammer and an iron bar (Smithing 10). Helm, boots and gloves in the under-30 gear roll, the chainbody its bonus roll |
| 8 | **Woodsman** (8108-8112) | Ranged 15 | A forester's greens: a feathered hood, a jerkin with a quiver strap, patched chaps. Leather to studded | hood +2 ranged; jerkin +6 ranged, 12/16/14/6/16; chaps +4 ranged; boots +1; vambraces +4 ranged | **Pell's Provisions** (352). Hood, boots and vambraces in the under-30 gear roll, the jerkin its bonus roll |
| 9 | **Croftwise** (8113-8117) | Magic 20 | A hedge-witch's homespun: a straw-coloured pointed hat, a green shawl with a herb pouch, a green-hemmed robe. Just past wizard robes | hat +3 magic; top +10; bottom +7; boots +2; gloves +2 | **Marta Tollan** (Tollan's Croft, shop 351) |
| 10 | **Rattlebone** (8118-8123) | Defence 25 | Cursed Island pirate salvage: rusty mail with a ribcage and a red rag, horned helm, a crossbones shield. Steel-to-mithril | med helm 14/15/12; chainbody 30/40/46 +1 str; legs 28/26/25; shield 21/22/19; boots 5/6/7; gloves +2 attack, +1 str | **Mad Pete's Salvage** (349) sells the helm, shield, boots and gloves; **cursed pirates** drop helm, boots and gloves (1/40 each) and the shield (1/60); the **cursed first mate** drops the chainbody and legs (1/12 each) |
| 11 | **Blackbrine** (8124-8128) | Ranged 30 (body and chaps also Defence 20) | Captain Blackbrine's crew's hide: a red bandana coif, a black hide coat with a red sash and brass buckles. Studded to green d'hide | coif +3 ranged; body +13 ranged, 24/30/34/18/30; chaps +7 ranged; boots +2; vambraces +6 ranged | **Sly Nettle** (356) sells coif, chaps, boots and vambraces; the body only drops, from the **cursed first mate** (1/16) |
| 12 | **Warden initiate** (8129-8134) | Defence 30 | The Wardens' first plate: pale steel with a night-blue tabard and star, a blue-and-star kiteshield. Mithril-to-adamant | full helm 20/22/17; body 66/64/56 +1 prayer; legs 34/32/30; kite 30/32/28; boots 8/9/10; gauntlets +3 attack, +1 str | **The Warden's Armoury** (357, after Hold the Gate). Helm, kite, boots and gauntlets in the 30-49 gear roll; body and legs its bonus roll |
| 13 | **Barrowbronze** (8135-8140) | Defence 35 | Verdigrised bronze from the Sunken Barrow's dead: scale mail, a crested helm, a sun-disc shield. Adamant-like, +prayer | med helm 24/26/21 +1 prayer; chainbody 42/56/62 +2 prayer; legs 40/38/36 +1 prayer; shield 34/36/32; boots 10/11/12; gloves +4 attack, +2 str | Only in the **Sunken Barrow**: half its monsters' gear rolls (helm, shield, boots, gloves) and half their bonus rolls (chainbody, legs) |
| 14 | **Veilsworn** (8141-8145) | Magic 40, Defence 10 | The Hollowdeep cult's ash-and-violet robes: a violet-tipped hat, the white eye on the chest, a frayed hem. Wizard to mystic | hat +5 magic; top +20; bottom +15; boots +4; gloves +4 | Only in **Hollowdeep**: half its monsters' gear rolls (hat, boots, gloves) and half their bonus rolls (top, bottom) |

### The middle sets (levels 20-60)

Ten more sets fill the gaps between the low sets and the saga's: four melee (two chain, two plate), three ranged
and three magic. Each belongs to a place or a people of Nythera. Five of them are worn by the
monsters that drop them, the kit-wearers (below). The models are in `tools/mapgen/MidArmour.java`.

| # | Set (ids) | Wear | Look and story | Pieces: key bonuses | Source |
|---|---|---|---|---|---|
| 15 | **Quarryhand** (8146-8151) | Defence 20 | Tess's quarry crews' work mail: grey chain under a leather apron, a candle-lamp on the helm, crossed picks on the shield. Iron-to-steel | med helm 12/13/10; chainbody 27/36/42; legs 24/23/22; shield 18/19/16; boots 4/5/6; gloves +1 attack, +1 str | **Quarrywright's Picks** (Tess Quarryhand, shop 354) |
| 16 | **Silkspinner** (8152-8156) | Ranged 25 | Silkwood's stalkers: dark-green leathers bound in spider silk, a hood with fangs and red eyes, a web on the back. Studded-like | hood +3 ranged; jerkin +9 ranged, 18/22/24/12/22; chaps +6; boots +1; vambraces +5 ranged | Worn and dropped by **Silkwood stalkers** (kit roll) |
| 17 | **Stonecaller** (8157-8161) | Magic 30 | Slate-grey robes that carry a waystone's rune: a hat set with standing stones, a carved stone on a cord, violet runes down the front | hat +4 magic; top +14; bottom +10; boots +3; gloves +3 | Use a **Croftwise** piece on any **waystone** with 20 earth and 2 cosmic runes (Crafting 25, Magic 30) |
| 18 | **Ridgerunner** (8162-8166) | Ranged 35 (body and chaps also Defence 25) | Snow-white hide for the mountain pass: a rope over the shoulder, a fur hem, a snowflake on the back. Green d'hide-like | coif +5 ranged; body +17 ranged, 30/34/38/28/34; chaps +10; boots +3; vambraces +8 ranged | **The Last Hearth** (Morra, shop 355) |
| 19 | **Ravager** (8167-8172) | Defence 40 | The Scar reavers' plate: a ravager's bleached bones over black, a gaping maw for a visor, clawed pauldrons, a violet slit. Rune-like, trades prayer for strength | full helm 30/32/27 +1 str; body 82/80/72 +2 str; legs 51/49/47 +1 str; kite 47/49/43; boots 12/13/14 +1 str; gauntlets +4 attack, +3 str | Worn and dropped by **Scar reavers** (kit roll) |
| 20 | **Moonmourn** (8173-8177) | Magic 45, Defence 10 | The barrow mourners' midnight robes: a veiled hat with a silver crescent, the moon on the breast with three tears, crescents and stars round the hem | hat +5 magic; top +22; bottom +16; boots +5; gloves +5 | Worn and dropped by **Moonmourners** (kit roll) |
| 21 | **Rimebound** (8178-8183) | Defence 50 | Frostfang's frozen soldiers: ice-blue mail with icicles, an icicle crown, crystal knee-guards, a snowflake shield | med helm 32/34/29; chainbody 58/74/82; legs 54/52/50; shield 46/48/44; boots 13/14/15; gloves +5 attack, +3 str | Worn and dropped by **Rimebound warriors** (kit roll, Frostfang) |
| 22 | **Nightrunner** (8184-8188) | Ranged 50 (body and chaps also Defence 40) | The Cove's night crew: charcoal and sea-teal hide, a face-scarf, crossed belts, a lantern at the hip, a ragged half-cloak | coif +6 ranged; body +26 ranged, 44/38/50/46/46; chaps +15; boots +4; vambraces +10 ranged | Worn and dropped by **Cove nightrunners** (kit roll) |
| 23 | **Farseer** (8189-8193) | Magic 55, Defence 20 | A seer's deep-teal and gold: a gold circlet with a seeing-crystal at the brow, a scrying mirror on the breast, gold bands down the robe | hat +6 magic; top +23; bottom +18; boots +5; gloves +5 | Hat, boots and gloves in the 50+ gear roll; top and bottom in the 50+ bonus roll |
| 24 | **Last knight's plate** (8194-8199) | Defence 60 | Aurel's knights' plate, as Sir Vaelric wore it: grey steel and gold, a broken crown round the helm, a crimson tabard and plume. Just under starmetal, with prayer | full helm 38/40/35 +2 prayer; body 96/94/88 +3 prayer; legs 64/62/58; kite 60/62/56; boots 15/16/17; gauntlets +5 attack, +4 str (+1 prayer on each of these) | Use an **adamant** full helm, platebody, platelegs, kiteshield or boots, or a **Warden's gauntlets**, on **Gatehold's anvil** with a hammer and starmetal (1 helm, 2 body, 2 legs, 1 kite, 1 boots, 1 gauntlets), Smithing 60, after Heartforged. **Sir Vaelric** drops the helm (1/4) |

### The kit-wearers

Five monsters wear one of the middle sets, every piece of it, with the matching weapon in hand. Their model is built
from the set's own worn models and the weapon's wielded model on the human rig (`KitWeapons.wearer`), so what you see
on them is what you get. On top of Nythera's ordinary loot, each kill has a **15% chance of one piece of the kit**: a
random piece of the set or the weapon, all equally likely (15% in all, not per piece; `VoidLoot.kit`).

| NPC (id) | Combat, hp, max hit | Fights with | Wears / carries | Where |
|---|---|---|---|---|
| **Silkwood stalker** (3891) | 28, 30 hp, 3 | Ranged (steel arrows) | Silkspinner + **Silkstring shortbow** (8200: Ranged 25, +22 ranged) | Silkwood: 1376,2688; 1378,2685; 1386,2683; 1384,2686 (north edge) and 1401,2655; 1407,2661; 1406,2654 (south-east) |
| **Scar reaver** (3892) | 45, 50 hp, 5 | Melee (battleaxe) | Ravager + **Ravager battleaxe** (8201: Attack 40, +44 slash, +38 crush, +52 str) | The Hollow Scar east of the fissure: 1633,2740; 1636,2737; 1634,2733; 1631,2743; 1637,2745 |
| **Moonmourner** (3893) | 48, 45 hp, 6 | Magic (water bolt) | Moonmourn + **Moonmourn staff** (8202: Magic 45, Attack 40, +13 magic, +30 crush, +10 magic defence) | Round the Sunken Barrow's mound: 1468,2744; 1469,2738; 1474,2751; 1482,2751; 1488,2747 |
| **Rimebound warrior** (3894) | 55, 65 hp, 7 | Melee (warhammer) | Rimebound + **Rimebound warhammer** (8203: Attack 50, Strength 45, +58 crush, +60 str) | Frostfang, the warriors' hall: 1582,9697; 1586,9701; 1587,9698; 1583,9702 |
| **Cove nightrunner** (3895) | 54, 55 hp, 6 | Ranged (adamant arrows) | Nightrunner + **Nightrunner shortbow** (8204: Ranged 50, +62 ranged) | Smugglers' Cove, the east beach: 1486,2620; 1489,2617; 1492,2621; 1488,2624; 1494,2618 |

All five are aggressive. Their attack, block and death animations are a player's (`NpcEmotes`); the ranged and magic
ones are set up in `NpcCombat.loadSpell` and `NpcData` (range and hit delay). The Silkstring shortbow fires up to
steel arrows, the Nightrunner shortbow up to rune.

The models live in `tools/mapgen/ArmourSets.java`, one `Style` per set: palette, motif, ids, names and examines.
`ArmourPreview data/cache outDir set [slot...|full]` writes one card per piece (the inventory icon, then the piece
worn on the male and female bodies, front and back), `A1-01-oathsworn-helm.png` and so on, and `A1-full-...png` for
the whole set. If a piece's decorations would spill out of its 32x32 sprite, the inventory camera is pulled back
(`ArmourSets.fitCamera`). The generator does the same, so the game shows what the preview shows. Wield levels,
slots and recipes are in `game/content/voidrealm/VoidArmoury.java`. `::vt armour n` puts set n (1-24) into your
inventory.

### The Shardback

When the star fell on Nythera, the rock-bears denning on that hill weren't killed. They were fused with it: their hides
went to slag and the star's violet glass grew up through their backs. Gatehold's miners won't work the crater rim
without a watchman. **Shardback** (npc 4000): combat 47, 48 hp, max hit 6 (a bite), 2x2, attack speed 4 ticks,
aggressive. It resists ranged (+75: arrows glance off the glass) and is weak to magic (+10). It drops big bones, and
rolls Nythera's loot tiers like any other monster there.

Four live on the crater rim, away from the stone road, Oren's camp and the landing inside the crater: 1556,2715 (north),
1565,2712 (north-east), 1569,2699 (east) and 1567,2693 (south-east). Each is the south-west tile of a 2x2 that is open
on the map. They are written by `tools/nythera_data.py`.

It's the first creature with its own model and its own animations. `tools/mapgen/Shardback.java` builds the
710-triangle model (16000) on a 23-bone rig, and authors the stand (4100), walk (4101), bite (4102), death (4103) and
flinch (4104) sequences into frame file 1100 (frames 34000-34049). The legs are posed by IK so planted paws stay on the
ground. `regen-world.sh` installs it after the world is written: it pads the npc table up to 4000 with empty entries,
and refuses if anything else already holds its ids. `ShardbackPreview` renders the GIFs, contact sheets and card.

### The Gravecoil

The barrow-folk sealed a serpent into every king's grave to guard the grave-goods. The Thane's curse would not let his
household die, and it did not let the serpents die either: centuries in the dark have made them huge, and they still
wear the kings' bronze arm-rings they slid into as hatchlings, grown deep into the hide. **Gravecoil** (npc 4001):
combat 52, 60 hp, max hit 7 (a strike), 2x2, attack speed 4 ticks, aggressive. It is weak to ranged (+12: arrows find
the hide between its rings) and resists magic (+80: grave-wards are cut into the bronze). It drops big bones and rolls
Nythera's loot tiers. **It is defined but not placed anywhere yet**: its home would be the graves round the Sunken
Barrow's mound.

It's the second creature with its own model and animations, and the first that isn't a quadruped. `tools/mapgen/Gravecoil.java`
builds a 670-triangle serpent on an 18-bone chain (model 16001): one skin of eight-sided rings, one ring per joint, so
the body bends without seams; a hood that flares and folds (a scale joint), a hinged jaw and a forked tongue that slides
out to taste the air. Every ring's belly is flat at y = 0, so the lying body is posed by turning about the vertical
alone and never leaves the ground; the reared neck is posed by pitch. Its sequences are in frame file 1101 (frames
34100-34163): stand (4110: a cobra's sway, hood breathing, tongue flicks), walk (4111: a slither that lays each body
joint on a fixed track in the ground's frame, so nothing slips sideways), strike (4112), death (4113: rears, topples
onto its side, thrashes once, goes slack) and recoil (4114).

The rig is the shared `CreatureRig` (joints to a skin list, poses quantised to frames, the cache installer), which is
the Shardback's approach made general for the next creature. `GravecoilPreview outDir` renders the GIFs, a captioned
showcase, contact sheets and a card through `CreaturePreview`, and measures every frame against the ground: nothing
under it, every part that should be resting on it within a unit of it (the body always; everything once it has died),
seams between bones, and the walk's distance from its track. It exits non-zero if a frame fails.

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
| The weapon ladder (items 8001-8030, and the god tier 8053-8067), the Starless shard (8031), the starmetal and voidsteel sets (8032-8052): rules, recipes, effects, `::vt weapons`/`sets`; models; offline previews | `game/content/voidrealm/VoidArsenal.java`; `tools/mapgen/LadderWeapons.java`, `WeaponSets.java`, `GodWeapons.java`; `tools/mapgen/ModelPreview.java` |
| The armour sets (items 8068-8199): slots, wield levels, recipes, `::vt armour`; models; offline previews | `game/content/voidrealm/VoidArmoury.java`; `tools/mapgen/ArmourSets.java` (the saga's six), `tools/mapgen/LowArmour.java` (the eight low sets) and `tools/mapgen/MidArmour.java` (the ten middle sets); `tools/mapgen/ArmourPreview.java`; drops in `VoidLoot.java` and `tools/nythera_data.py` |
| The kit-wearers (NPCs 3891-3895) and their weapons (items 8200-8204): models, the 15% kit roll, combat | `tools/mapgen/KitWeapons.java` (weapons and the npc model), `VoidRealmGenerator.kitWearers`; `VoidLoot.kit`; `NpcEmotes`, `NpcCombat.loadSpell`, `NpcData`; rules in `VoidArsenal.KIT_RULES`; stats and spawns in `tools/nythera_data.py`; `ModelPreview data/cache outDir K` for the weapon cards |
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
| `::vt loot rolls npcId` | tally Nythera's loot tiers over `rolls` rolls (and, for a kit-wearer, its 15% kit roll), and drop ten real rolls at your feet |
| `::vt head npcId anim` | show an NPC's chathead playing a chat animation |
| `::vt weapons [from to]` | put the weapon ladder (ranks 1-45, or from-to; `31 45` is the god tier) into your inventory |
| `::vt sets 1` / `::vt sets 2` | put the starmetal (1) or voidsteel (2) set into your inventory, with 10 starmetal or 10 Starless shards |
| `::vt armour n` | put armour set n into your inventory (1 Oathsworn, 2 Duskstalker, 3 Scarweave, 4 starmetal, 5 voidsteel, 6 Heartfire, 7 Gatehold recruit, 8 Woodsman, 9 Croftwise, 10 Rattlebone, 11 Blackbrine, 12 Warden initiate, 13 Barrowbronze, 14 Veilsworn, 15 Quarryhand, 16 Silkspinner, 17 Stonecaller, 18 Ridgerunner, 19 Ravager, 20 Moonmourn, 21 Rimebound, 22 Nightrunner, 23 Farseer, 24 Last knight) |

## Ideas for what comes next

- The people inland Oren mentions: a settlement past the Broken Gate.
- The Starless has seen the player. Its spawn could start turning up on Nythera.
- Example armour (Gatehold Warden / Aurel's Royal Guard) and more quest weapons are drafted but not yet wired in.
