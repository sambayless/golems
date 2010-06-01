package com.golemgame.properties.fengGUI;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.ListItem;
import org.fenggui.ObservableWidget;
import org.fenggui.StatefullWidget;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.util.Color;

import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.WidgetAlteredListener;
import com.golemgame.mvc.BoolType;
import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.QuaternionType;
import com.golemgame.mvc.Vector2Type;
import com.golemgame.mvc.Vector3Type;
import com.golemgame.mvc.DataType.Type;
import com.golemgame.properties.NoValueException;
import com.golemgame.properties.PropertyStoreAdjuster;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.jme.math.FastMath;
import com.jme.system.DisplaySystem;

public class PropertyTabAdapter extends TabAdapter {
	
	protected enum Format
	{
		Float,Double,Angle,String,Bool, Default, Int, 
		/**
		 * Float 2X is a float multiplied by two
		 */
		Float2x, 
		/**
		 * ProportionalX is a double multiplied by the current screen width
		 */
		ProportionalX, 
		/**
		 * ProportionalY is a double multiplied by the current screen height
		 */
		ProportionalY, 
		KeyCode;
	}
	
	private PropertyStoreAdjuster propertyStoreAdjuster;
	private Set<Object> alteredComponents = new HashSet<Object>();

	private Map<Object,String> keyMap = new HashMap<Object,String>();

//	private Map<Object, Boolean> multipleValueMap = new HashMap<Object, Boolean>();
	
	public PropertyTabAdapter(String title) {
		super(title);

	}
	
	public PropertyStore getPrototype()
	{
		return this.propertyStoreAdjuster.getPrototype();
	}

	public void setPropertyStores(PropertyStore store)
	{
		Collection<PropertyStore> stores = new ArrayList<PropertyStore>();
		stores.add(store);
		setPropertyStores(stores);
	}

	public void setPropertyStores(Collection<PropertyStore> stores)
	{
		this.propertyStoreAdjuster = new PropertyStoreAdjuster(stores, "Menu Action");
	
	}

	public PropertyStoreAdjuster getPropertyStoreAdjuster() {
		return propertyStoreAdjuster;
	}
	
	public void setValueAltered(String key, boolean altered)
	{
		propertyStoreAdjuster.setValueAltered(key,altered);
	}
	

	
	public void setIndexAltered(String key, int index,boolean altered)
	{
		propertyStoreAdjuster.setIndexAltered(key, index,altered);
	}
	
	protected void initializePrototype() {
	
		
		for(String key:getPrototype().getKeys().toArray(new String[0]))
		{
			DataType val;
			try {
				val = getPropertyStoreAdjuster().getCurrentValue(key);
		
				if (val != null && val.getType()!= DataType.Type.NULL)
				{
					getPrototype().setProperty(key,val);
				}
			} catch (NoValueException e) {
				if(getPropertyStoreAdjuster().hasMultipleValues(key))
					getPrototype().nullifyKey(key);
			}
		}
		//setComparePoint();
	}
	
/*	public boolean hasMultipleValues(Object object)
	{
		Boolean mult = this.multipleValueMap.get(object);
		if(mult == null)
			return false;
		return mult;
	}
	*/
	
	public boolean hasMultipleValues(String key)
	{
		return this.propertyStoreAdjuster.hasMultipleValues(key);
	}
	
	public boolean isAltered(StatefullWidget<?> widget)
	{
		return this.alteredComponents.contains(widget);
	}
	public static float toRadians(float valueOf) {
		return valueOf * FastMath.PI/180f;
	}

	public static float fromRadians(float angle) {
		return angle * 180f/FastMath.PI;
	}
	
	public static int fromProportionalX(double x)
	{
		return (int) (DisplaySystem.getDisplaySystem().getWidth()*x);
	}
	public static int fromProportionalY(double y)
	{
		return (int) (DisplaySystem.getDisplaySystem().getHeight()*y);
	}
	public static double toProportionalX(double x)
	{
		return  x/((double)DisplaySystem.getDisplaySystem().getWidth());
	}
	public static double toProportionalY(double y)
	{
		return  y/((double)DisplaySystem.getDisplaySystem().getHeight());
	}
	
	
	protected void associateWithKey(Widget widget, String key)
	{
		keyMap.put(widget, key);
		
		setMultipleValues(widget,hasMultipleValues(key),key);
		
	}
	protected void setWidgetAltered(Widget widget) {
		alteredComponents.add(widget);
	}
	
