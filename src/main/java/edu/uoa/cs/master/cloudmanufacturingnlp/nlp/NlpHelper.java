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
package edu.uoa.cs.master.cloudmanufacturingnlp.nlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import edu.uoa.cs.master.cloudmanufacturingnlp.dictionary.DictionaryService;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class NlpHelper {

	private DictionaryService dictionaryService;

	public NlpHelper(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	/**
	 * Lookup the dependent from the triples.
	 * 
	 * @param gov
	 * @param reln
	 * @return
	 */
	public String lookupDep(Map<String, Map<String, String>> triples, String gov, String reln) {
		String dep = null;

		Map<String, String> dep_reln_map = triples.get(gov);
		if (dep_reln_map == null) {
			return dep;
		}

		Iterator<Entry<String, String>> it = dep_reln_map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();

			if (reln.equals(entry.getValue())) {
				dep = entry.getKey();
				break;
			}
		}

		return dep;
	}

	/**
	 * Lookup the list of dependents from the triples.
	 * 
	 * @param gov
	 * @param reln
	 * @return
	 */
	public List<String> lookupDeps(Map<String, Map<String, String>> triples, String gov, String reln) {
		List<String> deps = null;

		Map<String, String> dep_reln_map = triples.get(gov);
		if (dep_reln_map == null) {
			return deps;
		}

		Iterator<Entry<String, String>> it = dep_reln_map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();

			if (this.dictionaryService.isSynonym(entry.getValue(), reln)) {
				if (deps == null) {
					deps = new ArrayList<String>();
				}
				deps.add(entry.getKey());
			}
		}

		return deps;
	}

	/**
	 * Retrieve the base location which can be: NZ, New Zealand, Auckland, etc.
	 * 
	 * @param originalString
	 * @return
	 */
	public String getBaseLocation(String originalString) {

		// remove the suffix
		String tempString = Tools.removeDashSuffix(originalString);

		if (tempString.toLowerCase().endsWith(Constants.Nlp.BASED)) {
			tempString = tempString.substring(0, tempString.toLowerCase().indexOf(Constants.Nlp.BASED));
		}

		tempString = tempString.trim();
		if (tempString.endsWith("-")) {
			tempString = tempString.substring(0, tempString.length() - 1);
		}

		String name = Tools.upperCaseString(tempString);
		String countryName = this.dictionaryService.getCountryName(name);

		return StringUtils.isBlank(countryName) ? name : countryName;
	}

	/**
	 * Get the comparison value. 1: more than -1: less than 0: equal
	 * 
	 * @param triples
	 * @param yearsNum
	 * @return
	 */
	public String getComparison(Map<String, Map<String, String>> triples, String yearsNum) {
		String quantmod = lookupDep(triples, yearsNum, Constants.Nlp.QUANTMOD);
		if (this.dictionaryService.isSynonym(quantmod, "than")) {
			return lookupDep(triples, quantmod, Constants.Nlp.MWE);
		}

		return null;
	}
}
