package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import renderengine.DisplayManager;

public class Particle {

	private Vector3f position;
	private Vector3f velocity;
	
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private ParticleTexture texture;
	
	private Vector2f texOffset1;
	private Vector2f texOffset2;
	
	private float blend;
	
	private float elapsedTime;
	private float distance;
	
	private Vector3f reusableChange;

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {

		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.elapsedTime = 0;
		
		this.texOffset1 = new Vector2f();
		this.texOffset2 = new Vector2f();
		
		this.reusableChange = new Vector3f();
		
		ParticleMaster.addParticle(this);
		
	}
		
	protected boolean update(Camera camera){
		
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		reusableChange.set(velocity);
		reusableChange.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(reusableChange, position, position);

		updateTextureCoordInfo();
		
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		
		return elapsedTime < lifeLength;
		
	}
	
	private void updateTextureCoordInfo(){
		
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		
		this.blend = atlasProgression % 1;
		
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
		
	}
	
	private void setTextureOffset(Vector2f offset, int index){
		
		int col = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		
		offset.x = (float) col / (float) texture.getNumberOfRows();
		offset.y = (float) row / (float) texture.getNumberOfRows();
		
	}
	
	public Vector3f getPosition() {
	
		return position;
		
	}

	
	public float getRotation() {
	
		return rotation;
		
	}

	
	public float getScale() {
	
		return scale;
		
	}
	
	public ParticleTexture getTexture(){
		
		return texture;
		
	}
	
	public Vector2f getTexOffset1() {
	
		return texOffset1;
		
	}

	
	public Vector2f getTexOffset2() {
	
		return texOffset2;
		
	}

	
	public float getBlend() {
	
		return blend;
		
	}
	
	
	public float getDistance() {
	
		return distance;
	
	}
	
}
