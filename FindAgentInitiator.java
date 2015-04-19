import jade.proto.ContractNetInitiator;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.content.ContentElementList;
import jade.content.onto.basic.Action;
import jade.core.behaviours.WakerBehaviour;
import java.util.Vector;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

public class FindAgentInitiator extends ContractNetInitiator
{
	private int units;
	private BlottoPlayerAgent agent;
	
	public FindAgentInitiator(Agent a, ACLMessage cfp)
	{
		super(a, cfp);
		agent = (BlottoPlayerAgent) a;
	}
	
	protected Vector prepareCfps(ACLMessage cfp)
	{
		agent.setOtherAgents();
		List<AID> receivers = agent.getOtherAgents();
		Vector cfps = new Vector<ACLMessage>();
		
		cfp.setLanguage("fipa-sl");
        cfp.setOntology("blotto-ontology");
        cfp.setProtocol("fipa-contract-net");
        cfp.addReplyTo(agent.getAID());
		
		for (AID receiver: receivers) {
			ACLMessage msg = (ACLMessage) cfp.clone();
			
			msg.addReceiver(receiver);
			msg.setReplyByDate(new Date(Calendar.getInstance().getTime().getTime() + 5000));
			
			ContentElementList content = new ContentElementList();
			content.add(new Action(receiver, new PlayBlotto()));
			content.add(new CommittedUnits(1));
			
			agent.fillMessageContent(msg, content);
			cfps.add(msg);
		}
		
		System.out.println(agent.getLocalName() + ": wysłano CFP.");
		
		return cfps;
	}
	
	protected void handlePropose(ACLMessage msg, Vector acceptances)
	{		
		if (msg.getPerformative() == ACLMessage.PROPOSE) {
			ACLMessage reply = msg.createReply();
			
			//System.out.println(agent.getLocalName() + ": " + msg);
			
			units = agent.getUnits();
			
			if (units == 0 || agent.getArbitrator() == null) {
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				System.out.println(agent.getLocalName() + ": poszedł reject.");
			}
			else {
				agent.newBattle();
				agent.takeUnits(units);
				
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					
				ContentElementList content = new ContentElementList();
				content.add(agent.extractProposeAction(msg));
				content.add(new CommittedUnits(units));
				
				agent.fillMessageContent(reply, content);
				
				System.out.println(agent.getLocalName() + ": poszedł accept.");
			}
			
			acceptances.add(reply);
		}
	}
	
	protected void handleFailure(ACLMessage msg)
	{
		agent.giveBackUnits(units);
		agent.battleFinished();
		System.out.println(agent.getLocalName() + ": bitwa się nie udała.");
	}
	
	protected void handleInform(ACLMessage msg)
	{
		int res = agent.extractBlottoResult(msg);
		//agent.addResult(agent.getLocalName(), msg.getSender().getLocalName(), res);
		System.out.println(agent.getLocalName() + " i " + msg.getSender().getLocalName() + ": " + res);
		agent.battleFinished();
		
		System.out.println(agent.getLocalName() + ": wynik bitwy = " + res);
	}
	
	public int onEnd()
	{
		if (agent.getUnits() == 0 && agent.getRemainingBattles() == 0) {
			System.out.println(agent.getLocalName() + ": KONIEC.");
			agent.doDelete();	
		}
		else {
			System.out.println(agent.getLocalName() + ": następny CNP będzie leciał.");
			agent.addBehaviour(new WakerBehaviour(agent, 1000) {
				protected void onWake() {
					agent.addBehaviour(new FindAgentInitiator(myAgent, new ACLMessage(ACLMessage.CFP)));
				}
			});
		}
		
		return 0;
	}
}
