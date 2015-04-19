import jade.content.Concept;
import jade.util.leap.List;

public class Allocation implements Concept
{
	private List assignment;
	
	public Allocation()
	{
		assignment = null;
	}
	
	public Allocation(List a)
	{
		assignment = a;
	}
	
	public void setAssignment(List a)
	{
		assignment = a;
	}
	
	public List getAssignment()
	{
		return assignment;
	}
}
