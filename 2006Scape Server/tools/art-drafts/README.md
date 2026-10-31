# Art drafts (now wired in)

**These are in the game now.** `ArmourForge`, `QuestWeapons`, `VoidEffects`, `FxAnim` and `AnimCache` live in
`src/main/java/com/rs2/tools/mapgen`, and `docs/VOID_QUESTS.md` ("The saga's gear") says where each piece drops. The
copies here are the research versions, kept with their previews.

Example models in the old-school style for the Void saga. Each set was built and previewed outside the game. The
models round-trip through `ModelData`; the armour was animated with the cache's own frames.

- `armour/`: the Gatehold Warden and Aurel's Royal Guard sets (helm, platebody, legs, kiteshield, boots, gauntlets),
  rebuilt from the rune worn models with matching skin labels.
- `weapons/`: Starfall halberd, Rimecleaver, Hollow King's sceptre, Duskyew shortbow, Staff of the caged star.
- `effects/`: void bolt and impact, the Emberbrand's Heartfire Arc, starfall marker, falling star, eclipse ring and
  rime burst. Each is laid out to be driven by an animation already in the cache.

Each folder's README covers lore, suggested stats and exact wiring steps. The sources are in package
`com.rs2.tools.mapgen`; to wire one in, move it into `src/main/java/com/rs2/tools/mapgen` and pack its models from
`VoidRealmGenerator` the way the Emberbrand is packed.
