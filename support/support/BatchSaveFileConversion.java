package support;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.prefs.Preferences;


import com.golemgame.model.quality.QualityManager;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.save.SaveManager;
import com.golemgame.save.SaveManager.SaveContainer;
import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.CameraManager;
import com.golemgame.tool.ToolManager;
import com.golemgame.tool.ToolPool;
import com.jme.system.GameSettings;
import com.jme.system.PreferencesGameSettings;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.StandardGame.GameType;
import com.jmex.physics.PhysicsSpace;

public class BatchSaveFileConversion {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		 
		 StateManager.setLogger( java.util.logging.Logger.getLogger("GolemGame"));
			
		    
			//   
				
		
				 StateManager.getLogger().setUseParentHandlers(false);
				 StateManager.getLogger().addHandler(new ConsoleHandler());
				 StateManager.getLogger().getHandlers()[0].setLevel(Level.FINE);
		
		java.util.logging.Logger.getLogger("com.jme").setLevel(Level.OFF);
		    java.util.logging.Logger.getLogger("com.jmex").setLevel(Level.OFF);
		    java.util.logging.Logger.getLogger( PhysicsSpace.LOGGER_NAME ).setLevel(Level.OFF);
		    StateManager.getLogger().setLevel(Level.OFF);
		
		
		  final  GameSettings settings = new PreferencesGameSettings(Preferences.userRoot().node(StateManager.PREFERENCES_ROOT_NAME + "/SystemPrefs" ));
			settings.setFullscreen(StateManager.getGeneralSettings().getFullscreen().isValue());//load this from settings manager, not settings file.
			 settings.setFramerate(settings.getFrequency());//default framerate.
			 if (settings.getWidth()<640 || settings.getHeight() < 480)
			 {	
				 settings.setWidth(640);
				 settings.setHeight(480);
			 }
	
			 settings.setMusic(false);
			 settings.setSFX(false);
			 
			 final  StandardGame game = new StandardGame("Robot Game",GameType.GRAPHICAL);
			 StateManager.setGame(game);
			 game.start();
			 
			
			  //Force the opengl to load before continuing
			game.delayForUpdate();
			
			GameTaskQueueManager.getManager().update(new Callable<Object>()
					{

					
						public Object call() throws Exception {
							GUILayer.loadInstance();
							
							 StateManager.setCameraManager(new CameraManager());
							
							 StateManager.setToolPool(new ToolPool());
						
							 StateManager.setToolManager(new ToolManager());
							   
							    StateManager.setQualityManager(new QualityManager());
							
							File[] files = new File("C:\\Users\\Sam\\Machines\\old2").listFiles();
							File destinationDirectory = new File("C:\\Users\\Sam\\Machines\\Conversion\\");
							//	File[] files = new File("C:\\Users\\Sam\\Machines\\Conversion\\").listFiles();
							//	File destinationDirectory = new File("C:\\Users\\Sam\\Machines\\Conversion\\Conversion\\");

							 
							 destinationDirectory.mkdirs();
							for (File mFile:files)
						//	for(int i = 0; i<10;i++)
							{
								//mFile = new File("C:\\Users\\Sam\\Machines\\Conversion\\HumanChair.mchn");
							//	File mFile = files[40];
								if(mFile.isDirectory())
									continue;
								try{
									String fileName = mFile.getName();
									long startTime = System.nanoTime();
									File destinationFile = new File( destinationDirectory.getAbsolutePath() + "\\" + fileName) ;
									
									
									SaveContainer saveContainer = SaveManager.getInstance().load(mFile);
									long loadStart = System.nanoTime();
									SaveManager.getInstance().save(destinationFile, saveContainer.getMachineSpace(), saveContainer.getPrimaryMachine());
									long doneTime = System.nanoTime();
									System.out.println(destinationFile + "Load: " + ((loadStart- startTime)/1000000)  + "Save: " + ((doneTime- loadStart)/1000000));
								}catch(Exception e)
								{
									System.out.println(e);
									e.printStackTrace();
								}catch(Error e)
								{
									System.out.println(e);
									e.printStackTrace();
									throw e;
								}
								
								System.gc();
								System.out.println(SpatialModel.countReferencedSpatials());
								//these are being retained!
							}
							StateManager.setGame(null);
							StateManager.setRootModel(null);
							StateManager.setMachineSpace(null);
							StateManager.setStructuralMachine(null);
							StateManager.setCameraManager(null);
							StateManager.setCurrentRootNode(null);
							System.out.println("done");
						
							
							return null;
						}
				
					});
			
			
	}
}
