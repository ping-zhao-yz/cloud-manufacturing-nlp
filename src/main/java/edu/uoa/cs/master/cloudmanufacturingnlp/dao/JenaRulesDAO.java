package edu.uoa.cs.master.cloudmanufacturingnlp.dao;

import java.util.ArrayList;
import java.util.List;

public class JenaRulesDAO {
	private static JenaRulesDAO instance = new JenaRulesDAO();

	private JenaRulesDAO() {
		// do nothing;
	}

	public static JenaRulesDAO getInstance() {
		return instance;
	}

	public void insertRule(String[] rule) {

	}

	public String queryRule() {
		return null;
	}

	public List<String> queryRules() {
		List<String> rules = new ArrayList<String>();

		return rules;
	}
}
