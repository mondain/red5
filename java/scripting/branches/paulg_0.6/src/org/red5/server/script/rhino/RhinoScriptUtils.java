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
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.Namespace;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleNamespace;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.Undefined;
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
		if (null != extendedClass) {
			nameSpace.put("supa", extendedClass);
		}
		//compile the script
		//CompiledScript script = ((Compilable) engine).compile(scriptSource);
		//see if the script returns an instance of the "class"
		//Object o = script.eval(nameSpace);
		//eval the script with the associated namespace
		Object o = engine.eval(scriptSource, nameSpace);
		RhinoScriptFactory.log.debug("Result of script call: " + o);
		if (null != o) dump(o);		
		//get the function name ie. class name
		String funcName = RhinoScriptUtils.getFunctionName(scriptSource);
		RhinoScriptFactory.log.debug("Function: " + funcName);
		//null result so try constructor
		if (null == o) {
			//if function name is not null call it
			if (null != funcName) {
				o = ((Invocable) engine).call(funcName, interfaces);
				RhinoScriptFactory.log.debug("Result of script constructor call: " + o);
				if (null != o) dump(o);
				if (null == o) {
					//o = engine.getNamespace(ScriptContext.ENGINE_SCOPE).get(funcName);	
					//RhinoScriptFactory.log.debug("Result of lookup with constructor name: " + o);
					ObjToIntMap functionNames = new ObjToIntMap();					
//					Object clazz = engine.getNamespace(ScriptContext.ENGINE_SCOPE).get(funcName);
//					for (Method methName : clazz.getClass().getMethods()) {
//						functionNames.put(methName, methName.hashCode());
//						RhinoScriptFactory.log.debug("Adding function name: " + methName);
//					}
					functionNames.put("appStart", "appStart".hashCode());
					functionNames.put("appConnect", "appConnect".hashCode());
					functionNames.put("appDisconnect", "appDisconnect".hashCode());
					//ObjToIntMap functionNames, String adapterName, Class superClass, Class interfaces[], String scriptClassName
					byte[] classBytes = JavaAdapter.createAdapterCode(functionNames, funcName, extendedClass, interfaces, funcName);
					GeneratedClassLoader loader = SecurityController.createLoader(null, null);
					o = loader.defineClass(funcName, classBytes);
					loader.linkClass((Class) o);
					RhinoScriptFactory.log.debug("Result of JavaAdapter: " + o);
				}
			} 
		} else {
			RhinoScriptFactory.log.debug("Result is a function: " + Function.class.isInstance(o));
			RhinoScriptFactory.log.debug("Result is a compiled script: " + CompiledScript.class.isInstance(o));
			RhinoScriptFactory.log.debug("Result is undefined: " + Undefined.class.isInstance(o));
			RhinoScriptFactory.log.debug("Result is a rhino native: " + NativeObject.class.isInstance(o));
			
			ObjToIntMap functionNames = new ObjToIntMap();					
			functionNames.put("appStart", "appStart".hashCode());
			functionNames.put("appConnect", "appConnect".hashCode());
			functionNames.put("appDisconnect", "appDisconnect".hashCode());
			//ObjToIntMap functionNames, String adapterName, Class superClass, Class interfaces[], String scriptClassName
			byte[] classBytes = JavaAdapter.createAdapterCode(functionNames, funcName, extendedClass, interfaces, o.getClass().getName());
			// Setup Contect and ClassLoader
			Context ctx = Context.enter();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			GeneratedClassLoader loader = ctx.createClassLoader(cl);			
			//GeneratedClassLoader loader = SecurityController.createLoader(null, null);
			o = loader.defineClass(funcName, classBytes);
			loader.linkClass((Class) o);
			Context.exit();
			RhinoScriptFactory.log.debug("Result of JavaAdapter 2: " + o);			
			
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
		private final Object instance;

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
			String name = method.getName();
			RhinoScriptFactory.log.debug("Calling: "  + name);
			try {
				if (o instanceof JavaAdapter) {
			        Context cx = Context.enter();
			        try {
			        	Scriptable scope = cx.initStandardObjects();	
			        	//Scriptable thisObj, Function f, Object[] args, long argsToWrap
			        	o = JavaAdapter.callMethod(cx.getFactory(), scope, JavaAdapter.getFunction(scope, name), args, 0L);
			        } finally {
			        	Context.exit();
			        }					
				} else {
					Invocable invocable = (Invocable) engine;
					o = invocable.call(name, args);
				}
			} catch(Throwable t) {
				//RhinoScriptEngine rhinoEng = (RhinoScriptEngine) engine;
				//if function
				if (Function.class.isInstance(instance)) {
			        Context cx = Context.enter();
			        try {
			        	Scriptable scope = cx.initStandardObjects();		
			        	//Function f = (Function) instance;
			        	//RhinoScriptFactory.log.debug("Function name: "  + f.getClass().getName());
			        	//RhinoScriptFactory.log.debug("Function classname: "  + f.getClassName());
			        	//o = f.call(cx, scope, scope, args);
			        	o = ScriptableObject.callMethod(scope, name, args);
			        } finally {
			        	Context.exit();
			        }
		        } else {
		        	Class clazz = proxy.getClass();
		        	//Class[] paramTypes = new Class[args.length];
		        	//for (int i=0; i<args.length; i++) {
		        	//	paramTypes[i] = args[i].getClass();
		        	//}
		            try {
		                Method scriptMethod = null;
	                	//clazz.getDeclaredMethod(method.getName(), paramTypes);
		            	//Method scriptMethod = clazz.getMethod(method.getName(), paramTypes);
		            	Method[] methods = clazz.getMethods();
		                for (Method element : methods) {
		                	if (element.getName().equals(name)) {
		                		scriptMethod = element;
		                		break;
		                	}
		                }		            	
		                //String nmmm = clazz.getName() + '.' + method.getName();
						if (NativeObject.class.isInstance(instance)) {
					        Context cx = Context.enter();
					        try {
					        	Scriptable scope = cx.initStandardObjects();	
					        	scope.put("", ((NativeObject) instance), this);
					        	o = ScriptableObject.callMethod(cx, scope, name, args); 
					        	//o = ScriptableObject.callMethod(cx, ((NativeObject) instance), name, args); 
					        } finally {
					        	Context.exit();
					        }
						} else {
							o = scriptMethod.invoke(instance, args);							
						}
		            //} catch (NoSuchMethodException e) {
		            //	RhinoScriptFactory.log.error(e);
		            //} catch (IllegalAccessException e) {
		            //	RhinoScriptFactory.log.error(e);
		            //} catch (InvocationTargetException e) {
		            //	RhinoScriptFactory.log.error(e);
		            } catch (Exception e) {
		            	RhinoScriptFactory.log.error(e);
		            }		        	
		        	//o = ((Invocable) instance).call(method.getName(), args);
		        	RhinoScriptFactory.log.debug("Result classname: "  + o.getClass().getName());
		        }

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
        //String name = null;
        for (Method element : methods) {
            m = element;
            //name = m.getName();			
            RhinoScriptFactory.log.debug("Method: " + m.toGenericString()); 
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
