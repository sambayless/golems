package com.golemgame.model.texture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.model.texture.TextureWrapper.TextureFormat;
import com.golemgame.model.texture.spatial.SpatialTextureServer;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.StateManager;
import com.jme.image.Texture;

/**
 * The texture server loads texture type keys.
 * It allows you to register a load a texutre wrapper by registering it against the texture type keys.
 * @author Sam
 *
 */
public abstract class TextureServer {
	private static final TextureServer instance = new SpatialTextureServer();

	private static final ExecutorService textureThreadPool =  new ThreadPoolExecutor(1,1,1, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(), new TextureThreadFactory());
	
	//private ReentrantLock listenerLock = new ReentrantLock();
	private ReentrantLock loadingLock = new ReentrantLock();
	/**
	 * This maintains a list of methods to be called when a texture completes loading.
	 *//*
	private Map<TextureWrapper, ConcurrentLinkedQueue<Callable<?>>> loadingListeners = new HashMap<TextureWrapper, ConcurrentLinkedQueue<Callable<?>>>();
	

	private Map<TextureTypeKey,Queue<TextureWrapper>> loadingWrappers = new HashMap<TextureTypeKey,Queue<TextureWrapper>>();
	*/
	
	/**
	 * A set of loaded keys.
	 */
	private Set<TextureTypeKey> loadedKeys = new HashSet<TextureTypeKey>();
	
	//private Set<TextureTypeKey> loadingKeys = new HashSet<TextureTypeKey>();
	
	
	/**
	 * A map for each *UNLOADED* texture type key that is waiting to load, pointing to each texture wrapper that should be notified when loading is complete.
	 */
	private Map<TextureTypeKey,Set<TextureWrapper>> wrapperMap = new HashMap<TextureTypeKey,Set<TextureWrapper>>();
	
	
	
	
	private static class TextureThreadFactory implements ThreadFactory
	{

		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.MIN_PRIORITY);
			t.setDaemon(true);
			return t;
		}
		
	};
	
