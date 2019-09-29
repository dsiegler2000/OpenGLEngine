package terrains;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;
import utils.Maths;

public class TerrainShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "/terrains/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "/terrains/terrainFragmentShader.txt";

	private int locationTransformationMatrix;
	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int[] locationLightPosition;
	private int[] locationLightColor;
	private int[] locationAttenuation;
	private int locationShineDamper;
	private int locationReflectivity;
	private int locationSkyColor;
	private int locationPlane;
	private int locationToShadowMapSpace;
	private int locationShadowMap;
	private int[] locationMaxHeightOfHeightLayers;
	private int locationHeightTextures;
	private int locationNumLayers;
	
	public TerrainShader() {
		
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
		
		locationSkyColor = super.getUniformLocation("skyColor");
		
		locationLightPosition = new int[MAX_LIGHTS];
		locationLightColor = new int[MAX_LIGHTS];
		locationAttenuation = new int[MAX_LIGHTS];
		
		for(int i = 0; i < MAX_LIGHTS; i++){
			
			locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locationLightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
			
		}
		
		locationMaxHeightOfHeightLayers = new int[15];
		
		for(int i = 0; i < 15; i++){
			
			locationMaxHeightOfHeightLayers[i] = super.getUniformLocation("maxHeightOfLayers[" + i + "]");
			
		}
				
		locationPlane = super.getUniformLocation("plane");
		locationToShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		locationShadowMap = super.getUniformLocation("shadowMap");
		locationHeightTextures = super.getUniformLocation("heightTextures");
		locationNumLayers = super.getUniformLocation("numLayers");
		
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
	
	public void connectTextureUnits(){
		
		super.loadInt(locationHeightTextures, 0);
		super.loadInt(locationShadowMap, 5);
		
	}
	
	public void loadSkyColor(float r, float g, float b){
		
		super.loadVector(locationSkyColor, new Vector3f(r, g, b));
		
	}
	
	public void loadClipPlane(Vector4f clipPlane){
		
		super.loadVector(locationPlane, clipPlane);
		
	}
	
	public void loadToShadowSpaceMatrix(Matrix4f m){
		
		super.loadMatrix(locationToShadowMapSpace, m);
		
	}
	
	public void loadMaxHeightOfLayers(float[] maxHeightOfLayers){
		
		super.loadInt(locationNumLayers, maxHeightOfLayers.length);
						
		for(int i = 0; i < maxHeightOfLayers.length; i++){
			
			super.loadFloat(locationMaxHeightOfHeightLayers[i], maxHeightOfLayers[i]);
			
		}
		
	}

}
