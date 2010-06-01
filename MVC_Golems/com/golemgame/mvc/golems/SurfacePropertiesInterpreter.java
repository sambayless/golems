package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;

public class SurfacePropertiesInterpreter extends StoreInterpreter{
	public static final String SURFACE = "surface";
	public static final String ENABLED = "surface.enabled";
	
	public static enum SurfaceType
	{
		GLASS("Glass"),TIN("Tin"),WOOD("Wood"),FELT("Felt"),CHORD("Chords"),NONE("(No sound)"), INFER(""),
		BRICK("Brick"),CARDBOARD("Cardboard"),CUSHION("Cushion"),PIPE("Pipe"),PLASTIC("Plastic"),
		PLASTIC2("Plastic 2"),PLASTIC3("Plastic 3"),RING("Ring"),STYROFOAM("Styrofoam"),VENT("Vent"),WOOD2("Wood2"), CARPET("Carpet"), BONGO("Bongo Drum");
		//,WOOD3("Wood3") BOOK("Book")
		
		
		
		private final String description;

		@Override
		public String toString() {
			return description;
		}

		private SurfaceType(String description) {
			this.description = description;
		}
		
		
	}
	

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
	
		keys.add(SURFACE);
		keys.add(ENABLED);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(SURFACE))
			return defaultEnum;
		if(key.equals(ENABLED))
			return defaultBool;

		return super.getDefaultValue(key);
	}
	
	public SurfacePropertiesInterpreter() {
		this(new PropertyStore());
	}
	public SurfacePropertiesInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.SURFACE_PROPERTIES_CLASS);
	}
	
	public SurfaceType getSoundSurface()
	{
		return getStore().getEnum(SURFACE, SurfaceType.INFER);
	}
	
	public void setMaterial(SurfaceType material)
	{
		getStore().setProperty(SURFACE, material);
	}
	
	public boolean isEnabled()
	{
		return getStore().getBoolean(ENABLED, true);
	}
	
	public void setEnabled(boolean enabled)
	{
		getStore().setProperty(SURFACE, enabled);
	}
	
	public static SurfaceType getDefaultSurface(MaterialClass material)
	{
	
		switch(material)
		{
			case GLASS:
				return SurfaceType.GLASS;
			case CONCRETE:
				return SurfaceType.TIN;
			case ICE:
				return SurfaceType.GLASS;
			case GRANITE:
				return SurfaceType.GLASS;
			case HELIUM_BALLOON:
				return SurfaceType.FELT;
			case PLASTIC:
				return SurfaceType.WOOD;
			case IRON:
				return SurfaceType.TIN;
			case RUBBER:
				return SurfaceType.FELT;
			case SPONGE:
				return SurfaceType.FELT;
			case WOOD:
				return SurfaceType.WOOD;
			case DEFAULT:
				return SurfaceType.GLASS;
			default:
				return SurfaceType.NONE;
		}
	}
	
}
