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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.dictionary.DictionaryService;
import edu.uoa.cs.master.cloudmanufacturingnlp.jena.OntologyService;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class JenaRulesBuilder {

	private OntologyService ontologyService;

	private String ruleHeadTerm = null;
	private String hasResource = null;
	private String resourceType = null;
	private String resourceName = null;
	private String organization = null;
	private String accesserName = null;
	private List<String> accesserNameList = new ArrayList<>();
	private String accesserBaseLocation = null;
	private String accesserEntityType = null;
	private String accessCreditGrade = null;
	private String accessOperationYears = null;

	public JenaRulesBuilder(DictionaryService dictionaryService) {
		this.ontologyService = new OntologyService(dictionaryService);
	}

	/**
	 * e.g. (?x manuservice:hasAccessTo ?y)
	 * 
	 * @param action
	 * @return
	 */
	public JenaRulesBuilder withRuleHeadTerm(String action) {
		String objectProperty_action = this.ontologyService.lookupOntProperty(action);
		this.ruleHeadTerm = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", objectProperty_action, "?y");

		return this;
	}

	/**
	 * e.g. (?org manuservice:hasResource ?y), (?org manuservice:name 'B')
	 */
	public JenaRulesBuilder withHasResource(String subject) {
		String property_hasResource = this.ontologyService.lookupOntProperty(Constants.Ontology.HAS_RESOURCE);
		String fact_hasRes = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?org", property_hasResource, "?y");

		String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		String fact_name = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?org", property_name, subject);

		this.hasResource = fact_hasRes + ", " + fact_name;
		return this;
	}

	/**
	 * e.g. (?y rdf:type manuservice:SoftResource)
	 * 
	 * @param resourceType
	 * @return
	 */
	public JenaRulesBuilder withResouceType(String resourceType) {
		if (StringUtils.isBlank(resourceType)) {
			resourceType = Constants.Ontology.RESOURCE;
		}
		String class_resourceType = this.ontologyService.lookupOntClass(resourceType);

		this.resourceType = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?y", Constants.Ontology.RDF_TYPE_USING_PREFIX,
				class_resourceType);
		return this;
	}

	/**
	 * e.g. (?y manuservice:name 'R_AutoCAD1_F')
	 * 
	 * @param resourceName
	 * @return
	 */
	public JenaRulesBuilder withResouceName(String resourceName) {
		String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		this.resourceName = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?y", property_name, resourceName);
		return this;
	}

	/**
	 * Factor e.g. "(?x rdf:type manuservice:BusinessEntity)"
	 * 
	 * @param variableNode
	 */
	public JenaRulesBuilder withOrganization() {

		String class_business_entity = this.ontologyService.lookupOntClass(Constants.Ontology.BUSINESS_ENTITY);
		this.organization = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", Constants.Ontology.RDF_TYPE_USING_PREFIX,
				class_business_entity);
		return this;
	}

	/**
	 * e.g. (?x manuservice:name 'Company-B')
	 * 
	 * @param subject
	 * @return
	 */
	public JenaRulesBuilder withAccesserName(String subject) {
		String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		this.accesserName = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", property_name, subject);
		return this;
	}

	public JenaRulesBuilder addAccesserName(String subject, String referenceSubject) {
		String object = this.ontologyService.lookupObjectByNlObjectAndReferenceObject(subject, referenceSubject);
		String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		String name = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", property_name, object);

		this.accesserNameList.add(name);
		return this;
	}

	/**
	 * e.g. (?x manuservice:hasAddress ?addr), (?addr manuservice:country 'New Zealand')
	 * 
	 * @param baseLocation
	 * @param allowedObj
	 * @return
	 */
	public JenaRulesBuilder withAccesserBaseLocation(String baseLocation, String allowedObj) {
		this.accesserBaseLocation = buildLocationRules(baseLocation, Tools.removeDashSuffix(allowedObj));
		return this;
	}

	/**
	 * Lookup proper subject and predicate by the given object and assemble the rule fact.
	 * 
	 * New Zealand={manuservice:country=manuservice:CompanyFAdd
	 * manuservice:CompanyFAdd={manuservice:hasAddress=manuservice:CompanyF
	 * manuservice:BusinessEntity={rdf:type=manuservice:CompanyF, rdfs:domain=manuservice:name}
	 * 
	 * @param object
	 * @param referenceSubject
	 * @return
	 */
	private String buildLocationRules(String object, String referenceSubject) {

		// look into 1 depth
		// e.g. New Zealand={manuservice:country=manuservice:CompanyFAdd
		Entry<String, String> entry_one = this.ontologyService.lookupPredicateSubjectByObject(object, referenceSubject);
		if (entry_one != null) {
			String realObject = this.ontologyService.lookupObjectByNlObject(object);
			return buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", entry_one.getKey(), realObject);
		}

		// look into 2 depths
		for (Map<String, String> predicateSubjectMap : this.ontologyService.lookupPredicateSubjectListByObject(object)) {
			for (java.util.Iterator<Entry<String, String>> it = predicateSubjectMap.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry_two = it.next();

				// e.g. manuservice:CompanyFAdd={manuservice:hasAddress=manuservice:CompanyF
				Entry<String, String> entryResult = this.ontologyService.lookupPredicateSubjectByObject(entry_two.getValue(),
						referenceSubject);

				if (entryResult != null) {
					String realObject = this.ontologyService.lookupObjectByNlObject(object);
					return buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", entryResult.getKey(), "?addr")
							+ ", " + buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?addr", entry_two.getKey(), realObject);
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @param entityType
	 * @return
	 */
	public JenaRulesBuilder withAccesserEntityType(String entityType, String referenceSubject) {

		Entry<String, String> predicateSubject = this.ontologyService.lookupPredicateSubjectByObject(entityType, referenceSubject);

		if (predicateSubject != null) {
			String realObject = this.ontologyService.lookupObjectByNlObject(entityType);
			this.accesserEntityType = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", predicateSubject.getKey(),
					realObject);
		}

		return this;
	}

	/**
	 * e.g. (?x cm:creditGrade ?creditGrade), greaterThan(?creditGrade, 8.0)
	 * 
	 * @param rating
	 * @param comparator
	 * @param thanValue
	 * @return
	 */
	public JenaRulesBuilder withCreditGrade(String rating, String comparator, String thanValue) {

		String property_rating = this.ontologyService.lookupOntProperty(rating);
		String object = "?" + Tools.removePrefix(property_rating);

		this.accessCreditGrade = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", property_rating, object)
				+ ", " + comparator + "(" + object + ", " + thanValue + ")";

		return this;
	}

	public JenaRulesBuilder withOperationYears(String years, String comparator, String thanValue) {
		String property_years = this.ontologyService.lookupOntProperty(years);
		String object = "?" + Tools.removePrefix(property_years);

		this.accessOperationYears = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", property_years, object)
				+ ", " + comparator + "(" + object + ", " + thanValue + ")";

		return this;
	}

	private String buildRuleFact(String ruleTemplate, String subject, String predicate, String object) {

		String ruleFact = ruleTemplate;
		if (subject != null) {
			ruleFact = ruleFact.replace(Constants.Param.SUBJECT, subject);
		}
		if (predicate != null) {
			ruleFact = ruleFact.replace(Constants.Param.PREDICATE, predicate);
		}
		if (object != null) {
			ruleFact = ruleFact.replace(Constants.Param.OBJECT, object);
		}
		return ruleFact;
	}

	public String build() {
		StringBuilder jenaFactors = new StringBuilder(1024);

		if (!StringUtils.isBlank(this.ruleHeadTerm)) {
			jenaFactors.append(this.ruleHeadTerm).append(" <- ");
		}

		if (!StringUtils.isEmpty(this.hasResource)) {
			jenaFactors.append(this.hasResource).append(", ");
		}

		if (!StringUtils.isEmpty(this.resourceType)) {
			jenaFactors.append(this.resourceType).append(", ");
		}

		if (!StringUtils.isEmpty(this.resourceName)) {
			jenaFactors.append(this.resourceName).append(", ");
		}

		if (!StringUtils.isEmpty(this.organization)) {
			jenaFactors.append(this.organization).append(", ");
		}

		if (!StringUtils.isEmpty(this.accesserName)) {
			jenaFactors.append(this.accesserName).append(", ");
		}

		if (!this.accesserNameList.isEmpty()) {
			// since Jena doesn't support logic disjunction, only temporarily assemble factors with disjunctions, need to remove them in the end
			jenaFactors.append(Constants.Param.JENA_DISJUNCTION_START).append(" ");

			for (String name : this.accesserNameList) {
				jenaFactors.append(name).append(" ").append(Constants.Param.JENA_DISJUNCTION).append(" ");
			}
			jenaFactors.append(Constants.Param.JENA_DISJUNCTION_END).append(" ");
		}

		if (!StringUtils.isEmpty(this.accesserBaseLocation)) {
			jenaFactors.append(this.accesserBaseLocation).append(", ");
		}

		if (!StringUtils.isEmpty(this.accesserEntityType)) {
			jenaFactors.append(this.accesserEntityType).append(", ");
		}

		if (!StringUtils.isEmpty(this.accessCreditGrade)) {
			jenaFactors.append(this.accessCreditGrade).append(", ");
		}

		if (!StringUtils.isEmpty(this.accessOperationYears)) {
			jenaFactors.append(this.accessOperationYears).append(", ");
		}

		return jenaFactors.toString();
	}
}
