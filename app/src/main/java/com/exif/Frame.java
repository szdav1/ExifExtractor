package com.exif;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.DimensionUIResource;

public final class Frame extends JFrame {
	private final LeftPanel leftPanel;
	private final RightPanel rightPanel;
	private final JSplitPane splitPane;
	private final JScrollPane scrollPane;

	public Frame() {
		this.rightPanel = new RightPanel(this);
		this.leftPanel = new LeftPanel(this.rightPanel);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		this.scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		this.scrollPane.setViewportView(this.rightPanel);

		this.splitPane = new JSplitPane(SwingConstants.VERTICAL);
		this.splitPane.setDividerLocation(400);
		this.splitPane.setEnabled(false);
		this.splitPane.add(this.leftPanel, JSplitPane.LEFT);
		this.splitPane.add(this.scrollPane, JSplitPane.RIGHT);

		this.setDefaultFrameSetup();
		this.add(this.splitPane);
	}

	private void setDefaultFrameSetup() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new DimensionUIResource(800, 600));
		this.setLocationRelativeTo(null);
		this.setTitle("Exif Extractor");
	}
}