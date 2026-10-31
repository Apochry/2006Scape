# Void saga quest weapons (example models)

Five quest-reward weapons, hand-built in code in the old-school style. Each one has an **inventory model** and a
**wielded model**. Every weapon copies its placement, scale and bone label from a cache item of the same type, so the
existing item camera and attack/stand/walk animations work with it unchanged.

| File | What it is |
|---|---|
| `src/QuestWeapons.java` | The five weapons: one `xxxFrame()` builder each (with a design comment), the per-type `Pose` values read off the cache, and `place()` / `wielded()` / `inventory()`. |
| `src/WeaponSheet.java` | Renders everything and checks the encode/decode round trip. The renderer is a generalised copy of `Preview.draw`, plus the 317 client's item-sprite camera. |
| `src/Preview.java` | Copy of the base Preview, compiled so `Preview.rgb` is available. |
| `tools/*.java` | Throwaway probes used for the research: `Find` (item defs and their models, by name), `Dump` (vertices, skins, colours), `Kits` (identity kits), `Bases` (animation skeletons), `Ops` (item opcodes). |
| `sheet.png` | Contact sheet, one row per weapon. Columns: 32x32 icon through the item camera (ours above the original's) · inventory model from the top, the original's next to ours with the original's wireframe drawn over ours · wielded side view, the original next to ours at the same scale with the hand ringed in red and the original's wire over ours · 3/4 front and back · on the default male body. |
| `png/<name>.png`, `png/<name>_big.png`, `png/<name>_icon.png` | Per weapon: the icon plus three angles, large close-ups, and the icon alone. |
| `roundtrip.txt` | Vertex and face counts, colours, skins, encoded sizes, round-trip result. |

To rebuild, run `./build.sh` (javac against the local `server.jar`, then `WeaponSheet cache png`). The cache here is a
private copy, and nothing in it is written.

## The weapons

| Weapon | Based on (item / inv model / wield model) | Bone | Tris (wield / inv) | Verts | Colours |
|---|---|---|---|---|---|
| Starfall halberd | Rune halberd 3202 / 2791 / 555 | 50 | 212 / 212 | 130 | 6 |
| Rimecleaver (battleaxe) | Rune battleaxe 1373 / 2778 / 550 | 50 | 140 / 140 | 90 | 7 |
| Hollow King's sceptre (mace) | Rune mace 1432 / 2464 / 502 | 27 | 274 / 274 | 163 | 6 |
| Duskyew shortbow | Magic shortbow 861 / 2562 / 512 | 27 | 128 / 128 | 88 | 5 |
| Staff of the caged star | Magic staff 1389 / 2810 / 534 (+567 orb) | 50 | 286 / 286 | 193 | 6 |

All ten models round-trip through `ModelData.encode()` → `ModelData.decode()` without error, and the decoded vertices, skins, faces,
colours and render types are all identical. Every face is flat-shaded (render type 1).

### Starfall halberd
*"Dorrin beat it out of the fallen star. It still rings when the wind catches the blade."*
A long charcoal-violet haft with two grip wraps. On top is a broad crescent axe blade of pale starmetal. It hangs on the
same side as the rune halberd's blade and has a bright honed bevel. There is also a back hook and a diamond top spike.
A violet star-shard is set through the blade as the one accent colour.
Suggested reward for *Hold the Gate* (or crafted from Starmetal at Dorrin's forge). Requirements: 65 Attack, 60 Strength.
Two-handed, 2-tile reach like halberds. Bonuses: stab +58, slash +78, crush 0, strength +76. It is a little better than
the rune halberd and below the dragon halberd's 60 Attack tier in damage, but it needs no quest-locked dragon access.

### Rimecleaver
*"Its edge has never thawed. Neither will the things it cuts."*
A frost-blackened haft with ice-blue wraps and a blunt ice-crystal pommel. One side of the head has a crescent bit of
rime ice with a white frosted bevel; it sits where the rune battleaxe's longer bit is. The other side has a crest of
three icicles, and an icicle spike tops the head.
Suggested reward from the Rimeheart / rime altar storyline. Requirements: 60 Attack. Bonuses: slash +54, crush +46,
strength +68. Suggested passive effect: 1 in 8 hits chills the target (−1 Attack for 30 s, no stacking).

### Hollow King's sceptre
*"The crown he no longer has a head for."*
A black-iron rod with a gold pommel and band. The head is a crown of five gold flanges, the crown's points, around a
hollow violet void where the jewel should be.
Reward for *The Hollow King*. Requirements: 60 Attack, 40 Prayer. A mace, so it gets a prayer bonus. Bonuses: stab +18,
crush +62, strength +56, prayer +6, magic +4.

### Duskyew shortbow
*"Cut from a yew that grew in the Scar and came up black."*
Faceted black-violet limbs drawn in the magic shortbow's shallow double curve, with recurved tips capped in starmetal.
It has a ghost-teal grip and a pale string. The inventory model is bent deeper, as the cache's own bows are, so the
icon reads as a bow.
Suggested reward for helping Gatehold's watch. Requirements: 60 Ranged (fires up to rune arrows). Bonuses: ranged
+72. Speed is the same as the magic shortbow.

### Staff of the caged star
*"A star the Hollow King pulled out of the sky, still trying to get home."*
A thick void-yew staff with a starmetal ferrule and collar. Its head is a cage of four starmetal prongs around a
violet three-axis star with a white core. The cage reaches exactly as far as the magic staff's orb does.
Reward for the Starless storyline. Requirements: 60 Magic. Bonuses: magic attack +14, magic defence +14, crush +7.
Autocasts the standard spellbook like a magic staff. Suggested passive effect: the second rune of any Bind or Snare
spell is free.

## How placement was matched (read this before wiring)

**Wielded models.** Every existing wielded weapon model has the same frame:
- It lies in the y-z plane at x ≈ −30 and points forward (−z) and up (−y).
- The grip ring sits on the right hand at **(−30, −97, 0)**. Hand kit model 176 has its right-hand vertices there,
  on label 27.

For each type I read four things off the comparable WIELDED model: its butt point, its tip point, which side its blade
or belly is on, and the label of all its vertices. These are stored in `QuestWeapons.HALBERD/BATTLEAXE/MACE/SHORTBOW/STAFF`,
and the source comment lists the exact coordinates. The weapon is built in a frame where `s` runs butt→tip and `w` runs
toward the blade side. It is then placed so that its s = 0..L axis lands exactly on the original's butt→tip line, and
the grip falls on the hand. The side views on the sheet show our models inside the original's wireframe, with the hand
ring on the grip.

**Bone labels.** I decoded the player skeleton: 116 frame files share one base with 117 transform groups. It shows that:
- **Label 27** (right hand) is in the arm and hand groups (29–35) and its own origin group (33).
- **Label 50** is in all of those groups too, so it follows the hand. It also has groups of its own: 47 and 48
  (rotate), 87 (origin) and 88 (scale).

So label 50 is a *weapon* bone that the halberd, staff and battleaxe animations can swing separately from the hand. The
rune longsword, rune mace, rune spear and magic shortbow use 27. Dragon weapons use 50. Every vertex of our models
carries the label of its base item: 50 for the halberd, battleaxe and staff, 27 for the mace and bow. As a result, the
animations each base item uses move ours identically.

**Inventory models.** These rest on the ground plane (y ≤ 0). Each one copies the original inventory model's
butt and tip placement and blade side, scaled the same way: the cache's inventory models are about 0.62× their
wielded size for polearms and staffs, about 0.66× for bows and about 1× for short weapons. Because of this, the base
item's camera opcodes can be copied unchanged: 4 (zoom), 5 and 6 (rotations), 7 and 8 (offsets). The icons on the
sheet are rendered through the client's own item-sprite math (`ItemDef.getSprite` → `Model.method482`) with the base
item's values, and they sit in the slot the way the originals do.

## Wiring one into the game

Follow the pattern `VoidRealmGenerator` already uses for the Barrowsteel longsword and the Emberbrand:

1. Copy `QuestWeapons.java` into `com.rs2.tools.mapgen`. It only uses `ModelData`. Then pack both models:
   ```java
   QuestWeapons.Weapon w = QuestWeapons.ALL[0]; // Starfall halberd
   int inv = forge.pack(w.inventoryModel()), held = forge.pack(w.wieldedModel());
   ```
2. Clone the base item's definition: `g.item(items, <base id>, NEW_ID)`, with base ids halberd 3202, battleaxe 1373,
   mace 1432, shortbow 861, staff 1389. Then set the name, examine text and cost, and:
   - `remove(40)`. Every base item has one recolour pair (rune or magic colouring), and our colours are baked into the
     model.
   - `shorts(1, inv)`.
   - `set(23, {held>>8, held, 0})` (male, offset 0) and `set(25, {held>>8, held, 6})` (female). Keep the base's female
     y-offset byte, which is **6** for all five bases. Side finding: the rune longsword (1303) also has female offset 6,
     but the existing Barrowsteel and Emberbrand wiring writes 0 there. On female characters those swords probably sit
     6 units off where the rune longsword sits.
   - **Staff only:** `remove(24).remove(26)`. On the magic staff these add the orb model 567 as a second wielded
     model, and our staff head replaces it.
   - Keep opcodes 4–8 (camera) and 36 as they are.
3. Server side, give the new item id the base item's weapon data: the same weapon interface, attack, stand, walk and
   run animations, attack speed, and 2-handed flag for the halberd and the bow. Also give it the base's equipment slot.
   Add the bonuses suggested above. The halberd also needs the halberd 2-tile reach.
4. Call `forge.finish()` once after packing, as the existing generator already does.

No new animations, skeletons or textures are needed.

## Style notes and honest limits
- 128–286 triangles, with 5–7 colours each, all muted. There is one accent per weapon: a violet shard, frosted edges,
  the void core, a teal grip, and the star.
- Blades use a "bevelled plate": a flat face plus a bright honed bevel band. This is the classic two-tone old-school
  blade, and it reads as an edge even at 32 px.
- The renders use Preview's lighting, not the client's. In game the shading will be a little different and the hues the
  same.
- I could only check the placement statically. The side views and body renders show it matches the original at rest.
  I did not play the animations, but because the axis, grip and bone label are identical, they should hold the
  weapons the way they hold the originals.
- The halberd and staff are a touch busier than 2006 weapons; their counts are 212 and 286 triangles. If you need
  them lighter, drop the second grip wrap or turn the prongs into 3-sided tubes. Either saves about 40 triangles.
