import java.text.DecimalFormat;
import java.util.Locale;

public class S_KrymeBigBones extends Script {
    
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private boolean veteran = true;
    private long start_time;
    private int start_xp;
    private int xp;
    private int levels_gained;
    private boolean check;
    
    public S_KrymeBigBones(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        start_time = -1L;
        levels_gained = 0;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            start_xp = xp = getXpForLevel(5);
        } else {
            xp = getXpForLevel(5);
        }
        if (getFatigue() > 98) {
            useSleepingBag();
            return random(1000, 2000);
        }
        int slot = getInventoryIndex(413);
        if (slot != -1) {
            useItem(slot);
            return random(600, 800);
        }
        int[] item = getItemById(413);
        if (item[1] == 700 && item[2] == 648) {
            pickupItem(item[0], item[1], item[2]);
            return random(1000, 2000);
        } else if (!check) {
            check = true;
            return random(900, 1200);
        }
        switch (getWorld()) {
            case 1:
                check = false;
                hop(2);
                break;
            case 2:
                check = false;
                hop(3);
                break;
            case 3:
                check = false;
                if (veteran)
                    hop(1);
                else
                    hop(2);
                break;
        }
        return random(1000, 2000);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("advanced")) {
            ++levels_gained;
        }
    }
    
    @Override
    public void paint() {
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S Kryme Big Bones", x, y, 1, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, 1, white);
        y += 15;
        int xp_gained = xp - start_xp;
        drawString("XP gained: " + iformat.format(xp_gained) + " (" + _perHour(xp_gained) + "/h)", x, y, 1, white);
        y += 15;
        if (levels_gained > 0) {
            drawString("Levels gained: " + levels_gained + " (" + _perHour(levels_gained) + "/h)", x, y, 1, white);
            y += 15;
        }
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
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
    
    // blood
    private String _perHour(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return iformat.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }
}