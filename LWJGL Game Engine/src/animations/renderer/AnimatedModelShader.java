package animations.renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import shaders.ShaderProgram;
import utils.Maths;

public class AnimatedModelShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50; // Max number of joints in a skeleton

	private static final String VERTEX_SHADER = "/animations/renderer/animatedEntityVertex.glsl";
	private static final String FRAGMENT_SHADER = "/animations/renderer/animatedEntityFragment.glsl";

	private int locationProjectionViewMatrix;
	private int locationLightDirection;
	private int[] locationJointTransforms;
	private int locationDiffuseMap;

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		connectTextureUnits();
		
	}
	
	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "jointIndices");
		super.bindAttribute(4, "weights");
		
	}
	
	@Override
	protected void getAllUniformLocations() {

		locationProjectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
		locationLightDirection = super.getUniformLocation("lightDirection");
		
		locationJointTransforms = new int[MAX_JOINTS];
		
		for(int i = 0; i < MAX_JOINTS; i++){
			
			locationJointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
			
		}
		
		locationDiffuseMap = super.getUniformLocation("diffuseMap");
		
	}

	public void loadJointTransforms(Matrix4f[] transforms){
		
		for(int i = 0; i < transforms.length; i++){
			
			super.loadMatrix(locationJointTransforms[i], transforms[i]);
			
		}
		
	}
	
	public void loadProjectionViewMatrix(Matrix4f projectionMatrix, Camera cam){
		
		super.loadMatrix(locationProjectionViewMatrix, Matrix4f.mul(projectionMatrix, Maths.createViewMatrix(cam), null));
		
	}
	
	public void loadLightDirection(Vector3f lightDirection){
		
		super.loadVector(locationLightDirection, lightDirection);
		
	}
	
	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	public void connectTextureUnits() {

		super.loadInt(locationDiffuseMap, 0);
		
	}

}
