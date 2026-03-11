package com.exif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javaxt.io.Directory;
import javaxt.io.Image;

public final class LeftPanel extends JPanel implements ActionListener {
	private final RightPanel rightPanel;

	private final JLabel promptLabel;
	private final JTextField exportNameInput;

	private final JCheckBox displayImagesCheckBox;
	private final JProgressBar progressBar;

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

		this.displayImagesCheckBox = new JCheckBox();
		this.displayImagesCheckBox.setText("Preview images? (On the right side)");

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setPreferredSize(new Dimension(350, 20));

		this.feedbackLabel = new JLabel();
		this.feedbackLabel.setPreferredSize(new Dimension(400, 40));
		this.feedbackLabel.setHorizontalAlignment(JLabel.CENTER);
		this.feedbackLabel.setVerticalAlignment(JLabel.CENTER);
		this.feedbackLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));

		this.feedbackArea = new JTextArea();
		this.feedbackArea.setEditable(false);
		this.feedbackArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10));

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
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileChooser.setAcceptAllFileFilterUsed(false);
		this.fileChooser.setMultiSelectionEnabled(false);
		this.fileChooser.setDialogTitle("Select directory...");

		this.fileSaver = new JFileChooser();
		this.fileSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileSaver.setAcceptAllFileFilterUsed(false);
		this.fileSaver.setMultiSelectionEnabled(false);
		this.fileSaver.setDialogTitle("Select directory...");

		this.setPreferredSize(new Dimension(400, 600));
		this.setLayout(new FlowLayout());
		this.add(this.promptLabel);
		this.add(this.exportNameInput);
		this.add(this.importButton);
		this.add(this.exportDataButton);
		this.add(this.displayImagesCheckBox);
		this.add(this.progressBar);
		this.add(this.feedbackLabel);
		this.add(this.feedbackScrollPane);
	}
	

	private boolean filterFile(final File file) {
		return file.getName().endsWith(".png") || 
			file.getName().endsWith(".jpg") || 
			file.getName().endsWith(".jpeg") || 
			file.getName().endsWith(".heic") || 
			file.getName().endsWith(".tiff");

	}

	private void getInputDirectory() {
		this.feedbackLabel.setForeground(Color.green);
		this.feedbackLabel.setText("Importing...");

		int result = this.fileChooser.showOpenDialog(null);

		if (result != JFileChooser.APPROVE_OPTION) {
			this.feedbackLabel.setText("");
			return;
		}

		File directory = this.fileChooser.getSelectedFile();

		if (directory == null) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("Invalid directory!");
			return;
		}

		this.rightPanel.setRootDirectory(directory, this.displayImagesCheckBox.isSelected());

		this.feedbackLabel.setForeground(Color.green);
		this.feedbackLabel.setText("Images loaded successfully!");
	}

	private File getOutputDirectory() {
		int result = this.fileSaver.showSaveDialog(null);

		if (result != JFileChooser.APPROVE_OPTION)
			return null;

		return this.fileSaver.getSelectedFile();
	}

	private void exportData() {
		if (this.rightPanel.getRootDirectory() == null) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("No input directory!");
			return;
		}

		this.feedbackLabel.setForeground(Color.green);
		this.feedbackLabel.setText("Exporting...");

		File outputDirectory = this.getOutputDirectory();
		String outputFileName = this.exportNameInput.getText();

		if (outputDirectory == null || outputFileName == null || outputFileName.isBlank()) {
			this.feedbackLabel.setForeground(Color.red);
			this.feedbackLabel.setText("No output file or directory!");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(outputDirectory.getAbsolutePath())
			.append("\\")
			.append(outputFileName)
			.append(".csv");

		try (FileWriter writer = new FileWriter(new File(stringBuilder.toString()))) {
			File[] inputFiles = this.rightPanel.getRootDirectory().listFiles();
			Image image;

			for (File inputFile : inputFiles) {
				if (!this.filterFile(inputFile))
					continue;

				image = new Image(inputFile.getAbsolutePath());
				double[] coordinates = image.getGPSCoordinate();
				String date = image.getGPSDatum();

				writer.write(inputFile.getName());
				writer.write(";");

				if (coordinates == null) {
					this.feedbackArea.setText(feedbackArea.getText() + inputFile.getName() + " NO_DATA\n");
					writer.write(";;");
					writer.write(date == null ? "" : date);
					writer.write("\n");
					continue;
				}

				for (int i = coordinates.length - 1; i >= 0; i--) {
					writer.write(String.valueOf(coordinates[i]));
					writer.write(";");
				}

				writer.write(date == null ? "" : date);
				writer.write("\n");
			}

			this.feedbackLabel.setForeground(Color.green);
			this.feedbackLabel.setText("EXIF export succesfull!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.importButton))
			getInputDirectory();

		else if (e.getSource().equals(this.exportDataButton))
			this.exportData();
	}
}