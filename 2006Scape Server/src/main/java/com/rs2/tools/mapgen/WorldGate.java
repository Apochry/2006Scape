package com.rs2.tools.mapgen;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The gate every generated world has to pass before it is written to the cache. The generators build the world in
 * memory, then this lints every declared area (WorldAreas) with WorldLint, and refuses the whole world if anything is
 * wrong:
 * <ul>
 * <li>any WorldLint finding that hasn't been accepted, with the reason it's right, in tools/worldlint-accepted.txt;</li>
 * <li>any of an area's must-reach spots (npcs, doors, camps) that can't be walked to from where the player arrives;</li>
 * <li>anything the generators placed outside every declared area (declare the area, with its brief, first);</li>
 * <li>an accepted-file line with no reason, or one that no longer matches anything (it would hide the next real
 * mistake made in that spot, so it has to go).</li>
 * </ul>
 * Each finding names the generator line that produced it. A pass writes tools/worldgate.stamp, a hash of the world code,
 * which the Claude Code hooks check before letting world changes be committed or a session end: world code can only
 * go in once the world it builds has been through here.
 *
 * Runs from CursedIslandGenerator.main; -Dworldgate=warn reports without refusing (and writes no stamp), for
 * experimenting. The report and a map of each area with its findings ringed go to target/worldgate/.
 */
final class WorldGate {

	static final Path ACCEPTED = Paths.get("tools/worldlint-accepted.txt"), FILES = Paths.get("tools/worldgate.files"),
			STAMP = Paths.get("tools/worldgate.stamp"), OUT = Paths.get("target/worldgate");

	private WorldGate() {
	}

