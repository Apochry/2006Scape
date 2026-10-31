# Quest armour: Gatehold Warden set (+ Aurel's Royal Guard recolour)

Two quest armour sets for the Void saga. They share the same geometry and use different palettes:

* **Gatehold Warden**: blackened starmetal, ember-gold trim, a void-violet tabard, and the Ember stone set in the Gatehold gate on the chest and shield.
* **Aurel's Royal Guard**: polished pale starmetal, bright gold and royal violet, with a visor that glows ember-gold.

Each set has a full helm, a platebody (body plus sleeves), platelegs, a kiteshield, boots and gauntlets. Every piece has a worn (male) model and an inventory model.

`sheet.png` is the contact sheet:

* **Top two rows:** the full outfit on a reassembled player, for both palettes. The views are front, 3/4, shield side and back. The posed views are walk (seq 819, frames 2 and 6), slash (seq 451) and shield block (seq 1156). They come from the real cache animation frames applied by `Anim.java`.
* **Below that:** each Warden piece from the front, 3/4, side (the outer face for the shield) and back. Then come the inventory view (using the rune item's own rotation opcodes) and a **skin-label map**, where each colour is one animation label. The Royal Guard pieces follow.

The `png/` folder holds other checks:

* `invcompare.png`: rune inventory models next to the new ones, at the same angles.
* `animtest.png`: the rune outfit animated, to check the animator itself.
* `rune.png`: the untouched rune worn models.
* `report.txt`: counts and skin labels.

## How it was made (and why it animates)

The worn models are **not** built from scratch. Each one is the cache's own worn rune mesh, decoded with `ModelData.decode`. I re-dyed it, then added new geometry with `ArmourForge.attach`. That method gives every new vertex the skin label of the **nearest original vertex**, in one of three modes:

* `PER_VERTEX`: each vertex copies its nearest label. Used for cloth, which should bend.
* `RIGID`: the whole piece takes the most common nearest label, so a plate can never tear across two bones.
* `RIGID_PER_PART`: the same rule applied to each connected part, for example one pauldron per shoulder.

To check the result, `Anim.java` decodes `seq.dat` and all idx2 frame files (skeleton plus frames) and deforms the merged player model exactly the way the client's `Model.applyTransform` does (origin, translate, rotate, scale). The posed renders in `sheet.png` come from this. The pauldrons ride the raised sword arm, the tabard swings with the stride, the shield follows the block, and nothing tears.

The default male body was rebuilt from the identity kits in `idk.dat` (head 230, jaw 249, torso 292, arms 151, hands 176, legs 254, feet 181). For the full outfit, only the hands kit is still visible, because the full helm and platebody hide the rest.

### Skin labels used

The front of the player is −z, the player's left is +x, and y is down.

| piece | base mesh (rune item → worn model) | labels kept from base | labels on NEW geometry |
|---|---|---|---|
| Full helm | Rune full helm 1163 → 218 | 1 skull, 2 neck, 3 jaw | wings, brow chevron, ember stone: **1** (rigid, 71 verts) |
| Platebody | Rune platebody 1127 → 306 | 2, 4, 5, 8, 21, 25 | pauldrons: **21** (right upper arm) / **25** (left upper arm), 38 verts each, rigid per part; chest gate emblem: **8** (chest, 61 verts) |
| Sleeves (worn #2) | 1127 → 164 | 17, 19, 20, 22, 23, 26 | recolour only (gold cuff band) |
| Platelegs | Rune platelegs 1079 → 268 | 5, 29–42 | knee cops: **33** (right knee) / **36** (left knee), rigid; tabard front: **29, 39, 43, 44** and back: **29, 43, 44**, per vertex |
| Kiteshield | Rune kiteshield 1201 → 486 | 28 (left hand) | gate emblem and Ember: **28** (43 verts) |
| Boots | Rune boots 4131 → 4954 | 0, 32, 38, 45–48 | toe caps: **45** / **46** (toes) |
| Gauntlets | default hands kit 176 (this cache has no rune gloves) | 19, 22, 27, 28 | flared cuffs: **19** / **22** (cuff/wrist), rigid per part |

The tabard takes its labels from the **rune plateskirt (worn 266)**, not from the platelegs. That model is the only one carrying the skirt labels 43 (left half), 44 (right half) and 57 (centre). Its lower edge therefore moves with the skirt animation channels, so the tabard swings like a skirt instead of being split between the two knee bones.

Draw priorities were picked to match the base meshes:

| part | priority | base it sits on / matches |
|---|---|---|
| helm add-ons | 7 | helm is 6/7 |
| chest emblem | 4 | torso is 3; arms (10) still draw over it |
| pauldrons and cuffs | 10 | same as arms; depth-interleaved |
| knee cops and tabard | 2 | legs are 1; the torso (3) overlaps the tabard's top |
| shield emblem | 11 | same as the rune kite's outer field |
| toe caps | 0 | boots |

New faces use smooth (gouraud) shading, like the rune meshes.

## Poly counts (every model passes `encode()` → `decode()` with identical vertices, skins, faces, colours and priorities)

| model | verts | tris | colours |
|---|---|---|---|
| Full helm (worn / inventory) | 158 | 257 | 6 |
| Platebody (worn) | 251 | 397 | 7 |
| Platebody sleeves (worn #2) | 48 | 76 | 2 |
| Platebody inventory (body + sleeves) | 299 | 473 | 7 |
| Platelegs (worn / inventory) | 196 | 311 | 6 |
| Kiteshield (worn / inventory) | 99 | 159 | 6 |
| Boots (worn / inventory) | 73 | 125 | 4 |
| Gauntlets (worn / inventory) | 88 | 140 | 3 |

For comparison, the rune bases have 157, 196 + 76, 153, 102 and 113 tris. The body and legs are above the brief's 300-tri guideline because they *contain* the full rune mesh. The added parts cost about 100–200 tris per piece. The Royal Guard models have identical counts.

## Lore and suggested stats

| item | lore / examine | suggested stats |
|---|---|---|
| **Warden's full helm** | "The helm of a Gatehold Warden. Its gilded wings remember the gate-guardians who never slept." | def 60 · ~rune full helm +2 all, prayer +1 |
| **Warden's platebody** | "Starmetal plate, blackened in the Ember's glow. The gate on its breast has never been opened from within." | def 60 · rune platebody stats, magic def −4 instead of −6 |
| **Warden's platelegs** | "Gilded knee-guards and a violet tabard: the colours of those who hold the Gatehold." | def 60 · rune platelegs |
| **Warden's kiteshield** | "The gate of Gatehold, with the Ember burning in its doorway. Wardens say the Starless can't look at it." | def 60 · rune kite +3 all; set effect idea below |
| **Warden's boots / gauntlets** | "Charcoal leather and starmetal plate, trimmed in ember gold." | def 40 · rune boots / +4 all, str +2 |

The **Royal Guard** set reuses these items in Aurel's colours ("Polished starmetal of King Aurel's own guard."). It could be a later-quest variant at Defence 65, or a cosmetic reward.

* **Requirements:** completion of the Gatehold quest of the Void saga, plus 60 Defence.
* **Set-effect idea:** wearing all four main pieces reduces damage taken from Starless / void creatures by 10%.

## Wiring it into the game (not done; the live repo and cache were not touched)

1. **Pack the models.** In a `ModelForge`-style run, call `ArmourForge(new ModelForge(cache))`. For each palette (`WARDEN` / `ROYAL`), call `forge.pack(...)` on:
   * `helm(p)`
   * `body(p)[0]` (the body) and `body(p)[1]` (the sleeves)
   * `legs(p)`, `shield(p)`, `boots(p)`, `gauntlets(p)`
   * and on the inventory models: `inventory(helm, 218, 2813)`, `inventory(mergeAll(body, arms), new int[]{306, 164}, 2605)`, `inventory(legs, 268, 2582)`, `shieldInventory(kite, -1104·2π/2048, 344·2π/2048)`, `inventory(boots, 4954, 5037)`, `gauntletsInventory(gloves)`

   Then call `forge.finish()`. `Build.java` shows the exact calls.
2. **Item definitions.** Clone each rune item with `ConfigDefinitions` (obj table) and override:
   * opcode 1: the new inventory model
   * 23: the new worn model, offset 0
   * 24: the sleeves (platebody only)
   * name and examine

   Keep 4–8 (zoom, rotation, offsets) from the rune item, because the inventory models are placed in the rune inventory frame on purpose (`png/invcompare.png`).

   Replace opcode 40 with `{61→steel, 926→plume}`. The new models no longer contain those placeholder colours, so the recolour only affects the full helm's chathead models (opcodes 90/91 = 56/116). Those can stay as the rune ones.

   | new item | clone of |
   |---|---|
   | helm | 1163 |
   | platebody | 1127 |
   | platelegs | 1079 |
   | kiteshield | 1201 |
   | boots | 4131 |
   | gauntlets | a gloves item, e.g. 1059 leather gloves; give it a gloves-style inventory zoom (~opcode 4 = 600–800, opcode 5 = 420) |
3. **Female models (opcodes 25/26).** These were not built. Run the same pipeline on the female rune meshes:

   | piece | female rune worn model |
   |---|---|
   | helm | 394 |
   | body | 468 |
   | sleeves | 344 |
   | legs | 432 |
   | kite | 486 (same as male, offset 6) |
   | boots | 5031 |

   As a stop-gap, point opcode 25 at the male model.
4. **Server.** `ItemData` decides hiding by name suffix:
   * a name ending in **"full helm"** is a full mask (hides hair and beard)
   * a name ending in **"platebody"** is full body (hides the arms kit)

   So the names should be exactly "Warden's full helm", "Warden's platebody", and so on. Also:
   * add bonuses and weight to `data/cfg/ItemDefinitions.json`
   * add equip slots and requirements wherever 2006Scape's other rune pieces are configured
   * keep the gauntlets in the hands slot

## Files

* `src/ArmourForge.java`: every model (one design comment each) plus the helpers: skinning/attach, conforming decals (projected onto the base surface by ray-casting), blades, domes, rims, inventory re-framing.
* `src/Anim.java`: the 317 `seq.dat` and idx2 frame/skeleton decoder plus the client-exact frame applier.
* `src/Build.java`: builds both palettes, verifies round trips, prints counts and labels (`png/report.txt`), renders `png/pieces.png` and `png/outfit.png`. `sheet.png` stacks the two.
* `src/Sheet.java`: contact sheet around a copy of `Preview.draw` (`src/Preview.java`).
* Tools: `ArmourDump` (item / identity-kit opcodes), `ModelInfo` (labels, colours and priorities per model), `Dbg` (where a colour sits), `RuneLook`, `AnimTest`, `InvCompare`.

Build and run:

```
javac -cp server.jar -d out src/*.java && java -cp out:server.jar com.rs2.tools.mapgen.Build cache png
```

`cache/` is a private copy of the cache and was never written to.

### Known caveats

* Preview is a painter's-algorithm renderer. In some 3/4 views a sliver of the shield emblem shows through the shield's inner face. The client sorts the kite's faces by priority and depth (emblem 11 = the kite's outer field), so this should not happen in game, but check it in game.
* The pauldrons are rigid on the upper-arm labels. When the arm is raised high (slash) they lift off the collar slightly, as OSRS pauldrons do.
