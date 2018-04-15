/**
 * 
 */
package nl.ou.testar.tgherkin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Before;
import org.junit.Test;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.TgherkinErrorListener;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.Document;

/**
 * Test DocumentBuilder class.
 * This JUnit test verifies the created document model.
 * This class checks whether the Tgherkin source code is equal to the result of the 
 * toString method of the created Document class (ignoring spaces, tabs and eol).  
 *
 */
public class DocumentBuilderTest {

	private List<String> testList = new ArrayList<String>();
	
	@Before
	public void setUp() throws Exception {
		// test case 1
		testList.add("Feature: Compute with Windows calculator. \r\n" + 
				"\r\n" + 
				"	Selection: click()\r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Step 1 \r\n" + 
				"		When  $Title=\"Een\" click()\r\n" + 
				"    Step: Step 2 \r\n" + 
				"		When  $Title=\"Een\" or $Title=\"Twee\" or $Title=\"Drie\" click()\r\n");
		
		// test case 2
		testList.add("Feature: Uitvoeren berekeningen met windows calculator. \r\n" + 
				"\r\n" + 
				"  Background: Enter 5 en clear\r\n" + 
				"    Step: Selecteer 5\r\n" + 
				"		When  $Title=\"Vijf\" click()\r\n" + 
				"    Step: Selecteer Clear\r\n" + 
				"		When  $Title=\"Wissen\" click()\r\n" + 
				"		\r\n" + 
				"  Scenario: Optellen twee getallen\r\n" + 
				"    Step: Selecteer 2\r\n" + 
				"		When  $Title=\"Twee\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 2\"\r\n" + 
				"    Step: Selecteer +\r\n" + 
				"		When  $Title=\"Plus\" click()\r\n" + 
				"		Then  $Title=\"Expressie is 2 + \"\r\n" + 
				"    Step: Selecteer 3\r\n" + 
				"		When  $Title=\"Drie\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 3\"\r\n" + 
				"    Step: Selecteer = \r\n" + 
				"		When  $Title=\"Is gelijk aan\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 5\"\r\n" + 
				"\r\n" + 
				"  Scenario Outline: Optellen twee getallen\r\n" + 
				"    Step: Selecteer getal 1\r\n" + 
				"		When  $Title=<getal1> click()\r\n" + 
				"    Step: Selecteer +\r\n" + 
				"		When  $Title=\"Plus\" click()\r\n" + 
				"    Step: Selecteer getal 2\r\n" + 
				"		When  $Title=<getal2> click()\r\n" + 
				"    Step: Selecteer = \r\n" + 
				"		When  $Title=\"Is gelijk aan\" click()\r\n" + 
				"  Examples: Examples titel\r\n" + 
				"    | getal1 | getal2 |\r\n" + 
				"    |  Negen  |  Vier |\r\n" + 
				"    |  Zeven  |  Zes  |  \r\n" + 
				"	\r\n" + 
				"  Scenario: Optellen twee getallen\r\n" + 
				"    Step: Selecteer 8\r\n" + 
				"		When  $Title=\"Acht\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 8\"\r\n" + 
				"    Step: Selecteer +\r\n" + 
				"		When  $Title=\"Plus\" click()\r\n" + 
				"		Then  $Title=\"Expressie is 8 + \"\r\n" + 
				"    Step: Selecteer 1\r\n" + 
				"		When  $Title=\"Een\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 1\"\r\n" + 
				"    Step: Selecteer = \r\n" + 
				"		When  $Title=\"Is gelijk aan\" click()\r\n" + 
				"		Then  $Title=\"Weergave is 9\"\r\n" + 
				"\r\n" + 
				"\r\n");
	}

	@Test
	public void test() {
		for (String expression : testList) {	
			ANTLRInputStream inputStream = new ANTLRInputStream(expression);
			TgherkinLexer lexer = new TgherkinLexer(inputStream);
			TgherkinParser parser = new TgherkinParser(new CommonTokenStream(lexer));
		    TgherkinErrorListener errorListener = new TgherkinErrorListener();
			parser.removeErrorListeners();
			parser.addErrorListener(errorListener);
			Document document = new DocumentBuilder().visitDocument(parser.document());
			List<String> errorList = errorListener.getErrorList();
			if (errorList.size() == 0) {
				// post-processing check
				errorList = document.check();
			}
			if (errorList.size() != 0) {
				for(String errorText : errorList) {
					LogSerialiser.log(errorText, LogSerialiser.LogLevel.Info);
				}
				throw new TgherkinException("Invalid Tgherkin document, see log for details");
			}
			String inString = expression.replaceAll("\\n|\\r|\\s|\\t","");
			String outString = document.toString().replaceAll("\\n|\\r|\\s|\\t","");
			assertTrue(inString.equals(outString));
		}
		
	}

}
