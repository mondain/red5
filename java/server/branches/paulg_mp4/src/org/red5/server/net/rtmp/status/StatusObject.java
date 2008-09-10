package org.red5.server.net.rtmp.status;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 *
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.red5.annotations.Anonymous;
import org.red5.io.object.ICustomSerializable;
import org.red5.io.object.Output;
import org.red5.io.object.Serializer;

/**
 * Status object that is sent to client with every status event
 * @author The Red5 Project (red5@osflash.org)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */
@Anonymous
public class StatusObject
implements Serializable, ICustomSerializable, Externalizable {

	private static final long serialVersionUID = 8817297676191096283L;

	public static final String ERROR = "error";

	public static final String STATUS = "status";

	public static final String WARNING = "warning";

	protected String code;

	protected String level;

	protected String description = "";

	protected Object application;

	protected Map<String, Object> additional;
	
	/** Constructs a new StatusObject. */
    public StatusObject() {

	}

	public StatusObject(String code, String level, String description) {
		this.code = code;
		this.level = level;
		this.description = description;
	}

	/**
     * Getter for property 'code'.
     *
     * @return Value for property 'code'.
     */
    public String getCode() {
		return code;
	}

	/**
     * Setter for property 'code'.
     *
     * @param code Value to set for property 'code'.
     */
    public void setCode(String code) {
		this.code = code;
	}

	/**
     * Getter for property 'description'.
     *
     * @return Value for property 'description'.
     */
    public String getDescription() {
		return description;
	}

	/**
     * Setter for property 'description'.
     *
     * @param description Value to set for property 'description'.
     */
    public void setDescription(String description) {
		this.description = description;
	}

	/**
     * Getter for property 'level'.
     *
     * @return Value for property 'level'.
     */
    public String getLevel() {
		return level;
	}

	/**
     * Setter for property 'level'.
     *
     * @param level Value to set for property 'level'.
     */
    public void setLevel(String level) {
		this.level = level;
	}

	/**
     * Setter for property 'application'.
     *
     * @param application Value to set for property 'application'.
     */
    public void setApplication(Object application) {
		this.application = application;
	}

	/**
     * Getter for property 'application'.
     *
     * @return Value for property 'application'.
     */
    public Object getApplication() {
		return application;
	}

	/** {@inheritDoc} */
    @Override
	public String toString() {
		return "Status code: " + getCode() + " desc: " + getDescription()
				+ " level: " + getLevel();
	}

    /**
     * Generate Status object that can be returned through a RTMP channel.
     * 
     * @return
     */ 
    public Status asStatus() {
    	return new Status(getCode(), getLevel(), getDescription());
    }

    public void setAdditional(String name, Object value) {
    	if ("code".equals(name) || "level".equals(name) ||
    			"description".equals(name) || "application".equals(name)) {
    		throw new RuntimeException("the name \"" + name + "\" is reserved");
    	}
    	if (additional == null) {
    		additional = new HashMap<String, Object>();
    	}
    	additional.put(name, value);
    }
    
    public void serialize(Output output, Serializer serializer) {
    	output.putString("level");
    	output.writeString(getLevel());
    	output.putString("code");
    	output.writeString(getCode());
    	output.putString("description");
    	output.writeString(getDescription());
    	if (getApplication() != null) {
    		output.putString("application");
    		serializer.serialize(output, getApplication());
    	}
    	if (additional != null) {
    		// Add additional parameters
    		for (Map.Entry<String, Object> entry: additional.entrySet()) {
    	    	output.putString(entry.getKey());
    	    	serializer.serialize(output, entry.getValue());
    		}
    	}
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		code = (String) in.readObject();
		description = (String) in.readObject();
		level = (String) in.readObject();
		application = in.readObject();
		additional = (Map<String, Object>) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(code);
		out.writeObject(description);
		out.writeObject(level);
		out.writeObject(application);
		out.writeObject(additional);
	}
}
