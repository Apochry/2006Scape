import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RuneLite-style XP counter and XP drops. The counter in the top-right of the
 * game view shows the skill being trained, its XP and progress to the next
 * level; drops rise into it as XP is gained. Hovering the counter shows a
 * tracker box with XP/hr, XP to level, actions left and time to level.
 */
final class XpTracker {

	static boolean dropsEnabled = true;
	static boolean counterEnabled = true;

	private static final int SKILLS = Skills.skillsCount;
	private static final int HITPOINTS = 3;
	/** Sprite per skill ID: 0-17 index "staticons", 100+ index "staticons2". */
	private static final int[] ICONS = {0, 2, 1, 6, 3, 4, 5, 15, 17, 11, 14, 16, 10, 13, 12, 8, 7, 9, 101, 102, 100};
	private static final long DROP_LIFE = 1300;
	private static final int DROP_RISE = 46;
	private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

	// Counter box, in game view coordinates (the view is 512x334)
	private static final int WIDTH = 124;
	private static final int X = 512 - WIDTH - 6;
	private static final int Y = 5;

	private final Game game;
	private Sprite[] icons;
	private final boolean[] known = new boolean[SKILLS];
	private final int[] exp = new int[SKILLS];
	private final int[] gained = new int[SKILLS];
	private final int[] lastDrop = new int[SKILLS];
	private final long[] startTime = new long[SKILLS];
	private long sessionStart;
	private int counterSkill = -1;
	private final List<Drop> drops = new ArrayList<>();

	private static final class Drop {
		final long time;
		final List<Integer> skills = new ArrayList<>();
		int amount;

		Drop(long time) {
			this.time = time;
		}
	}

	XpTracker(Game game) {
		this.game = game;
	}

	/** Forget everything, e.g. when logging in again. */
	void reset() {
		for (int i = 0; i < SKILLS; i++) {
			known[i] = false;
			gained[i] = 0;
			lastDrop[i] = 0;
			startTime[i] = 0;
		}
		counterSkill = -1;
		sessionStart = System.currentTimeMillis();
		drops.clear();
	}

	/** Called for every skill update from the server. */
	void onExperience(int skill, int newExp) {
		if (skill < 0 || skill >= SKILLS) {
			return;
		}
		if (!known[skill]) {
			// The first update after login is the starting value, not a gain
			known[skill] = true;
			exp[skill] = newExp;
			return;
		}
		int diff = newExp - exp[skill];
		exp[skill] = newExp;
		if (diff <= 0) {
			return;
		}
		long now = System.currentTimeMillis();
		if (startTime[skill] == 0) {
			startTime[skill] = now;
		}
		gained[skill] += diff;
		lastDrop[skill] = diff;
		// Combat also gives hitpoints XP; keep the counter on the combat skill
		if (skill != HITPOINTS || counterSkill == -1) {
			counterSkill = skill;
		}

		// XP from several skills in the same tick shares one drop
		Drop drop = drops.isEmpty() ? null : drops.get(drops.size() - 1);
		if (drop == null || now - drop.time > 150) {
			drop = new Drop(now);
			drops.add(drop);
		}
		if (!drop.skills.contains(skill)) {
			drop.skills.add(skill);
		}
		drop.amount += diff;
	}

	/** Draws the counter and drops; the main game view's DrawingArea must be active. */
	void draw() {
		loadIcons();
		long now = System.currentTimeMillis();
		int counterBottom = Y;
		if (counterEnabled) {
			counterBottom = drawCounter(now);
		}
		if (dropsEnabled) {
			drawDrops(now, counterBottom);
		} else {
			drops.clear();
		}
	}

	/** Returns the y just below the counter. */
	private int drawCounter(long now) {
		boolean total = counterSkill == -1;
		int height = total ? 22 : 33;
		background(X, Y, WIDTH, height);
		if (total) {
			game.aTextDrawingArea_1271.textLeftShadow(true, X + 6, 0xff981f, "Total XP", Y + 15);
			game.chatTextDrawingArea.textRightShadow(true, X + WIDTH - 6, 0xffffff, FORMAT.format(totalExp()), Y + 16);
		} else {
			Sprite icon = icon(counterSkill);
			if (icon != null) {
				icon.drawSprite(X + 5, Y + 11 - icon.height / 2);
			}
			game.chatTextDrawingArea.textRightShadow(true, X + WIDTH - 6, 0xffffff, FORMAT.format(exp[counterSkill]), Y + 17);
			progressBar(X + 5, Y + 23, WIDTH - 10, 6, levelProgress(counterSkill));
		}
		int mouseX = game.mouseX - 4;
		int mouseY = game.mouseY - 4;
		if (mouseX >= X && mouseX < X + WIDTH && mouseY >= Y && mouseY < Y + height) {
			drawTooltip(total, Y + height + 3, now);
		}
		return Y + height;
	}

	private void drawDrops(long now, int counterBottom) {
		int right = X + WIDTH - 6;
		for (Iterator<Drop> it = drops.iterator(); it.hasNext();) {
			Drop drop = it.next();
			long age = now - drop.time;
			if (age > DROP_LIFE) {
				it.remove();
				continue;
			}
			// Rise from below the counter until it reaches it, then vanish
			int y = counterBottom + 12 + DROP_RISE - (int) (age * DROP_RISE / DROP_LIFE);
			String text = FORMAT.format(drop.amount);
			int x = right - game.chatTextDrawingArea.getTextWidth(text);
			game.chatTextDrawingArea.textLeftShadow(true, x, 0xffffff, text, y);
			for (int i = drop.skills.size() - 1; i >= 0; i--) {
				Sprite icon = icon(drop.skills.get(i));
				if (icon != null) {
					x -= icon.width + 2;
					icon.drawSprite(x, y - 6 - icon.height / 2);
				}
			}
		}
	}

