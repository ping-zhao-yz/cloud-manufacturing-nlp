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
package edu.uoa.cs.master.cloudmanufacturingnlp.dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class DictionaryService {

	/**
	 * e.g. with-5 vs. with
	 *
	 * @param originalTerm
	 * @param ontologyTerm
	 * @return
	 */
	public boolean isSynonym(final String originalTerm, final String ontologyTerm) {

		// validation
		if (StringUtils.isEmpty(originalTerm) || StringUtils.isEmpty(ontologyTerm)) {
			return false;
		}

		// literal match
		final String lowerCaseOriginalTerm = Tools.lowerCaseString(originalTerm);
		final String lowerCaseOntologyTerm = Tools.lowerCaseString(ontologyTerm);
		if (doesLiteralMatch(lowerCaseOriginalTerm, lowerCaseOntologyTerm)) {
			return true;
		}

		final String originalTermWithoutDashSuffix = Tools.removeDashSuffix(lowerCaseOriginalTerm);
		final String ontologyTermWithoutDashSuffix = Tools.removeDashSuffix(lowerCaseOntologyTerm);
		if (doesLiteralMatch(originalTermWithoutDashSuffix, ontologyTermWithoutDashSuffix)) {
			return true;
		}

		// WordNet synonym match
		final Set<String> synonymSet = JwnlWordNet.getInstance().lookupSynonym(ontologyTermWithoutDashSuffix, null);
		if (doesLiteralArrayMatch(originalTermWithoutDashSuffix, synonymSet)) {
			return true;
		}

		// local synonym match
		List<String> synonymList = LocalDictionary.getIntance().lookupSynonym(ontologyTermWithoutDashSuffix);
		if (doesLiteralArrayMatch(originalTermWithoutDashSuffix, synonymList)) {
			return true;
		}

		synonymList = LocalDictionary.getIntance().lookupSynonym(originalTermWithoutDashSuffix);
		if (doesLiteralArrayMatch(ontologyTermWithoutDashSuffix, synonymList)) {
			return true;
		}

		return false;
	}

	/**
	 * @param lowerCaseResource
	 * @param lowerCaseOriginalString
	 */
	private boolean doesLiteralMatch(final String lowerCaseResource, final String lowerCaseOriginalString) {
		if (lowerCaseResource.equalsIgnoreCase(lowerCaseOriginalString)) {
			return true;
		}
		if (lowerCaseResource.contains(lowerCaseOriginalString) || lowerCaseOriginalString.contains(lowerCaseResource)) {
			return true;
		}
		return false;
	}

	private boolean doesLiteralArrayMatch(final String resource, final Collection<String> synonyms) {
		if (synonyms != null && !synonyms.isEmpty()) {
			for (final String synonym : synonyms) {
				if (synonym.equalsIgnoreCase(resource)) {
					return true;
				}
			}

			for (final String synonym : synonyms) {
				if ((synonym.contains(resource)) || resource.contains(synonym)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Country name can be: NZ or New Zealand.
	 *
	 * @param name
	 * @return
	 */
	public String getCountryName(final String name) {
		return LocalDictionary.getIntance().getCountryName(name);
	}

	public static void main(final String[] args) {
		final DictionaryService dictionaryService = new DictionaryService();

		final String originalString = "share";
		final String resource = "mcloud:hasAccess";

		if (dictionaryService.isSynonym(originalString, resource)) {
			System.out.println(originalString + " is a synonym of " + resource);
		}
	}
}
