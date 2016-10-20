package com.xrbpowered.greenhouse;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.SystemSettings;
import com.xrbpowered.gl.collider.Collider;
import com.xrbpowered.gl.examples.BlurBackground;
import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.examples.ExampleMenu;
import com.xrbpowered.gl.examples.ExampleWidgetPainters;
import com.xrbpowered.gl.res.ObjMeshLoader;
import com.xrbpowered.gl.res.StandardMeshBuilder;
import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessRenderer;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.gl.res.shaders.SceneShader;
import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.res.textures.TextureCache;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.Projection;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.AbstractLoadScreen;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.gl.ui.UIPage;
import com.xrbpowered.gl.ui.UIPane;
import com.xrbpowered.gl.ui.UIPaneSet;
import com.xrbpowered.gl.ui.widgets.Label;
import com.xrbpowered.gl.ui.widgets.Widget;
import com.xrbpowered.gl.ui.widgets.WidgetPane;
import com.xrbpowered.gl.ui.widgets.menu.MenuItem;
import com.xrbpowered.gl.ui.widgets.menu.MenuOptionItem;
import com.xrbpowered.gl.ui.widgets.menu.WidgetMenuBuilder;
import com.xrbpowered.greenhouse.map.GreenhouseMap;
import com.xrbpowered.greenhouse.map.MapConverter;
import com.xrbpowered.greenhouse.render.ComponentStack;
import com.xrbpowered.greenhouse.render.FlatShader;
import com.xrbpowered.greenhouse.render.GlassBlurShader;
import com.xrbpowered.greenhouse.render.GlassShader;
import com.xrbpowered.greenhouse.render.GlassSkin;
import com.xrbpowered.greenhouse.render.GreenhouseEnvironment;
import com.xrbpowered.greenhouse.render.LoadScreen;
import com.xrbpowered.greenhouse.render.MapShader;
import com.xrbpowered.greenhouse.render.MaterialStack;
import com.xrbpowered.greenhouse.render.Prefab;
import com.xrbpowered.greenhouse.render.RenderStack;
import com.xrbpowered.greenhouse.render.TerminalPointerActor;
import com.xrbpowered.greenhouse.render.TileActor;
import com.xrbpowered.greenhouse.render.WallShader;
import com.xrbpowered.greenhouse.render.WallSkin;
import com.xrbpowered.utils.JNIUtils;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GreenhouseClient extends ExampleClient {

	public static final String VERSION_NAME =  "pre-alpha build 16A03";
	
	private VertexInfo debugLinesInfo = new VertexInfo().addFloatAttrib("in_Position", 2);
	private StaticMesh debugLines = null;
	private Shader debugLinesShader;
	private boolean showDebugLines = false;
	
	public Prefab meshSetWall;
	public Prefab meshSetMid;
	public Prefab meshSetMidLight;
	public Prefab meshSetColPod;
	public Prefab meshSetCIn;
	public Prefab meshSetCOut;
	public StaticMesh meshCrystal;

	public TextureCache textures = new TextureCache();
	
	public WallSkin checkerSkin;
	public WallSkin floorSkin;
	public GlassSkin glassSkin;
	
	public Texture specular;
	public Texture texturePlain;
	public Texture crystalTexture;
	
	public RenderStack renderStack = new RenderStack();

	private MapConverter converter = new MapConverter(this);
	private GreenhouseMap map = null;
	private float timeElapsed;
	private float timeShown = 0f; 

	public ArrayList<TerminalPointerActor> pointerActors = new ArrayList<>();
	
	private boolean mapViewEnabled = false;
	private MapView mapView;
	
	private UIPane uiPaneObjective;
	private UIPane uiMenuPane;
	private String msgObjective;
	private String msgHint;
	public static Font TITLE_FONT;
	public static Font BODY_FONT;
	
	public GreenhouseClient() {
		AssetManager.defaultAssets = new FileAssetManager("assets_dev", new FileAssetManager("assets", AssetManager.defaultAssets));
		init("MazeTest");
		run();
	}
	
	public void teleportPawn(float x, float z) {
		scene.activeCamera.position.x = x;
		scene.activeCamera.position.z = z;
		scene.activeCamera.rotation.x = 0f;
		scene.activeCamera.rotation.y = 0f;
		scene.activeCamera.rotation.z = 0f;
		scene.activeCamera.updateTransform();
	}
	
	public StaticMeshActor makeCrystalActor(Vector3f position, float phase) {
		StaticMeshActor actor = StandardMeshBuilder.makeActor(scene, meshCrystal, crystalTexture, plainSpecularTexture, plainNormalTexture);
		actor.position = position;
		actor.rotation.x = (float) Math.PI / 12f;
		actor.rotation.y = (float) Math.PI * 2f * phase;
		actor.updateTransform();
		
		TerminalPointerActor pointer = new TerminalPointerActor(ui, actor);
		pointer.position = position;
		pointer.updateTransform();
		pointer.maxDist = 10f;
		pointerActors.add(pointer);
		return actor;
	}
	
	private void startNewMap(int size) {
		for(TerminalPointerActor pointer : pointerActors) {
			pointer.pane.destroy();
		}
		pointerActors.clear();
		
		map = converter.generate(size);
		updateInstanceData();
		msgObjective = "Objective: Collect crystals";
		msgHint = null;
		timeElapsed = timeShown = 0f;
	}
	
	private void updateInstanceData() {
		if(debugLines!=null)
			debugLines.destroy();
		float[] lineData = TileActor.getColliderLineData();
		debugLines = new StaticMesh(debugLinesInfo, lineData, 2, lineData.length/2, false);

		renderStack.environment.updateLightColors();
		renderStack.compStack.updateInstanceData(map);
	}

	@Override
	protected void setupResources() {
		Client.timestamp(null);
		try {
			SMALL_FONT = AssetManager.defaultAssets.loadFont("Roboto-Medium.ttf").deriveFont(15f);
			BODY_FONT = SMALL_FONT.deriveFont(17f);
			LARGE_FONT = AssetManager.defaultAssets.loadFont("DaysOne-Regular.ttf").deriveFont(18f);
			TITLE_FONT = LARGE_FONT.deriveFont(30f);
		} catch(IOException e) {
			e.printStackTrace();
		}
		UI_PANE_WIDTH = 220;
		UI_PANE_HEIGHT = 200;
		MAX_DTLOG = 200;
		UI_PANE_BG_COLOR = new Color(0x77000000, true);
		
		AbstractLoadScreen loading = new LoadScreen(MaterialStack.getMaxProgress()+ComponentStack.maxProgress+2).start();
		super.setupResources();
		Client.timestamp("init");
		
		renderStack.environment = new GreenhouseEnvironment();
		renderStack.wallShader = new WallShader(renderStack.environment, scene);
		renderStack.flatShader = new FlatShader(renderStack.environment, scene);
		renderStack.glassShader = new GlassShader(renderStack.environment, scene);
		renderStack.glassBlurShader = new GlassBlurShader(renderStack.environment, scene);
		renderStack.createOrResizeBuffers();
		renderStack.postProc = new PostProcessShader("post_blur_f.glsl") {
			@Override
			protected void storeUniformLocations() {
				super.storeUniformLocations();
				GL20.glUseProgram(pId);
				GL20.glUniform1i(GL20.glGetUniformLocation(pId, "numSamples"), 5);
				GL20.glUniform1f(GL20.glGetUniformLocation(pId, "range"), 15f);
				GL20.glUseProgram(0);
			}
		};
		
		renderStack.mapShader = new MapShader(scene);
		
		debugLinesShader = new SceneShader(scene, debugLinesInfo, "debug_lines_v.glsl", "blank_f.glsl");
		loading.addProgress(1);
		Client.timestamp("load shaders");

		glassSkin = new GlassSkin();
		specular = BufferTexture.createPlainColor(4, 4, new Color(0x055500));
		texturePlain = BufferTexture.createPlainColor(4, 4, new Color(0xdddddd));
		crystalTexture = BufferTexture.createPlainColor(4, 4, new Color(0x55ff00));

		renderStack.mtlStack = MaterialStack.createMaterialStack(loading);
		WallSkin.defaults = new WallSkin(texturePlain, plainNormalTexture, specular, renderStack.mtlStack.mask(0));
		checkerSkin = new WallSkin(textures.get("checker.png"), plainNormalTexture, specular, noSpecularTexture);
		floorSkin = new WallSkin(texturePlain, plainNormalTexture, specular, renderStack.mtlStack.mask(0xf5));
		Client.timestamp("load materials");
		
		renderStack.compStack = ComponentStack.createComponentStack(this, loading); 
		Client.timestamp("load components");
		
		meshSetWall = new Prefab(
				ComponentStack.wallPanel, ComponentStack.wallFrame, ComponentStack.wallFloor,
				ComponentStack.mapFloorWall, ComponentStack.mapLinesWallFrame, ComponentStack.mapLinesWallPanel, ComponentStack.mapLinesWallFloor
			).addColliders(new Vector2f(-1.5f, 1f), new Vector2f(1.5f, 1f));
		meshSetMid = new Prefab(
				ComponentStack.floor, ComponentStack.ceil,
				ComponentStack.mapFloorMid
			);
		meshSetMidLight = new Prefab(
				ComponentStack.floor, ComponentStack.ceilLamp,
				ComponentStack.mapFloorMid, ComponentStack.mapLinesCeilLamp
			);
		meshSetColPod = new Prefab(
				ComponentStack.podOut, ComponentStack.podIn, ComponentStack.plant, ComponentStack.podFloor, ComponentStack.podGlass,
				ComponentStack.mapFloorPod, ComponentStack.mapLinesPod, ComponentStack.mapLinesPodFloor
			).addColliders(
				new Vector2f(0.75f, 1.5f), new Vector2f(1.5f, 0.75f),
				new Vector2f(1.5f, -0.75f), new Vector2f(0.75f, -1.5f),
				new Vector2f(-0.75f, -1.5f), new Vector2f(-1.5f, -0.75f),
				new Vector2f(-1.5f, 0.75f), new Vector2f(-0.75f, 1.5f),
				new Vector2f(0.75f, 1.5f)
			);
		meshSetCIn = new Prefab(
				ComponentStack.cinFrame, ComponentStack.cinFloor,
				ComponentStack.mapFloorCin, ComponentStack.mapLinesCinFrame, ComponentStack.mapLinesCinFloor
			).addColliders(new Vector2f(1f, 1.5f), new Vector2f(1.5f, 1f));
		meshSetCOut = new Prefab(
				ComponentStack.coutPanel, ComponentStack.coutFrame, ComponentStack.coutFloor,
				ComponentStack.mapFloorCout, ComponentStack.mapLinesCoutFrame, ComponentStack.mapLinesCoutPanel, ComponentStack.mapLinesCoutFloor
			).addColliders(new Vector2f(-1.5f, 1f), new Vector2f(0.25f, 1f), new Vector2f(1f, 0.25f), new Vector2f(1f, -1.5f));
		
		meshCrystal = ObjMeshLoader.loadObj("test.obj", 0, 0.2f);
		Client.timestamp("load prefabs");
		
		CLEAR_COLOR = new Color(0.5f, 0.6f, 0.4f);
		
		StandardShader.environment.ambientColor.set(0.1f, 0.2f, 0.1f);//GreenhouseEnvironment.DEFAULT_AMBIENT);
		StandardShader.environment.lightColor.set(0.4f, 0.8f, 0.4f);
		lightActor.rotation = new Vector3f((float)Math.PI/3f, (float)Math.PI/5f, 0);
		lightActor.updateTransform();
		
		StandardShader.environment.setFog(6f, 60f, new Vector4f(0.5f, 0.6f, 0.4f, 1f));
		
		renderStack.environment.setGlobalLighting(GreenhouseEnvironment.DEFAULT_AMBIENT);
		renderStack.environment.setFog(6f, 60f, new Vector4f(0.5f, 0.6f, 0.4f, 1f));
		renderStack.environment.initLightColors(this);
		
		scene.activeCamera.position = new Vector3f(0, 1.5f, 0);
		scene.activeCamera.updateTransform();
		
		controller = new Controller() {
			private Vector2f q = new Vector2f();
			private Vector2f s = new Vector2f();
			private Vector2f d = new Vector2f();
			@Override
			protected void applyVelocity(Vector3f position, Vector4f v) {
				q.x = position.x;
				q.y = position.z;
				s.x = v.x;
				s.y = v.z;
				Collider.calculateDestination(TileActor.allColliders, q, s, d, true);
				position.x = d.x;
				position.z = d.y;
			}
		}.setActor(scene.activeCamera).setLookController(true);
		controller.moveSpeed = 4.5f;
		controller.rotateSpeed *= 0.75f;
		activeController = controller;
		
		loading.addProgress(1);
		Client.timestamp("done setting up resources");
		
		startNewMap(3);
		msgHint = "WIP note: they will be replaced with other stuff later";
		
		Client.checkError();
		loading.destroy();
		
		uiPaneObjective = new UIPane(ui, new BufferTexture(500, 100, false, false, true) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				int w = getWidth();
				int h = getHeight();
				BufferTexture.clearBuffer(g2, w, h);
				
				g2.setPaint(new LinearGradientPaint(50, 0, 450, 0, new float[] {0f, 0.3f, 0.7f, 1f},  new Color[] {
						new Color(0x00000000, true),
						new Color(0x77000000, true),
						new Color(0x77000000, true),
						new Color(0x00000000, true)
					}));
				g2.fillRect(50, 5, 400, 38);
				g2.setPaint(new LinearGradientPaint(0, 0, 500, 0, new float[] {0f, 0.5f, 1f},  new Color[] {
						new Color(0x00ffffff, true),
						new Color(0xffffffff, true),
						new Color(0x00ffffff, true)
					}));
				g2.fillRect(0, 4, 500, 1);
				g2.fillRect(0, 44, 500, 1);
				
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setColor(Color.WHITE);
				g2.setFont(LARGE_FONT);
				drawStringCentered(g2, msgObjective.toUpperCase(), 250, 20);
				g2.setFont(BODY_FONT);
				String hint = msgHint;
				if(hint==null) {
					hint = String.format("%02d:%02d", ((int) timeElapsed)/60, ((int) timeElapsed)%60);
					timeShown = timeElapsed;
				}
				drawStringCentered(g2, hint, 250, 55);
				
				return true;
			}
		});
		uiPaneObjective.setAnchor(Display.getWidth()/2 - 250, 20);
		
		mapView = new MapView(this, (BlurBackground) menu.getBackground());
		
		showMenu();
		((BlurBackground) menu.getBackground()).startTween(0f, 1f);
	}
	
	public static int drawStringCentered(Graphics2D g2, String str, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		int w = fm.stringWidth(str);
		int h = fm.getHeight();
		g2.drawString(str, x - w/2, y + h/2);
		return y + h;
	}
	
	public GreenhouseMap getMap() {
		return map;
	}
	
	public UIManager getUI() {
		return ui;
	}

	private boolean initialMenu = true;

	@Override
	protected ExampleMenu createMenu() {
		UIManager ui = new UIManager();
		uiMenuPane = new UIPane(ui, new BufferTexture(500, 100, false, false, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				int w = getWidth();
				int h = getHeight();
				BufferTexture.clearBuffer(g2, w, h);
				
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setColor(Color.WHITE);
				g2.setFont(TITLE_FONT);
				drawStringCentered(g2, "GREENHOUSE", 250, 20);
				g2.setFont(SMALL_FONT);
				g2.setColor(Color.GRAY);
				drawStringCentered(g2, VERSION_NAME, 250, 50);
				g2.setFont(BODY_FONT);
				g2.setColor(Color.WHITE);
				return true;
			}
		}) {
			@Override
			public void layout(int screenWidth, int screenHeight) {
				centerX();
				y = screenHeight / 4;
			}
		};
		menu = new ExampleMenu(this, ui) {
			private MenuOptionItem optRefPass;
			private UIPage pNewMap;
			private Widget wResume;
			private int mapSize = 5;
			private boolean mapViewEnabled = false;
			@Override
			protected PostProcessRenderer createBackground(Renderer parent) {
				return new BlurBackground(parent);
			}
			protected UIPage createNewMapPage(WidgetMenuBuilder mb) {
				mb.startPage(WIDTH, CAPTION_WIDTH);
				mb.addWidget(new Label(mb.getPageRoot(), "START NEW", ExampleWidgetPainters.LABEL_STYLE_MENU_TITLE));
				mb.addBlank(20);
				mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Map size", new String[] {"Tiny (3x3)", "Small (4x4)", "Normal (5x5)", "Large (6x6)", "Very Large (7x7)", "Huge (8x8)"}, 2, 0) {
					@Override
					public void onChangeValue(int index) {
						mapSize = index+3;
					}
				});
				mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Security Drones", new String[] {"None"}, 0, 0)).setEnabled(false);
				mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Oxygen", new String[] {"Unlimited"}, 0, 0)).setEnabled(false);
				mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Can view map", new String[] {"No", "Yes"}, 0, 0) {
					@Override
					public void onChangeValue(int index) {
						mapViewEnabled = index>0;
					}
				});
				mb.addBlank(20);
				mb.addMenuItem(new MenuItem(mb.getPageRoot(), "START", ExampleWidgetPainters.MENU_STYLE_ACTION) {
					@Override
					public void onMouseDown(int x, int y, int button) {
						startNewMap(mapSize);
						GreenhouseClient.this.mapViewEnabled = mapViewEnabled;
						uiPaneObjective.repaint();
						wResume.setEnabled(true);
						pages.pop();
						client.hideMenu();
					}
				});
				mb.addCancelItem("BACK", ExampleWidgetPainters.MENU_STYLE_ACTION);
				return mb.finishPage();
			}
			@Override
			protected UIPage createMainPage(WidgetMenuBuilder mb) {
				pNewMap = createNewMapPage(mb);
				mb.startPage(WIDTH, CAPTION_WIDTH);
				wResume = mb.addMenuItem(new MenuItem(mb.getPageRoot(), "RESUME", ExampleWidgetPainters.MENU_STYLE_ACTION) {
					@Override
					public void onMouseDown(int x, int y, int button) {
						client.hideMenu();
					}
				}).setEnabled(false);
				mb.addPageItem("START NEW", pNewMap, ExampleWidgetPainters.MENU_STYLE_ACTION);
				mb.addPageItem("SETTINGS", pSettings, ExampleWidgetPainters.MENU_STYLE_ACTION);
				mb.addMenuItem(new MenuItem(mb.getPageRoot(), "EXIT", ExampleWidgetPainters.MENU_STYLE_ACTION) {
					@Override
					public void onMouseDown(int x, int y, int button) {
						client.exit();
					}
				});
				
				WidgetPane m = mb.finishPage();
				m.setVisible(true);
				UIPaneSet panes = new UIPaneSet(m, uiMenuPane);
				panes.setVisible(false);
				return panes;
			}
			@Override
			protected void addQualitySettings(WidgetMenuBuilder mb) {
				super.addQualitySettings(mb);
				mb.addMenuItem(optRefPass = new MenuOptionItem(mb.getPageRoot(), "Refraction pass", new String[] {"Off", "Refraction only", "Refraction+Blur"}, 2, 0));
			}
			@Override
			protected boolean applySettings() {
				if(super.applySettings()) {
					renderStack.refractionMode = optRefPass.getSelectedIndex();
					return true;
				}
				else
					return false;
			}
			@Override
			protected void layout() {
				super.layout();
				pNewMap.layout(Display.getWidth(), Display.getHeight());
			}
			@Override
			public void start() {
				super.start();
				((BlurBackground) getBackground()).startTween(1f, 0.5f);
			}
		};
		return menu;
	}
	
	@Override
	public Matrix4f projectionMatrix() {
		return Projection.perspective(settings.fov, getAspectRatio(), 0.1f, 60.0f);
	}
	
	@Override
	public void hideMenu() {
		super.hideMenu();
		if(initialMenu) {
			initialMenu = false;
			uiMenuPane.repaint();
		}
		activeController.setMouseLook(true);
	}
	
	public void showMap() {
		activeRenderer = mapView;
		activeInput = mapView;
		mapView.start();
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		renderStack.destroy();
		meshCrystal.destroy();
		
		specular.destroy();
		texturePlain.destroy();
		crystalTexture.destroy();
		
		textures.destroy();
	}
	
	@Override
	protected void resizeResources() {
		renderStack.createOrResizeBuffers();
		super.resizeResources();
		uiPaneObjective.setAnchor(Display.getWidth()/2 - 250, 20);
//		uiMenuPane.setAnchor(Display.getWidth()/2 - 250, Display.getHeight()/2 - 320);
	}
	
	@Override
	public void updateResources(SystemSettings settings, SystemSettings old) {
		super.updateResources(settings, old);
		if(settings.anisotropy!=old.anisotropy) {
			renderStack.mtlStack.setAnisotropy(settings.anisotropy);
		}
	}
	
	@Override
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_TAB:
				if(mapViewEnabled) {
					activeController.setMouseLook(false);
					showMap();
				}
				break;
			case Keyboard.KEY_F4:
				showDebugLines = !showDebugLines;
				break;
			case Keyboard.KEY_F5:
				renderStack.refractionMode = (renderStack.refractionMode+1)%3;
				break;
			case Keyboard.KEY_F6:
				settings.anisotropy = settings.anisotropy==16 ? 1 : settings.anisotropy*2; 
				renderStack.mtlStack.setAnisotropy(settings.anisotropy);
				break;
			default:
				super.keyDown(key);
		}
	}
	
	@Override
	protected void updateControllers(float dt) {
		if(Mouse.isButtonDown(0))
			activeController.setMouseLook(true);
		activeController.update(dt);
	}

	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		boolean updateLighting = false;
		boolean objectiveRepaint = false;
		for(Iterator<StaticMeshActor> i = map.crystalActors.iterator(); i.hasNext();) {
			StaticMeshActor actor = i.next();
			if(scene.activeCamera.getDistTo(actor)<1f) {
				map.collectedCrystals++;
				TileActor tile = map.tileFromPosition(actor.position.x, actor.position.z);
				tile.lightColor = MapConverter.CRYSTAL_LIGHT_OFF_COLOR;
				updateLighting = true;
				i.remove();
				
				for(TerminalPointerActor pointer : pointerActors) {
					if(pointer.link==actor)
						pointer.visible = false;
					if(map.crystalActors.size()<=5)
						pointer.maxDist = -1f;
				}
			}
			else {
				actor.rotation.y += (float) Math.PI * dt;
				actor.updateTransform();
			}
		}
		if(updateLighting) {
			if(map.crystalActors.size()==0) {
				msgObjective = "Objective complete in "+String.format("%02d:%02d", ((int) timeElapsed)/60, ((int) timeElapsed)%60);;
				msgHint = "Press Escape to open menu";
			}
			else {
				msgObjective = String.format("Collected %d of %d crystals", map.collectedCrystals, map.collectedCrystals+map.crystalActors.size());
				msgHint = null;
			}
			objectiveRepaint = true;
//			map.calculateLighting();
			updateInstanceData();
		}
		timeElapsed += dt;
		if(objectiveRepaint || msgHint==null && ((int)timeElapsed!=(int)timeShown)) {
			uiPaneObjective.repaint();
		}
		
		renderStack.render(target, map);
		
		if(showDebugLines) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			debugLinesShader.use();
			debugLines.draw();
			debugLinesShader.unuse();
		}
		
		for(TerminalPointerActor pointer : pointerActors) {
			pointer.updateView();
		}
		uiDebugInfo = String.format("Instances: %d\nTris: %d\nDraw calls: %d",
				renderStack.compStack.countInst()+map.crystalActors.size(),
				renderStack.compStack.countTris()+map.crystalActors.size()*meshCrystal.countTris(),
				renderStack.draws);
		uiDebugInfo += "\nRefraction pass: " + ((renderStack.refractionMode==0) ? "off" : (renderStack.refractionMode==1) ? "no blur" : "ref + blur"); 
	}
		
	protected boolean updateDebugInfoBuffer(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		int n = 0;
		String[] s = null;
		if(uiDebugInfo!=null) {
			s = uiDebugInfo.split("\\n");
			n = Math.min(s.length, (h-50)/17+1);
		}
		h = (n==0) ? 30 : (n-1)*17+90;
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(UI_PANE_BG_COLOR);
		g2.fillRect(0, 0, w-1, h-1);

		g2.setFont(SMALL_FONT);
		int y = 20;
		if(n>0) {
			g2.setColor(Color.GRAY);
			g2.drawLine(0, y+10, UI_PANE_WIDTH, y+10);
			g2.drawLine(0, h-30, UI_PANE_WIDTH, h-30);
			g2.setColor(Color.WHITE);
			g2.drawString("Debug info:", 10, y+2);
			y+=30;
			for(int i=0; i<n; i++) {
				g2.drawString(s[i], 10, y);
				y+=17;
			}
		}
		if(fpsUpdateTime>0f) {
			g2.setColor(Color.WHITE);
			g2.drawString(formatFps(), 10, h-10);
		}
		return true;
	}
	
	protected boolean updateGraphBuffer(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(UI_PANE_BG_COLOR);
		g2.fillRect(0, 0, w-1, h-1);
		drawFpsGraph(g2, 10, 25, w-20, h-35);
		return true;
	}
	
	public static void main(String[] args) {
		JNIUtils.addLibraryPath("lib/native");
		new GreenhouseClient();
	}
}
