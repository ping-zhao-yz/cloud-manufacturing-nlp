package edu.uoa.cs.master.cloudmanufacturingnlp.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

public class Dictionary {

	private Map<String, List<String>> synonyms = new HashMap<>();
	private Map<String, String> countryCodeToCountryMap = new HashMap<>();
	private List<String> countryList = new ArrayList<>();

	private static Dictionary dictionary = new Dictionary();

	private Dictionary() {
		Tools.loadSynonyms(synonyms, Constants.FilePath.SYNONYM);
		Tools.loadCountryCodes(countryCodeToCountryMap, countryList, Constants.FilePath.COUNTRY_CODE);
	}

	public static Dictionary getIntance() {
		return dictionary;
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
