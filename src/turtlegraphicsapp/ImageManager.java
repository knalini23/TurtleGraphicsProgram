package turtlegraphicsapp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ImageManager {
    private final TurtleGraphics turtleGraphics;
    private final List<String> recentImages = new ArrayList<>();
    private boolean unsavedChanges = false;
    private File currentImageFile = null;

    public ImageManager(TurtleGraphics turtleGraphics) {
        this.turtleGraphics = turtleGraphics;
    }

    public void saveImage() {
        if (currentImageFile != null) {
            writeImageToFile(currentImageFile);
        } else {
            saveImageAs(); // If no previous file, prompt for location
        }
    }

    public void saveImageAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Save Image As");

        while (true) {
            int userSelection = fileChooser.showSaveDialog(turtleGraphics.getMainFrame());

            if (userSelection != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            if (!path.endsWith(".png") && !path.endsWith(".jpg")) {
                selectedFile = new File(path + ".png");
            }

            boolean isSameAsCurrent = currentImageFile != null &&
                    selectedFile.getAbsolutePath().equals(currentImageFile.getAbsolutePath());

            if (selectedFile.exists() && !isSameAsCurrent) {
                int choice = JOptionPane.showConfirmDialog(
                        turtleGraphics.getMainFrame(),
                        "This file already exists and is not the currently loaded image.\n" +
                                "Saving will overwrite its contents. Do you want to proceed?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice != JOptionPane.YES_OPTION) {
                    continue;
                }
            }

            currentImageFile = selectedFile;
            writeImageToFile(currentImageFile);
            break;
        }
    }


    private void writeImageToFile(File file) {
        try {
            BufferedImage image = turtleGraphics.getBufferedImage();
            String format = file.getName().toLowerCase().endsWith(".jpg") ? "jpg" : "png";
            ImageIO.write(image, format, file);
            markImageAsSaved();
            addToRecentImages(file.getAbsolutePath());
            turtleGraphics.displayMessage("Image saved successfully.");
            turtleGraphics.getToolbar().updateRecentImages(getRecentImages());
        } catch (IOException e) {
            e.printStackTrace(System.err);
            turtleGraphics.displayMessage("Failed to save image.");
        }
    }

    public void loadImageWithPrompt() {
        if (hasUnsavedChanges()) {
            int result = JOptionPane.showConfirmDialog(
                    turtleGraphics.getMainFrame(),
                    "You have unsaved image data. Save before loading?",
                    "Unsaved Image",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            if (result == JOptionPane.CANCEL_OPTION) return;
            if (result == JOptionPane.YES_OPTION) saveImage();
        }

        String[] options = (currentImageFile != null)
                ? new String[]{"Choose File...", "Load recent File"}
                : new String[]{"Choose File..."};

        int choice = JOptionPane.showOptionDialog(
                turtleGraphics.getMainFrame(),
                "Choose how to load the image:",
                "Load Image",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Image");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int userSelection = fileChooser.showOpenDialog(turtleGraphics.getMainFrame());
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                currentImageFile = selectedFile;
                loadImageFromFile(selectedFile);
            }
        } else if (choice == 1 && currentImageFile != null) {
            loadImageFromFile(currentImageFile);
        }

    }

    public void loadImageFromFile(File file) {
        try {
            BufferedImage loadedImage = ImageIO.read(file);
            if (loadedImage != null) {
                turtleGraphics.setBufferedImage(loadedImage);
                markImageAsSaved();
                addToRecentImages(file.getAbsolutePath());
                turtleGraphics.repaint();
                turtleGraphics.displayMessage("Image loaded successfully.");
            } else {
                turtleGraphics.displayMessage("Invalid image file.");
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            turtleGraphics.displayMessage("Failed to load image.");
        }
    }

    private void addToRecentImages(String path) {
        if (!recentImages.contains(path)) {
            recentImages.add(path);
        }
    }

    public List<String> getRecentImages() {
        return new ArrayList<>(recentImages);
    }

    public void markImageAsUnsaved() {
        unsavedChanges = true;
    }

    public void markImageAsSaved() {
        unsavedChanges = false;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }
}
