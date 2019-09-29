package guis;
 
import org.lwjgl.util.vector.Matrix4f;
 
import shaders.ShaderProgram;
 
public class GuiShader extends ShaderProgram {
     
    private static final String VERTEX_FILE = "/guis/guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "/guis/guiFragmentShader.txt";
     
    private int locationTransformationMatrix;
 
    public GuiShader() {
    	
        super(VERTEX_FILE, FRAGMENT_FILE);
        
    }
     
    @Override
    protected void bindAttributes() {
    	
        super.bindAttribute(0, "position");
        
    }
 
    @Override
    protected void getAllUniformLocations() {
    	
    	locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
    
    }
    
    public void loadTransformation(Matrix4f matrix){
        
    	super.loadMatrix(locationTransformationMatrix, matrix);
    
    }
 
}