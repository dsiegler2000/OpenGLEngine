package entities;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import shaders.ShaderProgram;
import utils.Maths;

public class EntityShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "/entities/vertexShader.txt";
	private static final String FRAGMENT_FILE = "/entities/fragmentShader.txt";

	private int locationTransformationMatrix;
	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int[] locationLightPosition;
	private int[] locationLightColor;
	private int[] locationAttenuation;
	private int locationShineDamper;
	private int locationReflectivity;
	private int locationUseFakeLighting;
	private int locationSkyColor;
	private int locationNumberOfRows;
	private int locationOffset;
	private int locationPlane;
	private int locationToShadowMapSpace;
	private int locationShadowMap;
	private int locationSpecularMap;
	private int locationUsesSpecularMap;
	private int locationModelTexture;
	
	public EntityShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);

	}

	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		
	}

	@Override
	protected void getAllUniformLocations() {

		locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		
		locationShineDamper = super.getUniformLocation("shineDamper");
		locationReflectivity = super.getUniformLocation("reflectivity");
		
		locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
		
		locationSkyColor = super.getUniformLocation("skyColor");
		
		locationNumberOfRows = super.getUniformLocation("numberOfRows");
		locationOffset = super.getUniformLocation("offset");
		
		locationLightPosition = new int[MAX_LIGHTS];
		locationLightColor = new int[MAX_LIGHTS];
		locationAttenuation = new int[MAX_LIGHTS];
		
		for(int i = 0; i < MAX_LIGHTS; i++){
			
			locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locationLightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
			
		}
		
		locationPlane = super.getUniformLocation("plane");
		locationToShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		locationShadowMap = super.getUniformLocation("shadowMap");	
		
		locationSpecularMap = super.getUniformLocation("specularMap");
		locationUsesSpecularMap = super.getUniformLocation("usesSpecularMap");
		locationModelTexture = super.getUniformLocation("modelTexture");
		
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		
		super.loadMatrix(locationTransformationMatrix, matrix);
		
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		
		super.loadMatrix(locationProjectionMatrix, projection);
		
	}
	
	public void loadViewMatrix(Camera camera){
		
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		
		super.loadMatrix(locationViewMatrix, viewMatrix);
		
	}
	
	public void loadLights(List<Light> lights){
		
		for(int i = 0; i < MAX_LIGHTS; i++){
			
			if(i < lights.size()){
				
				super.loadVector(locationLightPosition[i], lights.get(i).getPosition());
				super.loadVector(locationLightColor[i], lights.get(i).getColor());
				super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
				
			}
			
			else{
				
				super.loadVector(locationLightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(locationLightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(locationAttenuation[i], new Vector3f(1, 0, 0));
				
			}
			
		}
		
	}
	
	public void loadShineVariables(float damper, float reflectivity){
		
		super.loadFloat(locationShineDamper, damper);
		super.loadFloat(locationReflectivity, reflectivity);
		
	}
	
	public void loadFakeLighting(boolean useFake){
		
		super.loadBoolean(locationUseFakeLighting, useFake);
		
	}
	
	public void loadSkyColor(float r, float g, float b){
		
		super.loadVector(locationSkyColor, new Vector3f(r, g, b));
		
	}
	
	public void loadNumberOfRows(int numRows){
		
		super.loadFloat(locationNumberOfRows, numRows);
		
	}
	
	public void loadOffset(float x, float y){
		
		super.load2DVector(locationOffset, new Vector2f(x, y));
		
	}
	
	public void loadClipPlane(Vector4f plane){
		
		super.loadVector(locationPlane, plane);
		
	}
	
	public void loadToShadowSpaceMatrix(Matrix4f mat){
		
		super.loadMatrix(locationToShadowMapSpace, mat);
		
	}
	
	public void connectTextureUnits(){
		
		super.loadInt(locationShadowMap, 5);
		super.loadInt(locationModelTexture, 0);
		super.loadInt(locationSpecularMap, 1);
		
	}
	
	public void loadUsesSpecularMap(boolean useMap){
		
		super.loadBoolean(locationUsesSpecularMap, useMap);
		
	}

}
