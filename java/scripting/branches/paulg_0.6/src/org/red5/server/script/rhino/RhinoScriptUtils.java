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

import javax.script.Namespace;
import javax.script.ScriptContext;
import javax.script.SimpleNamespace;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

import org.jruby.exceptions.JumpException;
import org.red5.server.script.ScriptCompilationException;

import com.sun.script.javascript.RhinoScriptEngine;

/**
 * Utility methods for handling Rhino / Javascript objects.
 *
 * @author Paul Gregoire
 * @since 0.6
 */
public abstract class RhinoScriptUtils {

	protected Class extendsClass = Object.class;

	protected String id;

	/**
	 * Create a new Rhino-scripted object from the given script source.
	 * @param scriptSource the script source text
	 * @param interfaces the interfaces that the scripted Java object
	 * is supposed to implement
	 * @return the scripted Java object
	 * @throws JumpException in case of Rhino parsing failure
	 */
	public static Object createRhinoObject(String scriptSource,
			Class[] interfaces) throws ScriptCompilationException {
		RhinoScriptEngine engine = new RhinoScriptEngine();
		//String className = findClassName(scriptSource);
		Class clazz = null;
		//Script rhinoObject = (Script) rhino.eval("\n" + className + ".new");
		try {
			//ScriptEngine engine = scriptContext.getScriptManager().getEngineByExtension(".js");
			//set engine scope namespace
			Namespace n = new SimpleNamespace();
			engine.setNamespace(n, ScriptContext.ENGINE_SCOPE);
			//add the logger to the script
			n.put("log", RhinoScriptFactory.log);
			clazz = (Class) engine.eval(scriptSource);
			//rhinoObject = clazz.newInstance();
			//System.out.println("Result of compiled script: " + compiled.eval());
		} catch (Exception ex) {
			throw new ScriptCompilationException(
					"Could not compile Rhino script: " + scriptSource, ex);
		}
		if (null == clazz) {
			throw new ScriptCompilationException(
					"Compilation of Rhino script returned " + clazz.getName());
		}
		return engine.getInterface(clazz);
	}

	/*
	 private static String findClassName(String source) {
	 String result = null;
	 try {
	 Pattern regex = Pattern.compile("function ([\\w]+)[.\\(\\) ]+[\\s|{]\\s",
	 Pattern.CANON_EQ);
	 Matcher matcher = regex.matcher(source);
	 if (matcher.find()) {
	 result = matcher.group();
	 } 
	 } catch (PatternSyntaxException ex) {
	 // Syntax error in the regular expression
	 }
	 return result;
	 }
	 */

