import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
//import java.util.Locale;

public final class S_ChatLogger extends Script {

	/*private long move_time;
	private boolean idle_move_dir;*/
	private static final String URL = "http://sakaki:933/chat/add";
	private static final String KEY = "srw03Cf51VImqfWJBY89PAyZ";

	public S_ChatLogger(Extension ex)
	{
		super(ex);
	}

	@Override
	public void init(String params)
	{
		//move_time = -1L;
	}

	@Override
	public int main()
	{
		/*if (move_time != -1L) {
			return _idleMove();
		}*/
		return 0;
	}

	/*private boolean _idleMoveP1()
	{
		int x = getX();
		int y = getY();
		if (isReachable(x + 1, y)) {
			walkTo(x + 1, y);
			return true;
		}
		if (isReachable(x, y + 1)) {
			walkTo(x, y + 1);
			return true;
		}
		if (isReachable(x + 1, y + 1)) {
			walkTo(x + 1, y + 1);
			return true;
		}
		return false;
	}

	private boolean _idleMoveM1()
	{
		int x = getX();
		int y = getY();
		if (isReachable(x - 1, y)) {
			walkTo(x - 1, y);
			return true;
		}
		if (isReachable(x, y - 1)) {
			walkTo(x, y - 1);
			return true;
		}
		if (isReachable(x - 1, y - 1)) {
			walkTo(x - 1, y - 1);
			return true;
		}
		return false;
	}

	private int _idleMove()
	{
		if (System.currentTimeMillis() >= move_time) {
			if (idle_move_dir) {
				if (!_idleMoveP1()) {
					_idleMoveM1();
				}
			} else {
				if (!_idleMoveM1()) {
					_idleMoveP1();
				}
			}
			idle_move_dir = !idle_move_dir;
			move_time = -1L;
			return random(1500, 2500);
		}
		return 0;
	}*/

	private static byte[] load(String dest)
		throws IOException
	{
		final URL url = new URL(dest);
		final URLConnection connect = url.openConnection();
		connect.addRequestProperty("Accept",
			"text/html, application/xhtml+xml, */*");
		connect.addRequestProperty("Accept-Language", "en-US");
		connect.addRequestProperty("User-Agent", "ChatLogger");
		connect.setConnectTimeout(5000);
		try (InputStream in = connect.getInputStream()) {
			int read = 0;
			final int block_size = 4096;
			byte b[] = new byte[block_size];
			for (;;) {
				final int r = in.read(b, read, block_size);
				if (r == -1) {
					break;
				}
				read += r;
				b = Arrays.copyOf(b, b.length + block_size);
			}
			return Arrays.copyOf(b, read);
		}
	}

	@Override
	public void onServerMessage(String str)
	{
		/*if (str.toLowerCase(Locale.ENGLISH).contains("standing")) {
			move_time = System.currentTimeMillis() + random(800, 2500);
		}*/
	}

	@Override
	public void onChatMessage(String msg, String name, boolean mod,
		boolean admin)
	{
		try {
			name = URLEncoder.encode(name, "UTF-8");
			msg = URLEncoder.encode(msg, "UTF-8");
			String str = new String(load(String.format("%s?k=%s&n=%s&m=%s",
				URL, KEY, name, msg)), "UTF-8");
			if (!str.equals("OK")) {
				System.out.println(String
					.format("Message from server: %s", str));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}