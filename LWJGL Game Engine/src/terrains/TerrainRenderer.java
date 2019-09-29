package terrains;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import terrains.Terrain;
import terrains.TerrainShader;
import utils.Maths;

public class TerrainRenderer {
	
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
		
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
		
	}
	
	public void render(List<Terrain> terrains, Matrix4f toShadowSpace){
		
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		
		for(Terrain terrain : terrains){
						
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			
			unbindTexturedModel();
			
		}
		
	}
	
	private void prepareTerrain(Terrain terrain){
		
		RawModel rawModel = terrain.getModel();
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL11.glDisable(GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
		shader.loadMaxHeightOfLayers(terrain.getHeightPack().getMaxHeightOfLayers());
		
	}
	
	private void bindTextures(Terrain terrain){
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, terrain.getHeightPack().getHeightTextures().getTextureID());
		
	}
	
	private void unbindTexturedModel(){
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
		
	}
	
	private void loadModelMatrix(Terrain terrain){
		
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX1(), 0, terrain.getZ1()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
		
	}

}
