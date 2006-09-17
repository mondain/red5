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

import org.springframework.util.Assert;

/**
 * Static implementation of the
 * {@link org.springframework.scripting.ScriptSource} interface,
 * encapsulating a given String that contains the script
 * source text.
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class StaticScriptSource implements ScriptSource {

	private String script;

	private boolean modified;

	/**
	 * Create a new StaticScriptSource for the given script.
	 * @param script the script String
	 * @throws IllegalArgumentException if the supplied <code>script</code> is <code>null</code>
	 */
	public StaticScriptSource(String script) {
		setScript(script);
	}

	/**
	 * Set a fresh script String, overriding the previous script.
	 * @param script the script String
	 * @throws IllegalArgumentException if the supplied <code>script</code> is <code>null</code>
	 */
	public void setScript(String script) {
		Assert.hasText(script, "Script must not be null");
		this.modified = !script.equals(this.script);
		this.script = script;
	}

	public String getScriptAsString() {
		this.modified = false;
		return this.script;
	}

	public boolean isModified() {
		return this.modified;
	}

	public String toString() {
		return this.script;
	}

}
