import java.util.Locale;

import javax.swing.JOptionPane;

public final class NaturesAllWorlds extends Script {

    private int[] worlds;
    private int hop_target;
    private long hop_time;

    public NaturesAllWorlds(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {      
        final Object[] options = { "World 1, 2, 3", "World 1, 2", "World 2, 3", "World 1, 3" };
        final int selection = JOptionPane.showOptionDialog(null,
                "Select a world",
                "Hopping script",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                options[0]);
        
        switch (selection) {
            case 0:
                worlds = new int[] { 1, 2, 3 };
                break;
                
            case 1:
                worlds = new int[] { 1, 2 };
                break;
                
            case 2:
                worlds = new int[] { 2, 3 };
                break;
                
            case 3:
                worlds = new int[] { 1, 3 };
                break;
                
            default:
                System.out.println("Unrecognized world option");
                break;
        }
    }

    @Override
    public int main() {
        if (hop_time == -1L) {
            hop_time = System.currentTimeMillis() + random(2000, 2500);
        }
        
        if (getFightMode() != 2) {
            setFightMode(2);
            return 0;
        }

        if (getFatigue() > 99) {
            int[] bed = getObjectById(14);
            if (bed[0] != -1 && distanceTo(bed[1], bed[2]) < 7) {
                atObject(bed[1], bed[2]);
            } else {
                useSleepingBag();
            }
            return random(1000, 2000);
        }

        int[] nats = getObjectById(335);
        if (nats[0] != -1 && distanceTo(nats[1], nats[2]) < 10) {
            atObject2(nats[1], nats[2]);
            return 5000;
        }
        
        if (System.currentTimeMillis() >= hop_time) {
            final int cur_world = getWorld();
            do {
                if (++hop_target >= worlds.length) {
                    hop_target = 0;
                }
            } while (worlds[hop_target] == cur_world);
            hop(worlds[hop_target]);
            hop_time = -1L;
            return random(2000, 3000);
        }
        
        return 0;
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("welcome")) {
            hop_time = System.currentTimeMillis() + random(2000, 2500);
        }
    }
}