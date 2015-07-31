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

import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

public class JenaInfModelManager {

	private Model cloudOntologyModel = FileManager.get().loadModel(Constants.FilePath.MC_LOCAL);

	private static JenaInfModelManager instance = new JenaInfModelManager();

	private JenaInfModelManager() {
		// do nothing;
	}

	public static JenaInfModelManager getInstance() {
		return instance;
	}

	public InfModel createInfModel(String rule) {
		// get a generic rule reasoner using the incoming rules
		Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rule));
		reasoner.setDerivationLogging(true);

		// create the inference model using the reasoner against the data
		InfModel infModel = ModelFactory.createInfModel(reasoner, this.cloudOntologyModel);
		validateInfModel(infModel);

		return infModel;
	}

	private void validateInfModel(InfModel infModel) {
		ValidityReport validityReport = infModel.validate();

		if (!validityReport.isValid()) {
			System.out.println("Validity report: \nInvalid!\n" + validityReport.toString());
			for (Iterator<Report> i = validityReport.getReports(); i.hasNext();) {
				System.out.println((Object) i.next());
			}
		}
	}

	public void inferenceModel(InfModel infModel, String resourceStringA, String propertyString, String resourceStringB) {
		System.out.printf("\nList all statements for ResourceA (%s), Property (%s), ResourceB (%s):\n", resourceStringA,
				propertyString, resourceStringB);

		Resource resourceA = (StringUtils.isEmpty(resourceStringA)) ? null : infModel.getResource(resourceStringA);
		Property property = (StringUtils.isEmpty(propertyString)) ? null : infModel.getProperty(propertyString);
		Resource resourceB = (StringUtils.isEmpty(resourceStringB)) ? null : infModel.getResource(resourceStringB);

		PrintWriter out = new PrintWriter(System.out);

		for (StmtIterator stmtIt = infModel.listStatements(resourceA, property, resourceB); stmtIt.hasNext();) {
			Statement statement = stmtIt.nextStatement();
			System.out.println("Statement: " + PrintUtil.print(statement));

			// open trace
			for (Iterator<Derivation> id = infModel.getDerivation(statement); id.hasNext();) {
				Derivation deriv = (Derivation) id.next();
				deriv.printTrace(out, true);
			}
		}
		out.flush();
	}

	public static void main(String[] args) {

		PrintUtil.registerPrefix("manuservice", Constants.Ontology.NS_MC);

		//		String rule = "[rule1: (?x manuservice:hasAccess manuservice:Resource) <- (?x manuservice:operateYears 54)]";
		//		String rule = "[rule1: (?x manuservice:hasAccess manuservice:Resource) <- (?x gr:name 'Company A')]";
		String rule = "[rule1: (?x manuservice:hasResource manuservice:SoftResource) <- (?x rdf:type manuservice:BusinessEntity), (?x manuservice:name 'CompanyA')]";
		InfModel infModel = JenaInfModelManager.getInstance().createInfModel(rule);

		// 1. hasPOS - exactly match
		// at least resourceA is not null, property and/or resourceB can be null
		//		JenaInfModelManager.getInstance().inferenceModel(infModel, Constants.Ontology.NS_MC + "C_EagleBurgmann",
		//				Constants.Ontology.NS_GR + "hasPOS", Constants.Ontology.NS_MC + "L_EagleBurgmann");

		// 2. hasRes
		JenaInfModelManager.getInstance().inferenceModel(infModel, "", Constants.Ontology.NS_MC + "hasResource",
				Constants.Ontology.NS_MC + "Resource");

		// 3. all the subjects
		//		for (ResIterator it = infModel.listSubjects(); it.hasNext();) {
		//			System.out.println("Subjects: " + PrintUtil.print(it.next()));
		//		}
		//
		// 4. all the predicates
		//		for (StmtIterator it = infModel.listStatements(); it.hasNext();) {
		//			System.out.println("Statements: " + PrintUtil.print(it.next()));
		//		}
		//
		// 5. all the objects
		//		for (NodeIterator it = infModel.listObjects(); it.hasNext();) {
		//			System.out.println("Objects: " + PrintUtil.print(it.next()));
		//		}
	}
}
