package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import renderengine.DisplayManager;
import viewmanager.ViewState;

public class Camera {
	
	private static final float SPECTATOR_CLIMB_SPEED = 60f;
	private static final float SPECTATOR_TURN_SPEED = 10f;
	private static final float SPECTATOR_MOVE_SPEED = 50f;
	
	private static final float PITCH_SENSITIVITY = 0.3f;
	private static final float YAW_SENSITIVITY = 0.3f;
	private static final float MAX_PITCH = 90;
	
	private static final float MAX_DISTANCE_FROM_PLAYER = 150;
	private static final float MIN_DISTANCE_FROM_PLAYER = 13;
	
	public static final float FAR_PLANE = 10000f;
	public static final float NEAR_PLANE = 0.2f;
	
	private static final float Y_OFFSET = 2.7f;
	
	public static final float FOV = 70;

	private Vector3f position;
	
	private float yaw;
	private float roll;
	
	private Player player;
	
	private float pitch;
	private float angleAroundPlayer;
	private float distanceFromPlayer;
	
	private ViewState viewState;
	
	public Camera(Player p){
		
		player = p;
		position = new Vector3f(0, 10, 0);
		
		pitch = 25;
		angleAroundPlayer = 0;
		distanceFromPlayer = 32;
		
		viewState = ViewState.THIRD_PERSON;
				
	}
	
	public void setViewState(ViewState s){
		
		viewState = s;
		
		if(viewState == ViewState.SPECTATOR){
			
			player.setMoveable(false);
			
		}
		
		else{
			
			player.setMoveable(true);
			
		}
		
	}
	
	public void move(){
		
		switch(viewState){
			
			case THIRD_PERSON:
				thirdPersonMove();
				break;
				
			case FIRST_PERSON:
				firstPersonMove();
				break;
				
			case SPECTATOR:
				spectatorMove();
				break;
			
		}
		
	}
	
	private void spectatorMove(){
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			
			position.y += SPECTATOR_CLIMB_SPEED * DisplayManager.getFrameTimeSeconds();
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			
			position.y -= SPECTATOR_CLIMB_SPEED * DisplayManager.getFrameTimeSeconds();
				
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			
			float xOff = (float) ((SPECTATOR_MOVE_SPEED * DisplayManager.getFrameTimeSeconds()) * Math.cos(Math.toRadians(yaw)));
			float zOff = (float) ((SPECTATOR_MOVE_SPEED * DisplayManager.getFrameTimeSeconds()) * Math.sin(Math.toRadians(yaw)));
			
			position.x += xOff;
			position.z += zOff;
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			
			float xOff = (float) ((SPECTATOR_MOVE_SPEED * DisplayManager.getFrameTimeSeconds()) * Math.cos(Math.toRadians(yaw)));
			float zOff = (float) ((SPECTATOR_MOVE_SPEED * DisplayManager.getFrameTimeSeconds()) * Math.sin(Math.toRadians(yaw)));
			
			position.x -= xOff;
			position.z -= zOff;
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			
			yaw -= SPECTATOR_TURN_SPEED * DisplayManager.getFrameTimeSeconds();
			
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			
			yaw += SPECTATOR_TURN_SPEED * DisplayManager.getFrameTimeSeconds();
			
		}
					
		pitch -= Mouse.getDY() * 0.3;
		yaw += Mouse.getDX() * 0.3;
				
		yaw %= 360;
		
	}
	
	private void firstPersonMove(){
		
		position = player.getPosition();
		position.y += Y_OFFSET;
		pitch = 0f;
		distanceFromPlayer = 3f;
		angleAroundPlayer = 0f;
		yaw = player.getRotY();
		
	}
	
	private void thirdPersonMove(){
		
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		
		calculateCameraPosition(horizontalDistance, verticalDistance);
		
		yaw = 360 - angleAroundPlayer;
		yaw %= 360;
		
		yaw = 180 - (player.getRotY() + angleAroundPlayer);
		
	}
	
	private void calculateZoom(){
		
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		
		distanceFromPlayer -= zoomLevel;
		
		clampZoom();
		
	}
	
	private void calculatePitch(){
		
		if(Mouse.isButtonDown(0)){
			
			float pitchChange = Mouse.getDY() * PITCH_SENSITIVITY;			
			pitch -= pitchChange;
			clampPitch();
			
		}
				
	}
	
	private void calculateAngleAroundPlayer(){
		
		if (Mouse.isButtonDown(0)) {
			
			float angleChange = Mouse.getDX() * YAW_SENSITIVITY;
			angleAroundPlayer -= angleChange;
		
		}
				
	}
	
	private float calculateHorizontalDistance(){
		
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	
	}
	
	private float calculateVerticalDistance(){
		
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
				
	}
	
	public void invertPitch() {

		pitch = -pitch;
		
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));

		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		
		position.y = player.getPosition().y + verticalDistance + Y_OFFSET;
		
	}
	
	private void clampPitch() {

		if (pitch < 0) {
			
			pitch = 0;
			
		}
		
		else if (pitch > MAX_PITCH) {
			
			pitch = MAX_PITCH;
			
		}
		
	}
	
	private void clampZoom(){
		
		distanceFromPlayer = Math.min(MAX_DISTANCE_FROM_PLAYER, distanceFromPlayer);
		distanceFromPlayer = Math.max(MIN_DISTANCE_FROM_PLAYER, distanceFromPlayer);
				
	}
	
	public Vector3f getPosition() {
		
		return position;
		
	}

	public float getPitch() {
		
		return pitch;
		
	}

	public float getYaw() {
		
		return yaw;
		
	}

	public float getRoll() {
		
		return roll;
		
	}

}
