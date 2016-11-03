import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Locale;

public final class S_HorvikSeller extends Script {

    private static final int COINS = 10;
    private static final int BANK_DOOR_CLOSED = 64;
    private static final int HORVIK_OPTION = 1;
    private static final int BANK_OPTION = 0;
    private static final int HORVIK = 48;
    private static final int DOOR_CLOSED = 2;
    private long shop_time;
    private long bank_time;
    private long menu_time;
    private long start_time;
    private int[] item_ids;
    private boolean gp_banked;
    private int gp_banked_count;
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private static final Point outside_store = new Point(112, 501);
    private static final Point east_store_door = new Point(113, 501);
    private static final Point south_store_door = new Point(117, 506);

    public S_HorvikSeller(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        error: try {
            if (params.isEmpty()) break error;
            String[] split = params.split(",");
            if (split.length == 0) break error;
            item_ids = new int[split.length];
            for (int i = 0; i < split.length; ++i) {
                item_ids[i] = Integer.parseInt(split[i]);
            }
            shop_time = -1L;
            bank_time = -1L;
            menu_time = -1L;
            start_time = -1L;
            gp_banked = false;
            return;
        } catch (NumberFormatException ex) {
        }
        System.out.println("Error: You must give item id(s) to sell as parameters. Example: 118,8 (steel and iron plates)");
    }
    
    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            menu_time = -1L;
            if (getInventoryIndex(item_ids) != -1) {
                answer(HORVIK_OPTION);
                shop_time = System.currentTimeMillis();
            } else {
                answer(BANK_OPTION);
                bank_time = System.currentTimeMillis();
            }
            return random(600, 900);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isBanking()) {
            bank_time = -1L;
            if (getInventoryCount() == MAX_INV_SIZE) {
                closeBank();
                return random(600, 900);
            }
            int coin_count = getInventoryCount(COINS);
            if (coin_count > 0) {
                deposit(COINS, coin_count);
                if (!gp_banked) {
                    gp_banked_count += coin_count;
                    gp_banked = true;
                }
                return random(600, 900);
            }
            for (int id : item_ids) {
                int count = bankCount(id);
                int empty = getEmptySlots();
                if (count > empty) {
                    count = empty;
                }
                if (count <= 0) continue;
                withdraw(id, count);
                return random(600, 900);
            }
            closeBank();
            if (getInventoryIndex(item_ids) == -1) {
                System.out.println("Out of things to sell");
                System.out.println("Banked gp: " + iformat.format(gp_banked_count));
                stopScript(); setAutoLogin(false);
            }
            return random(600, 900);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isShopOpen()) {
            shop_time = -1L;
            for (int id : item_ids) {
                int count = getInventoryCount(id);
                int index = getShopItemById(id);
                if (count > 0 && index != -1) {
                    sellShopItem(index, count);
                    gp_banked = false;
                    return random(600, 900);
                }
            }
            closeShop();
            return random(600, 900);
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(300, 400);
        }
        
        if (_inStore()) {
            if (getInventoryIndex(item_ids) != -1) {
                int[] horvik = getNpcByIdNotTalk(HORVIK);
                if (horvik[0] != -1) {
                    if (_inStore(horvik[1], horvik[2]) || !_openSouthStoreDoor()) {
                        talkToNpc(horvik[0]);
                        menu_time = System.currentTimeMillis();
                    }
                }
                return random(600, 900);
            } else {
                if (!isWalking()) {
                    Point p = _getBankPoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else if (!_openEastStoreDoor()) {
                        p = _getOutsideBank();
                        walkTo(p.x, p.y);
                    }
                }
                return random(1000, 2000);
            }
        } else if (_inBank()) {
            if (getInventoryIndex(item_ids) == -1) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 900);
            } else {
                if (!isWalking()) {
                    Point p = _getStorePoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else if (!_openBankDoor()) {
                        p = _getOutsideStore();
                        walkTo(p.x, p.y);
                    }
                }
                return random(1000, 2000);
            }
        } else if (!isWalking()) {
            if (getInventoryIndex(item_ids) != -1) {
                int[] horvik = getNpcById(HORVIK);
                if (horvik[0] != -1 && !_inStore(horvik[1], horvik[2])) {
                    talkToNpc(horvik[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 900);
                }
                Point p = _getStorePoint();
                if (isReachable(p.x, p.y)) {
                    walkTo(p.x, p.y);
                } else {
                    p = _getOutsideStore();
                    if (!isAtApproxCoords(p.x, p.y, 3)) {
                        walkTo(p.x, p.y);
                    } else if (!_openEastStoreDoor()) {
                        System.out.println("Trapped, wat do?");
                    }
                }
            } else {
                Point p = _getBankPoint();
                if (isReachable(p.x, p.y)) {
                    walkTo(p.x, p.y);
                } else {
                    p = _getOutsideBank();
                    if (!isAtApproxCoords(p.x, p.y, 3)) {
                        walkTo(p.x, p.y);
                    } else if (!_openBankDoor()) {
                        System.out.println("Trapped, wat do?");
                    }
                }
            }
            return random(1000, 2000);
        }
        return random(600, 800);
    }
    
    private boolean _openBankDoor() {
        int[] door = getObjectById(BANK_DOOR_CLOSED);
        if (door[0] != -1 && distanceTo(door[1], door[2]) < 16) {
            atObject(door[1], door[2]);
            return true;
        }
        return false;
    }
    
    private boolean _openEastStoreDoor() {
        Point p = east_store_door;
        if (getWallObjectIdFromCoords(p.x, p.y) == DOOR_CLOSED) {
            atWallObject(p.x, p.y);
            return true;
        }
        return false;
    }
    
    private boolean _openSouthStoreDoor() {
        Point p = south_store_door;
        if (getWallObjectIdFromCoords(p.x, p.y) == DOOR_CLOSED) {
            atWallObject(p.x, p.y);
            return true;
        }
        return false;
    }
    
    private boolean _inBank() {
        return _inBank(getX(), getY());
    }
    
    private static boolean _inBank(int x, int y) {
        return x < 107 && y < 516 && y > 509;
    }
    
    private Point _getBankPoint() {
        return new Point(102 + random(-3, 3), 512 + random(-1, 1));
    }

    private boolean _inStore() {
        return _inStore(getX(), getY());
    }
    
    private static boolean _inStore(int x, int y) {
        return x > 112 && y < 506;
    }
    
    private Point _getStorePoint() {
        return new Point(115 + random(-2, 2), 502 + random(-2, 2));
    }
    
    private Point _getOutsideBank() {
        boolean b1 = random(0, 1) != 0;
        return new Point(b1 ? 102 : 103, 509);
    }
    
    private Point _getOutsideStore() {
        return outside_store;
    }
    
    @Override
    public void paint() {
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S Horvik Seller", x, y, 1, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, 1, white);
        y += 15;
        drawString("Banked gp: " + iformat.format(gp_banked_count), x, y, 1, white);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return iformat.format((secs / 3600)) + " hours, " +
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
