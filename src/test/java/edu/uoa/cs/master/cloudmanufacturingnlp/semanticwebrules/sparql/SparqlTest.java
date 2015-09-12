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
package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules.sparql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;

import edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules.SemanticWebRulesTest;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;

/**
 * @author pingz
 *
 */
public class SparqlTest extends SemanticWebRulesTest {

	/**
	 * @param expectedValue
	 * @param infModel
	 */
	@Override
	public void testRules(final Map<String, Integer> expectedValue, final InfModel infModel) {
		final String objectName = expectedValue.keySet().iterator().next();
		final String queryString = String.format("PREFIX manuservice: <%s> ", Constants.Ontology.NS_MC)
				+ String.format("SELECT ?partner WHERE { ?partner manuservice:hasAccessTo manuservice:%s }", objectName);

		final Query query = QueryFactory.create(queryString);
		final QueryExecution queryExecution = QueryExecutionFactory.create(query, infModel);

		final ResultSet resultSet = queryExecution.execSelect();
		ResultSetFormatter.out(System.out, resultSet, query);

		final Integer expectedNumber = expectedValue.values().iterator().next();
		assertThat(resultSet.getRowNumber()).isSameAs(expectedNumber);

		queryExecution.close();
	}
}
