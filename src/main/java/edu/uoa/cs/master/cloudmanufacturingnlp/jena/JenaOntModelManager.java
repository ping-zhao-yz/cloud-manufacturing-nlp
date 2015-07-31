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
import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

public class JenaOntModelManager {

	private OntModel mcOntModel = ModelFactory.createOntologyModel();

	private static JenaOntModelManager instance = new JenaOntModelManager();

	private JenaOntModelManager() {
		this.mcOntModel.getDocumentManager().addAltEntry(Constants.Ontology.MC_SOURCE, "file:" + Constants.FilePath.MC_LOCAL);
		this.mcOntModel.read(Constants.Ontology.MC_SOURCE, "RDF/XML");
	}

	public static JenaOntModelManager getInstance() {
		return instance;
	}

	public List<Individual> loadIndividuals() {

		List<Individual> individualList = new ArrayList<>();
		for (ExtendedIterator<Individual> classes = this.mcOntModel.listIndividuals(); classes.hasNext();) {
			Individual individual = (Individual) classes.next();
			individualList.add(individual);
		}
		return individualList;
	}

	public List<ObjectProperty> loadObjectProperties() {

		List<ObjectProperty> objectPropertyList = new ArrayList<>();
		for (ExtendedIterator<ObjectProperty> objectProperties = this.mcOntModel.listObjectProperties(); objectProperties.hasNext();) {
			objectPropertyList.add(objectProperties.next());
		}
		return objectPropertyList;
	}

	public List<DatatypeProperty> loadDataProperties() {

		List<DatatypeProperty> dataPropertyList = new ArrayList<>();
		for (ExtendedIterator<DatatypeProperty> dataProperties = this.mcOntModel.listDatatypeProperties(); dataProperties.hasNext();) {
			dataPropertyList.add(dataProperties.next());
		}
		return dataPropertyList;
	}

	public List<OntProperty> loadOntProperties() {

		List<OntProperty> ontPropertyList = new ArrayList<>();
		for (ExtendedIterator<OntProperty> ontProperties = this.mcOntModel.listAllOntProperties(); ontProperties.hasNext();) {
			ontPropertyList.add(ontProperties.next());
		}
		return ontPropertyList;
	}

	public List<OntClass> loadOntClasses() {

		List<OntClass> ontClassList = new ArrayList<>();
		for (ExtendedIterator<OntClass> ontClasses = this.mcOntModel.listClasses(); ontClasses.hasNext();) {
			ontClassList.add(ontClasses.next());
		}
		return ontClassList;
	}

	public static void main(String[] args) {
		PrintUtil.registerPrefix(Constants.Ontology.PREFIX, Constants.Ontology.NS_MC);

		// load mc model and individuals
		System.out.println("Loading individuals from http://www.semanticweb.org/yuqianlu/ontologies/2013/10/manuservice ...");

		for (Individual individual : JenaOntModelManager.getInstance().loadIndividuals()) {
			System.out.println("MC Individual: " + PrintUtil.print(individual));
		}

		for (ObjectProperty objectProperty : JenaOntModelManager.getInstance().loadObjectProperties()) {
			System.out.println("MC Object Property: " + PrintUtil.print(objectProperty));
		}

		for (DatatypeProperty dataProperty : JenaOntModelManager.getInstance().loadDataProperties()) {
			System.out.println("MC Data Property: " + PrintUtil.print(dataProperty));
		}

		for (OntProperty ontProperty : JenaOntModelManager.getInstance().loadOntProperties()) {
			System.out.println("MC Ont Property: " + PrintUtil.print(ontProperty));
		}

		for (OntClass ontClass : JenaOntModelManager.getInstance().loadOntClasses()) {
			System.out.println("MC Ont Class: " + PrintUtil.print(ontClass));
		}
	}
}