	/*	
	 protected Object createObject(InputStream is) throws IOException, BeansException {
	 // clearInterfaces();
	 Class clazz = null;
	 try {
	 // Get the JavaScript into a String
	 String js = "";
	 
	 if(isInline()) js = inlineScriptBody();
	 else js = getAsString(is);

	 // Setup Contect and ClassLoader
	 Context ctx = Context.enter();
	 
	 //ctx.initStandardObjects();
	 
	 ClassLoader cl = Thread.currentThread().getContextClassLoader();
	 
	 GeneratedClassLoader gcl = ctx.createClassLoader(cl);
	 
	 CompilerEnvirons ce = new CompilerEnvirons();
	 
	 //ce.setAllowMemberExprAsFunctionName(false)
	 //ctx.hasFeature(Context.FEATURE_DYNAMIC_SCOPE);
	 ce.initFromContext(ctx);
	 ce.setXmlAvailable(true);
	 ce.setOptimizationLevel(9);
	 
	 ClassCompiler cc = new ClassCompiler(ce);
	 cc.setTargetExtends(getExtends());
	 cc.setTargetImplements(getInterfaces());

	 Object[] generated = cc.compileToClassFiles(js, this.getLocation(),	0, getTempClassName());
	 addGeneratedToClassLoader(gcl, generated);
	 
	 // get the scope;
	 ScriptableObject scope = JavaScriptScopeThreadLocal.getScope();
	 if(scope==null) scope = ScriptRuntime.getGlobal(ctx);
	 
	 // add a log object to the scope
	 ScriptableObject.putProperty(scope, "log", Context.javaToJS(LogFactory.getLog(getClassName()), scope));
	 
	 // load the script class 
	 clazz = ((ClassLoader) gcl).loadClass((String)generated[2]);
	 Script script = (Script) clazz.newInstance();

	 // execute the script saving the resulting scope
	 // this is a bit like calling the constuctor on an object
	 // the scope contains the resulting object
	 Scriptable result = (Scriptable) script.exec(ctx, scope);
	 
	 cc.setTargetExtends(getExtends());
	 
	 if(log.isDebugEnabled())
	 log.debug("Target extends: "+cc.getTargetExtends());
	 
	 if(meta.getImplements().length>0){
	 String[] interfaces = meta.getImplements();
	 for(int i=0; i<interfaces.length; i++){
	 addInterface(cl.loadClass(interfaces[i]));
	 }
	 }
	 
	 if(meta.getMethodNames().length>0){
	 
	 Class publicInterface;
	 
	 try{
	 publicInterface = cl.loadClass(getPublicInterfaceName());
	 }
	 catch(ClassNotFoundException ex){
	 InterfaceMaker interfaceMaker = new InterfaceMaker();
	 interfaceMaker.setClassLoader(cl);
	 NamingPolicy namingPolicy = new InterfaceNamingPolicy(getPublicInterfaceName());
	 interfaceMaker.setNamingPolicy(namingPolicy);
	 String[] methodNames = meta.getMethodNames();
	 Type[] noEx = new Type[0];
	 for(int i=0; i<methodNames.length; i++){
	 String descriptor = meta.getMethodDescriptor(methodNames[i]);
	 if(log.isDebugEnabled())
	 log.debug("Method descriptor: "+descriptor);
	 interfaceMaker.add(TypeUtils.parseSignature(descriptor),noEx);
	 }
	 publicInterface = interfaceMaker.create();
	 }				
	 
	 if(log.isDebugEnabled())
	 log.debug("Generated public interface " + publicInterface.getName());
	 
	 addInterface(publicInterface);
	 
	 }
	 
	 cc.setTargetImplements(getInterfaces());
	 
	 try {
	 
	 generated = cc.compileToClassFiles(js, this.getLocation(),
	 0, getClassName());
	 
	 addGeneratedToClassLoader(gcl, generated);
	 
	 clazz = ((ClassLoader) gcl).loadClass(getClassName());
	 if(log.isDebugEnabled())
	 log.debug("Loaded javascript class " + clazz);
	 }
	 catch(NoClassDefFoundError ncex){
	 throw new BeanCreationException("Class not found", ncex);
	 }
	 
	 // Call the constructor passing in the scope object
	 Constructor cstr = clazz.getConstructor(new Class[]{Scriptable.class});
	 Object instance = cstr.newInstance(new Object[]{scope});

	 JavaScriptScopeThreadLocal.setScope(scope);
	 
	 Context.exit();
	 return instance;

	 } catch (RuntimeException rex){ 
	 throw new BeanCreationException("Runtime exception", rex);
	 } catch (Exception ex) {
	 throw new BeanCreationException("Error instantiating" + clazz, ex);
	 }
	 }
	 */

	public void setExtends(Class extendsClass) {
		this.extendsClass = extendsClass;
	}

	public Class getExtends() {
		return this.extendsClass;
	}

	/**
	 * @param gcl
	 * @param generated
	 * /
	 private void addGeneratedToClassLoader(GeneratedClassLoader gcl, Object[] generated) {
	 for (int i = 0; i < generated.length; i += 2) {
	 String name = (String) generated[i];
	 byte[] code = (byte[]) generated[i + 1];
	 gcl.defineClass(name, code);
	 }
	 }

	 private void setClassName(String className){
	 this.className = className + '_' + id;
	 }
	 
	 private String getClassName(){
	 if(className!=null) return className;
	 else if(isInline()) return getInlineClassName();
	 else return getSafeClassName(getLocation());
	 }
	 
	 private String getInlineClassName(){
	 return "InlineJS__"+id;
	 }
	 
	 private String getTempClassName(){
	 return "TempJS__"+id;
	 }

	 private String getPublicInterfaceName(){
	 return getClassName()+"_Pub";
	 }

	 public String getSafeClassName(String unsafe){
	 if(unsafe.toLowerCase().endsWith(".js")) 
	 unsafe = unsafe.substring(0, unsafe.length()-3);
	 unsafe = unsafe.replace('/', '.');
	 unsafe = unsafe.replace('-', '_');
	 unsafe = unsafe.replace(' ', '_');
	 if(unsafe.startsWith(".")) 
	 unsafe = unsafe.substring(1);
	 return unsafe + "_" + id;
	 }
	 */

	class InterfaceNamingPolicy implements NamingPolicy {

		private String interfaceName;

		public InterfaceNamingPolicy(String interfaceName) {
			this.interfaceName = interfaceName;
		}

		/* (non-Javadoc)
		 * @see net.sf.cglib.core.NamingPolicy#getClassName(java.lang.String, java.lang.String, java.lang.Object, net.sf.cglib.core.Predicate)
		 */
		public String getClassName(String arg0, String arg1, Object arg2,
				Predicate arg3) {
			return interfaceName;
		}

	}

}
