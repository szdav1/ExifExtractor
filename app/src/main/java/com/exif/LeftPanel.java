package com.exif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javaxt.io.Image;

public final class LeftPanel extends JPanel implements ActionListener {
	private final RightPanel rightPanel;

	private final JLabel promptLabel;
	private final JTextField exportNameInput;
	private final JLabel feedbackLabel;

	private final JTextArea feedbackArea;
	private final JScrollPane feedbackScrollPane;

	private final JButton importButton;
	private final JButton exportDataButton;

	private final JFileChooser fileChooser;
	private final JFileChooser fileSaver;

	public LeftPanel(final RightPanel rightPanel) {
		this.rightPanel = rightPanel;

		this.promptLabel = new JLabel("Destination file name:");

		this.exportNameInput = new JTextField();
		this.exportNameInput.setPreferredSize(new Dimension(200, 20));
		this.exportNameInput.setToolTipText("The name of the file the EXIF data should be exported to (Without file extension)");
		this.exportNameInput.setText("exif");

		this.feedbackLabel = new JLabel();
		this.feedbackLabel.setPreferredSize(new Dimension(400, 40));
		this.feedbackLabel.setHorizontalAlignment(JLabel.CENTER);
		this.feedbackLabel.setVerticalAlignment(JLabel.CENTER);
		this.feedbackLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));

		this.feedbackArea = new JTextArea();
		this.feedbackArea.setEditable(false);
		this.feedbackArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

		this.feedbackScrollPane = new JScrollPane();
		this.feedbackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.feedbackScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.feedbackScrollPane.setPreferredSize(new Dimension(400, 400));
		this.feedbackScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		this.feedbackScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		this.feedbackScrollPane.setViewportView(this.feedbackArea);


		this.importButton = new JButton();
		this.importButton.setToolTipText("Import images for EXIF extraction");
		this.importButton.setText("Import Images");
		this.importButton.setFocusable(false);
		this.importButton.addActionListener(this);

		this.exportDataButton = new JButton();
		this.exportDataButton.setToolTipText("Export EXIF data from the imported files");
		this.exportDataButton.setText("Export EXIF...");
		this.exportDataButton.setFocusable(false);
		this.exportDataButton.addActionListener(this);

		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.fileChooser.setMultiSelectionEnabled(true);

		this.fileSaver = new JFileChooser();
		this.fileSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileSaver.setAcceptAllFileFilterUsed(false);
		this.fileSaver.setMultiSelectionEnabled(false);

		this.setPreferredSize(new Dimension(400, 600));
		this.setLayout(new FlowLayout());
		this.add(this.promptLabel);
		this.add(this.exportNameInput);
		this.add(this.importButton);
		this.add(this.exportDataButton);
		this.add(this.feedbackLabel);
		this.add(this.feedbackScrollPane);
	}
	

	private List <File> filterFiles(final File[] files) {
		return Arrays.asList(files)
			.stream()
			.filter(file ->  {
				return file.getName().endsWith(".png") || 
					file.getName().endsWith(".jpg") || 
					file.getName().endsWith(".jpeg") || 
					file.getName().endsWith(".heic") || 
					file.getName().endsWith(".tiff");
				}
			)
			.toList();
	}

	private void getSelectedFiles() {
		int result = this.fileChooser.showOpenDialog(null);

		if (result != JFileChooser.APPROVE_OPTION) 
			return;

		File[] selectedFiles = this.fileChooser.getSelectedFiles();
		List <File> filtered = this.filterFiles(selectedFiles);
		HashMap <String, Image> images = new HashMap<>();

		filtered.forEach(file -> images.put(file.getName(), new Image(file.getAbsolutePath())));
		this.rightPanel.setImages(images);
	}

	private void exportData() {
		String targetFileName = this.exportNameInput.getText();

		if (targetFileName.isBlank()) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("Please specify target filename!");
			return;
		}

		if (this.rightPanel.getImages() == null || this.rightPanel.getImages().size() <= 0) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("Select images for EXIF extraction!");
			return;
		}

		int result = this.fileSaver.showSaveDialog(null);

		if (result != JFileChooser.APPROVE_OPTION)
			return;

		File selectedDirectory = this.fileSaver.getSelectedFile();

		if (selectedDirectory == null) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("Select images for EXIF extraction!");
			return;
		}
		
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(selectedDirectory.getAbsolutePath())
			.append("\\")
			.append(targetFileName)
			.append(".csv");

		try (FileWriter writer = new FileWriter(new File(stringBuilder.toString()))) {
			for (Map.Entry <String, Image> entry : this.rightPanel.getImages().entrySet()) {
				Image image = entry.getValue();
				String name = entry.getKey();

				double[] coordinates = image.getGPSCoordinate();
				String datum = image.getGPSDatum();

				if (coordinates == null) {
					StringBuilder errorBuilder = new StringBuilder();
					
					errorBuilder.append(name)
						.append(" NO_DATA")
						.append("\n");

					this.feedbackArea.setText(this.feedbackArea.getText() + errorBuilder.toString());
					continue;
				}

				writer.write(name);
				writer.write(";");

				for (double coordinate : coordinates) {
					writer.write(String.valueOf(coordinate));
					writer.write(";");
				}

				writer.write(datum);
				writer.write("\n");
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}

		this.feedbackLabel.setForeground(Color.green);
		this.feedbackLabel.setText("EXIF export successfull!");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.importButton))
			getSelectedFiles();

		else if (e.getSource().equals(this.exportDataButton))
			this.exportData();
	}
}