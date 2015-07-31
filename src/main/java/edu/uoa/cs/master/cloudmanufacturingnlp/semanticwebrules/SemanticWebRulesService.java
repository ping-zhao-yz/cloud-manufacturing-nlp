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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.nlp.StanfordDependencies;
import edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules.jena.JenaRulesProcessor;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

public class SemanticWebRulesService {

	private String action;

	/**
	 * Parse the natural language rule and use the parsed elements/tags to generate the Jena rule.
	 * 
	 * @param naturalLanguageRule
	 * @return String[]
	 */
	public String[] process(String naturalLanguageRule) {

		String preprocessedRule = preprocessRule(naturalLanguageRule);

		// get the triples of a natural sentence in the format <gov, <dep, reln>>, e.g. shares-3={companyC-2=nsubj}
		Map<String, Map<String, String>> triples = new HashMap<String, Map<String, String>>();
		this.action = StanfordDependencies.getInstance().parseNaturalLanguage(triples, preprocessedRule);

		if (isParsedLanguageValid()) {
			return new JenaRulesProcessor(triples, this.action).assembleRule();
		}

		return null;
	}

	/**
	 * Standardize the input natural language rule.
	 * 1. Add "." at the end.
	 * "CompanyA shares resources within its own company" => "CompanyA shares resources within its own company."
	 * 
	 * 2. Add "The" at the beginning
	 * "CompanyA shares resources within its own company." => "The CompanyA shares resources within its own company."
	 * 
	 * @param naturalLanguageRule
	 * @return
	 */
	private String preprocessRule(String naturalLanguageRule) {

		String preprocessedRule = naturalLanguageRule.trim();

		// remove the beginning and ending character "\"" in case it is carried from the browser URL
		if (preprocessedRule.startsWith("\"") && preprocessedRule.endsWith("\"")) {
			preprocessedRule = preprocessedRule.substring(1, preprocessedRule.length() - 1);
		}

		// add "." at the end of the sentence
		if (!preprocessedRule.endsWith(".")) {
			preprocessedRule += ".";
		}

		// add "The" at the beginning
		if (!preprocessedRule.toLowerCase().startsWith("the")) {
			preprocessedRule = "The " + preprocessedRule;
		}

		return preprocessedRule;
	}

	/**
	 * Verify the input rule by checking the gov of nsubj, i.e. action.
	 * 
	 * @param action
	 * @param triples
	 * @return
	 */
	private boolean isParsedLanguageValid() {

		if (StringUtils.isEmpty(this.action)) {
			System.out.println(Constants.ErrMsg.NO_ACTION);
			throw new RuntimeException(Constants.ErrMsg.NO_ACTION);
		}

		return true;
	}

	public static void main(String[] args) {
		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyA shares resources within its own company.");
		//
		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyB shares resources with NZ-based companies.");
		//
		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyC shares soft resources within the public cloud;");
		//
		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyC shares hard resources with specific companies, i.e., companyA, companyB and companyE;");
		//
		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyD shares machining resources with companies having an credit rating higher than 8.0;");
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyD shares machining resources with companies having an credit rating higher than 8.0 (out of 10.0);");

		// passed
		// String[] jenaRule =
		// SemanticWebRulesService.getInstance().process("The companyE shares OKUMA MP-46V within the public cloud.");
		//
		// passed
		String[] jenaRule = new SemanticWebRulesService().process(
				"The companyF shares resources with private limited companies in operation for more than 10 years.");

		for (String rule : jenaRule) {
			System.out.println(rule);
		}
	}
}
