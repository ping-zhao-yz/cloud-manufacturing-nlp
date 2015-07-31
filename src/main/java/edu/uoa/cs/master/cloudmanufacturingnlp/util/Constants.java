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
package edu.uoa.cs.master.cloudmanufacturingnlp.util;

public interface Constants {

	public interface Nlp {
		public final String NSUBJ = "nsubj";
		public final String DOBJ = "dobj";
		public final String PREP = "prep";
		public final String POBJ = "pobj";
		public final String AMOD = "amod";
		public final String NN = "nn";
		public final String CONJ = "conj";
		public final String NUM = "num";
		public final String QUANTMOD = "quantmod";
		public final String MWE = "mwe";
		public final String VMOD = "vmod";
		public final String ADVMOD = "advmod";

		public final String WITH = "with";
		public final String OWN = "own";
		public final String PUBLIC = "public";
		public final String SPECIFIC = "specific";
		public final String BASED = "based";

		public final String HAVING = "having";
		public final String IN = "in";
		public final String RATING = "rating";
		public final String OPERATION = "operation";

		public final String FOR = "for";
		public final String YEARS = "years";

		public final String MORE = "more";
		public final String LESS = "less";
	}

	public interface Ontology {
		public static final String MC_SOURCE = "http://www.semanticweb.org/yuqianlu/ontologies/2013/10/manuservice";
		public static final String NS_MC = MC_SOURCE + "#";
		public static final String PREFIX = "manuservice";

		public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
		public static final String NS_RDF = RDF + "#";
		public static final String RDF_TYPE = NS_RDF + "type";
		public static final String RDF_TYPE_USING_PREFIX = "rdf:type";

		public static final String NAME = "name";
		public static final String BUSINESS_ENTITY = "BusinessEntity";

		public static final String HAS_RESOURCE = "hasResource";
		public final String RESOURCE = "resources";
	}

	public interface Param {
		public final String SUBJECT = "$SUBJ";
		public final String PREDICATE = "$PRED";
		public final String OBJECT = "$OBJ";

		public final String JENA_DISJUNCTION = "&OR";
		public final String JENA_DISJUNCTION_START = "$OR_START";
		public final String JENA_DISJUNCTION_END = "$OR_END";
	}

	public interface JenaRules {
		public final String FACT_WITH_RESOURCE_OBJECT = "(" + Constants.Param.SUBJECT + " " + Constants.Param.PREDICATE + " "
				+ Constants.Param.OBJECT + ")";

		public final String FACT_WITH_LITERAL_OBJECT = "(" + Constants.Param.SUBJECT + " " + Constants.Param.PREDICATE + " '"
				+ Constants.Param.OBJECT + "')";

		public final String FACT_NATIVE_GREATER_THAN = "greaterThan";
		public final String FACT_NATIVE_LESS_THAN = "lessThan";
		public final String FACT_NATIVE_EQUAL = "equal";
		public final String FACT_NATIVE_NOTEQUAL = "notEqual";
	}

	public interface FilePath {
		public static final String LOCAL_BASE = "/Users/pingz/Documents/software/apache-tomcat-7.0.55/webapps/cloudmanufacturingnlp/WEB-INF/classes/resource/";

		public static final String MC_LOCAL = LOCAL_BASE + "cloudontology.owl";
		public static final String SYNONYM = LOCAL_BASE + "synonym.txt";
		public static final String COUNTRY_CODE = LOCAL_BASE + "countrycode.txt";
	}

	public interface ErrMsg {
		public final String NO_SUBJ = "Error: natural language rule doesn't contain subject";
		public final String NO_ACTION = "Error: natural language rule doesn't contain action";
		public final String NO_OBJ = "Error: natural language rule doesn't contain object";
	}
}
