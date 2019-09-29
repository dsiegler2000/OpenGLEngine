package normalmapping.normalmappingrenderer;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Light;
import shaders.ShaderProgram;

public class NormalMappingShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "/normalmapping/normalMappingRenderer/normalMapVShader.txt";
	private static final String FRAGMENT_FILE = "/normalmapping/normalMappingRenderer/normalMapFShader.txt";

	private int locationTransformationMatrix;
	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int locationLightPositionEyeSpace[];
	private int locationLightColor[];
	private int locationAttenuation[];
	private int locatioShineDamper;
	private int locationReflectivity;
	private int locationSkyColor;
	private int locationNumberOfRows;
	private int locationOffset;
	private int locationPlane;
	private int locationModelTexture;
	private int locationNormalMap;
	private int locationSpecularMap;
	private int locationUsesSpecularMap;

	public NormalMappingShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
		
	}

	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		
	}

	@Override
	protected void getAllUniformLocations() {

		locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		locatioShineDamper = super.getUniformLocation("shineDamper");
		locationReflectivity = super.getUniformLocation("reflectivity");
		locationSkyColor = super.getUniformLocation("skyColor");
		locationNumberOfRows = super.getUniformLocation("numberOfRows");
		locationOffset = super.getUniformLocation("offset");
		locationPlane = super.getUniformLocation("plane");
		locationModelTexture = super.getUniformLocation("modelTexture");
		locationNormalMap = super.getUniformLocation("normalMap");

		locationLightPositionEyeSpace = new int[MAX_LIGHTS];
		locationLightColor = new int[MAX_LIGHTS];
		locationAttenuation = new int[MAX_LIGHTS];
		
		for (int i = 0; i < MAX_LIGHTS; i++) {
			
			locationLightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[" + i + "]");
			locationLightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		
		}
		
		locationSpecularMap = super.getUniformLocation("specularMap");
		locationUsesSpecularMap = super.getUniformLocation("usesSpecularMap");
	
	}

	protected void connectTextureUnits() {

		super.loadInt(locationModelTexture, 0);
		super.loadInt(locationNormalMap, 1);
		super.loadInt(locationSpecularMap, 2);
		
	}
	
	public void loadUseSpecularMap(boolean useMap){
		
		super.loadBoolean(locationUsesSpecularMap, useMap);
		
	}

	protected void loadClipPlane(Vector4f plane) {

		super.loadVector(locationPlane, plane);
		
	}

	protected void loadNumberOfRows(int numberOfRows) {

		super.loadFloat(locationNumberOfRows, numberOfRows);
		
	}

	protected void loadOffset(float x, float y) {

		super.load2DVector(locationOffset, new Vector2f(x, y));
		
	}

	protected void loadSkyColor(float r, float g, float b) {

		super.loadVector(locationSkyColor, new Vector3f(r, g, b));
	
	}

	protected void loadShineVariables(float damper, float reflectivity) {

		super.loadFloat(locatioShineDamper, damper);
		super.loadFloat(locationReflectivity, reflectivity);
	
	}

	protected void loadTransformationMatrix(Matrix4f matrix) {

		super.loadMatrix(locationTransformationMatrix, matrix);
	
	}

	protected void loadLights(List<Light> lights, Matrix4f viewMatrix) {

		for (int i = 0; i < MAX_LIGHTS; i++) {
			
			if (i < lights.size()) {
				
				super.loadVector(locationLightPositionEyeSpace[i], getEyeSpacePosition(lights.get(i), viewMatrix));
				super.loadVector(locationLightColor[i], lights.get(i).getColor());
				super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
			
			}
			
			else {
				
				super.loadVector(locationLightPositionEyeSpace[i], new Vector3f(0, 0, 0));
				super.loadVector(locationLightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(locationAttenuation[i], new Vector3f(1, 0, 0));
			
			}
		
		}
	
	}

	protected void loadViewMatrix(Matrix4f viewMatrix) {

		super.loadMatrix(locationViewMatrix, viewMatrix);
	
	}

	protected void loadProjectionMatrix(Matrix4f projection) {

		super.loadMatrix(locationProjectionMatrix, projection);
	
	}

	private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix) {

		Vector3f position = light.getPosition();
		Vector4f eyeSpacePos = new Vector4f(position.x, position.y, position.z, 1f);
		Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
		
		return new Vector3f(eyeSpacePos);
	
	}

}
