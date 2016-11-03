import javax.swing.*;

public class KinosZWine extends Script {

    // IDENTIFY INTEGERS
    int SpellId = 16;
    int TotalWines = 0;
    int GrabbingItem = 501;
    int Inventory = 0;
    int DesiredWines = 5000;
    boolean bankingnow;
    boolean walkingtoaltar;
    public String hoppingtoday;
    boolean walkingtobank;
    boolean waitingnow;
    boolean castingnow;
    boolean donecasting;
    boolean depositeditems;
    boolean endscript;
    boolean walking_path;
    int nextWorld = 2;
    int worldchosen = 2;
    int hopping = 3;
    int gnomeBall = 981;
    int[] bankGate;
    private long start_time;
    private int tile = 0;
    private final int[] altar_path_x = {
        316, 315, 297, 305, 313, 304, 325, 328
    };
    private final int[] altar_path_y = {
        538, 520, 504, 493, 477, 442, 435, 435
    };
    private final int[] bank_path_x = {
        328, 325, 321, 303, 312, 298, 314, 315, 326
    };
    private final int[] bank_path_y = {
        435, 435, 435, 439, 469, 505, 514, 533, 546
    };

    public KinosZWine(Extension e) {
        super(e);
    }

    public void init(String params) {
        // INITIAL VARIABLE QUERY
        Object[] ServerOptions = {
            "Vet Server 1, 2, & 3 - (NEEDS VETERAN ACCOUNT)",
            "Non Vet - Server 2 & 3"
        };
        String sd = (String) JOptionPane.showInputDialog(null,
                "Which Servers?", "Kino's Zammy Wines",
                JOptionPane.PLAIN_MESSAGE, null, ServerOptions,
                ServerOptions[0]);

        if (sd.equals("Non Vet - Server 2 & 3")) {
            hopping = 2;
            hoppingtoday = "Worlds 2 & 3";

        } else if (sd.equals("Vet Server 1, 2, & 3 - (NEEDS VETERAN ACCOUNT)")) {
            hopping = 3;
            hoppingtoday = "Worlds 1, 2, & 3";
        }

        String number = JOptionPane.showInputDialog(null,
                "How many wines to snatch?", "5000");
        if (number == null) {
            System.out
                    .println("You clicked cancel. How do you expect me to know what to do?");
        } else {
            try {
                DesiredWines = Integer.parseInt(number);
            } catch (Throwable t) {
                System.out.println("You entered an invalid number.");
            }
        }

        // OPENING SCREEN

        System.out.println(" ");
        System.out
                .println("  @xxxx|==============> Kino's SCRIPTS  <=============|xxxx@        ");
        System.out
                .println("*****************  Kino's Zammy Wines - Version 1.00    ****************");
        System.out
                .println("************************ INSTRUCTIONS  ********************************");
        System.out
                .println("******************* Start in West Fally Bank   *****************************");
        System.out
                .println("******   With at least 30 Laws, and a Staff Of Air in your bank     *********");
        System.out
                .println

                ("****************************************************************************");
        System.out.println("Will be hopping " + hoppingtoday
                + " worlds today, and stealing "

                + DesiredWines + " wines");

        // CLEAR VARIABLES
        castingnow = false;
        bankingnow = true;
        walkingtoaltar = false;
        waitingnow = false;
        walkingtobank = false;
        depositeditems = false;
        donecasting = false;
        endscript = false;
        start_time = -1L;

    }

    public int main() {

        // TIME CALCULATION STUFF
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }

        // SCRIPT ENDING

        if (endscript) {
            System.out.println("All done for today! Script Ending");
            stopScript();
            return random(600, 800);
        }

        // IF REACHED AMOUNT OF WINE AMOUNTS

        if ((TotalWines == DesiredWines) && castingnow) {
            castingnow = false;
            walkingtobank = true;
            donecasting = false;
            System.out.println("Finished getting all your wines!");
            return random(600, 800);
        }

