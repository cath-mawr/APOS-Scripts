import java.util.*;

public final class S_ItemDistributor extends Script {

    private static final String[] messages = {
        "@mag@Trade me for free stuff",
        "@whi@Trade me for free stuff",
        "@cya@Trade me for free stuff",
        "@whi@Trade me for free stuff"
    };
    private int message_ptr;

    private final HashMap<String, Long> trade_requests = new HashMap<>();
    private final HashSet<String> blocked_players = new HashSet<>();

    private int[] items;
    private boolean[] traded_items;

    private long menu_time;
    private long bank_time;
    private boolean close_bank;

    private long trade_started;
    private long next_message;
    private String current_target_player = "";

    public S_ItemDistributor(Extension ex)
    {
        super(ex);
    }

    @Override
    public void init(String params)
    {
    	try {
	    	StringTokenizer t = new StringTokenizer(params, ",");
	    	int count = t.countTokens();
	    	items = new int[count];
	    	traded_items = new boolean[count];
	    	for (int i = 0; i < count; ++i) {
	    		items[i] = Integer.parseInt(t.nextToken());
	    	}
    	} catch (Throwable t) {
    		System.out.println(
    			"Error: you must enter the item IDs to distribute in the parameters box! " +
    			"(example: 6,8,9,2 to give out iron sets)");
    		return;
    	}
        trade_started = next_message = System.currentTimeMillis();
        menu_time = bank_time = -1L;
        close_bank = false;
    }

    @Override
    public int main()
    {
        if (isQuestMenu()) {
            answer(0);
            menu_time = -1L;
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }

        if (isBanking()) {
            bank_time = -1L;
            if (close_bank) {
                closeBank();
                close_bank = false;
                return random(600, 800);
            }

            int inv_size = getInventoryCount();
            for (int i = 0; i < inv_size; ++i) {
                int id = getInventoryId(i);
                if (!inArray(items, getInventoryId(i))) {
                    deposit(id, getInventoryCount(id));
                    return random(600, 1000);
                }
            }

            int lowest_id = -1;
            int lowest_amount = Integer.MAX_VALUE;
            for (int id : items) {
                int count = getInventoryCount(id);
                if (count < lowest_amount) {
                    lowest_amount = count;
                    lowest_id = id;
                }
            }
            if (lowest_id == -1 || !hasBankItem(lowest_id)) {
                System.out.println("Out of items?");
                stopScript(); setAutoLogin(false);
                closeBank();
                return random(600, 800);
            }
            withdraw(lowest_id, 1);
            return random(600, 1000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        if (isInTradeConfirm()) {
            if ((System.currentTimeMillis() - trade_started) > 15000L) {
                declineTrade();
                blocked_players.add(current_target_player);
            } else {
                confirmTrade();
            }
            return random(1000, 2000);
        }

        if (isInTradeOffer()) {
            for (int i = 0; i < items.length; ++i) {
                int index = getInventoryIndex(items[i]);
                if (!traded_items[i] && index != -1) {
                    offerItemTrade(index, 1);
                    traded_items[i] = true;
                    return random(600, 1000);
                }
            }
            if ((System.currentTimeMillis() - trade_started) > 15000L) {
                declineTrade();
                blocked_players.add(current_target_player);
            } else {
                acceptTrade();
            }
            return random(600, 900);
        } else {
            trade_started = System.currentTimeMillis();
            Arrays.fill(traded_items, false);
        }

        if (getInventoryCount(items) <= 0) {
            int[] npc = getNpcByIdNotTalk(BANKERS);
            if (npc[0] != -1) {
                talkToNpc(npc[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(1000, 2000);
        }

        Iterator<String> it = trade_requests.keySet().iterator();
        while (it.hasNext()) {
        	String name = it.next();
        	if (System.currentTimeMillis() > (trade_requests.get(name) + 3000L)) {
        		it.remove();
                continue;
        	}
            int[] player = getPlayerByName(name);
            if (player[0] == -1 || distanceTo(player[1], player[2]) > 10) {
                it.remove();
                continue;
            }
            current_target_player = name;
            sendTradeRequest(getPlayerPID(player[0]));
            it.remove();
            return random(1500, 2500);
        }

        if (System.currentTimeMillis() > next_message) {
        	setTypeLine(_getMessage());
        	next_message = Long.MAX_VALUE;
        }
        if (next() && next_message == Long.MAX_VALUE) {
            next_message = System.currentTimeMillis() + random(5000, 7000);
        }
        return 0;
    }

    @Override
    public void onServerMessage(String str)
    {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = System.currentTimeMillis();
        } else if (str.contains("don't have room")) {
            close_bank = true;
        }
    }

    @Override
    public void onTradeRequest(String str)
    {
    	if (!blocked_players.contains(str)) {
    		trade_requests.put(str, System.currentTimeMillis());
    	}
    }

    private String _getMessage()
    {
        if ((++message_ptr) >= messages.length) {
            message_ptr = 0;
        }
        return messages[message_ptr];
    }
}