	/** Lints the world; true when it may be written. Prints the report either way. */
	static boolean check(Map<Integer, MapRegion> world, ConfigDefinitions.Table locs, CacheEditor cache) throws Exception {
		boolean warnOnly = "warn".equals(System.getProperty("worldgate"));
		Files.createDirectories(OUT);
		List<String> bad = new ArrayList<>();
		List<WorldLint.Accepted> accepted = WorldLint.readAccepted(ACCEPTED, bad);
		Map<String, List<String>> failures = new LinkedHashMap<>();
		StringBuilder report = new StringBuilder();
		int acceptedHits = 0;

		for (WorldAreas.Area area : WorldAreas.ALL) {
			WorldLint lint = new WorldLint(world, locs, area.x0, area.y0, area.x1, area.y1);
			lint.accepted = accepted;
			lint.checkAll(area.arrivals());
			for (WorldAreas.Spot s : area.mustReach) {
				if (!lint.reachable(s.x, s.y)) {
					lint.report("MUST-REACH", s.x, s.y, s.name + " can't be walked to from the arrival at " + area.fromX + "," + area.fromY);
				}
			}
			acceptedHits += lint.acceptedHits;
			List<String> found = new ArrayList<>();
			for (String kind : WorldLint.ORDER) {
				found.addAll(lint.findings.getOrDefault(kind, List.of()));
			}
			report.append("== ").append(area.name).append(" (").append(area.x0).append(',').append(area.y0).append(" to ").append(area.x1)
					.append(',').append(area.y1).append(")\n   purpose:  ").append(area.brief.purpose).append("\n   arrival:  ")
					.append(area.brief.arrival).append("\n   landmark: ").append(area.brief.landmark).append('\n');
			found.forEach(f -> report.append("   ").append(f.replace("\n", "\n   ")).append('\n'));
			report.append("   -> ").append(found.isEmpty() ? "clean" : found.size() + " finding(s)").append(", map: ")
					.append(OUT.resolve(area.slug() + ".png")).append("\n\n");
			if (!found.isEmpty()) {
				failures.put(area.name, found);
			}
			try {
				lint.png(cache, OUT.resolve(area.slug() + ".png").toString(), null);
			} catch (Exception e) {
				report.append("   (couldn't draw the map: ").append(e).append(")\n");
			}
		}

		List<String> world0 = new ArrayList<>();
		// everything the generators placed must sit in a declared area
		for (MapRegion r : world.values()) {
			boolean covered = false;
			for (WorldAreas.Area a : WorldAreas.ALL) {
				covered |= a.x0 <= r.regionX * 64 + 63 && a.x1 >= r.regionX * 64 && a.y0 <= r.regionY * 64 + 63 && a.y1 >= r.regionY * 64;
			}
			if (!covered) {
				world0.add(String.format("%-18s region %d,%d (%d,%d)  the generators build here but no area in WorldAreas covers it: declare it, with its brief",
						"UNREGISTERED", r.regionX, r.regionY, r.regionX * 64, r.regionY * 64));
				continue;
			}
			Map<String, int[]> stray = new LinkedHashMap<>();
			for (MapRegion.MapObject o : r.objects) {
				int x = r.regionX * 64 + o.x, y = r.regionY * 64 + o.y;
				boolean in = false;
				for (WorldAreas.Area a : WorldAreas.ALL) {
					in |= a.contains(x, y);
				}
				if (!in && !Origin.STOCK.equals(o.origin)) {
					stray.putIfAbsent(o.origin, new int[] { x, y });
				}
			}
			stray.forEach((origin, p) -> world0.add(String.format("%-18s %d,%d  placed outside every declared area\n%-18s <- %s", "UNREGISTERED",
					p[0], p[1], "", origin)));
		}
		for (String b : bad) {
			world0.add(String.format("%-18s %s", "ACCEPT-UNREADABLE", b));
		}
		for (WorldLint.Accepted a : accepted) {
			if (a.hits == 0) {
				world0.add(String.format("%-18s line %d: \"%s\"  matches nothing any more: delete it, or it will hide the next real mistake there",
						"ACCEPT-STALE", a.lineNo, a.line));
			}
		}
		if (!world0.isEmpty()) {
			failures.put("The world as a whole", world0);
			report.append("== The world as a whole\n");
			world0.forEach(f -> report.append("   ").append(f.replace("\n", "\n   ")).append('\n'));
			report.append('\n');
		}

		int total = failures.values().stream().mapToInt(List::size).sum();
		String verdict = total == 0 ? "WORLD GATE: PASSED - " + WorldAreas.ALL.size() + " areas clean (" + acceptedHits + " accepted findings)"
				: "WORLD GATE: " + (warnOnly ? "WARNING" : "REFUSED") + " - " + total + " finding(s) in " + String.join(", ", failures.keySet());
		report.append(verdict).append('\n');
		Files.write(OUT.resolve("report.txt"), report.toString().getBytes(StandardCharsets.UTF_8));

		System.out.println();
		if (total == 0) {
			System.out.println(verdict);
			if (!warnOnly) {
				String hash = sourceHash();
				Files.write(STAMP, (hash + "\n# Written by WorldGate when the world built from the files in tools/worldgate.files passed the gate.\n"
						+ "# The Claude Code hooks (.claude/hooks/worldgate-*.sh) compare it with those files before world code can be\n"
						+ "# committed or a session can end. Never edit it by hand: regenerate the world (tools/regen-world.sh).\n")
						.getBytes(StandardCharsets.UTF_8));
				System.out.println("Stamped " + STAMP + " " + hash.substring(0, 12));
			}
			return true;
		}
		failures.forEach((area, found) -> {
			System.out.println("== " + area);
			found.forEach(f -> System.out.println("   " + f.replace("\n", "\n   ")));
		});
		System.out.println();
		System.out.println(verdict);
		System.out.println("Full report: " + OUT.resolve("report.txt") + "; each area's map, findings ringed: " + OUT + File.separator + "<area>.png");
		System.out.println("Fix each finding at the line it names. Only if one is truly right as it is, add it to " + ACCEPTED
				+ " with the reason why.");
		return warnOnly;
	}

	/** SHA-256 of the world code: the files listed in tools/worldgate.files, in that order, concatenated. */
	static String sourceHash() throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		for (String line : Files.readAllLines(FILES)) {
			String f = line.replaceAll("#.*", "").trim();
			if (!f.isEmpty()) {
				md.update(Files.readAllBytes(Paths.get(f)));
			}
		}
		StringBuilder b = new StringBuilder();
		for (byte x : md.digest()) {
			b.append(String.format("%02x", x));
		}
		return b.toString();
	}

	/** {@code java ... WorldGate hash}: prints the current world-code hash (what the hooks compare with the stamp). */
	public static void main(String[] args) throws Exception {
		System.out.println(sourceHash());
	}
}