        // AUTOMATIC DOOR OPENER
        if (isAtApproxCoords(331, 553, 4) || isAtApproxCoords(331, 553, 4)) {
            int[] bankGate = getObjectById(64);
            if (bankGate[0] != -1) {
                atObject(bankGate[1], bankGate[2]);
                return random(1200, 1300);
            }
        }

        // NOOB CHECKER
        if (getCurrentLevel(6) < 33) {
            System.out
                    .println("You have got to be joking, your magic level is not high enough.");
            System.out.println("Come back when your magic level is above 33");
            endscript = true;
        }

        // BANKING ROUTINE
        if (bankingnow) {
            if (!isBanking()) {
                if (isQuestMenu()) {
                    answer(0);
                    return random(600, 800);
                }
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] == -1) {
                    return random(200, 400);
                } else if (isReachable(banker[1], banker[2])) {
                    if (isAtApproxCoords(330, 554, 5)) {
                        talkToNpc(banker[0]);
                        return random(5000, 5000);
                    } else {
                        walkTo(330 + random(0, 1), 554 + random(0, 1));
                    }
                    return random(800, 1200);
                }
            }

            else if (isBanking()) {
                // STOP GNOMEBALL TRICK

                if (getInventoryCount(gnomeBall) > 0) {
                    System.out.println("We got trolled by "
                            + getInventoryCount(gnomeBall)
                            + "Gnome Ball! Banked!");
                    deposit(gnomeBall, getInventoryCount(gnomeBall));
                    return random(1111, 1500);
                }

                // CHECK TO SEE IF THERE ARE ENOUGH LAWS
                if ((bankCount(42) < 30) && (getInventoryCount(42) < 30)) {
                    System.out
                            .println("Might pay to get some Laws before running this script.");
                    endscript = true;

                }
                // CHECK TO SEE IF USER HAS STAFF OF AIR

                if ((bankCount(101) < 1) && (getInventoryCount(101) < 1)) {
                    System.out
                            .println("You need a Staff Of Air to run this script.");
                    endscript = true;

                }
                // CHECK TO SEE IF USER HAS SLEEPING BAG

                if ((bankCount(1263) < 1) && (getInventoryCount(1263) < 1)) {
                    System.out.println("Go get a sleeping bag and come back.");
                    endscript = true;
                }

                if (TotalWines == DesiredWines) {
                    endscript = true;

                }
                // WITHDRAW NEEDED ITEMS

                if (depositeditems) {
                    if (getInventoryCount(101) < 1) {
                        withdraw(101, 1);
                    } else if (getInventoryCount(42) < 30) {
                        withdraw(42, 30);
                    } else if (getInventoryCount(1263) < 1) {
                        withdraw(1263, 1);
                    } else {
                        depositeditems = false;
                        bankingnow = false;
                        walkingtoaltar = true;
                        Inventory = 0;
                        System.out.println("Heading to the Zammy Altar!");
                        closeBank();
                    }
                    return random(600, 800);
                }

                // DEPOSIT ENTIRE INVENTORY

                if (getEmptySlots() != MAX_INV_SIZE) {

                    for (int i = 0; i < getInventoryCount(); i++) {
                        final int id = getInventoryId(i);
                        deposit(id, getInventoryCount(id));
                    }
                    depositeditems = true;
                    return random(750, 1000);

                } else
                    depositeditems = true;
                return random(750, 1000);
            }

        }
        // ONLY 3 WORLDS

        if (nextWorld >= 4) {
            nextWorld = worldchosen;
        }
        // FATIGUE CHECK

        if (getFatigue() > 75) {

            useSleepingBag();
            return 1000;
        }

        // WALKING TO ZAMMY ALTAR

        if (walkingtoaltar) {

            if (getY() == 435 && getX() == 328) {

                walkingtoaltar = false;
                castingnow = true;
                donecasting = false;
                walkingtobank = false;
                walking_path = false;
                tile = 0;
                System.out.println("Entering Chapel..");
                return random(1000, 1500);
            } else {
                walkToChurch();
                return random(1000, 1500);

            }
        }

        // WALKING TO THE BANK

        if (walkingtobank) {
            if (getY() >= 545 && getY() <= 549 && getX() >= 325) {
                walkingtobank = false;
                bankingnow = true;
                walking_path = false;
                System.out.println("Returning to the bank.");
                tile = 0;
                walkTo(330, 554);
                return random(1000, 1500);
            } else {
                walkToBank();
                return random(1000, 1500);
            }
        }

        // HOPPING WORLDS
        if (donecasting) {
            hop(nextWorld);
            nextWorld++;
            donecasting = false;
            System.out.println("Let's Hop");
            return random(8000, 8000);
        }

        // FULL INVENTORY CONDITIONS
        if ((getInventoryCount() == 30) && (!walkingtobank) && (castingnow)) {
            System.out.println("All full");
            castingnow = false;
            walkingtobank = true;
            System.out.println("Returning to bank");
            return 5000;
        }

        // OUT OF LAW RUNES
        if ((getInventoryCount(42) < 1) && (!walkingtobank) && (castingnow)) {
            System.out.println("You have run out of Law Runes Mate...");
            castingnow = false;
            walkingtobank = true;
            return 5000;
        }

        // NOTHING TO PICK UP
        if (getGroundItemCount() < 1 && castingnow) {
            System.out.println("Waiting for more wines to spawn.");

            return 5000;
        }

        // GOT THE WINES

        if ((getInventoryCount(501) > Inventory) && (castingnow)) {
            Inventory = getInventoryCount(501);
            TotalWines = TotalWines + 1;
            System.out.println("Wully Bulllllyyy!!!....... Wully Bully! 'hic");
            donecasting = true;
            return 3000;
        }

        // TELEGRABBING THE WINES
        if (getGroundItemCount() > 0 && castingnow) {
            int staff = getInventoryIndex(101);
            if (staff == -1) {
                System.out.println("no staff, dummy");
                endscript = true;
                return 0;
            }
            if (!isItemEquipped(staff)) {
                wearItem(staff);
                return random(600, 800);
            }
            castOnGroundItem(SpellId, GrabbingItem, 333, 434);
            return 3000;
        }
        return 8000;

    } // END OF MAIN PROGRAM

    // PAINT ROUTINE
    public void paint() {
        int y = 40;
        int x = 315;
        drawBoxAlphaFill(315, y, 193, 80, 160, 0x008080);
        drawBoxOutline(315, y, 193, 80, 0x0033FF);
        y += 15;
        x += 3;
        drawString("Kino's Zammy Wines", x, y, 4, 0xd436fb);
        y += 10;
        drawHLine(x - 2, y, 190, 0xd436fb);
        y += 20;
        drawString("Runtime: " + _getRuntime(), x, y, 1, 0xd436fb);
        y += 15;
        drawString("Snatched " + TotalWines + " Wine's Of Zamorak.", x, y, 1,
                0xd436fb);
    }

    // KEEPING TIME
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return (secs / 3600) + " hours, " + ((secs % 3600) / 60)
                    + " mins, " + (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " + (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }

    // WALKING TO CHURCH ROUTINE

    private void walkToChurch() {
        walking_path = true;
        if (isAtApproxCoords(altar_path_x[tile], altar_path_y[tile], 0)) {
            tile++;
        }
        walkTo(altar_path_x[tile], altar_path_y[tile]);
    }

    // WALKING TO BANK ROUTINE
    private void walkToBank() {
        walking_path = true;
        if (isAtApproxCoords(bank_path_x[tile], bank_path_y[tile], 0)) {
            tile++;
        }
        walkTo(bank_path_x[tile], bank_path_y[tile]);
    }

} // end of script 