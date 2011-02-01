/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.toolbar.tooltip;

import com.golemgame.local.StringConstants;
import com.jme.image.Texture;

public class ToolTipData {
	
	private Texture icon;
	private String message = " ";
	private Texture background;
	private String title = new String();

//	private Description description = null;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean hasTitle()
	{
		return title != null && title.length()>0;
	}

	public boolean hasIcon()
	{
		return icon != null;
	}
	
	public boolean hasBackground()
	{
		return background != null;
	}
	
	public boolean hasMessage()
	{
		return (message != null && message.length()>0);// ||  (description != null) && ToolbarDescriptions.getInstance().getDescription(description)!= null && ToolbarDescriptions.getInstance().getDescription(description).length()>0 ;
	}
	
/*	public String getMessage()
	{
		if (description == null || ToolbarDescriptions.getInstance().getDescription(description) == null || ToolbarDescriptions.getInstance().getDescription(description).length() == 0)
			return message;
		else
			return  ToolbarDescriptions.getInstance().getDescription(description);
	}
	*/
	public Texture getIcon()
	{
		return icon;
	}
	
	public Texture getBackground()
	{
		return background;
	}

	public ToolTipData(Texture icon, String title) {
		super();
		this.icon = icon;
		this.title = title;
	}

	public ToolTipData(String title) {
		super();
		this.title = title;
	}

	public ToolTipData(Texture icon, Texture background, String title) {
		super();
		this.icon = icon;
		this.background = background;
		this.title = title;
	}
	
	public ToolTipData(Texture icon, Texture background, String title,String description) {
		super();
		this.icon = icon;
		this.background = background;
		this.title = title;
		this.message = description;
	}
	public String getMessage() {
		return StringConstants.wordwrap( StringConstants.replace(message),IconToolTip.WORD_WRAP);
	}
	public void setMessage(String message) {
		this.message = message;
	}
/*
	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}
	*/
	
}
