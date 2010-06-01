package com.golemgame.states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;

import com.golemgame.audio.AudioManager;
import com.golemgame.audio.dummy.DummyAudioManager;
import com.golemgame.mechanical.MachineSpace;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.ParentModel;
import com.golemgame.model.quality.QualityManager;
import com.golemgame.mvc.golems.ImageInterpreter.ImageCompression;
import com.golemgame.states.camera.CameraManager;
import com.golemgame.states.construct.ConstructionManager;
import com.golemgame.states.machine.MachineSpaceManager;
import com.golemgame.states.record.RecordingManager;
import com.golemgame.tool.ToolManager;
import com.golemgame.tool.ToolPool;
import com.golemgame.tool.selection.SelectionToolManager;
import com.golemgame.views.ObservableViewManager;
import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.system.DisplaySystem;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameState;

public class StateManager {

	private static AudioManager audioManager = new DummyAudioManager();

	private static CameraManager cameraManager = null;

	private static GameState currentGameState = null;
	
	private static ConstructionManager constructionManager = null;

	private static DesignState designState = null;
	

	private static Node currentRootNode = null;
	
	private static Lock functionalLock = new ReentrantLock();

	private static OdePhysicsState functionalState = null;

	private static StandardGame game = null;

	public static final String GAME_NAME = "Golems";
	private static FileFilter ioFilter = null;
	public static final boolean IS_AWT_MOUSE = false;

	private static Logger logger;
	public static final String LOGGER_NAME = "com.golemgame";

	private final static MachineSpaceManager machineManager = new MachineSpaceManager();

	private static MachineSpace machineSpace = null;

	// private static FileNameExtensionFilter machineFilter=null;

	private static QualityManager qualityManager;

	private static RecordingManager recordingManager ;

	public static void setRecordingManager(RecordingManager recordingManager) {
		StateManager.recordingManager = recordingManager;
	}

	private static ParentModel rootModel = null;

	public static SelectionToolManager selectionManager = new SelectionToolManager();

	public static final boolean SOUND_ENABLED = false;
	public static StructuralMachine structuralMachine;
	private static ExecutorService threadPool = new ThreadPoolExecutor(2, Integer.MAX_VALUE, 10, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	public static final int thumbnailHeight = 256;

	public static final int thumbnailWidth = 256;

	public static ToolManager toolManager;

	private static ToolPool toolPool;

	public static final boolean useVBO = false;

	private static ObservableViewManager viewManager = new ObservableViewManager();

	public static BufferedImage captureScreenRender() {
		int size = 256;
		Renderer r = DisplaySystem.getDisplaySystem().getRenderer();
		TextureRenderer tRenderer = DisplaySystem.getDisplaySystem().createTextureRenderer(size, size, TextureRenderer.RENDER_TEXTURE_2D);

		// set the texture renderers camera to match the worlds camera
		tRenderer.getCamera().setLocation(r.getCamera().getLocation());
		tRenderer.getCamera().setAxes(r.getCamera().getLeft(), r.getCamera().getUp(), r.getCamera().getDirection());

		tRenderer.getCamera().update();

		Texture texture = new Texture();
		texture.setMipmapState(Texture.MM_LINEAR);
		texture.setFilter(Texture.FM_NEAREST);

		tRenderer.setupTexture(texture);
		// tRenderer.render(StateManager.getMachineSpace().getSpatial(),texture);
		if (StateManager.getCameraManager().getSkyBoxManager().isEnabled()) {
			boolean overlayenabled = StateManager.getCameraManager().getSkyBoxManager().isOverlayEnabled();
			StateManager.getCameraManager().getSkyBoxManager().setTextureRenderMode(true);
			StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(false);
			// StateManager.getCameraManager().getSkyBoxManager().
			// something magical has to happen after the sky box has its texture
			// reset before the render will succeed.
			// StateManager.getCameraManager().getSkyBoxManager().getSkyBoxSpatial().updateGeometricState(0,
			// true);

			// StateManager.getCameraManager().getCameraLocationNode().updateRenderState();
			// StateManager.getDesignState().getRootNode().updateRenderState();

			// StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(true);
			// StateManager.getDesignState().getRootNode().updateRenderState();
			// tRenderer.render(StateManager.getCameraManager().getSkyBoxManager().getSkyBoxSpatial(),texture);
			tRenderer.render(StateManager.getDesignState().getRootNode(), texture, true);
			StateManager.getCameraManager().getSkyBoxManager().setTextureRenderMode(false);
			StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(overlayenabled);
		}

		IntBuffer buff = ByteBuffer.allocateDirect(tRenderer.getWidth() * tRenderer.getHeight() * 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		LWJGLTextureState.doTextureBind(texture.getTextureId(), 0);

		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);

		int[] data = new int[size * size];

		buff.get(data);
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_BGR);// bgr
																						// is
																						// important
																						// -
																						// thats
																						// the
																						// order
																						// that
																						// matches
																						// the
																						// gl
		img.getRaster().setDataElements(0, 0, size, size, data);

		BufferedImage img2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = img2.createGraphics();
		g.drawImage(img, 0, size, size, 0, 0, 0, size, size, null);
		return img2;
	}

