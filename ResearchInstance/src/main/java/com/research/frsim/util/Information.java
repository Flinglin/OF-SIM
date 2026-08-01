
package com.research.frsim.util;

import java.io.Serializable;

public class Information implements Serializable{

	private static final long serialVersionUID = 7482539776116569949L;

	private String message;

	private boolean successful = false;

	private Object result;

	private Object[] results;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Object[] getResults() {
		return results;
	}
	public void setResults(Object[] results) {
		this.results = results;
	}
}
