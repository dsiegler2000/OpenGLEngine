package renderengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animations.animatedmodel.AnimatedModel;
import animations.renderer.AnimatedModelRenderer;
import animations.renderer.AnimatedModelShader;
import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import entities.EntityShader;
import entities.Light;
import models.TexturedModel;
import normalmapping.normalmappingrenderer.NormalMappingRenderer;
import scene.Scene;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainShader;

public class MasterRenderer {

	public static final float RED = 0.41f;
	public static final float GREEN = 0.43f;
	public static final float BLUE = 0.46f;

	private Matrix4f projectionMatrix;

	private EntityShader shader;
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	
	private Map<TexturedModel, List<Entity>> entities;
	private Map<TexturedModel, List<Entity>> normalMapEntities;
	private List<AnimatedModel> animatedModels;
	private List<Terrain> terrains;

	private NormalMappingRenderer normalMapRenderer;

	private SkyboxRenderer skyboxRenderer;

	private ShadowMapMasterRenderer shadowMapRenderer;
	
	private AnimatedModelRenderer animatedModelRenderer;
	private AnimatedModelShader animatedModelShader;

	public MasterRenderer(Loader loader, Camera cam) {

		entities = new HashMap<>();
		normalMapEntities = new HashMap<>();
		animatedModels = new ArrayList<>();
		terrains = new ArrayList<>();
		
		enableCulling();

		createProjectionMatrix();

		shader = new EntityShader();
		renderer = new EntityRenderer(shader, projectionMatrix);

		terrainShader = new TerrainShader();
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);

		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);

		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);

		shadowMapRenderer = new ShadowMapMasterRenderer(cam);
		
		animatedModelShader = new AnimatedModelShader();
		animatedModelRenderer = new AnimatedModelRenderer(animatedModelShader, projectionMatrix);

	}

	public static void enableCulling() {

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

	}

	public static void disableCulling() {

		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	public void renderScene(Scene scene, Vector4f clipPlane) {

		for (Terrain terrain : scene.getTerrains()) {

			processTerrain(terrain);

		}

		for (Entity entity : scene.getEntities()) {

			processEntity(entity);

		}

		for (Entity entity : scene.getNormalMappedEntities()) {

			processNormalMapEntity(entity);

		}
		
		for(AnimatedModel animatedModel : scene.getAnimatedModels()){
			
			processAnimatedModel(animatedModel);
			
		}

		render(scene.getLights(), scene.getCamera(), clipPlane);

	}

	private void render(List<Light> lights, Camera camera, Vector4f clipPlane) {

		prepare();

		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);

		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());

		shader.stop();

		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
		
		animatedModelRenderer.render(animatedModels.get(0), camera,  new Vector3f(0.2f, -0.3f, -0.8f));

		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);

		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

		terrainShader.stop();

		skyboxRenderer.render(camera, RED, GREEN, BLUE);

		entities.clear();
		terrains.clear();
		normalMapEntities.clear();
		animatedModels.clear();

	}

	public void processTerrain(Terrain terrain) {

		terrains.add(terrain);

	}

	public void processEntity(Entity entity) {

		TexturedModel entityModel = entity.getModel();

		List<Entity> batch = entities.get(entityModel);

		if (batch != null) {

			batch.add(entity);

		}

		else {

			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);

		}

	}

	public void processNormalMapEntity(Entity entity) {

		TexturedModel entityModel = entity.getModel();

		List<Entity> batch = normalMapEntities.get(entityModel);

		if (batch != null) {

			batch.add(entity);

		}

		else {

			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);

		}

	}
	
	private void processAnimatedModel(AnimatedModel model){
		
		animatedModels.add(model);
		
	}

	public void prepare() {

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());

	}

	public void renderShadowMap(List<Entity> entityList, Light sun) {

		for (Entity entity : entityList) {

			processEntity(entity);

		}

		shadowMapRenderer.render(entities, sun);
		entities.clear();

	}

	public int getShadowMapTexture() {

		return shadowMapRenderer.getShadowMap();

	}

	public void cleanUp() {

		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();

	}

	private void createProjectionMatrix() {

		projectionMatrix = new Matrix4f();

		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(Camera.FOV / 2f))));
		float xScale = yScale / aspectRatio;
		float frustum_length = Camera.FAR_PLANE - Camera.NEAR_PLANE;

		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((Camera.FAR_PLANE + Camera.NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * Camera.NEAR_PLANE * Camera.FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;

	}

	public Matrix4f getProjectionMatrix() {

		return projectionMatrix;

	}

}
