package com.app.demos.base;

import android.app.Application;

public class BaseApp extends Application {
	
	private String s;
	private long l;
	private int i;
	
	public static boolean debug=false;
	
	public static void setDebug(boolean debug){
		BaseApp.debug=debug;
	}

	public static boolean getDebug(){
		return BaseApp.debug;
	}
	
	public int getInt () {
		return i;
	}
	
	public void setInt (int i) {
		this.i = i;
	}
	
	public long getLong () {
		return l;
	}
	
	public void setLong (long l) {
		this.l = l;
	}
	
	public String getString () {
		return s;
	}
	
	public void setString (String s) {
		this.s = s;
	}
}