//	protected abstract int loadTextureID(TextureTypeKey textureClass) throws NoTextureException;
	
	/**
	 * Get a texture if it is loaded (null otherwise);
	 */
	public abstract Texture getLoadedTexture(TextureTypeKey textureClass) ;
	
	public abstract int getLoadedTextureID(TextureTypeKey textureClass) ;
	
	
	public TextureWrapper getTexture(ImageType image)
	{
		TextureTypeKey textureClass = new TextureTypeKey(image,512,512, TextureWrapper.TextureFormat.RGBA,true);
		return getTexture(textureClass);
	}
	
	public TextureWrapper getTexture(ImageType image, int width,int height, TextureFormat format, boolean useMipmaps)
	{
		TextureTypeKey textureClass = new TextureTypeKey(image, width,height, format,useMipmaps);
		return getTexture(textureClass);
	}
	

	
	/**
	 * 
	 * @param textureClass
	 * @param loadNow Whether to load the texture now, or not. For SpatialTextures, the texture will be loaded lazily anyways, so this should be to true.
	 * @return
	 */
	public  synchronized TextureWrapper getTexture(TextureTypeKey textureClass, boolean loadNow)
	{
	/*	TextureWrapper wrapper = makeTextureWrapper(textureClass);
		((SpatialTexture)wrapper).setTexture(new Texture(), 0);
		
		return wrapper;*/
	//	try{
			TextureWrapper wrapper = makeTextureWrapper(textureClass);
			//if (loadNow)			
			loadTextureWrapper(wrapper);
		
			
			
			return wrapper;
	/*	}catch(NoTextureException e)
		{
			StateManager.logError(e);
			throw new NullPointerException("No Texture");//throw early
		}*/

	}
	
	private void loadTextureWrapper(TextureWrapper wrapper) {
		for(int i = 0;i< wrapper.getElements();i++)
		{
			TextureTypeKey key = wrapper.getTextureTypeKey(i);
			
			loadingLock.lock();
			try{
				if(isLoaded(key))
					wrapper.load(key); //load immediately
				else{
					
					associateWithKey(key,wrapper);
					loadTexture(key);
					
				}
			
			}finally{
				loadingLock.unlock();
			}
		}
	}
	
	protected void notifyIsLoaded(TextureTypeKey key)
	{
		loadingLock.lock();
		try{
			loadedKeys.add(key);
			Set<TextureWrapper> wrapperSet = wrapperMap.get(key);
			if(wrapperSet!=null)
			{
				for(TextureWrapper wrapper:wrapperSet)
					wrapper.load(key);
				
			}
			wrapperMap.remove(key);
			
		}finally{
			loadingLock.unlock();
		}
	}


	private void associateWithKey(TextureTypeKey key, TextureWrapper wrapper) {
		loadingLock.lock();
		try{
			if(isLoaded(key))
			{
				wrapper.load(key);
			}else{
				Set<TextureWrapper> wrapperSet = wrapperMap.get(key);
				if(wrapperSet==null)
				{
					wrapperSet = new HashSet<TextureWrapper>();
					wrapperMap.put(key, wrapperSet);
				}
				wrapperSet.add(wrapper);
			}
			
		}finally{
			loadingLock.unlock();
		}
	}

	/**
	 * Request that a texture be loaded, at some point in the future,
	 * at which time notifyIsoaded should be called with the corresponding key.
	 * @param key
	 */
	public abstract void loadTexture(TextureTypeKey key) ;
	


	public  TextureWrapper getTexture(TextureTypeKey textureClass)
	{
		return this.getTexture(textureClass, true);
	}
	
	protected abstract TextureWrapper makeTextureWrapper(TextureTypeKey textureClass);
	
	/*public void loadTextureWrapper(TextureWrapper textureWrapper) throws NoTextureException
	{
		wrapperLock.lock();
		try{
			for (int element = 0;element<textureWrapper.getElements();element++)
			{
				Queue<TextureWrapper> wrappers = loadingWrappers.get(textureWrapper.getTextureTypeKey(element));
				if(wrappers == null)
				{
					wrappers = new LinkedList<TextureWrapper>();
					loadingWrappers.put(textureWrapper.getTextureTypeKey(element), wrappers);
				}
			//	if(!wrappers.contains(textureWrapper))//watch out could be slow
					wrappers.add(textureWrapper);
			}
		}finally{
			wrapperLock.unlock();
		}
		
	}*/


	public static TextureServer getInstance() {
		return instance;
	}
	
	protected void execute(Runnable r)
	{
		TextureServer.textureThreadPool.execute(r);
	}
	
	/*public void addTextureLoadingCallback(TextureWrapper textureClass, Callable<?> listener)
	{
		if (textureClass == null)
			return;
		
		if (textureClass.isLoaded())
			{
				try{
					listener.call();
					return;
				}catch(Exception e)
				{
					StateManager.logError(e);
				}finally
				{

				}
			}
		listenerLock.lock();
		try{//prevent two listener queues from being added concurrently, which would cause one to be lost.
			ConcurrentLinkedQueue<Callable<?>> listeners = loadingListeners.get(textureClass);
			if (listeners != null)
				listeners.add(listener);
			else
			{
				listeners = new ConcurrentLinkedQueue<Callable<?>> ();
				listeners.add(listener);
				loadingListeners.put(textureClass, listeners);
			}
		}finally
		{
			listenerLock.unlock();
		}

	}
	*/
	
	/*
	protected void textureLoaded(TextureTypeKey textureClass)
	{
		if (textureClass == null)
			return ;
		
		Queue<TextureWrapper> queue = null;
		wrapperLock.lock();
		try{
			 queue = loadingWrappers.get(textureClass);
		}finally{
			wrapperLock.unlock();
		}
		if(queue == null)
			return;
		Set<TextureWrapper> toLoadQueue = new HashSet<TextureWrapper>();
		try{
		
			while(queue.peek() != null)
			{
				TextureWrapper wrapper = queue.poll();
				
				if(!wrapper.isLoaded())
					this.loadTextureWrapper(wrapper);
				
				toLoadQueue.add(wrapper);
			}
			
		}catch(NoTextureException e)
		{
			StateManager.logError(e);
		}finally{
			for(TextureWrapper wrapper:toLoadQueue)
			{
				callTextureLoadingCallbacks(wrapper);
			}
		}
	}
	*/
	/*
	private void callTextureLoadingCallbacks(final TextureWrapper textureClass)
	{
	

				ConcurrentLinkedQueue<Callable<?>> listeners = loadingListeners.get(textureClass);
					if(listeners == null)
						return ;
					
					while(!listeners.isEmpty())
					{
						try{
							//long startTime = System.nanoTime();
							listeners.poll().call();
							//System.out.println(textureClass + "\t" + (System.nanoTime() - startTime)/1000000f);
							//Thread.sleep(250);
						}catch(Exception e)
						{
							StateManager.logError(e);
						}
						
					}
					loadingListeners.remove(textureClass);
				return ;
			


	}*/
	
	public boolean isLoaded(TextureTypeKey typeKey){
		loadingLock.lock();
		try{
			return loadedKeys.contains(typeKey);
		}finally{
			loadingLock.unlock();
		}
	}

	protected void lock(){
		loadingLock.lock();
	}
	
	protected void unlock()
	{
		loadingLock.unlock();
	}

	public static class NoTextureException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public NoTextureException() {
			super();
		
		}

		public NoTextureException(String message, Throwable cause) {
			super(message, cause);
		
		}

		public NoTextureException(String message) {
			super(message);

		}

		public NoTextureException(Throwable cause) {
			super(cause);

		}
		
	}
}
