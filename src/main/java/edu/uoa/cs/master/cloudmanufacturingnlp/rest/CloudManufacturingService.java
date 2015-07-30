package edu.uoa.cs.master.cloudmanufacturingnlp.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.uoa.cs.master.cloudmanufacturingnlp.dao.JenaRulesDAO;
import edu.uoa.cs.master.cloudmanufacturingnlp.semanticwebrules.SemanticWebRulesService;
import edu.uoa.cs.master.cloudmanufacturingnlp.util.Tools;

@Path("/")
public class CloudManufacturingService {
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_HTML)
	public String test() {
		return generateResponse("The Cloud Manufacturing Services are runing!");
	}

	@GET
	@Path("generate-jena-rule-html")
	@Produces(MediaType.TEXT_HTML)
	public String generateJenaRuleHtml(@QueryParam("rule") String naturalLanguageRule) {

		String jenaRuleText = generateJenaRuleText(naturalLanguageRule);
		if (jenaRuleText == null) {
			jenaRuleText = "There is no Jena rule generated due to the server error!";
		}

		String jenaRule = Tools.replaceSpecialCharacters(jenaRuleText);
		return generateResponse(generateJenaResponse(naturalLanguageRule, jenaRule));
	}

	@GET
	@Path("generate-jena-rule-text")
	@Produces(MediaType.TEXT_PLAIN)
	public String generateJenaRuleText(@QueryParam("rule") String naturalLanguageRule) {

		String[] jenaRules = new SemanticWebRulesService().process(naturalLanguageRule);
		if (jenaRules != null) {
			JenaRulesDAO.getInstance().insertRule(jenaRules);
			return Tools.getContentFromArray(jenaRules);
		}
		return null;
	}

	private String generateJenaResponse(String naturalLanguageRule, String jenaRule) {
		return "<div>The original natural language rule is:<br/>"
				+ naturalLanguageRule + "<br/><br/>"
				+ "The generated Jena rule is:<br/>"
				+ jenaRule + "</div>";
	}

	private String generateResponse(String responseContent) {
		return "<html> <head><title>Cloud Manufacturing Services</title></head> "
				+ "<body> <h1>Jena rules auto generation</h1> "
				+ responseContent + "</body></html>";
	}

}
