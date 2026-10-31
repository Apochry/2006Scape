# Void saga special effects (spotanims / "graphics")

Seven old-school-style effects, each a low-poly model with vertex and face skin groups. For each one there are three things:

- an **authored frame script** (a skin list plus translate, rotate, scale and alpha ops per frame, in exact 317 semantics);
- a **mapping onto an existing cache animation**, so it can go into the game with no new frames;
- **sprite strips and GIFs** of both.

All of it is verified by a real round trip through a patched copy of the cache.

| effect | gfx role | model v/f | reuses existing seq (frame file) | precedent spotanim | authored script |
|---|---|---|---|---|---|
| Void bolt | projectile | 35 / 55 | **663** (214), 8 fr, loops | fire blast projectile gfx 130 | 24 slots, 8 fr |
| Void bolt impact | target, h=100 | 54 / 80 | **693** (186), 8 fr | fire wave impact gfx 157 | 8 slots, 8 fr |
| Emberbrand special "Heartfire Arc" | on wielder | 36 / 96 | **1063** (247), 18 fr | dragon dagger spec gfx 252 | 10 slots, 9 fr |
| Starfall marker | ground tile, h=0 | 75 / 72 | **1959** (521), 18 fr | ice rush impact gfx 361 | 13 slots, 8 fr (loop) |
| Falling star impact | ground tile, h=0 | 103 / 136 | **2597** (657), 13 fr | gfx 437 (falling column) | 8 slots, 10 fr |
| Eclipse ring (closes in) | target, h=0 | 130 / 192 | **660** (202), 8 fr, *inverted shards* | strike impacts gfx 92/95/98/101 | 18 slots, 10 fr |
| Rime burst | target, h=100 | 50 / 60 | **660** (202), 8 fr | strike impacts gfx 92/95/98/101 | 16 slots, 10 fr |

Every model passes `ModelData.encode()` then `decode()` with an identical result (vertices, faces, colours, render types, priorities, alphas, vertex skins and face skins). See `render/report.txt`.

## Files
- `src/VoidEffects.java`: the seven models and their authored scripts, with a design comment for each.
- `src/FxAnim.java`: applies a 317 frame to a ModelData exactly as `Model.method470/472` does. Origin, translate, rotate (client sine table and rotation order), scale /128 and alpha (+x*8, clamped) are all handled, and every frame starts from a fresh copy of the base model. It also holds the **idx2 frame-group encoder**, the inverse of `Class36.method529`. Authored scripts are pushed through encode and then the client's decode before they are rendered.
- `src/AnimCache.java`: decoders ported 1:1 from the client for `spotanim.dat`, `seq.dat`, idx2 frame files and skin lists (`Class18`).
- `src/FxRender.java`: the Preview renderer plus per-face alpha (client blend `src*(256-a)+dst*a`), a priority tie-break, a brightness boost and a ground grid. `src/FxMain.java` renders the strips and views and writes the report. `src/RenderGfx.java` renders any cache spotanim through its seq.
- `src/FxWire.java`: wires everything into **`cache_patched/`** (a copy) by both routes, then reads it all back.
- `src/DumpGfx.java` and `src/DumpSeq.java` list spotanims and sequences. Their output is in `gfx_dump.txt`, `gfx_compact.txt` and `seq_dump.txt`, covering all 666 spotanims and every seq they use.
- `render/<key>_views.png` shows the base pose from 3 angles. `render/<key>_strip.png` / `.gif` is the authored script. `render/<key>_reuse_strip.png` / `.gif` is the same model driven by the existing cache seq. `render/<key>.frames.dat` is the encoded idx2 file (uncompressed).
- `render/from_patched_cache/cache_gfx666..679.png` are rendered purely from the patched cache copy.
- `sheet.png` is the contact sheet: for each effect, its views, the authored strip and the reuse strip.
- `study/` holds the cache effects I studied (gfx 130, 101, 252, 437, 361) rendered through my simulator. They match what you see in game.

Build and run:
```
javac -cp server.jar -d out ../base/src/{ModelData,Preview,ModelForge,CacheEditor,ConfigDefinitions}.java src/*.java
java -cp out:server.jar com.rs2.tools.mapgen.FxMain cache render
python3 make_gifs.py
cp -r cache cache_patched && java -cp out:server.jar com.rs2.tools.mapgen.FxWire cache_patched
```

