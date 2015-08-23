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

	private final OntologyService ontologyService;

	private String ruleHeadTerm = null;
	private String hasResource = null;
	private String resourceType = null;
	private String resourceName = null;
	private String organization = null;
	private String accesserName = null;
	private final List<String> accesserNameList = new ArrayList<>();
	private String accesserBaseLocation = null;
	private String accesserEntityType = null;
	private String accessCreditGrade = null;
	private String accessOperationYears = null;

	public JenaRulesBuilder(final DictionaryService dictionaryService) {
		this.ontologyService = new OntologyService(dictionaryService);
	}

	/**
	 * e.g. (?x manuservice:hasAccessTo ?y)
	 *
	 * @param action
	 * @return
	 */
	public JenaRulesBuilder withRuleHeadTerm(final String action) {
		final String objectProperty_action = this.ontologyService.lookupOntProperty(action);
		this.ruleHeadTerm = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", objectProperty_action, "?y");

		return this;
	}

	/**
	 * e.g. (?org manuservice:hasResource ?y), (?org manuservice:name 'B')
	 */
	public JenaRulesBuilder withResourceOwner(final String subject) {
		final String property_hasResource = this.ontologyService.lookupOntProperty(Constants.Ontology.HAS_RESOURCE);
		final String fact_hasRes = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?org", property_hasResource, "?y");

		final String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		final String fact_name = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?org", property_name, subject);

		this.hasResource = fact_hasRes + ", " + fact_name;
		return this;
	}

	/**
	 * e.g. (?y rdf:type manuservice:SoftResource)
	 *
	 * @param ontologyResourceType
	 * @return
	 */
	public JenaRulesBuilder withResouceType(final String ontologyResourceType) {
		final String classResourceType = StringUtils.isBlank(ontologyResourceType) ? Constants.Ontology.RESOURCE : ontologyResourceType;
		final String class_resourceType = this.ontologyService.lookupOntClass(classResourceType);

		this.resourceType = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?y", Constants.Ontology.RDF_TYPE_USING_PREFIX,
				class_resourceType);

		return this;
	}

	/**
	 * e.g. (?y manuservice:name 'R_AutoCAD1_F')
	 *
	 * @param resName
	 * @return
	 */
	public JenaRulesBuilder withResouceName(final String resName) {
		final String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		this.resourceName = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?y", property_name, resName);
		return this;
	}

	/**
	 * Factor e.g. "(?x rdf:type manuservice:BusinessEntity)"
	 *
	 * @param variableNode
	 */
	public JenaRulesBuilder withPartnerGeneralInformation() {

		final String class_business_entity = this.ontologyService.lookupOntClass(Constants.Ontology.BUSINESS_ENTITY);
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
	public JenaRulesBuilder withPartnerName(final String subject) {
		final String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		this.accesserName = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", property_name, subject);
		return this;
	}

	public JenaRulesBuilder addPartnerName(final String subject, final String referenceSubject) {
		final String object = this.ontologyService.lookupObjectByNlObjectAndReferenceObject(subject, referenceSubject);
		final String property_name = this.ontologyService.lookupOntProperty(Constants.Ontology.NAME);
		final String name = buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", property_name, object);

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
	public JenaRulesBuilder withPartnerBaseLocation(final String baseLocation, final String allowedObj) {
		this.accesserBaseLocation = buildLocationRules(baseLocation, Tools.removeDashSuffix(allowedObj));
		return this;
	}

	/**
	 * Lookup proper subject and predicate by the given object and assemble the rule fact.
	 *
	 * New Zealand={manuservice:country=manuservice:CompanyFAdd manuservice:CompanyFAdd={manuservice:hasAddress=manuservice:CompanyF
	 * manuservice:BusinessEntity={rdf:type=manuservice:CompanyF, rdfs:domain=manuservice:name}
	 *
	 * @param object
	 * @param referenceSubject
	 * @return
	 */
	private String buildLocationRules(final String object, final String referenceSubject) {

		// look into 1 depth
		// e.g. New Zealand={manuservice:country=manuservice:CompanyFAdd
		final Entry<String, String> entry_one = this.ontologyService.lookupPredicateSubjectByObject(object, referenceSubject);
		if (entry_one != null) {
			final String realObject = this.ontologyService.lookupObjectByNlObject(object);
			return buildRuleFact(Constants.JenaRules.FACT_WITH_LITERAL_OBJECT, "?x", entry_one.getKey(), realObject);
		}

		// look into 2 depths
		for (final Map<String, String> predicateSubjectMap : this.ontologyService.lookupPredicateSubjectListByObject(object)) {
			for (final Entry<String, String> entry_two : predicateSubjectMap.entrySet()) {
				// e.g. manuservice:CompanyFAdd={manuservice:hasAddress=manuservice:CompanyF
				final Entry<String, String> entryResult = this.ontologyService.lookupPredicateSubjectByObject(entry_two.getValue(),
						referenceSubject);

				if (entryResult != null) {
					final String realObject = this.ontologyService.lookupObjectByNlObject(object);
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
	public JenaRulesBuilder withPartnerEntityType(final String entityType, final String referenceSubject) {

		final Entry<String, String> predicateSubject = this.ontologyService.lookupPredicateSubjectByObject(entityType, referenceSubject);

		if (predicateSubject != null) {
			final String realObject = this.ontologyService.lookupObjectByNlObject(entityType);
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
	public JenaRulesBuilder withCreditGrade(final String rating, final String comparator, final String thanValue) {

		final String property_rating = this.ontologyService.lookupOntProperty(rating);
		final String object = "?" + Tools.removePrefix(property_rating);

		this.accessCreditGrade = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", property_rating, object)
				+ ", " + comparator + "(" + object + ", " + thanValue + ")";

		return this;
	}

	public JenaRulesBuilder withOperationYears(final String years, final String comparator, final String thanValue) {
		final String property_years = this.ontologyService.lookupOntProperty(years);
		final String object = "?" + Tools.removePrefix(property_years);

		this.accessOperationYears = buildRuleFact(Constants.JenaRules.FACT_WITH_RESOURCE_OBJECT, "?x", property_years, object)
				+ ", " + comparator + "(" + object + ", " + thanValue + ")";

		return this;
	}

	private String buildRuleFact(final String ruleTemplate, final String subject, final String predicate, final String object) {

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
		final StringBuilder jenaFactors = new StringBuilder(1024);

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
			// since Jena doesn't support logic disjunction, only temporarily assemble factors with disjunctions, need to remove them in the
			// end
			jenaFactors.append(Constants.Param.JENA_DISJUNCTION_START).append(" ");

			for (final String name : this.accesserNameList) {
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
