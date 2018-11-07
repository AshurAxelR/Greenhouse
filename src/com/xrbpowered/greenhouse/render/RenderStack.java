package com.xrbpowered.greenhouse.render;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.greenhouse.map.GreenhouseMap;

public class RenderStack {

	public static final int PASS_WALL = 0;
	public static final int PASS_INNER = 1;
	public static final int PASS_FLATS = 2;
	public static final int PASS_GLASS1 = 3;
	public static final int PASS_GLASS2 = 4;

	public static final int PASS_MAP_WALLBG = 100;
	public static final int PASS_MAP_FLOOR = 101;
	public static final int PASS_MAP_FLOOR_LINES = 102;
	public static final int PASS_MAP_WALL_LINES = 103;

	public GreenhouseEnvironment environment;
	public WallShader wallShader;
	public FlatShader flatShader;
	public GlassShader glassShader;
	public GlassBlurShader glassBlurShader;

	public MapShader mapShader;

	public OffscreenBuffers interBuffers = null;
	public OffscreenBuffers blurBuffers = null;
	public PostProcessShader postProc;
	
	public MaterialStack mtlStack;
	public ComponentStack compStack;
	
	public int draws;
	
	public int refractionMode = 2;

	public int render(RenderTarget target, GreenhouseMap map) {
		draws = 0;
		
		mtlStack.bind(4);
		GL11.glEnable(GL11.GL_CULL_FACE);
		wallShader.use();
		draws += compStack.drawPass(PASS_WALL, wallShader);
		draws += compStack.drawPass(PASS_INNER, wallShader);
		wallShader.setGlobalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		flatShader.use();
		draws += compStack.drawPass(PASS_FLATS, flatShader);
		flatShader.setGlobalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		for(StaticMeshActor actor : map.crystalActors) {
//			map.tileFromPosition(actor.position.x, actor.position.z).setLights();
			actor.draw();
			draws++;
		}
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		Texture.unbind(4);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		glassShader.use();
		draws += compStack.drawPass(PASS_GLASS1, glassShader);
		glassShader.setGlobalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);

		if(refractionMode>0) {
			OffscreenBuffers.blit(target.resolve(), interBuffers, true);
			if(refractionMode>1) {
				blurBuffers.use();
				postProc.draw(interBuffers, 0f);
			}
			target.use();
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			glassBlurShader.use();
			interBuffers.bindColorBuffer(2);
			if(refractionMode>1)
				blurBuffers.bindColorBuffer(3);
			else
				interBuffers.bindColorBuffer(3);
			draws += compStack.drawPass(PASS_GLASS2, glassBlurShader);
			glassBlurShader.unuse();
			Texture.unbind(2);
			Texture.unbind(3);
		}
		
		return draws;
	}
	
	public int renderMap(RenderTarget target, GreenhouseMap map) {
		draws = 0;
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		mapShader.setPlainColor(new Vector4f(0f, 0f, 0f, 1f));
		mapShader.use();
		draws += compStack.drawPass(PASS_WALL, mapShader);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		mapShader.setFog(50f, new Vector4f(0.4f, 0.4f, 0.4f, 1f), 120f, new Vector4f(0f, 0f, 0f, 1f));
		mapShader.use();
		draws += compStack.drawPass(PASS_MAP_FLOOR, mapShader);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glEnable(GL11.GL_BLEND);
		mapShader.setFog(40f, new Vector4f(0.25f, 0.25f, 0.25f, 1f), 110f, new Vector4f(0f, 0f, 0f, 1f));
		mapShader.use();
		draws += compStack.drawPass(PASS_MAP_WALL_LINES, mapShader);
		GL11.glDisable(GL11.GL_BLEND);

		mapShader.setPlainColor(new Vector4f(1f, 1f, 1f, 1f));
		mapShader.use();
		draws += compStack.drawPass(PASS_MAP_FLOOR_LINES, mapShader);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		environment.setFog(6f, 60f, new Vector4f(0.5f, 0.6f, 0.4f, 1f));
		GL11.glDepthMask(true);
		
		return draws;
	}
	
	public void createOrResizeBuffers() {
		int w = Display.getWidth();
		int h = Display.getHeight();
		if(interBuffers!=null)
			interBuffers.destroy();
		interBuffers = new OffscreenBuffers(w, h, false);
		if(blurBuffers!=null)
			blurBuffers.destroy();
		blurBuffers = new OffscreenBuffers(w/6, h/6, false);
	}
	
	public void destroy() {
		wallShader.destroy();
		flatShader.destroy();
		glassShader.destroy();
		compStack.destroy();
		mtlStack.destroy();
	}
}
