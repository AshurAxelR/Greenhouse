package com.xrbpowered.greenhouse.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.Shader;
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
	
	public static PrefabComponent mapFloorWall;
	public static PrefabComponent mapFloorMid;
	public static PrefabComponent mapFloorPod;
	public static PrefabComponent mapFloorCin;
	public static PrefabComponent mapFloorCout;
	
	public static PrefabComponent mapLinesWallFrame;
	public static PrefabComponent mapLinesWallPanel;
	public static PrefabComponent mapLinesWallFloor;
	public static PrefabComponent mapLinesCeilLamp;
	public static PrefabComponent mapLinesPod;
	public static PrefabComponent mapLinesPodFloor;
	public static PrefabComponent mapLinesCinFrame;
	public static PrefabComponent mapLinesCinFloor;
	public static PrefabComponent mapLinesCoutFrame;
	public static PrefabComponent mapLinesCoutPanel;
	public static PrefabComponent mapLinesCoutFloor;

	public static final int maxProgress = 16*4+1;  
	
	public static ComponentStack createComponentStack(GreenhouseClient client, AbstractLoadScreen loading) {
		ComponentStack st = new ComponentStack();
		wallPanel = st.add(new PrefabComponent("prefabs/wall/wall_panel.obj", WallSkin.load(client.textures, "prefabs/wall/wall_panel", loading).setMaterialTiling(2, 2)));
		wallFrame = st.add(new PrefabComponent("prefabs/wall/wall_frame.obj", WallSkin.load(client.textures, "prefabs/wall/wall_frame", loading).setMaterialTiling(2, 4)));
		wallFloor = st.add(new PrefabComponent("prefabs/wall/wall_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 0.5f))); loading.addProgress(4);
		mapFloorWall = st.add(new PrefabComponent(wallFloor.mesh, null)).setRenderPass(RenderStack.PASS_MAP_FLOOR);
		mapLinesWallFrame = st.add(new PrefabComponent("map/e_wall_frame.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesWallPanel = st.add(new PrefabComponent("map/e_wall_panel.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesWallFloor = st.add(new PrefabComponent("map/e_wall_floor.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR_LINES);
		
		floor = st.add(new PrefabComponent("prefabs/mid/floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 2))); loading.addProgress(4);
		ceil = st.add(new PrefabComponent("prefabs/mid/ceil.obj", WallSkin.load(client.textures, "prefabs/mid/ceil", loading).setMaterialTiling(2, 4)));
		ceilLamp = st.add(new PrefabComponent("prefabs/mid/ceil_lamp.obj", WallSkin.load(client.textures, "prefabs/mid/ceil_lamp", loading).setMaterialTiling(2, 4)));
		mapFloorMid = st.add(new PrefabComponent(floor.mesh, null)).setRenderPass(RenderStack.PASS_MAP_FLOOR);
		mapLinesCeilLamp = st.add(new PrefabComponent("map/e_ceil_lamp.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		
		podOut = st.add(new PrefabComponent("prefabs/pod/pod_out.obj", WallSkin.load(
				client.textures, "prefabs/pod/pod_out", client.texturePlain, null, null, null, loading
			).setMaterialTiling(4, 4)));
		podFloor = st.add(new PrefabComponent(floor.mesh, WallSkin.load(
				client.textures, "prefabs/pod/pod_floor", client.floorSkin.diffuse, client.floorSkin.normal, null, client.floorSkin.materialMask, loading
			).setMaterialTiling(2, 2)));
		podIn = st.add(new PrefabComponent("prefabs/pod/pod_in.obj", WallSkin.load(client.textures, "prefabs/pod/pod_in", loading).setMaterialTiling(2, 4))
				.setRenderPass(RenderStack.PASS_INNER)
				.setLocalLighting(GreenhouseEnvironment.INNER_AMBIENT, new Vector3f(0f, 2f, 0f), new Vector4f(1.5f, 1.5f, 1.5f, 1f), 2f));
		plant = st.add(new PrefabComponent("prefabs/pod/plant.obj", FlatSkin.load(client.textures, "prefabs/pod/plant",
				null, null, null, client.renderStack.mtlStack.mask(0), null, loading
			).setMaterialTiling(0.2f, 1))
				.setRenderPass(RenderStack.PASS_FLATS)
				.setLocalLighting(GreenhouseEnvironment.INNER_AMBIENT, new Vector3f(0f, 2.5f, 0f), new Vector4f(1.2f, 1.2f, 1.2f, 1f), 2f));
		podGlass = st.add(new PrefabComponent("prefabs/pod/pod_glass.obj", client.glassSkin) {
			public int drawPass(int pass, Shader shader) {
				if(pass==RenderStack.PASS_GLASS1 || pass==RenderStack.PASS_GLASS2)
					return drawInstances(pass, shader);
				else
					return 0;
			};
			public int countTris() {
				return super.countTris()*2;
			};
		}.setLocalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT, new Vector3f(0f, 3f, 0f), new Vector4f(1f, 1f, 1f, 1f), 3f)); loading.addProgress(4);
		mapFloorPod = st.add(new PrefabComponent("prefabs/pod/pod_floor.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR);
		mapLinesPod = st.add(new PrefabComponent("map/e_pod.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesPodFloor = st.add(new PrefabComponent("map/e_pod_floor.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR_LINES);

		cinFrame = st.add(new PrefabComponent("prefabs/cin/cin_frame.obj", WallSkin.load(client.textures, "prefabs/cin/cin_frame", loading).setMaterialTiling(2, 4)));
		cinFloor = st.add(new PrefabComponent("prefabs/cin/cin_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(0.5f, 0.5f))); loading.addProgress(4);
		mapFloorCin = st.add(new PrefabComponent("prefabs/cin/cin_floor_x.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR);
		mapLinesCinFrame = st.add(new PrefabComponent("map/e_cin_frame.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesCinFloor = st.add(new PrefabComponent("map/e_cin_floor.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR_LINES);

		coutPanel = st.add(new PrefabComponent("prefabs/cout/cout_panel.obj", WallSkin.load(client.textures, "prefabs/cout/cout_panel", loading).setMaterialTiling(2, 2)));
		coutFrame = st.add(new PrefabComponent("prefabs/cout/cout_frame.obj", WallSkin.load(client.textures, "prefabs/cout/cout_frame", loading).setMaterialTiling(4, 4)));
		coutFloor = st.add(new PrefabComponent("prefabs/cout/cout_floor.obj", new WallSkin(client.floorSkin).setMaterialTiling(2, 2))); loading.addProgress(4);
		mapFloorCout = st.add(new PrefabComponent(coutFloor.mesh, null)).setRenderPass(RenderStack.PASS_MAP_FLOOR);
		mapLinesCoutFrame = st.add(new PrefabComponent("map/e_cout_frame.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesCoutPanel = st.add(new PrefabComponent("map/e_cout_panel.obj", null)).setRenderPass(RenderStack.PASS_MAP_WALL_LINES);
		mapLinesCoutFloor = st.add(new PrefabComponent("map/e_cout_floor.obj", null)).setRenderPass(RenderStack.PASS_MAP_FLOOR_LINES);
		
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
	
	public int drawPass(int pass, Shader shader) {
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
