package com.rs2.tools.mapgen;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rs2.game.content.skullbrine.SkullbrineConstants;

/**
 * Every area the generators build, declared once: where it is, where the player arrives, what it is for, and the places
 * in it a player must be able to walk to. The world gate lints each of them before the cache is written, and anything
 * the generators place outside all of them fails the gate - so a new area can't be built without first being declared
 * here, with a brief that says what it is meant to be.
 *
 * The brief is not decoration. Write it before the code: what the place is for, how the player arrives (and so what
 * they see first), and the one landmark that makes it recognisable from a distance. If those three can't be written
 * down, the area isn't designed yet, and no amount of scattered props will make it read as a place.
 */
final class WorldAreas {

	/** A named spot the player has to be able to walk up to: an npc's post, a door, a cave mouth, a camp. */
	static final class Spot {
		final String name;
		final int x, y;

		Spot(String name, int x, int y) {
			this.name = name;
			this.x = x;
			this.y = y;
		}
	}

	/** What an area is meant to be. Every part must be written out, in a sentence or more. */
	static final class Brief {
		final String purpose, arrival, landmark;

		Brief(String purpose, String arrival, String landmark) {
			this.purpose = need("purpose", purpose);
			this.arrival = need("arrival", arrival);
			this.landmark = need("landmark", landmark);
		}

		private static String need(String what, String text) {
			if (text == null || text.trim().split("\\s+").length < 6) {
				throw new IllegalArgumentException("an area's brief needs its " + what
						+ " written out (a sentence, not a word or two): design the place before building it");
			}
			return text.trim();
		}
	}

	static final class Area {
		final String name;
		final int x0, y0, x1, y1, fromX, fromY;
		final Brief brief;
		final List<Spot> mustReach;
		/** Where else the game puts the player down (a quest moving them past a seal, a ladder from another area). */
		final List<int[]> alsoArrives = new ArrayList<>();

		Area(String name, int x0, int y0, int x1, int y1, int fromX, int fromY, Brief brief, Spot... mustReach) {
			this.name = name;
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
			this.fromX = fromX;
			this.fromY = fromY;
			this.brief = brief;
			this.mustReach = Arrays.asList(mustReach);
			if (fromX < x0 || fromX > x1 || fromY < y0 || fromY > y1) {
				throw new IllegalArgumentException(name + ": the arrival tile " + fromX + "," + fromY + " is outside the area");
			}
			for (Spot s : mustReach) {
				if (s.x < x0 || s.x > x1 || s.y < y0 || s.y > y1) {
					throw new IllegalArgumentException(name + ": " + s.name + " (" + s.x + "," + s.y + ") is outside the area");
				}
			}
		}

		/** The game also puts the player down here (say why): reach is flooded from every arrival. */
		Area alsoArrives(int x, int y, String why) {
			if (!contains(x, y) || why == null || why.trim().isEmpty()) {
				throw new IllegalArgumentException(name + ": an extra arrival needs to be inside the area and say why it exists");
			}
			alsoArrives.add(new int[] { x, y });
			return this;
		}

		int[][] arrivals() {
			List<int[]> all = new ArrayList<>();
			all.add(new int[] { fromX, fromY });
			all.addAll(alsoArrives);
			return all.toArray(new int[0][]);
		}

		boolean contains(int x, int y) {
			return x >= x0 && x <= x1 && y >= y0 && y <= y1;
		}

		String slug() {
			return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
		}
	}

	static Spot spot(String name, int x, int y) {
		return new Spot(name, x, y);
	}

	/** One 64 x 64 dungeon region on the Nythera dungeon row, entered at its arrival tile (32, 5). */
	static Area nytheraDungeon(String name, int regionX, Brief brief, Spot... spots) {
		int bx = regionX * 64, by = NY_DUNGEON_REGION_Y * 64;
		return new Area(name, bx, by, bx + 63, by + 63, bx + 32, by + 5, brief, spots);
	}

	static final List<Area> ALL = new ArrayList<>();

