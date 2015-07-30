package edu.uoa.cs.master.cloudmanufacturingnlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Tools {

	public static String lowerCaseString(String string) {
		return string.trim().toLowerCase();
	}

	public static String upperCaseString(String string) {
		return string.trim().toUpperCase();
	}

	public static boolean doesMatch(String resource, String originalString) {
		// validation
		if (StringUtils.isEmpty(resource) || StringUtils.isEmpty(originalString)) {
			return false;
		}

		// directly match
		if (resource.contains(originalString) || originalString.contains(resource)) {
			return true;
		}
		return false;
	}

	public static String getContentFromArray(String[] args) {

		StringBuilder builder = new StringBuilder(1024);
		for (String arg : args) {
			builder.append(StringUtils.isEmpty(arg) ? "" : arg + "; ");
		}

		String buffer = builder.toString().trim();
		if (buffer.endsWith(";")) {
			buffer = buffer.substring(0, buffer.length() - 1);
		}
		return buffer;
	}

	// replace "<-" with "&lt;-"
	public static String replaceSpecialCharacters(String originalString) {
		return originalString.replace("<-", "&lt;-").replace("; ", "<br/>");
	}

	public static String removePrefix(String originalString) {
		int pos = originalString.indexOf(":");

		if (pos != -1) {
			return originalString.substring(pos + 1).trim();
		}

		return originalString;
	}

	/**
	 * Remove the suffix of a String. e.g. companyB-2, E.
	 * 
	 * @param originalString
	 * @return
	 */
	public static String removeDashSuffix(String originalString) {

		// remove suffix of dash
		int posOfDash = originalString.lastIndexOf("-");
		if (posOfDash == -1) {
			return originalString;
		}

		String suffix = originalString.substring(posOfDash + 1, originalString.length());
		for (char character : suffix.toCharArray()) {
			if ((character < '0') || (character > '9')) {
				return originalString;
			}
		}

		String stringWithoutDash = originalString.substring(0, posOfDash);

		// remove suffix of full stop
		if (stringWithoutDash.endsWith(".")) {
			return stringWithoutDash.substring(0, stringWithoutDash.length() - 1);
		}
		return stringWithoutDash;
	}

	public static void loadSynonyms(Map<String, List<String>> synonyms, String fileName) {

		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (null != line) {
				if (!line.startsWith("/")) {

					String[] pair = line.split(":");
					if (pair.length > 1) {
						List<String> list = new ArrayList<>();
						for (String value : pair[1].split(",")) {
							list.add(lowerCaseString(value));
						}
						synonyms.put(lowerCaseString(pair[0]), list);
					}
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
	}

	public static void loadCountryCodes(Map<String, String> countryCodeToCountryMap, List<String> countryList, String fileName) {

		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (null != line) {
				String[] countries = line.split(",");
				String[] codes = countries[1].split("/");

				for (String code : codes) {
					countryCodeToCountryMap.put(code.trim(), countries[0].trim());
					countryList.add(countries[0].trim());
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
	}

	public static void main(String[] args) {
		// System.out.println(equalsIgnoreSuffix("resources-411", "resources"));
		//		System.out.println(getCountry("NZ-based-6"));
	}
}
