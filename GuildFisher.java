import java.text.DecimalFormat;
import java.util.Locale;

public final class GuildFisher extends Script {

   private boolean walk1;
   private boolean walk2;
   private boolean certDone;
   private int answer2;
   private int checkLoop = 1;
   private int npcId;
   private int random;
   private int sleepAt = 79;
   private int [] fishId;
   private int [] fishCoords;

   // beep boop
   private final DecimalFormat iformat = new DecimalFormat("#,##0");
   private long start_time;
   private long menu_time;
   private int fishXp;
   private int startFishXp;
   private int cookXp;
   private int startCookXp;
   private int caught;
   private int idle;
   private int idle_x;
   private int idle_y;
   private int cooked;
   private int burned;

   public GuildFisher(Extension e) {
      super(e);
   }

   @Override
   public void init(String params) {
      System.out.println("Guild Fisher by mofo | edited by Storm for Frank");
      if(params.equalsIgnoreCase("shark")) {
         System.out.println("Set to fish shark..");
         answer2 = 2;
         fishId = new int[] {545, 546, 547};
         fishCoords = new int[] {261, 585, 498};
         sleepAt = 79;
         npcId = 370;
      } else if(params.equalsIgnoreCase("lobster")) {
         System.out.println("Set to fish lobster..");
         answer2 = 0;
         fishId = new int[] {372, 373, 374};
         fishCoords = new int[] {376, 589, 501, 588, 500};
         sleepAt = 85;
         npcId = 369;
      } else {
         System.out.println("Not a valid option. Please use lobster or shark!");
      }
      caught = 0;
      cooked = 0;
      burned = 0;
      start_time = menu_time = -1L;
   }

   @Override
   public int main() {
      if(start_time == -1L) {
         start_time = System.currentTimeMillis();
         fishXp = startFishXp = getXpForLevel(10);
         cookXp = startCookXp = getXpForLevel(7);
      } else {
         fishXp = getXpForLevel(10);
         cookXp = getXpForLevel(7);
      }
      switch (idle) {
      case 0:
         break;
      case 1:
         if (getX() != idle_x || getY() != idle_y) {
            idle = 2;
            return 0;
         }
         int x, y;
         do {
            x = idle_x + random(-1, 1);
            y = idle_y + random(-1, 1);
         } while (x == idle_x && y == idle_y);
         walkTo(x, y);
         return random(1000, 2000);
      case 2:
         if (getX() == idle_x && getY() == idle_y) {
            idle = 0;
            return 0;
         }
         walkTo(idle_x, idle_y);
         return random(300, 400);
      }
      if(getFatigue() > sleepAt) {
         useSleepingBag();
         return 1000;
      }
      if(isQuestMenu() && getInventoryCount(fishId[1]) > 4) {
         menu_time = -1L;
         certDone = true;
         if(questMenuCount() == 3) {
            answer(1);
            return random(1400, 1800);
         }
         if(questMenuCount() == 4) {
            answer(answer2);
            return random(1400, 1800);
         }
         if(questMenuCount() == 5) {
            if(getInventoryCount(fishId[1]) > 24)
               answer(4);
            if(getInventoryCount(fishId[1]) > 19 && getInventoryCount(fishId[1]) < 25)
               answer(3);
            if(getInventoryCount(fishId[1]) > 14 && getInventoryCount(fishId[1]) < 20)
               answer(2);
            if(getInventoryCount(fishId[1]) > 9 && getInventoryCount(fishId[1]) < 15)
               answer(1);
            if(getInventoryCount(fishId[1]) > 4 && getInventoryCount(fishId[1]) < 10)
               answer(0);
            random = random(1, 2);
            if(npcId == 369 && random > 1)
               fishCoords = new int[] {376, 589, 501};
            else if(npcId == 369 && random < 2)
               fishCoords = new int[] {376, 588, 500};
            return random(1000, 1500);
         }
         return 1000;
      } else if(menu_time != -1L) {
          if(System.currentTimeMillis() >= (menu_time + 8000L)) {
             menu_time = -1L;
          }
          return random(300, 400);
      }
      if(!isAtApproxCoords(587, 498, 5) && getInventoryCount(fishId[1]) < 5 && getInventoryCount() < 25) {
         int[] doors = getWallObjectById(2);
         if(walk2 == true && doors[1] == 603) {
            System.out.println("Someone shut us in while certing! Opening..");
            atWallObject(doors[1], doors[2]);
            return random(1500, 2000);
         }
         walkTo(587, 498 + random(-2, 2));
         return random(2500, 3000);
      }
      if(isAtApproxCoords(587,498, 5) && getInventoryCount() != 30) {
         certDone = false;
         walk2 = false;
         checkLoop = 0;
         int[] fish = getObjectById(fishCoords[0]);
         if( fish[0] != -1 && npcId == 370 ) {
            atObject2(fishCoords[1], fishCoords[2]);
         } else {
            atObject(fishCoords[1], fishCoords[2]);
         }
         return random(700, 1100);
      }
      if(getInventoryCount(fishId[0]) > 0 && getInventoryCount() == 30 && !isAtApproxCoords(586,521, 2)) {
         if(isAtApproxCoords(586, 511, 4))
            walk1 = true;
         if(!isAtApproxCoords(586, 511, 4) && walk1 == false) {
            walkTo(586, 511 + random(-2, 2));
            return random(1000, 1500);
         }
         int[] doors = getWallObjectById(2);
         if(walk1 == true && doors[1] == 586) {
            System.out.println("Someone shut the range door! Opening..");
            atWallObject(doors[1], doors[2]);
            return random(1500, 2000);
         }
         if(!isAtApproxCoords(586,521, 2) && getInventoryCount() == 30) {
            walkTo(586 + random(-2, 2), 521 + random(-2, 2));
            return random(2000, 3000);
         }
         return 1000;
      }
      if(isAtApproxCoords(586,521, 5) && getInventoryCount(fishId[0]) != 0) {
         int[] range = getObjectById(11);
         if(range[0] != -1) {
            if(getInventoryCount(fishId[0]) != 0) {
               useItemOnObject(fishId[0], 11);
               return random(700, 1100);
            }
            return 1000;
         }
         return 1000;
      }
      if(isAtApproxCoords(586,521, 5) && getInventoryCount(fishId[2]) != 0) {
         if(getInventoryIndex(fishId[2]) != -1) {
            dropItem(getInventoryIndex(fishId[2]));
            return random(1000, 1200);
         }
         return 500;
      }
      if(!isAtApproxCoords(604, 503, 2) && getInventoryCount(fishId[1]) > 4) {
         int[] doors = getWallObjectById(2);
         if(walk1 == true && doors[1] == 586) {
            System.out.println("Someone shut us in while cooking! Opening..");
            atWallObject(doors[1], doors[2]);
            return random(1500, 2000);
         }
         if(isAtApproxCoords(597, 512, 5)) {
            walk1 = false;
            walk2 = true;
         }
         if(!isAtApproxCoords(597, 512, 5) && walk2 == false) {
            walkTo(597, 512 + random(-2, 2));
            return random(1500, 2000);
         }
         if(walk2 == true && doors[1] == 603) {
            System.out.println("Someone shut the certer door! Opening..");
            atWallObject(doors[1], doors[2]);
            return random(1500, 2000);
         }
         walkTo(604 + random(-1, 1), 503 + random(-2, 2));
         return random(1500, 2000);
      }
      if(isAtApproxCoords(604, 503, 2) && getInventoryCount(fishId[1]) > 4 && !isQuestMenu() && certDone == false) {
         int npc[] = getNpcByIdNotTalk(npcId);
         if(npc[0] != -1) {
            talkToNpc(npc[0]);
            menu_time = System.currentTimeMillis();
         }
         return random(600, 800);
      }
      checkLoop++;
      if(checkLoop > 5)
         certDone = false;
      return 1000;
   }

