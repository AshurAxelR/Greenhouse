package com.xrbpowered.greenhouse.render;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.ObjMeshLoader;
import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.InstanceBuffer;
import com.xrbpowered.greenhouse.map.GreenhouseMap;
import com.xrbpowered.greenhouse.map.generate.TopologyExpander;
import com.xrbpowered.greenhouse.map.generate.TopologyMap;

public class PrefabComponent {
	public static final String BASE_PATH = "";
	public static final int MAX_INSTANCES = TopologyExpander.EXPAND_X * TopologyExpander.EXPAND_Y * TopologyMap.SIZE_X * TopologyMap.SIZE_Y * 4;
	private static int startAttrib = 4;
	private static final String[] ATTRIB_NAMES = {"ins_Position", "ins_RotationY"}; // , "ins_PivotPosition"};
	

	public StaticMesh mesh;
	public ComponentSkin skin;

	private float[] instanceData;
	private int instCount;
	private InstanceBuffer instBuffer;
	private int renderPass = 0;
	
	private Vector4f localAmbient;
	private Vector3f localLightOffset;
	private Vector4f localLightColor;
	private float localLightRange;
	
	public PrefabComponent(String objPath, ComponentSkin skin) {
		this.mesh = ObjMeshLoader.loadObj(BASE_PATH + objPath, 0, 1f);
		this.skin = skin;
		instanceData = new float[MAX_INSTANCES * 4];
		instBuffer = new InstanceBuffer(1, MAX_INSTANCES, startAttrib, new int[] {3, 1});
	}

	public void updateInstanceData(GreenhouseMap map) {
		instCount = map.getInstanceData(instanceData, this);
		instBuffer.updateInstanceData(instanceData, instCount);
	}
	
	public PrefabComponent setRenderPass(int renderPass) {
		this.renderPass = renderPass;
		return this;
	}
	
	public PrefabComponent setLocalLighting(Vector4f ambientColor, Vector3f localLightOffset, Vector4f localLightColor, float range) {
		this.localAmbient = ambientColor;
		this.localLightOffset = localLightOffset;
		this.localLightColor = localLightColor;
		this.localLightRange = range;
		return this;
	}
	
	public int countInst() {
		return instCount;
	}
	
	public int countTris() {
		return mesh.countTris();
	}
	
	public int drawInstances(int pass, GreenhouseShader shader) {
		skin.use(pass, shader);
		if(localLightColor!=null && shader instanceof GreenhouseLightShader) {
			((GreenhouseLightShader) shader).setLocalLighting(localAmbient, localLightOffset, localLightColor, localLightRange);
		}
		mesh.enableDraw(null);
		instBuffer.enable();
		mesh.drawCallInstanced(instCount);
		instBuffer.disable();
		mesh.disableDraw();
		return 1;
	}

	public int drawPass(int pass, GreenhouseShader shader) {
		if(pass==renderPass)
			return drawInstances(pass, shader);
		else
			return 0;
	}
		
	public void destroy() {
		instBuffer.destroy();
		mesh.destroy();
		skin.destroy();
	}

	public static int bindShader(GreenhouseShader shader, int startAttrib) {
		PrefabComponent.startAttrib = startAttrib;
		return InstanceBuffer.bindAttribLocations(shader, startAttrib, ATTRIB_NAMES);
	}	
	
}
