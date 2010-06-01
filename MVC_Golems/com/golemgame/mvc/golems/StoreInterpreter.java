package com.golemgame.mvc.golems;

import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mvc.BoolType;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.EnumType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.IntType;
import com.golemgame.mvc.NullType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.QuaternionType;
import com.golemgame.mvc.ReferenceType;
import com.golemgame.mvc.StringType;
import com.golemgame.mvc.Vector2Type;
import com.golemgame.mvc.Vector3Type;

/**
 * A higher level abstraction for a property store; subclasses provide easier to use access methods for the properties.
 * @author Sam
 *
 */
public abstract class StoreInterpreter {
	
	private final PropertyStore store;
	
	public static final PropertyStore defaultStore = new PropertyStore();
	public static final CollectionType defaultCollection = new CollectionType();
	public static final ReferenceType defaultReference = new ReferenceType();
	public static final Vector3Type defaultVector3 = new Vector3Type();
	public static final Vector2Type defaultVector2 = new Vector2Type();
	public static final ColorType defaultColor = new ColorType();
	public static final QuaternionType defaultQuat = new QuaternionType();
	public static final BoolType defaultBool = new BoolType();
	public static final FloatType defaultFloat = new FloatType();
	public static final StringType defaultString = new StringType();
	public static final DoubleType defaultDouble = new DoubleType();
	public static final IntType defaultInt = new IntType();
	public static final EnumType defaultEnum = new EnumType();
	
	public StoreInterpreter(PropertyStore store) {
		super();
		this.store = store;
//		loadDefaults();
	}
	
	public StoreInterpreter()
	{
		this( new PropertyStore());
	}

	public  PropertyStore getStore(){
		return store;
	}
	
	public void refresh()
	{
		store.refresh();
	}
	
	/**
	 * Enumerate the keys that this interpreter controls
	 * @param store
	 * @return
	 */
	public Collection<String> enumerateKeys(Collection<String> keys)
	{
		return keys;
	}
	
	/**
	 * Get the default value for a given key.
	 * @param key
	 * @return DataType.Type.NULL if there is no such key, otherwise the default datatype for this key. (Note: this default should be cloned/copied before being added to a multiple, as it may be a static instance.)
	 * 
	 */
	public DataType getDefaultValue(String key) 
	{
		return NullType.get();
	}
	
	/**
	 * Non-destructively load default values into fields that have not been initialized
	 */
	public void loadDefaults()
	{
		Collection<String> keys = enumerateKeys(new ArrayList<String>());
		for (String key:keys)
		{
			if (store.hasProperty(key))
				continue;
			DataType defVal = getDefaultValue(key);
			if (defVal != null && defVal != NullType.get())				
			{
				store.setProperty(key, defVal.deepCopy());
			}
		}
	}
}
