import java.util.Locale;

public final class S_DragonAxeBuyer extends Script {
    
    // Requires 74 to be present in PathWalker.bounds_1
    // (or it can't open the guild door)
    
    private static final int
    NPC = 269,
    AXE = 594,
    GUILD_X = 372,
    GUILD_Y = 440,
    STAIRS_DOWN_E = 359,
    STAIRS_DOWN_E_X = 385,
    STAIRS_DOWN_E_Y = 465,
    BANK_X = 439,
    BANK_Y = 496,
    STAIRS_DOWN_W = 359,
    STAIRS_DOWN_W_X = 426,
    STAIRS_DOWN_W_Y = 457,
    DOORS_CLOSED = 64,
    UG_STAIRS_BANK = 43,
    UG_STAIRS_BANK_X = 426,
    UG_STAIRS_BANK_Y = 3293,
    UG_STAIRS_GUILD = 43,
    UG_STAIRS_GUILD_X = 387,
    UG_STAIRS_GUILD_Y = 3299;
    
    private final PathWalker pw;
    private PathWalker.Path guild_to_stairs;
    private PathWalker.Path stairs_to_guild;
    private PathWalker.Path bank_to_stairs;
    private PathWalker.Path stairs_to_bank;
    private PathWalker.Path underground_to_bank;
    private PathWalker.Path underground_to_guild;

    private long menu_time;
    private long bank_time;
    private long shop_time;
    private long move_time;
    private long start_time;

    private boolean banked;
    private int banked_count;

    public S_DragonAxeBuyer(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            pw.init(null);
            guild_to_stairs = pw.calcPath(
                    GUILD_X, GUILD_Y, STAIRS_DOWN_E_X, STAIRS_DOWN_E_Y);
            
            stairs_to_guild = pw.calcPath(
                    STAIRS_DOWN_E_X, STAIRS_DOWN_E_Y, GUILD_X, GUILD_Y);
            
            bank_to_stairs = pw.calcPath(
                    BANK_X, BANK_Y, STAIRS_DOWN_W_X, STAIRS_DOWN_W_Y);
            
            stairs_to_bank = pw.calcPath(
                    STAIRS_DOWN_W_X, STAIRS_DOWN_W_Y, BANK_X, BANK_Y);
            
            underground_to_bank = pw.calcPath(
                    UG_STAIRS_GUILD_X, UG_STAIRS_GUILD_Y,
                    UG_STAIRS_BANK_X, UG_STAIRS_BANK_Y);
            
            underground_to_guild = pw.calcPath(
                    UG_STAIRS_BANK_X, UG_STAIRS_BANK_Y,
                    UG_STAIRS_GUILD_X, UG_STAIRS_GUILD_Y);
            
            menu_time = bank_time = shop_time = move_time = -1L;
            
            banked = false;
            banked_count = 0;
        }
        
        if (isQuestMenu()) {
            menu_time = -1L;
            answer(0);
            if (inside_guild()) {
                shop_time = System.currentTimeMillis();
            } else {
                bank_time = System.currentTimeMillis();
            }
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isShopOpen()) {
            shop_time = -1L;
            if (move_time == -1L && getInventoryCount() < MAX_INV_SIZE) {
                if (countPlayers() <= 1) {
                    int index = getShopItemById(AXE);
                    int amount = getShopItemAmount(index);
                    int empty = getEmptySlots();
                    if (amount > empty) {
                        amount = empty;
                    }
                    if (index != -1 && amount > 0) {
                        buyShopItem(index, amount);
                    }
                }
                return random(1500, 2500);
            }
            closeShop();
            return random(600, 800);
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(300, 400);
        }

