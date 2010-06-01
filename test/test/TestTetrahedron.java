package test;



import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.buoyancy.util.Tetrahedron;

/**
 * A simple test of the Tetrahedron class
 * 
 */
public class TestTetrahedron  extends SimpleGame {
	private Tetrahedron t;
    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0).normalizeLocal();
    
    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
    	TestTetrahedron app = new TestTetrahedron();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {
    	  if (timer.getTimePerFrame() < 1) {
              angle = angle + (timer.getTimePerFrame() * 1);
              if (angle > 360) {
                  angle = 0;
              }
          }

          rotQuat.fromAngleNormalAxis(angle, axis);
          t.setLocalRotation(rotQuat);

    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Tetrahedron Test");

        t = new Tetrahedron("Tetrahedron", new Vector3f(1,0,0), new Vector3f(0,1,0), new Vector3f(0,0,1), new Vector3f(1,1,1),true);
       t.setModelBound(new BoundingBox());
       t.updateModelBound();
        
        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_NONE);
        rootNode.setRenderState(cs);
        
        input = new FirstPersonHandler(cam, 10f, 1f);
        
        rootNode.attachChild(t);
       
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(TestTetrahedron.class
                .getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR));
        ts.getTexture().setWrap(Texture.WM_WRAP_S_WRAP_T);
        rootNode.setRenderState(ts);

        lightState.setTwoSidedLighting(true);
    }

}
