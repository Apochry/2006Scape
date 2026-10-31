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
    npcdef(3869, 'Hollow husk', 'Whoever this was, the Scar emptied them out and kept walking them around.', 41, 42, 5, 1, 4000, 422, 404, 836, 40, 34, True, True),
    npcdef(3870, 'Captain Ysolde', "Captain of Gatehold's watch. All six of them."),
    npcdef(3871, 'Pell the Trader', "He'll sell you anything. He'll buy most things, too."),
    npcdef(3872, 'Dorrin Anvilhand', 'A smith with forearms like hams and a voice like a bellows.'),
    npcdef(3873, 'Tess Quarryhand', 'Covered in rock dust from boots to eyebrows.'),
    npcdef(3874, 'Morra', 'Keeper of the Last Hearth. The fire never goes out, and neither does she.'),
    npcdef(3875, 'Sly Nettle', "Smiles with every tooth he's got. That's not many."),
    npcdef(3876, 'The Barrow Thane', 'A king of the old barrows who would not stay buried.', 68, 110, 9, 1, 4000, 260, 261, 263, 70, 60, True, True),
    npcdef(3877, 'The Hollow Matriarch', 'The mother of husks. Her heart beats with nothing at all.', 92, 150, 12, 2, 4000, 75, 76, 78, 95, 80, True, True),
    npcdef(3878, 'Rimeclaw', 'The cold of the mountain, given claws.', 110, 180, 14, 2, 5000, 128, 129, 131, 115, 100, True, True),
    # Ashes of the Crown
    npcdef(3879, 'Archivist Maelis', 'Ink to the elbows. He knows more about Nythera than Nythera does.'),
    npcdef(3880, 'Silkwood broodmother', 'Every spider in Silkwood came out of her. She would like you to go in.', 44, 70, 6, 2, 4000, 143, 144, 146, 45, 40, True, True),
    npcdef(3881, 'Hollow Herald', 'It speaks with the Hollow King\'s voice, because it has none of its own.', 62, 160, 8, 2, 3000, 2655, 2653, 2654, 70, 60, True, True),
    npcdef(3882, 'Hollow shade', 'A scrap of the dark between the stars, wearing a shape.', 26, 26, 3, 1, 4000, 422, 1834, 836, 30, 22, True, True),
    # the saga: Hold the Gate, Heartforged, The Hollow King, and the Starless
    npcdef(3883, 'Hollow ravager', 'Something big, emptied out, and pointed at the gate.', 48, 80, 6, 1, 4000, 422, 404, 836, 45, 40, True, True),
    npcdef(3884, "Thane's oathsworn", 'It swore to guard the Thane\'s things. Death did not release it.', 40, 55, 5, 1, 4000, 260, 261, 263, 40, 35, True, True),
    npcdef(3885, 'Rimebound sentinel', 'Ice that remembers being a soldier.', 58, 110, 7, 1, 4000, 422, 404, 2304, 60, 55, True, True),
    npcdef(3886, 'Sir Vaelric', 'The last knight of King Aurel. Still keeping his oath.', 66, 170, 9, 1, 4000, 422, 404, 2304, 75, 65, True, True),
    npcdef(3887, 'Hollow knight', 'An empty suit of armour that has forgotten it is empty.', 44, 40, 5, 1, 4000, 422, 404, 2304, 45, 40, True, True),
    npcdef(3888, 'Aurel', 'The last King of Nythera. Free, at the very end.'),
    npcdef(3889, 'The Starless', 'The dark between the stars, looking back at you.', 400, 900, 16, 3, 3000, 422, 404, 2304, 180, 150, True, True),
    npcdef(3890, 'Starless spawn', 'A piece of the Starless that wandered off. It wants to go back.', 72, 60, 8, 1, 4000, 422, 404, 2304, 90, 70, True, True),
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
# Nythera's ordinary monsters roll VoidLoot's tiers in the server (40% just bones / 30% misc / 20% useful / 5% valuable /
# 5% gear, +1% bonus gear); their tables here hold only what they always drop
for i, bones in ((3868, 2859), (3869, 526), (3882, 592), (3883, 532), (3884, 526), (3887, 526)):
    drops = [x for x in drops if x['id'] != i]
    drops.append({'id': i, 'items': [{'item_id': bones, 'chance': 0, 'amounts': [1, 1]}]})
