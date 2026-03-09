package com.exif;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javaxt.io.Image;

public final class RightPanel extends JPanel {
	private final Frame frame;
	private HashMap <String, Image> images;

	public RightPanel(final Frame frame) {
		this.frame = frame;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	private void refresh() {
		this.removeAll();
		this.frame.revalidate();

		for (Map.Entry <String, Image> image : this.images.entrySet()) {
			JLabel label = new JLabel(new ImageIcon(new ImageIcon(image.getValue().getImage()).getImage().getScaledInstance(350, 350, java.awt.Image.SCALE_SMOOTH)));

			this.add(label);
			this.add(Box.createRigidArea(new Dimension(0, 10)));
			this.frame.revalidate();
			this.repaint();
		}
	}

	public HashMap <String, Image> getImages() {
		return this.images;
	}

	public void setImages(HashMap <String, Image> images) {
		this.images = images;
		this.refresh();
	}
}