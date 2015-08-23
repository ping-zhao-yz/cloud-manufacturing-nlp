package edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCases {

	public static List<Map<String, Map<String, Integer>>> cases = new ArrayList<>();

	static {
		cases.add(createCasesMap("The CompanyA shares resources within its own company.",
				createExpectedResultMap("R_AutoCAD1_A", Integer.valueOf(1))));

		cases.add(createCasesMap("The CompanyB shares resources with NZ-based companies.",
				createExpectedResultMap("R_AutoCAD1_B", Integer.valueOf(4))));

		cases.add(createCasesMap("The CompanyC shares soft resources in the public cloud;",
				createExpectedResultMap("R_AutoCAD1_C", Integer.valueOf(6))));

		cases.add(createCasesMap("The CompanyC shares hard resources with specific companies, i.e. Company A, B and E;",
				createExpectedResultMap("R_NXCNC1_C", Integer.valueOf(3))));

		cases.add(createCasesMap(
				"The CompanyD shares machining resources with companies having an credit rating higher than 8.0 (out of 10.0)",
				createExpectedResultMap("R_NXCNC1_D", Integer.valueOf(2))));

		cases.add(createCasesMap("The CompanyE shares OKUMA MP-46V in the public cloud",
				createExpectedResultMap("OKUMA_MP-46V", Integer.valueOf(6))));

		cases.add(createCasesMap("The CompanyF shares resources with private limited companies in operation for more than 10 years",
				createExpectedResultMap("R_AutoCAD1_F", Integer.valueOf(1))));
	}

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
