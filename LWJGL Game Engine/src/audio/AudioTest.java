package audio;

import java.io.IOException;

import org.lwjgl.openal.AL10;

public class AudioTest {
	
	public static void main(String[] args) throws IOException, InterruptedException{
		
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioMaster.loadSound("audio/bounce.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		
		float xPos = 8;
		source.setPosition(xPos, 0, 0);
		
		char c = ' ';
		
		while(c != 'q'){
			
			xPos -= 0.03f;
			source.setPosition(xPos, 0, 0);
			
			Thread.sleep(10);
			
		}
		
		source.delete();
		AudioMaster.cleanUp();
		
	}

}