# the three bosses: always good loot, and a rare unique each
boss_drops = {
    3876: [(532, 0, 1, 1), (995, 0, 800, 2000), (1319, 15, 1, 1), (1333, 10, 1, 1), (1127, 20, 1, 1), (1163, 12, 1, 1),
           (565, 3, 20, 40), (2440, 6, 1, 1), (7969, 11, 1, 1), (8015, 15, 1, 1),
           (8068, 19, 1, 1), (8070, 24, 1, 1), (8071, 24, 1, 1), (8069, 34, 1, 1)],  # ... Barrowsteel, Oathsworn battleaxe and plate
    3877: [(532, 0, 1, 1), (995, 0, 1200, 3000), (1617, 4, 1, 2), (1615, 30, 1, 1), (564, 3, 30, 60), (3024, 5, 1, 2),
           (1213, 10, 1, 1), (1149, 12, 1, 1), (7968, 11, 1, 1), (8018, 13, 1, 1), (8075, 24, 1, 1)],  # ... Hollowheart amulet and shortbow, Duskstalker body
    3878: [(532, 0, 1, 1), (995, 0, 1500, 4000), (451, 2, 1, 2), (2363, 6, 1, 1), (560, 3, 30, 60), (385, 2, 3, 6),
           (1079, 12, 1, 1), (1201, 12, 1, 1), (7970, 11, 1, 1), (8016, 13, 1, 1)],  # ... Frostbound, Frostfang spear
}
for i, items in boss_drops.items():
    # drops roll in order and stop at the first hit, so the unique goes first (after the always-drops): a true 1/12;
    # the ladder weapon rolls right after it
    rare = lambda d: d[0] in (7968, 7969, 7970) or d[0] > 8000
    items = [d for d in items if d[1] == 0] + [d for d in items if rare(d)] + [d for d in items if d[1] != 0 and not rare(d)]
    drops = [x for x in drops if x['id'] != i]
    drops.append({'id': i, 'items': [{'item_id': it, 'chance': c, 'amounts': [lo, hi]} for it, c, lo, hi in items]})
# the quest's fights: no unique, but worth winning
drops = [x for x in drops if x['id'] not in (3880, 3881)]
drops.append({'id': 3880, 'items': [{'item_id': 526, 'chance': 0, 'amounts': [1, 1]}, {'item_id': 995, 'chance': 0, 'amounts': [300, 900]},
    {'item_id': 1617, 'chance': 8, 'amounts': [1, 1]}, {'item_id': 2434, 'chance': 4, 'amounts': [1, 1]}, {'item_id': 1615, 'chance': 60, 'amounts': [1, 1]}]})
drops.append({'id': 3881, 'items': [{'item_id': 592, 'chance': 0, 'amounts': [1, 1]}, {'item_id': 995, 'chance': 0, 'amounts': [2500, 5000]},
    {'item_id': 565, 'chance': 0, 'amounts': [40, 80]}, {'item_id': 8080, 'chance': 3, 'amounts': [1, 1]}, {'item_id': 8081, 'chance': 3, 'amounts': [1, 1]},
    {'item_id': 8079, 'chance': 2, 'amounts': [1, 1]},  # the Herald's Scarweave
    {'item_id': 1319, 'chance': 3, 'amounts': [1, 1]}, {'item_id': 1615, 'chance': 4, 'amounts': [1, 1]}]})
def droptable(i, items):
    global drops
    drops = [x for x in drops if x['id'] != i]
    drops.append({'id': i, 'items': [{'item_id': it, 'chance': c, 'amounts': [lo, hi]} for it, c, lo, hi in items]})
droptable(3885, [(526, 0, 1, 1), (995, 0, 400, 900), (7997, 11, 1, 1), (8022, 15, 1, 1), (555, 1, 30, 80), (1079, 20, 1, 1)])  # sentinel: Rimecleaver, Rimeheart staff, water runes, rune legs
droptable(3884, [(526, 0, 1, 1), (995, 0, 120, 400), (8072, 19, 1, 1), (8073, 19, 1, 1), (8068, 24, 1, 1), (8070, 29, 1, 1),
                 (8071, 29, 1, 1), (8069, 39, 1, 1), (1123, 25, 1, 1), (2353, 3, 2, 5)])  # Thane's oathsworn: its own plate
