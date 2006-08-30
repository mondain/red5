package org.red5.server.script;

import static org.junit.Assert.assertFalse;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.Test;

/**
 * Simple script engine tests. Some of the hello world scripts found here:
 * http://www.roesler-ac.de/wolfram/hello.htm
 * 
 * @author paul.gregoire
 */
public class ScriptEngineTest
{

	// ScriptEngine manager
	private static ScriptEngineManager mgr = new ScriptEngineManager();

	// Javascript
	@Test
	public void testJavascriptHelloWorld()
	{
		ScriptEngine jsEngine = mgr.getEngineByName("rhino");
		try
		{
			jsEngine.eval("print('Javascript - Hello, world!\\n')");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Ruby
	@Test
	public void testRubyHelloWorld()
	{
		ScriptEngine rbEngine = mgr.getEngineByName("ruby");
		try
		{
			rbEngine.eval("puts 'Ruby - Hello, world!'");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Python
	@Test
	public void testPythonHelloWorld()
	{
		ScriptEngine pyEngine = mgr.getEngineByName("python");
		try
		{
			pyEngine.eval("print \"Python - Hello, world!\"");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Groovy
	@Test
	public void testGroovyHelloWorld()
	{
		ScriptEngine gvyEngine = mgr.getEngineByName("groovy");
		try
		{
			gvyEngine.eval("println  \"Groovy - Hello, world!\"");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Judoscript
	@Test
	public void testJudoscriptHelloWorld()
	{
		ScriptEngine jdEngine = mgr.getEngineByName("judo");
		try
		{
			jdEngine.eval(". \"Judoscript - Hello World\";");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Haskell
	@Test
	public void testHaskellHelloWorld()
	{
		ScriptEngine hkEngine = mgr.getEngineByName("jaskell");
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append("module Hello where\n");
			sb.append("hello::String\n");
			sb.append("hello = \"Haskell - Hello World!\"");

			hkEngine.eval(sb.toString());
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Tcl
	@Test
	public void testTclHelloWorld()
	{
		ScriptEngine tEngine = mgr.getEngineByName("tcl");
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append("#!/usr/local/bin/tclsh\n");
			sb.append("puts \"Tcl - Hello World!\"");

			tEngine.eval(sb.toString());
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// Awk
	@Test
	public void testAwkHelloWorld()
	{
		ScriptEngine aEngine = mgr.getEngineByName("awk");
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append("BEGIN {\n");
			sb.append("  print \"Awk - Hello World!\"\n");
			sb.append("}");
			sb.append("END");

			aEngine.eval(sb.toString());
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// E4X
	@Test
	public void testE4XHelloWorld()
	{
		ScriptEngine eEngine = mgr.getEngineByName("rhino");
		try
		{
			eEngine.eval("var doc = '<mydocument><item>E4X - </item><item>Hello</item><item>World!</item></mydocument>';print doc;");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	// PHP
	@Test
	public void testPHPHelloWorld()
	{
		//have to add php lib to java env
		//java.library.path 
		//System.setProperty("java.library.path", "C:\\PHP;" + System.getProperty("java.library.path"));
		ScriptEngine pEngine = mgr.getEngineByName("php");
		try
		{
			pEngine.eval("<? echo 'PHP - Hello World'; ?>");
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}	
	
	@Test
	public void testE4X()
	{
		// Javascript
		ScriptEngine jsEngine = mgr.getEngineByName("rhino");
		try
		{
			System.out.println("Engine: " + jsEngine.getClass().getName());
			jsEngine.eval(new FileReader("samples/E4X/e4x_example.js"));
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	@Test
	public void testRubySwing()
	{
		ScriptEngine rbEngine = mgr.getEngineByName("ruby");
		try
		{
			System.out.println("Engine: " + rbEngine.getClass().getName());
			rbEngine.eval(new FileReader("samples/ex.rb"));
		}
		catch (Exception ex)
		{
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	@Test
	public void testEngines()
	{
		Map<String, ScriptEngineFactory> engineFactories = new HashMap<String, ScriptEngineFactory>(7);

		// List<ScriptEngineFactory> factories = mgr.getEngineFactories(); //jdk6
		// style
		ScriptEngineFactory[] factories = mgr.getEngineFactories();
		for (ScriptEngineFactory factory : factories)
		{
			System.out.println("ScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);

			engineFactories.put(engName, factory);

			// List<String> engNames = factory.getNames(); //jdk6 style
			String[] engNames = factory.getNames();
			for (String name : engNames)
			{
				System.out.printf("\tEngine Alias: %s\n", name);
			}
			String[] ext = factory.getExtensions();
			for (String name : ext)
			{
				System.out.printf("\tExtension: %s\n", name);
			}
			System.out.println(" ------------------------------- ");
		}

	}

}
