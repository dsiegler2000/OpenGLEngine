package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderengine.Loader;

public class ParticleMaster {
	
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
	private static ParticleRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix){
		
		renderer = new ParticleRenderer(loader, projectionMatrix);
		
	}
	
	public static void update(Camera cam){
		
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		
		while(mapIterator.hasNext()){
			
			Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
			
			List<Particle> list = entry.getValue();
			
			Iterator<Particle> iterator = list.iterator();
			
			while(iterator.hasNext()){
				
				Particle p = iterator.next();
				boolean stillAlive = p.update(cam);
				
				if(!stillAlive){
					
					iterator.remove();
					
					if(list.isEmpty()){
						
						mapIterator.remove();
						
					}
					
				}
				
			}
			
			if(!entry.getKey().usesAddativeBlending()){
				
				InsertionSort.sortHighToLow(list);
				
			}
						
		}
		
	}
	
	public static void renderParticles(Camera camera){
		
		renderer.render(particles, camera);
		
	}
	
	public static void cleanUp(){
		
		renderer.cleanUp();
		
	}
	
	public static void addParticle(Particle p){
		
		List<Particle> list = particles.get(p.getTexture());
		
		if(list == null){
			
			list = new ArrayList<>();
			particles.put(p.getTexture(), list);
			
		}
		
		list.add(p);
		
	}

}