droptable(3886, [(532, 0, 1, 1), (995, 0, 1500, 3000), (1319, 5, 1, 1), (1201, 6, 1, 1), (565, 2, 20, 40)])  # Vaelric
# the Starless: always rich, and two uniques at a true 1/12 each (they roll first, after the always-drops)
droptable(3889, [(592, 0, 1, 1), (995, 0, 8000, 20000), (7979, 0, 2, 4), (8031, 0, 1, 2), (7983, 11, 1, 1), (7984, 11, 1, 1),
                 (8023, 23, 1, 1), (8024, 31, 1, 1), (8025, 63, 1, 1),  # the ladder's top rungs
                 (8026, 79, 1, 1), (8027, 95, 1, 1), (8028, 111, 1, 1), (8029, 127, 1, 1),
                 (8042, 39, 1, 1), (8034, 29, 1, 1),  # a voidsteel dagger, a starmetal longsword
                 # the god tier's drops, very rare: Wardensoath, Scarmaw, Dawnbringer, Crownfire, Nightfall, Sunfall
                 (8053, 149, 1, 1), (8055, 179, 1, 1), (8057, 199, 1, 1), (8060, 219, 1, 1), (8062, 249, 1, 1), (8064, 279, 1, 1),
                 (7990, 35, 1, 1), (7991, 35, 1, 1), (7992, 35, 1, 1), (7993, 35, 1, 1), (7994, 35, 1, 1), (7995, 35, 1, 1),
                 (1149, 7, 1, 1),
                 (1215, 5, 1, 1), (1187, 40, 1, 1), (1615, 9, 1, 1), (2363, 2, 3, 6), (560, 1, 50, 150), (385, 1, 5, 10)])
droptable(3890, [(592, 0, 1, 1), (8031, 3, 1, 1), (7979, 3, 1, 1)])   # spawn: sometimes a Starless shard, or starmetal
save('npcdrops.json', drops)

