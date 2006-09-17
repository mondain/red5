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
 * Interface that defines the source of a script.
 * Tracks whether the underlying script has been modified.
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public interface ScriptSource {

	/**
	 * Retrieve the current script source text as String.
	 * @throws IOException if script retrieval failed
	 */
	String getScriptAsString() throws IOException;

	/**
	 * Indicate whether the underlying script data was modified since the last time
	 * <code>getScriptAsString()</code> was called. Returns <code>true</code> if
	 * the script has not been read yet.
	 * @see #getScriptAsString()
	 */
	boolean isModified();

}