## Does ModelData support face alpha and priorities? Yes. A few rules matter for effects
`ModelData` already decodes and encodes per-face **alpha** (footer flag, one byte per face), per-face **priority** (footer byte 255 means a per-face list; otherwise one global value), **face skins** and **vertex skins**. Nothing is missing for effects. Rules taken from the client source:
- Alpha runs from 0 (opaque) to 255 (invisible). An alpha frame op (skin type 5) adds `x*8`, so `A(32)` hides a group.
- Alpha ops act on **FACE skins**, not vertex skins, so effect faces need `faceSkins` set. The helper `VoidEffects.tag()` sets face skin, alpha and priority together.
- The client only animates alpha if the model **has an alpha array**, which means at least one face with non-zero alpha (`Model.method472`: `anIntArray1639 != null`). `encode()` drops the array when every alpha is 0, and then fade ops silently do nothing. All my models have translucent faces.
- In this client, `Class36.method532(frame)` returns `frame == -1`, so the alpha array is always copied per frame and alpha animation works.
- Priorities run 0 to 11. Jagex spell effects use 10 and 11 (model 3082 is all 11, the dds slash 3478 is all 11) so they draw over the body. I used 0 to 2 inside an effect, and 10 for the slash so it draws over the wielder.
- Rotation values run from 0 to 255 for a full turn, applied about the last origin as roll(z), then pitch(x), then yaw(y). Scale is out of 128.
- A spotanim **must have a seq** if it is spawned as a still graphic. `Animable_Sub3` dereferences `aAnimation_407` without a null check, and only projectiles (`Animable_Sub4`) tolerate seq -1.

## How the reuse works: wiring in without new frames
A 317 animation frame does not name vertices. It names **labels** through the frame file's skin list ("slot k: type T acts on labels [..]"). Any model whose vertex and face skins use the same labels with the same *meaning* is animated correctly by the existing frames. For each effect I picked an existing sequence with a clean, generic skin list, read its frames (`seq_dump.txt`), and built the model's labels to match:

- **Void bolt → seq 663** (fire blast projectile). Label 0 is the core gem (rotate, scale pulse 80-144%). Label 1 is the shell (counter-rotate). Labels 2-4 are tail rings and 5 is the tail tip; they ripple and translate +z in a staggered loop. Label 6 is the tail root, with origin only. Face skins 2-8 fade together. The tail is along **+z** and the head toward -z, the same as model 3087.
- **Void impact → seq 693** (fire wave impact). Labels 0-9 all scale together from 45% to 214% while face skins 0-6 fade in and out. Any geometry labelled 0-9 with face skins 0-6 works: core is 0, spikes are 1-6, halo is vertex 7 / face skin 0.
- **Heartfire Arc → seq 1063** (dragon dagger special). This sequence only animates alpha: face skins 0-7 are revealed in order and fade behind the sweep (0→3, then 4→7). The arc is a crescent ribbon of 8 segments with face skin = segment index in sweep order, placed in front of the wielder at -z like model 3478. It is merged with the player model, so it turns with the player.
- **Starfall marker → seq 1959** (ice rush). Label 0 is the ground sigil, which grows from 12% to 94% while fading. Labels 1, 2 and 3 are star motes that pop up in turn (scale 0→32→64→96→0).
- **Falling star → seq 2597** (gfx 437). Label 9 is the falling object: it translates from 583 above to 75 below its base and spins, so the star sits at y≈-84 as in model 9250. Labels 0-8 are rings of a light column at exactly 9250's heights (-2, -13, -34, -69, -122, -177, -240, -328, -494). Face skins 0-8 are the column segments, which light up top to bottom as the star passes. Label 0 / face skin 0 also carries the ground splash, which the seq scales to 137-148% and flashes on impact.
- **Rime burst → seq 660** (strike impact). It copies model 3082 label for label. Label 0 is the core (scaled 30%→300%, faded). Label 1 is the shell. Labels 2-9 are shards pushed outward 10→40 units in fixed compass directions: 2 to -z, 3 to +x-z, 4 to +x, 5 to +x+z, 6 to +z, 7 to -x+z, 8 to -x, 9 to -x-z. Each shard points along its label's push direction.
- **Eclipse ring → seq 660, inverted.** No cached seq shrinks a ring uniformly, so each of the 8 arc segments gets the shard label whose *outward* push points at the **centre** from where the segment sits. For example, the arc at +x gets label 8 (pushed -x). The "burst outward" frames therefore make the ring **contract**, from radius 68 to 28, while the black sun (label 0) swells. Frame 7 snaps back to the full ring at half alpha, which works as the after-image. The mapping is printed in `VoidEffects.eclipseRing().labelMap`.

