import jade.content.onto.Ontology;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

public class BlottoOntology extends Ontology
{
     public static final String NAME = "blotto-ontology";

     public static final String PLAY_BLOTTO = "play-blotto";
     public static final String COMMITTED_UNITS = "committed-units";
     public static final String UNITS = "units";
     public static final String GET_BLOTTO_RESULT = "get-blotto-result";
     public static final String ALLOCATION = "allocation";
     public static final String ASSIGNMENT = "assignment";
     public static final String BLOTTO_RESULT = "blotto-result";
     public static final String RESULT = "result";

     private static Ontology instance = new BlottoOntology();
     
     public static Ontology getInstance()
     {
         return instance;
     }
     
     private BlottoOntology()
     {
         super(NAME, BasicOntology.getInstance());

         try {
             add(new AgentActionSchema(PLAY_BLOTTO), PlayBlotto.class);
             
             PredicateSchema ps = new PredicateSchema(COMMITTED_UNITS);
             add(ps, CommittedUnits.class);
             ps.add(UNITS, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
             
             ps = new PredicateSchema(BLOTTO_RESULT);
             add(ps, BlottoResult.class);
             ps.add(RESULT, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
             
             ConceptSchema cs = new ConceptSchema(ALLOCATION);
             add(cs, Allocation.class);
             cs.add(ASSIGNMENT, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), 5, 5, "sequence");
             
             AgentActionSchema as = new AgentActionSchema(GET_BLOTTO_RESULT);
             add(as, GetBlottoResult.class);
             as.add(ALLOCATION, (ConceptSchema) getSchema(ALLOCATION));
		 }
		 catch (OntologyException e) {
			 e.printStackTrace();
		 }
	}
}
