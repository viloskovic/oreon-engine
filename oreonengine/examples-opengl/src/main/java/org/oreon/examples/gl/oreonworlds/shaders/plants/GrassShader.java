package org.oreon.examples.gl.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class GrassShader extends GLShaderProgram{

	private static GrassShader instance = null;

	public static GrassShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GrassShader();
	    }
	      return instance;
	}
	
	protected GrassShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/Grass_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/Grass_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/Grass_FS.glsl"));
		compileShader();
		
		addUniform("material.diffusemap");
//		addUniform("material.shininess");
//		addUniform("material.emission");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		
		for (int i=0; i<500; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}	
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		((GLInstancedCluster) object.getParentNode()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((GLInstancedCluster) object.getParentNode()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		setUniformi("isReflection", EngineContext.getConfig().isRenderReflection() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		setUniform("clipplane", EngineContext.getConfig().getClipplane());
		
		@SuppressWarnings("unchecked")
		Material<GLTexture> material = (Material<GLTexture>) object.getComponent(NodeComponentType.MATERIAL0);

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
//		setUniformf("material.shininess", material.getShininess());
//		setUniformf("material.emission", material.getEmission());
		
		List<Integer> indices = ((InstancedCluster) object.getParentNode()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
