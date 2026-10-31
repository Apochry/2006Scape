# Mining, Smithing and resource changes

## Lower levels

Mining and Smithing requirements for the original content are multiplied by
`Constants.MINING_SMITHING_LEVEL_SCALE` (0.6). Change that one number to retune them.

- **Ores, pickaxes and bars** are also rounded to a multiple of 5 (`Constants.roundedLevel`).
- **Smithed items** keep their 1-level steps (`Constants.scaledLevel`), so each new item is still a step up.
- The skill guides and anvil menus show the new levels.

| | Ore | Bar | Items |
|---|---:|---:|---:|
| Copper / tin / bronze | 1 | 1 | 1-11 |
| Blurite | 5 | | |
| Iron | 10 | 10 | 9-20 |
| Silver | 10 | 10 | |
| Coal / steel | 20 | 20 | 18-29 |
| Gold | 25 | 25 | |
| Mithril | 35 | 30 | 30-41 |
| Adamantite | 40 | 40 | 42-53 |
| Runite | 50 | 50 | 51-59 |
| **Dragon** | **60** | **60** | **60-72** |

Pickaxes: bronze/iron 1, steel 5, mithril 15, adamant 20, rune 25. Pure essence comes from level 20.
How long a rock takes to mine still follows its original level.

## Dragon metal

- **Dragon ore** (item 7959) comes from **dragon rocks** (object 14985). There are six in the Dragonvein
  cavern on Skullbrine Isle: enter Skull Rock's mouth, then go west from the portal chamber.
- **Dragon bar** (item 7960): use the ore on any furnace with 4 coal (level 60, 65 XP). It smelts all your ore.
- **Smithing**: use a dragon bar on an anvil.

| Item | Level | Bars |
|---|---:|---:|
| Dagger | 60 | 1 |
| Axe | 61 | 1 |
| Mace | 62 | 1 |
| Med helm | 63 | 1 |
| Scimitar | 64 | 2 |
| Longsword | 65 | 2 |
| Sq shield | 66 | 2 |
| Battleaxe | 68 | 3 |
| Chainbody | 69 | 3 |
| 2h sword | 70 | 3 |
| Platelegs / plateskirt | 72 | 3 |

Everything is in `DragonMetal.java`. The new definitions are made by `CursedIslandGenerator`
(runite recoloured red, see `docs/SKULLBRINE_ISLE.md` to regenerate).

## Faster resources

- Rocks and trees grow back `Constants.RESOURCE_RESPAWN_SPEED` (3) times faster.
- When a rock or tree you're harvesting runs out, you wait by it and start again as soon as it's back
  (`RespawnWait`). This stops when your inventory is full, or when you move or start something else.