        if (isBanking()) {
            bank_time = -1L;
            int count = getInventoryCount(AXE);
            if (count > 0) {
                if (!banked) {
                    banked = true;
                    banked_count += count;
                }
                deposit(AXE, count);
                return random(600, 800);
            }
            banked = false;
            closeBank();
            pw.setPath(bank_to_stairs);
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (pw.walkPath()) return 0;
        
        if (move_time != -1L) {
            if (isWalking()) {
                move_time = -1L;
                return 0;
            }
            if (!inside_guild()) {
                System.out.println("Something is seriously wrong...");
                setAutoLogin(false); stopScript();
                return 0;
            }
            walk_approx(getX(), getY(), 4);
            return random(1000, 1500);
        }
        
        if (inside_guild()) {
            if (getInventoryCount() < MAX_INV_SIZE) {
                if (countPlayers() <= 1) {
                    int[] npc = getNpcByIdNotTalk(NPC);
                    if (npc[0] == -1) {
                        return random(600, 800);
                    }
                    if (inner_obstacles(npc[1])) {
                        return random(1000, 2000);
                    }
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            } else {
                if (getX() != GUILD_X || getY() != GUILD_Y) {
                    if (inner_obstacles(GUILD_X)) {
                        return random(1000, 2000);
                    }
                    if (!isWalking()) {
                        walkTo(GUILD_X, GUILD_Y);
                    }
                    return random(600, 800);
                }
                pw.setPath(guild_to_stairs);
                return 0;
            }
        }
        
        if (is_underground()) {
            if (isAtApproxCoords(UG_STAIRS_BANK_X, UG_STAIRS_BANK_Y, 5)) {
                if (getInventoryCount() < MAX_INV_SIZE) {
                    pw.setPath(underground_to_guild);
                    return 0;
                } else {
                    int[] object = getObjectById(UG_STAIRS_BANK);
                    if (object_valid(object)) {
                        atObject(object[1], object[2]);
                    }
                    return random(1000, 2000);
                }
            }
            if (isAtApproxCoords(UG_STAIRS_GUILD_X, UG_STAIRS_GUILD_Y, 5)) {
                if (getInventoryCount() < MAX_INV_SIZE) {
                    int[] object = getObjectById(UG_STAIRS_GUILD);
                    if (object_valid(object)) {
                        atObject(object[1], object[2]);
                    }
                    return random(1000, 2000);
                } else {
                    pw.setPath(underground_to_bank);
                    return 0;
                }
            }
            return random(600, 800);
        }
        
        if (isAtApproxCoords(STAIRS_DOWN_E_X, STAIRS_DOWN_E_Y, 5)) {
            int[] doors = getObjectById(DOORS_CLOSED);
            if (object_valid(doors)) {
                atObject(doors[1], doors[2]);
                return random(1000, 2000);
            }
            if (getInventoryCount() < MAX_INV_SIZE) {
                pw.setPath(stairs_to_guild);
                return 0;
            } else {
                int[] object = getObjectById(STAIRS_DOWN_E);
                if (object_valid(object)) {
                    atObject(object[1], object[2]);
                }
                return random(1000, 2000);
            }
        }
        
        if (isAtApproxCoords(STAIRS_DOWN_W_X, STAIRS_DOWN_W_Y, 5)) {
            int[] doors = getObjectById(DOORS_CLOSED);
            if (object_valid(doors)) {
                atObject(doors[1], doors[2]);
                return random(1000, 2000);
            }
            if (getInventoryCount() < MAX_INV_SIZE) {
                int[] object = getObjectById(STAIRS_DOWN_W);
                if (object_valid(object)) {
                    atObject(object[1], object[2]);
                }
                return random(1000, 2000);
            } else {
                pw.setPath(stairs_to_bank);
                return 0;
            }
        }
        
        if (inside_bank()) {
            if (getInventoryCount() < MAX_INV_SIZE) {
                pw.setPath(bank_to_stairs);
                return 0;
            } else {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        final int font = 2;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("S Dragon Axe Buyer", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        drawString("Banked count: " + banked_count + " (" + per_hour(banked_count) + "/h)", x, y, font, white);
        y += 15;
    }
    
    /**/                                                                                                                                                                                                                                                                                    public void hop(int i){}
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("standing")) {
            move_time = System.currentTimeMillis() + random(600, 800);
        } else if (str.contains("money")) {
            System.out.println("out of money?");
            setAutoLogin(false); stopScript();
        }
    }
    
    // blood
    private String per_hour(int total) {
        try {
            return String.valueOf(((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L));
        } catch (ArithmeticException ex) {
        }
        return "0";
    }
    
    private boolean is_underground() {
        return getY() > 1000;
    }
    
    private void walk_approx(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 100) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
    }

    private boolean inside_bank() {
        return isAtApproxCoords(439, 496, 10);
    }

    private boolean inside_guild() {
        return getX() >= 368 && getX() <= 382 && getY() <= 440;
    }

    private boolean guild_inner_door() {
        if (getWallObjectIdFromCoords(378, 437) == 2) {
            atWallObject(378, 437);
            return true;
        }
        return false;
    }
    
    private boolean inner_obstacles(int target_x) {
        if (getX() > 377 && guild_inner_door()) {
            return true;
        }
        if (target_x > 377 && getX() <= 377 && guild_inner_door()) {
            return true;
        }
        if (getX() > 377 && target_x <= 377 && guild_inner_door()) {
            return true;
        }
        return false;
    }
    
    private boolean object_valid(int[] o) {
        return o[0] != -1 && distanceTo(o[1], o[2]) < 20;
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
}