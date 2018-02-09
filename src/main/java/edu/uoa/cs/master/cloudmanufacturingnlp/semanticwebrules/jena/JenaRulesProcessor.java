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
package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules.jena;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.dictionary.DictionaryService;
import edu.uoa.cs.master.cloudmanufacturingnlp.nlp.NlpHelper;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class JenaRulesProcessor {

	/** triples, action, subject, and object of a natural sentence */
	private final Map<String, Map<String, String>> triples;
	private final String action;
	private String subject;
	private String object;

	private final DictionaryService dictionaryService = new DictionaryService();
	private final NlpHelper nlpHelper = new NlpHelper(this.dictionaryService);
	private final JenaRulesBuilder jenaRulesBuilder = new JenaRulesBuilder(this.dictionaryService);

	/** container for parsing Jena rule */
	private final StringBuffer jenaRule = new StringBuffer(1024);

	/**
	 * Constructor.
	 *
	 * @param triples
	 * @param action
	 */
	public JenaRulesProcessor(final Map<String, Map<String, String>> triples, final String action) {
		this.triples = triples;
		this.action = action;
	}

	/**
	 * Assemble the Jena rule based on the parsed triples of input natural language rule.
	 *
	 * <pre>
	 * 	Company-B	Share resources with NZ-based companies
	 * 	=>
	 * 	[(?x cm:have_access_to ?y) <- (?x rdf:type gr:Organization), (?x gr:location ?z), (?z cm:country ?cnt), equal(?cnt, "New Zealand"),
	 * 								  (?org cm:hasRes ?y), (?org gr:name ?n), equal(?n, "B")]
	 * </pre>
	 *
	 * @return String[]
	 */
	public String[] assembleRule() {

		// make sure "subject" and "object" is not null
		validateTriples();

		// assemble head term and body terms
		assembleRuleHeadTerm();
		assembleBodyTerms();

		return finaliseRule();
	}

	private void validateTriples() {

		this.subject = this.nlpHelper.lookupDep(this.triples, this.action, Constants.Nlp.NSUBJ);
		if (StringUtils.isEmpty(this.subject)) {
			System.out.println(Constants.ErrMsg.NO_SUBJ);
			throw new RuntimeException(Constants.ErrMsg.NO_SUBJ);
		}

		this.object = this.nlpHelper.lookupDep(this.triples, this.action, Constants.Nlp.DOBJ);
		if (StringUtils.isEmpty(this.object)) {
			System.out.println(Constants.ErrMsg.NO_OBJ);
			throw new RuntimeException(Constants.ErrMsg.NO_OBJ);
		}
	}

	/**
	 * Assemble the head term (conclusion) of the Jena rule.
	 *
	 * The action "Share" indicates two aspects: 1) someone has information 2) others can access that information. In this case, we assume
	 * "Share" is the only action.
	 *
	 * e.g. (?x cm:have_access_to ?y)
	 *
	 */
	private void assembleRuleHeadTerm() {
		this.jenaRulesBuilder.withRuleHeadTerm(this.action);
	}

	/**
	 * Assemble the body terms (premises) of Jena rule.
	 *
	 * e.g. (?x rdf:type gr:Organization), (?x gr:location ?z), (?z cm:country ?cnt), equal(?cnt, "New Zealand"), (?org cm:hasRes ?y), (?org
	 * gr:name ?n), equal(?n, "B")
	 *
	 */
	private void assembleBodyTerms() {

		// who has resource, e.g. (?org cm:hasRes ?y), (?org gr:name ?n), equal(?n, "B")
		this.jenaRulesBuilder.withResourceOwner(Tools.removeDashSuffix(this.subject));

		// the type of resource
		assembleResourceType();

		// assemble the conditions applied to the user who has access to resource
		assembleAccessConditions();

		this.jenaRule.append(this.jenaRulesBuilder.build());
	}

	/**
	 * Assemble the resource type.
	 *
	 * e.g. (?y rdf:type cm:MachiningResource)
	 *
	 */
	private void assembleResourceType() {

		// general resource
		if (this.dictionaryService.isSynonym(this.object, Constants.Ontology.RESOURCE)) {
			final String obj_amod = this.nlpHelper.lookupDep(this.triples, this.object, Constants.Nlp.AMOD);
			// hard resources
			if (!StringUtils.isEmpty(obj_amod)) {
				this.jenaRulesBuilder.withResouceType(obj_amod);
			}
			// machining resources
			else {
				final String obj_nn = this.nlpHelper.lookupDep(this.triples, this.object, Constants.Nlp.NN);
				this.jenaRulesBuilder.withResouceType(obj_nn);
			}
		}
		// assume the resource is specific, e.g., OKUMA_MP-46V
		else {
			final String obj_nn = this.nlpHelper.lookupDep(this.triples, this.object, Constants.Nlp.NN);
			String resName = Tools.removeDashSuffix(this.object);

			if (!StringUtils.isBlank(obj_nn)) {
				resName = Tools.removeDashSuffix(obj_nn) + "_" + resName;
			}
			this.jenaRulesBuilder.withResouceName(resName);
		}
	}

	/**
	 * Access conditions include: e.g. "within", "with", "in".
	 *
	 */
	private void assembleAccessConditions() {

		// usually all the partner should be an organization
		this.jenaRulesBuilder.withPartnerGeneralInformation();

		// conditions such as the geography from where the resource can be accessed
		final String action_prep = this.nlpHelper.lookupDep(this.triples, this.action, Constants.Nlp.PREP);
		if (!StringUtils.isEmpty(action_prep)) {
			final String accessLimit_pobj = this.nlpHelper.lookupDep(this.triples, action_prep, Constants.Nlp.POBJ);
			if (!StringUtils.isEmpty(accessLimit_pobj)) {
				assemblePartnerSpecificInformation(action_prep, accessLimit_pobj);
			}
		}
		// sometimes with is parsed as the prep of object rather than that of the action
		// e.g. CompanyF shares OKUMA MP-46V with private limited companies.
		else {
			final String object_prep = this.nlpHelper.lookupDep(this.triples, this.object, Constants.Nlp.PREP);
			if (!StringUtils.isEmpty(object_prep)) {
				final String accessLimit_pobj = this.nlpHelper.lookupDep(this.triples, object_prep, Constants.Nlp.POBJ);
				if (!StringUtils.isEmpty(accessLimit_pobj)) {
					assemblePartnerSpecificInformation(object_prep, accessLimit_pobj);
				}
			}
		}
	}

	/**
	 * Assemble the allowed object. e.g. "within its own company", "with NZ-based companies", "in the public cloud".
	 *
	 * 1. assemble partner identity 2. assemble partner credit rating 3. assemble partner operation years
	 *
	 * @param allowedObj
	 */
	private void assemblePartnerSpecificInformation(final String actionPrep, final String allowedObj) {

		if (!this.dictionaryService.isSynonym(actionPrep, Constants.Nlp.WITH)) {
			return;
		}

		final String allowedObj_amod = this.nlpHelper.lookupDep(this.triples, allowedObj, Constants.Nlp.AMOD);

		// share resource within own company
		if (this.dictionaryService.isSynonym(allowedObj_amod, Constants.Nlp.OWN)) {
			this.jenaRulesBuilder.withPartnerName(Tools.removeDashSuffix(this.subject));
		}
		// share resource with the public cloud
		else if (this.dictionaryService.isSynonym(allowedObj_amod, Constants.Nlp.PUBLIC)) {
			// do nothing;
		}
		// share resource with specific companies
		else if (this.dictionaryService.isSynonym(allowedObj_amod, Constants.Nlp.SPECIFIC)) {
			assembleCompaniesSpecific(allowedObj);
		}
		// share resource with location based companies
		else if (!StringUtils.isEmpty(allowedObj_amod) && allowedObj_amod.contains(Constants.Nlp.BASED)) {
			final String baseLocation = this.nlpHelper.getBaseLocation(allowedObj_amod);

			if (!StringUtils.isEmpty(baseLocation)) {
				this.jenaRulesBuilder.withPartnerBaseLocation(baseLocation, allowedObj);
			}
		}
		// share resource with other companies
		else {
			final List<String> allowedObj_amods = this.nlpHelper.lookupDeps(this.triples, allowedObj,
					Constants.Nlp.AMOD);

			// e.g. private limited company
			if (allowedObj_amods != null) {
				String amods = "";
				for (final String amod : allowedObj_amods) {
					amods += Tools.removeDashSuffix(amod) + " ";
				}
				amods += Tools.removeDashSuffix(allowedObj);
				this.jenaRulesBuilder.withPartnerEntityType(amods, allowedObj);
			}
		}

		// share resource with companies with particular credit rating
		final String allowedObj_vmod = this.nlpHelper.lookupDep(this.triples, allowedObj, Constants.Nlp.VMOD);

		if (this.dictionaryService.isSynonym(allowedObj_vmod, Constants.Nlp.HAVING)) {
			final String having_dobj = this.nlpHelper.lookupDep(this.triples, allowedObj_vmod, Constants.Nlp.DOBJ);
			if (this.dictionaryService.isSynonym(having_dobj, Constants.Nlp.RATING)) {
				assembleCompaniesCreditRating(having_dobj);
			}
		}

		// share resource with companies with particular operation years
		final String allowedObj_prep = this.nlpHelper.lookupDep(this.triples, allowedObj, Constants.Nlp.PREP);

		if (this.dictionaryService.isSynonym(allowedObj_prep, Constants.Nlp.IN)) {
			final String prep_pobj = this.nlpHelper.lookupDep(this.triples, allowedObj_prep, Constants.Nlp.POBJ);
			if (this.dictionaryService.isSynonym(prep_pobj, Constants.Nlp.OPERATION)) {
				assembleCompaniesOperationYears(prep_pobj);
			}
		}
	}

	/**
	 * Assemble the companies that only specific companies are allowed to access the resources. e.g. specific companies, i.e. Company A, B
	 * and E
	 *
	 * @param allowedObj
	 */
	private void assembleCompaniesSpecific(final String allowedObj) {
		final List<String> deps = this.nlpHelper.lookupDeps(this.triples, allowedObj, Constants.Nlp.CONJ);

		if (deps != null) {
			if (deps.size() == 1) {
				final String name = Tools.removeDashSuffix(deps.get(0));
				this.jenaRulesBuilder.withPartnerName(name);

			} else {
				for (final String dep : deps) {
					if (Tools.doesMatch(dep, "i.e.")) {
						continue;
					}

					final String name = Tools.removeDashSuffix(dep);
					this.jenaRulesBuilder.addPartnerName(name, allowedObj);
				}
			}
		}
	}

	/**
	 * Assemble the companies with particular credit rating value. e.g. credit rating higher than 8.0
	 *
	 * @param rating
	 */
	private void assembleCompaniesCreditRating(final String rating) {
		// rating_amod: e.g. "higher", "lower"
		final String rating_amod = this.nlpHelper.lookupDep(this.triples, rating, Constants.Nlp.AMOD);
		if (!StringUtils.isEmpty(rating_amod)) {

			final String rating_amod_prep = this.nlpHelper.lookupDep(this.triples, rating_amod, Constants.Nlp.PREP);
			if (this.dictionaryService.isSynonym(rating_amod_prep, "than")) {

				String than_value = "";
				final String than_pobj = this.nlpHelper.lookupDep(this.triples, rating_amod_prep,
						Constants.Nlp.POBJ);

				if (!StringUtils.isEmpty(than_pobj)) {
					than_value = Tools.removeDashSuffix(than_pobj);
				}

				if (this.dictionaryService.isSynonym(rating_amod, "higher")) {
					this.jenaRulesBuilder.withCreditGrade(rating,
							Constants.JenaRules.FACT_NATIVE_GREATER_THAN, than_value);

				} else if (this.dictionaryService.isSynonym(rating_amod, "lower")) {
					this.jenaRulesBuilder.withCreditGrade(rating, Constants.JenaRules.FACT_NATIVE_LESS_THAN,
							than_value);

				} else {
					this.jenaRulesBuilder.withCreditGrade(rating, Constants.JenaRules.FACT_NATIVE_EQUAL,
							than_value);
				}
			}
		}
	}

	/**
	 * Assemble the companies with particular operation years.
	 *
	 * @param operation
	 */
	private void assembleCompaniesOperationYears(final String operation) {
		final String operation_prep = this.nlpHelper.lookupDep(this.triples, operation, Constants.Nlp.PREP);

		if (this.dictionaryService.isSynonym(operation_prep, Constants.Nlp.FOR)) {
			final String prep_pobj = this.nlpHelper.lookupDep(this.triples, operation_prep, Constants.Nlp.POBJ);

			if (this.dictionaryService.isSynonym(prep_pobj, Constants.Nlp.YEARS)) {
				final String yearsNum = this.nlpHelper.lookupDep(this.triples, prep_pobj, Constants.Nlp.NUM);

				if (!StringUtils.isEmpty(yearsNum)) {
					final String comparison = this.nlpHelper.getComparison(this.triples, yearsNum);
					final String num = Tools.removeDashSuffix(yearsNum);

					if (this.dictionaryService.isSynonym(comparison, Constants.Nlp.MORE)) {
						this.jenaRulesBuilder.withOperationYears(prep_pobj,
								Constants.JenaRules.FACT_NATIVE_GREATER_THAN, num);

					} else if (this.dictionaryService.isSynonym(comparison, Constants.Nlp.LESS)) {
						this.jenaRulesBuilder.withOperationYears(prep_pobj,
								Constants.JenaRules.FACT_NATIVE_LESS_THAN, num);

					} else {
						this.jenaRulesBuilder.withOperationYears(prep_pobj,
								Constants.JenaRules.FACT_NATIVE_EQUAL, num);
					}
				}
			}
		}
	}

	/**
	 * Finalise the Jena rule output. Specifically, split the disjunction factors, which is not supported in Jena, into multiple rules.
	 *
	 * @return
	 */
	private String[] finaliseRule() {
		String[] jenaRule_ary = null;

		// check the "or" disjunction facts and split them if they exist
		if (this.jenaRule.indexOf(Constants.Param.JENA_DISJUNCTION) != -1) {

			// parse the disjuntion part of the jena rule
			final int start = this.jenaRule.indexOf(Constants.Param.JENA_DISJUNCTION_START);
			final int end = this.jenaRule.indexOf(Constants.Param.JENA_DISJUNCTION_END);

			final String jenaRule_or_str = this.jenaRule.substring(
					start + Constants.Param.JENA_DISJUNCTION_START.length(), end);
			final String[] jenaRule_or_ary = jenaRule_or_str.split(Constants.Param.JENA_DISJUNCTION);
			final String jenaRule_main_str = this.jenaRule.replace(start,
					end + Constants.Param.JENA_DISJUNCTION_END.length(), "").toString();

			// recompile the jena rule using each of disjunction factors
			jenaRule_ary = new String[jenaRule_or_ary.length];

			for (int i = 0; i < jenaRule_or_ary.length; i++) {
				if (StringUtils.isEmpty(jenaRule_or_ary[i]) || StringUtils.isEmpty(jenaRule_or_ary[i].trim())) {
					continue;
				}

				final String tmp = jenaRule_main_str + jenaRule_or_ary[i];
				if (tmp.endsWith(", ")) {
					jenaRule_ary[i] = tmp.substring(0, tmp.lastIndexOf(", ")) + "]";
				} else {
					jenaRule_ary[i] = tmp + "]";
				}
			}

		} else {
			String tmp = this.jenaRule.toString();
			if (tmp.endsWith(", ")) {
				tmp = tmp.substring(0, tmp.lastIndexOf(", ")) + "]";
			}

			jenaRule_ary = new String[] { tmp };
		}

		return jenaRule_ary;
	}
}
