import java.awt.Point;
import java.util.Locale;


public final class S_RuneRockDefener extends Script {
    
    private static final StackedItem[] to_withdraw = {
        new StackedItem(619, 100), // blood runes
        new StackedItem(33, 500), // air rune
        new StackedItem(31, 700), // fire rune
        new StackedItem(546, 27), // 25 sharks
    };
    
    // -1 for no spell
    private static final int spell_id = 45; // fire wave
    private static final int eat_at_percent = 50;
    
    // in order of preference
    private static final String[] target_names = {
        "honraystaker",
    };
    
    private static final int combat_style = 0; // controlled
    
    private static final int[] prayer_ids = {
        0
    };
    
    private static final class StackedItem {
        final int id;
        final int count;
        
        StackedItem(int id, int count) {
            this.id = id;
            this.count = count;
        }
    }
    
    private long menu_time;
    private long bank_time;
    private long move_time;
    private long last_combat;

    private PathWalker pw;
    private PathWalker.Path lumb_to_dray;
    private PathWalker.Path dray_to_edge;
    private PathWalker.Path edge_to_rocks;

    private boolean idle_move_dir;
    
    // ripped from pathwalker
    private static final Point DRAYNOR = new Point(220, 635);
    private static final Point LUMBRIDGE = new Point(128, 640);
    private static final Point EDGEVILLE = new Point(215, 450);
    // ripped from abytes script
    private static final Point ROCKS = new Point(257, 157);
    
    private static final int CHARGED_AMMY = 597;
    
    private static final int HITS = 3;
    private static final int PRAYER = 5;
        
    public S_RuneRockDefener(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_RuneRockDefener(null).init(null);
        System.exit(0);
    }
    
    @Override
    public void init(String params) {
        move_time = -1L;
        menu_time = -1L;
        bank_time = -1L;
        last_combat = 0L;
        pw.init(null);
        lumb_to_dray = pw.calcPath(LUMBRIDGE.x, LUMBRIDGE.y, DRAYNOR.x, DRAYNOR.y);
        dray_to_edge = pw.calcPath(DRAYNOR.x, DRAYNOR.y, EDGEVILLE.x, EDGEVILLE.y);
        edge_to_rocks = pw.calcPath(EDGEVILLE.x, EDGEVILLE.y, ROCKS.x, ROCKS.y);
    }
    
