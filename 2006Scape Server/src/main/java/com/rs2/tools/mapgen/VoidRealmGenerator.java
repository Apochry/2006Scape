package com.rs2.tools.mapgen;

import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.tools.mapgen.CursedIslandGenerator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Everything for the Void quest series, run as part of {@link CursedIslandGenerator}:
 * <ul>
 * <li>new NPCs, items and objects (cloned from existing ones and recoloured),</li>
 * <li>quest scenery on Skullbrine Isle (the weeping eye, the epitaph graves, the void vein),</li>
 * <li>the void between worlds: a lone platform where the Hollow King waits,</li>
 * <li>Nythera, a new continent west of Skullbrine (7x7 regions with the sea around it).</li>
 * </ul>
 */
final class VoidRealmGenerator {

	private final CursedIslandGenerator g;
	private final Random random = new Random(0x401D);

	VoidRealmGenerator(CursedIslandGenerator g) {
		this.g = g;
	}

	/* ---------- floors (flo index + 1) ---------- */
	static final int GRASS_A = 48, GRASS_B = 49, GRASS_C = 50, GRASS_D = 63, GRASS_E = 64, FOREST_FLOOR = 57, SLOPE = 58,
			MOUNTAIN = 56, SAND_FLOOR = 68, STONE_ROAD = 10, DIRT_ROAD = 81, MUD_FIELD = 131;
	/**
	 * The void's own floors, appended to flo.dat by {@link #floors()}: one family, darkest to brightest, so every
	 * place the Scar has touched reads the same - a near-black pool, scorched violet ash, and the glassy violet of
	 * ground the rift has fused. (They replace the stock crypt magenta and pure black, which looked like holes.)
	 */
	static final int VOID_POOL = 175, VOID_ASH = 176, VOID_GLASS = 177, EMBER_SCORCH = 178;
	/** Skull Rock's eye sockets and mouth: as dark as the void pool, but warm, and never the flat black of a hole. */
	static final int SKULL_SHADOW = 179;
	static final int[][] VOID_FLOORS = { { 0x16121d }, { 0x241f2b }, { 0x4b3e6c }, { 0x3d2418 }, { 0x1e1b18 } };
	static final String[] VOID_FLOOR_NAMES = { "void_pool", "void_ash", "void_glass", "ember_scorch", "skull_shadow" };
	/**
	 * The void platform's stone: dusky slate-violet underlays (blended by the client, so the stone shades smoothly).
	 * The map format can't hold an underlay id above 174, so these take over two stock floors no map uses
	 * (cosmic_floor_softpink and fairy_grass_overlay) instead of being appended.
	 */
	static final int VOIDSTONE = 167, VOIDSTONE_DARK = 168;
	static final int[] VOIDSTONE_RGB = { 0x34304a, 0x25222f };

	/* ---------- scenery ---------- */
	static final int TREE = 1276, TREE_2 = 1278, OAK = 1281, WILLOW = 1308, MAPLE = 1307, YEW = 1309, EVERGREEN = 1315,
			EVERGREEN_BIG = 1316, FLOWERS = 1192, FERN = 1298, BUSH = 1124, BUSH_2 = 1125, BIG_BUSH = 1123, FALLEN_TREE = 1296,
			STANDING_STONE = 11782, PILLAR = 2440, PILLAR_2 = 2441, RUBBLE = 2508,
			FIRE = 2732, LOGS = 5581, CRYSTAL_SRC = 10014, CRYSTAL_DARK_SRC = 9751, COPPER = 2090,
			TIN = 2094, IRON = 2092, COAL = 2096, MITHRIL = 2102, GROUND_FLOWERS = 4735;

	/* ====================================================================== */
	/*                              DEFINITIONS                               */
	/* ====================================================================== */

