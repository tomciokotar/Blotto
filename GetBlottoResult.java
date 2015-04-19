import jade.content.AgentAction;

public class GetBlottoResult implements AgentAction
{
	private Allocation allocation;
	
	public GetBlottoResult()
	{
		allocation = null;
	}
	
	public GetBlottoResult(Allocation a)
	{
		allocation = a;
	}
	
	public void setAllocation(Allocation a)
	{
		allocation = a;
	}
	
	public Allocation getAllocation()
	{
		return allocation;
	}
		
}