# the bosses' uniques: equipment bonuses, in ItemDefinitions.json's own compact format
uniques = {
    7968: (0.3, {'attackStab': 6, 'attackSlash': 6, 'attackCrush': 6, 'attackMagic': 6, 'attackRange': 6, 'defenceStab': 3,
                 'defenceSlash': 3, 'defenceCrush': 3, 'defenceMagic': 3, 'defenceRange': 3, 'strengthBonus': 12, 'prayerBonus': 2}),
    7969: (1.8, {'attackStab': 44, 'attackSlash': 54, 'attackCrush': -2, 'defenceSlash': 3, 'defenceCrush': 2, 'strengthBonus': 56}),
    7970: (0.9, {'defenceStab': 6, 'defenceSlash': 6, 'defenceCrush': 6, 'defenceMagic': 3, 'defenceRange': 6, 'strengthBonus': 2,
                 'prayerBonus': 1}),
    # Warden's kiteshield (Hold the Gate)
    7977: (5.4, {'attackMagic': -6, 'attackRange': -2, 'defenceStab': 27, 'defenceSlash': 29, 'defenceCrush': 26, 'defenceMagic': 2,
                 'defenceRange': 28, 'strengthBonus': 1, 'prayerBonus': 1}),
    # the Emberbrand (Heartforged): a quest longsword a little behind Barrowsteel, and the only blade that cuts the King
    7981: (1.6, {'attackStab': 40, 'attackSlash': 50, 'attackCrush': -2, 'attackMagic': 4, 'defenceSlash': 3, 'defenceCrush': 2,
                 'defenceMagic': 3, 'strengthBonus': 52, 'prayerBonus': 2}),
    # the Crown of Aurel (The Hollow King)
    7982: (0.5, {'attackMagic': 6, 'defenceStab': 8, 'defenceSlash': 8, 'defenceCrush': 8, 'defenceMagic': 8, 'defenceRange': 8,
                 'strengthBonus': 3, 'prayerBonus': 4}),
    # the Gatehold Warden set (Def 30): between adamant and rune, and a little prayer
    7985: (2.3, {'attackMagic': -4, 'attackRange': -1, 'defenceStab': 24, 'defenceSlash': 26, 'defenceCrush': 21, 'defenceMagic': -1,
                 'defenceRange': 24, 'prayerBonus': 1}),
    7986: (9.5, {'attackMagic': -24, 'attackRange': -8, 'defenceStab': 72, 'defenceSlash': 70, 'defenceCrush': 62, 'defenceMagic': -4,
                 'defenceRange': 70, 'prayerBonus': 1}),
    7987: (8.6, {'attackMagic': -18, 'attackRange': -6, 'defenceStab': 40, 'defenceSlash': 38, 'defenceCrush': 36, 'defenceMagic': -3,
                 'defenceRange': 38, 'prayerBonus': 1}),
    7988: (1.0, {'attackMagic': -2, 'defenceStab': 10, 'defenceSlash': 11, 'defenceCrush': 12, 'strengthBonus': 1}),
    7989: (0.5, {'attackStab': 4, 'attackSlash': 4, 'attackCrush': 4, 'attackMagic': 2, 'attackRange': 4, 'defenceStab': 5,
                 'defenceSlash': 5, 'defenceCrush': 5, 'defenceMagic': 3, 'defenceRange': 5, 'strengthBonus': 3}),
    # Aurel's Royal Guard (Def 50): a step past rune, with prayer and a touch of strength
    7990: (2.7, {'attackMagic': -2, 'attackRange': -1, 'defenceStab': 33, 'defenceSlash': 35, 'defenceCrush': 30, 'defenceRange': 33,
                 'strengthBonus': 1, 'prayerBonus': 2}),
    7991: (9.9, {'attackMagic': -26, 'attackRange': -8, 'defenceStab': 88, 'defenceSlash': 86, 'defenceCrush': 78, 'defenceMagic': -4,
                 'defenceRange': 86, 'strengthBonus': 2, 'prayerBonus': 2}),
    7992: (9.0, {'attackMagic': -19, 'attackRange': -6, 'defenceStab': 55, 'defenceSlash': 53, 'defenceCrush': 51, 'defenceMagic': -2,
                 'defenceRange': 53, 'strengthBonus': 1, 'prayerBonus': 2}),
    7993: (5.4, {'attackMagic': -7, 'attackRange': -2, 'defenceStab': 48, 'defenceSlash': 52, 'defenceCrush': 50, 'defenceMagic': 2,
                 'defenceRange': 50, 'strengthBonus': 1, 'prayerBonus': 2}),
    7994: (1.2, {'attackMagic': -2, 'defenceStab': 14, 'defenceSlash': 15, 'defenceCrush': 16, 'defenceMagic': 1, 'defenceRange': 1,
                 'strengthBonus': 2, 'prayerBonus': 1}),
    7995: (0.6, {'attackStab': 7, 'attackSlash': 7, 'attackCrush': 7, 'attackMagic': 3, 'attackRange': 7, 'defenceStab': 7,
                 'defenceSlash': 7, 'defenceCrush': 7, 'defenceMagic': 4, 'defenceRange': 7, 'strengthBonus': 6, 'prayerBonus': 1}),
    # the saga's weapons
    7996: (3.0, {'attackStab': 62, 'attackSlash': 86, 'attackMagic': -4, 'defenceStab': -1, 'defenceSlash': 4, 'defenceRange': 4,
                 'strengthBonus': 82}),                                              # Starfall halberd (Att 60, two-handed)
    7997: (2.7, {'attackStab': -2, 'attackSlash': 60, 'attackCrush': 55, 'defenceSlash': 2, 'defenceCrush': 2, 'defenceMagic': 3,
                 'strengthBonus': 74}),                                              # Rimecleaver battleaxe (Att 55)
    7998: (1.8, {'attackStab': 38, 'attackSlash': -2, 'attackCrush': 58, 'attackMagic': 8, 'defenceSlash': 2, 'defenceCrush': 2,
                 'defenceMagic': 4, 'strengthBonus': 52, 'prayerBonus': 7}),         # Hollow King's mace (Att 60)
    7999: (1.0, {'attackRange': 72}),                                                # Duskyew shortbow (Ranged 50)
    8000: (2.0, {'attackStab': 10, 'attackSlash': -1, 'attackCrush': 40, 'attackMagic': 16, 'defenceStab': 2, 'defenceSlash': 3,
                 'defenceCrush': 1, 'defenceMagic': 16, 'strengthBonus': 45, 'prayerBonus': 2}),  # Staff of the caged star (Magic 60)
    # the Starless's uniques
    7983: (0.4, {'attackStab': 4, 'attackSlash': 4, 'attackCrush': 4, 'attackMagic': 4, 'attackRange': 4, 'defenceStab': 11,
                 'defenceSlash': 11, 'defenceCrush': 11, 'defenceMagic': 11, 'defenceRange': 11, 'strengthBonus': 6, 'prayerBonus': 3}),
    7984: (0.1, {'attackStab': 12, 'attackSlash': 12, 'attackCrush': 12, 'attackMagic': 12, 'attackRange': 12, 'defenceStab': 8,
                 'defenceSlash': 8, 'defenceCrush': 8, 'defenceMagic': 8, 'defenceRange': 8, 'strengthBonus': 8, 'prayerBonus': 4}),
}
# the weapon ladder (VoidArsenal / LadderWeapons): 25 weapons, each a step past the last. Rank, then weight and bonuses.
S, SL, C, M, R, DS, DSL, DC, DM, DR, ST, P = ('attackStab', 'attackSlash', 'attackCrush', 'attackMagic', 'attackRange', 'defenceStab',
                                              'defenceSlash', 'defenceCrush', 'defenceMagic', 'defenceRange', 'strengthBonus', 'prayerBonus')
