// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(50) braces fieldsfirst nonlb space radix(10) 
// Source File Name:   Catherby.java
// I lost this script's source code, sorry!

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Locale;

public class S_CatherbyFishCook extends Script
    implements ActionListener {

	private static final int SKILL_COOK = 7;
	private static final int SKILL_FISH = 10;
	private final DecimalFormat iformat = new DecimalFormat("#,##0");
	private Frame frame;
	private Choice choice;
	private long bank_time;
	private long menu_time;
	private long sleep_time;
	private long click_time;
	private int object_id;
	private int raw_id;
	private int cooked_id;
	private int burned_id;
	private int move_x;
	private int move_y;
	private boolean cook;
	private boolean second_click;

	private long start_time;
	private long total_success;
	private long cur_success;
	private long total_fails;
	private long cur_fails;
	private int levels_gained;
	private long lvl_time;

	public S_CatherbyFishCook(Extension extension) {
		super(extension);
	}

	public void init(String s) {
		lvl_time = -1L;
		bank_time = -1L;
		menu_time = -1L;
		sleep_time = -1L;
		click_time = -1L;
		start_time = -1L;

		total_success = 0L;
		cur_success = 0L;
		total_fails = 0L;
		cur_fails = 0L;
		levels_gained = 0;
		if (frame == null) {
			Frame frame1 = new Frame("Catherby");
			frame1.setIconImages(Constants.ICONS);
			frame1.addWindowListener(new StandardCloseHandler(frame1, 0));
			Panel panel = new Panel();
			choice = new Choice();
			choice.add("Shrimp");
			choice.add("Lobsters");
			choice.add("Swordfish");
			choice.add("Sharks");
			choice.select(1);
			panel.add(choice);
			Panel panel1 = new Panel();
			Button button = new Button("Cook");
			button.addActionListener(this);
			panel1.add(button);
			Button button1 = new Button("Fish");
			button1.addActionListener(this);
			panel1.add(button1);
			frame1.add(panel, "Center");
			frame1.add(panel1, "South");
			frame1.pack();
			frame1.setMinimumSize(frame1.getSize());
			frame1.setSize(180, 120);
			frame = frame1;
		}
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public int main() {
		if (lvl_time != -1L) {
			if (System.currentTimeMillis() >= lvl_time) {
				System.out.print("Congrats on level ");
				System.out.print(getLevel(cook ? SKILL_COOK : SKILL_FISH));
				System.out.println(cook ? " cooking!" : " fishing!");
				lvl_time = -1L;
			}
		}

		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
		}

		if (isQuestMenu()) {
			answer(0);
			menu_time = -1L;
			bank_time = System.currentTimeMillis();
			return random(600, 800);
		}
		if (menu_time != -1L) {
			if (System.currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(100, 300);
		}
		if (bank_time != -1L) {
			if (!isBanking()) {
				if (System.currentTimeMillis() >= (bank_time + 8000L)) {
					bank_time = -1L;
				}
				return random(300, 400);
			} else {
				bank_time = -1L;
			}
		}
		if (move_x != 0 && move_y != 0) {
			if (getX() == move_x && getY() == move_y) {
				move_x = 0;
				move_y = 0;
				return 0;
			}
			walkTo(move_x, move_y);
			return random(1000, 2000);
		}
		if (sleep_time != -1L) {
			if (System.currentTimeMillis() >= sleep_time) {
				useSleepingBag();
				sleep_time = -1L;
				return random(1000, 1500);
			}
			return 0;
		}
		if (click_time != -1L) {
			if (System.currentTimeMillis() >= click_time) {
				click_time = -1L;
			}
			return 0;
		}
		return cook ? doCook() : doFish();
	}

	private int doFish() {
		if (isBanking()) {
			int i = getInventoryCount(raw_id);
			if (i > 0) {
				deposit(raw_id, i);
				return random(600, 800);
			}
			closeBank();
			return random(1000, 1300);
		}
		if (getInventoryCount() == MAX_INV_SIZE) {
			if (getX() < 424) {
				walkTo(424 + random(0, 1), 498 + random(0, 1));
				return random(1000, 1300);
			}
			if (getX() < 434) {
				walkTo(434 + random(0, 1), 498 + random(0, 1));
				return random(1000, 1300);
			}
			if (getY() > 496) {
				int[] ai = getObjectById(64);
				if (ai[0] != -1) {
					atObject(ai[1], ai[2]);
					return random(1000, 1300);
				}
			}
			int[] ai1 = getNpcByIdNotTalk(BANKERS);
			if (ai1[0] != -1) {
				talkToNpc(ai1[0]);
			}
			return random(1500, 1600);
		}
		if (getY() < 497) {
			int[] ai2 = getObjectById(64);
			if (ai2[0] != -1) {
				atObject(ai2[1], ai2[2]);
				return random(1000, 1300);
			}
			walkTo(430 + random(0, 1), 497 + random(0, 1));
			return random(1000, 1300);
		}
		if (getX() > 417) {
			walkTo(417 - random(0, 1), 498 + random(0, 1));
			return random(1000, 1300);
		}
		if (getX() > 414) {
			walkTo(414 - random(0, 1), 499 + random(0, 1));
			return random(1000, 1300);
		}
		if (raw_id == 349) {
			int j = getInventoryIndex(351);
			if (j != -1) {
				dropItem(j);
				return random(800, 900);
			}
		} else if (raw_id == 369) {
			int k = getInventoryIndex(366);
			if (k != -1) {
				dropItem(k);
				return random(800, 900);
			}
		}
		atObject();
		return random(600, 800);
	}

	private void atObject() {
		int[] ai = getObjectById(object_id);
		if (ai[0] != -1) {
			if (distanceTo(ai[1], ai[2]) > 2) {
				walkTo(ai[1], ai[2] - 1);
				return;
			}
			if (second_click) {
				atObject2(ai[1], ai[2]);
			} else {
				atObject(ai[1], ai[2]);
			}
		}
	}

	private int doCook() {
		if (isBanking()) {
			int i = getInventoryCount(cooked_id);
			if (i > 0) {
				deposit(cooked_id, i);
				return random(600, 800);
			}
			if (!hasInventoryItem(raw_id)) {
				if (hasBankItem(raw_id)) {
					withdraw(raw_id, getEmptySlots());
					return random(600, 800);
				}
				System.out.println("Out of raw fish");
				stopScript();
				setAutoLogin(false);
				return 0;
			}
			closeBank();
			return random(1000, 1300);
		}
		int j = getInventoryIndex(burned_id);
		if (j != -1) {
			dropItem(j);
			return random(800, 900);
		}
		if (hasInventoryItem(raw_id)) {
			if (getX() > 436 && getY() > 490) {
				int[] ai = getObjectById(64);
				if (ai[0] != -1) {
					atObject(ai[1], ai[2]);
					return random(1000, 1300);
				}
				walkTo(436 - random(0, 1), 488 - random(0, 2));
				return random(1000, 1300);
			}
			if (distanceTo(432, 481) > 1) {
				if (getWallObjectIdFromCoords(435, 486) == 2) {
					atWallObject(435, 486);
					return random(1000, 1300);
				}
				walkTo(433, 481 - random(0, 1));
				return random(1000, 1300);
			}
			useItemOnObject(raw_id, 11);
			return random(600, 800);
		}
		if (getY() < 486 && getWallObjectIdFromCoords(435, 486) == 2) {
			atWallObject(435, 486);
			return random(1000, 1300);
		}
		if (getX() < 437 && getY() < 491) {
			walkTo(440 - random(0, 1), 497);
			return random(1000, 1300);
		}
		if (getX() > 436 && getY() > 496) {
			int[] ai1 = getObjectById(64);
			if (ai1[0] != -1) {
				atObject(ai1[1], ai1[2]);
				return random(1000, 1300);
			}
		}
		int[] ai2 = getNpcByIdNotTalk(BANKERS);
		if (ai2[0] != -1) {
			talkToNpc(ai2[0]);
			return random(3000, 3500);
		}
		return random(200, 300);
	}

	private void processChoice() {
		switch (choice.getSelectedIndex()) {
		case 0: // '\0'
			object_id = 193;
			raw_id = 349;
			cooked_id = 350;
			burned_id = 353;
			second_click = false;
			break;

		case 1: // '\001'
			object_id = 194;
			raw_id = 372;
			cooked_id = 373;
			burned_id = 374;
			second_click = true;
			break;

		case 2: // '\002'
			object_id = 194;
			raw_id = 369;
			cooked_id = 370;
			burned_id = 371;
			second_click = false;
			break;

		case 3: // '\003'
			object_id = 261;
			raw_id = 545;
			cooked_id = 546;
			burned_id = 547;
			second_click = true;
			break;
		}
		frame.setVisible(false);
	}

	public void actionPerformed(ActionEvent actionevent) {
		if (actionevent.getActionCommand().equals("Cook")) {
			cook = true;
			processChoice();
		} else if (actionevent.getActionCommand().equals("Fish")) {
			cook = false;
			processChoice();
		}
	}

	public void onServerMessage(String s) {
		s = s.toLowerCase(Locale.ENGLISH);
		if (s.contains("tired")) {
			sleep_time = System.currentTimeMillis() + random(1500, 1800);
		} else if (s.contains("standing here")) {
			move_x = getX();
			if (cook) {
				move_y = getY() + 1;
			} else {
				move_y = getY() - 1;
			}
		} else if (s.contains("attempt") || s.contains("you cook")) {
			click_time = System.currentTimeMillis() + random(5000, 7000);
		} else if (s.contains("fail") || s.contains("burn")) {
			click_time = System.currentTimeMillis() + random(100, 200);
			++cur_fails;
			++total_fails;
		} else if (s.contains("nicely") || s.contains("you catch")) {
			click_time = System.currentTimeMillis() + random(100, 200);
			++cur_success;
			++total_success;
		} else if (s.contains("advanced")) {
			System.out.println("You just advanced a level.");
			System.out.print("Runtime: ");
			System.out.println(get_time_since(start_time));
			System.out.print("Old success count: ");
			System.out.println(cur_success);
			System.out.print("Old fail count: ");
			System.out.println(cur_fails);
			System.out.print("Old fail rate: ");
			System.out.println((double) cur_fails / (double) cur_success);
			System.out.print("Fail total: ");
			System.out.println(total_fails);
			System.out.print("Success total: ");
			System.out.println(total_success);
			lvl_time = System.currentTimeMillis() + 2000L;
			cur_fails = 0;
			cur_success = 0;
			++levels_gained;
		}
	}

	public String toString() {
		return (new StringBuilder("Catherby ")).append(cook ? "Cooker" : "Fisher").toString();
	}

	@Override
	public void walkTo(int x, int y) {
		if (!isWalking()) {
			super.walkTo(x, y);
		}
	}

	@Override
	public void paint() {
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;
		int x = (getGameWidth() / 2) - 125;
		int y = 50;
		drawString("S " + toString(),
		    x, y, 1, orangey);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x + 10, y, 1, white);
		y += 15;
		drawString(String.format("Stats for current level (%d gained)",
		    levels_gained),
		    x, y, 1, orangey);
		y += 15;
		drawString(String.format("Successful attempts: %s (%s/h)",
		    iformat.format(cur_success),
		    per_hour(cur_success)),
		    x + 10, y, 1, white);
		y += 15;
		drawString(String.format("Failed attempts: %s (%s/h)",
		    iformat.format(cur_fails),
		    per_hour(cur_fails)),
		    x + 10, y, 1, white);
		y += 15;
		drawString("Fail rate: " + (float)
		    ((double) cur_fails / (double) cur_success),
		    x + 10, y, 1, white);
		y += 15;
		if (levels_gained > 0) {
			drawString("Total:", x, y, 1, orangey);
			y += 15;
			drawString("Successful attempts: " +
			    iformat.format(total_success),
			    x + 10, y, 1, white);
			y += 15;
			drawString("Failed attempts: " +
			    iformat.format(total_fails),
			    x + 10, y, 1, white);
			y += 15;
		}
	}

	private static String get_time_since(long t) {
		long millis = (System.currentTimeMillis() - t) / 1000;
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

	private String per_hour(long count) {
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}
}
