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
package com.golemgame.menu;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.fenggui.Button;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.List;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyAdapter;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Dimension;
import org.fenggui.util.ImageConverter;

import com.golemgame.local.StringConstants;
import com.golemgame.states.StateManager;
import com.golemgame.util.loading.ConcurrentLoadable;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;

/**
 * This class displays the standard components of a file choosing dialog.
 * To retrieve the chosen file from the dialog, attach a FileChooserDialog.FileDialogListener
 * to the FileChooserDialog.
 * 
 * @author Sam Bayless
 *
 */
public class FileChooserDialog extends Container implements ConcurrentLoadable
{
	
	/**Maximum amount of time to wait while another thread is displaying folder contents before giving up.*/
	private static final int DIRECTORY_TIMEOUT = 5000;
	/**Maximum amount of time to wait before giving up on calling the GL thread.*/
	private static final long TIME_OUT = 1000;
	/**Number of types of icons to hold in cache. Items are removed from the cache in the order they were entered.*/
	private final static int LRU_CACHE_SIZE = 100;
	/**this determines the number of pixels that are examined from each icon to form a hash number; increase this number if icons are incorrectly identified.*/
	private final static int ICON_FINGERPRINT_SIZE = 16;//
	/**The size of an icon.*/
	private final int ICON_DIMENSIONS;
	/**The displayed directory.*/
	private File currentDirectory;
	/**Thread safe list of previous viewed directories*/
	private Stack<File> previousDirectories = new Stack<File>();
	/**Set of files to display in the top drop down box*/
	private Set<File>	 rootFiles = new HashSet<File>();
	/**File selected by the user*/
	private File selectedFile = null;

	private ExecutorService threadPool = null;
	
	private ArrayList<FileDialogListener> fileDialogListeners = new ArrayList<FileDialogListener>();
	
	private volatile boolean cancelLoading = false;
	
	/**
	 * Whether or not to use icon caching
	 */
	private final boolean useCache;
	
	/**
	 * Lock for changes to the list of root files.
	 */
	private Lock rootFileLock = new ReentrantLock();
	
	/**
	 * Whether or not to use multithreaded routines.
	 */
	private final boolean multithreaded;
	/**
	 * The identity of the openGl thread. Required to prevent deadlocks.
	 */
	private Thread glThread = null;

	private AtomicBoolean dropDownLock = new AtomicBoolean();
	
	private Semaphore directoryUpdateSemaphore = new Semaphore(1);
	

	
	/**
	 * Contains a set of random coordinates used for generating hashes from icons.
	 */
	private int[] fingerprintX;
	
	/**
	 * Contains a set of random coordinates used for generating hashes from icons.
	 */
	private int[] fingerprintY;
	