ladder = [
    (0.4, {S: 5, SL: 3, C: -4, ST: 4}),                                            # 1  Netwright's gutting dagger (Att 1)
    (1.6, {S: -2, SL: 7, C: 5, ST: 8}),                                            # 2  Hob's felling axe (Att 3)
    (2.0, {S: 3, SL: -1, C: 9, M: 6, DM: 5, ST: 6}),                               # 3  Pilgrim's staff (Magic 5)
    (2.2, {S: 12, SL: 12, C: 12, DS: 1, DSL: 1, DC: 1, ST: 11}),                   # 4  Militia spear (Att 5)
    (0.9, {R: 12}),                                                                # 5  Wolfbone shortbow (Ranged 5)
    (1.4, {S: 16, SL: 11, C: -2, DSL: 1, ST: 14}),                                 # 6  Watchman's sword (Att 10)
    (1.8, {S: 10, SL: -2, C: 19, DSL: 1, ST: 17, P: 2}),                           # 7  Steelcap mace (Att 15)
    (1.4, {S: 6, SL: 25, C: -2, ST: 24}),                                          # 8  Smuggler's scimitar (Att 20)
    (2.7, {S: -4, SL: -4, C: 32, ST: 33, P: 1}),                                   # 9  Barrowstone warhammer (Att 25)
    (0.3, {M: 12, DM: 6}),                                                         # 10 Silkwood wand (Magic 25)
    (1.8, {S: 30, SL: 41, C: -2, DSL: 2, DC: 1, ST: 42}),                          # 11 Gatehold longsword (Att 30)
    (3.0, {S: 31, SL: 50, M: -4, DS: -1, DSL: 4, DR: 4, ST: 50}),                  # 12 Warden's halberd (Att 30, Str 30)
    (1.3, {R: 38}),                                                                # 13 Warden's longbow (Ranged 30)
    (0.4, {S: 31, SL: 18, C: -4, M: 2, ST: 29}),                                   # 14 Scarglass dagger (Att 35)
    (2.9, {S: -2, SL: 52, C: 45, DS: 2, DSL: 2, DC: 2, ST: 62, P: 2}),             # 15 Oathsworn battleaxe (Att 40)
    (2.6, {S: 41, SL: 41, C: 41, DS: 5, DSL: 5, DC: 5, DM: 2, ST: 45}),            # 16 Frostfang spear (Att 40)
    (3.6, {S: -4, SL: 74, C: 60, DSL: 2, DC: 2, ST: 76}),                          # 17 Gravewarden 2h sword (Att 45)
    (1.0, {R: 60}),                                                                # 18 Hollowheart shortbow (Ranged 45)
    (0.3, {M: 15, DM: 10, P: 2}),                                                  # 19 Wand of the Scar (Magic 50)
    (3.0, {S: -4, SL: -4, C: 64, DS: 2, DSL: 2, DC: 2, ST: 68, P: 2}),             # 20 Gravestar warhammer (Att 55)
    (3.0, {S: 66, SL: 90, M: -4, DSL: 4, DR: 4, ST: 88}),                          # 21 Riftglass halberd (Att 60, Str 40)
    (2.2, {S: 12, SL: -1, C: 44, M: 18, DS: 2, DSL: 3, DC: 1, DM: 18, ST: 42, P: 2}),  # 22 Rimeheart staff (Magic 65)
    (1.6, {S: 12, SL: 72, C: -2, M: 2, ST: 72}),                                   # 23 Nightglass scimitar (Att 70)
    (2.2, {S: 46, SL: -2, C: 84, M: 6, DSL: 2, DC: 2, DM: 4, ST: 78, P: 6}),       # 24 Crownbreaker mace (Att 75)
    (1.8, {S: 70, SL: 90, C: -2, M: 6, DS: 4, DSL: 4, DC: 4, DM: 6, DR: 4, ST: 92, P: 4}),  # 25 Starless longsword (Att 80)
    (1.4, {R: 92, DM: 4, DR: 4}),                                                  # 26 Eventide longbow (Ranged 85)
    (2.6, {S: 86, SL: 86, C: 86, DS: 6, DSL: 6, DC: 6, DM: 4, ST: 94, P: 2}),      # 27 Sunspear (Att 85)
    (2.2, {S: 14, SL: -1, C: 50, M: 26, DS: 3, DSL: 4, DC: 2, DM: 26, ST: 48, P: 4}),  # 28 Staff of the first star (Magic 90)
    (3.2, {S: -2, SL: 104, C: 96, DSL: 3, DC: 3, ST: 118, P: 3}),                 # 29 Kingsbane battleaxe (Att 90, Str 80)
    (3.6, {S: -4, SL: 124, C: 108, DS: 4, DSL: 6, DC: 4, DM: 6, ST: 128, P: 6}),  # 30 Last Light 2h sword (Att 99, Str 90)
]
for rank, (w, b) in enumerate(ladder):
    uniques[8001 + rank] = (w, b)
