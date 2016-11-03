import javax.swing.JOptionPane;


public class Limps extends Script {


   int step = 0;
   int[] path = null;
   int loop = 0;
   String fightModeSelected;
   int fightMode;
   int countLimp = 0;


   int start_xp = 0;
   int statTraining;
   long time = -1L;
   
   private static final int LIMPWURT_ROOT = 220;
   private static final int[] drops = {
        220, 527, 526, 20
    };


   public Limps(Extension e) {
      super(e);
   }

   public void init(String params) {
    
        Object[] fightModeOptions = {"Attack", "Strength", "Defense"};
        fightModeSelected = (String)JOptionPane.showInputDialog(null, "Select fight mode", "Limp Collector", JOptionPane.PLAIN_MESSAGE, null, fightModeOptions, fightModeOptions[0]);
        if (fightModeSelected.equals("Strength")) {
            fightMode = 1;
            statTraining = 2;
        } else if (fightModeSelected.equals("Attack")) {
            fightMode = 2;
            statTraining = 0;
        } else if (fightModeSelected.equals("Defense")) {
            fightMode = 3;
            statTraining = 1;
        }
}

      public int main() {

        if(time == -1L) {
            time = System.currentTimeMillis();
            start_xp = getXpForLevel(statTraining);
            return 500;
        }


        int bone = getInventoryIndex(20);
        if (bone != -1 && !inCombat())
        {
            useItem(bone);
            return random(600, 800);
        }

      if (getFightMode() != fightMode) {
            setFightMode(fightMode);
            return random(100,200);
        }

       if (getFatigue() >= 90 && hasInventoryItem(1263)) {
            useSleepingBag();
            return random(1000, 1500);
        }

      if(isBanking()) {
         int limp = getInventoryCount(LIMPWURT_ROOT);
                 if (limp > 0) {
                  countLimp += limp;
                  deposit(LIMPWURT_ROOT, limp);
                  limp = 0;
                  return random(1000, 1200);
                    }
         for (int i = 0; i < drops.length; i++) {
        if (hasInventoryItem(drops[i])) {
       deposit(drops[i], getInventoryCount(drops[i]));
      return random(700, 900);
   }
}
         closeBank();
      }
      if(isQuestMenu()) {
         answer(0);
         return random(6000, 6500);
      }
      if(isAtApproxCoords(364, 608, 20) && getInventoryCount() != 30) {
         step = 0;
         path = new int[] {351, 621, 336, 620, 320, 607, 307, 594, 294, 584, 282, 569};
         int[] item = getItemById(drops);
        int[] goblin = getNpcById(67);
        if (item[0] != -1 && (distanceTo(item[1] , item[2]) < 5) && (getInventoryCount() < 30)) {
       pickupItem(item[0], item[1], item[2]);
       return random(600, 900);
         }
         if(getInventoryCount() != 30) {
            if(goblin[0] != -1 && !inCombat())
               attackNpc(goblin[0]);
            return random(800, 1300);
         }
         return 1000;
      }
      if(isAtApproxCoords(282, 569, 4) && (getInventoryCount(220) > 0)) {
         int[] bankdoor = getObjectById(64);
         if(bankdoor[0] != -1) {
            atObject(bankdoor[1], bankdoor[2]);
            return random(1000, 1500);
          }
         step = 0;
         path = new int[] {294, 584, 307, 594, 320, 607, 336, 620, 351, 621, 364, 608};
         int[] npc = getNpcById(95);
         if(npc[0] != -1) {
            talkToNpc(npc[0]);
            return random(2000, 2700);
         }
      }
      if((step + 1) < path.length) {
         int[] bankdoor = getObjectById(64);
         if(bankdoor[0] != -1) {
            atObject(bankdoor[1], bankdoor[2]);
            return random(1000, 1500);
          }
         if(isAtApproxCoords(path[step], path[step + 1], 2))
            step = step + 2;
         walkTo(path[step] + random(-2, 2), path[step + 1] + random(-2, 2));
         return random(2000, 2500);
      }
      loop++;
      if(loop > 10) {
         if(isAtApproxCoords(282, 569, 10))
            path = new int[] {294, 584, 307, 594, 320, 607, 336, 620, 351, 621, 364, 608};
         step = 0;
         loop = 0;
      }
      return random(1000, 1200);
   }  
        public void paint() {
        int x = 10;
        int y = 222;
        drawString("Runtime: @whi@" + getTimeRunning(), x, y, 1, 0xFFFFFF);       
        y += 15;
        drawString("Limps: @whi@" + countLimp + "@whi@", x, y, 1, 0xFFFFFF);
y += 15;
    }
    
    private String getTimeRunning() {
        long time = ((System.currentTimeMillis() - this.time) / 1000);
        if (time >= 7200) {
            return new String((time / 3600) + " hours, " + ((time % 3600) / 60) + " minutes, " + (time % 60) + " seconds.");
        }
        if (time >= 3600 && time < 7200) {
            return new String((time / 3600) + " hour, " + ((time % 3600) / 60) + " minutes, " + (time % 60) + " seconds.");
        }
        if (time >= 60) {
            return new String(time / 60 + " minutes, " + (time % 60) + " seconds.");
        }
        return new String(time + " seconds.");
    }

}