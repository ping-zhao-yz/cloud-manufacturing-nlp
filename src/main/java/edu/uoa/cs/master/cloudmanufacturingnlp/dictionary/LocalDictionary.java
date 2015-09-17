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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class LocalDictionary {

	private Map<String, List<String>> synonyms = new HashMap<>();
	private Map<String, String> countryCodeToCountryMap = new HashMap<>();
	private List<String> countryList = new ArrayList<>();

	private static LocalDictionary instance = new LocalDictionary();

	private LocalDictionary() {
		Tools.loadSynonyms(synonyms, Constants.FilePath.SYNONYM);
		Tools.loadCountryCodes(countryCodeToCountryMap, countryList, Constants.FilePath.COUNTRY_CODE);
	}

	public static LocalDictionary getIntance() {
		return instance;
	}

	public List<String> lookupSynonym(String originalString) {
		return synonyms.get(originalString);
	}

	public String getCountryName(String name) {
		if (this.countryList.contains(name)) {
			return name;
		}

		if (this.countryCodeToCountryMap.containsKey(name)) {
			return this.countryCodeToCountryMap.get(name);
		}
		return null;
	}
}