# the two smithed tiers (WeaponSets), in set order: dagger, sword, longsword, scimitar, mace, warhammer, battleaxe,
# 2h sword, spear, halberd (and voidsteel's longbow). Starmetal sits between rune and dragon, voidsteel above dragon;
# within a set the dagger is weakest and quickest, the 2h sword and halberd strongest and slowest, as rune's are.
starmetal = [
    (0.4, {S: 33, SL: 18, C: -4, M: 1, ST: 32}),
    (1.6, {S: 46, SL: 34, C: -2, DSL: 2, DC: 1, ST: 47}),
    (1.8, {S: 48, SL: 58, C: -2, DSL: 3, DC: 2, ST: 60}),
    (1.6, {S: 8, SL: 56, C: -2, DSL: 1, ST: 55}),
    (1.8, {S: 30, SL: -2, C: 50, DSL: 1, ST: 46, P: 4}),
    (2.7, {S: -4, SL: -4, C: 64, ST: 60}),
    (2.9, {S: -2, SL: 59, C: 54, DSL: 1, ST: 74}),
    (3.6, {S: -4, SL: 80, C: 65, DSL: 2, DC: 1, ST: 82}),
    (2.2, {S: 46, SL: 46, C: 46, DS: 5, DSL: 5, DC: 5, ST: 51}),
    (3.0, {S: 58, SL: 80, M: -4, DS: -1, DSL: 4, DR: 5, ST: 78}),
]
voidsteel = [
    (0.4, {S: 48, SL: 32, C: -4, M: 2, ST: 47}),
    (1.6, {S: 62, SL: 46, C: -2, DSL: 2, DC: 1, ST: 63}),
    (1.8, {S: 66, SL: 78, C: -2, DSL: 3, DC: 2, ST: 80}),
    (1.6, {S: 10, SL: 76, C: -2, DSL: 1, ST: 75}),
    (1.8, {S: 48, SL: -2, C: 70, DSL: 1, ST: 64, P: 6}),
    (2.7, {S: -4, SL: -4, C: 82, ST: 80}),
    (2.9, {S: -2, SL: 80, C: 74, DSL: 1, ST: 95}),
    (3.6, {S: -4, SL: 102, C: 90, DSL: 2, DC: 1, ST: 104}),
    (2.2, {S: 62, SL: 62, C: 62, DS: 6, DSL: 6, DC: 6, ST: 68}),
    (3.0, {S: 80, SL: 106, M: -4, DS: -1, DSL: 4, DR: 5, ST: 99}),
    (1.4, {R: 82}),
]
for i, wb in enumerate(starmetal):
    uniques[8032 + i] = wb
for i, wb in enumerate(voidsteel):
    uniques[8042 + i] = wb
# the god tier (GodWeapons, ladder ranks 31-45): a clear step past everything else, kept where the max-hit formulas
# stay sane (strength bonus tops out at 155, ranged at 112, magic at 40)
god = [
    (3.4, {S: -4, SL: 130, C: 112, DS: 6, DSL: 8, DC: 6, DM: 4, DR: 6, ST: 132, P: 4}),       # 31 Wardensoath 2h sword
    (1.8, {S: 82, SL: 104, C: -2, M: 4, DS: 4, DSL: 4, DC: 4, DM: 6, ST: 108, P: 4}),         # 32 Heart of Aurel longsword
    (3.0, {S: 96, SL: 124, M: -4, DSL: 4, DR: 5, ST: 120, P: 2}),                            # 33 Scarmaw halberd
    (3.0, {S: -4, SL: -4, C: 112, DS: 3, DSL: 3, DC: 3, ST: 116, P: 4}),                     # 34 Hollow Crown warhammer
    (1.4, {R: 104, DM: 6, DR: 6}),                                                           # 35 Dawnbringer longbow
    (0.3, {M: 32, DM: 18, P: 4}),                                                            # 36 Wand of the Unmade
    (2.6, {S: 100, SL: 100, C: 100, DS: 8, DSL: 8, DC: 8, DM: 4, ST: 110, P: 3}),            # 37 Spear of the First Star
    (1.6, {S: 14, SL: 112, C: -2, M: 2, ST: 112, P: 3}),                                     # 38 Crownfire scimitar
    (3.2, {S: -2, SL: 122, C: 112, DSL: 3, DC: 3, ST: 136, P: 3}),                           # 39 Gatebreaker battleaxe
    (1.0, {R: 112, DM: 4, DR: 4}),                                                           # 40 Nightfall shortbow
    (2.2, {S: 16, SL: -1, C: 56, M: 34, DS: 3, DSL: 4, DC: 2, DM: 30, ST: 52, P: 4}),        # 41 Riftcaller staff
    (3.6, {S: -4, SL: 140, C: 122, DS: 4, DSL: 6, DC: 4, DM: 6, ST: 144, P: 6}),             # 42 Sunfall 2h sword
    (3.0, {S: -4, SL: -4, C: 128, DS: 4, DSL: 4, DC: 4, DM: 4, ST: 134, P: 6}),              # 43 Black Sun warhammer
    (2.2, {S: 18, SL: -1, C: 60, M: 40, DS: 4, DSL: 5, DC: 3, DM: 36, ST: 56, P: 6}),        # 44 Crown of Stars staff
    (3.6, {S: 20, SL: 150, C: 132, DS: 10, DSL: 10, DC: 10, DM: 10, DR: 10, ST: 155, P: 8}),  # 45 Godsfall 2h sword
]
for i, wb in enumerate(god):
    uniques[8053 + i] = wb
