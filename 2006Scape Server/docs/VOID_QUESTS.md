# The Void quest series

The series continues from **Curse of Skullbrine**. The portal under Skull Rock wakes up. The player
gathers the three splinters of its broken seal, opens the door, and meets **the Hollow King**, a
boss who can't be beaten yet. He shatters the platform under them, and they fall to **Nythera**,
a new continent. For now the story stops there ("To be continued...").

| Quest | QP | Requirements | Stage field |
|---|---|---|---|
| Echoes of the Void | 2 | Curse of Skullbrine; a spade and a pickaxe; able to fight level 10-30 undead | `Player.voidEchoes` (0-3) |
| Beyond the Veil | 3 | Echoes of the Void | `Player.voidVeil` (0-4) |

Both stage fields are saved in the character file. In the quest tab they take the slots of
Desert Treasure and Devious Minds, which this server never implemented.

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
   After about half a minute, or once you are down to 40% HP, he decides you're more fun alive.
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

Only Starfall Crater and Oren have content so far. The rest is ready for the next quests:
Oren says "There are people inland who'll want to meet you."

Regenerate a top-down preview with
`-Dnythera=/path/nythera.png` when running `CursedIslandGenerator`.

## Where things live

| What | Where |
|---|---|
| Ids and coordinates (NPCs 3858-3862, items 7961-7967, objects 14986-14991) | `game/content/voidrealm/VoidConstants.java` |
| Dialogue, objects, the boss scene, lantern, drops | `game/content/voidrealm/VoidRealm.java` |
| Quest journals and rewards | `game/content/quests/impl/EchoesOfTheVoid.java`, `BeyondTheVeil.java` |
| NPC / item / object definitions, island scenery, the void arena, Nythera | `tools/mapgen/VoidRealmGenerator.java` |
| Item stats | `data/cfg/ItemDefinitions.json` (7966, 7967); slots in `ItemData`, level in `ItemAssistant.getRequirements` |
| NPC stats and spawns | `data/cfg/npcDefinitions.json`, `npc.json`, `spawns.json` |

Dialogue ids 9600-9799 belong to the series. `VoidRealm.wrap` reflows every NPC and player line
to fit beside a chathead. Ilsabet and Oren borrow chatheads (from the Varrock witch and
Kaqemeex) that only have the talking animations, so their other emotes fall back to talking.

## Admin commands

| Command | Does |
|---|---|
| `::voidecho n` / `::voidveil n` | set a quest stage (no argument prints it) |
| `::nythera` | teleport to Starfall Crater |
| `::vt npc id` / `obj id x y` / `obj2 id x y` / `use objId x y itemId` / `item id` | fire the same handler a click would (testing) |
| `::vt journal 1/2` | open either journal |
| `::vt list` | list NPCs spawned for you |

## Ideas for what comes next

- The people inland Oren mentions: a settlement past the Broken Gate.
- The Hollow Scar spreading, and Hollow-touched creatures.
- A way to actually wound the Hollow King: the cape's "mark" and Hollowfang's "hunger" are
  hooks for that rematch.
