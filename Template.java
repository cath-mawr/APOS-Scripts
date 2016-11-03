public final class Template extends Script {

	public Template(Extension ex)
	{
		super(ex);
	}

	@Override
	public void init(String params)
	{
	}

	@Override
	public int main()
	{
		int[] n = getNpcById(780);
		if (n[0] != -1) {
			System.out.println(distanceTo(n[1], n[2]));
		}
		return 1000;
	}

	@Override
	public void paint()
	{
	}

	@Override
	public void onServerMessage(String str)
	{
	}
}