# the six armour sets (ArmourSets / VoidArmoury), slot order helm, body, legs, shield, boots, gloves (None = no piece).
# Plate follows rune (Defence 40) -> dragon (60) and on; the hide and robes follow d'hide and mystic.
A, DEF = (lambda st, sl, cr, mg, rg: {DS: st, DSL: sl, DC: cr, DM: mg, DR: rg}), None
def plate(d, mpen, extra=None):
    out = {k: v for k, v in d.items() if v}
    if mpen:
        out[M] = -mpen
        out[R] = -(mpen // 3)
    out.update(extra or {})
    return out
armour = {
    8068: (2.7, plate(A(32, 34, 29, -1, 31), 6, {P: 1})),                         # Oathsworn full helm
    8069: (9.5, plate(A(86, 84, 76, -6, 84), 30, {P: 2})),                        # Oathsworn platebody
    8070: (9.0, plate(A(54, 52, 50, -4, 52), 21, {P: 1})),                        # Oathsworn platelegs
    8071: (5.4, plate(A(50, 52, 46, -1, 50), 8)),                                  # Oathsworn kiteshield
    8072: (1.0, plate(A(13, 14, 15, 0, 0), 3)),                                    # Oathsworn boots
    8073: (0.5, plate(A(9, 9, 9, 2, 6), 0, {S: 4, SL: 4, C: 4, ST: 2})),            # Oathsworn gauntlets
    8074: (0.9, {R: 7, DS: 5, DSL: 7, DC: 9, DM: 5, DR: 6}),                        # Duskstalker coif
    8075: (6.0, {M: -15, R: 28, DS: 46, DSL: 40, DC: 52, DM: 50, DR: 48}),          # Duskstalker body
    8076: (5.0, {M: -10, R: 16, DS: 26, DSL: 22, DC: 29, DM: 27, DR: 25}),          # Duskstalker chaps
    8077: (0.5, {R: 4, DS: 5, DSL: 5, DC: 6, DM: 4, DR: 5}),                         # Duskstalker boots
    8078: (0.3, {M: -10, R: 11, DS: 3, DSL: 3, DC: 4, DM: 3, DR: 4}),               # Duskstalker vambraces
    8079: (0.4, {M: 6, DM: 6}),                                                    # Scarweave hat
    8080: (1.8, {M: 25, DS: 4, DSL: 4, DC: 4, DM: 25}),                             # Scarweave robe top
    8081: (1.4, {M: 19, DS: 3, DSL: 3, DC: 3, DM: 19}),                             # Scarweave robe bottom
    8082: (0.3, {M: 6, DM: 6}),                                                    # Scarweave boots
    8083: (0.3, {M: 6, DM: 6}),                                                    # Scarweave gloves
    8084: (2.5, plate(A(40, 42, 37, 0, 40), 6, {P: 1})),                           # Starmetal full helm
    8085: (9.0, plate(A(100, 96, 90, -4, 98), 30, {P: 2})),                        # Starmetal platebody
    8086: (8.5, plate(A(66, 64, 60, -2, 64), 21, {P: 1})),                         # Starmetal platelegs
    8087: (5.0, plate(A(62, 64, 58, 0, 62), 8, {P: 1})),                           # Starmetal kiteshield
    8088: (1.0, plate(A(16, 17, 18, 0, 0), 3)),                                    # Starmetal boots
    8089: (0.5, plate(A(12, 12, 12, 3, 8), 0, {S: 6, SL: 6, C: 6, ST: 4})),         # Starmetal gauntlets
    8090: (2.5, plate(A(50, 52, 46, 2, 50), 6, {ST: 2, P: 2})),                    # Voidsteel full helm
    8091: (9.0, plate(A(120, 116, 110, 4, 118), 30, {ST: 4, P: 3})),              # Voidsteel platebody
    8092: (8.5, plate(A(80, 78, 74, 2, 78), 21, {ST: 2, P: 2})),                   # Voidsteel platelegs
    8093: (5.0, plate(A(76, 78, 72, 4, 76), 8, {P: 2})),                           # Voidsteel kiteshield
    8094: (1.0, plate(A(20, 21, 22, 1, 2), 3, {ST: 2})),                           # Voidsteel boots
    8095: (0.5, plate(A(15, 15, 15, 4, 10), 0, {S: 9, SL: 9, C: 9, ST: 7})),        # Voidsteel gauntlets
    8096: (2.5, plate(A(60, 62, 56, 6, 60), 4, {ST: 4, P: 4})),                    # Heartfire full helm
    8097: (9.0, plate(A(140, 136, 130, 10, 138), 20, {ST: 6, P: 6})),             # Heartfire platebody
    8098: (8.5, plate(A(94, 92, 88, 6, 92), 14, {ST: 4, P: 3})),                   # Heartfire platelegs
    8099: (5.0, plate(A(90, 92, 86, 8, 90), 4, {P: 4})),                           # Heartfire kiteshield
    8100: (1.0, plate(A(24, 25, 26, 2, 4), 2, {ST: 3, P: 1})),                     # Heartfire boots
    8101: (0.5, plate(A(18, 18, 18, 6, 12), 0, {S: 12, SL: 12, C: 12, ST: 10, P: 2})),  # Heartfire gauntlets
}
uniques.update(armour)
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
    {'itemId': 333, 'itemAmount': 10}, {'itemId': 329, 'itemAmount': 10}, {'itemId': 351, 'itemAmount': 5},
    {'itemId': 8001, 'itemAmount': 5}]})
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
                                (1351, 5), (1265, 5), (233, 5), (36, 10), (1785, 3), (5341, 5), (8002, 3), (8003, 3), (8005, 3)])
