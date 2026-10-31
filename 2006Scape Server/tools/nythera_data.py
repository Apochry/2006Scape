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
save('npcdrops.json', drops)

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
save('shops.json', shops)

# spawns: drop everything previously in Nythera's regions except Oren, then add camps + generated wildlife
X0, Y0, N = 20 * 64, 39 * 64, 7 * 64
inside = lambda s: X0 <= s['x'] < X0 + N and Y0 <= s['y'] < Y0 + N and s['height'] == 0
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
count = 0
for line in open(spawnfile):
    i, x, y, w = map(int, line.split())
    if i in (3868, 3869):
        spawn(i, x, y, 1, 4 if i == 3868 else 5, 28 if i == 3868 else 40, 0)
    else:
        spawn(i, x, y, w)
    count += 1
save('spawns.json', sp)
print('wildlife spawns', count, 'total spawns', len(sp))
