package org.red5.server.script;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import static org.junit.Assert.*;
import org.junit.Test;

public class ScriptEngineTest {

	//ScriptEngine manager
	private static ScriptEngineManager mgr = new ScriptEngineManager();	
	
	@Test
	public void testE4X() {
		//Javascript
		ScriptEngine jsEngine = mgr.getEngineByName("rhino");
		try {
			System.out.println("Engine: " + jsEngine.getClass().getName());
			jsEngine.eval(new FileReader("/samples/E4X/e4x_example.js"));
		} catch (Exception ex) {
			assertFalse(true);
			ex.printStackTrace();
		}		
	}
	
	@Test
	public void testEngines() {
		Map<String, ScriptEngineFactory> engineFactories = new HashMap<String, ScriptEngineFactory>(7);
		
		//List<ScriptEngineFactory> factories = mgr.getEngineFactories(); //jdk6 style
		ScriptEngineFactory[] factories = mgr.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			System.out.println("ScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
			
			engineFactories.put(engName, factory);
			
			//List<String> engNames = factory.getNames(); //jdk6 style
			String[] engNames = factory.getNames();
			for (String name : engNames) {
				System.out.printf("\tEngine Alias: %s\n", name);
			}
			String[] ext = factory.getExtensions();
			for (String name : ext) {
				System.out.printf("\tExtension: %s\n", name);
			}
			System.out.println(" ------------------------------- ");
		}		
		
		//Javascript
		ScriptEngine jsEngine = mgr.getEngineByName("rhino");
		try {
			jsEngine.eval("print('Javascript - Hello, world!\\n')");
		} catch (Exception ex) {
			assertFalse(true);
			ex.printStackTrace();
		}

		//Ruby
		ScriptEngine rbEngine = mgr.getEngineByName("ruby");
		try {
			rbEngine.eval("puts 'Ruby - Hello, world!'");
		} catch (Exception ex) {
			assertFalse(true);
			ex.printStackTrace();
		}		

		//Python
		ScriptEngine pyEngine = mgr.getEngineByName("python");
		try {
			pyEngine.eval("print \"Python - Hello, world!\"");
		} catch (Exception ex) {
			assertFalse(true);			
			ex.printStackTrace();
		}		
		
		//Groovy
		ScriptEngine gvyEngine = mgr.getEngineByName("groovy");
		try {
			gvyEngine.eval("println  \"Groovy - Hello, world!\"");
		} catch (Exception ex) {
			assertFalse(true);
			ex.printStackTrace();
		}			

	}
	
}
