package com.exif;

import java.awt.Image;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class RightPanel extends JPanel {
	private boolean shouldDisplay;
	private final Frame frame;
	private File rootDirectory;

	public RightPanel(final Frame frame) {
		this.shouldDisplay = false;
		this.frame = frame;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	private void refresh() {
		this.removeAll();
		this.frame.revalidate();

		if (!this.shouldDisplay)
			return;

		if (this.rootDirectory == null)
			return;

		File[] files = this.rootDirectory.listFiles();

		for (File file : files) {
			JLabel label = new JLabel();
			label.setIcon(new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(350, 400, Image.SCALE_FAST)));
			this.add(label);
			this.frame.revalidate();
		}
	}

	public File getRootDirectory() {
		return this.rootDirectory;
	}

	public void setRootDirectory(File rootDirectory, boolean shouldDisplay) {
		this.rootDirectory = rootDirectory;
		this.shouldDisplay = shouldDisplay;
		this.refresh();
	}
}