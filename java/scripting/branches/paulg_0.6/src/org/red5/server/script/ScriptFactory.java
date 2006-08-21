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

package org.red5.server.script;

import java.io.IOException;

/**
 * Script definition interface, encapsulating the configuration
 * of a specific script as well as a factory method for
 * creating the actual scripted Java <code>Object</code>.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 */
public interface ScriptFactory {

	/**
	 * Return a locator that points to the source of the script.
	 * Interpreted by the post-processor that actually creates
	 * the script.
	 * <p>Typical supported locators are Spring resource locations
	 * (such as "file:C:/myScript.bsh" or "classpath:myPackage/myScript.bsh")
	 * and inline scripts ("inline:myScriptText...").
	 * @see org.springframework.scripting.support.ScriptFactoryPostProcessor#convertToScriptSource
	 * @see org.springframework.core.io.ResourceLoader
	 */
	String getScriptSourceLocator();

	/**
	 * Return the business interfaces that the script is supposed
	 * to implement.
	 * <p>Can return <code>null</code> if the script itself determines
	 * its Java interfaces (such as in the case of Groovy).
	 */
	Class[] getScriptInterfaces();

	/**
	 * Return whether the script requires a config interface to be
	 * generated for it. This is typically the case for scripts that
	 * do not determine Java signatures themselves, with no appropriate
	 * config interface specified in <code>getScriptInterfaces()</code>.
	 * @see #getScriptInterfaces()
	 */
	boolean requiresConfigInterface();

	/**
	 * Factory method for creating a scripted Java object.
	 * @param actualScriptSource the actual ScriptSource to retrieve
	 * the script source text from (never <code>null</code>)
	 * @param actualInterfaces the actual interfaces to expose,
	 * including script interfaces as well as a generated config interface
	 * (if applicable, can be <code>null</code>)
	 * @return the scripted Java object
	 * @throws IOException if script retrieval failed
	 * @throws ScriptCompilationException if script compilation failed
	 */
	Object getScriptedObject(ScriptSource actualScriptSource, Class[] actualInterfaces)
			throws IOException, ScriptCompilationException;

}
