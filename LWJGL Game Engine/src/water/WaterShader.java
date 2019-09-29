package water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;
import utils.Maths;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "/water/waterFragment.txt";

	private int locationModelMatrix;
	private int locationViewMatrix;
	private int locationProjectionMatrix;
	private int locationReflectionTexture;
	private int locationRefractionTexture;
	private int locationDudvMap;
	private int locationMoveFactor;
	private int locationCameraPosition;
	private int locationNormalMap;
	private int locationLightColor;
	private int locationLightPosition;
	private int locationDepthMap;
	private int locationSkyColor;

	public WaterShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
		
	}

	@Override
	protected void bindAttributes() {
		
		bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		locationModelMatrix = super.getUniformLocation("modelMatrix");
		
		locationReflectionTexture = super.getUniformLocation("reflectionTexture");
		locationRefractionTexture = super.getUniformLocation("refractionTexture");
		locationDudvMap = super.getUniformLocation("dudvMap");
		locationNormalMap = super.getUniformLocation("normalMap");
		locationMoveFactor = super.getUniformLocation("moveFactor");
		
		locationCameraPosition = super.getUniformLocation("cameraPosition");
		
		locationLightColor = super.getUniformLocation("lightColor");
		locationLightPosition = super.getUniformLocation("lightPosition");
		
		locationDepthMap = super.getUniformLocation("depthMap");
		
		locationSkyColor = super.getUniformLocation("skyColor");
		
	}

	public void loadProjectionMatrix(Matrix4f projection) {
	
		loadMatrix(locationProjectionMatrix, projection);
	
	}
	
	public void loadViewMatrix(Camera camera){
	
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(locationViewMatrix, viewMatrix);
		
		super.loadVector(locationCameraPosition, camera.getPosition());
	
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
	
		loadMatrix(locationModelMatrix, modelMatrix);
	
	}

	public void connectTextureUnits(){
		
		super.loadInt(locationReflectionTexture, 0);
		super.loadInt(locationRefractionTexture, 1);
		super.loadInt(locationDudvMap, 2);
		super.loadInt(locationNormalMap, 3);
		super.loadInt(locationDepthMap, 4);
		
	}
	
	public void loadMoveFactor(float factor){
		
		super.loadFloat(locationMoveFactor, factor);
		
	}
	
	public void loadLight(Light sun){
		
		super.loadVector(locationLightColor, sun.getColor());
		super.loadVector(locationLightPosition, sun.getPosition());
		
	}
	
	public void loadSkyColor(float r, float g, float b){
		
		super.loadVector(locationSkyColor, new Vector3f(r, g, b));
		
	}
	
}
