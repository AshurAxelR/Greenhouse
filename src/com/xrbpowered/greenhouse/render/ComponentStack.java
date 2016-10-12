package com.xrbpowered.greenhouse.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.ui.AbstractLoadScreen;
import com.xrbpowered.greenhouse.GreenhouseClient;
import com.xrbpowered.greenhouse.map.GreenhouseMap;

public class ComponentStack {

	public static PrefabComponent wallPanel; 
	public static PrefabComponent wallFrame; 
	public static PrefabComponent wallFloor; 
	
	public static PrefabComponent floor;
	public static PrefabComponent ceil;
	public static PrefabComponent ceilLamp;

	public static PrefabComponent podOut;
	public static PrefabComponent podIn;
	public static PrefabComponent podFloor;
	public static PrefabComponent plant;
	public static PrefabComponent podGlass;

	public static PrefabComponent cinFrame;
	public static PrefabComponent cinFloor;

	public static PrefabComponent coutPanel;
	public static PrefabComponent coutFrame;
	public static PrefabComponent coutFloor;

	public static final int maxProgress = 16*4+1;  
	
	public static ComponentStack createComponentStack(GreenhouseClient client, AbstractLoadScreen loading) {
		ComponentStack st = new ComponentStack();
		wallPanel = st.add(new PrefabComponent("wall_panel.obj", WallSkin.load(client.textures, "wall_panel", loading).setMaterialTiling(2, 2)));
		wallFrame = st.add(new PrefabComponent("wall_frame.obj", WallSkin.load(client.textures, "wall_frame", loading).setMaterialTiling(2, 4)));
		wallFloor = st.add(new PrefabComponent("wall_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 0.5f))); loading.addProgress(4);
		
		floor = st.add(new PrefabComponent("floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 2))); loading.addProgress(4);
		ceil = st.add(new PrefabComponent("ceil.obj", WallSkin.load(client.textures, "ceil", loading).setMaterialTiling(2, 4)));
		ceilLamp = st.add(new PrefabComponent("ceil_lamp.obj", WallSkin.load(client.textures, "ceil_lamp", loading).setMaterialTiling(2, 4)));
		
		podOut = st.add(new PrefabComponent("pod_out.obj", WallSkin.load(
				client.textures, "pod_out", client.texturePlain, null, null, null, loading
			).setMaterialTiling(4, 4)));
		podFloor = st.add(new PrefabComponent("floor.obj", WallSkin.load(
				client.textures, "pod_floor", client.floorSkin.diffuse, client.floorSkin.normal, null, client.floorSkin.materialMask, loading
			).setMaterialTiling(2, 2)));
		podIn = st.add(new PrefabComponent("pod_in.obj", WallSkin.load(client.textures, "pod_in", loading).setMaterialTiling(2, 4))
				.setRenderPass(RenderStack.PASS_INNER)
				.setLocalLighting(GreenhouseEnvironment.INNER_AMBIENT, new Vector3f(0f, 2f, 0f), new Vector4f(1.5f, 1.5f, 1.5f, 1f), 2f));
		plant = st.add(new PrefabComponent("plant.obj", FlatSkin.load(client.textures, "plant",
				null, null, null, client.renderStack.mtlStack.mask(0), null, loading
			).setMaterialTiling(0.2f, 1))
				.setRenderPass(RenderStack.PASS_FLATS)
				.setLocalLighting(GreenhouseEnvironment.INNER_AMBIENT, new Vector3f(0f, 2.5f, 0f), new Vector4f(1.2f, 1.2f, 1.2f, 1f), 2f));
		podGlass = st.add(new PrefabComponent("pod_glass.obj", client.glassSkin) {
			public int drawPass(int pass, GreenhouseShader shader) {
				if(pass==RenderStack.PASS_GLASS1 || pass==RenderStack.PASS_GLASS2)
					return drawInstances(pass, shader);
				else
					return 0;
			};
			public int countTris() {
				return super.countTris()*2;
			};
		}.setLocalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT, new Vector3f(0f, 3f, 0f), new Vector4f(1f, 1f, 1f, 1f), 3f)); loading.addProgress(4);

		cinFrame = st.add(new PrefabComponent("cin_frame.obj", WallSkin.load(client.textures, "cin_frame", loading).setMaterialTiling(2, 4)));
		cinFloor = st.add(new PrefabComponent("cin_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(0.5f, 0.5f))); loading.addProgress(4);

		coutPanel = st.add(new PrefabComponent("cout_panel.obj", WallSkin.load(client.textures, "cout_panel", loading).setMaterialTiling(2, 2)));
		coutFrame = st.add(new PrefabComponent("cout_frame.obj", WallSkin.load(client.textures, "cout_frame", loading).setMaterialTiling(4, 4)));
		coutFloor = st.add(new PrefabComponent("cout_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 2))); loading.addProgress(4);
		
		loading.addProgress(1);
		
		return st;
	}
	
	private List<PrefabComponent> components = new ArrayList<>();
	
	public PrefabComponent add(PrefabComponent comp) {
		components.add(comp);
		return comp;
	}
	
	public void updateInstanceData(GreenhouseMap map) {
		for(PrefabComponent c : components)
			c.updateInstanceData(map);
	}
	
	public int countInst() {
		int count = 0;
		for(PrefabComponent c : components)
			count += c.countInst();
		return count;
	}

	public int countTris() {
		int tris = 0;
		for(PrefabComponent c : components)
			tris += c.countInst() * c.countTris();
		return tris;
	}

	public int drawInstances(WallShader shader) {
		int draws = 0;
		for(PrefabComponent c : components)
			draws += c.drawInstances(0, shader);
		return draws;
	}
	
	public int drawPass(int pass, GreenhouseShader shader) {
		int draws = 0;
		for(PrefabComponent c : components)
			draws += c.drawPass(pass, shader);
		return draws;
	}
	
	public void destroy() {
		for(PrefabComponent c : components)
			c.destroy();
	}
	
}
