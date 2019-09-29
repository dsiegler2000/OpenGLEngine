package viewmanager;

import org.lwjgl.input.Keyboard;

import entities.Camera;

public class ViewManager {
	
	private static ViewState viewState;
	private static Camera camera;
		
	public static void init(Camera cam){
		
		viewState = ViewState.THIRD_PERSON;
		
		camera = cam;
		
		Keyboard.enableRepeatEvents(true);
				
	}
	
	public static void update(){
		
		while(Keyboard.next()){
			
			if(Keyboard.getEventKey() == Keyboard.KEY_TAB && Keyboard.getEventKeyState()){
				
				cycleStates();
				
			}
			
		}
		
	}
	
	private static void cycleStates(){
		
		viewState = viewState.getNextViewState();
		camera.setViewState(viewState);
		
	}
	
	public static ViewState getViewState(){
		
		return viewState;
		
	}

}
