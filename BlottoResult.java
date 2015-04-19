import jade.content.Predicate;

public class BlottoResult implements Predicate
{
	private int result;
	
	public BlottoResult()
	{
		result = 0;
	}
	
	public BlottoResult(int a)
	{
		result = a;
	}
	
	public void setResult(int a)
	{
		result = a;
	}
	
	public int getResult()
	{
		return result;
	}
}

