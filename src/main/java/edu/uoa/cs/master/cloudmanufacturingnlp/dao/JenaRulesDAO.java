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
