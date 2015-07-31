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
package edu.uoa.cs.master.cloudmanufacturingnlp.jena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import edu.uoa.cs.master.cloudmanufacturingnlp.dictionary.DictionaryService;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class OntologyService {

	private DictionaryService dictionaryService;

	private InfModel infModel = null;
	private List<String> ontProperties = new ArrayList<>();
	private List<String> ontClasses = new ArrayList<>();

	/*
	 * e.g. manuservice:CompanyFAdd manuservice:country 'New Zealand'
	 * <'New Zealand', <manuservice:country, manuservice:CompanyFAdd>, <manuservice:country, manuservice:CompanyBAdd>>
	 */
	private Map<String, List<Map<String, String>>> statementTriplesIndexedByObjects = new HashMap<>();
	/*
	 * e.g. manuservice:CompanyF rdf:type manuservice:BusinessEntity
	 * <rdf:type, <<manuservice:BusinessEntity, manuservice:CompanyF>, <manuservice:BusinessEntity, manuservice:CompanyB>>
	 */
	private Map<String, List<Map<String, String>>> statementTriplesIndexedByPredicates = new HashMap<>();

	public OntologyService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;

		loadOntProperties();
		loadStatementTriples();
	}

	private void loadOntProperties() {
		PrintUtil.registerPrefix(Constants.Ontology.PREFIX, Constants.Ontology.NS_MC);

		// load ont properties
		List<OntProperty> ontProperties = JenaOntModelManager.getInstance().loadOntProperties();
		for (OntProperty ontProperty : ontProperties) {
			this.ontProperties.add(PrintUtil.print(ontProperty));
		}

		// load ont classess
		List<OntClass> ontClassess = JenaOntModelManager.getInstance().loadOntClasses();
		for (OntClass ontClasse : ontClassess) {
			this.ontClasses.add(PrintUtil.print(ontClasse));
		}
	}

	private void loadStatementTriples() {

		PrintUtil.registerPrefix(Constants.Ontology.PREFIX, Constants.Ontology.NS_MC);
		this.infModel = JenaInfModelManager.getInstance().createInfModel("");

		for (StmtIterator it = this.infModel.listStatements(); it.hasNext();) {
			Triple triple = it.next().asTriple();

			String object = printLiteralLexicalForm(triple.getObject());
			String predicate = PrintUtil.print(triple.getPredicate());
			String subject = PrintUtil.print(triple.getSubject());

			// load triples, indexed by object
			List<Map<String, String>> predicateSubjects = this.statementTriplesIndexedByObjects.get(object);
			if (predicateSubjects == null) {
				predicateSubjects = new ArrayList<>();
			}
			Map<String, String> predicateSubject = new HashMap<>();
			predicateSubject.put(predicate, subject);

			predicateSubjects.add(predicateSubject);
			this.statementTriplesIndexedByObjects.put(object, predicateSubjects);

			// load triples, indexed by predicate
			List<Map<String, String>> objectSubjects = this.statementTriplesIndexedByPredicates.get(predicate);
			if (objectSubjects == null) {
				objectSubjects = new ArrayList<>();
			}
			Map<String, String> objectSubject = new HashMap<>();
			objectSubject.put(object, subject);

			objectSubjects.add(objectSubject);
			this.statementTriplesIndexedByPredicates.put(predicate, objectSubjects);
		}
	}

	private String printLiteralLexicalForm(Node node) {

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

	public String lookupOntProperty(String inputProperty) {
		for (String property : getOntProperties()) {
			if (this.dictionaryService.isSynonym(inputProperty, Tools.removePrefix(property))) {
				return property;
			}
		}
		return null;
	}

	public String lookupOntClass(String inputClass) {
		for (String ontClass : getOntClasses()) {
			if (this.dictionaryService.isSynonym(inputClass, Tools.removePrefix(ontClass))) {
				return ontClass;
			}
		}
		return null;
	}

	public List<Map<String, String>> lookupPredicateSubjectListByObject(String originalObject) {

		for (Iterator<String> objects = this.statementTriplesIndexedByObjects.keySet().iterator(); objects.hasNext();) {
			String object = objects.next();
			if (this.dictionaryService.isSynonym(object, originalObject)) {
				return this.statementTriplesIndexedByObjects.get(object);
			}
		}

		return new ArrayList<>();
	}

	public List<Map<String, String>> lookupObjectSubjectListByPredicate(String originalPredicate) {
		List<Map<String, String>> objectSubjects = null;

		for (Iterator<String> predicates = this.statementTriplesIndexedByPredicates.keySet().iterator(); predicates.hasNext();) {
			String predicate = predicates.next();
			if (predicate.equalsIgnoreCase(originalPredicate)) {
				objectSubjects = this.statementTriplesIndexedByPredicates.get(predicate);
			}
		}

		if (objectSubjects == null) {
			objectSubjects = new ArrayList<>();
		}
		return objectSubjects;
	}

	public String lookupObjectByNlObject(String originalObject) {
		for (Iterator<String> objects = this.statementTriplesIndexedByObjects.keySet().iterator(); objects.hasNext();) {
			String object = objects.next();
			if (this.dictionaryService.isSynonym(object, originalObject)) {
				return object;
			}
		}
		return null;
	}

	public String lookupObjectByNlObjectAndReferenceObject(String originalObject, String referenceObject) {

		String referenceRdfType = lookupReferenceRDFTypeBySubject(referenceObject);

		for (Iterator<String> objects = this.statementTriplesIndexedByObjects.keySet().iterator(); objects.hasNext();) {
			String object = objects.next();
			String object_local = Tools.removePrefix(object);

			if (Tools.doesMatch(object_local, originalObject)) {
				if (isDesiredSubject(object_local, referenceObject, referenceRdfType)) {
					return object;
				}
			}

		}
		return null;
	}

	public Entry<String, String> lookupPredicateSubjectByObject(String object, String referenceSubject) {

		String referenceRdfType = lookupReferenceRDFTypeBySubject(referenceSubject);

		for (Map<String, String> predicateSubjectMap : lookupPredicateSubjectListByObject(object)) {
			for (Iterator<Entry<String, String>> it = predicateSubjectMap.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				String subject = Tools.removePrefix(entry.getValue());

				if (isDesiredSubject(subject, referenceSubject, referenceRdfType)) {
					return entry;
				}
			}
		}
		return null;
	}

	private boolean isDesiredSubject(String subject, String referenceSubject, String referenceRdfType) {

		if (this.dictionaryService.isSynonym(subject, referenceSubject)) {
			// further check if their rdf:type matches
			String specificRdfType = lookupSpecificRDFTypeBySubject(subject);
			if (this.dictionaryService.isSynonym(specificRdfType, referenceRdfType)) {
				return true;
			}
		}
		return false;
	}

	private String lookupReferenceRDFTypeBySubject(String referenceSubject) {

		for (Map<String, String> objectSubjectMap : this.statementTriplesIndexedByPredicates.get(Constants.Ontology.RDF_TYPE_USING_PREFIX)) {

			for (Iterator<String> it = objectSubjectMap.keySet().iterator(); it.hasNext();) {
				String rdfType = Tools.removePrefix(it.next());
				if (this.dictionaryService.isSynonym(referenceSubject, rdfType)) {
					return rdfType;
				}
			}
		}

		return null;
	}

	private String lookupSpecificRDFTypeBySubject(String resourceString) {
		Resource resource = this.infModel.getResource(Constants.Ontology.NS_MC + resourceString);
		Property property = this.infModel.getProperty(Constants.Ontology.RDF_TYPE);

		StmtIterator stmtIt = this.infModel.listStatements(resource, property, (Resource) null);
		return stmtIt.next().getObject().asResource().getLocalName();
	}

	public static void main(String[] args) {
		OntologyService ontologyService = new OntologyService(new DictionaryService());
		Entry<String, String> entry = ontologyService.lookupPredicateSubjectByObject("New Zealand", "company");
		System.out.println((entry == null) ? "null" : entry.getKey() + " " + entry.getValue());
	}
}