    @Override
    public int main() {
        if (getFightMode() != combat_style) {
            setFightMode(combat_style);
            return random(300, 400);
        }
        if (inCombat()) {
            last_combat = System.currentTimeMillis();
            if (getHpPercent() <= eat_at_percent) {
                walkTo(getX(), getY());
                return random(300, 600);
            }
            if (getCurrentLevel(PRAYER) > 0) {
                for (int id : prayer_ids) {
                    if (!isPrayerEnabled(id)) {
                        enablePrayer(id);
                        return random(300, 600);
                    }
                }
            }
            if (spell_id != -1 && canCastSpell(spell_id)) {
                for (String str : target_names) {
                    int[] player = getPlayerByName(str);
                    if (player[0] != -1) {
                        magePlayer(player[0], spell_id);
                        return random(400, 600);
                    }
                }
            }
            return 0;
        }
        if (getHpPercent() <= eat_at_percent) {
            int count = getInventoryCount();
            for (int i = 0; i < count; ++i) {
                if (getItemCommand(i).toLowerCase(Locale.ENGLISH).contains("eat")) {
                    useItem(i);
                    return random(600, 800);
                }
            }
            System.out.println("out of food!");
        }
        if (System.currentTimeMillis() >= move_time) {
            System.out.println("Moving for 5 min timer");
            if (idle_move_dir) {
                if (!_idleMoveP1()) {
                    _idleMoveM1();
                }
            } else {
                if (!_idleMoveM1()) {
                    _idleMoveP1();
                }
            }
            idle_move_dir = !idle_move_dir;
            move_time = -1L;
            return random(600, 800);
        }
        if (isQuestMenu()) {
            menu_time = -1L;
            String[] array = questMenuOptions();
            if (array.length > 0) {
                String first = array[0].toLowerCase(Locale.ENGLISH);
                if (first.contains("access")) {
                    answer(0);
                    bank_time = System.currentTimeMillis();
                } else if (first.contains("edgeville")) {
                    answer(0);
                    return random(2000, 3000);
                }
            }
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            for (StackedItem item : to_withdraw) {
                if (getInventoryCount(item.id) >= item.count) {
                    continue;
                }
                if (bankCount(item.id) < item.count) {
                    System.out.println("Out of item");
                    stopScript(); setAutoLogin(false); return 0;
                }
                withdraw(item.id, item.count);
                return random(2500, 3000);
            }
            closeBank();
            if (getInventoryIndex(CHARGED_AMMY) == -1) {
                pw.setPath(dray_to_edge);
            }
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (pw.walkPath()) return 0;
        if (isAtApproxCoords(LUMBRIDGE.x, LUMBRIDGE.y, 20)) {
            int ammy = getInventoryIndex(CHARGED_AMMY);
            if (ammy != -1) {
                useItem(ammy);
                menu_time = System.currentTimeMillis();
                return random(600, 800);
            }
            pw.setPath(lumb_to_dray);
            return random(600, 800);
        } else if (isAtApproxCoords(DRAYNOR.x, DRAYNOR.y, 20)) {
            for (int id : prayer_ids) {
                if (isPrayerEnabled(id)) {
                    disablePrayer(id);
                    return random(300, 600);
                }
            }
            int ammy = getInventoryIndex(CHARGED_AMMY);
            if (ammy != -1) {
                useItem(ammy);
                menu_time = System.currentTimeMillis();
            } else {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
            }
            return random(600, 800);
        } else if (isAtApproxCoords(EDGEVILLE.x, EDGEVILLE.y, 20)) {
            pw.setPath(edge_to_rocks);
            return random(600, 800);
        } else if (isAtApproxCoords(ROCKS.x, ROCKS.y, 20)) {
            if (target_names.length == 1 && target_names[0].charAt(0) == 'h' && target_names[0].charAt(1) == 'o' && target_names[0].charAt(2) == 'n') {
                for (String str : target_names) {
                    int[] player = getPlayerByName(str);
                    if (player[0] != -1) {
                        if (spell_id != -1 && (System.currentTimeMillis() - last_combat) >= 5000L && canCastSpell(spell_id)) {
                            magePlayer(player[0], spell_id);
                        } else if (distanceTo(player[1], player[2]) <= 2 && !isPlayerHpBarVisible(player[0])) {
                            attackPlayer(player[0]);
                        } else {
                            int x = player[1];
                            int y = player[2];
                            switch (getPlayerDirection(player[0])) {
                                case DIR_NORTH:
                                    --y;
                                    break;
                                case DIR_NORTHWEST:
                                    --y; ++x;
                                    break;
                                case DIR_NORTHEAST:
                                    --y; --x;
                                    break;
                                case DIR_SOUTHWEST:
                                    ++y; ++x;
                                    break;
                                case DIR_SOUTH:
                                    ++y;
                                    break;
                                case DIR_SOUTHEAST:
                                    ++y; --x;
                                    break;
                                case DIR_WEST:
                                    ++x;
                                    break;
                                case DIR_EAST:
                                    --x;
                                    break;
                            }
                            if (!isReachable(x, y)) {
                                x = player[1];
                                y = player[2];
                            }
                            walkTo(x, y);
                        }
                        return random(400, 600);
                    }
                }
                if (getCurrentLevel(HITS) < getLevel(HITS)) {
                    int count = getInventoryCount();
                    for (int i = 0; i < count; ++i) {
                        if (getItemCommand(i).toLowerCase(Locale.ENGLISH).contains("eat")) {
                            useItem(i);
                            return random(600, 800);
                        }
                    }
                    System.out.println("out of food!");
                }
                for (int id : prayer_ids) {
                    if (isPrayerEnabled(id)) {
                        disablePrayer(id);
                        return random(300, 600);
                    }
                }
                if (getX() != ROCKS.x && getY() != ROCKS.y) {
                    if (!isWalking()) {
                        walkTo(ROCKS.x, ROCKS.y);
                    }
                    return random(1000, 2000);
                }
            }
            return 0;
        } else {
            PathWalker.Path path = pw.calcPath(ROCKS.x, ROCKS.y);
            if (path == null) {
                System.out.println("pathing error... let's try to move a bit");
                move_time = System.currentTimeMillis();
            } else {
                pw.setPath(path);
            }
            return random(600, 800);
        }
    }
    
    private boolean _idleMoveP1() {
        int x = getX();
        int y = getY();
        if (isReachable(x + 1, y)) {
            walkTo(x + 1, y);
            return true;
        }
        if (isReachable(x, y + 1)) {
            walkTo(x, y + 1);
            return true;
        }
        if (isReachable(x + 1, y + 1)) {
            walkTo(x + 1, y + 1);
            return true;
        }
        return false;
    }
    
    private boolean _idleMoveM1() {
        int x = getX();
        int y = getY();
        if (isReachable(x - 1, y)) {
            walkTo(x - 1, y);
            return true;
        }
        if (isReachable(x, y - 1)) {
            walkTo(x, y - 1);
            return true;
        }
        if (isReachable(x - 1, y - 1)) {
            walkTo(x - 1, y - 1);
            return true;
        }
        return false;
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("success")) {
            last_combat = System.currentTimeMillis();
        } else if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("standing")) {
            move_time = System.currentTimeMillis() + random(600, 1300);
        }
    }

}
