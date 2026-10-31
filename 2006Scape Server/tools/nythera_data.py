"""Writes Nythera's people, creatures, drops, shops and spawns into data/cfg.

Usage (from '2006Scape Server'):
  java -Dnythera.spawns=/tmp/ny_spawns.txt -cp target/server-1.0-jar-with-dependencies.jar \
       com.rs2.tools.mapgen.CursedIslandGenerator
  python3 tools/nythera_data.py /tmp/ny_spawns.txt

The generator picks the wildlife tiles (it knows where the meadows, woods, shores and the Scar are); this script
replaces every spawn inside Nythera's regions (except Oren) with the camps' people plus those wildlife spawns.
"""
import json, os, sys
cfg = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'data', 'cfg') + '/'

def gson(text):
    # match the Gson escaping the original files use
    return text.replace("'", '\\u0027')
spawnfile = sys.argv[1]

def load(n): return json.load(open(cfg + n))
def save(n, d): open(cfg + n, 'w').write(json.dumps(d, indent=2) + ('' if n == 'npcdrops.json' else '\n'))

def npcdef(i, name, ex, combat=0, hp=0, maxhit=0, size=1, spd=0, att=0, dfn=0, dth=0, ab=0, dm=0, attackable=False, aggressive=False):
    return {'id': i, 'name': name, 'examine': ex, 'respawn': 30 if not attackable else 40, 'combat': combat, 'hitpoints': hp,
            'maxHit': maxhit, 'size': size, 'attackSpeed': spd, 'attackAnim': att, 'defenceAnim': dfn, 'deathAnim': dth,
            'attackBonus': ab, 'defenceMelee': dm, 'defenceRange': dm, 'defenceMage': 0, 'attackable': attackable,
            'aggressive': aggressive, 'retreats': False, 'poisonous': False}

people = [
    npcdef(3863, 'Bram Netwright', 'His hands smell of lake-weed and pipe smoke.'),
    npcdef(3864, 'Marta Tollan', 'She looks like she could wrestle a ram. She probably has.'),
    npcdef(3865, 'Hob Ashby', 'A woodcutter with arms like oak limbs.'),
    npcdef(3866, 'Warden Kael', "She hasn't taken her eyes off the Scar since you arrived."),
    npcdef(3867, 'Wandering pilgrim', 'Walking somewhere slowly, and in no hurry to arrive.'),
    npcdef(3868, 'Hollowed wolf', 'Its eyes are empty. Something else is looking out of them.', 34, 30, 4, 2, 4000, 75, 76, 78, 28, 22, True, True),
    npcdef(3869, 'Hollow husk', 'Whoever this was, the Scar emptied them out and kept walking them around.', 41, 42, 5, 1, 4000, 299, 300, 302, 40, 34, True, True),
    npcdef(3870, 'Captain Ysolde', "Captain of Gatehold's watch. All six of them."),
    npcdef(3871, 'Pell the Trader', "He'll sell you anything. He'll buy most things, too."),
    npcdef(3872, 'Dorrin Anvilhand', 'A smith with forearms like hams and a voice like a bellows.'),
    npcdef(3873, 'Tess Quarryhand', 'Covered in rock dust from boots to eyebrows.'),
    npcdef(3874, 'Morra', 'Keeper of the Last Hearth. The fire never goes out, and neither does she.'),
    npcdef(3875, 'Sly Nettle', "Smiles with every tooth he's got. That's not many."),
    npcdef(3876, 'The Barrow Thane', 'A king of the old barrows who would not stay buried.', 68, 110, 9, 1, 4000, 260, 261, 263, 70, 60, True, True),
    npcdef(3877, 'The Hollow Matriarch', 'The mother of husks. Her heart beats with nothing at all.', 92, 150, 12, 2, 4000, 75, 76, 78, 95, 80, True, True),
    npcdef(3878, 'Rimeclaw', 'The cold of the mountain, given claws.', 110, 180, 14, 2, 5000, 128, 129, 131, 115, 100, True, True),
]
ids = {p['id'] for p in people}

# npcDefinitions.json: drop placeholder "Suspect" rows for our ids, replace any earlier version of ours
txt = open(cfg + 'npcDefinitions.json').read()
d = json.loads(txt)
d = [n for n in d if n['id'] not in ids]
d += people
open(cfg + 'npcDefinitions.json', 'w').write(gson(json.dumps(d, indent=2)) + '\n')

n = load('npc.json')
n = [x for x in n if x['id'] not in ids]
n += [{'hitpoints': p['hitpoints'], 'name': p['name'], 'combat': p['combat'], 'id': p['id']} for p in people]
save('npc.json', n)

