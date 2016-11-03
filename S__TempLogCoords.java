public final class S__TempLogCoords extends Script {
    
    private int last_x;
    private int last_y;
    private long last_log;

    public S__TempLogCoords(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
    }

    @Override
    public int main() {
        int x = getX();
        int y = getY();
        if (distanceTo(last_x, last_y) >= 5 || ((System.currentTimeMillis() - last_log) > 5000L && last_x != x && last_y != y)) {
            System.out.println("new Point(" + x + ", " + y + "),");
            last_x = x;
            last_y = y;
            last_log = System.currentTimeMillis();
        }
        return 0;
    }

    @Override
    public void paint() {
    }
    
    @Override
    public void onServerMessage(String str) {
        
    }
}