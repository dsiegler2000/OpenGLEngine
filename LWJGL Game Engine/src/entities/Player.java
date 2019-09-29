package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderengine.DisplayManager;
import scene.Scene;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 800f;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 18;
		
	private float currentSpeed;
	private float currentTurnSpeed;
	private float upwardSpeed;
	
	private boolean isInAir;
	
	private boolean moveable;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		
		super(model, position, rotX, rotY, rotZ, scale);
		
		currentSpeed = 0;
		currentTurnSpeed = 0;
		upwardSpeed = 0;
		
		isInAir = false;
		moveable = true;

	}
	
	public void move(Scene scene){
		
		if(moveable){
			
			checkInputs();
			
			super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
			super.setRotY(super.getRotY() % 360);
			
			float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
			float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
			float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
			
			super.increasePosition(dx, 0, dz);
			
			upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
			super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
			
			float terrainHeight = scene.getHeightOfTerrainAt(super.getPosition().x, super.getPosition().z);

			if(super.getPosition().y < terrainHeight){
				
				upwardSpeed = 0;
				super.getPosition().y = terrainHeight;
				isInAir = false;
				
			}
			
		}
		
	}
	
	public void setMoveable(boolean m){
		
		moveable = m;
		
	}
	
	public boolean isMoveable(){
		
		return moveable;
		
	}
	
	private void jump(){
		
		if(!isInAir){
			
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
			
		}
				
	}
	
	private void checkInputs(){
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			
			currentSpeed = RUN_SPEED;
			
		}
		
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			
			currentSpeed = -RUN_SPEED;
			
		}
		
		else{
			
			currentSpeed = 0;
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			
			currentTurnSpeed = -TURN_SPEED;
			
		}
		
		else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			
			currentTurnSpeed = TURN_SPEED;
			
		}
		
		else{
			
			currentTurnSpeed = 0;
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			
			jump();
			
		}
		
	}

}