	private void drawTooltip(boolean total, int top, long now) {
		List<String[]> rows = new ArrayList<>();
		String title;
		if (total) {
			int sessionGained = 0;
			for (int g : gained) {
				sessionGained += g;
			}
			title = "Total XP";
			rows.add(new String[] {"Current:", FORMAT.format(totalExp())});
			rows.add(new String[] {"Gained:", FORMAT.format(sessionGained)});
			rows.add(new String[] {"XP/hr:", FORMAT.format(perHour(sessionGained, sessionStart, now))});
		} else {
			int skill = counterSkill;
			int level = levelFor(exp[skill]);
			int next = level >= 99 ? -1 : Game.anIntArray1019[level - 1];
			int toLevel = next < 0 ? 0 : next - exp[skill];
			long perHour = perHour(gained[skill], startTime[skill], now);
			title = capitalise(Skills.skillNames[skill]) + " - level " + level;
			rows.add(new String[] {"Current:", FORMAT.format(exp[skill])});
			if (next >= 0) {
				rows.add(new String[] {"To level:", FORMAT.format(toLevel)});
			}
			rows.add(new String[] {"Gained:", FORMAT.format(gained[skill])});
			rows.add(new String[] {"XP/hr:", FORMAT.format(perHour)});
			if (next >= 0 && lastDrop[skill] > 0) {
				rows.add(new String[] {"Actions left:", FORMAT.format((toLevel + lastDrop[skill] - 1) / lastDrop[skill])});
			}
			if (next >= 0 && perHour > 0) {
				rows.add(new String[] {"Time to level:", formatDuration(toLevel * 3600000L / perHour)});
			}
		}

		TextDrawingArea font = game.aTextDrawingArea_1271;
		int width = Math.max(WIDTH + 30, font.getTextWidth(title) + 12);
		int height = 19 + rows.size() * 14;
		int x = X + WIDTH - width;
		DrawingArea.fillArea(0xffffa0, top, width, height, 256, x);
		outline(x, top, width, height, 0x000000);
		font.textLeftShadow(false, x + 5, 0x000000, title, top + 14);
		int y = top + 14;
		for (String[] row : rows) {
			y += 14;
			font.textLeftShadow(false, x + 5, 0x5a5a5a, row[0], y);
			font.textRightShadow(false, x + width - 5, 0x000000, row[1], y);
		}
	}

	/** RuneLite's overlay panel: translucent brown with a dark outer and light inner edge. */
	private static void background(int x, int y, int width, int height) {
		DrawingArea.fillArea(0x463d32, y, width, height, 160, x);
		outline(x, y, width, height, 0x0e0c0a);
		outline(x + 1, y + 1, width - 2, height - 2, 0x5a5145);
	}

	private static void progressBar(int x, int y, int width, int height, double progress) {
		DrawingArea.fillArea(0x000000, y, width, height, 256, x);
		DrawingArea.fillArea(0x3d3d3d, y + 1, width - 2, height - 2, 256, x + 1);
		DrawingArea.fillArea(0xe3c31c, y + 1, (int) ((width - 2) * progress), height - 2, 256, x + 1);
	}

	private static void outline(int x, int y, int width, int height, int color) {
		DrawingArea.fillArea(color, y, width, 1, 256, x);
		DrawingArea.fillArea(color, y + height - 1, width, 1, 256, x);
		DrawingArea.fillArea(color, y, 1, height, 256, x);
		DrawingArea.fillArea(color, y, 1, height, 256, x + width - 1);
	}

	private long totalExp() {
		long total = 0;
		for (int i = 0; i < SKILLS; i++) {
			total += game.currentExp[i];
		}
		return total;
	}

	private static long perHour(int gained, long start, long now) {
		if (start == 0 || gained == 0) {
			return 0;
		}
		return (long) (gained * 3600000D / Math.max(1000, now - start));
	}

	private double levelProgress(int skill) {
		int level = levelFor(exp[skill]);
		if (level >= 99) {
			return 1;
		}
		int prev = level <= 1 ? 0 : Game.anIntArray1019[level - 2];
		int next = Game.anIntArray1019[level - 1];
		return Math.max(0, Math.min(1, (exp[skill] - prev) / (double) (next - prev)));
	}

	private static int levelFor(int xp) {
		int level = 1;
		for (int i = 0; i < 98; i++) {
			if (xp >= Game.anIntArray1019[i]) {
				level = i + 2;
			}
		}
		return level;
	}

	private Sprite icon(int skill) {
		return icons == null || skill >= icons.length ? null : icons[skill];
	}

	private void loadIcons() {
		if (icons != null) {
			return;
		}
		icons = new Sprite[ICONS.length];
		try {
			StreamLoader media = game.mediaArchive();
			for (int i = 0; i < ICONS.length; i++) {
				int id = ICONS[i];
				Sprite full = id >= 100 ? new Sprite(media, "staticons2", id - 100) : new Sprite(media, "staticons", id);
				icons[i] = OverlayDraw.shrink(full, 17);
			}
		} catch (Exception e) {
			System.out.println("Could not load skill icons: " + e);
		}
	}

	private static String formatDuration(long ms) {
		long s = ms / 1000;
		if (s >= 3600) {
			return (s / 3600) + "h " + (s % 3600 / 60) + "m";
		}
		if (s >= 60) {
			return (s / 60) + "m " + (s % 60) + "s";
		}
		return s + "s";
	}

	private static String capitalise(String s) {
		return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
