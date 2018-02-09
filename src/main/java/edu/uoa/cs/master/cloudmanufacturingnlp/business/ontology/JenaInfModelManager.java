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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	private static JenaInfModelManager instance = new JenaInfModelManager();

	private JenaInfModelManager() {
		// do nothing;
	}

	public static JenaInfModelManager getInstance() {
		return instance;
	}

	public InfModel createInfModelByRules(final String[] rules) {
		final List<Rule> parsedRules = new ArrayList<>();
		for (final String rule : rules) {
			if (StringUtils.isNotBlank(rule)) {
				parsedRules.addAll(Rule.parseRules(rule));
			}
		}

		final Reasoner reasoner = new GenericRuleReasoner(parsedRules);
		return createInfModelWithReasoner(reasoner);
	}

	public InfModel createInfModelByRule(final String rule) {
		final Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rule));
		return createInfModelWithReasoner(reasoner);
	}

	/**
	 * @param reasoner
	 * @return
	 */
	private InfModel createInfModelWithReasoner(final Reasoner reasoner) {
		reasoner.setDerivationLogging(true);

		// create the inference model using the reasoner against the data
		final Model cloudOntologyModel = FileManager.get().loadModel(Constants.FilePath.MC_LOCAL);
		final InfModel infModel = ModelFactory.createInfModel(reasoner, cloudOntologyModel);

		validateInfModel(infModel);
		return infModel;
	}

	private void validateInfModel(final InfModel infModel) {
		final ValidityReport validityReport = infModel.validate();

		if (!validityReport.isValid()) {
			System.out.println("Validity report: \nInvalid!\n" + validityReport.toString());

			for (final Iterator<Report> i = validityReport.getReports(); i.hasNext();) {
				System.out.println(i.next());
			}
		}
	}

	public int inferenceModel(final InfModel infModel, final String resourceStringA, final String propertyString,
			final String resourceStringB) {

		int statementNum = 0;

		System.out.printf("\nList all statements for ResourceA (%s), Property (%s), ResourceB (%s):\n", resourceStringA, propertyString,
				resourceStringB);

		final Resource resourceA = (StringUtils.isEmpty(resourceStringA)) ? null : infModel.getResource(resourceStringA);
		final Property property = (StringUtils.isEmpty(propertyString)) ? null : infModel.getProperty(propertyString);
		final Resource resourceB = (StringUtils.isEmpty(resourceStringB)) ? null : infModel.getResource(resourceStringB);

		final PrintWriter out = new PrintWriter(System.out);
		final StmtIterator listStatements = infModel.listStatements(resourceA, property, resourceB);

		for (final StmtIterator stmtIt = listStatements; stmtIt.hasNext();) {
			final Statement statement = stmtIt.nextStatement();

			statementNum++;
			System.out.println("Statement: " + PrintUtil.print(statement));

			// open trace
			for (final Iterator<Derivation> id = infModel.getDerivation(statement); id.hasNext();) {
				final Derivation deriv = id.next();
				deriv.printTrace(out, true);
			}
		}

		out.flush();
		return statementNum;
	}

	public static void main(final String[] args) {

		Constants.LOCAL_BASE = Constants.FilePath.LOCAL_BASE_JAVA;
		PrintUtil.registerPrefix("manuservice", Constants.Ontology.NS_MC);

		// the below rule will result in dead loop
		// final String commonRule = "[rule0: (?x rdf:type ?z) <- (?x rdf:type ?y), (?y rdfs:subClassOf ?z)]";

		final String rule =
				"[rule1: (?x manuservice:hasAccessTo ?y) <- "
						+ "(?org manuservice:hasResource ?y), (?org manuservice:name 'CompanyB'), (?y rdf:type manuservice:Resource), (?x rdf:type manuservice:BusinessEntity), "
						+ "(?x manuservice:hasAddress ?addr), (?addr manuservice:country 'New Zealand')]";

		// query based on the generated rule
		final InfModel infModel = JenaInfModelManager.getInstance().createInfModelByRules(new String[] { rule });

		JenaInfModelManager.getInstance().inferenceModel(infModel, "",
				Constants.Ontology.NS_MC + "hasAccessTo",
				Constants.Ontology.NS_MC + "R_AutoCAD1_B");

		// 3. all the subjects
		// for (ResIterator it = infModel.listSubjects(); it.hasNext();) {
		// System.out.println("Subjects: " + PrintUtil.print(it.next()));
		// }
		//
		// 4. all the predicates
		// for (final StmtIterator it = infModel.listStatements(); it.hasNext();) {
		// System.out.println("Statements: " + PrintUtil.print(it.next()));
		// }
		//
		// 5. all the objects
		// for (NodeIterator it = infModel.listObjects(); it.hasNext();) {
		// System.out.println("Objects: " + PrintUtil.print(it.next()));
		// }
	}
}
