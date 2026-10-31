/**
 * RuneLite-style hitpoints, prayer and run orbs on the left edge of the
 * minimap, each with its value in a box beside it. They are drawn over the
 * finished frame (screen coordinates) so the boxes can sit on the stone frame
 * between the game view and the minimap. The run orb toggles running.
 */
final class MinimapOrbs {

	static boolean enabled = true;

	/** Menu action ID for toggling run (unused by the base client). */
	static final int TOGGLE_RUN_ACTION = 697;
	/** Settings-tab run button; the server toggles running for it. */
	private static final int RUN_BUTTON_ID = 19136;
	private static final int RUN_CONFIG = 173;

	private static final int ORB_X = 563;
	private static final int[] ORB_Y = {56, 89, 122};
	private static final int RADIUS = 13;
	private static final int BOX_WIDTH = 25;
	private static final int BOX_HEIGHT = 15;

	private static Sprite[] icons;

	private MinimapOrbs() {
	}

	/** Draws the orbs; the finished frame must be the active DrawingArea. */
	static void draw(Game game) {
		if (!enabled) {
			return;
		}
		loadIcons(game);
		boolean running = game.variousSettings[RUN_CONFIG] == 1;
		boolean runHovered = orbAt(game.mouseX, game.mouseY) == 2;
		drawOrb(game, 0, game.currentStats[3], game.maxStats[3], 0xa8130e, false);
		drawOrb(game, 1, game.currentStats[5], game.maxStats[5], 0x3fb0cb, false);
		drawOrb(game, 2, game.energy, 100, running ? 0xd9b51a : 0x6b6045, runHovered);
	}

	private static void drawOrb(Game game, int index, int value, int max, int fillColor, boolean highlighted) {
		max = Math.max(1, max);
		int cy = ORB_Y[index];
		double fill = Math.max(0, Math.min(1, value / (double) max));

		int boxX = ORB_X - RADIUS - BOX_WIDTH + 2;
		int boxY = cy - BOX_HEIGHT / 2 + 3;
		OverlayDraw.box(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, 0x302a22, 0x0e0c0a);
		game.aTextDrawingArea_1270.textCenterShadow(valueColor(value, max), boxX + BOX_WIDTH / 2 - 1, String.valueOf(value), boxY + 12, true);

		OverlayDraw.orb(ORB_X, cy, RADIUS, fill, fillColor, highlighted);
		Sprite icon = icons == null ? null : icons[index];
		if (icon != null) {
			icon.drawSprite(ORB_X - icon.width / 2, cy - icon.height / 2);
		}
	}

	private static void loadIcons(Game game) {
		if (icons != null) {
			return;
		}
		icons = new Sprite[3];
		try {
			StreamLoader media = game.mediaArchive();
			icons[0] = OverlayDraw.shrink(new Sprite(media, "staticons", 6), 15);
			icons[1] = OverlayDraw.shrink(new Sprite(media, "staticons", 4), 15);
			icons[2] = OverlayDraw.shrink(new Sprite(media, "sideicons", 11), 16);
		} catch (Exception e) {
			System.out.println("Could not load orb icons: " + e);
		}
	}

	private static int valueColor(int value, int max) {
		double pct = value / (double) max;
		if (pct >= 0.75) {
			return 0x00ff00;
		}
		if (pct >= 0.5) {
			return 0xffff00;
		}
		if (pct >= 0.25) {
			return 0xff9900;
		}
		return 0xff0000;
	}

	/** Which orb (0 hitpoints, 1 prayer, 2 run) is under a screen position, or -1. */
	static int orbAt(int screenX, int screenY) {
		if (!enabled) {
			return -1;
		}
		for (int i = 0; i < ORB_Y.length; i++) {
			int dx = screenX - ORB_X;
			int dy = screenY - ORB_Y[i];
			if (dx * dx + dy * dy <= RADIUS * RADIUS) {
				return i;
			}
		}
		return -1;
	}

	/** Adds the run orb's menu option while it is hovered; returns true if it did. */
	static boolean addMenuOption(Game game) {
		if (orbAt(game.mouseX, game.mouseY) != 2) {
			return false;
		}
		game.menuActionName[1] = game.variousSettings[RUN_CONFIG] == 1 ? "Toggle Run (On)" : "Toggle Run (Off)";
		game.menuActionID[1] = TOGGLE_RUN_ACTION;
		game.menuActionRow = 2;
		return true;
	}

	static void toggleRun(Game game) {
		game.stream.createFrame(185);
		game.stream.writeWord(RUN_BUTTON_ID);
	}
}
