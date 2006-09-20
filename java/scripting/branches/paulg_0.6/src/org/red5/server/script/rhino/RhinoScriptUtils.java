/*
 * Copyright 2002-2006 the original author or authors.
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
 */

package org.red5.server.script.rhino;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.regex.PatternSyntaxException;

import javax.script.Invocable;
import javax.script.Namespace;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleNamespace;

import org.jruby.exceptions.JumpException;
import org.red5.server.script.ScriptCompilationException;
import org.springframework.util.ClassUtils;

/**
 * Utility methods for handling Rhino / Javascript objects.
 * 
 * @author Paul Gregoire
 * @since 0.6
 */
public abstract class RhinoScriptUtils {

	/**
	 * Create a new Rhino-scripted object from the given script source.
	 * 
	 * @param scriptSource
	 *            the script source text
	 * @param interfaces
	 *            the interfaces that the scripted Java object is supposed to
	 *            implement
	 * @return the scripted Java object
	 * @throws JumpException
	 *             in case of Rhino parsing failure
	 */
	public static Object createRhinoObject(String scriptSource,
			Class[] interfaces) throws ScriptCompilationException, Exception {
		//System.out.println("\n" + scriptSource + "\n");
		//JSR223 style
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("rhino");
		// set engine scope namespace
		Namespace nameSpace = engine.createNamespace();
		if (null == nameSpace) {
			System.out.println("Engine namespace not created, using simple");
			nameSpace = new SimpleNamespace();
			System.out.println("Setting ns");
			engine.setNamespace(nameSpace, ScriptContext.ENGINE_SCOPE);
		}
		// add the logger to the script
		nameSpace.put("log", RhinoScriptFactory.log);

		return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), interfaces, new RhinoObjectInvocationHandler(engine, nameSpace, scriptSource, interfaces));
	}

	/**
	 * InvocationHandler that invokes a Rhino script method.
	 */
	private static class RhinoObjectInvocationHandler implements InvocationHandler {

		private final Invocable invocable;

		public RhinoObjectInvocationHandler(ScriptEngine engine, Namespace nameSpace, String scriptSource, Class[] interfaces) {
			try {
				//get the function name ie. class name
				String funcName = RhinoScriptUtils.getFunctionName(scriptSource);
				RhinoScriptFactory.log.debug("Function: " + funcName);
				//run it
				engine.eval(scriptSource, nameSpace);
				//get invocable
				this.invocable = (Invocable) engine;
				//call the constructor
				//Object o = invocable.call(funcName, new Object[]{""});
				Object o = invocable.call(funcName, interfaces);
				RhinoScriptFactory.log.debug("Result of script constructor call: " + o);
			} catch (Exception ex) {
				throw new ScriptCompilationException("Could not compile Rhino script: " + scriptSource, ex);
			}
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//			RhinoScriptFactory.log.debug("Proxy: " + proxy.getClass().getName());	
//
//			Method[] methods = proxy.getClass().getMethods();
//            Method m = null;
//            String name = null;
//            for (Method element : methods) {
//                m = element;
//                name = m.getName();			
//                RhinoScriptFactory.log.debug("Proxy method: " + name); 
//            }			
//			RhinoScriptFactory.log.debug("Get interface: " + invocable.getInterface(proxy.getClass()));			
			//ensure a set of args are available
			if (args == null || args.length == 0) {
				args = new Object[]{""};
			}
			RhinoScriptFactory.log.debug("Calling: "  + method.getName());
			return  invocable.call(method.getName(), args);
		}

	}	
	
	/**
	 * Uses a regex to get the first "function" name, this name
	 * is used to create an instance of the javascript object.
	 * 
	 * @param scriptSource
	 * @return
	 */
	private static String getFunctionName(String scriptSource) {
		String ret = "undefined";
		try {
			ret = scriptSource.replaceAll(
					"[\\S\\w\\s]*?function ([\\w]+)\\(\\)[\\S\\w\\s]+", "$1");
		} catch (PatternSyntaxException ex) {
			// Syntax error in the regular expression
		} catch (IllegalArgumentException ex) {
			// Syntax error in the replacement text (unescaped $ signs?)
		} catch (IndexOutOfBoundsException ex) {
			// Non-existent backreference used the replacement text
		}
		return ret;
	}

}
