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
package edu.uoa.cs.master.cloudmanufacturingnlp.business.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.PrintUtil;

import edu.uoa.cs.master.cloudmanufacturingnlp.business.dictionary.DictionaryService;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class OntologyService {

	private final DictionaryService dictionaryService;

	private InfModel infModel = null;
	private final List<String> ontProperties = new ArrayList<>();
	private final List<String> ontClasses = new ArrayList<>();

	/*
	 * e.g. manuservice:CompanyFAdd manuservice:country 'New Zealand' <'New Zealand', <manuservice:country, manuservice:CompanyFAdd>,
	 * <manuservice:country, manuservice:CompanyBAdd>>
	 */
	private final Map<String, List<Map<String, String>>> statementTriplesIndexedByObjects = new HashMap<>();
	/*
	 * e.g. manuservice:CompanyF rdf:type manuservice:BusinessEntity <rdf:type, <<manuservice:BusinessEntity, manuservice:CompanyF>,
	 * <manuservice:BusinessEntity, manuservice:CompanyB>>
	 */
	private final Map<String, List<Map<String, String>>> statementTriplesIndexedByPredicates = new HashMap<>();

	public OntologyService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;

		loadOntProperties();
		loadStatementTriples();
	}

	private void loadOntProperties() {
		PrintUtil.registerPrefix(Constants.Ontology.PREFIX, Constants.Ontology.NS_MC);

		// load ont properties
		final List<OntProperty> ontologyProperties = JenaOntModelManager.getInstance().loadOntProperties();
		for (final OntProperty ontProperty : ontologyProperties) {
			this.ontProperties.add(PrintUtil.print(ontProperty));
		}

		// load ont classess
		final List<OntClass> ontClassess = JenaOntModelManager.getInstance().loadOntClasses();
		for (final OntClass ontClasse : ontClassess) {
			this.ontClasses.add(PrintUtil.print(ontClasse));
		}
	}

	private void loadStatementTriples() {

		PrintUtil.registerPrefix(Constants.Ontology.PREFIX, Constants.Ontology.NS_MC);
		this.infModel = JenaInfModelManager.getInstance().createInfModelByRule("");

		for (final StmtIterator it = this.infModel.listStatements(); it.hasNext();) {
			final Triple triple = it.next().asTriple();

			final String object = printLiteralLexicalForm(triple.getObject());
			final String predicate = PrintUtil.print(triple.getPredicate());
			final String subject = PrintUtil.print(triple.getSubject());

			// load triples, indexed by object
			List<Map<String, String>> predicateSubjects = this.statementTriplesIndexedByObjects.get(object);
			if (predicateSubjects == null) {
				predicateSubjects = new ArrayList<>();
			}
			final Map<String, String> predicateSubject = new HashMap<>();
			predicateSubject.put(predicate, subject);

			predicateSubjects.add(predicateSubject);
			this.statementTriplesIndexedByObjects.put(object, predicateSubjects);

			// load triples, indexed by predicate
			List<Map<String, String>> objectSubjects = this.statementTriplesIndexedByPredicates.get(predicate);
			if (objectSubjects == null) {
				objectSubjects = new ArrayList<>();
			}
			final Map<String, String> objectSubject = new HashMap<>();
			objectSubject.put(object, subject);

			objectSubjects.add(objectSubject);
			this.statementTriplesIndexedByPredicates.put(predicate, objectSubjects);
		}
	}

	private String printLiteralLexicalForm(final Node node) {

		if (node.isLiteral()) {
			return node.getLiteralLexicalForm();
		} else {
			return PrintUtil.print(node);
		}
	}

	public List<String> getOntProperties() {
		return this.ontProperties;
	}

	public List<String> getOntClasses() {
		return this.ontClasses;
	}

	public String lookupOntProperty(final String inputProperty) {
		for (final String property : getOntProperties()) {
			if (this.dictionaryService.isSynonym(inputProperty, Tools.removePrefix(property))) {
				return property;
			}
		}
		return null;
	}

	public String lookupOntClass(final String inputClass) {
		for (final String ontClass : getOntClasses()) {
			if (this.dictionaryService.isSynonym(inputClass, Tools.removePrefix(ontClass))) {
				return ontClass;
			}
		}
		return null;
	}

	public List<Map<String, String>> lookupPredicateSubjectListByObject(final String originalObject) {

		for (final String object : this.statementTriplesIndexedByObjects.keySet()) {
			if (this.dictionaryService.isSynonym(object, originalObject)) {
				return this.statementTriplesIndexedByObjects.get(object);
			}
		}

		return new ArrayList<>();
	}

	public List<Map<String, String>> lookupObjectSubjectListByPredicate(final String originalPredicate) {
		List<Map<String, String>> objectSubjects = null;

		for (final String predicate : this.statementTriplesIndexedByPredicates.keySet()) {
			if (predicate.equalsIgnoreCase(originalPredicate)) {
				objectSubjects = this.statementTriplesIndexedByPredicates.get(predicate);
			}
		}

		if (objectSubjects == null) {
			objectSubjects = new ArrayList<>();
		}
		return objectSubjects;
	}

	public String lookupObjectByNlObject(final String originalObject) {
		for (final String object : this.statementTriplesIndexedByObjects.keySet()) {
			if (this.dictionaryService.isSynonym(object, originalObject)) {
				return object;
			}
		}
		return null;
	}

	public String lookupObjectByNlObjectAndReferenceObject(final String originalObject, final String referenceObject) {

		final String referenceRdfType = lookupReferenceRDFTypeBySubject(referenceObject);

		for (final String object : this.statementTriplesIndexedByObjects.keySet()) {
			final String object_local = Tools.removePrefix(object);

			if (Tools.doesMatch(object_local, originalObject)) {
				if (isDesiredSubject(object_local, referenceObject, referenceRdfType)) {
					return object;
				}
			}

		}
		return null;
	}

	public Entry<String, String> lookupPredicateSubjectByObject(final String object, final String referenceSubject) {

		final String referenceRdfType = lookupReferenceRDFTypeBySubject(referenceSubject);

		for (final Map<String, String> predicateSubjectMap : lookupPredicateSubjectListByObject(object)) {
			for (final Entry<String, String> entry : predicateSubjectMap.entrySet()) {
				final String subject = Tools.removePrefix(entry.getValue());

				if (isDesiredSubject(subject, referenceSubject, referenceRdfType)) {
					return entry;
				}
			}
		}
		return null;
	}

	private boolean isDesiredSubject(final String subject, final String referenceSubject, final String referenceRdfType) {

		if (this.dictionaryService.isSynonym(subject, referenceSubject)) {
			// further check if their rdf:type matches
			final String specificRdfType = lookupSpecificRDFTypeBySubject(subject);
			if (this.dictionaryService.isSynonym(specificRdfType, referenceRdfType)) {
				return true;
			}
		}
		return false;
	}

	private String lookupReferenceRDFTypeBySubject(final String referenceSubject) {

		for (final Map<String, String> objectSubjectMap : this.statementTriplesIndexedByPredicates
				.get(Constants.Ontology.RDF_TYPE_USING_PREFIX)) {

			for (final String string : objectSubjectMap.keySet()) {
				final String rdfType = Tools.removePrefix(string);
				if (this.dictionaryService.isSynonym(referenceSubject, rdfType)) {
					return rdfType;
				}
			}
		}

		return null;
	}

	private String lookupSpecificRDFTypeBySubject(final String resourceString) {
		final Resource resource = this.infModel.getResource(Constants.Ontology.NS_MC + resourceString);
		final Property property = this.infModel.getProperty(Constants.Ontology.RDF_TYPE);
		final StmtIterator stmtIt = this.infModel.listStatements(resource, property, (Resource) null);

		while (stmtIt.hasNext()) {
			final Resource objectResource = stmtIt.next().getObject().asResource();
			if (objectResource.getNameSpace().equals(Constants.Ontology.NS_MC)) {
				return objectResource.getLocalName();
			}
		}

		return null;
	}

	public static void main(final String[] args) {
		final OntologyService ontologyService = new OntologyService(new DictionaryService());
		final Entry<String, String> entry = ontologyService.lookupPredicateSubjectByObject("New Zealand", "company");
		System.out.println((entry == null) ? "null" : entry.getKey() + " " + entry.getValue());
	}
}
