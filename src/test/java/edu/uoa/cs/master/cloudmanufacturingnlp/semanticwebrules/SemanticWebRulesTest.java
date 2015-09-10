/*
 * Copyright (c) 2015 The University of Auckland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Ping Zhao (pzha291@aucklanduni.ac.nz)
 * Created: 2015-06-10
 * Last Updated: --
 */
package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.util.PrintUtil;

import edu.uoa.cs.master.cloudmanufacturingnlp.jena.JenaInfModelManager;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

/**
 * @author pingz
 *
 */
public abstract class SemanticWebRulesTest {
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

		testRules(expectedValue, infModel);
	}

	abstract protected void testRules(final Map<String, Integer> expectedValue, final InfModel infModel);
}