	public static void exit() {
		getGame().shutdown();
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	public static CameraManager getCameraManager() {
		return cameraManager;
	}

	public static GameState getCurrentGameState() {
		return currentGameState;
	}

	public static Node getCurrentRootNode() {
		return currentRootNode;
	}

	public static DesignState getDesignState() {
		return designState;
	}

	public static OdePhysicsState getFunctionalState() {
		return functionalState;
	}

	public static StandardGame getGame() {
		return game;
	}

	public static synchronized FileFilter getIOMachineFilter() {
		if (ioFilter == null) {
			ioFilter = new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.getPath().endsWith(".mchn");
				}

			};
		}
		return ioFilter;

	}

	public static Logger getLogger() {
		return logger;
	}

	public static MachineSpaceManager getMachineManager() {
		return machineManager;
	}

	public static MachineSpace getMachineSpace() {
		return machineSpace;
	}

	public static QualityManager getQualityManager() {
		return qualityManager;
	}

	public static RecordingManager getRecordingManager() {
		return recordingManager;
	}

	public static ParentModel getRootModel() {
		return rootModel;
	}

	public static BufferedImage getScreenShot(int left, int top, int width, int height) {
		// Create a pointer to the image info and create a buffered image to
		// hold it.
		IntBuffer buff = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer(); // little
																														// endian
																														// or
																														// native?
		DisplaySystem.getDisplaySystem().getRenderer().grabScreenContents(buff, left, top, width, height);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Grab each pixel information and set it to the BufferedImage info.
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				img.setRGB(x, y, buff.get((height - y - 1) * width + x));
			}
		}
		return img;
	}

	public static SelectionToolManager getSelectionManager() {
		return selectionManager;
	}

	private static String getStackTraceString(Throwable e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		sw.write(e.toString() + "\n");

		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static StructuralMachine getStructuralMachine() {
		return structuralMachine;
	}

	public static ExecutorService getThreadPool() {
		return threadPool;
	}

	public static ToolManager getToolManager() {
		return toolManager;
	}

	public static ToolPool getToolPool() {
		return toolPool;
	}

	public static ObservableViewManager getViewManager() {
		return viewManager;
	}

	public static URL loadResource(String path) {
		// try this with and without leading slash...
		if (path.startsWith("/")) {
			URL url = StateManager.class.getClassLoader().getResource(path);
			if (url == null) {
				url = StateManager.class.getClassLoader().getResource(path.substring(1));
			}
			return url;
		} else {
			URL url = StateManager.class.getClassLoader().getResource(path);
			if (url == null) {
				url = StateManager.class.getClassLoader().getResource("/" + path);
			}
			return url;
		}

	}

	public static void lockFunctional() {
		// TODO Auto-generated method stub
		functionalLock.lock();
	}

	public static void logError(Throwable e) {
		if (e == null) {
			StateManager.getLogger().log(Level.SEVERE, "Null Error");
		} else
			StateManager.getLogger().log(Level.SEVERE, getStackTraceString(e));

	}

	public static void setAudioManager(AudioManager audioManager) {
		StateManager.audioManager = audioManager;
	}

	public static void setCameraManager(CameraManager cameraManager) {
		StateManager.cameraManager = cameraManager;
	}

	public static void setCurrentGameState(GameState currentGameState) {
		// replace this method.
		StateManager.currentGameState = currentGameState;
		// ArrayList test = GameStateManager.getInstance().getChildren();

		// GameStateManager.getInstance().attachChild(currentGameState);

		if (StateManager.getCurrentGameState() instanceof Controllable)
			setCurrentRootNode(((Controllable) StateManager.getCurrentGameState()).getRootNode());

	}

	public static void setCurrentRootNode(Node node) {
		if (node == null)
		{
			currentRootNode = new Node();
		}
		else
			currentRootNode = node;
	}

	public static void setDesignState(DesignState designState) {
		StateManager.designState = designState;
	}

	public static void setFunctionalState(OdePhysicsState functionalState) {
		functionalLock.lock();
		try {
			StateManager.functionalState = functionalState;
		} finally {
			functionalLock.unlock();
		}
	}

	public static void setGame(StandardGame game) {
		StateManager.game = game;
	}

	public static void setLogger(Logger logger) {
		StateManager.logger = logger;
	}

	public static void setMachineScreenshot() {
		BufferedImage image = null;
		try {
			image = (BufferedImage) StateManager.getGame().executeInGL(new Callable<Object>() {

				public Object call() throws Exception {

					return captureScreenRender();
				}

			});
		} catch (Exception e1) {

			StateManager.logError(e1);
		}
		// choose jpg or png intelligently
		StateManager.getMachineSpace().getImageView().setImage(image, ImageCompression.CHOOSE);

	}

	public static void setMachineSpace(MachineSpace machineSpace) {
		StateManager.machineSpace = machineSpace;
	}

	public static void setQualityManager(QualityManager qualityManager) {
		StateManager.qualityManager = qualityManager;
	}

	public static void setRootModel(ParentModel rootModel) {
		StateManager.rootModel = rootModel;
	}

	public static void setStructuralMachine(StructuralMachine structuralMachine) {
		StateManager.structuralMachine = structuralMachine;
		getViewManager().refreshViews();
	}


	public static ConstructionManager getConstructionManager() {
		return constructionManager;
	}

	public static void setConstructionManager(ConstructionManager constructionManager) {
		StateManager.constructionManager = constructionManager;
	}
	
	public static void setToolManager(ToolManager toolManager) {
		StateManager.toolManager = toolManager;
	}

	public static void setToolPool(ToolPool toolPool) {
		StateManager.toolPool = toolPool;
	}

	public static void unlockFunctional() {
		// TODO Auto-generated method stub
		functionalLock.unlock();
	}

	private StateManager() {

	}
}
