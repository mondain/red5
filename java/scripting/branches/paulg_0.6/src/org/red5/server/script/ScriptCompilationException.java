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

import org.springframework.core.NestedRuntimeException;

/**
 * Exception to be thrown on script compilation failure.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ScriptCompilationException extends NestedRuntimeException {

	/**
	 * Constructor for ScriptCompilationException.
	 * @param msg the detail message
	 */
	public ScriptCompilationException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for ScriptCompilationException.
	 * @param msg the detail message
	 * @param ex root cause (usually from using an underlying
	 * script compiler API)
	 */
	public ScriptCompilationException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
