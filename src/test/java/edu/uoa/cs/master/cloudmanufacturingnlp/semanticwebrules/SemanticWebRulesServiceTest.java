package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.util.PrintUtil;

import edu.uoa.cs.master.cloudmanufacturingnlp.jena.JenaInfModelManager;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

public class SemanticWebRulesServiceTest {

	private SemanticWebRulesService semanticWebRulesService;

	@Before
	public void setup() {
		Constants.LOCAL_BASE = Constants.FilePath.LOCAL_BASE_JAVA;
		this.semanticWebRulesService = new SemanticWebRulesService();
	}

	@Test
	public void inference_ontologies_using_parsed_rules() {

		for (final Map<String, Map<String, Integer>> testCase : TestCases.cases) {
			final String naturalLanguageRule = testCase.keySet().iterator().next();
			final Map<String, Integer> expectedValue = testCase.get(naturalLanguageRule);

			final String[] semanticRules = this.semanticWebRulesService.process(naturalLanguageRule);
			verifyRules(semanticRules, expectedValue);
		}
	}

	private void verifyRules(final String[] semanticRules, final Map<String, Integer> expectedValue) {
		assertThat(semanticRules).isNotEmpty();

		PrintUtil.registerPrefix("manuservice", Constants.Ontology.NS_MC);
		final InfModel infModel = JenaInfModelManager.getInstance().createInfModelByRules(semanticRules);

		final int statementNum = JenaInfModelManager.getInstance().inferenceModel(infModel, "", Constants.Ontology.NS_MC + "hasAccessTo",
				Constants.Ontology.NS_MC + expectedValue.keySet().iterator().next());

		assertThat(statementNum).isSameAs(expectedValue.values().iterator().next());
	}
}
