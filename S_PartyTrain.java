public final class S_PartyTrain extends Script {
    
    // to kill wolves, bears, highwaymen and npcs in the way of the path set to false
    private static final boolean AVOID_COMBAT = false;

    // to specify your own messages use the parameters field. separate them with ;
    private String[] messages = {
        "@red@drop party 27th decemeber 4pm eastern 9pm gmt seers village",
        "@gre@drop party 27th decemeber 4pm eastern 9pm gmt seers village",
        "@whi@drop party 27th decemeber 4pm eastern 9pm gmt seers village"
    };
    
    private static final int LUMB_X = 124;
    private static final int LUMB_Y = 648;
    
    private static final int DRAY_X = 219;
    private static final int DRAY_Y = 636;
    
    private static final int ROOM_X = 496;
    private static final int ROOM_Y = 469;
    
    private static final int GATE_R_X = 342;
    private static final int GATE_R_Y = 488;
    private static final int GATE_D_X = 341;
    private static final int GATE_D_Y = 488;
    
    private static final int[] gate = {
        341, 487
    };
    
    private static final int[] wolves = {
        239, 248, 249, 89 /*highwayman*/, 8 /*bear*/, 49 /* more bear (isn't this the varrock museum bear you can't attack? oh well) */
    };
    
    private static final int[] att_blocking = {
        200, 65, 127, 11, 0, 4, 62, 153, 154, 114, 81, 91, 23, 34, 19, 29, 47, 74
    };

    private int ptr;
    private long chat_wait;
    private long action_wait;
    private boolean fromlumb;
    private PathWalker pw;
    
    private PathWalker.Path lumb_to_dray;
    private PathWalker.Path dray_to_lumb;
    
    private PathWalker.Path gate_to_dray;
    private PathWalker.Path dray_to_gate;
    
    private PathWalker.Path room_to_gate;
    private PathWalker.Path gate_to_room;

    public S_PartyTrain(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
        if (!params.isEmpty()) {
            messages = params.split(";");
        }
        setTypeLine(messages[0]);
        ptr = 0;
        chat_wait = 0L;
        action_wait = -1L;
        if (lumb_to_dray == null) {
            pw.init(null);
            lumb_to_dray = pw.calcPath(LUMB_X, LUMB_Y, DRAY_X, DRAY_Y);
            dray_to_lumb = pw.calcPath(DRAY_X, DRAY_Y, LUMB_X, LUMB_Y);
            
            gate_to_dray = pw.calcPath(GATE_D_X, GATE_D_Y, DRAY_X, DRAY_Y);
            dray_to_gate = pw.calcPath(DRAY_X, DRAY_Y, GATE_D_X, GATE_D_Y);
            
            gate_to_room = pw.calcPath(GATE_R_X, GATE_R_Y, ROOM_X, ROOM_Y);
            room_to_gate = pw.calcPath(ROOM_X, ROOM_Y, GATE_R_X, GATE_R_Y);
        }
        if (isAtApproxCoords(DRAY_X, DRAY_Y, 15)) {
            fromlumb = false;
            pw.setPath(dray_to_lumb);
            System.out.println("Starting in Draynor.");
        } else if (isAtApproxCoords(ROOM_X, ROOM_Y, 20)) {
            fromlumb = false;
            pw.setPath(room_to_gate);
            System.out.println("Starting at Seers Village.");
        } else {
            fromlumb = true;
            pw.setPath(lumb_to_dray);
            System.out.println("Starting in Lumbridge.");
        }
    }

    @Override
    public int main() {
        if (System.currentTimeMillis() > chat_wait) {
            if (next()) {
                ++ptr;
                if (ptr >= messages.length) {
                    ptr = 0;
                }
                setTypeLine(messages[ptr]);
                chat_wait = System.currentTimeMillis() + random(9000, 11000);
            }
        }
        if (inCombat()) {
            pw.resetWait();
            if (AVOID_COMBAT) {
                if (System.currentTimeMillis() > action_wait) {
                    walkTo(getX(), getY());
                    action_wait = System.currentTimeMillis() + random(400, 600);
                }
            }
            return 0;
        }
        
        if (!AVOID_COMBAT) {
            int[] npc = getNpcById(wolves);
            if (npc[0] != -1 && distanceTo(npc[1], npc[2]) <= 5) {
                if (System.currentTimeMillis() > action_wait) {
                    pw.resetWait();
                    attackNpc(npc[0]);
                    action_wait = System.currentTimeMillis() + random(600, 1000);
                }
                return 0;
            }
            
            // defeat blocking npcs
            if (!isWalking()) {
                npc = getNpcById(att_blocking);
                if (npc[0] != -1 && distanceTo(npc[1], npc[2]) <= 1) {
                    if (System.currentTimeMillis() > action_wait) {
                        pw.resetWait();
                        attackNpc(npc[0]);
                        action_wait = System.currentTimeMillis() + random(600, 1000);
                    }
                    return 0;
                }
            }
        }
        
        if (action_wait != -1L) {
            if (System.currentTimeMillis() > action_wait) {
                action_wait = -1L;
            } else {
                return 0;
            }
        }
        
        if (pw.walkPath()) return 0;
        
        pw.resetWait();
        
        if (isAtApproxCoords(LUMB_X, LUMB_Y, 2)) {
            System.out.println("lumb to dray");
            pw.setPath(lumb_to_dray);
            fromlumb = true;
        } else if (isAtApproxCoords(DRAY_X, DRAY_Y, 2)) {
            if (fromlumb) {
                System.out.println("dray to gate");
                pw.setPath(dray_to_gate);
                fromlumb = false;
            } else {
                System.out.println("dray to lumb");
                pw.setPath(dray_to_lumb);
            }
        } else if (isAtApproxCoords(ROOM_X, ROOM_Y, 2)) {
            System.out.println("seers to gate");
            pw.setPath(room_to_gate);
        } else if (distanceTo(GATE_R_X, GATE_R_Y) < distanceTo(GATE_D_X, GATE_D_Y)) {
            System.out.println("gate to dray");
            atObject(gate[0], gate[1]);
            pw.setPath(gate_to_dray);
        } else {
            System.out.println("gate to room");
            atObject(gate[0], gate[1]);
            pw.setPath(gate_to_room);
        }
        action_wait = System.currentTimeMillis() + random(5000, 9000);
        return 0;
    }

}