	static {
		ALL.add(new Area("Skullbrine Isle", 1920, 2816, 2111, 3007, 2016, 2900,
				new Brief("A pirate haven on a cursed rock: the first stop of the Void saga, with a ruined shanty town, a graveyard, a swamp and the cave into Blackbrine's Hold.",
						"Players step off the boat at the end of the jetty in Deadwater Cove and walk north up it into Rattlebone Row.",
						"Skull Rock: a giant sculpted skull on the north of the island, staring south over the town, with the cave in its mouth."),
				spot("jetty end, where the boat ties up", 2016, 2879), spot("the cave in Skull Rock's mouth", SkullbrineConstants.CAVE_OUTSIDE_X,
						SkullbrineConstants.CAVE_OUTSIDE_Y), spot("Ilsabet", ILSABET_X, ILSABET_Y),
				spot("Mad Pete's camp", 2029, 2905), spot("the graveyard", 1993, 2916)));
		ALL.add(new Area("Blackbrine's Hold", 1984, 9280, 2047, 9343, SkullbrineConstants.DUNGEON_ARRIVAL_X,
				SkullbrineConstants.DUNGEON_ARRIVAL_Y,
				new Brief("The smugglers' cave under Skull Rock: a winding pirate dungeon whose far end holds the dormant void portal.",
						"Players climb down from the cave in Skull Rock's mouth and arrive at the south end of the cave.",
						"The void portal and its crystals at the far north end, glowing in the dark at the end of the cave."),
				spot("the void portal", 2016, 9332)));
		ALL.add(new Area("Nythera", VoidRealmGenerator.NX0, VoidRealmGenerator.NY0, VoidRealmGenerator.NX0 + VoidRealmGenerator.NSIZE - 1,
				VoidRealmGenerator.NY0 + VoidRealmGenerator.NSIZE - 1, LANDING_X, LANDING_Y,
				new Brief("The continent beyond the void: a ruined old kingdom of camps, a walled Gatehold, a quarry, an inn, woods and the Scar, where most of the saga is played.",
						"Players come through the rift at the crater in the middle of the land and climb out by Oren's camp onto the stone road.",
						"The crater and its rift at the heart of the land, with the old kingdom's stone road leading away to the walled Gate."),
				spot("Oren", OREN_X, OREN_Y), spot("the fishing camp", FISH_CAMP_X, FISH_CAMP_Y), spot("the croft", CROFT_X, CROFT_Y),
				spot("the woodcutters' camp", WOOD_CAMP_X, WOOD_CAMP_Y), spot("the Scar watch", WATCH_X, WATCH_Y),
				spot("Gatehold", GATEHOLD_X, GATEHOLD_Y), spot("the quarry", QUARRY_X, QUARRY_Y), spot("the inn", INN_X, INN_Y),
				spot("the smugglers' cove", SMUGGLER_COVE_X, SMUGGLER_COVE_Y), spot("Silkwood", SILKWOOD_X, SILKWOOD_Y),
				spot("the barrow door", BARROW_DOOR_X, BARROW_DOOR_Y), spot("the Hollow fissure", FISSURE_X, FISSURE_Y),
				spot("the Frostfang cave", FROST_CAVE_X, FROST_CAVE_Y), spot("the Gatehold hatch", HATCH_X, HATCH_Y),
				spot("Maelis", MAELIS_X, MAELIS_Y)));
		ALL.add(new Area("The Void arena", ARENA_REGION_X * 64, ARENA_REGION_Y * 64, ARENA_REGION_X * 64 + 63, ARENA_REGION_Y * 64 + 63,
				ARENA_ARRIVAL_X, ARENA_ARRIVAL_Y,
				new Brief("The Hollow King's arena: a floating platform in the void where the saga's boss fight is held.",
						"Players are pulled in at the south edge of the platform, facing the king across it.",
						"The platform itself, ringed with void crystals, floating alone in the black."),
				spot("the Hollow King", KING_X, KING_Y)));
		ALL.add(new Area("The Starless expanse", STARLESS_REGION_X * 64, STARLESS_REGION_Y * 64, STARLESS_REGION_X * 64 + 63,
				STARLESS_REGION_Y * 64 + 63, STARLESS_ARRIVAL_X, STARLESS_ARRIVAL_Y,
				new Brief("The optional mega boss's lair: a ring of star pillars in the void where the Starless waits.",
						"Players step through the gate at the south of the expanse and see the pillars ahead of them.",
						"The ring of star pillars around the Starless, lit against the dark."),
				spot("the Starless", STARLESS_X, STARLESS_Y)));
		ALL.add(nytheraDungeon("The Sunken Barrow", BARROW_REGION_X,
				new Brief("A drowned burial vault under the barrow mound, home to the dead of the old kingdom.",
						"Players climb down through the barrow door and arrive at the south end of the vault.",
						"The flooded burial hall with its tombs, deep in the mound.")));
		ALL.add(nytheraDungeon("Hollowdeep", HOLLOWDEEP_REGION_X,
				new Brief("The cavern under the Scar where the void bleeds into Nythera, crusted with crystal.",
						"Players drop through the Hollow fissure and land at the south of the cavern.",
						"The dark void pool in the cavern, with crystals growing out of it.")));
		ALL.add(nytheraDungeon("Frostfang", FROSTFANG_REGION_X,
				new Brief("An ice cave in the northern hills, a lair of cold things and a source of rare ore.",
						"Players walk in from the cave mouth in the hills and arrive at the south of the cave.",
						"The frozen cavern walls, white and blue, that set it apart from every other cave.")));
		ALL.add(nytheraDungeon("The Crown Vault", VAULT_REGION_X,
				new Brief("The old kingdom's sealed vault under Gatehold, where the Ashes of the Crown quest ends.",
						"Players climb down the Gatehold hatch and arrive at the south of the vault, before its ward-seal.",
						"The ward-seal across the passage, and the ember chamber beyond it."),
				spot("the ward-seal", VAULT_REGION_X * 64 + SEAL_LX, NY_DUNGEON_REGION_Y * 64 + SEAL_LY),
				spot("the ember pedestal", VAULT_REGION_X * 64 + EMBER_LX, NY_DUNGEON_REGION_Y * 64 + EMBER_LY))
				.alsoArrives(VAULT_REGION_X * 64 + SEAL_LX, NY_DUNGEON_REGION_Y * 64 + SEAL_LY + 2,
						"CrownQuest.touchSeal steps the player through the seal once the three wards are lit"));
	}

	private WorldAreas() {
	}
}
