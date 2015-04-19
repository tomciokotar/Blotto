import jade.content.Predicate;

public class CommittedUnits implements Predicate
{
	private int units;
	
	public CommittedUnits()
	{
		units = 0;
	}
	
	public CommittedUnits(int a)
	{
		units = a;
	}
	
	public void setUnits(int a)
	{
		units = a;
	}
	
	public int getUnits()
	{
		return units;
	}
}
