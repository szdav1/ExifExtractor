package com.exif;

import javax.swing.UIManager;

public class App {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exc) {
			System.err.println("Using default look and feel.");
		}

		Frame frame = new Frame();
		frame.setVisible(true);
		frame.repaint();
	}
}