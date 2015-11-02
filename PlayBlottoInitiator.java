import jade.proto.SimpleAchieveREInitiator;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.DataStore;
import jade.util.leap.List;
import jade.util.leap.ArrayList;

public class PlayBlottoInitiator extends SimpleAchieveREInitiator
{
	private BlottoPlayerAgent agent;
	private FindAgentResponder responder;
	
	public PlayBlottoInitiator(Agent a, ACLMessage request, FindAgentResponder resp)
	{
		super(a, request);
		agent = (BlottoPlayerAgent) a;
		responder = resp;
	}
	
	protected ACLMessage prepareRequest(ACLMessage abc)
	{	
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		
		msg.addReceiver(agent.getArbitrator());
		msg.addReplyTo(agent.getAID());
	    msg.setLanguage("fipa-sl");
	    msg.setOntology("blotto-ontology");
	    msg.setProtocol("fipa-request");
	    
	    int units = responder.getUnits();
	    
	    ACLMessage acc = (ACLMessage) getDataStore().get(responder.ACCEPT_PROPOSAL_KEY);
	    units += agent.extractCommittedUnits(acc);
	    
	    List list = new ArrayList();
	    for (int i = 0; i < 4; i++)
			list.add(units/5);
		
		units -= 4*(units/5);
		list.add(units);
		
		agent.fillMessageContent(msg, new GetBlottoResult(new Allocation(list)));
		
		System.out.println(agent.getLocalName() + ": request sent to the arbiter.");
		
		return msg;
	}
	
	private void sendAnswer(ACLMessage msg, int performativeType)
	{
		DataStore ds = getDataStore();
		ACLMessage reply = ((ACLMessage) ds.get(responder.ACCEPT_PROPOSAL_KEY)).createReply();
		reply.setPerformative(performativeType);
		reply.setContent(msg.getContent());
		
		ds.put(responder.REPLY_KEY, reply);
	}
	
	protected void handleRefuse(ACLMessage msg)
	{
		sendAnswer(msg, ACLMessage.FAILURE);
		agent.giveBackUnits(responder.getUnits());
		agent.battleFinished();
		
		System.out.println(agent.getLocalName() + ": refuse from the arbiter.");
	}
	
	protected void handleFailure(ACLMessage msg)
	{
		sendAnswer(msg, ACLMessage.FAILURE);
		agent.giveBackUnits(responder.getUnits());
		agent.battleFinished();
		
		System.out.println(agent.getLocalName() + ": failure from the arbiter.");
	}
	
	protected void handleInform(ACLMessage msg)
	{
		sendAnswer(msg, ACLMessage.INFORM);
		/*agent.addResult(agent.getLocalName(), ((ACLMessage) getDataStore().get(
						responder.ACCEPT_PROPOSAL_KEY)).getSender().getLocalName(), 8);*/
		
		agent.battleFinished();
		
		System.out.println(agent.getLocalName() + ": inform from the arbiter.");
	}
}
