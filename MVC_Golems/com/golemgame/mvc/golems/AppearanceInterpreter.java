package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.renderer.ColorRGBA;

/**
 * This holds the standard appearance settings for a structure.
 * A later version may be more adaptable, if that becomes necessary.
 * @author Sam
 *
 */
public class AppearanceInterpreter extends StoreInterpreter {
	public static final String BASE_COLOR = "base.color";
	public static final String HIGHLIGH_COLOR = "highlight.color";
	public static final String TEXTURE_IMAGE = "texture.image";
	public static final String TEXT = "text";
	public static final String TEXT_COLOR = "text.color";
	
	public static enum ImageType
	{
			VOID, 
		 DEFAULT,
		 SPOTS,
		 STRIPES,
			CAMO,
			CAMO3,
			CAMO4,
			CHECKER1,
			CHECKER2,
			CHECKER3,
			STAR1,
			STAR2,
			STAR3,
			STAR4,
			RIVET1,
			RIVET2,
			TREADPLATE,
			STEELBOX1,
			STEELBOX2, 
			WOOD, 
			WOOD2,
			CONCRETE,
			GALVANIZED, 
			TEXT, HAMMER, GRADIENT, HEART, FACE, TRIANGLE, ARROW, TRIANGLE3,
			;
		
	}
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(BASE_COLOR);	
		keys.add(HIGHLIGH_COLOR);	
		keys.add(TEXTURE_IMAGE);	
		keys.add(TEXT);
		keys.add(TEXT_COLOR);
		return super.enumerateKeys(keys);
	}
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(BASE_COLOR))
			return defaultColor;	
		if(key.equals(HIGHLIGH_COLOR))
			return defaultColor;	
		if(key.equals(TEXTURE_IMAGE))
			return defaultEnum;	
		if(key.equals(TEXT))
			return defaultString;	
		if(key.equals(TEXT_COLOR))
			return defaultColor;	
		return super.getDefaultValue(key);
	}
	public AppearanceInterpreter() {
		this(new PropertyStore());
	}

	
	
	public AppearanceInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.APPEARANCE_CLASS);
	}

	public ColorRGBA getBaseColor()
	{
		return getStore().getColor(BASE_COLOR, new ColorRGBA(ColorRGBA.gray));
	}
	
	public ColorRGBA getHighlightColor()
	{
		return getStore().getColor(HIGHLIGH_COLOR,new ColorRGBA(ColorRGBA.white));
	}
	
	public ImageType getImage()
	{
		return getStore().getEnum(TEXTURE_IMAGE, ImageType.VOID);
	}
	
	public void setBaseColor(ColorRGBA color)
	{
		getStore().setProperty(BASE_COLOR, color);
	}
	public void setHighlightColor(ColorRGBA color)
	{
		getStore().setProperty(HIGHLIGH_COLOR, color);
	}
	
	public void setTextureImage(ImageType image)
	{
		getStore().setProperty(TEXTURE_IMAGE, image);
	}
	
	public void setText(String text)
	{
		getStore().setProperty(TEXT, text);
	}
	
	public void setTextColor(ColorRGBA color)
	{
		getStore().setProperty(TEXT_COLOR, color);
	}
	public ColorRGBA getTextColor()
	{
		return getStore().getColor(TEXT_COLOR,new ColorRGBA(ColorRGBA.white));
	}
	public String getText()
	{
		return getStore().getString(TEXT,"");
	}
	
}