shop(353, "Dorrin's Forge", [(2347, 10), (2349, 20), (2351, 20), (2353, 10), (2359, 5), (1117, 3), (1121, 3), (1073, 3),
                             (1109, 2), (1123, 2), (1159, 2), (1197, 2), (1287, 3), (1301, 2), (453, 50), (8004, 3), (8006, 3),
                             (8007, 2)])
shop(354, "Quarrywright's Picks", [(1265, 10), (1267, 8), (1269, 6), (1273, 4), (1271, 2), (1755, 5), (1823, 5)])
shop(355, 'The Last Hearth', [(2003, 20), (2289, 10), (1993, 15), (1917, 20), (1891, 10), (2327, 15), (1985, 10),
                              (2011, 10), (379, 20), (373, 15), (1971, 30)])
shop(356, "Nettle's Hold", [(954, 10), (301, 10), (311, 5), (1623, 5), (1621, 3), (1619, 2), (1617, 1),
                            (1201, 1), (1347, 1), (562, 100), (561, 50), (2489, 2), (1033, 2), (8008, 1),
                            (8074, 2), (8075, 1), (8076, 1), (8077, 2), (8078, 2)])  # ... and the Duskstalker hide
# the Warden's armoury: Dorrin opens it once you've held the gate (the gauntlets only come off the Hollowed)
shop(357, "The Warden's Armoury", [(7985, 3), (7986, 2), (7987, 2), (7988, 3), (7977, 1), (1163, 2), (1079, 1), (1201, 1),
                                   (4131, 2), (2363, 4), (8011, 2), (8012, 1), (8013, 2)])
save('shops.json', shops)

# spawns: drop everything previously in Nythera's regions except Oren, then add camps + generated wildlife
X0, Y0, N = 20 * 64, 39 * 64, 7 * 64
DUNGEONS = (20, 22, 24, 26)  # the Sunken Barrow, Hollowdeep, Frostfang (region y 151)
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
         3876: (9, 70), 3877: (12, 95), 3878: (14, 115), 3882: (5, 40)}
count = 0
for line in open(spawnfile):
    i, x, y, w = map(int, line.split())
    mh, at = STATS.get(i, (0, 0))
    spawn(i, x, y, w, mh, at, at)
    count += 1
save('spawns.json', sp)
print('wildlife spawns', count, 'total spawns', len(sp))