	/**
	 * This is an LRU cache for recently used icon textures.
	 * See http://blogs.sun.com/swinger/date/20041012#collections_trick_i_lru_cache
	 */
	private final LinkedHashMap<Integer, Pixmap>  iconMap = new LinkedHashMap<Integer, Pixmap> ( 10, 0.75f, true )
	 {
		private static final long serialVersionUID = 1;

		
		protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Pixmap> eldest) {
			 return size() > LRU_CACHE_SIZE;
		}


	 };

	 /**
	  * Holds a simple, thread safe task queue that is executed when the dialog is painted (ie, in the openGL thread).
	  */
	private ConcurrentLinkedQueue<Runnable> glQueue = new ConcurrentLinkedQueue<Runnable>();
		

	private Map<FileFilter, String> fileFilters = new HashMap<FileFilter, String>();
	private List<FileItem> fileList;
	private FileFilter currentFilter = null;
	
	private ComboBox<FileItem> dropDown;
	private TextEditor fileName;
	private ComboBox<FileFilter> filterList;
	
	private Button upButton;
	private Button backButton;
	
	private ScrollContainer scrollContainer;
	
	private Container internalContainer;

	/**
	 * Creates a simple file choosing dialog. 
	 * @param threadPool This parameter is ignored if multithreading is not in use. Otherwise, if null, a new 
	 * ThreadPoolExecutor will be created for this dialog; if you provide a ThreadPool, then the dialog will use
	 * it for loading file icons.
	 * @param glThread This parameter is ignored if multithreading is not in use. Certain methods must be run from
	 * the OpenGL thread (for example, creating a texture). Providing the identity of the glThread, while not necessary,
	 * is recommended; if the GLThread is not provided, it will attempt to find it out; until it does so, 
	 * multithreading will be disabled.
	 * @param multithreaded Specify whether to make this dialog multithreaded or not. (It is strongly recommended to use multithreading).
	 */
	public FileChooserDialog(ExecutorService threadPool, Thread glThread,  boolean multithreaded)
	{
		super();
		this.multithreaded = multithreaded;
		this.glThread = glThread;
		this.useCache = true;//use icon caching

		//find out the size of a test icon, and assume all other icons will have the same dimensions
		File file = FileSystemView.getFileSystemView().getDefaultDirectory();
		if (file != null)
			ICON_DIMENSIONS=FileSystemView.getFileSystemView().getSystemIcon(file).getIconWidth();
		else
			ICON_DIMENSIONS = 16;
		
		/*
		 * Because loading and displaying the files is relatively slow, it is strongly recommended
		 * to use multithreading with this dialog. 
		 */
		if (!multithreaded)
			threadPool = null;
		else
		{
			if (threadPool == null)
				this.threadPool = new ThreadPoolExecutor(1,1,10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
			else
				this.threadPool = threadPool;
		}
			
		
		buildFingerPrints();
		buildComponents();

		initComponents();
		setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
	}
	
	private void buildFingerPrints()
	{
		/*
		 * Creating textures for icons is expensive, so this dialog will cache recently created 
		 * icon textures. However, there is only one reliable way to determine whether a particular file
		 * has a particular icon: to load the icons and compare them. 
		 * It would be expensive to compare, pixel for pixel, each icon, so instead a hash is created 
		 * from 16 preselected random pixels (fingerprintX and Y). 
		 * 
		 * 
		 */
	
		
		Random random = new Random();
		
		fingerprintX = new int[ICON_FINGERPRINT_SIZE];
		for (int i = 0; i < fingerprintX.length; i++)
		{

				fingerprintX[i] = random.nextInt(ICON_DIMENSIONS-4)+2;
				//restrict the random pixels to not include the outermost ones, 
				//which are more likely to be transparent
		}
		
		fingerprintY = new int[ICON_FINGERPRINT_SIZE];
		for (int i = 0; i < fingerprintY.length; i++)
		{

			fingerprintY[i] = random.nextInt(ICON_DIMENSIONS-4) + 2;
			//restrict the random pixels to not include the outermost ones, 
			//which are more likely to be transparent
		}
		
	
	}

	/**
	 * Create a file dialog that is multithreaded.
	 */
	public FileChooserDialog() {
		this(null,null, true);
	}

	/*
	 * -------------THIS SECTION NOT FOR SUBMITION
	 * 
	 */
	

	public static FileChooserDialog createLoadableFileChooserDialog(ExecutorService threadPool, Thread glThread,  boolean multithreaded)
	{
		return new FileChooserDialog(threadPool, glThread, multithreaded, true);
	}
	
	/**
	 * Creates a simple file choosing dialog. 
	 * @param threadPool This parameter is ignored if multithreading is not in use. Otherwise, if null, a new 
	 * ThreadPoolExecutor will be created for this dialog; if you provide a ThreadPool, then the dialog will use
	 * it for loading file icons.
	 * @param glThread This parameter is ignored if multithreading is not in use. Certain methods must be run from
	 * the OpenGL thread (for example, creating a texture). Providing the identity of the glThread, while not necessary,
	 * is recommended; if the GLThread is not provided, it will attempt to find it out; until it does so, 
	 * multithreading will be disabled.
	 * @param multithreaded Specify whether to make this dialog multithreaded or not. (It is strongly recommended to use multithreading).
	 */
	private FileChooserDialog(ExecutorService threadPool, Thread glThread,  boolean multithreaded, boolean concurrentLoad)
	{
		super();
		this.multithreaded = multithreaded;
		this.glThread = glThread;
		this.useCache = true;//use icon caching

		//find out the size of a test icon, and assume all other icons will have the same dimensions
		File file = FileSystemView.getFileSystemView().getDefaultDirectory();
		if (file != null)
			ICON_DIMENSIONS=FileSystemView.getFileSystemView().getSystemIcon(file).getIconWidth();
		else
			ICON_DIMENSIONS = 16;
		
		/*
		 * Because loading and displaying the files is relatively slow, it is strongly recommended
		 * to use multithreading with this dialog. 
		 */
		if (!multithreaded)
			threadPool = null;
		else
		{
			if (threadPool == null)
				this.threadPool = new ThreadPoolExecutor(1,1,10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
			else
				this.threadPool = threadPool;
		}

	}

	private boolean loaded = false;
	protected ReentrantLock loadingLock = new ReentrantLock();
	protected Condition loadingCondition = loadingLock.newCondition();
	
	public boolean isLoaded() {
		return loaded;
	}

	
	public void waitForLoad() {
		while(!isLoaded())
		{
			try{
				loadingCondition.await();
			}catch(InterruptedException e)
			{
			}
		}
	}

	
	public void load() throws Exception {
		
		loadingLock.lock();
		try {
			
			buildFingerPrints();
			StateManager.getGame().executeInGL(new Callable<Object>()
					{
						
						public Object call() throws Exception {
							buildComponents();
							return null;
						}
					});
			//
			initComponents();
			setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
			loaded = true;
			loadingCondition.signalAll();
		} finally {
			loadingLock.unlock();
		}
	}

	/*
	 * END SECTION NOT FOR SUBMITION
	 * 
	 */
	
	/**
	 * Set the current directory to display
	 * @param file
	 */
	public void setCurrentDirectory(File file)
	{
		setCurrentDirectory(file, false);
	}
	
	/**
	 * Go back to the last seen directory
	 */
	public void back()
	{
		synchronized(previousDirectories)
		{
			File file;
			try{
				if ((file = previousDirectories.pop()) != null)
				{
					setCurrentDirectory(file,true);
				}
			}catch(EmptyStackException  e)
			{
				//do nothing
			}
		}
	}

	/**
	 * Display the files in the current folder.
	 */
	public void refreshFiles()
	{	
		cancelLoading = false;
				if (!multithreaded || (glThread == null))
				{
						//try
						{
						//	if (directoryUpdateSemaphore.tryAcquire(DIRECTORY_TIMEOUT, TimeUnit.MILLISECONDS))
							{
							
								try{
									
									final FileFilter filter = this.getCurrentFilter();
									final File file = currentDirectory;
									if (file == null || !file.isDirectory())
										return;
									fileList.clear();
									populateDropDown(dropDown,file);
									//this can be slow - if possible, put it into a separate thread.
									
									
									ArrayList<FileItem> items = new ArrayList<FileItem>();
									for (File f:file.listFiles(filter))
									{
										if (!f.isHidden())
										{
											FileItem item = new FileItem(f);
											items.add(item);
										}
									}
									
								
								
									Collections.sort(items, fileComparator);
									
									for (FileItem item:items)
									{			
									
											fileList.addItem(new ListItem<FileItem>(item.toString(),getIcon(item.getFile()), item));
											
									}
									fileList.layout();
									scrollContainer.layout();
									scrollContainer.scrollVertical(1);
								}finally
								{//release the semaphore no matter what happens
									directoryUpdateSemaphore.release();
								}
							}	
						}
					
				}else
				{
						threadPool.execute(new Runnable()
						{
			
							
							public void run() {
								//try
								//{
							
									//if (directoryUpdateSemaphore.tryAcquire(DIRECTORY_TIMEOUT, TimeUnit.MILLISECONDS))
									{
										
										try{
											
											final FileFilter filter = getCurrentFilter();
											final File file = currentDirectory;
											if (file == null  || !file.isDirectory())
											{
											
												return;
											}
											fileList.clear();
											populateDropDown(dropDown,file);
											final	ArrayList<FileItem> items = new ArrayList<FileItem>();
											if (filter != null)
											{
												File[] files = file.listFiles(filter);
												if (files != null)
												{
									
														for (File f : files)
														{
															if (!f.isHidden())
															{
																FileItem item = new FileItem(f);
																items.add(item);
															}
														}
													
												}else
												{//For unknown reasons, my computer returns a null list of files when given a filter, so try again if that happens without a filter.
													files = file.listFiles();
													if (files != null)
													{
														for (File f : files)
														{
															if (!f.isHidden())
															{
																FileItem item = new FileItem(f);
																items.add(item);
															}
														}
													}
												}
											}else
											{
												File[] files = file.listFiles();
												if (files != null)
												{
													for (File f : files)
													{
												
														if (!f.isHidden())
														{
															FileItem item = new FileItem(f);
															items.add(item);
														}
													}
												}
											}
										
											if (items.isEmpty())
											{
											
												return;
											}
											Collections.sort(items, fileComparator);
											scrollContainer.scrollVertical(1);
											
											if(cancelLoading)
												return;
											
											//final FileItem lastItem = items.get(items.size()-1);
											final int epoch = Math.max(items.size()/6,6);
											//int epochPos = 0;
											
											for (int i = 0;i<items.size();i+= epoch)
											{		
												if (cancelLoading)
													return;
													
													final Pixmap[] icons = new Pixmap[epoch];
												
													for (int e = 0;e<epoch && i + e < items.size();e++)
													{
														FileItem item = items.get(i+e);
														Pixmap icon = getIcon(item.getFile());
														icons[e] = icon; 
													}
												   
													final int iPos = i;
												    
												    try{
												    	callFromGL(new Callable<Object>()
												    			{//This has to be executed from the GL thread, otherwise the list will jump between scroll positions in an ugly way while it is being filled
																	
																	public Object call()throws Exception 
																	{
																		for (int e = 0;e<epoch && iPos + e < items.size();e++)
																		{
																			try{
																				FileItem item = items.get(iPos+e);
																				
																			    if (icons[e] != null)
																			    {
																			    	fileList.getItems().add(new ListItem<FileItem>(item.toString(),icons[e], item));
																			    }else //this is causing concurrent mod. errors
																			    	fileList.getItems().add(new ListItem<FileItem>(item.toString(), item));
																			    
																			  
																			}finally{
																				 
																			   // if (item == lastItem)
																			   // 	directoryUpdateSemaphore.release();
																			    //allow the directory to be refreshed after the file is displayed
																		}
																		}
																		fileList.updateMinSize();
																		 scrollContainer.scrollVertical(1);
																		return new Object();
																	}
												    		
												    			});												    
													}catch(TimeoutException e)
													{//these are ok, they just mean the gl thread was too busy.
														StateManager.getLogger().warning(e.toString());
													}  catch(Exception e)
												    {
												    	e.printStackTrace();
												    }
													
											}
											
											fileList.layout();
											scrollContainer.layout();
											scrollContainer.scrollVertical(1);
								
										}catch(Exception e)
										{
											e.printStackTrace();
										}finally
										{
											directoryUpdateSemaphore.drainPermits();
											directoryUpdateSemaphore.release();
										}
									}
							//}
								/*catch(InterruptedException e)
								{
									e.printStackTrace();
								}*/
							}
							
						});
					}
			
			

	}
	
	/**
	 * Attempt to cancel whatever file loading is currently occuring (for example, if you want to 
	 * switch to a different directory before this one has finished loading)
	 */
	public void cancelLoadingFiles()
	{
		
	}
	
	public void setCurrentDirectory(final File file, boolean dontAddToHistory)
	{
		if (file == null || !file.isDirectory())
			return;
		

			File currentDirectory = this.getCurrentDirectory();
			if (currentDirectory != null && currentDirectory.equals(file))
			{
				return;
			}
		
		try {
			cancelLoading = true;
			if(directoryUpdateSemaphore.tryAcquire(DIRECTORY_TIMEOUT, TimeUnit.MILLISECONDS))
			{	
				if (!dontAddToHistory && currentDirectory != null)
				{
					synchronized(previousDirectories)
					{
						previousDirectories.add( currentDirectory);
					}
				}
				this.currentDirectory = file;
				setSelectedFile(null);
				refreshFiles();
			}else
			{
				System.out.println("Directory loading timed out");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Execute the callable from the GL thread, sometime in the future.
	 * @param toCall
	 */
	protected void executeInGL(Callable<?> toCall) throws Exception 
	{
		if (glThread == null)
			return;
		if (!multithreaded || Thread.currentThread() == glThread )
		{
			toCall.call();
		}else
		{
			//FutureTask<?> task = new FutureTask(toCall);
			//glQueue.add(task);
			//This section depends on JME
			GameTaskQueueManager.getManager().update(toCall);
		}
	}
	
	/**
	 * Execute the callable from the GL thread, and wait until it returns;
	 * @param <E>
	 * @param toCall
	 * @return
	 */
	private <E> E callFromGL(Callable<E> toCall) throws Exception
	{
				
			if (glThread == null)
				throw new Exception("GL Thread not known");
			if ( Thread.currentThread() == glThread)
			{
				return toCall.call();
			}else
			{
				//This section depends on JME
				//FutureTask<E> task = new FutureTask<E>(toCall);
				//glQueue.add(task);
				Future<E> task = GameTaskQueueManager.getManager().update(toCall);
				return task.get(TIME_OUT, TimeUnit.MILLISECONDS);
			}
	
	}
	
	private BufferedImage img = ImageConverter.createGlCompatibleAwtImage(16, 16) ;//new BufferedImage(16, 16,BufferedImage.TYPE_INT_ARGB);

	
	private int computeHash(BufferedImage image)
	{
		long hash = 31;//prime value
		for (int i = 0; i < fingerprintX.length; i++)
		{
			hash *= image.getRGB(fingerprintX[i], fingerprintY[i]) + 1;
			hash %= Integer.MAX_VALUE;
			
		}
		
		return (int) hash;
	}

	/**
	 * Efficiently get the icon for this file, if it exists.
	 * Retrieved icons are cached for later use.
	 * Returns null if no icon could be found.
	 * @param file
	 * @return
	 */
	private Pixmap getIcon(final File file)
	{
		
		FileSystemView view = FileSystemView.getFileSystemView();
		
		//final String fileType = view.getSystemTypeDescription(file);
		//Unfortunately, on some platforms (Windows), files and folders that have different icons will have the same description.
		//For example, My Documents may have a unique icon, but will have the same description as other folders.
		//If a cache is used, this can lead to incorrect icons.
		
		Icon icon = view.getSystemIcon(file);
		
		//Because only one Image context is being used, it is important that only one thread access it at a time.
		synchronized(img)
		{
			Graphics2D graphics = img.createGraphics();
			Composite previousComposite = graphics.getComposite();
			
			//Erase any previous texture in this icon
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
			graphics.setComposite(previousComposite);
			
			try{
				icon.paintIcon(null, graphics, 0, 0);
				
			}catch(ArrayIndexOutOfBoundsException e)
			{
				StateManager.logError(e);//some icons throw this for unknown reasons
			}
		
			int hash = computeHash(img);
			
			
			if (useCache)
			{
				if (iconMap.containsKey(hash))
				{//this isnt working?
					return iconMap.get(hash);
				}
			}
	
			Pixmap pixmap;
				
				if (Thread.currentThread() == glThread)
				{
					ITexture texture = Binding.getInstance().getTexture(img);
					pixmap = new Pixmap(texture);
				}else
				{
					Callable<Pixmap> pixCall = new Callable<Pixmap>()
					{
						public Pixmap call()
						{
							
							ITexture texture = Binding.getInstance().getTexture(img);
							Pixmap pixmap = new Pixmap(texture);
					        com.jme.renderer.RenderContext context = DisplaySystem.getDisplaySystem()
					        .getCurrentContext();
							TextureStateRecord record = (TextureStateRecord) context
							        .getStateRecord(RenderState.RS_TEXTURE);
							record.invalidate();
							return pixmap;
						}
					};
					
					try{
						pixmap = callFromGL(pixCall);
					}catch(Exception e)
					{
						pixmap = null;
					}
				}
				if (useCache && pixmap != null )
					iconMap.put(hash, pixmap);
				return pixmap;
			
		}
		
	}
	
	/**
	 * Set common root files to appear in the top drop down box.
	 */
	private void setRootFiles()
	{
		FileSystemView view = FileSystemView.getFileSystemView();
		rootFileLock.lock();
		try{
			for (File f:view.getRoots())
			{
				if (!f.isHidden())
				{
				
					rootFiles.add(f);
				}
			}
			 rootFiles.add(view.getHomeDirectory());
			 File home = new File(System.getProperty("user.home"));
			 if (!home.equals(view.getHomeDirectory()))
			 {//on some systems (including Vista) these are not the same
				 rootFiles.add(home);
			 }
		}finally{rootFileLock.unlock();}
	}
	
	/**
	 * Add a folder to appear in the top drop down box of the dialog.
	 * Call refreshFiles to display the change.
	 * @param file
	 */
	public void addToRoots(File file)
	{
		rootFileLock.lock();
		try{
			rootFiles.add(file);
		}finally{rootFileLock.unlock();}
	}
	
	/**
	 * Remove a folder from the top drop down box of the dialog.
	 * Call refreshFiles to display the change.
	 * @param file
	 */
	public void removeFromRoots(File file)
	{
		rootFileLock.lock();
		try{
		rootFiles.remove(file);
		}finally{rootFileLock.unlock();}
	}
	
	/**
	 * Get the list of files in the top drop down box.
	 * This list is immutable.
	 * @return
	 */
	public Set<File> getRootFiles()
	{	
		return Collections.unmodifiableSet(rootFiles);
	}
	/**
	 * Populate the drop down with the root folders, and the current directory, and ensure the current directory is selected.
	 * @param dropDown
	 * @param currentDirectory
	 */
	private void populateDropDown(ComboBox<FileItem>dropDown, File currentDirectory)
	{		
			try{
				dropDownLock.set(true);
				dropDown.getList().clear();
				rootFileLock.lock();
				try{
				for (File file:rootFiles)
				{		
					FileItem item = new FileItem(file);
					ListItem<FileItem> listItem = new ListItem<FileItem>(item.toString(),getIcon(item.getFile()), item);
				
						dropDown.addItem(listItem);
				
					if (file.equals(currentDirectory))
						dropDown.setSelected(listItem);
				}
				}finally{rootFileLock.unlock();}
				
				if (!rootFiles.contains(currentDirectory) )
				{
					FileItem item = new FileItem(currentDirectory);
					ListItem<FileItem> current = new ListItem<FileItem>(item.toString(),getIcon(item.getFile()), item);
					dropDown.addItem(current);
					dropDown.setSelected(current);
				}
			}catch(ConcurrentModificationException e)
			{
				StateManager.logError(e);
			}finally
			{
				dropDownLock.set(false);
				//dropDownSemaphore.release();
			}
	
	}
	
	/**
	 * Signal that a file has been selected to the listeners.
	 */
	private synchronized void fileSelected()
	{
		

		File file = selectedFile;
		for (FileDialogListener listener:fileDialogListeners.toArray(new FileDialogListener[fileDialogListeners.size()]))
		{
			listener.fileSelected(file);
		}
	}
	
	/**
	 * Signal that no file was selected and the dialog was cancelled.
	 */
	public synchronized void cancel()
	{
		for (FileDialogListener listener:fileDialogListeners.toArray(new FileDialogListener[fileDialogListeners.size()]))
		{
			listener.cancel();
		}
	}
	
	/**
	 * Construct all the inner components of the dialog. They will be loaded with the current theme.
	 */
	private void buildComponents()
	{
		
		internalContainer = this;
		
		internalContainer.setLayoutManager(new BorderLayout());	
		
		Container topRow = new Container();
		topRow.setLayoutData(BorderLayoutData.NORTH);
		topRow.setLayoutManager(new RowExLayout());
		internalContainer.addWidget(topRow);
		
		dropDown = FengGUI.<FileItem>createComboBox(topRow);
		dropDown.setLayoutData(new RowExLayoutData(true,true));
		
		topRow.addWidget(dropDown);
		

		dropDown.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				if (dropDownLock.get())
					return;

					File file = dropDown.getSelectedItem().getValue().getFile();
					setCurrentDirectory(file);

				
			}
			
		});
		
		
		//This container defines the bottom section of the dialog
        Container south = FengGUI.createContainer(internalContainer);
        south.setLayoutData(BorderLayoutData.SOUTH);
        south.setLayoutManager(new RowExLayout(false));
        
        Container fileNameContainer = FengGUI.createContainer(south);
        fileNameContainer.setLayoutData(new RowExLayoutData(true,true));
        fileNameContainer.setLayoutManager(new RowExLayout());
        
        Label nameLabel = FengGUI.createLabel(fileNameContainer);
        nameLabel.setText(StringConstants.get("FILEMENU.FILE_NAME" ,"File Name: "));
        nameLabel.setLayoutData(new RowExLayoutData(false,false));
        
        fileName = FengGUI.createTextEditor(fileNameContainer);
        fileName.setLayoutData(new RowExLayoutData(true,true));
        fileName.addKeyListener(new KeyAdapter()
        {
			
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				if (keyPressedEvent.getKeyClass() == Key.ENTER)
				{
					attemptSelectFile(fileName.getText());
					keyPressedEvent.setUsed();
					fileSelected();
				}else if (keyPressedEvent.getKeyClass() == Key.ESCAPE)
				{
					keyPressedEvent.setUsed();
					cancel();
				}
			}
        });
        
    	
        Container filterContainer = FengGUI.createContainer(south);
        filterContainer.setLayoutData(new RowExLayoutData(true,true));
        filterContainer.setLayoutManager(new RowExLayout());
        
        Label filterLabel = FengGUI.createLabel(filterContainer);
        filterLabel.setText(StringConstants.get("FILEMENU.FILE_TYPES" ,"Files of Type: "));
        filterLabel.setLayoutData(new RowExLayoutData(false,false));
  
        
		filterList = FengGUI.createComboBox(filterContainer);

		
		filterList.setLayoutData(new RowExLayoutData(true,true));
		
		filterList.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				if (filterList.getSelectedItem() != null)
				{
					FileFilter filter = filterList.getSelectedItem().getValue();
					if (filter != null)
					{
						setCurrentFilter(filter);
					}
						
				}
				
				
			}
			
		});
		
		Container buttonContainer = FengGUI.createContainer(south);
		buttonContainer.setLayoutData(new RowExLayoutData(true,true));
		buttonContainer.setLayoutManager(new BorderLayout());
	
		Container buttons = FengGUI.createContainer(buttonContainer);
		buttons.setLayoutManager(new RowLayout());
		buttons.setLayoutData(BorderLayoutData.EAST);

		Button acceptButton =FengGUI.createButton(buttons,StringConstants.get("MAIN_MENU.OK" , "OK"));
		acceptButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				attemptSelectFile(fileName.getText());
				e.setUsed();
				fileSelected();
			}
			
		});
		
		Button cancelButton = FengGUI.createButton(buttons,StringConstants.get("MAIN_MENU.CANCEL" , "Cancel"));
		
		cancelButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				e.setUsed();
				cancel();
			}
			
		});
		
		backButton = FengGUI.createButton(topRow);
		backButton.setLayoutData(new RowExLayoutData(true,false));

		backButton.setText("Back");
		
		
		upButton = FengGUI.createButton(topRow);
		upButton.setLayoutData(new RowExLayoutData(true,false));

		

		upButton.setText("Up");
		
		
		backButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				back();
				
			}
			
		});
		
		upButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				File file;
				if ((file = getCurrentDirectory()) != null)
				{
					
					File parent = file.getParentFile();
					if (parent != null)
						setCurrentDirectory(parent);
				
				}
				
			}
			
		});
		


		scrollContainer= FengGUI.createScrollContainer(internalContainer);   
		
		scrollContainer.setLayoutData(BorderLayoutData.CENTER);
	//	internalContainer.addWidget(scrollContainer);
		
		fileList = FengGUI.<FileItem>createList() ;//new List<FileItem>() ;

		//scrollContainer.addWidget(fileList);
		scrollContainer.setInnerWidget(fileList);
	    ;
	    
	    fileList.setTraversable(true);
	    fileList.setToggle(false);
	    
	  
	  
	    IKeyListener fileKeyListener = new KeyAdapter()
        {
			
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				if (keyPressedEvent.getKeyClass() == Key.ENTER)
				{
					keyPressedEvent.setUsed();
					ListItem<FileItem> listItem =  fileList.getSelectedItem();
					if (listItem != null)
					{
						if (listItem.getValue().getFile().isDirectory())
						{
							//move to that folder
							setCurrentDirectory(listItem.getValue().getFile());
							
						}else
						{
							setSelectedFile  (listItem.getValue().getFile(),true);
							
							fileSelected();
						}
					}
					
				
				}else if (keyPressedEvent.getKeyClass() == Key.ESCAPE)
				{
					keyPressedEvent.setUsed();
					cancel();
				}else {
					char letter = String.valueOf(keyPressedEvent.getKey()).toLowerCase().charAt(0);
					char uLetter = String.valueOf(keyPressedEvent.getKey()).toUpperCase().charAt(0);
					
					boolean currentLetter = false;
					
					int selectedIndex = -1;
					if(fileList.getSelectedItem()!=null)
					{
						currentLetter = ( fileList.getSelectedItem().getText().charAt(0) == letter) || ( fileList.getSelectedItem().getText().charAt(0) == uLetter);
					
						selectedIndex = fileList.getItem(fileList.getSelectedItem().getValue());
						
						if(fileList.getItems().size()<= selectedIndex + 1)
							currentLetter = false;
						else if  ((fileList.getItem(selectedIndex+1).getText().charAt(0)!=letter) && (fileList.getItem(selectedIndex+1).getText().charAt(0)!=uLetter))
						{//this test doesnt work because the file order might be incorrect
							currentLetter = false;
						}
					//if this is the last item with that letter, then current letter = false
					}
					
					
					boolean selectionOccured = false;
					//scroll to the next item with that letter, if it exists
					ArrayList<ListItem<FileItem>> files = fileList.getItems();
					for(int i = selectedIndex+1;i<files.size();i++)
					{
						ListItem<FileItem> listItem = files.get(i);
						
						if((listItem.getText().charAt(0) == letter) || (listItem.getText().charAt(0) == uLetter))
						{
							//if((!currentLetter) || (i>selectedIndex) ){
								selectionOccured = true;
								fileList.setSelectedIndex(i);
								ensureShowed(scrollContainer,(((double)fileList.getHeight())/((double)fileList.getItems().size()-1.0)) * ((double) i));
								//listItem.setSelected(true);
								break;
							//}
						}
					}
					if(!selectionOccured)
					{
						for(int i = 0;i<selectedIndex;i++)
						{
							ListItem<FileItem> listItem = files.get(i);
							
							if((listItem.getText().charAt(0) == letter) || (listItem.getText().charAt(0) == uLetter))
							{
								//if((!currentLetter) || (i>selectedIndex) ){
									fileList.setSelectedIndex(i);
									ensureShowed(scrollContainer,(((double)fileList.getHeight())/((double)fileList.getItems().size() - 1.0)) * ((double) i));
									//listItem.setSelected(true);
									break;
								//}
							}
						}
					}
				}
			}

			private void ensureShowed(ScrollContainer scrollContainer, double height) {
				double totalHeight = scrollContainer.getInnerWidget().getSize().getHeight();
				double onscreenPos = ( scrollContainer.getInnerWidget().getPosition().getY()) + (totalHeight - height) ;
				//double higher = (-totalHeight + scrollContainer.getInnerWidget().getPosition().getY()) + height;
				if(onscreenPos < 10 || onscreenPos > scrollContainer.getHeight())//doesnt account for horizontal scroll bars height
				{
					
					
					scrollContainer.getVerticalScrollBar().getSlider().setValue(1.0 - height/totalHeight);
				}
				
				
			}
        };
	    
        fileList.addKeyListener(fileKeyListener);
        scrollContainer.getVerticalScrollBar().getSlider().addKeyListener(fileKeyListener);
	   fileList.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener()
	   {

			
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
				
				ListItem<FileItem> listItem =  fileList.getSelectedItem();
				selectionChangedEvent.setUsed();
				if (listItem != null)
				{
					if (listItem.getValue().getFile().isFile())
						setSelectedFile  (listItem.getValue().getFile(),true);
				}
				
				
				
			}
		   
	   });
	    
	   fileList.addMouseListener(new MouseAdapter()
	   {

		
		public void mouseDoubleClicked(
				MouseDoubleClickedEvent mouseDoubleClickedEvent) {
			mouseDoubleClickedEvent.setUsed();
			ListItem<FileItem> listItem =  fileList.getSelectedItem();
			if (listItem != null)
			{
				if (listItem.getValue().getFile().isDirectory())
				{
					//move to that folder
					setCurrentDirectory(listItem.getValue().getFile());
					
				}else
				{
					setSelectedFile  (listItem.getValue().getFile(),true);
					
					fileSelected();
				}
			}
		}
		   
	   });
	   
		internalContainer.setMinSize(this.getMinContentSize());
		//internalContainer.setSizeToMinSize();
		
		Dimension minContent=  buttonContainer.getMinContentSize();
		buttonContainer.setMinSize(minContent);
	    internalContainer.layout();
	    south.layout();
		buttonContainer.layout();
		 buttons.layout();
		filterContainer.layout();
		 
	    topRow.layout();
	    


	}
	public void initComponents()
	{	
		setRootFiles();
		
		FileFilter filter = generateFileFilter(StringConstants.get("FILEMENU.FILTER_ALL" , "All Files"), "*");
		addFileFilter(filter, StringConstants.get("FILEMENU.FILTER_ALL" , "All Files") );
		
		updateFilterList();
		setCurrentFilter(filter);
		
	}
	
	

	/**
	 * Attempt to select the given file, if it exists
	 * @param fileName
	 */
	private void attemptSelectFile(String fileName)
	{

			File file = new File(getCurrentDirectory().getPath() + "/" + fileName);

			
				setSelectedFile(file);
			

	}
	
	/**
	 * Set the currently selected file.
	 * @param file
	 */
	public void setSelectedFile(File file)
	{
		setSelectedFile(file,false);
	}
	
	/**
	 * Set the currently selected file list, but do not update the file list's display.
	 * @param file
	 * @param ignoreFileList
	 */
	private void setSelectedFile(File file, boolean ignoreFileList)
	{
		selectedFile = file;
		if (file == null)
		{
			ListItem<FileItem> selectedItem = fileList.getSelectedItem();
			if (!ignoreFileList && selectedItem!= null)
				selectedItem.setSelected(false);
			fileName.setText("");
		}else
		{
			if (!ignoreFileList)
			{
				if(file.getParentFile() != null && file.getParentFile().exists() &! this.getCurrentDirectory().equals(file.getParentFile()))
    	 			setCurrentDirectory(file.getParentFile());
				
				
				for (int i = 0; i < fileList.size();i++)
				{
					ListItem<FileItem> item = fileList.getItem(i);
					if (item.getValue().getFile().equals(file))
					{
						fileList.setSelectedIndex(i);
						break;
					}
				}
			}
			fileName.setText(file.getName());
		}
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	/**
	 * Get the currently displayed directory.
	 * @return
	 */
	public File getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * Get the list of file filters displayed in the bottom of the dialog. 
	 * Changes to this list will be reflected in changes to that drop down 
	 * box, once updateFilterList() is called.
	 * @return
	 */
	public Set<FileFilter> getFileFilters() {
		return fileFilters.keySet();
	}

	/**
	 * Add a file filter with the given pattern (ie, *, *.*, or *.txt) to the list of filters.
	 * @param description
	 * @param pattern
	 */
	public FileFilter addFileFilter(String description, String pattern)
	{
		FileFilter filter = FileChooserDialog.generateFileFilter(description,pattern);
        addFileFilter(filter,description );
        return filter;
	}

	/**
	 * Add a file filter to the list of file filters displayed at the bottom of the screen..
	 * @param filter
	 * @param description
	 */
	public void addFileFilter(FileFilter filter, String description)
	{
		this.fileFilters.put(filter, description);
		updateFilterList();
	}
	
	/**
	 * Remove a file filter from the list of displayed filters.
	 * @param filter
	 */
	public void removeFileFilter(FileFilter filter)
	{
		this.fileFilters.remove(filter);
		updateFilterList();
	}
	
	/**
	 * Clear the list of displayed file filters.
	 */
	public void clearFileFilters()
	{
		this.fileFilters.clear();
		updateFilterList();
	}

	/**
	 * Updates the file filters displayed in the comboBox. 
	 * Called automatically by addFileFilter and removeFileFilter.
	 */
	public void updateFilterList()
	{

		filterList.getList().clear();
		for (FileFilter filter:fileFilters.keySet())
			filterList.addItem(new ListItem<FileFilter>(fileFilters.get(filter),filter));
	}

	/**
	 * Get the list of currently attached file listeners.
	 * @return
	 */
	public ArrayList<FileDialogListener> getFileDialogListeners() {
		return fileDialogListeners;
	}
	
	/**
	 * Attach a listener to receive events when a file is selected, or the dialog is cancelled.
	 * @return
	 */
	public void addListener(FileDialogListener listener)
	{
		this.fileDialogListeners.add(listener);
	}
	
	/**
	 * Remove a File Dialog Listener.
	 * @param listener
	 */
	public void removeListener(FileDialogListener listener)
	{
		this.fileDialogListeners.remove(listener);
	}
	
	/**
	 * This interface is main way to find out when the user has selected a file, or cancelled the dialog.
	 * If this dialog is not attached to a FileDialogWindow, then steps should be taken on recieving
	 * an event to hide this dialog (the FileDialogWindow will do this itself).
	 * @author Sam Bayless
	 *
	 */
	public interface FileDialogListener
	{
		public void fileSelected(File file);
		public void cancel();
	}

	
	public void paintContent(Graphics g, IOpenGL gl) {
		//Inject code into the GL thread here
		if (glThread == null)
			glThread = Thread.currentThread();
		
        Runnable task = glQueue.poll();
        do {
            if (task == null) break;
            task.run();
           
        } while (((task = glQueue.poll()) != null));
		
		super.paintContent(g, gl);
	}

	/**
	 * Get the currently used file filter.
	 * @return
	 */
	public FileFilter getCurrentFilter() {
		return currentFilter;
	}

	/**
	 * Set the currently selected file filter.
	 * @param currentFilter
	 */
	public void setCurrentFilter(FileFilter currentFilter) {
		
		if (this.currentFilter != currentFilter)
		{
			try {
				cancelLoading = true;
					if(directoryUpdateSemaphore.tryAcquire(DIRECTORY_TIMEOUT, TimeUnit.MILLISECONDS))
					{
						this.currentFilter = currentFilter;
						refreshFiles();
					}	else
					{
						System.out.println("Directory filter timed out");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Generates a FileFilter for the given pattern.
	 * @param description
	 * @param pattern
	 * @return
	 */
	public static FileFilter generateFileFilter(String description, String pattern)
	{
		//Wrap the javax.swing.filechooser.FileNameExtensionFilter in java.io.FileFilter interface
		
		//final FileNameExtensionFilter extFilter =  new FileNameExtensionFilter(description, pattern );
		//have to alter the pattern to turn it into a regex
		
		pattern = pattern.replace(".", "\\.");
		pattern = pattern.replace("*", ".*");
		Pattern filter = Pattern.compile(pattern);
		return new FilePatternFilter(filter);
	}
	
	/**
	 * This is the comparator used to put files, folders, etc in order in the file list
	 */
	private Comparator<FileItem> fileComparator = new Comparator<FileItem>()
	{

		
		public int compare(FileItem item1, FileItem item2) {
				File file1 = item1.getFile();
				File file2 = item2.getFile();
				  if(file1 == file2)
					    return 0;
				  
				  if (file1 == null)
				  	return 1;
				  if (file2 == null)
					  	return -1;
				  
				  if(file1.isDirectory() && file2.isFile())
				    return -1;
				  if(file1.isFile() && file2.isDirectory())
				    return 1;

				  return Collator.getInstance().compare(file1.getName(), file2.getName());
			
		}
		
	};
}

class FilePatternFilter implements FileFilter
{
	private Pattern pattern;

	public FilePatternFilter(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	
	public boolean accept(File pathname) {
        if (pathname != null) {
            if (pathname.isDirectory()) {
                return true;
            }
            
            if (pattern.matcher(pathname.getName()).matches())
            {
            	return true;
            }
        }
		return false;
	}
	
}

class FileItem implements Comparable<FileItem>
{
	private String name;
	
	public int compareTo(FileItem o) 
	{
		//directories are always before files
		if (o.getFile().isDirectory() && !getFile().isDirectory() )
			return 1;
		else if (!o.getFile().isDirectory() && getFile().isDirectory() )
			return -1;
		
		return getFile().getName().compareTo(o.getFile().getName());
	}

	File file;

	public FileItem(File file) {
		super();
		name = FileSystemView.getFileSystemView().getSystemDisplayName(file);
		
		if (name.length() == 0)
			name = file.toString();
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	
	public String toString() {

		return name;
	}
	


	
}