	public void setUnaltered(Widget widget)
	{
		if (widget instanceof TextEditor)
		{
			
			final TextEditor w = (TextEditor)widget;
		
			
	
				w.addTextChangedListener(new ITextChangedListener()
				{

					public void textChanged(TextChangedEvent textChangedEvent) {
						w.removeTextChangedListener(this);
						//w.setEnabled(true);
						setWidgetAltered(w);
					
					}

				
				});
		
		}else if (widget instanceof ColorPatch)
		{
			final ColorPatch w = (ColorPatch)widget;
			w.addAlteredListener(new WidgetAlteredListener()
			{

				public void widgetAltered(Widget source) {
					w.removeAlteredListener(this);
					//w.setEnabled(true);
					setWidgetAltered(w);
				}
				
			});
		}else if (widget instanceof CheckBox<?>)
		{
			final CheckBox<?> w = (CheckBox<?>) widget;
		
			
					
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							//w.setEnabled(true);
							w.removeSelectionChangedListener(this);
							setWidgetAltered(w);
									
						}						
					});
			
			
		}else if (widget instanceof ComboBox<?>)
		{
			final ComboBox<?> w = (ComboBox<?>) widget;
		
			
			
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							w.removeSelectionChangedListener(this);
							setWidgetAltered(w);
										
						}						
					});
				
			
		}else
		{
			final ObservableWidget w = (ObservableWidget)widget;
			
		
			w.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mousePressed(MousePressedEvent mousePressedEvent) {
					w.removeMouseListener(this);
					setWidgetAltered(w);
				}

				
					
			});
		}
		
		this.alteredComponents.remove(widget);
	
	}
	
	public void setMultipleValues(Widget widget,final boolean multipleValues, final String key)
	{
		if(!multipleValues){
			//alteredComponents.add(widget);
		}else
			setUnaltered(widget);
		//this.multipleValueMap.put(object, multipleValues);
		final PlainBackground grayedBackground = new PlainBackground(Color.GRAY);
		if (widget instanceof TextEditor)
		{
			
			final TextEditor w = (TextEditor)widget;
			if (multipleValues)
			{
				w.setText("(Multiple Values)");
				//w.setEnabled(false);
			
				w.getAppearance().add(grayedBackground);
				w.addTextChangedListener(new ITextChangedListener()
				{

					public void textChanged(TextChangedEvent textChangedEvent) {
						w.removeTextChangedListener(this);
						//w.setEnabled(true);
						setWidgetAltered(w);
					//	setValueAltered(key,true);
						w.getAppearance().remove(grayedBackground);
					}
					
				});
			}else
			{	
				w.setEnabled(true);
				w.addTextChangedListener(new ITextChangedListener()
				{

					public void textChanged(TextChangedEvent textChangedEvent) {
						w.removeTextChangedListener(this);
						setWidgetAltered(w);			
					}
					
				});
				
			}
			
		}else if (widget instanceof CheckBox<?>)
		{
			final CheckBox<?> w = (CheckBox<?>) widget;
			if(multipleValues)
			{
				if (multipleValues)
				{
				
				//	w.setEnabled(false);
					w.getAppearance().add(grayedBackground);
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							//w.setEnabled(true);
							w.removeSelectionChangedListener(this);
							setWidgetAltered(w);
							w.getAppearance().remove(grayedBackground);						
						}						
					});
				}else
				{		
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							//w.setEnabled(true);
							w.removeSelectionChangedListener(this);
							setWidgetAltered(w);
							
						}						
					});
					//w.getAppearance().add(new PlainBackground(Color.WHITE));
				}
			}
		}else if (widget instanceof ComboBox<?>)
		{
			final ComboBox<?> w = (ComboBox<?>) widget;
			if(multipleValues)
			{
				if (multipleValues)
				{
					//w.setEnabled(false);
					w.getLabel().setText("(Multiple Values)");
					
					w.getAppearance().add(grayedBackground);
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							w.removeSelectionChangedListener(this);
							//w.setEnabled(true);
							setWidgetAltered(w);
							w.getAppearance().remove(grayedBackground);						
						}						
					});
				}else
				{
					w.addSelectionChangedListener(new ISelectionChangedListener()
					{

						public void selectionChanged(
								SelectionChangedEvent selectionChangedEvent) {
							w.removeSelectionChangedListener(this);
						
							setWidgetAltered(w);
									
						}						
					});
					//w.setEnabled(true);
				//	w.getAppearance().add(new PlainBackground(Color.WHITE));
				}
			}
		}else if (widget instanceof ColorPatch)
		{
			
			final ColorPatch w = (ColorPatch) widget;
			if (multipleValues)
			{
				//w.setEnabled(false);
				//w.getLabel().setText("(Multiple Values)");
				
				w.getAppearance().add(grayedBackground);
				w.addAlteredListener(new WidgetAlteredListener()
				{


					public void widgetAltered(Widget source) {
						w.removeAlteredListener(this);
						//w.setEnabled(true);
						setWidgetAltered(w);
						w.getAppearance().remove(grayedBackground);	
					}						
				});
			}else
			{
				w.addAlteredListener(new WidgetAlteredListener()
				{

					public void widgetAltered(Widget source) {
							
						w.removeAlteredListener(this);
					
						setWidgetAltered(w);
								
					}						
				});
			}
		}
		
		this.alteredComponents.remove(widget);
		setValueAltered(key,false);
	}


	public void standardOpeningBehaviour(Widget widget,	String key) {
		standardOpeningBehaviour(widget,key,Format.Default);
	}
	public void standardOpeningBehaviour(Widget widget,	String key,Format format) {
	
		//setUnaltered(widget);
		DataType value;
		try {
			value = this.getPropertyStoreAdjuster().getCurrentValue(key);
		} catch (NoValueException e) {
			setMultipleValues(widget,true,key);
			return;
		}

		
			if (widget instanceof TextEditor)
			{
				TextEditor t = (TextEditor) widget;
				
				if(format == Format.Angle)
				{
					if (value.getType()==DataType.Type.FLOAT)
					{
						t.setText(String.valueOf(fromRadians(((FloatType)value).getValue())));
					}else
					{
						setMultipleValues(widget,true,key);
					}
					
				}else if (format == Format.Float2x)
				{
					if (value.getType()==DataType.Type.FLOAT)
					{
						t.setText(String.valueOf(2f*(((FloatType)value).getValue())));
					}else
					{
						setMultipleValues(widget,true,key);
					}
				}else if (format == Format.ProportionalX)
				{
					if (value.getType()==DataType.Type.FLOAT)
					{
						t.setText(String.valueOf(fromProportionalX(((FloatType)value).getValue())));
					}if (value.getType()==DataType.Type.DOUBLE)
					{
						t.setText(String.valueOf(fromProportionalX(((DoubleType)value).getValue())));
					}else
					{
						setMultipleValues(widget,true,key);
					}
				}else if (format == Format.ProportionalY)
				{
					if (value.getType()==DataType.Type.FLOAT)
					{
						t.setText(String.valueOf(fromProportionalY(((FloatType)value).getValue())));
					}if (value.getType()==DataType.Type.DOUBLE)
					{
						t.setText(String.valueOf(fromProportionalY(((DoubleType)value).getValue())));
					}else
					{
						setMultipleValues(widget,true,key);
					}
				}
				else			
					t.setText(value.toString());


			}else 	if (widget instanceof CheckBox)
			{
				CheckBox t = (CheckBox) widget;
				
				if(value != null && value.getType() == Type.BOOL)
					t.setSelected(((BoolType)value).getValue());
				else
					setMultipleValues(widget,true,key);

			}else 	if (widget instanceof ComboBox)
			{
				ComboBox t = (ComboBox) widget;
			
				String itemName = value.toString();
				if(itemName == null)
				{
					setMultipleValues(t,true,key);
					return;
				}
				ListItem<?> toSelect = null;
				for(Object item:t.getList().getItems())
				{
					if(item instanceof ListItem)
					{
						ListItem<?> i = (ListItem<?>)item;
						
						
						if (i.getValue() instanceof Enum<?>)
						{
							Enum<?> val = (Enum<?>) i.getValue();
							if (val.name().equalsIgnoreCase(itemName) || val.toString().equalsIgnoreCase(itemName))
							{
								toSelect = i;
								break;
							}
						}
						
						
						if (itemName.equalsIgnoreCase( i.getText()))
						{
							toSelect = i;
							break;
						}else if (itemName.equals(i.getValue()))
						{
							toSelect = i;
							break;
						}
					}
				}
				if(toSelect== null)
				{
					setMultipleValues(t,true,key);
				}else
				{
					t.setSelected(toSelect);
				}
			}else if  (widget instanceof ColorPatch)
			{
				ColorPatch t = (ColorPatch)widget;
				if(value.getType()==DataType.Type.COLOR)
				{
					ColorType color = (ColorType)value;
					t.setColor(color.getValue());
					
				}else
				{
					setMultipleValues(t,true,key);
				}
			}
		
		setUnaltered(widget);
	}
	
		public void standardOpeningBehaviour(Widget widget,	String key,Format format, int index, DataType.Type type) {
	
		
			DataType value;
			try {
				
				value = this.getPropertyStoreAdjuster().getCurrentValue(key);
			} catch (NoValueException e) {
				setMultipleValues(widget,true,key);
				return;
			}

		
			if (widget instanceof TextEditor)
			{
				TextEditor t = (TextEditor) widget;
				float val = 0;
				if(type == DataType.Type.VECTOR3 && value instanceof Vector3Type)
				{
					Vector3Type v = (Vector3Type)value;
			
						if (index == 0)
							val = v.getValue().x;
						else if (index == 1)
							val = v.getValue().y;
						else if (index == 2)
							val = v.getValue().z;
					
			
				}else if(type == DataType.Type.VECTOR2 && value instanceof Vector2Type)
				{
					Vector2Type v = (Vector2Type)value;
			
						if (index == 0)
							val = v.getValue().x;
						else if (index == 1)
							val = v.getValue().y;
					
				}else if(type == DataType.Type.COLOR && value instanceof ColorType)
				{
					ColorType v = (ColorType)value;
		
						if (index == 0)
							val = v.getValue().r;
						else if (index == 1)
							val = v.getValue().g;
						else if (index == 2)
							val = v.getValue().b;
						else if (index == 3)
							val = v.getValue().a;
					
				
				}else if(type == DataType.Type.QUATERNION && value instanceof QuaternionType)
				{
					QuaternionType v = (QuaternionType)value;
					float[] vals = v.getValue().toAngles(new float[3]);
					val = vals[index];
					
				}
				
				if(format == Format.Angle)
				{
					t.setText(String.valueOf(fromRadians(val)));		
				}else if (format == Format.Float2x)
				{
					t.setText(String.valueOf(val*2f));
				}
				else			
					t.setText(String.valueOf(val));
			}
		setUnaltered(widget);
	}
	
	public void standardClosingBehaviour(StatefullWidget widget,String key)
	{
		standardClosingBehaviour(widget,key,Format.Default);
	}
	
