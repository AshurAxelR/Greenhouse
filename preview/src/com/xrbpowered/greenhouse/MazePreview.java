package com.xrbpowered.greenhouse;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.xrbpowered.greenhouse.map.generate.PrefabMap;
import com.xrbpowered.greenhouse.map.generate.StyleMap;
import com.xrbpowered.greenhouse.map.generate.TopologyExpander;
import com.xrbpowered.greenhouse.map.generate.TopologyMap;
import com.xrbpowered.greenhouse.preview.MapSet;
import com.xrbpowered.greenhouse.preview.Preview;
import com.xrbpowered.greenhouse.preview.PreviewBlockMap;
import com.xrbpowered.greenhouse.preview.PreviewPrefabMap;
import com.xrbpowered.greenhouse.preview.PreviewStyleMap;
import com.xrbpowered.greenhouse.preview.PreviewTopology;
import com.xrbpowered.utils.SilverOceanTheme;

public class MazePreview extends JFrame {

	private static final Random random = new Random();
	
	private MapSet mapSet = null;
	
	private PreviewPrefabMap previewPrefabMap = new PreviewPrefabMap();
	private PreviewStyleMap previewStyleMap = new PreviewStyleMap();
	private PreviewBlockMap previewBlockMap = new PreviewBlockMap();
	private PreviewTopology previewTopology = new PreviewTopology();
	private Preview<?, ?> preview = previewPrefabMap;
	
	private void generate() {
		mapSet = new MapSet();
		mapSet.topology = new TopologyMap();
		mapSet.topology.generate(random);
		mapSet.blockMap = TopologyExpander.expand(mapSet.topology, random);
		mapSet.styleMap = new StyleMap(mapSet.blockMap);
		mapSet.styleMap.decorate(random);
		mapSet.prefabMap = new PrefabMap(mapSet.styleMap, random);
		repaint();
	}
	
	public MazePreview() {
		super("Maze Generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem miGenerate = new JMenuItem("Generate");
		miGenerate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		miGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generate();
			}
		});
		menuFile.add(miGenerate);

		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);
		
		JMenuItem miViewPrefabMap = new JMenuItem("Preview prefab map");
		miViewPrefabMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
		miViewPrefabMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preview = previewPrefabMap;
				repaint();
			}
		});
		menuView.add(miViewPrefabMap);

		JMenuItem miViewStyleMap = new JMenuItem("Preview style map");
		miViewStyleMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		miViewStyleMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preview = previewStyleMap;
				repaint();
			}
		});
		menuView.add(miViewStyleMap);

		JMenuItem miViewMap = new JMenuItem("Preview block map");
		miViewMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0));
		miViewMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preview = previewBlockMap;
				repaint();
			}
		});
		menuView.add(miViewMap);
		
		JMenuItem miViewTopology = new JMenuItem("Preview topology");
		miViewTopology.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0));
		miViewTopology.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preview = previewTopology;
				repaint();
			}
		});
		menuView.add(miViewTopology);
		
		JPanel cp = new JPanel() {
			public void paint(java.awt.Graphics g) {
				super.paint(g);
				preview.paint((Graphics2D) g, mapSet);
			}
		};
		cp.setPreferredSize(new Dimension(
				Math.max(
					Math.max(
						TopologyMap.SIZE_X*TopologyExpander.EXPAND_X*PreviewBlockMap.tileSize,
						(TopologyMap.SIZE_X*TopologyExpander.EXPAND_X*2+2)*PreviewPrefabMap.tileSize
					),
					TopologyMap.SIZE_X*PreviewTopology.tileSize
				),
				Math.max(
					Math.max(
						TopologyMap.SIZE_Y*TopologyExpander.EXPAND_Y*PreviewBlockMap.tileSize,
						(TopologyMap.SIZE_Y*TopologyExpander.EXPAND_Y*2+2)*PreviewPrefabMap.tileSize
					),
					TopologyMap.SIZE_Y*PreviewTopology.tileSize
				)
			));
		
		setContentPane(cp);
		pack();
		setVisible(true);
		
		generate();
	}
	
	public static void main(String[] args) {
		SilverOceanTheme.enable();
		new MazePreview();
	}

}
