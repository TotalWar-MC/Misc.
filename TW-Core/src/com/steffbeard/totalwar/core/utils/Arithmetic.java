package com.steffbeard.totalwar.core.utils;

import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Arithmetic {
	ScriptEngineManager mgr = new ScriptEngineManager();
	ScriptEngine engine = mgr.getEngineByName("JavaScript"); //script to solve arithmetic
	
	public static String convertTicksToTimeUnits(long d) {
		String time = "";
		// converts to time units
		int day = (int) TimeUnit.SECONDS.toDays(d);
		int hours = (int) (TimeUnit.SECONDS.toHours(d) - (day * 24));
		int minute = (int) (TimeUnit.SECONDS.toMinutes(d) - (TimeUnit.SECONDS.toHours(d) * 60));
		int second = (int) (TimeUnit.SECONDS.toSeconds(d) - (TimeUnit.SECONDS.toMinutes(d) * 60));
		// checks times
		if(day > 0)	{
			time = time + day + " Days " + hours + " Hours " + minute + " Minutes " + second + " Seconds ";
		}
		else if(hours > 0) {
			time = time + hours + " Hours " + minute + " Minutes " + second + " Seconds ";
		}
		else if(minute > 0) {
			time = time + minute + " Minutes " + second + " Seconds ";
		}
		else if(second > 0) {
			time = time + second + " Seconds ";
		}
		return time;
	}
}