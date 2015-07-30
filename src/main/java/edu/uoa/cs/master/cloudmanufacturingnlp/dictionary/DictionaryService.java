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
