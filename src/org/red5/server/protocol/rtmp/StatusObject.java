package org.red5.server.protocol.rtmp;

public class StatusObject {

	public static final String ERROR = "error";
	public static final String STATUS = "status";
	public static final String WARNING = "warning";
	
	protected String code;
	protected String level;
	protected String description;
	
	public StatusObject(String code, String level, String description){
		
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLevel(){
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
}
