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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class DictionaryService {

	/**
	 * e.g. with-5 vs. with
	 * 
	 * @param resource
	 * @param originalString
	 * @return
	 */
	public boolean isSynonym(String resource, String originalString) {

		// validation
		if (StringUtils.isEmpty(resource) || StringUtils.isEmpty(originalString)) {
			return false;
		}

		// directly match
		String lowerCaseResource = Tools.lowerCaseString(resource);
		String lowerCaseOriginalString = Tools.lowerCaseString(originalString);
		if (lowerCaseResource.equalsIgnoreCase(lowerCaseOriginalString)) {
			return true;
		}

		if (lowerCaseResource.contains(lowerCaseOriginalString) || lowerCaseOriginalString.contains(lowerCaseResource)) {
			return true;
		}

		// trim the suffix and match
		String resourceWithoutDashSuffix = Tools.removeDashSuffix(lowerCaseResource);
		String originalStringWithoutDashSuffix = Tools.removeDashSuffix(lowerCaseOriginalString);
		if (resourceWithoutDashSuffix.equalsIgnoreCase(originalStringWithoutDashSuffix)) {
			return true;
		}

		if (resourceWithoutDashSuffix.contains(originalStringWithoutDashSuffix)
				|| originalStringWithoutDashSuffix.contains(resourceWithoutDashSuffix)) {
			return true;
		}

		// lookup the synonym and match
		if (doesSynonymMatch(resourceWithoutDashSuffix, originalStringWithoutDashSuffix)) {
			return true;
		}
		if (doesSynonymMatch(originalStringWithoutDashSuffix, resourceWithoutDashSuffix)) {
			return true;
		}

		return false;
	}

	private boolean doesSynonymMatch(String resource, String originalString) {
		List<String> synonyms = Dictionary.getIntance().lookupSynonym(originalString);

		if (synonyms != null && !synonyms.isEmpty()) {
			for (String synonym : synonyms) {
				if (StringUtils.isBlank(synonym) || StringUtils.isBlank(resource)) {
					continue;
				}
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
	public String getCountryName(String name) {
		return Dictionary.getIntance().getCountryName(name);
	}

	public static void main(String[] args) {
		DictionaryService dictionaryService = new DictionaryService();

		String originalString = "share";
		String resource = "mcloud:hasAccess";

		if (dictionaryService.isSynonym(originalString, resource)) {
			System.out.println(originalString + " is a synonym of " + resource);
		}
	}
}
