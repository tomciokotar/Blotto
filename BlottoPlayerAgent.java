import jade.core.Agent;
import jade.core.AID;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import jade.proto.ContractNetResponder;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.onto.basic.Action;
import jade.core.behaviours.WakerBehaviour;

public class BlottoPlayerAgent extends Agent
{
	private int units, score = 0, responders = 0;
	private int remainingBattles = 0;
	private Vector<BattleResult> battles = new Vector<BattleResult>();
	private AID arbitrator = null;
	private List<AID> otherAgents;
	
	public int getUnits()
	{
		return units;
	}
	
	public AID getArbitrator()
	{
		return arbitrator;
	}
	
	public List<AID> getOtherAgents()
	{
		return otherAgents;
	}
	
	public int getRemainingBattles()
	{
		return remainingBattles;
	}
	
	public void newBattle()
	{
		remainingBattles++;
	}
	
	public void battleFinished()
	{
		remainingBattles--;
	}
	
	public void takeUnits(int i)
	{
		units -= i;
	}
	
	public void giveBackUnits(int i)
	{
		units += i;
	}
	
	public void addResult(String a, String b, int res)
	{
		battles.add(new BattleResult(a, b, res));
		score += res;
	}
	
	private void registerInDF()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Blotto");
		sd.setName(getLocalName() + "-Blotto");
		
		dfd.addServices(sd);
		dfd.addLanguages("fipa-sl");
		dfd.addOntologies("blotto-ontology");
		dfd.addProtocols("fipa-contract-net");
		
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	private void setArbitrator()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Blotto-Play");
		
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] res = DFService.search(this, dfd);
			if (res.length > 0)
				arbitrator = res[0].getName();
			else
				System.out.println(getLocalName() + ": brak sędziego.");
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public void setOtherAgents()
	{
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Blotto");
        dfd.addServices(sd);
        
        otherAgents = new ArrayList<AID>();
        
        try {
			for (DFAgentDescription desc: DFService.search(this, dfd))
				if (!desc.getName().equals(getAID()))
						otherAgents.add(desc.getName());
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public void newResponder()
	{
		if (responders < 20) {
			responders++;
			addBehaviour(new FindAgentResponder(this,
						ContractNetResponder.createMessageTemplate("fipa-contract-net")));
			
			System.out.println(getLocalName() + ": nowy responder - nr " + responders);
		}
	}
	
	protected void setup()
	{	
		units = Integer.parseInt(getArguments()[0].toString());
		
		registerInDF();
		setArbitrator();
		setOtherAgents();
		
		getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(BlottoOntology.getInstance());
		
		addBehaviour(new WakerBehaviour(this, 5000) {
				protected void onWake() {
					addBehaviour(new FindAgentInitiator(myAgent, new ACLMessage(ACLMessage.CFP)));
				}
			});
		
		newResponder();
		
		System.out.println(getLocalName() + ": poszedł setup.");
	}
	
	protected void takeDown()
	{
		try {
			DFService.deregister(this);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		
		/*System.out.println(getLocalName() + " - wyniki:");
		for (BattleResult res: battles)
			System.out.println(res.getMyName() + " i " + res.getPartnersName()
											   + ": " + res.getResult() + " pkt");
											   
		System.out.println("Sumarycznie: " + score);*/
	}
	
	public Action extractCfpAction(ACLMessage msg)
	{
		return extractProposeAction(msg);
	}
	
	public Action extractProposeAction(ACLMessage msg)
	{
		Action action = null;
		
		try {
			action = (Action) ((ContentElementList) extractMessage(msg)).get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return action;
	}
	
	public int extractBlottoResult(ACLMessage msg)
	{
		int res = 0;
		
		try {
			res = ((BlottoResult) extractMessage(msg)).getResult();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public int extractCommittedUnits(ACLMessage msg)
	{
		return extractCfpMinUnits(msg);
	}
	
	public int extractCfpMinUnits(ACLMessage msg)
	{
		int minUnits = 0;
		
		try {
			minUnits = ((CommittedUnits) ((ContentElementList) extractMessage(msg)).get(1)).getUnits();
		}
		catch (Exception e) {}
		
		return minUnits;
	}
	
	protected ContentElement extractMessage(ACLMessage msg)
	{
		ContentElement content = null;
		
		try {
			content = getContentManager().extractContent(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return content;
	}
	
	public void fillMessageContent(ACLMessage msg, ContentElement content)
	{
		try {
			getContentManager().fillContent(msg, content);
		}
		catch (CodecException | OntologyException e) {
			e.printStackTrace();
		}
	}	
}