   @Override
   public void paint() {
        final int font = 2;
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("mofo's GuildFisher", x, y, font, orangey);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        int gained = fishXp - startFishXp;
        drawString("Fishing XP: " + iformat.format(gained) +
           " (" + per_hour(gained) + "/h)",
           x, y, font, white);
        y += 15;
        drawString("Caught: " + iformat.format(caught) +
           " (" + per_hour(caught) + "/h)",
           x, y, font, white);
        y += 15;
        gained = cookXp - startCookXp;
        if (gained > 0) {
        	drawString("Cooking XP: " + iformat.format(gained) +
        		" (" + per_hour(gained) + "/h)",
        		x, y, font, white);
        	y += 15;
        }
        if (cooked > 0) {
        	drawString("Cooked: " + iformat.format(cooked) +
                " (" + per_hour(cooked) + "/h)",
                x, y, font, white);
        	y += 15;
        }
        if (burned > 0) {
        	drawString("Burned: " + iformat.format(burned) +
                " (" + per_hour(burned) + "/h)",
                x, y, font, white);
        	y += 15;
        }
   }
   
   @Override
   public void onServerMessage(String str) {
      str = str.toLowerCase(Locale.ENGLISH);
      if (str.contains("busy")) {
         menu_time = -1L;
      } else if (str.contains("standing")) {
         idle = 1;
         idle_x = getX();
         idle_y = getY();
      } else if (str.contains("you catch")) {
         ++caught;
      } else if (str.contains("nicely cooked")) {
         ++cooked;
      } else if (str.contains("burn")) {
         ++burned;
      }
   }

   private String per_hour(int count) {
      if (count == 0) return "0";
      double amount = count * 60.0 * 60.0;
      double secs = (System.currentTimeMillis() - start_time) / 1000.0;
      return iformat.format((long) (amount / secs));
   }

   private String get_runtime() {
      long millis = (System.currentTimeMillis() - start_time) / 1000;
      long second = millis % 60;
      long minute = (millis / 60) % 60;
      long hour = (millis / (60 * 60)) % 24;
      long day = (millis / (60 * 60 * 24));

      if (day > 0L) {
         return String.format("%02d days, %02d hrs, %02d mins",
            day, hour, minute);
      }
      if (hour > 0L) {
         return String.format("%02d hours, %02d mins, %02d secs",
            hour, minute, second);
      }
      if (minute > 0L) {
         return String.format("%02d minutes, %02d seconds",
            minute, second);
      }
      return String.format("%02d seconds", second);
   }
}