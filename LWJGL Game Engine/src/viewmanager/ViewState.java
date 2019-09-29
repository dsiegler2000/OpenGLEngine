package viewmanager;


public enum ViewState {
	
	THIRD_PERSON,
	FIRST_PERSON,
	SPECTATOR;

	public ViewState getNextViewState(){
		
		switch(this){
			
			case THIRD_PERSON:
				return FIRST_PERSON;

			case FIRST_PERSON:
				return SPECTATOR;
				
			case SPECTATOR:
				return THIRD_PERSON;
				
			default:
				return THIRD_PERSON;
				
		}
		
	}
	
}
