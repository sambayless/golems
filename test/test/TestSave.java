package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureWrapper.TextureFormat;
import com.golemgame.save.DataTypeMap;
import com.golemgame.save.Loadable;
import com.golemgame.save.LoadableConstructorKey;
import com.golemgame.save.NoSuchKeyException;
import com.golemgame.save.ObjectConstructionException;
import com.golemgame.save.RefuseToLoadException;
import com.golemgame.save.RefuseToStoreException;
import com.golemgame.save.Store;
import com.golemgame.save.direct.DirectStore;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;

public class TestSave {

	/**
	 * @param args
	 * @throws ObjectConstructionException 
	 * @throws NoSuchKeyException 
	 */
	public static void main(String[] args) throws NoSuchKeyException, ObjectConstructionException {
		List<Integer> testList = new ArrayList<Integer>();
		testList.add(2);
		testList.add(3);
		try{
		DirectStore xmlStore = new DirectStore();
		xmlStore.put("testList", testList);
		xmlStore.putRaw("num", 0.001f);
		xmlStore.put("testRaw",3);
		
		xmlStore.put("vector", new Vector3f(1,2,3));
		
		Map testMap = new HashMap();
		testMap.put(2, "test2");
		testMap.put(3, "test3");
		
		xmlStore.put("testMap", testMap);
		
		xmlStore.put("testEnum", TextureWrapper.TextureFormat.RGB);
		
		DataTypeMap.getInstance().put("TestModel", LoadableModel.class);
		
		LoadableModel testModel = new LoadableModel("Original Model");
		xmlStore.put("model", testModel);
		
		

		DirectStore xmlRetrieve = new DirectStore(xmlStore);
		
		Object[] o = xmlRetrieve.retrieveAll("testList");
		System.out.println(o[0]);
		
		
		//note: type safety fails for collections here
		System.out.println( xmlRetrieve.<List<Integer>>retrieve("testList", new ArrayList<Integer>()));
		
		System.out.println(xmlRetrieve.<Vector3f>retrieve("vector", new Vector3f()));
		
		System.out.println( xmlRetrieve.retrieve("num", 0.1f));
		
		
		System.out.println( xmlRetrieve.retrieve("testRaw", 1));
		
		
		
		System.out.println( xmlRetrieve.<LoadableModel>retrieve("model", new LoadableModel("Load failed")));
		
		System.out.println(xmlRetrieve.retrieve("testMap", new HashMap()));
		
		TextureFormat testEnum = xmlRetrieve.retrieve("testEnum", TextureFormat.RGBA);
		System.out.println(testEnum);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	public static class LoadableModel extends SpatialModelImpl implements Loadable
	{

			/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
			private String name = "";
		
			public LoadableModel(String name) {
			super();
			this.name = name;
			// TODO Auto-generated constructor stub
		}

		@Override
			public String toString() {
				return name;
			}

		public LoadableModel(boolean registerSpatial) {
			super(registerSpatial);
			// TODO Auto-generated constructor stub
		}
		
		public LoadableModel(LoadableConstructorKey loadKey)
		{
			this.name = "Loaded Model";
		}

			@Override
			protected Spatial buildSpatial() {
				return new Box("testBox", new Vector3f(), 2f, 3f, 4f);
			}

			@Override
			public void load(Store store) throws RefuseToLoadException {
				super.setLocalTranslation(store.retrieve("local.translation", new Vector3f()));
			}

			@Override
			public void loadInitialize() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void store(Store store) throws RefuseToStoreException {
				store.put("local.translation", super.getLocalTranslation());
			}
			
		
		
	}
	
}
