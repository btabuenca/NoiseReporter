/*******************************************************************************
 * Copyright (C) 2014 Open University of The Netherlands
 * Author: Bernardo Tabuenca Archilla
 * Lifelong Learning Hub project 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.ounl.noisereporter.feeback.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.ounl.noisereporter.feeback.config.ConfigManager;

public class ColorCommand implements ICommands {
	/**
	 * * > PUT /ring/color/ HTTP/1.1           : Changes the color of the LED strip
	 */
	private static final String WS_PATH = "/ring/color/";
	private String ipAdress = "";
	private String sColorRed_DECIMAL = "0";
	private String sColorGreen_DECIMAL = "0";
	private String sColorBlue_DECIMAL = "0";
	
	
	public ColorCommand(String sIp, String sRedDecimal, String sGreenDecimal, String sBlueDecimal){
		ipAdress = sIp;
		
		sColorRed_DECIMAL = sRedDecimal;
		sColorGreen_DECIMAL = sGreenDecimal;
		sColorBlue_DECIMAL = sBlueDecimal;
	}
	
	private String getCommand(){		
		
		return ConfigManager.URL_PREFIX + ipAdress + WS_PATH;
	}
	
	
	public URL getUrlCommand(){
		try {
			return new URL(getCommand());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Override
	public String toString(){
		return "COMMAND ON: URL["+getUrlCommand().toString()+"] COMMAND["+getCommand()+"] HAS PARAMS:["+hasParams()+"] PARAMS:["+getParams()+"] METHOD:["+getHttpMethod()+"]";
	}
	

	@Override
	public boolean hasParams() {
		return true;
	}

	
	@Override
	public String getParams() {
		
		String sJson = "{\"r\":" + sColorRed_DECIMAL +
				",\"g\":" + sColorGreen_DECIMAL + 
				",\"b\":" + sColorBlue_DECIMAL + 
				"}";
				
		return sJson;
	}

	@Override
	public String getHttpMethod() {		
		return ICommands.HTTP_METHOD_PUT;
	}
	
	@Override
	public String getAction() {
		return ACTION_COLOR;
	}	
	
	@Override
	public String getWSPath() {
		return WS_PATH;
	}	

}
