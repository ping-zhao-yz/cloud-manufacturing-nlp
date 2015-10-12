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
import java.util.List;
import java.util.Random;

/**
 * @author pingz
 *
 */
public class TestingRulesGenerator {

	private static List<String> resourceOwners = new ArrayList<>();
	private static List<String> resourceTypes = new ArrayList<>();
	private static List<String> partners = new ArrayList<>();
	private static List<String> creditRequirements = new ArrayList<>();

	private static List<String> companyList = new ArrayList<>();

	private static final String SHARE = "shares";
	private static final String WITH = "with";

	private static final String PARAM_COMPANY = "$COMPANY";
	private static final String PARAM_CREDIT_RATING = "$CREDIT_RATING";
	private static final String PARAM_OPERATION_YEARS = "$OPERATION_YEARS";

	static {
		resourceOwners.add("CompanyA");
		resourceOwners.add("CompanyB");
		resourceOwners.add("CompanyC");
		resourceOwners.add("CompanyD");
		resourceOwners.add("CompanyE");
		resourceOwners.add("CompanyF");

		resourceTypes.add("soft resources");
		resourceTypes.add("hard resources");
		resourceTypes.add("resources");
		resourceTypes.add("machining resources");
		resourceTypes.add("OKUMA MP-46V");

		partners.add("its own company");
		partners.add("NZ-based company");
		partners.add("the public cloud");
		partners.add("specific companies, i.e., Company " + PARAM_COMPANY);
		partners.add("companies");
		partners.add("private limited companies");

		creditRequirements.add("");
		creditRequirements.add("having a credit rating higher than " + PARAM_CREDIT_RATING + " (out of 10.0)");
		creditRequirements.add("in operation for more than " + PARAM_OPERATION_YEARS + " years");

		companyList.add("A");
		companyList.add("B");
		companyList.add("C");
		companyList.add("D");
		companyList.add("E");
		companyList.add("F");
	}

	private static List<String> assembleRules() {
		final List<String> rules = new ArrayList<>();

		for (final String resourceOwner : resourceOwners) {
			for (final String resourceType : resourceTypes) {
				for (final String partner : partners) {
					final String rule = resourceOwner + " " + SHARE + " " + resourceType + " " + WITH + " " + partner;
					if (partner.contains("own") || partner.contains("specific") || partner.contains("public")) {
						rules.add(rule);
						continue;
					}
					for (final String creditRequirement : creditRequirements) {
						if (partner.equals("companies") && creditRequirement.equals("")) {
							continue;
						}
						final String newRule = rule + " " + creditRequirement;
						rules.add(newRule);
					}
				}
			}
		}

		return rules;
	}

	private static List<String> replaceVariables(final List<String> rules) {
		final List<String> finalRules = new ArrayList<>();
		final Random random = new Random();

		for (final String rule : rules) {
			final int randomValue = random.nextInt(4) + 5;
			String finalRule = "";

			// replace $COMPANY
			if (rule.indexOf(PARAM_COMPANY) != -1) {
				final int length = "Company".length();
				final String company = rule.substring(length, length + 1);

				final String companies = buildCompanies(company);
				finalRule = rule.replace(PARAM_COMPANY, companies);
			}
			// replace $OPERATION_YEARS
			else if (rule.indexOf(PARAM_OPERATION_YEARS) != -1) {
				final String operationYears = randomValue + "";
				finalRule = rule.replace(PARAM_OPERATION_YEARS, operationYears);
			}
			// replace $CREDIT_RATING
			else if (rule.indexOf(PARAM_CREDIT_RATING) != -1) {
				final String creditRating = randomValue + ".0";
				finalRule = rule.replace(PARAM_CREDIT_RATING, creditRating);
			}
			// no need to replace anything
			else {
				finalRule = rule;
			}

			finalRules.add(finalRule);
		}

		return finalRules;
	}

	private static String buildCompanies(final String owner) {
		final Random random = new Random();
		String companies = "";

		// 1) i.e., A and B; 2) i.e., A, B, and E;
		final int number = random.nextInt(2) + 2;
		int count = 0;

		for (final String company : companyList) {
			if (count >= number) {
				break;
			}
			if (company.equals(owner)) {
				continue;
			}
			companies += company + ", ";
			count++;
		}

		companies = companies.substring(0, companies.lastIndexOf(","));

		// replace the last "," with "and"
		final String[] companyArray = companies.split(",");
		if (companyArray.length == 2) {
			companies = companies.replace(",", " and");
		} else {
			final String lastPart = companies.substring(companies.lastIndexOf(",") + 1, companies.length());
			companies = companies.substring(0, companies.lastIndexOf(",") + 1) + " and" + lastPart;
		}

		companies += ";";
		return companies;
	}

	public static void main(final String[] args) {
		final Random random = new Random();

		final List<String> rules = assembleRules();
		final List<String> finalRules = replaceVariables(rules);

		final List<String> selectedRules = new ArrayList<>();
		final List<Integer> selectedList = new ArrayList<>();

		final int number = 66;
		int count = 0;

		while (count < number) {
			final int selectedId = random.nextInt(finalRules.size());
			if (selectedList.contains(selectedId)) {
				continue;
			}

			selectedRules.add(finalRules.get(selectedId));
			System.out.println(finalRules.get(selectedId));

			selectedList.add(selectedId);
			count++;
		}

		System.out.println(selectedRules.size());
	}
}