/*	public void standardClosingBehaviour(StatefullWidget widget,String key, Format format)
	{
		standardClosingBehaviour(widget,key,format,null);
	}
	
	public void standardClosingBehaviour(StatefullWidget widget,String key, Enum<?> defaultVal)
	{
		standardClosingBehaviour(widget,key,Format.Default,defaultVal);
	}*/
	
	@SuppressWarnings("unchecked")
	public void standardClosingBehaviourMultiplex(StatefullWidget widget,String key,int index, DataType.Type type, Format format) {
		if(!isAltered(widget))
			return;
	
		setIndexAltered(key, index,true);

		
		
			if (widget instanceof TextEditor)
			{
				TextEditor t = (TextEditor) widget;
			
				
				float val = 0;
				if(format == Format.Float || format == Format.Default)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						val = Float.valueOf(text);
					
					}catch(NumberFormatException e){}
				}else if(format == Format.Float2x)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						val = Float.valueOf(text)/2f;
					
					}catch(NumberFormatException e){}
				}else if (format == Format.Angle)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						
						val= (toRadians(Float.valueOf(text)));
					
					}catch(NumberFormatException e){}
				}else if (format == Format.Double)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						
						val = Float.valueOf(text);
					}catch(NumberFormatException e){}
				}
			
				
				if((type == Type.VECTOR3)  )
				{
					Vector3Type v =(Vector3Type) getPrototype().getProperty(key, new Vector3Type()).deepCopy();
					if(index == 0)
					{
						v.getValue().setX(val);
					}else if(index == 1)
					{
						v.getValue().setY(val);
					}else if(index == 2)
					{
						v.getValue().setZ(val);
					}
					getPrototype().setProperty(key, v);
				}else 	if((type == Type.VECTOR2) )
				{
					Vector2Type v =(Vector2Type) getPrototype().getProperty(key, new Vector2Type()).deepCopy();
					if(index == 0)
					{
						v.getValue().x = val;
					}else if(index == 1)
					{
						v.getValue().y = val;
					}
					getPrototype().setProperty(key, v);
				}else 	if((type == Type.COLOR) )
				{
			
					 ColorType v =(ColorType) getPrototype().getProperty(key, new ColorType()).deepCopy();
					if(index == 0)
					{
						v.getValue().r = val;
					}else if(index == 1)
					{
						v.getValue().g = val;
					}else if(index == 2)
					{
						v.getValue().b = val;
					}else if(index == 3)
					{
						v.getValue().a = val;
					}
					getPrototype().setProperty(key, v);
				}else if((type == Type.QUATERNION) )
				{
					
					propertyStoreAdjuster.setIndexValue(key,index, val);
					
					//this is mostly unused, but it is a) backup, and b) required for the property store adjuster to detect that
					QuaternionType v = new QuaternionType();
					float[] angles = new float[3];
					v.getValue().toAngles(angles);
					angles[index] = val;
					v.getValue().fromAngles(angles);//have to handle quaternions more carefully than this for floating point reasons - thus the index value thing
					getPrototype().setProperty(key, v);
				}

			}
		
	}
		
	@SuppressWarnings("unchecked")
	public void standardClosingBehaviour(StatefullWidget widget,String key, Format format) {
	
		if(!isAltered(widget))
			return;
	
		setValueAltered(key,true);
	
	//	if (!hasMultipleValues(key))
		{
			if (widget instanceof TextEditor)
			{
				TextEditor t = (TextEditor) widget;
			
				if(format == Format.Float || format == Format.Default)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						float v = Float.valueOf(text);
					
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if(format == Format.Float2x)
				{
					try{

						String text = t.getText();
						text =  text.replaceAll(",",".");
						float v = Float.valueOf(text)/2f;
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if (format == Format.Angle)
				{
					try{
						String text = t.getText();
						text =  text.replaceAll(",",".");
						float v = (toRadians(Float.valueOf(text)));
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if (format == Format.Double)
				{
					try{
						String text = t.getText();
						text =  text.replaceAll(",",".");
						double v = Double.valueOf(text);
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if (format == Format.Bool)
				{
					try{
						boolean v = Boolean.valueOf(t.getText());
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if (format == Format.String)
				{
					try{						
						this.getPrototype().setProperty(key, t.getText());
					}catch(NumberFormatException e){}
				}else if (format == Format.Int)
				{
					try{
						int v = Integer.valueOf(t.getText());
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}else if(format == Format.ProportionalX)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						double val = toProportionalX (Double.valueOf(text));
						this.getPrototype().setProperty(key, val);
					}catch(NumberFormatException e){}
				}else if(format == Format.ProportionalY)
				{
					try{
						String text = t.getText();
						text = text.replaceAll(",",".");
						double val = toProportionalY (Double.valueOf(text));
						this.getPrototype().setProperty(key, val);
					}catch(NumberFormatException e){}
				}


			}else 	if (widget instanceof CheckBox)
			{
				CheckBox t = (CheckBox) widget;
				
				if (format == Format.Bool || format == Format.Default)
				{
					try{
						boolean v = Boolean.valueOf(t.isSelected());
						this.getPrototype().setProperty(key, v);
					}catch(NumberFormatException e){}
				}

			}else 	if (widget instanceof ComboBox)
			{
				//dont know?
				ComboBox t = (ComboBox) widget;
				
				Object selected = t.getSelectedItem().getValue();
				if(selected instanceof Enum)
				{
					Enum<?> val = (Enum<?>)selected;
				
					{
						try{
							this.getPrototype().setProperty(key, val);
						}catch(IllegalArgumentException e)
						{
							StateManager.logError(e);
						}
					}
				}
			}else if  (widget instanceof ColorPatch)
			{
				ColorPatch t = (ColorPatch)widget;
				this.getPrototype().setProperty(key, t.getColorRGBA());
			}
			
		}
		
	}

	public void setComparePoint() {
		// TODO Auto-generated method stub
		
	}

	public Action<?> apply()
	{
		//do any required operations before applying 
		return propertyStoreAdjuster.apply();
	}

	
/*	public void setComparePoint() {
		this.propertyStoreAdjuster.setComparePoint();
	}*/
}