	void npcs(ConfigDefinitions.Table npcs) throws java.io.IOException {
		forge = new ModelForge(g.cache);
		// a seer in deep violet robes, drawn to the island by the portal's song
		ConfigDefinitions.Definition seer = g.npc(npcs, 176, SEER_ILSABET).name("Seer Ilsabet")
				.examine("Her eyes are the colour of the portal's light.").npcActions("Talk-to", null, null, null, null)
				.set(60, npcs.decode(611).get(60)); // the Varrock witch's chathead - the cloned witch has none
		// robes and hat only: leave skin (hues 4, 7, 8, and 26, which the witch's own recolour turns into skin), lips (1),
		// hair (6) and eyes (42) alone
		tintNpc(seer, ConfigDefinitions.VOID, 4, 0.9, 6, c -> (c >> 7 & 7) >= 1 && java.util.Arrays.binarySearch(new int[] { 1, 4, 6, 7, 8, 26, 42 }, c >> 10) < 0);
		g.ghostly(g.npc(npcs, 1612, HOLLOW_WRAITH), npcs, 1612, ConfigDefinitions.VOID, 4, 0.75, 14).name("Hollow wraith")
				.examine("Something wearing the shape of a drowned man.").shorts(95, 30);
		g.ghostly(g.npc(npcs, 2263, VOIDLING), npcs, 2263, ConfigDefinitions.VOID, 6, 0.6, 8).name("Voidling")
				.examine("It squirms out of the rock like a thought you'd rather not have.").shorts(95, 9);
		// the Hollow King: a colossus of absence, cut from the same stone as TzTok-Jad but drained of all its fire
		g.ghostly(g.npc(npcs, 2745, HOLLOW_KING), npcs, 2745, ConfigDefinitions.VOID, 3, 0.42, 2).name("The Hollow King")
				.examine("Looking at it is like looking down a well with no bottom.").shorts(95, 999).shorts(97, 118)
				.shorts(98, 118);
		ConfigDefinitions.Definition oren = g.npc(npcs, 14, OREN).name("Oren the Wanderer").examine("A weathered old man in travel-stained robes.")
				.npcActions("Talk-to", null, null, null, null).set(60, npcs.decode(455).get(60)); // Kaqemeex's chathead
		// a druid's white robes, after a long road: dust-grey, with the beard left white
		oren.recolor(new int[] { 25238, 8741, 6798 }, new int[] { hsl(8, 1, 76), hsl(8, 2, 58), 127 });
		// the people of Nythera's camps (ids must stay contiguous after Oren)
		g.npc(npcs, 219, BRAM).name("Bram Netwright").examine("His hands smell of lake-weed and pipe smoke.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 5, MARTA).name("Marta Tollan").examine("She looks like she could wrestle a ram. She probably has.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 231, HOB).name("Hob Ashby").examine("A woodcutter with arms like oak limbs.")
				.npcActions("Talk-to", null, null, null, null);
		g.npc(npcs, 649, KAEL).name("Warden Kael").examine("She hasn't taken her eyes off the Scar since you arrived.")
				.npcActions("Talk-to", null, null, null, null);
		g.npc(npcs, 222, PILGRIM).name("Wandering pilgrim").examine("Walking somewhere slowly, and in no hurry to arrive.")
				.npcActions("Talk-to", null, null, null, null);
		// what the Scar does to living things
		g.ghostly(g.npc(npcs, 142, HOLLOWED_WOLF), npcs, 142, ConfigDefinitions.VOID, 4, 0.55, 4).name("Hollowed wolf")
				.examine("Its eyes are empty. Something else is looking out of them.").shorts(95, 34);
		// husk and ravager are built on human rigs: the zombie's few animations fling its limbs about
		g.ghostly(g.npc(npcs, 2, HOLLOW_HUSK), npcs, 2, ConfigDefinitions.VOID, 3, 0.6, 6).name("Hollow husk")
				.examine("Whoever this was, the Scar emptied them out and kept walking them around.").shorts(95, 41);
		// Gatehold, the quarry, the inn and the cove
		g.npc(npcs, 17, YSOLDE).name("Captain Ysolde").examine("Captain of Gatehold's watch. All six of them.")
				.npcActions("Talk-to", null, null, null, null);
		g.npc(npcs, 3, PELL).name("Pell the Trader").examine("He'll sell you anything. He'll buy most things, too.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 24, DORRIN).name("Dorrin Anvilhand").examine("A smith with forearms like hams and a voice like a bellows.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 25, TESS).name("Tess Quarryhand").examine("Covered in rock dust from boots to eyebrows.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 352, MORRA).name("Morra").examine("Keeper of the Last Hearth. The fire never goes out, and neither does she.")
				.npcActions("Talk-to", "Trade", null, null, null);
		g.npc(npcs, 359, NETTLE).name("Sly Nettle").examine("Smiles with every tooth he's got. That's not many.")
				.npcActions("Talk-to", "Trade", null, null, null);
		// the dungeon bosses: grown, tinted versions of the creatures around them
		g.ghostly(g.npc(npcs, 93, BARROW_THANE), npcs, 93, GHOST_HUE, 3, 0.8, 4).name("The Barrow Thane")
				.examine("He was buried a king. He doesn't seem to have accepted the demotion.").shorts(95, 68).shorts(97, 140)
				.shorts(98, 140);
		g.ghostly(g.npc(npcs, 95, HOLLOW_MATRIARCH), npcs, 95, ConfigDefinitions.VOID, 5, 0.5, 2).name("The Hollow Matriarch")
				.examine("The first wolf the Scar took. Every Hollowed wolf is hers.").shorts(95, 92).shorts(97, 175)
				.shorts(98, 175);
		g.ghostly(g.npc(npcs, 111, RIMECLAW), npcs, 111, 36, 3, 1.1, 8).name("Rimeclaw")
				.examine("Ice, with an opinion. A very old, very cold opinion.").shorts(95, 110).shorts(97, 135).shorts(98, 135);
		// Ashes of the Crown
		g.npc(npcs, 646, MAELIS).name("Archivist Maelis").examine("Ink to the elbows. He knows more about Nythera than Nythera does.")
				.npcActions("Talk-to", null, null, null, null);
		g.ghostly(g.npc(npcs, 60, BROODMOTHER), npcs, 60, ConfigDefinitions.VOID, 2, 0.45, 2).name("Silkwood broodmother")
				.examine("Every spider in Silkwood is one of hers. She'd like you to be one of hers too.").shorts(95, 58)
				.shorts(97, 190).shorts(98, 190);
		// the Herald: a smaller echo of the Hollow King, crowned in his colours
		g.ghostly(g.npc(npcs, 2745, HOLLOW_HERALD), npcs, 2745, ConfigDefinitions.VOID, 5, 0.5, 6).name("The Hollow Herald")
				.examine("The King's voice, given a body. Smaller than him. That isn't much comfort.").shorts(95, 88)
				.shorts(97, 52).shorts(98, 52).set(12, new byte[] { 2 });
		g.ghostly(g.npc(npcs, 425, HOLLOW_SHADE), npcs, 425, ConfigDefinitions.VOID, 3, 0.7, 10).name("Hollow shade")
				.examine("A scrap of the void with a grudge.").shorts(95, 34);
		sagaNpcs(npcs);
	}

	/** NPCs for Hold the Gate, Heartforged, The Hollow King and the Starless. */
	void sagaNpcs(ConfigDefinitions.Table npcs) throws java.io.IOException {
		g.ghostly(g.npc(npcs, 186, HOLLOW_RAVAGER), npcs, 186, ConfigDefinitions.VOID, 5, 0.45, 2).name("Hollow ravager")
				.examine("The Scar's idea of a siege engine.").shorts(95, 48).shorts(97, 160).shorts(98, 160);
		g.ghostly(g.npc(npcs, 92, THANE_OATHSWORN), npcs, 92, GHOST_HUE, 3, 0.75, 6).name("Thane's oathsworn")
				.examine("Sworn to guard the Thane's things for ever. It took the oath literally.").shorts(95, 40);
		g.ghostly(g.npc(npcs, 125, RIMEBOUND_SENTINEL), npcs, 125, 36, 3, 1.25, 24).name("Rimebound sentinel")
				.examine("Frozen at its post so long it has become the post.").shorts(95, 58).shorts(97, 165).shorts(98, 165);
		g.ghostly(g.npc(npcs, 178, SIR_VAELRIC), npcs, 178, ConfigDefinitions.VOID, 5, 0.5, 4).name("Sir Vaelric, the Hollow Knight")
				.examine("Aurel's last knight. He followed his king into the dark, and kept following.").shorts(95, 66)
				.shorts(97, 150).shorts(98, 150);
		g.ghostly(g.npc(npcs, 178, HOLLOW_KNIGHT), npcs, 178, ConfigDefinitions.VOID, 3, 0.65, 8).name("Hollow knight")
				.examine("Armour with nobody left inside it, still marching.").shorts(95, 44);
		g.ghostly(g.npc(npcs, 648, AUREL_SPIRIT), npcs, 648, GHOST_HUE, 4, 1.0, 14).name("King Aurel")
				.examine("Nythera's last king, himself again - for a little while.").npcActions("Talk-to", null, null, null, null);
		// the Starless: a hand-built model on a bone no animation moves, so it hangs utterly still
		int starModel = forge.pack(forge.starless());
		g.npc(npcs, 2263, THE_STARLESS).name("The Starless").examine("The dark between the stars. It offered a king a crown once.")
				.remove(40).set(1, new byte[] { 1, (byte) (starModel >> 8), (byte) starModel }).set(12, new byte[] { 3 })
				.shorts(95, 400).shorts(97, 128).shorts(98, 128);
		g.ghostly(g.npc(npcs, 2263, STARLESS_SPAWN), npcs, 2263, ConfigDefinitions.VOID, 7, 0.9, 30).name("Starless spawn")
				.examine("A scrap of the Starless, sent to fetch you.").shorts(95, 72).shorts(97, 170).shorts(98, 170);
	}

	void items(ConfigDefinitions.Table items) throws java.io.IOException {
		splinter(items, EYE_SPLINTER, "Eye splinter", "It weeps cold green light.", 27, 6);
		splinter(items, GRAVE_SPLINTER, "Grave splinter", "Grey as a drowned man's skin. It's whispering.", 42, 2);
		splinter(items, VEIN_SPLINTER, "Vein splinter", "It still pulses, like something alive.", ConfigDefinitions.VOID, 4);
		ConfigDefinitions.Definition sigil = g.item(items, 1718, VOID_SIGIL).name("Void sigil")
				.examine("Three splinters bound into one. It's so cold it burns.").cost(1);
		tintItem(sigil, ConfigDefinitions.VOID, 6, 0.8, 10);
		ConfigDefinitions.Definition lantern = g.item(items, 4550, SEERS_LANTERN).name("Seer's lantern")
				.examine("Ilsabet's lantern. Its flame shows places, not things.").itemActions("Gaze", null, null, null, "Drop")
				.cost(2500);
		tintItem(lantern, ConfigDefinitions.VOID, 5, 1.0, 8);
		ConfigDefinitions.Definition cape = g.item(items, 1052, HOLLOW_CAPE).name("Hollow-touched cape")
				.examine("Woven from the edge of nothing. The Hollow King's mark is on it - and on you.").cost(60000);
		tintItem(cape, ConfigDefinitions.VOID, 4, 0.55, 0);
		ConfigDefinitions.Definition fang = g.item(items, 4587, HOLLOWFANG).name("Hollowfang scimitar")
				.examine("Forged from a sliver of the platform that shattered under you. It hums when it's hungry.").cost(80000);
		tintItem(fang, ConfigDefinitions.VOID, 3, 0.6, 6);
		ConfigDefinitions.Definition heart = g.item(items, 1725, HOLLOWHEART_AMULET).name("Hollowheart amulet")
				.examine("Cut from the Hollow Matriarch's heart. It beats when you're angry.").cost(90000);
		tintItem(heart, ConfigDefinitions.VOID, 6, 0.9, 10);
		ConfigDefinitions.Definition barrow = g.item(items, 1303, BARROWSTEEL_LONGSWORD).name("Barrowsteel longsword")
				.examine("The Barrow Thane's blade. Grave-cold, and very, very sharp.").cost(70000);
		// the Barrowsteel longsword has its own models, built in ModelForge (not a recoloured rune longsword)
		int swordInv = forge.pack(forge.swordInventory()), swordHeld = forge.pack(forge.swordWielded());
		barrow.remove(40).remove(41).shorts(1, swordInv).set(23, new byte[] { (byte) (swordHeld >> 8), (byte) swordHeld, 0 })
				.set(25, new byte[] { (byte) (swordHeld >> 8), (byte) swordHeld, 6 }); // the rune longsword sits 6 higher on women
		ConfigDefinitions.Definition boots = g.item(items, 1061, FROSTBOUND_BOOTS).name("Frostbound boots")
				.examine("Rimeclaw's hide, still frosted. Your feet will never be warm again. Worth it.").cost(50000);
		tintItem(boots, 36, 3, 1.3, 20);
		crownItems(items);
		sagaItems(items);
	}

	/** Items for Hold the Gate, Heartforged, The Hollow King and the Starless's uniques. */
	void sagaItems(ConfigDefinitions.Table items) throws java.io.IOException {
		ConfigDefinitions.Definition shield = g.item(items, 1201, WARDEN_KITESHIELD).name("Warden's kiteshield")
				.examine("Gatehold's watch carries these. They gave you one. Don't lose it.").cost(30000);
		ConfigDefinitions.Definition spikes = g.item(items, 2351, GATE_SPIKES).name("Gate spikes")
				.examine("Iron spikes from Dorrin's forge, for the barricades.").cost(1);
		tintItem(spikes, 0, 0, 0.7, 0);
		ConfigDefinitions.Definition starmetal = g.item(items, 451, STARMETAL).name("Starmetal")
				.examine("It fell from the sky. It's still warm, and it rings when you tap it.").cost(1);
		tintItem(starmetal, 42, 2, 1.5, 30);
		ConfigDefinitions.Definition rime = g.item(items, 1617, RIMEHEART).name("Rimeheart")
				.examine("Cold enough to quench a fallen star.").cost(1);
		tintItem(rime, 36, 2, 1.3, 30);
		ConfigDefinitions.Definition brand = g.item(items, 1303, EMBERBRAND).name("Emberbrand longsword")
				.examine("Aurel's heart, forged into a blade. It beats in your hand.").cost(150000);
		int brandInv = forge.pack(forge.emberbrandInventory()), brandHeld = forge.pack(forge.emberbrandWielded());
		brand.remove(40).remove(41).shorts(1, brandInv).set(23, new byte[] { (byte) (brandHeld >> 8), (byte) brandHeld, 0 })
				.set(25, new byte[] { (byte) (brandHeld >> 8), (byte) brandHeld, 6 });
		ConfigDefinitions.Definition crown = g.item(items, 5527, CROWN_OF_AUREL).name("Crown of Aurel")
				.examine("The circlet Aurel wore before the dark gave him a better one.").cost(100000);
		tintItem(crown, 8, 6, 1.2, 14);
		ConfigDefinitions.Definition cape = g.item(items, 6570, STARLESS_CAPE).name("Starless cape")
				.examine("A piece of the dark between the stars. There are stars in it, if you look.").cost(250000);
		tintItem(cape, ConfigDefinitions.VOID, 6, 0.45, 0);
		ConfigDefinitions.Definition eclipse = g.item(items, 1704, ECLIPSE_AMULET).name("Eclipse amulet")
				.examine("A black sun on a chain. It's warm on one side and freezing on the other.").cost(200000);
		tintItem(eclipse, ConfigDefinitions.VOID, 7, 0.55, 0);
		gearItems(items, shield);
		effects();
		floors();
	}

	/**
	 * The saga's hand-built gear (tools/art-drafts): the Gatehold Warden set, Aurel's Royal Guard and five weapons.
	 * Each clones the rune (or magic) item whose inventory camera its model was built to fit, then swaps the models in.
	 * No female meshes were built, so women wear the male models (opcodes 25/26) for now.
	 */
	void gearItems(ConfigDefinitions.Table items, ConfigDefinitions.Definition wardenShield) throws java.io.IOException {
		ArmourForge af = new ArmourForge(forge);
		double tau = Math.PI * 2;
		ArmourForge.Palette[] palettes = { ArmourForge.WARDEN, ArmourForge.ROYAL };
		String[] tag = { "Warden's", "Royal guard" };
		String[][] examines = {
				{ "Blackened starmetal, cut for the watch on the wall. The plume is Gatehold's colours.",
						"The watch's plate. The ember over the heart is meant to keep you warm on the wall. It doesn't.",
						"Gatehold steel, scratched by a hundred raids.", "Heavy boots for long nights on the wall.",
						"Pried from a watchman who didn't come home. Still warm." },
				{ "The helm of Aurel's own guard. The visor still glows, as if someone's behind it.",
						"Pale starmetal and royal violet. The last knights of Nythera wore this to the end.",
						"Polished so bright you can see the stars in them.", "Fit to walk a king's halls - or the void.",
						"Gold-cuffed gauntlets of the Royal Guard. A king's hand once clasped these." } };
		int[][] ids = { { WARDEN_HELM, WARDEN_BODY, WARDEN_LEGS, -1, WARDEN_BOOTS, WARDEN_GAUNTLETS },
				{ ROYAL_HELM, ROYAL_BODY, ROYAL_LEGS, ROYAL_KITESHIELD, ROYAL_BOOTS, ROYAL_GAUNTLETS } };
		int[] cost = { 170000, 400000 };
		for (int k = 0; k < 2; k++) {
			ArmourForge.Palette p = palettes[k];
			ModelData helm = af.helm(p), legs = af.legs(p), kite = af.shield(p), boots = af.boots(p), gl = af.gauntlets(p);
			ModelData[] ba = af.body(p);
			int[] worn = { forge.pack(helm), forge.pack(ba[0]), forge.pack(legs), forge.pack(kite), forge.pack(boots), forge.pack(gl) };
			int arms = forge.pack(ba[1]);
			int[] inv = { forge.pack(af.inventory(helm, 218, 2813)),
					forge.pack(af.inventory(ArmourForge.mergeAll(ba[0], ba[1]), new int[] { 306, 164 }, 2605)),
					forge.pack(af.inventory(legs, 268, 2582)), forge.pack(af.shieldInventory(kite, -1104 * tau / 2048, 344 * tau / 2048)),
					forge.pack(af.inventory(boots, 4954, 5037)), forge.pack(af.gauntletsInventory(gl)) };
			int[] base = { 1163, 1127, 1079, 1201, 4131, 1059 };
			String[] piece = { "full helm", "platebody", "platelegs", "kiteshield", "boots", "gauntlets" };
			int[] share = { 15, 30, 22, 20, 7, 6 };
			for (int i = 0; i < 6; i++) {
				if (ids[k][i] < 0) {
					// the Warden's kiteshield already exists (Hold the Gate's reward): give it the new model
					wear(wardenShield.remove(40).remove(41).shorts(1, inv[3]), worn[3], -1);
					continue;
				}
				String ex = examines[k][i < 3 ? i : i - 1];
				if (k == 1 && i == 3) {
					ex = "The last shield of the Royal Guard. Aurel's sun is still on it, under the soot.";
				}
				ConfigDefinitions.Definition d = g.item(items, base[i], ids[k][i]).name(tag[k] + " " + piece[i]).examine(ex)
						.cost(cost[k] * share[i] / 100).remove(41);
				if (i == 0) {
					d.recolor(new int[] { 61, 926 }, new int[] { p.steel, p.plume }); // only the chathead models still use these
				} else {
					d.remove(40);
				}
				wear(d.shorts(1, inv[i]), worn[i], i == 1 ? arms : -1);
			}
		}
		// the five weapons, each placed in its base item's own hand and inventory frame
		int[] weaponIds = { STARFALL_HALBERD, RIMECLEAVER, KINGS_SCEPTRE, DUSKYEW_SHORTBOW, CAGED_STAR_STAFF };
		String[] names = { "Starfall halberd", "Rimecleaver battleaxe", "Hollow King's mace", "Duskyew shortbow", "Staff of the caged star" };
		String[] wex = { "Starmetal on a long haft. Where it bites, the sky follows.",
				"A battleaxe of black ice that will not melt. It's colder than the sentinel it came from.",
				"Aurel's sceptre. The Starless lent him the stone in it, and you can feel it looking.",
				"Strung from a yew that grew in the Scar's shadow. It shoots straight, and it shoots fast.",
				"A star in a cage of starmetal. It burns for ever, so you never need fire runes." };
		int[] wcost = { 120000, 100000, 110000, 90000, 110000 };
		for (int i = 0; i < 5; i++) {
			QuestWeapons.Weapon w = QuestWeapons.ALL[i];
			int inv = forge.pack(w.inventoryModel()), held = forge.pack(w.wieldedModel());
			ConfigDefinitions.Definition d = g.item(items, w.pose.baseItem, weaponIds[i]).name(names[i]).examine(wex[i])
					.cost(wcost[i]).remove(40).remove(41).remove(24).remove(26).shorts(1, inv);
			d.set(23, new byte[] { (byte) (held >> 8), (byte) held, 0 }).set(25, new byte[] { (byte) (held >> 8), (byte) held, 6 });
		}
	}

	/** Points an item's worn models (male, and female as a stop-gap) at a new model, keeping each base's offsets. */
	private static void wear(ConfigDefinitions.Definition d, int model, int second) {
		for (int op : new int[] { 23, 25 }) {
			byte[] old = d.get(op);
			d.set(op, new byte[] { (byte) (model >> 8), (byte) model, old == null ? 0 : old[2] });
		}
		if (second >= 0) {
			d.shorts(24, second).shorts(26, second);
		}
	}

	/** Appends the void floors (see {@link #VOID_POOL}) to flo.dat. */
	void floors() {
		byte[] dat = g.config.get("flo.dat");
		int count = (dat[0] & 0xFF) << 8 | dat[1] & 0xFF;
		if (count != VOID_POOL - 1) {
			throw new IllegalStateException("flo.dat has " + count + " entries; the void floors assume " + (VOID_POOL - 1));
		}
		java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
		// copy the stock records, swapping in the void stone where it takes over an unused floor
		int pos = 2;
		for (int id = 1; id <= count; id++) {
			int start = pos;
			for (int op; (op = dat[pos++] & 0xFF) != 0;) {
				pos += op == 1 || op == 7 ? 3 : op == 2 ? 1 : 0;
				if (op == 6) {
					while (dat[pos++] != 10) {
						// name
					}
				}
			}
			if (id == VOIDSTONE || id == VOIDSTONE_DARK) {
				int rgb = VOIDSTONE_RGB[id - VOIDSTONE];
				o.write(1);
				o.write(rgb >> 16);
				o.write(rgb >> 8);
				o.write(rgb);
				byte[] name = ConfigDefinitions.string(id == VOIDSTONE ? "voidstone" : "voidstone_dark");
				o.write(6);
				o.write(name, 0, name.length);
				o.write(0);
			} else {
				o.write(dat, start, pos - start);
			}
		}
		for (int i = 0; i < VOID_FLOORS.length; i++) {
			int rgb = VOID_FLOORS[i][0];
			o.write(1);
			o.write(rgb >> 16);
			o.write(rgb >> 8);
			o.write(rgb);
			o.write(6);
			byte[] name = ConfigDefinitions.string(VOID_FLOOR_NAMES[i]);
			o.write(name, 0, name.length);
			o.write(0);
		}
		byte[] body = o.toByteArray(), out = new byte[body.length + 2];
		int n = count + VOID_FLOORS.length;
		out[0] = (byte) (n >> 8);
		out[1] = (byte) n;
		System.arraycopy(body, 0, out, 2, body.length);
		g.config.put("flo.dat", out);
	}

	/** The saga's spotanims (tools/art-drafts/effects): new models on existing animations, appended as gfx 666-672. */
	void effects() throws java.io.IOException {
		byte[] dat = g.config.get("spotanim.dat");
		int count = (dat[0] & 0xFF) << 8 | dat[1] & 0xFF;
		if (count != GFX_VOID_BOLT) {
			throw new IllegalStateException("spotanim.dat has " + count + " entries; the saga's gfx ids assume 666");
		}
		java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
		java.util.List<VoidEffects.Effect> all = VoidEffects.all();
		o.write(dat, 2, dat.length - 2);
		for (VoidEffects.Effect e : all) {
			if (e == all.get(GFX_HEARTFIRE - GFX_VOID_BOLT)) {
				e.resizeXY = e.resizeZ = 150; // the arc reads small beside a full swing; a little bigger, as the d long's is
			}
			int model = forge.pack(e.model);
			o.write(1);
			o.write(model >> 8);
			o.write(model);
			o.write(2);
			o.write(e.reuseSeq >> 8);
			o.write(e.reuseSeq);
			if (e.resizeXY != 128) {
				o.write(4);
				o.write(e.resizeXY >> 8);
				o.write(e.resizeXY);
			}
			if (e.resizeZ != 128) {
				o.write(5);
				o.write(e.resizeZ >> 8);
				o.write(e.resizeZ);
			}
			o.write(7);
			o.write(e.brightness);
			o.write(8);
			o.write(e.shadow);
			o.write(0);
		}
		byte[] body = o.toByteArray(), out = new byte[body.length + 2];
		int n = count + all.size();
		out[0] = (byte) (n >> 8);
		out[1] = (byte) n;
		System.arraycopy(body, 0, out, 2, body.length);
		g.config.put("spotanim.dat", out);
	}

	/** Items for Ashes of the Crown. */
	void crownItems(ConfigDefinitions.Table items) throws java.io.IOException {
		String[][] pages = { { "Chronicle page (Thane)", "Cold to the touch. It smells of the barrow." },
				{ "Chronicle page (silk)", "Still sticky with spider silk." },
				{ "Chronicle page (Nettle)", "Someone has used it as a coaster. Several times." } };
		int[] ids = { THANE_PAGE, SILK_PAGE, NETTLE_PAGE };
		for (int i = 0; i < 3; i++) {
			tintItem(g.item(items, 4569 + i, ids[i]).name(pages[i][0]).examine(pages[i][1]).cost(1), 8, 2, 0.88, 0); // old parchment
		}
		ConfigDefinitions.Definition chron = g.item(items, 1509, ROYAL_CHRONICLE).name("Royal chronicle")
				.examine("The last chronicle of Nythera's kings, bound back together.").itemActions("Read", null, null, null, "Drop")
				.cost(1);
		tintItem(chron, ConfigDefinitions.VOID, 4, 0.8, 0);
		ConfigDefinitions.Definition ember = g.item(items, 569, EMBER).name("The Ember")
				.examine("King Aurel's heart. It's still warm. It's still beating.").cost(1);
		remap(ember, itemModels(ember), c -> c == 61 ? hsl(2, 7, 34) : hsl(5, 7, 74)); // a coal-red core in an orange glow
		ConfigDefinitions.Definition signet = g.item(items, 2550, GATEHOLD_SIGNET).name("Signet of Gatehold")
				.examine("Gatehold's seal, given to those who've earned the right to come home.")
				.itemActions("Rub", "Wear", null, null, "Drop").cost(40000);
		// a gold band set with a chip of the Scar's crystal, the same glass Gatehold lights its streets with
		remap(signet, itemModels(signet), c -> c == 127 ? ConfigDefinitions.voidRamp(48, 4) : c);
	}

	void splinter(ConfigDefinitions.Table items, int id, String name, String examine, int hue, int sat) throws java.io.IOException {
		ConfigDefinitions.Definition d = g.item(items, 1617, id).name(name).examine(examine).cost(1)
				.itemActions(null, null, null, null, "Drop");
		tintItem(d, hue, sat, 0.9, 12);
	}

	/**
	 * Void glass: a stock crystal cluster re-made in the void's colours - cool lilac shards (or black glass, if
	 * {@code dark}) growing out of a disc of scorched, ashen ground, so the cluster sits in any terrain.
	 */
	void voidGlass(ConfigDefinitions.Definition d, boolean dark) throws java.io.IOException {
		remap(d, d.locModels(), c -> {
			int light = c & 127;
			if ((c >> 10) <= 9 && (c >> 7 & 7) >= 2) { // the sandy base disc
				return hsl(8, 1, 10);
			}
			return dark ? ConfigDefinitions.voidRamp(Math.max(6, light), 3) : ConfigDefinitions.voidRamp(Math.min(100, (int) (light * 0.85)), 3);
		});
	}

	/** Recolours every colour of the given models through {@code f} (16-bit HSL in, 16-bit HSL out). */
	void remap(ConfigDefinitions.Definition d, int[] models, java.util.function.IntUnaryOperator f) throws java.io.IOException {
		java.util.Set<Integer> all = new java.util.TreeSet<>();
		for (int m : models) {
			all.addAll(ConfigDefinitions.modelColors(g.cache, m));
		}
		List<Integer> from = new ArrayList<>(), to = new ArrayList<>();
		for (int c : all) {
			int n = f.applyAsInt(c);
			if (n != c) {
				from.add(c);
				to.add(n);
			}
		}
		d.recolor(from.stream().mapToInt(i -> i).toArray(), to.stream().mapToInt(i -> i).toArray());
	}

	void tintItem(ConfigDefinitions.Definition d, int hue, int sat, double light, int add) throws java.io.IOException {
		int[][] t = ConfigDefinitions.tint(g.cache, itemModels(d), hue, sat, light, add);
		d.recolor(t[0], t[1]);
	}

	/** An item's inventory model and its worn models. */
	int[] itemModels(ConfigDefinitions.Definition d) {
		List<Integer> models = new ArrayList<>();
		models.add(d.itemModel());
		for (int op : new int[] { 23, 25 }) { // male / female worn models
			byte[] p = d.get(op);
			if (p != null) {
				models.add((p[0] & 0xFF) << 8 | p[1] & 0xFF);
			}
		}
		return models.stream().mapToInt(i -> i).toArray();
	}

	void tintNpc(ConfigDefinitions.Definition d, int hue, int sat, double light, int add, java.util.function.IntPredicate filter)
			throws java.io.IOException {
		int[] body = d.npcModels();
		byte[] h = d.get(60);
		int heads = h == null ? 0 : h[0] & 0xFF;
		int[] models = java.util.Arrays.copyOf(body, body.length + heads);
		for (int i = 0; i < heads; i++) {
			models[body.length + i] = (h[1 + i * 2] & 0xFF) << 8 | h[2 + i * 2] & 0xFF;
		}
		int[][] t = ConfigDefinitions.tint(g.cache, models, hue, sat, light, add, filter);
		// keep the definition's own recolours for colours the tint leaves alone (e.g. a placeholder that becomes skin)
		byte[] own = d.get(40);
		java.util.List<int[]> pairs = new ArrayList<>();
		for (int i = 0; i < t[0].length; i++) {
			pairs.add(new int[] { t[0][i], t[1][i] });
		}
		if (own != null) {
			for (int k = 0; k < (own[0] & 0xFF); k++) {
				int from = (own[1 + k * 4] & 0xFF) << 8 | own[2 + k * 4] & 0xFF, to = (own[3 + k * 4] & 0xFF) << 8 | own[4 + k * 4] & 0xFF;
				if (pairs.stream().noneMatch(pr -> pr[0] == from)) {
					pairs.add(new int[] { from, to });
				}
			}
		}
		d.recolor(pairs.stream().mapToInt(pr -> pr[0]).toArray(), pairs.stream().mapToInt(pr -> pr[1]).toArray());
	}

	void locs(ConfigDefinitions.Table locs) throws java.io.IOException {
		ConfigDefinitions.Definition vein = g.loc(locs, 2106, VOID_VEIN).name("Void-veined rock")
				.examine("Violet veins crawl through it. They move when you aren't looking.");
		byte[] p = vein.get(40);
		java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(p, 1, p.length - 1);
		int from = b.getShort() & 0xFFFF;
		vein.recolor(new int[] { from }, new int[] { ConfigDefinitions.voidRamp(40, 3) });
		g.loc(locs, 404, EPITAPH_GRAVE).name("Gravestone").examine("A weathered stone, carved by an unsteady hand.")
				.set(30, ConfigDefinitions.string("Read")).set(31, ConfigDefinitions.string("Dig"));
		ConfigDefinitions.Definition crystal = g.loc(locs, CRYSTAL_SRC, VOID_CRYSTAL).name("Void crystal")
				.examine("It grew here when the void touched the world.");
		voidGlass(crystal, false);
		ConfigDefinitions.Definition dark = g.loc(locs, CRYSTAL_DARK_SRC, VOID_CRYSTAL_DARK).name("Void crystal")
				.examine("Black glass with a violet heart.");
		voidGlass(dark, true);
		ConfigDefinitions.Definition eye = g.loc(locs, GHOSTFIRE, EYE_FIRE).name("Ghostfire")
				.examine("It burns in the skull's eye. Something glints beneath the flames.")
				.set(30, ConfigDefinitions.string("Reach-into"));
		g.tintLoc(eye, 23, 6, 1.0, 4, c -> true);
		ConfigDefinitions.Definition rift = g.loc(locs, com.rs2.game.content.skullbrine.SkullbrineConstants.STOCK_PORTAL, RIFT)
				.name("Rift").examine("The hole your fall tore in the sky. It hasn't closed.").set(30, ConfigDefinitions.string("Enter"));
		g.tintLoc(rift, ConfigDefinitions.VOID, 4, 1.0, 12, c -> true);
		int cave = com.rs2.game.content.skullbrine.SkullbrineConstants.CAVE_ENTRANCE;
		ConfigDefinitions.Definition door = g.loc(locs, cave, BARROW_DOOR).name("Barrow door")
				.examine("Someone carved a warning over it. Someone else scratched it out.").set(30, ConfigDefinitions.string("Enter"));
		g.tintLoc(door, GHOST_HUE, 1, 0.7, 0, c -> true);
		ConfigDefinitions.Definition fissure = g.loc(locs, cave, HOLLOW_FISSURE).name("Hollow fissure")
				.examine("The Scar's heart. The dark down there is the wrong shape.").set(30, ConfigDefinitions.string("Descend"));
		g.tintLoc(fissure, ConfigDefinitions.VOID, 3, 0.6, 2, c -> true);
		ConfigDefinitions.Definition frost = g.loc(locs, cave, FROST_CAVE).name("Frostfang cave")
				.examine("Cold breath rolls out of it. Something in there is breathing it.").set(30, ConfigDefinitions.string("Enter"));
		g.tintLoc(frost, 36, 2, 1.4, 30, c -> true);
		// scenery with hand-built models
		forgedLoc(locs, WAYSTONE, forge.pack(forge.waystone()), "Waystone",
				"An old road marker. The rune on it still glows, though nobody remembers what it says.")
				.set(30, ConfigDefinitions.string("Touch")); // the waystone network (Ashes of the Crown)
		forgedLoc(locs, VOID_LANTERN, forge.pack(forge.voidLantern()), "Void lantern",
				"Gatehold lights its streets with the Scar's own crystal. Nobody finds this as funny as Pell does.");
		forgedLoc(locs, HOLLOWED_TREE, forge.pack(forge.hollowedTree()), "Hollowed tree",
				"The Scar emptied it. Something violet is growing in the space it left.")
				.scale(192, 192, 192); // 1.5x: built at person scale, it should tower over you
		int[] props = packWorldProps();
		int[] saga = { forge.pack(forge.brokenBarricade()), forge.pack(forge.spikedBarricade()), forge.pack(forge.fallenStar()),
				forge.pack(forge.rimeAltar()), forge.pack(forge.starlessGate()), forge.pack(forge.starPillar(false)),
				forge.pack(forge.starPillar(true)) };
		forge.finish();
		crownLocs(locs);
		sagaLocs(locs, saga);
		// the dormant portal in Skullbrine's smugglers' cave: the rift's twin, its violet banked down low
		ConfigDefinitions.Definition portal = g.loc(locs, com.rs2.game.content.skullbrine.SkullbrineConstants.STOCK_PORTAL,
				com.rs2.game.content.skullbrine.SkullbrineConstants.VOID_PORTAL)
				.examine("Dark and still. Something on the other side is waiting for a key.");
		g.tintLoc(portal, ConfigDefinitions.VOID, 3, 0.75, 4, c -> true);
		worldProps(locs, props); // after the portal: definitions are appended in id order
	}

	/** Skull Rock's sculpted face, in pieces (see {@link SkullRock}). */
	java.util.List<SkullRock.Piece> skullRock;

	/** Packs the models of {@link #worldProps}, in its order (models must be packed before the forge finishes). */
	int[] packWorldProps() throws java.io.IOException {
		WorldProps w = new WorldProps();
		ModelData[] models = { w.campTent(), w.fireRing(false), w.fireRing(true), w.logSeat(), w.gateholdBanner(), w.menhir(),
				w.menhir(), w.menhir(), w.gateTower(true), w.gateTower(false), w.fallenMasonry(), w.pirateFlag(), w.gateWall(true),
				w.gateWall(false) };
		skullRock = new SkullRock(g).pieces();
		int[] ids = new int[models.length + skullRock.size()];
		for (int i = 0; i < models.length; i++) {
			ids[i] = forge.pack(models[i]);
		}
		for (int i = 0; i < skullRock.size(); i++) {
			ids[models.length + i] = forge.pack(skullRock.get(i).model);
		}
		return ids;
	}

	/** Camp furniture, flags, the old stone circle and the Broken Gate (see {@link WorldProps}). */
	void worldProps(ConfigDefinitions.Table locs, int[] m) {
		forgedLoc(locs, CAMP_TENT, m[0], "Tent", "Patched canvas, pegged down tight. Someone sleeps here.").footprint(2, 2);
		// hearth stones go under a burning fire as ground decoration: nothing to bump into, nothing to click
		forgedLoc(locs, FIRE_RING, m[1], "Hearth stones", "Blackened stones round the fire.").set(17, new byte[0]);
		forgedLoc(locs, COOK_FIRE_RING, m[2], "Cooking tripod", "Whatever's in the pot smells better than it looks.").set(17,
				new byte[0]);
		forgedLoc(locs, LOG_SEAT, m[3], "Log", "Worn smooth on top by a lot of sitting.");
		forgedLoc(locs, GATEHOLD_BANNER, m[4], "Banner", "The Wardens' star on night-blue. Gatehold still flies it, for whoever is left to see.");
		int[] menhirs = { MENHIR, MENHIR_2, MENHIR_3 };
		for (int i = 0; i < 3; i++) {
			forgedLoc(locs, menhirs[i], m[5 + i], "Standing stone", "Older than the kingdom. Older than the Gate.");
		}
		forgedLoc(locs, GATE_TOWER, m[8], "Gate tower", "One side of the Broken Gate, still standing. The arch it held up is lying in the road.")
				.footprint(2, 2);
		forgedLoc(locs, GATE_TOWER_BROKEN, m[9], "Gate tower", "Broken off at half its height. The rest of it is all around your feet.")
				.footprint(2, 2);
		forgedLoc(locs, FALLEN_MASONRY, m[10], "Fallen masonry", "Blocks the size of a cart, and a piece of the arch still in one lump.")
				.footprint(2, 2);
		forgedLoc(locs, PIRATE_FLAG, m[11], "Black flag", "The colours of the Drowned Maiden, run up on a broken spar.");
		forgedLoc(locs, GATE_WALL, m[12], "Old wall", "The wall that ran out from the Gate. The kingdom ended a long way before it did.");
		forgedLoc(locs, GATE_WALL_LOW, m[13], "Old wall", "A stump of the old wall, waist-high and mossed over.");
		// Skull Rock's face: walked through (the terrain under it does the blocking), never clicked (opcode 19 = 0 keeps
		// it out of the client's mouse picking, so it can't swallow a click meant for the ground), and no ground shadow.
		// Every reserved id is defined, so the ids after them don't move when the face is cut into fewer pieces.
		for (int i = 0; i < SkullRock.MAX_PIECES; i++) {
			SkullRock.Piece p = skullRock.get(Math.min(i, skullRock.size() - 1));
			forgedLoc(locs, SKULL_ROCK + i, m[14 + Math.min(i, skullRock.size() - 1)], "Skull Rock", "Skull Rock.")
					.footprint(p.sizeX, p.sizeY).set(17, new byte[0]).set(19, new byte[] { 0 }).set(64, new byte[0]);
			if (i < skullRock.size()) {
				g.object(SKULL_ROCK + i, p.x, p.y, CursedIslandGenerator.TYPE_SCENERY, 0);
			}
		}
		System.out.println("Skull Rock: " + skullRock.size() + " pieces");
	}

	/** Objects for the later quests and the Starless, all on hand-built models. */
	void sagaLocs(ConfigDefinitions.Table locs, int[] models) {
		forgedLoc(locs, BROKEN_BARRICADE, models[0], "Broken barricade", "Something big came through here.")
				.set(30, ConfigDefinitions.string("Repair")).scale(170, 170, 170);
		forgedLoc(locs, SPIKED_BARRICADE, models[1], "Spiked barricade", "Let them come.").scale(170, 170, 170);
		forgedLoc(locs, FALLEN_STAR, models[2], "Fallen star", "It fell on the night you did. The metal in it is still glowing.")
				.set(30, ConfigDefinitions.string("Mine")).footprint(2, 2).scale(200, 200, 200);
		forgedLoc(locs, RIME_ALTAR, models[3], "Rime altar", "Something white and impossibly cold is frozen into the top of it.")
				.set(30, ConfigDefinitions.string("Take-from"));
		forgedLoc(locs, STARLESS_GATE, models[4], "Starless Gate", "There are no stars on the other side. There is something else.")
				.set(30, ConfigDefinitions.string("Enter"));
		forgedLoc(locs, STAR_PILLAR, models[5], "Star pillar", "An obsidian needle with a dead star caught at its tip.");
		forgedLoc(locs, STAR_PILLAR_LIT, models[6], "Star pillar", "Its star is burning. The dark won't come near it.");
	}

	/** Objects for Ashes of the Crown. */
	void crownLocs(ConfigDefinitions.Table locs) throws java.io.IOException {
		ConfigDefinitions.Definition coffer = g.loc(locs, 375, THANE_COFFER).name("Thane's coffer")
				.examine("Bound in black iron. The Thane's mark is on the lock.").set(30, ConfigDefinitions.string("Open"));
		g.tintLoc(coffer, GHOST_HUE, 2, 0.7, 0, c -> true);
		g.loc(locs, 3833, SILK_COCOON).name("Silk cocoon").examine("Something paper-flat is wrapped up in it. Something else is guarding it.")
				.set(30, ConfigDefinitions.string("Cut open"));
		ConfigDefinitions.Definition hatch = g.loc(locs, 1570, VAULT_HATCH).name("Rusted hatch")
				.examine("Half buried in the Gate's rubble. Steps go down into the dark.").set(30, ConfigDefinitions.string("Climb-down"))
				.remove(31);
		g.tintLoc(hatch, 7, 3, 0.7, 0, c -> true);
		int[][] wards = { { SUN_WARD, 8, 6 }, { STAR_WARD, 36, 4 }, { MOON_WARD, 0, 0 } };
		String[] names = { "Sun ward", "Star ward", "Moon ward" };
		String[] exam = { "A brazier marked with a crowned sun.", "A brazier marked with a guiding star.",
				"A brazier marked with a weeping moon." };
		for (int i = 0; i < 3; i++) {
			ConfigDefinitions.Definition w = g.loc(locs, 11017, wards[i][0]).name(names[i]).examine(exam[i])
					.set(30, ConfigDefinitions.string("Light")).remove(31).scale(184, 184, 184);
			g.tintLoc(w, wards[i][1], wards[i][2], 1.1, 10, c -> true);
		}
		ConfigDefinitions.Definition seal = g.loc(locs, CRYSTAL_SRC, VAULT_SEAL).name("Ward-seal")
				.examine("A wall of void glass grown across the passage.").set(30, ConfigDefinitions.string("Touch"))
				.scale(150, 170, 150);
		voidGlass(seal, false);
		ConfigDefinitions.Definition ped = g.loc(locs, 10988, EMBER_PEDESTAL).name("Ember pedestal")
				.examine("Something burns on it, very small and very bright.").set(30, ConfigDefinitions.string("Take"));
		// the vault's grey stone, with the ember itself burning in the bowl on top
		remap(ped, ped.locModels(), c -> (c >> 7 & 7) == 3 ? hsl(5, 7, 86) : hsl(7, 1, (int) ((c & 127) * 0.75)));
		// an archivist's lectern, open at the page he was reading
		g.loc(locs, 13642, MAELIS_BOOKS).name("Maelis's books").examine("Every book left in Nythera, by the look of it.")
				.set(30, ConfigDefinitions.string("Browse")).remove(31);
		int[] lit = { SUN_WARD_LIT, STAR_WARD_LIT, MOON_WARD_LIT };
		for (int i = 0; i < 3; i++) { // burning: the iron keeps the ward's colour, the flame takes it too
			ConfigDefinitions.Definition w = g.loc(locs, 11020, lit[i]).name(names[i]).examine(exam[i] + " It burns.").remove(30).scale(184, 184, 184)
					.remove(31);
			g.tintLoc(w, wards[i][1], Math.max(3, wards[i][2]), 1.4, 30, c -> (c >> 7 & 7) >= 5);
		}
	}

	ModelForge forge;

	/** An object that uses one of ModelForge's models (cloned from a plain 1x1 standing stone for its solidity). */
	ConfigDefinitions.Definition forgedLoc(ConfigDefinitions.Table locs, int id, int model, String name, String examine) {
		return g.loc(locs, STANDING_STONE, id).name(name).examine(examine).remove(1).remove(40).remove(41)
				.set(5, new byte[] { 1, (byte) (model >> 8), (byte) model }).footprint(1, 1);
	}

	/* ====================================================================== */
	/*                         SKULLBRINE ISLE SCENERY                        */
	/* ====================================================================== */

	/** The weeping eye, the epitaph graves and Seer Ilsabet's camp. Run after the island surface is built. */
	void islandScenery() {
		// a ghostfire burns at the lower lip of each eye, where you can reach it
		for (int x : new int[] { LEFT_EYE_X, RIGHT_EYE_X }) {
			g.removeObjectsAt(x, EYE_FIRE_Y);
			g.object(EYE_FIRE, x, EYE_FIRE_Y, TYPE_SCENERY, 0);
		}
		// the east eye weeps: a trail of the cove's green water runs down the face to the jaw
		for (int y = FACE_Y0 + 3; y < EYE_FIRE_Y; y++) {
			int x = RIGHT_EYE_X + ((y / 2) % 2 == 0 ? 0 : 1) - (y < FACE_Y0 + 6 ? 1 : 0);
			if (g.overlayAt(x, y) == 0) {
				g.setOverlay(x, y, GUNGY_WATER);
			}
		}
		// the island's overlays were smoothed before the trail was drawn: cut its stair-steps into a running line too
		CursedIslandGenerator.smoothGrid(g.worldGrid(), RIGHT_EYE_X - 3, FACE_Y0 + 2, RIGHT_EYE_X + 3, EYE_FIRE_Y,
				CursedIslandGenerator::overlayGroup, ov -> ov == WOOD, ov -> false);
		// the graves with epitaphs replace whatever stood there
		for (int[] grave : GRAVES) {
			g.removeObjectsAt(grave[0], grave[1]);
			g.object(EPITAPH_GRAVE, grave[0], grave[1], TYPE_SCENERY, 1);
		}
		// Ilsabet's camp east of the jaw plaza: a tent, a fire, and the void crystal she reads
		tent(ILSABET_X + 2, ILSABET_Y + 1, DOOR_WEST);
		campfire(ILSABET_X - 1, ILSABET_Y + 2, false);
		g.object(VOID_CRYSTAL, ILSABET_X + 1, ILSABET_Y - 3, TYPE_SCENERY, 0);
	}

	/** The void vein in the Dragonvein cavern (dungeon-region local coordinates). */
	void voidVein(MapRegion r, boolean[][] open) {
		for (int x = 2; x <= 10; x++) {
			for (int y = 44; y <= 56; y++) {
				final int fx = x, fy = y;
				if (open[x][y] && CursedIslandGenerator.bordersClosed(open, x, y)
						&& CursedIslandGenerator.openAround(open, x, y, 1) >= 4
						&& r.objects.stream().noneMatch(o -> Math.abs(o.x - fx) <= 1 && Math.abs(o.y - fy) <= 1)) {
					r.objects.add(new MapRegion.MapObject(VOID_VEIN, x, y, 0, TYPE_SCENERY, 0));
					r.objects.add(new MapRegion.MapObject(VOID_CRYSTAL_DARK, Math.max(1, x - 1), y + 2, 0, TYPE_SCENERY, 0));
					System.out.println("Void vein at " + (x + DBASE_X) + "," + (y + DBASE_Y));
					return;
				}
			}
		}
		throw new IllegalStateException("no place for the void vein");
	}

	/* ====================================================================== */
	/*                           THE VOID BETWEEN                             */
	/* ====================================================================== */

	/** A ragged platform of black stone hanging in nothing, ringed with violet crystals. */
	void arena() {
		MapRegion r = new MapRegion(ARENA_REGION_X, ARENA_REGION_Y);
		g.regions.put(r.id(), r);
		int bx = ARENA_REGION_X * 64, by = ARENA_REGION_Y * 64;
		int cx = ARENA_X - bx, cy = ARENA_Y - by;
		boolean[][] land = new boolean[64][64];
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				double a = Math.atan2(y - cy, x - cx);
				double rad = 9.5 + 1.2 * Math.sin(3 * a + 0.4) + 0.8 * Math.sin(7 * a + 2);
				land[x][y] = Math.hypot(x - cx, (y - cy) * 1.05) < rad;
			}
		}
		// floating shards of rock around the platform
		int[][] islets = { { -14, 4, 2 }, { 13, -6, 2 }, { 15, 8, 1 }, { -12, -9, 1 }, { 3, 15, 2 }, { -6, -14, 1 } };
		for (int[] i : islets) {
			for (int x = cx + i[0] - 2; x <= cx + i[0] + 2; x++) {
				for (int y = cy + i[1] - 2; y <= cy + i[1] + 2; y++) {
					if (Math.hypot(x - cx - i[0], y - cy - i[1]) <= i[2] + 0.3) {
						land[x][y] = true;
					}
				}
			}
		}
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				if (land[x][y]) {
					// the floor is underlay only - the client blends underlays, so the stone shades smoothly instead of
					// breaking into squares: dusky violet-grey void-stone with drifts of near-black glass
					r.underlay[0][x][y] = fbm(x / 4.0, y / 4.0, 82) > 0.2 ? VOIDSTONE_DARK : VOIDSTONE;
					// gentle swells, not per-tile jitter
					r.height[0][x][y] = 24 + (int) Math.round(fbm(x / 6.0, y / 6.0, 81) * 3);
					double d = Math.hypot(x - cx, y - cy);
					if (d > 4.5 && d < 6.0) {
						r.overlay[0][x][y] = VOID_GLASS; // a ring of violet light around the centre
					}

				} else {
					// the void: nothing at all. Level with the platform and with no floor of its own, so the platform ends
					// in a clean edge instead of sloping away into long grey shards
					r.settings[0][x][y] = MapRegion.BLOCKED;
					r.underlay[0][x][y] = 0;
					r.overlay[0][x][y] = INVISIBLE;
					r.height[0][x][y] = 24;
				}
			}
		}
		smoothDungeon(r);
		roundRim(r);
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				if (land[x][y] && Math.hypot(x - cx, y - cy) > 11) {
					r.settings[0][x][y] = MapRegion.BLOCKED; // the islets are out of reach (blocked after smoothing, so they soften too)
				}
			}
		}
		// crystals around the rim, two great ones flanking the king
		int[][] crystals = { { -8, 2 }, { 8, 2 }, { -6, -6 }, { 6, -6 }, { -3, 8 }, { 4, 8 }, { -9, -3 } }; // keep the arrival tile (0,-7) clear
		for (int i = 0; i < crystals.length; i++) {
			r.objects.add(new MapRegion.MapObject(i % 2 == 0 ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, cx + crystals[i][0] - 1,
					cy + crystals[i][1] - 1, 0, TYPE_SCENERY, i & 3));
		}
		for (int[] i : islets) {
			r.objects.add(new MapRegion.MapObject(VOID_CRYSTAL_DARK, cx + i[0] - 1, cy + i[1] - 1, 0, TYPE_SCENERY, 0));
		}
		expanse();
	}

	/** The Starless Expanse: a wide black disc in the dark, four star pillars round it, and the gate home. */
	void expanse() {
		MapRegion r = new MapRegion(STARLESS_REGION_X, STARLESS_REGION_Y);
		g.regions.put(r.id(), r);
		int bx = STARLESS_REGION_X * 64, by = STARLESS_REGION_Y * 64;
		int cx = STARLESS_X - bx, cy = STARLESS_Y - by;
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				double a = Math.atan2(y - cy, x - cx);
				double rad = 15.5 + 0.9 * Math.sin(5 * a + 1) + 0.6 * Math.sin(11 * a);
				double d = Math.hypot(x - cx, y - cy);
				if (d < rad) {
					r.underlay[0][x][y] = fbm(x / 4.0, y / 4.0, 92) > 0.15 ? VOIDSTONE_DARK : VOIDSTONE;
					r.height[0][x][y] = 24 + (int) Math.round(fbm(x / 6.0, y / 6.0, 91) * 2);
					if (d < 2.6) {
						r.overlay[0][x][y] = VOID_GLASS; // where it hangs, the floor glows
					} else if (Math.abs(d - 9) < 0.7) {
						r.overlay[0][x][y] = VOID_GLASS; // a ring between the pillars
					}
				} else {
					r.settings[0][x][y] = MapRegion.BLOCKED;
					r.underlay[0][x][y] = 0;
					r.overlay[0][x][y] = INVISIBLE;
					r.height[0][x][y] = 24;
				}
			}
		}
		smoothDungeon(r);
		roundRim(r);
		for (int[] p : STAR_PILLARS) {
			r.objects.add(new MapRegion.MapObject(STAR_PILLAR, p[0] - bx, p[1] - by, 0, TYPE_SCENERY, 0));
		}
		r.objects.add(new MapRegion.MapObject(STARLESS_GATE, EXPANSE_GATE_X - bx, EXPANSE_GATE_Y - by, 0, TYPE_SCENERY, 0));
		int[][] crystals = { { -13, 4 }, { 13, -3 }, { -5, 13 }, { 6, 13 }, { 12, 8 }, { -12, -6 } };
		for (int i = 0; i < crystals.length; i++) {
			r.objects.add(new MapRegion.MapObject(i % 2 == 0 ? VOID_CRYSTAL_DARK : VOID_CRYSTAL, cx + crystals[i][0], cy + crystals[i][1], 0,
					TYPE_SCENERY, i & 3));
		}
	}

	/* ====================================================================== */
	/*                                NYTHERA                                 */
	/* ====================================================================== */

	static final int NX0 = NYTHERA_REGION_X0 * 64, NY0 = NYTHERA_REGION_Y0 * 64, NSIZE = NYTHERA_REGIONS * 64;

	/** Value noise in [-1, 1] on a lattice of the given scale, smoothly interpolated. */
	static double vnoise(double x, double y, int seed) {
		int x0 = (int) Math.floor(x), y0 = (int) Math.floor(y);
		double fx = x - x0, fy = y - y0;
		fx = fx * fx * (3 - 2 * fx);
		fy = fy * fy * (3 - 2 * fy);
		double a = hash(x0, y0, seed), b = hash(x0 + 1, y0, seed), c = hash(x0, y0 + 1, seed), d = hash(x0 + 1, y0 + 1, seed);
		return (a + (b - a) * fx) * (1 - fy) + (c + (d - c) * fx) * fy;
	}

	static double hash(int x, int y, int seed) {
		long h = x * 374761393L + y * 668265263L + seed * 2147483647L;
		h = (h ^ (h >> 13)) * 1274126177L;
		h ^= h >> 16;
		return ((h & 0xFFFF) / 32767.5) - 1;
	}

	static double fbm(double x, double y, int seed) {
		return vnoise(x, y, seed) * 0.55 + vnoise(x * 2.1, y * 2.1, seed + 1) * 0.28 + vnoise(x * 4.3, y * 4.3, seed + 2) * 0.17;
	}

	/** Signed distance inland from Nythera's coast (negative = sea). */
	static double coast(int x, int y) {
		double dx = x - NYTHERA_CX, dy = y - NYTHERA_CY;
		double a = Math.atan2(dy, dx);
		double radius = 132 + 14 * Math.sin(2 * a + 0.7) + 9 * Math.sin(3 * a + 2.1) + 6 * Math.sin(5 * a + 0.3)
				+ 3 * Math.sin(11 * a) + 16 * fbm(x / 38.0, y / 38.0, 7);
		// a deep bay on the south-west coast
		double bay = 26 - Math.hypot(x - 1400, y - 2600);
		// plus a ragged edge: smooth curves cut into tiles leave long ruler-straight runs of shore
		return Math.min(radius - Math.hypot(dx, dy * 1.08), -bay) + ragged(x, y, 51);
	}

	/** Small-scale wobble (about +-2 tiles) for shores, so no stretch of water's edge runs ruler-straight. */
	static double ragged(int x, int y, int seed) {
		return 1.5 * vnoise(x / 6.0, y / 6.0, seed) + 1.0 * vnoise(x / 2.5, y / 2.5, seed + 1);
	}

	/** The northern mountains: 0 outside, up to 1 on the ridge line. */
	static double mountain(int x, int y) {
		double ridgeY = 2792 + 12 * Math.sin((x - NYTHERA_CX) / 31.0) + 6 * fbm(x / 20.0, 3, 11);
		double across = Math.abs(y - ridgeY) / (20 + 8 * fbm(x / 25.0, 9, 12));
		double along = smooth(1 - Math.abs(x - NYTHERA_CX + 10) / 105.0);
		return Math.max(0, 1 - across) * along;
	}

	/** The lake in the west-centre. */
	static double lake(int x, int y) {
		return 15 + 5 * fbm(x / 9.0, y / 9.0, 21) - Math.hypot(x - 1440, (y - 2716) * 1.3) + ragged(x, y, 53);
	}

	/** The river: from a spring in the mountains down to the lake, then out of it to the south-west bay. */
	static final int[][] RIVER = { { 1476, 2796 }, { 1470, 2784 }, { 1474, 2772 }, { 1464, 2762 }, { 1458, 2748 }, { 1448, 2736 },
			{ 1440, 2718 }, { 1432, 2700 }, { 1420, 2692 }, { 1424, 2676 }, { 1412, 2664 }, { 1414, 2648 }, { 1402, 2636 },
			{ 1398, 2618 } };

	/** Distance from each tile to the river's edge (negative = in the water), filled by {@link #buildRiver()}. */
	private final float[][] riverField = new float[NSIZE][NSIZE];

	double riverDistance(int x, int y) {
		int lx = x - NX0, ly = y - NY0;
		// its banks wobble a little on top of the meander: a river's edge is never ruled
		return lx < 0 || ly < 0 || lx >= NSIZE || ly >= NSIZE ? 99 : riverField[lx][ly] + 0.5 * ragged(x, y, 55);
	}

	/**
	 * Points along a smooth curve (Catmull-Rom) through the waypoints, about one per tile, nudged sideways by a gentle
	 * wave so the line meanders between them instead of running straight.
	 */
	static List<double[]> meander(int[][] pts, double amplitude, double wavelength, int seed) {
		List<double[]> out = new ArrayList<>();
		double travelled = 0;
		for (int i = 0; i + 1 < pts.length; i++) {
			int[] p0 = pts[Math.max(0, i - 1)], p1 = pts[i], p2 = pts[i + 1], p3 = pts[Math.min(pts.length - 1, i + 2)];
			int steps = (int) Math.ceil(Math.hypot(p2[0] - p1[0], p2[1] - p1[1]) * 2);
			for (int k = 0; k < steps; k++) {
				double t = k / (double) steps, t2 = t * t, t3 = t2 * t;
				double x = 0.5 * (2 * p1[0] + (-p0[0] + p2[0]) * t + (2 * p0[0] - 5 * p1[0] + 4 * p2[0] - p3[0]) * t2
						+ (-p0[0] + 3 * p1[0] - 3 * p2[0] + p3[0]) * t3);
				double y = 0.5 * (2 * p1[1] + (-p0[1] + p2[1]) * t + (2 * p0[1] - 5 * p1[1] + 4 * p2[1] - p3[1]) * t2
						+ (-p0[1] + 3 * p1[1] - 3 * p2[1] + p3[1]) * t3);
				double dx = p2[0] - p1[0], dy = p2[1] - p1[1], len = Math.hypot(dx, dy);
				travelled += 0.5;
				// the wave fades out at the ends so the line still starts and finishes where it should
				double fade = Math.min(1, Math.min(i + t, pts.length - 1 - i - t));
				double off = amplitude * fade * (Math.sin(travelled / wavelength * Math.PI * 2) * 0.7
						+ vnoise(travelled / wavelength, 0.5, seed) * 0.6);
				out.add(new double[] { x - dy / len * off, y + dx / len * off, travelled });
			}
		}
		return out;
	}

	void buildRiver() {
		for (float[] row : riverField) {
			java.util.Arrays.fill(row, 99f);
		}
		for (double[] pt : meander(RIVER, 3.2, 16, 33)) {
			double half = 1.1 + 0.9 * (0.5 + 0.5 * vnoise(pt[2] / 11, 0, 34)) + pt[2] / 260.0; // widens downstream
			for (int x = (int) pt[0] - 8; x <= pt[0] + 8; x++) {
				for (int y = (int) pt[1] - 8; y <= pt[1] + 8; y++) {
					int lx = x - NX0, ly = y - NY0;
					if (lx >= 0 && ly >= 0 && lx < NSIZE && ly < NSIZE) {
						float d = (float) (Math.hypot(x + 0.5 - pt[0], y + 0.5 - pt[1]) - half);
						riverField[lx][ly] = Math.min(riverField[lx][ly], d);
					}
				}
			}
		}
	}

	static double segmentDistance(double px, double py, int[] a, int[] b) {
		double vx = b[0] - a[0], vy = b[1] - a[1];
		double t = Math.max(0, Math.min(1, ((px - a[0]) * vx + (py - a[1]) * vy) / (vx * vx + vy * vy)));
		return Math.hypot(px - a[0] - t * vx, py - a[1] - t * vy);
	}

	/** The Hollow Scar in the east, where the Hollow King's shadow has withered the land: 0..1. */
	static double scar(int x, int y) {
		return smooth(1 - Math.hypot((x - 1600) / 34.0, (y - 2738) / 28.0) + 0.25 * fbm(x / 12.0, y / 12.0, 41));
	}

	/** Starfall Crater: 0..1 towards its centre. */
	static double crater(int x, int y) {
		return smooth(1 - Math.hypot(x - CRATER_X, y - CRATER_Y) / 9.0);
	}

	/** Forest density 0..1: thick in the west and the foothills, open meadows in the middle and south. */
	static double forest(int x, int y) {
		double west = smooth((1480 - x) / 60.0);
		double foothills = smooth(1 - Math.abs(y - 2768) / 18.0) * 0.8;
		return Math.max(0, Math.max(west, foothills) * 0.9 + 0.45 * fbm(x / 18.0, y / 18.0, 51) - 0.1);
	}

	void nythera() {
		for (int rx = 0; rx < NYTHERA_REGIONS; rx++) {
			for (int ry = 0; ry < NYTHERA_REGIONS; ry++) {
				MapRegion r = new MapRegion(NYTHERA_REGION_X0 + rx, NYTHERA_REGION_Y0 + ry);
				g.regions.put(r.id(), r);
			}
		}
		buildRiver();
		for (int x = NX0; x < NX0 + NSIZE; x++) {
			for (int y = NY0; y < NY0 + NSIZE; y++) {
				terrain(x, y);
			}
		}
		slopes();
		// the old stone road from the crater to the Broken Gate, then dirt tracks north to the pass and east to the scar
		road(2.6, STONE_ROAD, new int[][] { { 1564, 2662 }, { 1558, 2670 }, { 1546, 2676 }, { 1538, 2688 }, { 1526, 2694 },
				{ 1518, 2704 }, { 1510, 2710 } }, 2.2, 11, 91);
		road(2.2, DIRT_ROAD, new int[][] { { 1510, 2714 }, { 1506, 2726 }, { 1496, 2732 }, { 1492, 2746 }, { 1484, 2754 },
				{ 1482, 2768 }, { 1480, 2782 }, { 1482, 2800 }, { 1483, 2804 } }, 2.4, 10, 92); // to the inn's yard
		road(2.0, DIRT_ROAD, new int[][] { { 1514, 2712 }, { 1526, 2718 }, { 1538, 2728 }, { 1552, 2730 }, { 1566, 2738 },
				{ 1578, 2736 } }, 2.2, 10, 93);
		craterAndCamp();
		ruins();
		scarScenery();
		ores();
		camps();
		places();
		vegetation();
		smoothOverlays();
		g.drownedScenery(NX0, NY0, NSIZE);
		wildlife();
		dungeons();
		writeSpawns();
	}

	void terrain(int x, int y) {
		double inland = coast(x, y);
		if (inland < 0) {
			g.sea(x, y);
			return;
		}
		double m = mountain(x, y);
		double rolling = 5 * fbm(x / 26.0, y / 26.0, 61) + 2 * fbm(x / 8.0, y / 8.0, 62);
		double h = 3 + Math.min(inland, 10) * 0.9 + Math.max(0, rolling + 2) * smooth(inland / 8)
				+ 70 * Math.pow(m, 1.6) * (0.8 + 0.4 * fbm(x / 7.0, y / 7.0, 63));
		double lk = lake(x, y), river = riverDistance(x, y);
		boolean water = lk > 0 || river < 0 && m < 0.6;
		if (water) {
			h = Math.min(h, 2);
		} else if (lk > -4 || river < 3) {
			h = Math.min(h, 3 + Math.max(0, Math.min(lk < -4 ? 99 : -lk, river)) * 1.6); // banks slope down to the water
		}
		h -= 9 * crater(x, y); // the crater's bowl
		g.setHeight(x, y, (int) Math.round(Math.max(1, h)));
		if (water) {
			g.setOverlay(x, y, WATER);
			g.block(x, y);
			g.setUnderlay(x, y, SAND_FLOOR);
			return;
		}
		int floor;
		double n = fbm(x / 6.0, y / 6.0, 71);
		double sc = scar(x, y);
		if (inland < 3.5) {
			floor = SAND_FLOOR;
		} else if (m > 0.55) {
			floor = n > 0.2 ? MOUNTAIN : SLOPE;
		} else if (m > 0.3) {
			floor = n > 0.1 ? SLOPE : GRASS_D;
		} else if (sc > 0.15) {
			floor = sc > 0.6 ? (n > 0.2 ? ASH : BURGH_SOIL) : n > 0 ? DEAD_GRASS : MUDDY_GRASS;
		} else if (forest(x, y) > 0.55) {
			floor = n > 0.25 ? FOREST_FLOOR : n > -0.2 ? GRASS_D : GRASS_E;
		} else {
			floor = n > 0.35 ? GRASS_B : n > 0 ? GRASS_A : n > -0.35 ? GRASS_C : GRASS_E;
		}
		g.setUnderlay(x, y, floor);
		if (sc > 0.7 && fbm(x / 4.0, y / 4.0, 72) > 0.3) {
			g.setOverlay(x, y, VOID_ASH); // patches where the Scar has burned the ground to ash
		}
	}

	/** Rock faces too steep to climb are blocked, so the mountains are walls with passes through them. */
	void slopes() {
		for (int x = NX0 + 1; x < NX0 + NSIZE - 1; x++) {
			for (int y = NY0 + 1; y < NY0 + NSIZE - 1; y++) {
				if (mountain(x, y) < 0.2 || coast(x, y) < 0) {
					continue;
				}
				int h = g.heightAt(x, y), steep = 0;
				for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
					steep = Math.max(steep, Math.abs(h - g.heightAt(x + d[0], y + d[1])));
				}
				if (steep > 9) {
					g.block(x, y);
					g.setUnderlay(x, y, MOUNTAIN);
				}
			}
		}
		// a pass over the ridge where the north road goes
		for (int x = 1474; x <= 1486; x++) {
			for (int y = 2760; y <= 2820; y++) {
				MapRegion r = g.regionAt(x, y);
				r.settings[0][x & 63][y & 63] &= ~MapRegion.BLOCKED;
			}
		}
	}

	/** A winding road: every tile within width/2 of the meandering line gets the floor (no gaps, no diagonal-only joins). */
	void road(double width, int floor, int[][] points, double amplitude, double wavelength, int seed) {
		for (double[] pt : meander(points, amplitude, wavelength, seed)) {
			for (int x = (int) pt[0] - 3; x <= pt[0] + 3; x++) {
				for (int y = (int) pt[1] - 3; y <= pt[1] + 3; y++) {
					if (Math.hypot(x + 0.5 - pt[0], y + 0.5 - pt[1]) <= width / 2 && g.overlayAt(x, y) != WATER) {
						g.setOverlay(x, y, floor);
						g.removeObjectsAt(x, y);
					}
				}
			}
		}
	}

	void craterAndCamp() {
		// scorched, glassy ground where you fell, crystals thrown up around the rim
		for (int x = CRATER_X - 12; x <= CRATER_X + 12; x++) {
			for (int y = CRATER_Y - 12; y <= CRATER_Y + 12; y++) {
				double d = Math.hypot(x - CRATER_X, y - CRATER_Y);
				if (d < 7.5) {
					g.setUnderlay(x, y, d < 4 ? ASH : BURGH_SOIL);
					if (d < 3.4) {
						g.setOverlay(x, y, VOID_GLASS); // glassy violet where the rift burned the ground
					}
				} else if (d < 11 && fbm(x / 3.0, y / 3.0, 81) > 0) {
					g.setUnderlay(x, y, DEAD_GRASS);
				}
			}
		}
		g.object(RIFT, RIFT_X - 1, RIFT_Y, TYPE_SCENERY, 0);
		int[][] crystals = { { -7, 2 }, { 6, 4 }, { 7, -4 }, { -5, -6 }, { 1, 8 }, { -9, -1 } };
		for (int i = 0; i < crystals.length; i++) {
			g.object(i % 2 == 0 ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, CRATER_X + crystals[i][0] - 1, CRATER_Y + crystals[i][1] - 1,
					TYPE_SCENERY, i & 3);
		}
		// the ground still smoulders where you fell
		g.object(FIRE, CRATER_X + 3, CRATER_Y - 3, TYPE_SCENERY, 0);
		g.object(FIRE, CRATER_X - 4, CRATER_Y + 2, TYPE_SCENERY, 0);
		// Oren's camp on the rim: he pitched his tent to watch the crater, with its back to the wind off the hills
		tent(OREN_X - 4, OREN_Y + 1, DOOR_EAST);
		campfire(OREN_X + 1, OREN_Y + 1, true);
		seat(OREN_X + 1, OREN_Y + 3, ALONG_X);
		seat(OREN_X + 3, OREN_Y + 1, ALONG_Y);
		g.object(LOGS, OREN_X + 3, OREN_Y + 3, TYPE_SCENERY, 1); // firewood, stacked handy
		g.object(CRATE, OREN_X - 4, OREN_Y - 1, TYPE_SCENERY, 0);
		g.object(SACKS, OREN_X - 3, OREN_Y - 1, TYPE_SCENERY, 1);
	}

	/**
	 * The Broken Gate: the old kingdom's gate across the stone road, where it comes up from the crater. The west tower
	 * still stands to its full height with the stub of the arch reaching out over the road; the east tower is broken
	 * off at half height; the arch lies in pieces beside the road, half burying the hatch to the vault. Above it on the
	 * hill, a circle of plain standing stones, older than the gate.
	 */
	void ruins() {
		int[][] towers = { { GATE_X, GATE_Y - 6 }, { GATE_X + GATE_SPAN, GATE_Y - 6 } };
		for (int[] t : towers) {
			for (int dx = -1; dx <= 2; dx++) {
				for (int dy = -1; dy <= 2; dy++) {
					g.removeObjectsAt(t[0] + dx, t[1] + dy);
				}
			}
		}
		keepClear.add(new int[] { towers[0][0] - 5, towers[0][1] - 6, towers[1][0] + 4, towers[1][1] + 2 }); // and the road up to it
		g.object(GATE_TOWER, towers[0][0], towers[0][1], TYPE_SCENERY, 2); // its arch reaches east, towards its partner
		g.object(GATE_TOWER_BROKEN, towers[1][0], towers[1][1], TYPE_SCENERY, 0);
		// the curtain wall running out either side, crumbling away with distance from the towers
		int wy = towers[0][1];
		int[][] wall = { { towers[0][0] - 1, wy, GATE_WALL }, { towers[0][0] - 2, wy, GATE_WALL_LOW }, { towers[0][0] - 3, wy, GATE_WALL_LOW },
				{ towers[1][0] + 2, wy + 1, GATE_WALL }, { towers[1][0] + 3, wy + 1, GATE_WALL_LOW } };
		for (int[] w : wall) {
			g.removeObjectsAt(w[0], w[1]);
			g.object(w[2], w[0], w[1], TYPE_SCENERY, w[0] < towers[0][0] ? 2 : 0); // stepping down away from the gate
		}
		g.object(RUBBLE, towers[0][0] - 5, wy, TYPE_SCENERY, 0);
		g.removeObjectsAt(HATCH_X + 1, HATCH_Y + 1);
		g.object(FALLEN_MASONRY, HATCH_X + 1, HATCH_Y + 1, TYPE_SCENERY, 1);
		g.object(RUBBLE, towers[1][0] + 2, towers[1][1] + 2, TYPE_SCENERY, 1);
		g.object(RUBBLE, towers[0][0] - 1, towers[0][1] + 2, TYPE_SCENERY, 2);
		int[] stones = { MENHIR, MENHIR_2, MENHIR_3 };
		for (int i = 0; i < 7; i++) {
			double a = i * Math.PI * 2 / 7 + 0.3;
			int x = MAELIS_X + (int) Math.round(Math.cos(a) * 7), y = MAELIS_Y + (int) Math.round(Math.sin(a) * 7);
			g.removeObjectsAt(x, y);
			if (i != 4) { // one has fallen, long ago
				g.object(stones[i % 3], x, y, TYPE_SCENERY, i & 3);
			}
		}
	}

	void scarScenery() {
		for (int x = 1556; x < 1650; x++) {
			for (int y = 2700; y < 2780; y++) {
				double s = scar(x, y);
				if (s < 0.3 || coast(x, y) < 4 || !free(x, y, 2)) {
					continue;
				}
				int roll = random.nextInt(1000);
				if (roll < 10 * s) {
					g.object(random.nextBoolean() ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, x, y, TYPE_SCENERY, random.nextInt(4));
				} else if (roll < 45 * s) {
					g.object(random.nextBoolean() ? DEAD_TREE : BIG_DEAD_TREE, x, y, TYPE_SCENERY, random.nextInt(4));
				}
			}
		}
	}

	/** Clusters of ore rocks in the foothills and on the beaches' rocky points. */
	void ores() {
		int[][] clusters = { { 1452, 2768, COPPER, TIN }, { 1532, 2772, IRON, COAL }, { 1500, 2778, COAL, MITHRIL },
				{ 1420, 2760, IRON, TIN }, { 1556, 2700, COPPER, IRON } };
		for (int[] c : clusters) {
			int placed = 0;
			for (int attempt = 0; attempt < 60 && placed < 7; attempt++) {
				int x = c[0] + random.nextInt(9) - 4, y = c[1] + random.nextInt(9) - 4;
				if (free(x, y, 0) && !blocked(x, y) && coast(x, y) > 4) {
					g.object(placed % 2 == 0 ? c[2] : c[3], x, y, TYPE_SCENERY, random.nextInt(4));
					placed++;
				}
			}
		}
	}

	void vegetation() {
		for (int pass = 0; pass < 2; pass++) {
			for (int x = NX0 + 2; x < NX0 + NSIZE - 2; x++) {
				for (int y = NY0 + 2; y < NY0 + NSIZE - 2; y++) {
					double inland = coast(x, y);
					if (inland < 1 || blocked(x, y) || !free(x, y, 0) || crater(x, y) > 0 || scar(x, y) > 0.3
							|| Math.hypot(x - OREN_X, y - OREN_Y) < 6 || nearCamp(x, y)) {
						continue;
					}
					if (pass == 0) {
						bigPlant(x, y, inland);
					} else {
						groundCover(x, y, inland);
					}
				}
			}
		}
	}

	void bigPlant(int x, int y, double inland) {
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		double m = mountain(x, y), f = forest(x, y);
		boolean nearWater = lake(x, y) > -5 || riverDistance(x, y) < 4;
		if (inland < 4) {
			if (roll < 8 && free(x, y, 2)) {
				g.object(SMALL_MOSSY_ROCK, x, y, TYPE_SCENERY, rot);
			}
			return;
		}
		if (m > 0.35) {
			if (roll < 25 && free(x, y, 2)) {
				g.object(random.nextBoolean() ? EVERGREEN : EVERGREEN_BIG, x, y, TYPE_SCENERY, rot);
			} else if (roll < 36 && free(x, y, 2)) {
				g.object(MOSSY_ROCK, x, y, TYPE_SCENERY, rot); // 2x2: free(x, y, 2) covers it
			} else if (roll < 45 && free(x, y, 1)) {
				g.object(SMALL_MOSSY_ROCK, x, y, TYPE_SCENERY, rot);
			}
			return;
		}
		int treeChance = (int) (6 + 150 * f);
		if (roll < treeChance && free(x, y, 2)) {
			int t;
			if (nearWater && random.nextInt(3) == 0) {
				t = WILLOW;
			} else {
				int k = random.nextInt(100);
				t = k < 45 ? (k % 2 == 0 ? TREE : TREE_2) : k < 75 ? OAK : k < 88 ? (y > 2750 ? MAPLE : EVERGREEN) : k < 95 ? MAPLE : YEW;
			}
			g.object(t, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 10 && free(x, y, 2)) {
			g.object(random.nextInt(3) == 0 ? BIG_BUSH : random.nextBoolean() ? BUSH : BUSH_2, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 12 && free(x, y, 3) && f > 0.4) {
			g.object(FALLEN_TREE, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 15 && free(x, y, 1)) {
			g.object(SMALL_MOSSY_ROCK, x, y, TYPE_SCENERY, rot);
		}
	}

	void groundCover(int x, int y, double inland) {
		if (inland < 4 || mountain(x, y) > 0.35) {
			return;
		}
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		double f = forest(x, y);
		if (f < 0.5 && roll < 40) {
			g.object(GROUND_FLOWERS + random.nextInt(4), x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 60 && f >= 0.5) {
			g.object(FERN + random.nextInt(3), x, y, TYPE_SCENERY, rot);
		} else if (roll < 75) {
			g.object(FLOWERS + random.nextInt(2), x, y, TYPE_SCENERY, rot);
		} else if (roll < 110) {
			g.object(LONG_GRASS, x, y, TYPE_GROUND_DECOR, rot);
		}
	}

	/* ====================================================================== */
	/*                       CAMPS - the lived-in bits                        */
	/* ====================================================================== */

	static final int ROW_BOAT = 2515, NET_DRYING = 14972, CRATE = 9533, CRATES = 9535, BARREL = 9519,
			STACKED_BARRELS = 4641, FLAX = 2646, CABBAGE = 1161, WHEAT = 313, SCARECROW = 297, HAYSTACK = 10086,
			HAY_BALES = 10084, WELL = 884, TROUGH = 2044, CART = 307, PLOUGH = 296, LOG_PILE = 309, AXE_LOGS = 5582,
			STUMP = 1341, BEDROLL = 2379, SIGNPOST = 1034, FLAGPOLE = 3680, SPIT_ROAST = 4267, SACKS = 7512,
			WOOD_FLOOR = 5, TRODDEN = 96;

	static final int[][] CAMPS = { { FISH_CAMP_X, FISH_CAMP_Y, 9 }, { CROFT_X, CROFT_Y, 13 }, { WOOD_CAMP_X, WOOD_CAMP_Y, 7 },
			{ WATCH_X, WATCH_Y, 6 }, { GATEHOLD_X, GATEHOLD_Y, 13 }, { QUARRY_X, QUARRY_Y, 10 }, { INN_X, INN_Y, 10 },
			{ SMUGGLER_COVE_X, SMUGGLER_COVE_Y, 9 }, { SILKWOOD_X, SILKWOOD_Y, 10 }, { BARROW_DOOR_X, BARROW_DOOR_Y + 5, 9 },
			{ FISSURE_X, FISSURE_Y, 4 }, { FROST_CAVE_X - 3, FROST_CAVE_Y, 4 }, { MAELIS_X, MAELIS_Y, 6 } };

	boolean nearCamp(int x, int y) {
		for (int[] c : CAMPS) {
			if (Math.hypot(x - c[0], y - c[1]) < c[2]) {
				return true;
			}
		}
		for (int[] w : WAYSTONE_SPOTS) {
			if (Math.abs(x - w[0]) <= 2 && Math.abs(y - w[1]) <= 2) {
				return true;
			}
		}
		for (int[] k : keepClear) {
			if (x >= k[0] - 1 && x <= k[2] + 1 && y >= k[1] - 1 && y <= k[3] + 1) {
				return true;
			}
		}
		return Math.hypot(x - GATE_X, y - GATE_Y) < 7;
	}

	/** Clears scenery around a camp, eases the ground towards level and wears a little path into the grass. */
	void site(int cx, int cy, int r) {
		int sum = 0, n = 0;
		for (int x = cx - 2; x <= cx + 2; x++) {
			for (int y = cy - 2; y <= cy + 2; y++) {
				sum += g.heightAt(x, y);
				n++;
			}
		}
		int level = sum / n;
		for (int x = cx - r; x <= cx + r; x++) {
			for (int y = cy - r; y <= cy + r; y++) {
				double d = Math.hypot(x - cx, y - cy);
				if (d > r || g.overlayAt(x, y) == WATER) {
					continue;
				}
				g.removeObjectsAt(x, y);
				double t = Math.min(1, Math.max(0, (r - d) / (r * 0.5)));
				g.setHeight(x, y, (int) Math.round(g.heightAt(x, y) * (1 - t) + level * t));
				if (d < r * 0.55 && fbm(x / 2.0, y / 2.0, 97) > -0.1) {
					g.setUnderlay(x, y, TRODDEN);
				}
			}
		}
	}

	/* Rotations of the hand-built camp pieces: which way a tent's door faces, which way a log seat lies. */
	static final int DOOR_SOUTH = 0, DOOR_WEST = 1, DOOR_NORTH = 2, DOOR_EAST = 3, ALONG_X = 0, ALONG_Y = 1;

	/** A canvas tent (2x2, its south-west tile at x, y) with its door facing the way given. */
	void tent(int x, int y, int door) {
		for (int dx = 0; dx < 2; dx++) {
			for (int dy = 0; dy < 2; dy++) {
				g.removeObjectsAt(x + dx, y + dy);
			}
		}
		g.flatten(x, y, x + 2, y + 2); // pitched on level ground: a tent on a slope hangs in the air on one side
		g.object(CAMP_TENT, x, y, TYPE_SCENERY, door);
	}

	/** A fire burning in a ring of hearth stones, with a cooking tripod over it if anyone here cooks. */
	void campfire(int x, int y, boolean cooking) {
		g.removeObjectsAt(x, y);
		g.object(cooking ? COOK_FIRE_RING : FIRE_RING, x, y, TYPE_GROUND_DECOR, 0);
		g.object(FIRE, x, y, TYPE_SCENERY, 0);
	}

	/** A log to sit on. */
	void seat(int x, int y, int along) {
		g.removeObjectsAt(x, y);
		g.object(LOG_SEAT, x, y, TYPE_SCENERY, along);
	}

	static final int FENCE = 980;
	/** Tiles between the Broken Gate's two towers, centre to centre (the arch in WorldProps is built to span it). */
	static final int GATE_SPAN = WorldProps.GATE_SPAN;

	/**
	 * A wooden fence round the tiles x0..x1, y0..y1 (inclusive), clearing whatever grew inside but the crops, with a
	 * one-tile gap to walk through beside the edge tile (gapX, gapY) (none if gapX < 0). A wall sits on one edge of a
	 * tile (rotation 0 west, 1 north, 2 east, 3 south) and a tile holds one, so the west and east sides run along the
	 * inside tiles and the north and south sides along the row of tiles just outside: the corners meet without sharing.
	 */
	void fence(int x0, int y0, int x1, int y1, int gapX, int gapY) {
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				g.removeObjects(x, y, o -> o.type != TYPE_SCENERY || !isCrop(o.id));
			}
		}
		for (int y = y0; y <= y1; y++) {
			fencePiece(x0, y, 0, x0 == gapX && y == gapY);
			fencePiece(x1, y, 2, x1 == gapX && y == gapY);
		}
		for (int x = x0; x <= x1; x++) {
			fencePiece(x, y1 + 1, 3, x == gapX && y1 == gapY && gapX != x0 && gapX != x1);
			fencePiece(x, y0 - 1, 1, x == gapX && y0 == gapY && gapX != x0 && gapX != x1);
		}
		keepClear.add(new int[] { x0, y0, x1, y1 });
	}

	void fencePiece(int x, int y, int rot, boolean gap) {
		g.removeObjects(x, y, o -> o.type <= 3 || !gap && o.type == TYPE_SCENERY && !isCrop(o.id));
		if (!gap) {
			g.object(FENCE, x, y, 0, rot);
		}
	}

	static boolean isCrop(int id) {
		return id == FLAX || id == WHEAT || id == CABBAGE || id == SCARECROW;
	}

	/** Rectangles the scattered scenery and wildlife stay out of (fields, pens). */
	final List<int[]> keepClear = new ArrayList<>();

	void camps() {
		fishingCamp();
		croft();
		woodCamp();
		scarWatch();
		// a waymarker where the roads part at the Broken Gate
		// (beside the fallen masonry, not in it)
		g.removeObjectsAt(GATE_X + 6, GATE_Y + 3);
		g.object(SIGNPOST, GATE_X + 6, GATE_Y + 3, TYPE_SCENERY, 1);
	}

	/** Netwright's landing: a shack, a jetty out into the lake, boats, nets and the day's catch. */
	void fishingCamp() {
		int cx = FISH_CAMP_X, cy = FISH_CAMP_Y;
		site(cx, cy, 9);
		g.stampBuilding(3670, 2963, 3678, 2972, cx + 2, cy - 3); // a fisher's shack, door to the south
		// the jetty: from the shore out over the water
		int x = cx - 2;
		while (x > cx - 30 && g.overlayAt(x, cy) != WATER) {
			x--;
		}
		int shore = x + 1, end = x - 5;
		for (int jx = shore; jx >= end; jx--) {
			for (int jy = cy; jy <= cy + 1; jy++) {
				MapRegion r = g.regionAt(jx, jy);
				r.overlay[0][jx & 63][jy & 63] = WOOD_FLOOR;
				r.settings[0][jx & 63][jy & 63] = 0;
				r.height[0][jx & 63][jy & 63] = Math.max(3, g.heightAt(jx, jy));
				g.removeObjectsAt(jx, jy);
			}
		}
		System.out.println("Fishing camp: jetty " + shore + ".." + end + " at y " + cy + "-" + (cy + 1) + ", spots at "
				+ (end - 1) + "," + cy + " and " + (shore - 2) + "," + (cy + 3) + " and " + (shore - 2) + "," + (cy - 2));
		g.object(ROW_BOAT, end + 1, cy + 3, TYPE_SCENERY, 1);
		g.object(ROW_BOAT, shore - 2, cy - 3, TYPE_SCENERY, 3);
		g.object(NET_DRYING, cx - 1, cy + 3, TYPE_SCENERY, 1);
		g.object(BARREL, cx, cy - 2, TYPE_SCENERY, 0);
		g.object(STACKED_BARRELS, cx + 1, cy - 4, TYPE_SCENERY, 0);
		g.object(CRATES, shore + 2, cy - 3, TYPE_SCENERY, 0);
		int crateX = shore;
		while (water(crateX, cy + 3) || water(crateX - 1, cy + 3)) { // on the bank, not in the shallows
			crateX++;
		}
		g.object(CRATE, crateX, cy + 3, TYPE_SCENERY, 0);
		g.object(SPIT_ROAST, cx - 1, cy - 5, TYPE_SCENERY, 0); // the day's catch, roasting
	}

	/**
	 * Tollan's Croft: a farmhouse in a yard, the flax and wheat field to the north and the cabbage patch to the east
	 * fenced against the animals, a sheep pen and a cow pasture to the south, and the hay, well and trough between.
	 */
	void croft() {
		int cx = CROFT_X, cy = CROFT_Y;
		site(cx, cy, 13);
		g.stampBuilding(3652, 2969, 3658, 2979, cx - 11, cy - 4); // the farmhouse
		for (int x = cx + 1; x <= cx + 9; x++) {
			for (int y = cy - 8; y <= cy - 2; y++) {
				g.setUnderlay(x, y, MUD_FIELD);
				if (y % 2 == 0) {
					g.object(y < cy - 5 ? FLAX : WHEAT, x, y, TYPE_SCENERY, random.nextInt(4));
				}
			}
		}
		for (int x = cx + 2; x <= cx + 7; x++) {
			for (int y = cy + 3; y <= cy + 6; y++) {
				g.setUnderlay(x, y, MUD_FIELD);
				if ((x + y) % 2 == 0) {
					g.object(CABBAGE, x, y, TYPE_SCENERY, random.nextInt(4));
				}
			}
		}
		g.removeObjectsAt(cx + 5, cy - 5);
		g.object(SCARECROW, cx + 5, cy - 5, TYPE_SCENERY, 1);
		g.object(HAYSTACK, cx - 2, cy + 6, TYPE_SCENERY, 0);
		g.object(HAY_BALES, cx, cy + 7, TYPE_SCENERY, 1);
		g.object(WELL, cx - 1, cy + 1, TYPE_SCENERY, 0);
		// fences: the fields' gaps face the yard, the pens open towards the trough and the hay
		fence(cx, cy - 9, cx + 10, cy - 1, cx, cy - 4);
		fence(cx + 1, cy + 2, cx + 8, cy + 7, cx + 1, cy + 4);
		fence(cx - 9, cy + 9, cx - 1, cy + 15, cx - 1, cy + 11);
		fence(cx + 2, cy + 10, cx + 11, cy + 17, cx + 2, cy + 12);
		g.object(TROUGH, cx - 4, cy + 10, TYPE_SCENERY, 1); // water for the sheep
		g.object(TROUGH, cx + 6, cy + 11, TYPE_SCENERY, 1);
		g.object(HAY_BALES, cx - 6, cy + 14, TYPE_SCENERY, 0);
		g.object(CART, cx - 3, cy - 6, TYPE_SCENERY, 1); // in the yard, by the field gate
		g.object(PLOUGH, cx + 11, cy - 11, TYPE_SCENERY, 1); // left at the field's corner
		g.object(SACKS, cx - 3, cy + 2, TYPE_SCENERY, 0);
	}

	/**
	 * Hob's camp on the forest edge: two tents facing a cookfire, logs to sit on round it, the woodpile and chopping
	 * block on the forest side, a cart half-loaded for Gatehold, and the stumps of what they've already felled.
	 */
	void woodCamp() {
		int cx = WOOD_CAMP_X, cy = WOOD_CAMP_Y;
		site(cx, cy, 7);
		tent(cx - 4, cy, DOOR_EAST);
		tent(cx, cy + 3, DOOR_SOUTH);
		campfire(cx + 1, cy, true);
		seat(cx + 1, cy - 2, ALONG_X);
		seat(cx + 3, cy, ALONG_Y);
		g.object(BEDROLL, cx - 1, cy - 2, TYPE_SCENERY, 1); // someone sleeps out by the fire
		g.object(LOG_PILE, cx + 4, cy + 3, TYPE_SCENERY, 0);
		g.object(AXE_LOGS, cx + 3, cy + 5, TYPE_SCENERY, 1);
		g.object(CART, cx - 5, cy - 4, TYPE_SCENERY, 1);
		g.object(LOGS, cx - 5, cy - 6, TYPE_SCENERY, 0); // unloaded beside the cart
		int[][] stumps = { { -6, 4 }, { -3, 6 }, { 1, 6 }, { 5, 6 }, { -7, 1 }, { 6, 3 } }; // towards the trees
		for (int[] st : stumps) {
			g.object(STUMP + random.nextInt(4), cx + st[0], cy + st[1], TYPE_SCENERY, random.nextInt(4));
		}
	}

	/**
	 * The Scar Watch: a lookout on the edge of the dying land - one tent, a fire, the Wardens' flag on its pole where
	 * the Scar can see it, supplies stacked out of the weather, and the hollowed trees the Scar has already reached.
	 */
	void scarWatch() {
		int cx = WATCH_X, cy = WATCH_Y;
		site(cx, cy, 6);
		tent(cx - 5, cy - 1, DOOR_EAST);
		campfire(cx - 1, cy - 1, false);
		seat(cx - 1, cy - 3, ALONG_X);
		g.object(FLAGPOLE, cx + 2, cy + 2, TYPE_SCENERY, 0);
		g.object(CRATES, cx - 5, cy + 2, TYPE_SCENERY, 0);
		g.object(BARREL, cx - 3, cy + 2, TYPE_SCENERY, 0);
		// the Scar reaching for the trees at its edge
		placeNear(HOLLOWED_TREE, cx + 6, cy - 5, 0);
		placeNear(HOLLOWED_TREE, cx + 8, cy + 4, 0);
		placeNear(HOLLOWED_TREE, cx + 5, cy + 8, 0);
	}

	/* ====================================================================== */
	/*        PLACES - Gatehold, the quarry, the inn, the cove, Silkwood       */
	/* ====================================================================== */

	static final int BANK_BOOTH = 2213, FURNACE = 11666, ANVIL = 2783, RANGE = 2728, MARKET_STALL = 634, BENCH = 1104,
			TABLE = 595, MINE_CART = 637, MAGIC_TREE = 1306, GRAVE = 404, BIG_GRAVE = 406, SKELETON_DECOR = 5358, WEB = 733,
			IRON_2 = 2093, COAL_2 = 2097, GOLD = 2098, GOLD_2 = 2099, SILVER = 2100, MITHRIL_2 = 2103, ADAMANT = 2104,
			ADAMANT_2 = 2105, RUNITE = 2106, GUARD = 9, BANKER = 494, SPIDER = 60, BANDIT = 1883, PIRATE = 184;

	/** A person or creature placed with the scenery (people stand at fixed spots, walk 1 = mill about). */
	void person(int id, int x, int y, int walk) {
		spawns.add(new int[] { id, x, y, walk });
	}

	void places() {
		gatehold();
		quarry();
		inn();
		cove();
		bridgeAndSilkwood();
		barrowMound();
		fissure();
		frostCaveMouth();
		// last, so nothing above clears them away
		maelisCamp();
		waystones();
		sagaPlaces();
	}

	/** Hold the Gate's barricades across the Scar road; Heartforged's fallen star in the crater. */
	void sagaPlaces() {
		for (int[] b : BARRICADE_SPOTS) {
			g.removeObjectsAt(b[0], b[1]);
			g.object(BROKEN_BARRICADE, b[0], b[1], TYPE_SCENERY, 1);
		}
		for (int x = RAID_HOLD_X - 1; x <= RAID_HOLD_X + 1; x++) {
			for (int y = RAID_HOLD_Y - 1; y <= RAID_HOLD_Y + 1; y++) {
				g.removeObjectsAt(x, y);
			}
		}
		for (int x = STAR_X - 1; x <= STAR_X + 2; x++) {
			for (int y = STAR_Y - 1; y <= STAR_Y + 2; y++) {
				g.removeObjectsAt(x, y);
			}
		}
		g.object(FALLEN_STAR, STAR_X, STAR_Y, TYPE_SCENERY, 0);
	}

	/** Gatehold: two shacks, an open-air bank under the old gate's shadow, a smithy, a store, and the watch. */
	void gatehold() {
		int cx = GATEHOLD_X, cy = GATEHOLD_Y;
		site(cx, cy, 12);
		g.stampBuilding(3670, 2963, 3678, 2972, cx - 11, cy - 10); // the watch-house
		g.stampBuilding(3652, 2969, 3658, 2979, cx - 11, cy + 2); // Pell's store-house
		// the bank: three booths in a row, guarded, with the banker behind them
		for (int i = -1; i <= 1; i++) {
			g.object(BANK_BOOTH, cx + i, cy + 6, TYPE_SCENERY, 0);
		}
		g.object(GATEHOLD_BANNER, cx - 3, cy + 7, TYPE_SCENERY, 0);
		g.object(GATEHOLD_BANNER, cx + 3, cy + 7, TYPE_SCENERY, 0);
		person(BANKER, cx, cy + 7, 3);
		// the smithy, open to the air
		g.object(FURNACE, cx + 5, cy - 7, TYPE_SCENERY, 0);
		g.object(ANVIL, cx + 3, cy - 5, TYPE_SCENERY, 1);
		g.object(BARREL, cx + 7, cy - 5, TYPE_SCENERY, 0);
		g.object(CRATES, cx + 7, cy - 8, TYPE_SCENERY, 0);
		person(DORRIN, cx + 4, cy - 4, 1);
		// a range by the watch-house, the market stall, the well
		g.object(RANGE, cx - 2, cy - 8, TYPE_SCENERY, 0);
		g.object(MARKET_STALL, cx - 2, cy + 3, TYPE_SCENERY, 1);
		person(PELL, cx - 1, cy + 2, 1);
		g.object(WELL, cx + 2, cy, TYPE_SCENERY, 0);
		g.object(CART, cx + 6, cy + 3, TYPE_SCENERY, 1);
		g.object(STACKED_BARRELS, cx - 3, cy + 9, TYPE_SCENERY, 0);
		g.object(SACKS, cx + 5, cy + 9, TYPE_SCENERY, 0);
		// lanterns along the square
		placeNear(VOID_LANTERN, cx - 6, cy + 1, 1);
		placeNear(VOID_LANTERN, cx + 5, cy + 5, 1);
		placeNear(VOID_LANTERN, cx - 1, cy - 4, 1);
		person(YSOLDE, GATE_X - 1, GATE_Y - 1, 4);
		person(GUARD, cx + 1, cy - 2, 1);
		person(GUARD, cx - 4, cy + 4, 1);
	}

	/** Archivist Maelis's camp in the middle of the standing-stone circle above the Gate. */
	void maelisCamp() {
		int cx = MAELIS_X, cy = MAELIS_Y;
		for (int x = cx - 4; x <= cx + 4; x++) {
			for (int y = cy - 4; y <= cy + 4; y++) {
				if (Math.hypot(x - cx, y - cy) < 4.5) {
					g.removeObjectsAt(x, y);
				}
			}
		}
		tent(cx - 3, cy + 1, DOOR_SOUTH);
		g.object(380, cx + 3, cy + 1, TYPE_SCENERY, 1); // his shelves, facing the fire
		g.object(MAELIS_BOOKS, cx + 2, cy + 2, TYPE_SCENERY, 3);
		campfire(cx, cy - 2, false);
		seat(cx - 2, cy - 2, ALONG_Y);
		g.object(CRATES, cx - 3, cy - 1, TYPE_SCENERY, 0);
		person(MAELIS, cx + 1, cy, 1);
		// the hatch down into the vault, half-buried in the Gate's rubble
		g.removeObjectsAt(HATCH_X, HATCH_Y);
		g.removeObjectsAt(HATCH_X, HATCH_Y - 1);
		g.object(VAULT_HATCH, HATCH_X, HATCH_Y, TYPE_SCENERY, 0);
	}

	/** The waystones: one beside each place on the network, with the tile you arrive on kept clear. */
	void waystones() {
		for (int[] spot : WAYSTONE_SPOTS) {
			int x = spot[0], y = spot[1];
			for (int[] t : new int[][] { { x, y }, { x + 1, y } }) {
				g.removeObjectsAt(t[0], t[1]);
				g.regionAt(t[0], t[1]).settings[0][t[0] & 63][t[1] & 63] = 0;
			}
			g.object(WAYSTONE, x + 1, y, TYPE_SCENERY, 0);
		}
	}

	/** Places an object on the nearest free, dry, unblocked tile to (x, y). */
	void placeNear(int id, int x, int y, int rot) {
		for (int r = 0; r < 6; r++) {
			for (int dx = -r; dx <= r; dx++) {
				for (int dy = -r; dy <= r; dy++) {
					int px = x + dx, py = y + dy;
					if ((Math.abs(dx) == r || Math.abs(dy) == r) && free(px, py, 1) && !blocked(px, py) && !water(px, py)) {
						g.object(id, px, py, TYPE_SCENERY, rot);
						return;
					}
				}
			}
		}
		System.out.println("warning: nowhere to put object " + id + " near " + x + "," + y);
	}

	/** Tess's quarry: a pit cut into the foothills, rich in iron, coal, silver, gold and mithril. */
	void quarry() {
		int cx = QUARRY_X, cy = QUARRY_Y;
		site(cx, cy, 9);
		for (int x = cx - 8; x <= cx + 8; x++) {
			for (int y = cy - 8; y <= cy + 8; y++) {
				double d = Math.hypot(x - cx, (y - cy) * 1.2);
				if (d < 7.5) {
					g.setHeight(x, y, g.heightAt(x, y) - (int) Math.round(6 * (1 - d / 7.5)));
					g.setUnderlay(x, y, fbm(x / 2.0, y / 2.0, 98) > 0 ? MOUNTAIN : SLOPE);
				}
			}
		}
		// the ore is worked out of the pit's walls, richest at the back (north); the floor is kept clear, and the way in
		// is the ramp on the south side where the cart stands
		int[] ores = { IRON, IRON_2, COAL, COAL_2, IRON, COAL, SILVER, GOLD, MITHRIL, SILVER, GOLD_2, MITHRIL_2, COAL_2 };
		int placed = 0;
		for (int i = 0; i < 26 && placed < ores.length; i++) {
			double a = Math.toRadians(-70 + i * 320.0 / 25); // round the pit, leaving the south-east ramp open
			int x = cx + (int) Math.round(Math.cos(a) * 6), y = cy + (int) Math.round(Math.sin(a) * 5);
			if (free(x, y, 0) && !blocked(x, y) && (i % 2 == 0 || placed < i / 2)) {
				g.object(ores[placed++], x, y, TYPE_SCENERY, random.nextInt(4));
			}
		}
		g.object(MINE_CART, cx + 3, cy - 4, TYPE_SCENERY, 1);
		tent(cx + 9, cy - 3, DOOR_WEST);
		campfire(cx + 8, cy + 1, false);
		g.object(CRATES, cx + 10, cy + 1, TYPE_SCENERY, 0);
		g.object(BARREL, cx + 10, cy + 3, TYPE_SCENERY, 0);
		person(TESS, cx + 7, cy, 1);
	}

	/** The Last Hearth: an inn where the north road comes down off the pass, with tables out front. */
	void inn() {
		int cx = INN_X, cy = INN_Y;
		site(cx, cy, 9);
		g.stampBuilding(3673, 2985, 3679, 2993, cx - 3, cy); // the inn, door to the south
		g.stampBuilding(3678, 2978, 3684, 2985, cx + 5, cy - 1); // the stable-house
		g.object(TABLE, cx - 1, cy - 4, TYPE_SCENERY, 0);
		g.object(BENCH, cx - 1, cy - 5, TYPE_SCENERY, 0);
		g.object(BENCH, cx - 1, cy - 3, TYPE_SCENERY, 2);
		g.object(TABLE, cx + 3, cy - 4, TYPE_SCENERY, 0);
		g.object(BENCH, cx + 3, cy - 5, TYPE_SCENERY, 0);
		g.object(BENCH, cx + 3, cy - 3, TYPE_SCENERY, 2);
		campfire(cx - 5, cy - 3, true);
		g.object(STACKED_BARRELS, cx - 5, cy + 2, TYPE_SCENERY, 0);
		g.object(CART, cx + 7, cy - 4, TYPE_SCENERY, 1);
		g.object(TROUGH, cx + 6, cy - 6, TYPE_SCENERY, 1);
		person(MORRA, cx + 1, cy - 2, 1);
		person(PILGRIM, cx - 2, cy - 6, 1);
		person(PILGRIM, cx + 4, cy - 6, 1);
	}

	/** Smugglers' Cove: a beached ship, a camp of crates and contraband, and lobster pots in the shallows. */
	void cove() {
		int cx = SMUGGLER_COVE_X, cy = SMUGGLER_COVE_Y;
		site(cx, cy, 8);
		// the smugglers' ship, run aground with her stern in the surf (the Port Sarim ship, as at Skullbrine)
		int y = cy - 2;
		while (y > cy - 30 && !water(cx + 10, y)) {
			y--;
		}
		g.stamp(12082, 22, 9, 30, 28, cx + 6, y - 16, new int[] { 2083, 2084 }, false);
		tent(cx - 5, cy + 2, DOOR_SOUTH);
		tent(cx + 1, cy + 4, DOOR_SOUTH);
		campfire(cx, cy, true);
		seat(cx - 2, cy, ALONG_Y);
		g.object(CRATES, cx + 3, cy + 1, TYPE_SCENERY, 0);
		g.object(CRATE, cx + 4, cy - 1, TYPE_SCENERY, 0);
		g.object(STACKED_BARRELS, cx - 3, cy - 2, TYPE_SCENERY, 0);
		g.object(BARREL, cx - 1, cy - 3, TYPE_SCENERY, 0);
		g.object(ROW_BOAT, cx - 6, y, TYPE_SCENERY, 1);
		person(NETTLE, cx + 1, cy - 1, 1);
		for (int[] b : new int[][] { { -3, 1 }, { 3, -3 }, { 5, 3 }, { -5, -1 }, { 0, 5 } }) {
			person(b[0] % 2 == 0 ? PIRATE : BANDIT, cx + b[0], cy + b[1], 1);
		}
		// fishing in the cove: lobster and swordfish (cage/harpoon), sharks (net/harpoon)
		int fished = 0;
		for (int dx = -14; dx <= 14 && fished < 5; dx += 3) {
			for (int fy = cy - 2; fy > cy - 20; fy--) {
				if (shoreWater(cx + dx, fy)) {
					person(fished < 3 ? 312 : 313, cx + dx, fy, 0);
					fished++;
					break;
				}
			}
		}
	}

	/** A plank bridge over the river west of Hob's camp, and Silkwood beyond it. */
	void bridgeAndSilkwood() {
		// find the narrowest crossing between Hob's camp and Silkwood
		int bestY = -1, bestFrom = 0, bestTo = 0;
		for (int y = WOOD_CAMP_Y; y <= SILKWOOD_Y + 6; y++) {
			int from = -1, to = -1;
			for (int x = WOOD_CAMP_X - 6; x > SILKWOOD_X + 6; x--) {
				if (water(x, y)) {
					if (from < 0) {
						from = x;
					}
					to = x;
				} else if (from >= 0) {
					break;
				}
			}
			if (from >= 0 && !water(to - 1, y) && (bestY < 0 || from - to < bestFrom - bestTo)) {
				bestY = y;
				bestFrom = from;
				bestTo = to;
			}
		}
		if (bestY >= 0) {
			// a flat deck at the height of the banks (tile heights are corner heights, so level a ring around it too)
			int deck = (g.heightAt(bestFrom + 2, bestY) + g.heightAt(bestTo - 2, bestY)) / 2;
			for (int x = bestFrom + 2; x >= bestTo - 2; x--) {
				for (int y = bestY - 1; y <= bestY + 2; y++) {
					g.setHeight(x, y, deck);
				}
			}
			for (int x = bestFrom + 1; x >= bestTo - 1; x--) {
				for (int y = bestY; y <= bestY + 1; y++) {
					MapRegion r = g.regionAt(x, y);
					r.overlay[0][x & 63][y & 63] = WOOD_FLOOR;
					r.overlayShape[0][x & 63][y & 63] = 0;
					r.settings[0][x & 63][y & 63] = 0;
					g.removeObjectsAt(x, y);
				}
			}
			System.out.println("Bridge over the river at y " + bestY + ", x " + bestTo + ".." + bestFrom);
		}
		// Silkwood: magic and yew trees in a hushed clearing, bones of those who came for them
		int cx = SILKWOOD_X, cy = SILKWOOD_Y;
		site(cx, cy, 9);
		int[][] trees = { { -5, 3, MAGIC_TREE }, { 4, 5, MAGIC_TREE }, { 6, -3, MAGIC_TREE }, { -3, -6, MAGIC_TREE },
				{ 0, 7, YEW }, { -7, -2, YEW } };
		for (int[] t : trees) {
			g.object(t[2], cx + t[0], cy + t[1], TYPE_SCENERY, random.nextInt(4));
		}
		g.object(SKELETON_DECOR, cx + 1, cy - 1, TYPE_GROUND_DECOR, 1);
		g.object(SKELETON_DECOR, cx - 2, cy + 2, TYPE_GROUND_DECOR, 3);
		g.object(FALLEN_TREE, cx + 3, cy + 1, TYPE_SCENERY, 1);
		g.object(SILK_COCOON, cx - 1, cy + 4, TYPE_SCENERY, 0); // a page of the Royal Chronicle, wrapped up tight
		// webs hung across the gaps between the trees, thickest round the cocoon
		int[][] webs = { { -4, 4, 1 }, { 2, 6, 0 }, { 5, 1, 1 }, { -6, -4, 0 }, { 1, -5, 1 }, { -2, 5, 0 } };
		for (int[] w : webs) {
			g.removeObjectsAt(cx + w[0], cy + w[1]);
			g.object(WEB, cx + w[0], cy + w[1], TYPE_SCENERY, w[2]);
		}
		for (int i = 0; i < 6; i++) {
			person(SPIDER, cx - 4 + random.nextInt(9), cy - 4 + random.nextInt(9), 1);
		}
	}

	/** The Sunken Barrow: a grassy mound with a stone door, old graves around it. */
	void barrowMound() {
		int dx = BARROW_DOOR_X, dy = BARROW_DOOR_Y, mx = dx, my = dy + 5;
		site(mx, my, 8);
		for (int x = mx - 7; x <= mx + 7; x++) {
			for (int y = my - 7; y <= my + 7; y++) {
				double d = Math.hypot(x - mx, (y - my) * 1.1);
				if (d < 5.5) {
					g.setHeight(x, y, g.heightAt(x, y) + (int) Math.round(22 * Math.cos(d / 5.5 * Math.PI / 2)));
					g.block(x, y);
					g.setUnderlay(x, y, DEAD_GRASS);
				}
			}
		}
		System.out.println("Barrow mound: ground " + g.heightAt(dx, dy - 2) + ", crown " + g.heightAt(mx, my));
		g.object(BARROW_DOOR, dx, dy, TYPE_SCENERY, 0);
		// the Thane's household, buried in two rows either side of the way up to his door, all facing it
		keepClear.add(new int[] { dx - 6, dy - 7, dx + 6, dy - 1 });
		int[][] graves = { { -3, -3 }, { -5, -3 }, { 3, -3 }, { 5, -3 }, { -3, -6 }, { -5, -6 }, { 3, -6 }, { 5, -6 } };
		for (int i = 0; i < graves.length; i++) {
			g.removeObjectsAt(dx + graves[i][0], dy + graves[i][1]);
			g.object(i < 4 ? BIG_GRAVE : GRAVE, dx + graves[i][0], dy + graves[i][1], TYPE_SCENERY, 0);
		}
		// a stone either side of the door (it's three tiles wide), clear of it
		g.object(STANDING_STONE, dx - 2, dy - 1, TYPE_SCENERY, 0);
		g.object(STANDING_STONE, dx + 4, dy - 1, TYPE_SCENERY, 0);
	}

	/** Hollowdeep's mouth: a fissure in the Scar's black heart, ringed with crystals. */
	void fissure() {
		int cx = FISSURE_X, cy = FISSURE_Y;
		for (int x = cx - 3; x <= cx + 3; x++) {
			for (int y = cy - 3; y <= cy + 3; y++) {
				g.removeObjectsAt(x, y);
				double d = Math.hypot(x - cx, y - cy);
				if (d < 3.2) {
					g.setOverlay(x, y, d < 1.6 ? VOID_POOL : d < 2.4 ? VOID_GLASS : VOID_ASH);
				}
			}
		}
		g.object(HOLLOW_FISSURE, cx, cy, TYPE_SCENERY, 0);
		g.object(VOID_CRYSTAL, cx - 3, cy + 2, TYPE_SCENERY, 0);
		g.object(VOID_CRYSTAL_DARK, cx + 3, cy + 3, TYPE_SCENERY, 1);
		g.object(VOID_CRYSTAL, cx + 3, cy - 3, TYPE_SCENERY, 2);
	}

	/** Frostfang's mouth: a ledge cut from the pass road into the mountain's flank. */
	void frostCaveMouth() {
		int cx = FROST_CAVE_X, cy = FROST_CAVE_Y;
		int level = g.heightAt(cx - 7, cy);
		for (int x = cx - 7; x <= cx + 1; x++) {
			for (int y = cy - 1; y <= cy + 1; y++) {
				MapRegion r = g.regionAt(x, y);
				r.settings[0][x & 63][y & 63] = 0;
				g.removeObjectsAt(x, y);
				g.setHeight(x, y, level);
				g.setUnderlay(x, y, SLOPE);
			}
		}
		g.object(FROST_CAVE, cx, cy, TYPE_SCENERY, 3);
		person(96, cx - 5, cy + 2, 1); // white wolves prowl the ledge
	}

	/* ====================================================================== */
	/*          DUNGEONS - the Sunken Barrow, Hollowdeep, Frostfang           */
	/* ====================================================================== */

	static final int DOOR_EXIT = com.rs2.game.content.skullbrine.SkullbrineConstants.CAVE_EXIT;
	static final int CAVE_TORCH_ID = 724, ICE_FLOOR = 58, BLUE_ICE = 59;

	interface Floor {
		void paint(MapRegion r, int x, int y);
	}

	/** Carves an open/closed grid into a region: floors from {@code floor}, rock walls rising off them. */
	MapRegion carve(int regionX, boolean[][] open, Floor floor, int wall) {
		MapRegion r = new MapRegion(regionX, NY_DUNGEON_REGION_Y);
		g.regions.put(r.id(), r);
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				// gentle undulation underfoot; the rock rises in a rounded slope over three tiles, not a one-tile spike
				if (open[x][y]) {
					r.height[0][x][y] = 3 + (int) Math.round(fbm(x / 7.0, y / 7.0, regionX * 13 + 1) * 2);
					floor.paint(r, x, y);
				} else {
					r.settings[0][x][y] = MapRegion.BLOCKED;
					r.underlay[0][x][y] = wall;
					double lump = fbm(x / 4.0, y / 4.0, regionX * 13 + 2);
					if (bordersOpen(open, x, y, 1)) {
						r.height[0][x][y] = 20 + (int) Math.round(lump * 7);
					} else if (bordersOpen(open, x, y, 2)) {
						r.height[0][x][y] = 38 + (int) Math.round(lump * 6);
					} else {
						r.overlay[0][x][y] = INVISIBLE;
						r.height[0][x][y] = 52;
					}
				}
			}
		}
		smoothDungeon(r);
		// the way back out
		r.objects.add(new MapRegion.MapObject(DOOR_EXIT, 31, 2, 0, TYPE_SCENERY, 2));
		return r;
	}

	void put(MapRegion r, boolean[][] open, int id, int x, int y, int type, int rot) {
		if (!open[x][y]) {
			System.out.println("warning: dungeon object " + id + " at local " + x + "," + y + " is inside rock");
			return;
		}
		r.objects.removeIf(o -> o.x == x && o.y == y);
		r.objects.add(new MapRegion.MapObject(id, x, y, 0, type, rot));
	}

	/** Scatters {@code count} of an object against the walls of a chamber. */
	void hug(MapRegion r, boolean[][] open, int[] ids, int count, int cx, int cy, int radius) {
		int placed = 0;
		for (int attempt = 0; attempt < 600 && placed < count; attempt++) {
			int x = cx - radius + random.nextInt(radius * 2 + 1), y = cy - radius + random.nextInt(radius * 2 + 1);
			if (x < 2 || y < 2 || x > 61 || y > 61 || !open[x][y] || !bordersClosed(open, x, y) || openAround(open, x, y, 1) < 5) {
				continue;
			}
			final int fx = x, fy = y;
			if (r.objects.stream().anyMatch(o -> Math.abs(o.x - fx) + Math.abs(o.y - fy) < 3)) {
				continue;
			}
			r.objects.add(new MapRegion.MapObject(ids[placed % ids.length], x, y, 0, TYPE_SCENERY, random.nextInt(4)));
			placed++;
		}
	}

	/** Places monsters on open floor in a chamber. */
	void lair(int regionX, MapRegion r, boolean[][] open, int id, int count, int cx, int cy, int radius, int walk) {
		int placed = 0;
		for (int attempt = 0; attempt < 600 && placed < count; attempt++) {
			int x = cx - radius + random.nextInt(radius * 2 + 1), y = cy - radius + random.nextInt(radius * 2 + 1);
			if (x < 2 || y < 2 || x > 61 || y > 61 || !open[x][y] || openAround(open, x, y, 1) < 9) {
				continue;
			}
			final int fx = x, fy = y;
			if (r.objects.stream().anyMatch(o -> o.x == fx && o.y == fy)) {
				continue;
			}
			spawns.add(new int[] { id, regionX * 64 + x, DUNGEON_BASE_Y + y, walk });
			placed++;
		}
	}

	void dungeons() {
		sunkenBarrow();
		hollowdeep();
		frostfang();
		vault();
	}

	/**
	 * The Vault beneath the Gate (Ashes of the Crown): a stair down into an antechamber, the hall of the three wards,
	 * a single passage sealed with void glass, and the sanctum where the Ember waits.
	 */
	void vault() {
		boolean[][] o = new boolean[64][64];
		CursedIslandGenerator.blob(o, 32, 7, 5, 4); // the foot of the stair
		CursedIslandGenerator.tunnel(o, 32, 10, 32, 19, 2);
		CursedIslandGenerator.blob(o, 32, 25, 9, 6); // the hall of wards
		CursedIslandGenerator.tunnel(o, 32, 31, 32, 44, 1); // one tile wide: the seal closes it completely
		CursedIslandGenerator.blob(o, 32, 50, 9, 6); // the sanctum
		MapRegion r = carve(VAULT_REGION_X, o, (reg, x, y) -> {
			double f = fbm(x / 5.0, y / 5.0, 74);
			reg.underlay[0][x][y] = f > 0.25 ? DARK_PATH : f < -0.3 ? MUD : GREY_PATH; // worn stone; the client blends it
			double d = Math.hypot(x - 32, y - 50);
			if (d < 3.5) {
				reg.overlay[0][x][y] = EMBER_SCORCH; // where the Ember's heat has scorched the stone
			} else if (d < 5) {
				reg.overlay[0][x][y] = ASH;
			}
		}, DARK_ROCK);
		int[] wardIds = { SUN_WARD, STAR_WARD, MOON_WARD };
		for (int i = 0; i < 3; i++) {
			put(r, o, wardIds[i], WARD_SPOTS[i][0], WARD_SPOTS[i][1], TYPE_SCENERY, 0);
		}
		put(r, o, VAULT_SEAL, SEAL_LX, SEAL_LY, TYPE_SCENERY, 0);
		put(r, o, EMBER_PEDESTAL, EMBER_LX, EMBER_LY, TYPE_SCENERY, 0);
		for (int y = 12; y <= 18; y += 6) {
			put(r, o, CAVE_TORCH_ID, 31, y, TYPE_SCENERY, 1);
			put(r, o, CAVE_TORCH_ID, 33, y, TYPE_SCENERY, 3);
		}
		hug(r, o, new int[] { PILLAR, CAVE_TORCH_ID, PILLAR_2, SKULL_PILE }, 8, 32, 25, 10);
		hug(r, o, new int[] { VOID_CRYSTAL, PILLAR, VOID_CRYSTAL_DARK, CAVE_TORCH_ID }, 8, 32, 50, 10);
		lair(VAULT_REGION_X, r, o, HOLLOW_SHADE, 3, 32, 22, 6, 1); // shades drift through the hall
	}

	/** The Sunken Barrow: a straight processional hall, side crypts, an ossuary, and the Thane's tomb. */
	void sunkenBarrow() {
		boolean[][] o = new boolean[64][64];
		CursedIslandGenerator.blob(o, 32, 7, 5, 4); // antechamber
		CursedIslandGenerator.tunnel(o, 32, 10, 32, 40, 3); // the processional hall
		CursedIslandGenerator.tunnel(o, 32, 20, 20, 22, 2);
		CursedIslandGenerator.blob(o, 17, 23, 5, 4); // west crypt
		CursedIslandGenerator.tunnel(o, 32, 26, 44, 26, 2);
		CursedIslandGenerator.blob(o, 47, 27, 5, 5); // east crypt
		CursedIslandGenerator.tunnel(o, 31, 36, 18, 43, 2);
		CursedIslandGenerator.blob(o, 14, 45, 5, 5); // the ossuary
		CursedIslandGenerator.blob(o, 32, 49, 8, 7); // the Thane's tomb
		MapRegion r = carve(BARROW_REGION_X, o, (reg, x, y) -> {
			double f = fbm(x / 5.0, y / 5.0, 71);
			reg.underlay[0][x][y] = f > 0.25 ? DARK_PATH : f < -0.35 ? MUD : GREY_PATH; // worn flagstones, blended
		}, DARK_ROCK);
		for (int y = 12; y <= 38; y += 6) { // torches down both walls of the hall
			int west = 32, east = 32;
			while (o[west - 1][y]) {
				west--;
			}
			while (o[east + 1][y]) {
				east++;
			}
			if (east - west >= 2) {
				put(r, o, CAVE_TORCH_ID, west, y, TYPE_SCENERY, 1);
				put(r, o, CAVE_TORCH_ID, east, y, TYPE_SCENERY, 3);
			}
		}
		hug(r, o, new int[] { GRAVE, BIG_GRAVE, GRAVE }, 5, 17, 23, 6);
		hug(r, o, new int[] { GRAVE, BIG_GRAVE }, 5, 47, 27, 6);
		hug(r, o, new int[] { SKULL_PILE, DUNGEON_SKULLS, SKULL_PILE }, 6, 14, 45, 6);
		hug(r, o, new int[] { BIG_GRAVE, CAVE_TORCH_ID, DUNGEON_SKULLS }, 8, 32, 49, 9);
		put(r, o, BIG_GRAVE, 32, 53, TYPE_SCENERY, 0); // the Thane's own grave, empty
		put(r, o, LONG_SKELETON, 29, 46, TYPE_GROUND_DECOR, 1);
		put(r, o, THANE_COFFER, 35, 53, TYPE_SCENERY, 0); // where a chronicle page lies (Ashes of the Crown)
		int bx = BARROW_REGION_X;
		lair(bx, r, o, 90, 3, 32, 22, 4, 1);
		lair(bx, r, o, 91, 3, 17, 23, 4, 1);
		lair(bx, r, o, 92, 3, 47, 27, 4, 1);
		lair(bx, r, o, 103, 4, 14, 45, 4, 1);
		lair(bx, r, o, 93, 2, 32, 34, 3, 1);
		lair(bx, r, o, BARROW_THANE, 1, 32, 50, 2, 1);
	}

	/** Hollowdeep: caves the Scar has eaten through, black glass and crystal, the Matriarch's den at the bottom. */
	void hollowdeep() {
		boolean[][] o = new boolean[64][64];
		CursedIslandGenerator.blob(o, 32, 7, 5, 4);
		CursedIslandGenerator.tunnel(o, 32, 10, 26, 16, 2);
		CursedIslandGenerator.tunnel(o, 26, 16, 22, 22, 2);
		CursedIslandGenerator.blob(o, 19, 25, 7, 6); // the husk pit
		CursedIslandGenerator.tunnel(o, 24, 28, 38, 26, 2);
		CursedIslandGenerator.tunnel(o, 38, 26, 44, 30, 2);
		CursedIslandGenerator.blob(o, 46, 32, 7, 6); // the kennel
		CursedIslandGenerator.tunnel(o, 44, 37, 38, 42, 2);
		CursedIslandGenerator.blob(o, 31, 49, 9, 7); // the Matriarch's den
		CursedIslandGenerator.tunnel(o, 16, 30, 14, 42, 2);
		CursedIslandGenerator.blob(o, 13, 45, 4, 4); // a crystal grotto
		MapRegion r = carve(HOLLOWDEEP_REGION_X, o, (reg, x, y) -> {
			double n = fbm(x / 6.0, y / 6.0, 72);
			reg.underlay[0][x][y] = n > -0.1 ? VOIDSTONE : VOIDSTONE_DARK; // the same void-eaten stone as the platform
			if (n > 0.45) {
				reg.overlay[0][x][y] = n > 0.6 ? VOID_POOL : VOID_ASH; // pools of the Scar's black glass, ringed with ash
			} else if (n < -0.5) {
				reg.overlay[0][x][y] = VOID_GLASS;
			}
		}, SHADOW_ROCK);
		hug(r, o, new int[] { VOID_CRYSTAL, VOID_CRYSTAL_DARK }, 5, 19, 25, 8);
		hug(r, o, new int[] { VOID_CRYSTAL_DARK, ROOTS, VOID_CRYSTAL }, 5, 46, 32, 8);
		hug(r, o, new int[] { VOID_CRYSTAL, VOID_CRYSTAL_DARK, SKULL_PILE }, 8, 31, 49, 10);
		hug(r, o, new int[] { VOID_CRYSTAL, VOID_CRYSTAL }, 4, 13, 45, 5);
		int hx = HOLLOWDEEP_REGION_X;
		lair(hx, r, o, HOLLOW_HUSK, 5, 19, 25, 5, 1);
		lair(hx, r, o, HOLLOWED_WOLF, 5, 46, 32, 5, 1);
		lair(hx, r, o, VOIDLING, 4, 13, 45, 3, 1);
		lair(hx, r, o, HOLLOWED_WOLF, 2, 31, 47, 5, 1);
		lair(hx, r, o, HOLLOW_MATRIARCH, 1, 31, 51, 2, 1);
	}

	/** Frostfang Caves: ice halls, a rich ore cavern, and Rimeclaw's lair. */
	void frostfang() {
		boolean[][] o = new boolean[64][64];
		CursedIslandGenerator.blob(o, 32, 7, 5, 4);
		CursedIslandGenerator.tunnel(o, 32, 10, 32, 18, 2);
		CursedIslandGenerator.blob(o, 32, 22, 6, 5); // the frozen hall
		CursedIslandGenerator.tunnel(o, 27, 23, 18, 28, 2);
		CursedIslandGenerator.blob(o, 14, 31, 7, 6); // the ore cavern
		CursedIslandGenerator.tunnel(o, 37, 24, 46, 32, 2);
		CursedIslandGenerator.blob(o, 48, 35, 6, 6); // the warriors' hall
		CursedIslandGenerator.tunnel(o, 46, 41, 38, 45, 2);
		CursedIslandGenerator.blob(o, 31, 50, 9, 7); // Rimeclaw's lair
		CursedIslandGenerator.tunnel(o, 27, 19, 21, 14, 2); // the rime shrine (Heartforged), off the frozen hall
		CursedIslandGenerator.blob(o, 19, 12, 4, 3);
		MapRegion r = carve(FROSTFANG_REGION_X, o, (reg, x, y) -> {
			reg.underlay[0][x][y] = ICE_FLOOR;
			if (fbm(x / 5.0, y / 5.0, 73) > 0.3) {
				reg.overlay[0][x][y] = BLUE_ICE;
			}
		}, DARK_ROCK);
		hug(r, o, new int[] { STALAGMITE, STALAGMITE_2 }, 5, 32, 22, 7);
		// the reward for braving it: a cavern rich in high ore, with a little runite at the back
		int[] ores = { MITHRIL, MITHRIL_2, ADAMANT, ADAMANT_2, ADAMANT, MITHRIL, COAL, COAL_2, RUNITE };
		hug(r, o, ores, ores.length, 14, 31, 8);
		hug(r, o, new int[] { STALAGMITE, STALAGMITE_2 }, 4, 48, 35, 7);
		hug(r, o, new int[] { STALAGMITE_2, STALAGMITE, RUNITE }, 7, 31, 50, 10);
		int fx = FROSTFANG_REGION_X;
		lair(fx, r, o, 96, 3, 32, 22, 4, 1); // white wolves
		lair(fx, r, o, 125, 3, 14, 31, 5, 1); // ice warriors among the ore
		lair(fx, r, o, 125, 2, 48, 35, 4, 1);
		lair(fx, r, o, 111, 3, 48, 35, 4, 1); // ice giants
		lair(fx, r, o, RIMECLAW, 1, 31, 52, 2, 1);
		put(r, o, RIME_ALTAR, ALTAR_LX, ALTAR_LY, TYPE_SCENERY, 0);
		put(r, o, STALAGMITE_2, ALTAR_LX + 3, ALTAR_LY + 1, TYPE_SCENERY, 2);
	}

	/* ====================================================================== */
	/*                    WILDLIFE - spawns for spawns.json                   */
	/* ====================================================================== */

	/** Spawns chosen by {@link #wildlife()}: {npc id, x, y, walk}. Written out with -Dnythera.spawns=path. */
	final List<int[]> spawns = new ArrayList<>();

	boolean water(int x, int y) {
		return g.overlayAt(x, y) == WATER;
	}

	boolean open(int x, int y) {
		return !water(x, y) && !blocked(x, y) && free(x, y, 0) && !nearCamp(x, y) && crater(x, y) <= 0;
	}

	/** Water with dry, walkable land beside it (where a fishing spot or a duck makes sense). */
	boolean shoreWater(int x, int y) {
		if (!water(x, y)) {
			return false;
		}
		for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
			if (!water(x + d[0], y + d[1]) && !blocked(x + d[0], y + d[1])) {
				return true;
			}
		}
		return false;
	}

	interface Where {
		boolean ok(int x, int y);
	}

	/** Scatters {@code count} spawns of the given ids over tiles that pass {@code where}, at least {@code gap} apart. */
	void scatter(int[] ids, int count, int walk, int gap, Where where) {
		int placed = 0;
		for (int attempt = 0; attempt < 20000 && placed < count; attempt++) {
			int x = NX0 + 4 + random.nextInt(NSIZE - 8), y = NY0 + 4 + random.nextInt(NSIZE - 8);
			if (!where.ok(x, y)) {
				continue;
			}
			boolean crowded = false;
			for (int[] s : spawns) {
				if (Math.abs(s[1] - x) < gap && Math.abs(s[2] - y) < gap) {
					crowded = true;
					break;
				}
			}
			if (!crowded) {
				spawns.add(new int[] { ids[placed % ids.length], x, y, walk });
				placed++;
			}
		}
	}

	/** Scatters spawns in a disc around a point. */
	void around(int id, int count, int cx, int cy, int r, int walk) {
		int placed = 0;
		for (int attempt = 0; attempt < 2000 && placed < count; attempt++) {
			int x = cx + random.nextInt(2 * r + 1) - r, y = cy + random.nextInt(2 * r + 1) - r;
			if (Math.hypot(x - cx, y - cy) <= r && !water(x, y) && !blocked(x, y) && free(x, y, 0)) {
				spawns.add(new int[] { id, x, y, walk });
				placed++;
			}
		}
	}

	void wildlife() {
		Where meadow = (x, y) -> open(x, y) && forest(x, y) < 0.4 && mountain(x, y) < 0.25 && scar(x, y) < 0.05
				&& coast(x, y) > 6;
		Where woods = (x, y) -> open(x, y) && forest(x, y) > 0.6 && scar(x, y) < 0.05;
		Where foothills = (x, y) -> open(x, y) && mountain(x, y) > 0.2 && mountain(x, y) < 0.5;
		Where heights = (x, y) -> open(x, y) && mountain(x, y) > 0.5;
		Where beach = (x, y) -> open(x, y) && coast(x, y) < 3.5 && coast(x, y) > 0.5;
		Where scarEdge = (x, y) -> open(x, y) && scar(x, y) > 0.1 && scar(x, y) < 0.35;
		Where scarDeep = (x, y) -> open(x, y) && scar(x, y) > 0.4;
		Where lakeShore = (x, y) -> shoreWater(x, y) && lake(x, y) > 0;
		Where riverBank = (x, y) -> shoreWater(x, y) && lake(x, y) <= 0 && coast(x, y) > 8;

		scatter(new int[] { 1192, 1193, 1194 }, 16, 1, 6, meadow); // rabbits
		scatter(new int[] { 153, 154, 155, 156 }, 10, 1, 8, meadow); // butterflies
		scatter(new int[] { 3283, 3284, 3285 }, 8, 1, 6, woods); // squirrels
		scatter(new int[] { 89 }, 3, 1, 20, woods); // unicorns in the deep forest
		scatter(new int[] { 105 }, 4, 1, 14, foothills); // grizzly bears
		scatter(new int[] { 96 }, 5, 1, 8, heights); // white wolves on the ridge
		scatter(new int[] { 2693 }, 8, 1, 10, beach); // seagulls
		scatter(new int[] { 44, 45, 3296 }, 7, 1, 4, lakeShore); // ducks and swans
		scatter(new int[] { 1751, 1752, 1753 }, 6, 1, 6, scarEdge); // crows on the dying land's edge
		scatter(new int[] { HOLLOWED_WOLF }, 7, 1, 5, scarEdge);
		scatter(new int[] { HOLLOW_HUSK }, 8, 1, 5, scarDeep);
		// the croft's animals
		around(43, 5, CROFT_X - 5, CROFT_Y + 12, 2, 1); // sheep, in their pen
		around(81, 3, CROFT_X + 7, CROFT_Y + 14, 3, 1); // cows, in the pasture
		around(41, 4, CROFT_X - 3, CROFT_Y + 4, 3, 1); // chickens, loose in the yard
		// fishing: net/bait in the lake (one at the end of Bram's jetty), lure/bait in the river
		scatter(new int[] { 316 }, 4, 0, 10, lakeShore);
		scatter(new int[] { 309 }, 5, 0, 12, riverBank);
		System.out.println("Nythera wildlife: " + spawns.size() + " spawns");
	}

	void writeSpawns() {
		String out = System.getProperty("nythera.spawns");
		if (out != null) {
			try (java.io.PrintWriter pw = new java.io.PrintWriter(out)) {
				for (int[] s : spawns) {
					pw.println(s[0] + " " + s[1] + " " + s[2] + " " + s[3]);
				}
			} catch (java.io.IOException e) {
				throw new IllegalStateException(e);
			}
		}
		System.out.println("Nythera spawns in all: " + spawns.size());
	}

	boolean blocked(int x, int y) {
		return (g.regionAt(x, y).settings[0][x & 63][y & 63] & MapRegion.BLOCKED) != 0;
	}

	/** Nothing placed within r tiles, no overlay (road/water), and inside Nythera's regions. */
	boolean free(int x, int y, int r) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				int ax = x + dx, ay = y + dy;
				if (ax < NX0 || ay < NY0 || ax >= NX0 + NSIZE || ay >= NY0 + NSIZE) {
					return false;
				}
				MapRegion reg = g.regionAt(ax, ay);
				int lx = ax & 63, ly = ay & 63;
				if (reg.overlay[0][lx][ly] != 0) {
					return false;
				}
			}
		}
		return g.footprintFree(x, y, r);
	}

	/** Softens the edges of water, roads and patches (see CursedIslandGenerator#smoothGrid). */
	void smoothOverlays() {
		List<int[]> halves = CursedIslandGenerator.smoothGrid(g.worldGrid(), NX0 + 1, NY0 + 1, NX0 + NSIZE - 2, NY0 + NSIZE - 2,
				CursedIslandGenerator::overlayGroup, ov -> ov == WOOD, ov -> CursedIslandGenerator.overlayGroup(ov) != WATER);
		for (int[] c : halves) {
			MapRegion r = g.regionAt(c[0], c[1]);
			// the cut-away half shows the tile's underlay: sea tiles have none (it renders black), so borrow the shore's
			if (r.underlay[0][c[0] & 63][c[1] & 63] == 0) {
				for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
					int u = g.underlayAt(c[0] + d[0], c[1] + d[1]);
					if (u != 0 && g.overlayAt(c[0] + d[0], c[1] + d[1]) == 0) {
						r.underlay[0][c[0] & 63][c[1] & 63] = u;
						break;
					}
				}
			}
		}
	}

	/** Smooths a dungeon region's floor patches, in local coordinates; the invisible rock is left as it is. */
	/** Floor colours for the floating platforms (flo 87 grey_blue, flo 57 verydarkrock): plain colours that blend. */

	private static boolean isVoid(MapRegion r, int x, int y) {
		return x < 0 || y < 0 || x > 63 || y > 63 || r.overlay[0][x][y] == INVISIBLE && r.underlay[0][x][y] == 0;
	}

	/**
	 * Rounds the edge of a platform floating over nothing. Overlay smoothing can't do it (the floor there is bare
	 * underlay), so: a floor tile with the void on two adjacent sides gets an invisible diagonal half over its outer
	 * corner, and a void tile cupped by floor on two sides and the diagonal gets the floor back in its inner corner
	 * (an invisible all-but-corner overlay on the neighbour's underlay).
	 */
	static void roundRim(MapRegion r) {
		java.util.List<int[]> outer = new java.util.ArrayList<>(), inner = new java.util.ArrayList<>();
		for (int x = 1; x < 63; x++) {
			for (int y = 1; y < 63; y++) {
				boolean n = isVoid(r, x, y + 1), e = isVoid(r, x + 1, y), so = isVoid(r, x, y - 1), w = isVoid(r, x - 1, y);
				if (!isVoid(r, x, y)) {
					if (r.overlayShape[0][x][y] != 0 || (n ? 1 : 0) + (e ? 1 : 0) + (so ? 1 : 0) + (w ? 1 : 0) != 2 || n && so || e && w) {
						continue;
					}
					outer.add(new int[] { x, y, so && w ? 0 : w && n ? 1 : n && e ? 2 : 3 });
				} else {
					int[][] dirs = { { -1, 0, 0, -1 }, { -1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 1, 0, 0, -1 } }; // SW, NW, NE, SE
					for (int rot = 0; rot < 4; rot++) {
						int[] d = dirs[rot];
						if (!isVoid(r, x + d[0], y + d[1]) && !isVoid(r, x + d[2], y + d[3]) && !isVoid(r, x + d[0] + d[2], y + d[1] + d[3])
								&& isVoid(r, x - d[0], y - d[1]) && isVoid(r, x - d[2], y - d[3])) {
							inner.add(new int[] { x, y, rot, r.underlay[0][x + d[0] + d[2]][y + d[1] + d[3]] });
							break;
						}
					}
				}
			}
		}
		for (int[] c : outer) {
			r.overlay[0][c[0]][c[1]] = INVISIBLE;
			r.overlayShape[0][c[0]][c[1]] = CursedIslandGenerator.SHAPE_HALF;
			r.overlayRotation[0][c[0]][c[1]] = c[2];
		}
		for (int[] c : inner) {
			r.underlay[0][c[0]][c[1]] = c[3];
			r.overlay[0][c[0]][c[1]] = INVISIBLE;
			r.overlayShape[0][c[0]][c[1]] = 8; // all but one corner: shape 8 names the corner it covers opposite to shape 7
			r.overlayRotation[0][c[0]][c[1]] = (c[2] + 2) & 3;
		}
	}

	static void smoothDungeon(MapRegion r) {
		CursedIslandGenerator.smoothGrid(new CursedIslandGenerator.OverlayGrid() {
			public int overlay(int x, int y) {
				return x < 0 || y < 0 || x > 63 || y > 63 ? 0 : r.overlay[0][x][y];
			}

			public int shape(int x, int y) {
				return x < 0 || y < 0 || x > 63 || y > 63 ? 0 : r.overlayShape[0][x][y];
			}

			public void set(int x, int y, int overlay, int shape, int rotation) {
				r.overlay[0][x][y] = overlay;
				r.overlayShape[0][x][y] = shape;
				r.overlayRotation[0][x][y] = rotation;
			}

			public boolean frozen(int x, int y) {
				return (r.settings[0][x][y] & MapRegion.BLOCKED) != 0; // only the floor is softened
			}
		}, 1, 1, 62, 62, ov -> ov, ov -> ov == INVISIBLE, ov -> true);
	}
}