**To wire one in (route A), you only need one model plus one spotanim entry:**
1. Pack the model into idx1 with `ModelForge.pack(effect.model)`, then call `finish()`. That writes model_version, model_crc and model_index. Allocate the id at wire time: the other agents' models also take the "next free" id.
2. Append a record to `spotanim.dat` in config archive idx0 #2 and bump its leading u16 count. The record is `01 <u16 model> 02 <u16 seq> [04 <u16 resizeXY>] [05 <u16 resizeZ>] 07 <u8 bright> 08 <u8 shadow> 00`. The values are:

| effect | seq | resize | bright/shadow |
|---|---|---|---|
| void bolt | 663 | – | 60/60 |
| void impact | 693 | – | 60/60 |
| arc | 1063 | – | 80/80 |
| marker | 1959 | – | 70/70 |
| falling star | 2597 | – | 90/90 |
| eclipse | 660 | 160/160 | 60/60 |
| rime | 660 | 192/192 | 60/60 |

   No recolours are needed because the models carry their own palette.
3. Server side, use the new gfx ids like any other. Use the projectile packet for the void bolt. On the target, use height 100 for the void impact and rime burst. Use height 0 or a still-graphic-at-tile for the marker, falling star and eclipse. Put the arc on the player at height 0 (its geometry is already at chest height) together with the Emberbrand special attack animation.

`FxWire` did exactly this on `cache_patched/`, producing **gfx 666-672** (models 14942-14948). The readback shows every frame of each reused seq loaded, and `render/from_patched_cache/` shows them animating from the cache alone.

## Route B: authored frames (optional)
The authored scripts give Void-specific timing and moves: a spinning orb with a wagging tail, spikes that punch out and spin, a sweep with roll follow-through, a pulsing and rotating looped marker, a star that accelerates down with a trail and splash, a ring that spirals shut, and shards that fall as they fly. To ship them:
1. Encode them with `FxAnim.encode(skins, keys, firstFrameId)` and write each file to a new idx2 file with `CacheEditor.writeVersionedFile(2, file, data, 1, {anim_version, anim_crc})`.
2. Frame ids are **global**. `Class36` sizes its table from `anim_index.length/2`, which is 33568 now. The client never reads anim_index's values, only its length. So assign ids from 33568 upward and grow `anim_index` by 2 bytes per new frame.
3. Append `seq.dat` records: `01 <u8 n> n×(<u16 frame> <u16 65535> <u16 delay>) 00`, and bump the count. Delays are in 20 ms client cycles.
4. Point spotanims at the new seqs. The client requests every anim file at startup ("Requesting animations"), so no on-demand changes are needed.

`FxWire` did this too: new frames 33568-33630 in idx2 files 1044-1050, seqs 3997-4003 and **gfx 673-679**. Everything read back correctly. The dev client **and** the server must use the patched cache for either route.

## Lore and suggested use
- **Void bolt / impact.** "A sliver of the Starless, flung." A Void Magic spell at strike to bolt tier, around level 50 Magic, using Void and Chaos runes, max hit 12. Cast with the standard spell animation (gfx-cast slot free; reuse gfx 129's cast hand recoloured violet: `recol 960 -> hsl(50,7,48)`).
- **Heartfire Arc.** The Emberbrand special attack: 50% energy, one hit with +25% accuracy and +20% damage. If it hits, the wielder heals 10% of the damage. "The Ember remembers every king who held it."
- **Starfall marker + falling star.** A boss telegraph for King Aurel or the Hollow King's astronomers. The marker plays for about 2 ticks on the target tile, then the star lands: 20-40 typeless damage to anyone still on the tile, and it leaves a starmetal shard drop. Players can also use it as a skilling event.
- **Eclipse ring.** The Starless's signature attack. It plays for about 1.5 ticks on the target, then binds for 5 s and drains Prayer. "When the Starless turns its face toward you, the sky closes like an eye."
- **Rime burst.** The Rime Ice spell or a weapon proc: freezes for 5 s and does 0-15 damage. "Cold enough to crack the air it freezes."

## Honest notes
- The renderer is flat-shaded and painter-sorted. It approximates spotanim brightness and does not model the client's priority-10/11 interleave. The animation itself (`FxAnim`) is client-exact.
- Two reused seqs carry baggage from their original effect. 1063 opens with 4 hidden frames, about 16 cycles, to sync with the dds attack animation; the Emberbrand special animation should have a similar wind-up. Seq 660's final frame snaps back to the base pose. Both are visible in the reuse strips.
