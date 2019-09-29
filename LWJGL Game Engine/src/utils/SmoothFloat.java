package utils;

public class SmoothFloat {

	private final float agility;

	private float target;
	private float actual;
	
	private float lastUpdatePosition;

	public SmoothFloat(float initialValue, float agility) {
		
		this.target = initialValue;
		this.actual = initialValue;
		this.agility = agility;
		this.lastUpdatePosition = initialValue;
		
	}

	public void update(float delta) {

		float offset = target - actual;
		float change = offset * delta * agility;
		actual += change;
		
		if((lastUpdatePosition < target && actual > target) || (lastUpdatePosition > target && actual < target)){
			
			actual = target;
			
		}
		
		lastUpdatePosition = actual;
		
	}

	public void increaseTarget(float dT) {

		this.target += dT;
		
	}

	public void setTarget(float target) {

		this.target = target;
		
	}

	public float get() {

		return actual;
		
	}

	public float getTarget() {

		return target;
		
	}
	
	public void setActual(float val){
		
		actual = val;
		
	}

}
