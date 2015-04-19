public class BattleResult
{
	private String myName, partnersName;
	private int result;
	
	public BattleResult(String a, String b, int res)
	{
		myName = a;
		partnersName = b;
		result = res;
	}
	
	public String getMyName()
	{
		return myName;
	}
	
	public String getPartnersName()
	{
		return partnersName;
	}
	
	public int getResult()
	{
		return result;
	}
}
