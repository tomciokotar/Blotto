import jade.proto.ContractNetResponder;
import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import jade.content.ContentElementList;

public class FindAgentResponder extends ContractNetResponder
{
	private int units;
	private BlottoPlayerAgent agent;
	
	public FindAgentResponder(Agent a, MessageTemplate mt)
	{
		super(a, mt);
		agent = (BlottoPlayerAgent) a;
		registerHandleAcceptProposal(new PlayBlottoInitiator(agent,
									new ACLMessage(ACLMessage.REQUEST), this));
	}
	
	public int getUnits()
	{
		return units;
	}
	
	protected ACLMessage handleCfp(ACLMessage msg)
	{
		agent.newResponder();
		int minUnits = agent.extractCfpMinUnits(msg);
		units = agent.getUnits();
		
		ACLMessage reply = msg.createReply();
		
		//System.out.println(agent.getLocalName() + ": " + msg);
		
		if (units == 0 || units < minUnits) {
			reply.setPerformative(ACLMessage.REFUSE);
			System.out.println(agent.getLocalName() + ": refuse sent.");
		}
		else {
			agent.newBattle();
			agent.takeUnits(units);
			
			reply.setPerformative(ACLMessage.PROPOSE);
			
			ContentElementList content = new ContentElementList();
			content.add(agent.extractCfpAction(msg));
			content.add(new CommittedUnits(units));
			
			agent.fillMessageContent(reply, content);
			
			System.out.println(agent.getLocalName() + ": propose sent.");
		}
		
		return reply;
	}
	
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject)
	{
		agent.giveBackUnits(units);
		agent.battleFinished();
		System.out.println(agent.getLocalName() + ": proposed has been rejected.");
	}
}
