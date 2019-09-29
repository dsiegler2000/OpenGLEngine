package scene;

import java.util.List;

import animations.animatedmodel.AnimatedModel;
import entities.Camera;
import entities.Entity;
import entities.Light;
import terrains.Terrain;

public class Scene {

	private List<Entity> entities;
	private List<Entity> normalMappedEntities;
	private List<AnimatedModel> animatedModels;
	private List<Terrain> terrains;
	private List<Light> lights;
	private Camera camera;

	public Scene(List<Entity> entities, List<Entity> normalMappedEntities, List<AnimatedModel> animatedModels, List<Terrain> terrains, List<Light> lights, Camera camera) {

		this.entities = entities;
		this.normalMappedEntities = normalMappedEntities;
		this.animatedModels = animatedModels;
		this.terrains = terrains;
		this.lights = lights;
		this.camera = camera;

	}

	public void update() {

		for (AnimatedModel anim : animatedModels) {

			anim.update();

		}

	}
	
	public float getHeightOfTerrainAt(float x, float z) {

		for (Terrain t : terrains) {

			if(t.isInThisChunk(x, z)){
				
				return t.getHeightOfTerrain(x, z);
				
			}

		}

		return 0;

	}
	
	public boolean terrainDefinedAt(float x, float z){
		
		for(Terrain t : terrains){
			
			if(t.isInThisChunk(x, z)){
				
				return true;
				
			}
			
		}
		
		return false;
		
	}

	public void addEntity(Entity e) {

		entities.add(e);

	}

	public void addEntities(List<Entity> es) {

		entities.addAll(es);

	}

	public void addNormalMappedEntity(Entity e) {

		normalMappedEntities.add(e);

	}

	public void addAnimatedModel(AnimatedModel m) {

		animatedModels.add(m);

	}

	public void addNormalMappedEntities(List<Entity> es) {

		normalMappedEntities.addAll(es);

	}

	public void addTerrain(Terrain t) {

		terrains.add(t);

	}

	public void addLight(Light l) {

		lights.add(l);

	}

	public List<Entity> getEntities() {

		return entities;

	}

	public List<Entity> getNormalMappedEntities() {

		return normalMappedEntities;

	}

	public List<AnimatedModel> getAnimatedModels() {

		return animatedModels;

	}

	public List<Terrain> getTerrains() {

		return terrains;

	}

	public List<Light> getLights() {

		return lights;

	}

	public Camera getCamera() {

		return camera;

	}

}
