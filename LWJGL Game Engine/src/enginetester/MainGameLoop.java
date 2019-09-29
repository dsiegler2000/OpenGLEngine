package enginetester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animations.animatedmodel.AnimatedModel;
import animations.animation.Animation;
import animations.loaders.AnimatedModelLoader;
import animations.loaders.AnimationLoader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fonts.fontrendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalmapping.normalmappingobjconverter.NormalMappedObjLoader;
import objloader.OBJLoader;
import particles.ParticleMaster;
import postprocessing.PostProcessing;
import renderengine.DisplayManager;
import renderengine.Loader;
import renderengine.MasterRenderer;
import scene.Scene;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.MousePicker;
import utils.openglobjects.Fbo;
import viewmanager.ViewManager;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {
	
	private static double lastTime = Sys.getTime();
	private static int framesSinceLastSecond = 0;
	
	private static Loader loader;
	private static Player player;
	private static Camera camera;
	private static MasterRenderer renderer;
	private static Scene scene;
	private static MousePicker picker;
	
	private static Fbo multisampledFbo;
	private static Fbo outputFbo;
	private static Fbo outputFbo2;
	
	private static WaterFrameBuffers buffers;
	private static WaterShader waterShader;
	private static WaterRenderer waterRenderer;
	
	private static GuiRenderer guiRenderer;

	public static void main(String[] args) {
				
		init();
		
		
		
		float[] maxLayerHeights = {2, 35, 100, 150, 400};
		TerrainTexturePack texturePack = new TerrainTexturePack(new TerrainTexture(loader.loadTextures("grassy3", "grassy3", "grassy3", "grassy3", "grassy3")), maxLayerHeights);

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		
		ModelTexture rockTexture = new ModelTexture(loader.loadTexture("boulder"));
		
		ModelTexture cherryTexture = new ModelTexture(loader.loadTexture("cherry"));
		cherryTexture.setExtraInfoMap(loader.loadTexture("cherryS"));
		
		ModelTexture pineTexture = new ModelTexture(loader.loadTexture("pine"));
		
		TexturedModel fern = new TexturedModel(OBJLoader.loadOBJ("fern", loader), fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);

		TexturedModel lantern = new TexturedModel(OBJLoader.loadOBJ("lantern", loader), new ModelTexture(loader.loadTexture("lantern")));
		lantern.getTexture().setExtraInfoMap(loader.loadTexture("lanternS"));
		
		TexturedModel cherry = new TexturedModel(OBJLoader.loadOBJ("cherry", loader), cherryTexture);
		cherry.getTexture().setHasTransparency(true);
		cherry.getTexture().setReflectivity(0.2f);
		cherry.getTexture().setShineDamper(5);
		cherry.getTexture().setExtraInfoMap(loader.loadTexture("cherryS"));
		
		TexturedModel pine = new TexturedModel(OBJLoader.loadOBJ("pine", loader), pineTexture);
		pine.getTexture().setHasTransparency(true);
		
		TexturedModel rock = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), rockTexture);
		rock.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		rock.getTexture().setShineDamper(5);
		rock.getTexture().setReflectivity(0.2f);

		Terrain terrain = new Terrain(0, 0, loader, texturePack);
		
		scene.addTerrain(terrain);
		
		TexturedModel[] anywhereModels = {fern, fern, fern, fern};
		TexturedModel[] aboveWaterModels = {cherry, cherry, pine, pine, lantern};
		TexturedModel[] randomRotationAndScaleModels = {rock};
		TexturedModel[] none = {};

		scene.addEntities(populateLandscape(0, 0, Terrain.SIZE, Terrain.SIZE, terrain, 7, 1600, anywhereModels, aboveWaterModels, none, 0.3f, 1.5f));
		scene.addNormalMappedEntities(populateLandscape(0, 0, Terrain.SIZE, Terrain.SIZE, terrain, 7, 480, none, none, randomRotationAndScaleModels, 0.2f, 1.7f));

		AnimatedModel animatedModel = AnimatedModelLoader.loadEntity("model", "diffuse");
		Animation animation = AnimationLoader.loadAnimation("model");
		animatedModel.doAnimation(animation);
		scene.addAnimatedModel(animatedModel);
		
		Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
		scene.addLight(sun);

		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();

		scene.addEntity(player);

		
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(Terrain.SIZE / 2, Terrain.SIZE / 2, 7);
		waters.add(water);	
		
		
		
		while (!Display.isCloseRequested()) {
			
			outputFPS();
			
			update();
			
			
			renderer.renderShadowMap(scene.getEntities(), sun);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(scene, new Vector4f(0, 1, 0, -water.getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, water.getHeight()));

			GL11.glDisable(GL30.GL_CLIP_DISTANCE1);
			buffers.unbindCurrentFrameBuffer();
			
			multisampledFbo.bindFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);
			ParticleMaster.renderParticles(camera);
			multisampledFbo.unbindFrameBuffer();
			multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampledFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColorTexture(), outputFbo2.getColorTexture());
			
			guiRenderer.render(guiTextures);
			TextMaster.render();

			DisplayManager.updateDisplay();
			
		}


		cleanUp();
		

	}
	
	private static void update(){
		
		ViewManager.update();
		
		player.move(scene);
		camera.move();
		picker.update();
					
		ParticleMaster.update(camera);
		
		scene.update();
		
	}
	
	private static void cleanUp(){
		
		PostProcessing.cleanUp();
		TextMaster.cleanUp();
		ParticleMaster.cleanUp();
		multisampledFbo.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}
	
	private static void init(){
		
		DisplayManager.createDisplay();
		loader = new Loader();
		
		RawModel playerModel = OBJLoader.loadOBJ("person", loader);
		TexturedModel playerTexturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));

		player = new Player(playerTexturedModel, new Vector3f(Terrain.SIZE / 2, 50, Terrain.SIZE / 2), 0, 100, 0, 0.6f);
		player = new Player(playerTexturedModel, new Vector3f(0, 0, 0), 0, 100, 0, 0.6f);
		camera = new Camera(player);
		renderer = new MasterRenderer(loader, camera);
		
		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		ViewManager.init(camera);
		
		PostProcessing.init(loader);
		
		scene = new Scene(new ArrayList<Entity>(), new ArrayList<Entity>(), new ArrayList<AnimatedModel>(), new ArrayList<Terrain>(), new ArrayList<Light>(), camera);
		
		picker = new MousePicker(camera, renderer.getProjectionMatrix(), scene);
		
		multisampledFbo = new Fbo(Display.getWidth(), Display.getHeight());
		outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		
		buffers = new WaterFrameBuffers();
		waterShader = new WaterShader();
		waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		
		guiRenderer = new GuiRenderer(loader);
		
	}
	
	private static void outputFPS(){
		
		double currentTime = Sys.getTime();
		framesSinceLastSecond++;
					
		if(currentTime - lastTime >= 1000.0){
			
			System.out.println("" + (1000 / framesSinceLastSecond));
			framesSinceLastSecond = 0;
			lastTime += 1000.0;
			System.out.println(ViewManager.getViewState());
			
		}
				
	}
	
	private static List<Entity> populateLandscape(float minX, float minZ, float maxX, float maxZ, Terrain terrain, float waterHeight, int numIters, TexturedModel[] anywhereModels, TexturedModel[] aboveWaterModels, TexturedModel[] randomRotationAndScaleModels, float minScale, float maxScale){
				
		List<Entity> entities = new ArrayList<>();
		Random random = new Random();
		int totalNumEntities = anywhereModels.length + aboveWaterModels.length + randomRotationAndScaleModels.length;
		
		for (int i = 0; i < numIters; i++) {

			float x = (random.nextFloat() * (maxX - minX)) + minX;
			float z = (random.nextFloat() * (maxZ - minZ)) + minZ;
			float y = terrain.getHeightOfTerrain(x, z);
			
			int rand = random.nextInt(totalNumEntities);
			
			if(rand <= anywhereModels.length - 1){
				
				TexturedModel model = anywhereModels[rand];
				entities.add(new Entity(model, new Vector3f(x, y, z), 0, 0, 0, 1));
				
			}
			
			else if(rand - anywhereModels.length >= anywhereModels.length && rand - anywhereModels.length <= randomRotationAndScaleModels.length + anywhereModels.length - 1){
				
				rand -= anywhereModels.length;
				
				TexturedModel model = randomRotationAndScaleModels[rand];
				Entity e = new Entity(model, new Vector3f(x, y, z), 0, 0, 0, (random.nextFloat() * (maxScale - minScale)) + minScale);
				e.increaseRotation(random.nextFloat() * 360f, random.nextFloat() * 360f, random.nextFloat() * 360f);
				e.increasePosition(0, random.nextFloat() * 2f - 1f, 0);
				entities.add(e);
				
			}
			
			else if(rand - anywhereModels.length - randomRotationAndScaleModels.length >= randomRotationAndScaleModels.length && y > waterHeight){
				
				rand -= (anywhereModels.length + randomRotationAndScaleModels.length);
				
				TexturedModel model = aboveWaterModels[rand];
				entities.add(new Entity(model, new Vector3f(x, y, z), 0, 0, 0, 5));
				
			}
						
		}
		
		return entities;
		
	}

}