drops = load('npcdrops.json')
drops = [x for x in drops if x['id'] not in ids]
drops.append({'id': 3868, 'items': [
    {'item_id': 2859, 'chance': 0, 'amounts': [1, 1]},      # wolf bones
    {'item_id': 995, 'chance': 2, 'amounts': [8, 40]},
    {'item_id': 1617, 'chance': 200, 'amounts': [1, 1]},     # uncut diamond
]})
drops.append({'id': 3869, 'items': [
    {'item_id': 526, 'chance': 0, 'amounts': [1, 1]},
    {'item_id': 995, 'chance': 2, 'amounts': [15, 70]},
    {'item_id': 207, 'chance': 6, 'amounts': [1, 1]},        # grimy ranarr
    {'item_id': 1623, 'chance': 20, 'amounts': [1, 1]},      # uncut sapphire
    {'item_id': 1621, 'chance': 40, 'amounts': [1, 1]},      # uncut emerald
    {'item_id': 1215, 'chance': 400, 'amounts': [1, 1]},     # dragon dagger
]})
# the three bosses: always good loot, and a rare unique each
boss_drops = {
    3876: [(532, 0, 1, 1), (995, 0, 800, 2000), (1319, 15, 1, 1), (1333, 10, 1, 1), (1127, 20, 1, 1), (1163, 12, 1, 1),
           (565, 3, 20, 40), (2440, 6, 1, 1), (7969, 11, 1, 1)],  # big bones, coins, rune gear, blood runes, pots, Barrowsteel
    3877: [(532, 0, 1, 1), (995, 0, 1200, 3000), (1617, 4, 1, 2), (1615, 30, 1, 1), (564, 3, 30, 60), (3024, 5, 1, 2),
           (1213, 10, 1, 1), (1149, 12, 1, 1), (7968, 11, 1, 1)],  # gems, cosmics, restores, rune dagger, dragon helm, Hollowheart
    3878: [(532, 0, 1, 1), (995, 0, 1500, 4000), (451, 2, 1, 2), (2363, 6, 1, 1), (560, 3, 30, 60), (385, 2, 3, 6),
           (1079, 12, 1, 1), (1201, 12, 1, 1), (7970, 11, 1, 1)],  # runite ore & bar, death runes, sharks, rune legs/kite, Frostbound
}
for i, items in boss_drops.items():
    # drops roll in order and stop at the first hit, so the unique goes first (after the always-drops): a true 1/12
    items = [d for d in items if d[1] == 0] + [d for d in items if d[0] in (7968, 7969, 7970)] + \
            [d for d in items if d[1] != 0 and d[0] not in (7968, 7969, 7970)]
    drops = [x for x in drops if x['id'] != i]
    drops.append({'id': i, 'items': [{'item_id': it, 'chance': c, 'amounts': [lo, hi]} for it, c, lo, hi in items]})
save('npcdrops.json', drops)

# the bosses' uniques: equipment bonuses, in ItemDefinitions.json's own compact format
uniques = {
    7968: (0.3, {'attackStab': 6, 'attackSlash': 6, 'attackCrush': 6, 'attackMagic': 6, 'attackRange': 6, 'defenceStab': 3,
                 'defenceSlash': 3, 'defenceCrush': 3, 'defenceMagic': 3, 'defenceRange': 3, 'strengthBonus': 12, 'prayerBonus': 2}),
    7969: (1.8, {'attackStab': 44, 'attackSlash': 54, 'attackCrush': -2, 'defenceSlash': 3, 'defenceCrush': 2, 'strengthBonus': 56}),
    7970: (0.9, {'defenceStab': 6, 'defenceSlash': 6, 'defenceCrush': 6, 'defenceMagic': 3, 'defenceRange': 6, 'strengthBonus': 2,
                 'prayerBonus': 1}),
}
# text-level edit: keep the rest of the file byte-for-byte; our entries go (once) at the end, as 7966/7967 do
import re
txt = open(cfg + 'ItemDefinitions.json').read()
for i in uniques:
    txt = re.sub(r',\n   \{\n      "id":%d,\n[^{}]*(\{[^{}]*\})?\n   \}' % i, '', txt)
entries = ''.join(',\n   {\n      "id":%d,\n      "weight":%s,\n      "bonuses":%s\n   }'
                  % (i, w, json.dumps(b, separators=(',', ':'))) for i, (w, b) in sorted(uniques.items()))
end = txt.rindex('\n]')
open(cfg + 'ItemDefinitions.json', 'w').write(txt[:end] + entries + txt[end:])

shops = load('shops.json')
shops = [s for s in shops if s['id'] not in (350, 351)]
shops.append({'buyModifier': 2, 'sellModifier': 2, 'name': "Netwright's Tackle", 'id': 350, 'items': [
    {'itemId': 303, 'itemAmount': 5}, {'itemId': 307, 'itemAmount': 5}, {'itemId': 309, 'itemAmount': 3},
    {'itemId': 313, 'itemAmount': 500}, {'itemId': 314, 'itemAmount': 1000}, {'itemId': 305, 'itemAmount': 2},
    {'itemId': 333, 'itemAmount': 10}, {'itemId': 329, 'itemAmount': 10}, {'itemId': 351, 'itemAmount': 5}]})
