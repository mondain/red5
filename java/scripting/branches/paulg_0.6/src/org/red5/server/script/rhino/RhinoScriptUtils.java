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

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.Namespace;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleNamespace;

import org.mozilla.javascript.ScriptableObject;
import org.red5.server.script.ScriptCompilationException;
import org.springframework.util.ClassUtils;

import com.sun.script.javascript.ExternalScriptable;
import com.sun.script.javascript.RhinoScriptEngine;

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
	 * @throws ScriptCompilationException
	 *             in case of Rhino parsing failure
	 */
	public static Object createRhinoObject(String scriptSource,
			Class[] interfaces, Class extendedClass) throws ScriptCompilationException, Exception {
		//System.out.println("\n" + scriptSource + "\n");
		//JSR223 style
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("rhino");
		// set engine scope namespace
		Namespace nameSpace = engine.createNamespace();
		if (null == nameSpace) {
			System.out.println("Engine namespace not created, using simple");
			nameSpace = new SimpleNamespace();
			//set namespace
			System.out.println("Setting ns");
			engine.setNamespace(nameSpace, ScriptContext.ENGINE_SCOPE);
		}
		// add the logger to the script
		nameSpace.put("log", RhinoScriptFactory.log);
		// add the interfaces to the ns
		nameSpace.put("interfaces", interfaces);
		//use a string builder to control proto prefix
		StringBuilder sb = new StringBuilder();
		//add prototypes (extensions and interfaces)
		if (null != extendedClass) {
			sb.append("__proto__");
			nameSpace.put(sb.toString(), extendedClass.newInstance());
			sb.append('.');
		}
		for (Class interfac : interfaces) {
			sb.append("__proto__");
			nameSpace.put(sb.toString(), interfac);
			sb.append('.');
		}
		System.out.println("Proto chain: " + sb.toString());
		//compile the script
		CompiledScript script = ((Compilable) engine).compile(scriptSource);
		//see if the script returns an instance of the "class"
		Object o = script.eval(nameSpace);
		RhinoScriptFactory.log.debug("Result of script call: " + o);
		//null result so try constructor
		if (null == o) {
			//get the function name ie. class name
			String funcName = RhinoScriptUtils.getFunctionName(scriptSource);
			RhinoScriptFactory.log.debug("Function: " + funcName);
			//if function name is not null call it
			if (null != funcName) {
				o = ((Invocable) engine).call(funcName, interfaces);
				RhinoScriptFactory.log.debug("Result of script constructor call: " + o);
				if (null == o) {
					o = engine.getNamespace(ScriptContext.ENGINE_SCOPE).get(funcName);	
					RhinoScriptFactory.log.debug("Result of lookup with constructor name: " + o);
				}
			} 
			//if the result is still null then look for an instance
			if (null == o) {
				o = engine.getNamespace(ScriptContext.ENGINE_SCOPE).get("instance");	
			}
		}
		if (null == o) {
			throw new ScriptCompilationException("Compilation of Rhino script returned '" + o + "'");
		}		
		
		return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), interfaces, new RhinoObjectInvocationHandler(engine, o));
	}

	/**
	 * InvocationHandler that invokes a Rhino script method.
	 */
	private static class RhinoObjectInvocationHandler implements InvocationHandler {

		private final ScriptEngine engine;
		//private ExternalScriptable instance;
		private Object instance;

		public RhinoObjectInvocationHandler(ScriptEngine engine, Object instance) {
			this.engine = engine;
			this.instance = instance;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object o = null;
			//ensure a set of args are available
			if (args == null || args.length == 0) {
				args = new Object[]{""};
			}		
			Invocable invocable = (Invocable) engine;
			RhinoScriptFactory.log.debug("Calling: "  + method.getName());

//			for (Class interfac : interfaces) {
//				dump(interfac);
//				if (interfac.isInterface()) {
//					o = invocable.getInterface(interfac);
//					System.out.println("Interface: " + o.getClass().getName());
//					dump(o);
//					return o;
//				}

			Namespace nameSpace = engine.getNamespace(ScriptContext.ENGINE_SCOPE);
			Class[] interfaces = (Class[]) nameSpace.get("interfaces");
			dump(invocable.getInterface(interfaces[0]));
			dump(invocable.getInterface(interfaces[1]));
			dump(instance);
			try {
				o = invocable.call(method.getName(), args);
			} catch(Throwable t) {
				ExternalScriptable ext = new ExternalScriptable((RhinoScriptEngine) engine);
				o = ScriptableObject.callMethod(ext, method.getName(), args);
			}
			return o;
		}
	}	

	private static void dump(Object c) {
		if (!RhinoScriptFactory.log.isDebugEnabled()) {
			return;
		}
		System.out.println("Name: " + c.getClass().getName());
		System.out.println("==============================================================================");
		Method[] methods = c.getClass().getMethods();
        Method m = null;
        String name = null;
        for (Method element : methods) {
            m = element;
            name = m.getName();			
            RhinoScriptFactory.log.debug("Proxy method: " + name); 
        }			
        System.out.println("==============================================================================");
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
