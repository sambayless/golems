package test;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.FirstPersonHandler;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Dome;
import com.jme.scene.state.CullState;

public class DomeLightingTest extends SimpleGame{
	

    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0).normalizeLocal();
    private Dome t;
    private Capsule c;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
    	DomeLightingTest app = new DomeLightingTest();
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
        c.setLocalRotation(rotQuat);
    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Dome Lighting Test");

        t =new Dome("Dome", new Vector3f(), 30, 30, 0.5f,false);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();
        
        c = new Capsule("Capsule", 40, 32, 16, 0.5f, 1);
        c.setModelBound(new BoundingBox());
        c.updateModelBound();
        
        c.getLocalTranslation().x -= 2;
        
        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(cs);
        
        input = new FirstPersonHandler(cam, 10f, 1f);
        
        rootNode.attachChild(t);
        rootNode.attachChild(c);
        
        rootNode.updateRenderState();
        c.updateRenderState();
        t.updateRenderState();
        
        lightState.setTwoSidedLighting(false);
    }

}