shops.append({'buyModifier': 2, 'sellModifier': 2, 'name': "Tollan's Croft", 'id': 351, 'items': [
    {'itemId': 2309, 'itemAmount': 10}, {'itemId': 1965, 'itemAmount': 20}, {'itemId': 1927, 'itemAmount': 10},
    {'itemId': 1944, 'itemAmount': 20}, {'itemId': 1759, 'itemAmount': 20}, {'itemId': 1735, 'itemAmount': 3},
    {'itemId': 1925, 'itemAmount': 10}, {'itemId': 1931, 'itemAmount': 10}, {'itemId': 1942, 'itemAmount': 20},
    {'itemId': 952, 'itemAmount': 3}]})
def shop(i, name, stock):
    global shops
    shops = [s for s in shops if s['id'] != i]
    shops.append({'buyModifier': 2, 'sellModifier': 2, 'name': name, 'id': i,
                  'items': [{'itemId': a, 'itemAmount': b} for a, b in stock]})
shop(352, "Pell's Provisions", [(1931, 10), (1935, 10), (1925, 10), (590, 5), (954, 5), (946, 5), (1755, 5), (2347, 5),
                                (1351, 5), (1265, 5), (233, 5), (36, 10), (1785, 3), (5341, 5)])
shop(353, "Dorrin's Forge", [(2347, 10), (2349, 20), (2351, 20), (2353, 10), (2359, 5), (1117, 3), (1121, 3), (1073, 3),
                             (1109, 2), (1123, 2), (1159, 2), (1197, 2), (1287, 3), (1301, 2), (453, 50)])
shop(354, "Quarrywright's Picks", [(1265, 10), (1267, 8), (1269, 6), (1273, 4), (1271, 2), (1755, 5), (1823, 5)])
shop(355, 'The Last Hearth', [(2003, 20), (2289, 10), (1993, 15), (1917, 20), (1891, 10), (2327, 15), (1985, 10),
                              (2011, 10), (379, 20), (373, 15), (1971, 30)])
shop(356, "Nettle's Hold", [(954, 10), (301, 10), (311, 5), (1623, 5), (1621, 3), (1619, 2), (1617, 1),
                            (1201, 1), (1347, 1), (562, 100), (561, 50), (2489, 2), (1033, 2)])
save('shops.json', shops)

# spawns: drop everything previously in Nythera's regions except Oren, then add camps + generated wildlife
X0, Y0, N = 20 * 64, 39 * 64, 7 * 64
DUNGEONS = (20, 22, 24)  # the Sunken Barrow, Hollowdeep, Frostfang (region y 151)
inside = lambda s: s['height'] == 0 and ((X0 <= s['x'] < X0 + N and Y0 <= s['y'] < Y0 + N)
                                         or (s['x'] >> 6 in DUNGEONS and s['y'] >> 6 == 151))
sp = load('spawns.json')
sp = [s for s in sp if not inside(s) or s['id'] == 3862]
def spawn(i, x, y, walk, mh=0, att=0, st=0):
    sp.append({'maxHit': mh, 'strength': st, 'attack': att, 'x': x, 'y': y, 'id': i, 'walk': walk, 'height': 0})
# people: walk 1 = mill about, 2..5 = stand facing N,S,E,W
spawn(3863, 1455, 2713, 5)   # Bram at the jetty's root, facing the lake (west)
spawn(3864, 1520, 2664, 1)   # Marta in her yard
spawn(3865, 1448, 2650, 1)   # Hob by his cookfire
spawn(3866, 1576, 2741, 4)   # Kael watching the Scar (east)
spawn(3867, 1538, 2689, 1)   # pilgrims resting along the roads
spawn(3867, 1493, 2745, 1)
spawn(3867, 1553, 2731, 1)
# combat stats for the monsters the generator places (maxHit, attack/strength)
STATS = {3868: (4, 28), 3869: (5, 40), 3860: (3, 15), 90: (3, 20), 91: (3, 20), 92: (4, 25), 93: (6, 40), 103: (3, 20),
         60: (4, 25), 96: (3, 20), 111: (7, 50), 125: (7, 35), 184: (5, 25), 1883: (4, 30), 9: (6, 20),
         3876: (9, 70), 3877: (12, 95), 3878: (14, 115)}
count = 0
for line in open(spawnfile):
    i, x, y, w = map(int, line.split())
    mh, at = STATS.get(i, (0, 0))
    spawn(i, x, y, w, mh, at, at)
    count += 1
save('spawns.json', sp)
print('wildlife spawns', count, 'total spawns', len(sp))
