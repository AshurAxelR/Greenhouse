package com.xrbpowered.greenhouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ListAssets {

	public static final String assetListFile = "asset_list.txt";
	
	public static final String[] assetPaths = {
		"assets", "assets_dev"
	};
	
	public static void listPath(String path, List<File> list) {
		File dir = new File(path);
		if(!dir.exists())
			return;
		File[] files = dir.listFiles();
		for(File f : files) {
			if(f.isDirectory())
				listPath(f.getPath(), list);
			else if(f.isFile()) {
				list.add(f);
			}
		}
	}
	
	public static void printList(List<File> list) {
		for(File f : list) {
			System.out.printf("%d|%s\n", f.lastModified(), f.getPath().replaceAll("\\\\", "/"));
		}
	}
	
	public static void checkList(List<File> list) {
		Scanner in = new Scanner(System.in);
		int err = 0;
		int mod = 0;
		int ok = 0;
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split("\\|");
			if(s.length!=2)
				continue;
			long time = Long.parseLong(s[0]);
			File f = new File(s[1]);
			if(!f.exists()) {
				System.out.printf("ERROR! missing: %s\n", s[1]);
				err++;
			}
			else {
				list.remove(f);
				if(f.lastModified()<time) {
					System.out.printf("ERROR! outdated: %s\n", s[1]);
					err++;
				}
				else {
					if(f.lastModified()>time) {
						System.out.printf("MODIFIED! newer: %s\n", s[1]);
						mod++;
					}
					else {
						ok++;
					}
				}
			}
		}
		if(!list.isEmpty()) {
			for(File f : list) {
				System.out.printf("MODIFIED! added: %s\n", f.getPath().replaceAll("\\\\", "/"));
				mod++;
			}
		}
		System.out.printf("Done.\n%d errors, %d modified, %d OK\n", err, mod, ok);
		in.close();
	}
	
	public static void main(String[] args) {
		LinkedList<File> list = new LinkedList<>();
		for(String path : assetPaths)
			listPath(path, list);
		
		if(args.length>0 && args[0].equals("-list")) {
			try{
				System.setOut(new PrintStream(assetListFile));
				printList(list);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				System.setIn(new FileInputStream(assetListFile));
				checkList(list);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
