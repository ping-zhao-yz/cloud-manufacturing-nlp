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
package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCases {

	public static List<Map<String, Map<String, Integer>>> cases = new ArrayList<>();

	// auto-generated testing data set
	static {
		cases.add(createCasesMap("CompanyC shares hard resources with private limited companies in operation for more than 6 years",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyB shares machining resources with private limited companies",
				createExpectedResultMap("R_NXCNC1_B", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyC shares resources with private limited companies",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(2))));
		cases.add(createCasesMap(
				"CompanyE shares machining resources with private limited companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_E", Integer.valueOf(0))));
		cases.add(createCasesMap(
				"CompanyA shares resources with private limited companies having a credit rating higher than 7.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_A", Integer.valueOf(0))));
		cases.add(createCasesMap(
				"CompanyC shares machining resources with NZ-based company having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyE shares OKUMA MP-46V with NZ-based company",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(4))));
		cases.add(createCasesMap(
				"CompanyA shares machining resources with private limited companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_A", Integer.valueOf(0))));
		cases.add(createCasesMap("CompanyF shares resources with NZ-based company in operation for more than 7 years",
				createExpectedResultMap("R_AutoCAD1_F", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyD shares resources with NZ-based company in operation for more than 7 years",
				createExpectedResultMap("R_AutoCAD1_D", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyB shares OKUMA MP-46V with private limited companies",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(2))));

		cases.add(createCasesMap("CompanyB shares resources with specific companies, i.e., Company A and C;",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyB shares resources with companies in operation for more than 8 years",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyC shares hard resources with companies in operation for more than 7 years",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(4))));
		cases.add(createCasesMap("CompanyC shares machining resources with its own company",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyD shares hard resources with companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyA shares machining resources with private limited companies",
				createExpectedResultMap("R_NXCNC1_A", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyE shares soft resources with private limited companies in operation for more than 8 years",
				createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyF shares soft resources with companies having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_F", Integer.valueOf(4))));
		cases.add(createCasesMap("CompanyC shares soft resources with the public cloud",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyF shares machining resources with specific companies, i.e., Company A, B, and C;",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyB shares resources with NZ-based company having a credit rating higher than 5.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(4))));

		cases.add(createCasesMap("CompanyE shares soft resources with companies having a credit rating higher than 5.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(6))));
		cases.add(createCasesMap("CompanyA shares hard resources with private limited companies in operation for more than 5 years",
				createExpectedResultMap("R_NXCNC1_A", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyA shares OKUMA MP-46V with private limited companies",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyE shares machining resources with specific companies, i.e., Company A, B, and C;",
				createExpectedResultMap("R_NXCNC1_E", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyE shares resources with companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyA shares hard resources with private limited companies",
				createExpectedResultMap("R_NXCNC1_A", Integer.valueOf(2))));
		cases.add(createCasesMap(
				"CompanyE shares hard resources with private limited companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_E", Integer.valueOf(0))));
		cases.add(createCasesMap("CompanyF shares OKUMA MP-46V with NZ-based company in operation for more than 8 years",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyF shares hard resources with its own company",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(1))));
		cases.add(createCasesMap(
				"CompanyB shares soft resources with private limited companies having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(1))));
		cases.add(createCasesMap(
				"CompanyD shares machining resources with private limited companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(0))));

		cases.add(createCasesMap("CompanyD shares machining resources with companies in operation for more than 5 years",
				createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(4))));
		cases.add(createCasesMap("CompanyA shares resources with private limited companies in operation for more than 8 years",
				createExpectedResultMap("R_AutoCAD1_A", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyC shares soft resources with its own company",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyE shares OKUMA MP-46V with private limited companies",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(2))));
		cases.add(
				createCasesMap("CompanyC shares hard resources with NZ-based company having a credit rating higher than 6.0 (out of 10.0)",
						createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(2))));
		cases.add(
				createCasesMap("CompanyD shares soft resources with NZ-based company having a credit rating higher than 6.0 (out of 10.0)",
						createExpectedResultMap("R_AutoCAD1_D", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyA shares OKUMA MP-46V with companies having a credit rating higher than 5.0 (out of 10.0)",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(6))));
		cases.add(createCasesMap("CompanyE shares resources with NZ-based company in operation for more than 6 years",
				createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(3))));
		cases.add(createCasesMap(
				"CompanyC shares resources with private limited companies having a credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(0))));
		cases.add(createCasesMap("CompanyF shares resources with private limited companies in operation for more than 7 years",
				createExpectedResultMap("R_AutoCAD1_F", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyC shares soft resources with specific companies, i.e., Company A and B;",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(2))));

		cases.add(createCasesMap("CompanyE shares hard resources with companies in operation for more than 5 years",
				createExpectedResultMap("R_NXCNC1_E", Integer.valueOf(4))));
		cases.add(createCasesMap(
				"CompanyD shares resources with private limited companies having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("R_AutoCAD1_D", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyF shares hard resources with private limited companies",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(2))));
		cases.add(
				createCasesMap("CompanyE shares hard resources with NZ-based company having a credit rating higher than 8.0 (out of 10.0)",
						createExpectedResultMap("R_NXCNC1_E", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyE shares soft resources with its own company",
				createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyF shares machining resources with NZ-based company in operation for more than 6 years",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyB shares OKUMA MP-46V with private limited companies in operation for more than 8 years",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(1))));
		cases.add(
				createCasesMap("CompanyB shares hard resources with NZ-based company having a credit rating higher than 5.0 (out of 10.0)",
						createExpectedResultMap("R_NXCNC1_B", Integer.valueOf(4))));
		cases.add(createCasesMap("CompanyF shares hard resources with private limited companies in operation for more than 6 years",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(2))));
		cases.add(createCasesMap(
				"CompanyD shares OKUMA MP-46V with private limited companies having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyC shares OKUMA MP-46V with the public cloud",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(6))));

		cases.add(createCasesMap("CompanyD shares OKUMA MP-46V with companies having a credit rating higher than 7.0 (out of 10.0)",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyB shares machining resources with private limited companies in operation for more than 8 years",
				createExpectedResultMap("R_NXCNC1_B", Integer.valueOf(1))));
		cases.add(createCasesMap("CompanyD shares resources with NZ-based company ",
				createExpectedResultMap("R_AutoCAD1_D", Integer.valueOf(4))));
		cases.add(createCasesMap("CompanyF shares OKUMA MP-46V with specific companies, i.e., Company A, B, and C;",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyC shares machining resources with specific companies, i.e., Company A, B, and D;",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyA shares hard resources with companies in operation for more than 8 years",
				createExpectedResultMap("R_NXCNC1_A", Integer.valueOf(3))));
		cases.add(createCasesMap("CompanyB shares OKUMA MP-46V with NZ-based company",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(4))));
		cases.add(createCasesMap(
				"CompanyD shares machining resources with NZ-based company having a credit rating higher than 6.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyF shares machining resources with private limited companies",
				createExpectedResultMap("R_NXCNC1_F", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyC shares resources with private limited companies in operation for more than 6 years",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(2))));
		cases.add(createCasesMap("CompanyB shares resources with NZ-based company in operation for more than 7 years",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(3))));
	}

	// manual testing data set
	// static {
	// cases.add(createCasesMap("CompanyA shares soft resources with NZ-based companies.",
	// createExpectedResultMap("R_AutoCAD1_A", Integer.valueOf(4))));
	//
	// cases.add(createCasesMap("CompanyB shares hard resources within its own company.",
	// createExpectedResultMap("R_NXCNC1_B", Integer.valueOf(1))));
	//
	// cases.add(createCasesMap("CompanyC shares machining resources in the public cloud.",
	// createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(6))));
	//
	// cases.add(createCasesMap("CompanyC shares soft resources with companies having a credit rating higher than 6.5 (out of 10.0).",
	// createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(3))));
	//
	// cases.add(createCasesMap(
	// "CompanyD shares resources with specific companies, i.e., Company B and F;",
	// createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(2))));
	//
	// cases.add(createCasesMap("CompanyE shares software resources with companies in operation for more than 6 years.",
	// createExpectedResultMap("R_AutoCAD1_E", Integer.valueOf(4))));
	//
	// cases.add(createCasesMap("CompanyF shares OKUMA MP-46V with private limited companies.",
	// createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(2))));
	// }
	//
	// // training data set
	// static {
	// cases.add(createCasesMap("The CompanyA shares resources within its own company.",
	// createExpectedResultMap("R_AutoCAD1_A", Integer.valueOf(1))));
	//
	// cases.add(createCasesMap("The CompanyB shares resources with NZ-based companies.",
	// createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(4))));
	//
	// cases.add(createCasesMap("The CompanyC shares soft resources in the public cloud;",
	// createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(6))));
	//
	// cases.add(createCasesMap("The CompanyC shares hard resources with specific companies, i.e. Company A, B and E;",
	// createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(3))));
	//
	// cases.add(createCasesMap(
	// "The CompanyD shares machining resources with companies having an credit rating higher than 8.0 (out of 10.0)",
	// createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(2))));
	//
	// cases.add(createCasesMap("The CompanyE shares OKUMA MP-46V in the public cloud",
	// createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(6))));
	//
	// cases.add(createCasesMap("The CompanyF shares resources with private limited companies in operation for more than 10 years",
	// createExpectedResultMap("R_AutoCAD1_F", Integer.valueOf(1))));
	// }

	private static Map<String, Map<String, Integer>> createCasesMap(final String k, final Map<String, Integer> v) {
		final Map<String, Map<String, Integer>> map = new HashMap<>();
		map.put(k, v);
		return map;
	}

	private static Map<String, Integer> createExpectedResultMap(final String k, final Integer v) {
		final Map<String, Integer> map = new HashMap<>();
		map.put(k, v);
		return map;
	}

}
