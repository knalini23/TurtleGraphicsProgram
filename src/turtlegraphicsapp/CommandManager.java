package turtlegraphicsapp;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final TurtleGraphics turtleGraphics;
    private final List<String> commandHistory = new ArrayList<>();
    private final List<String> recentCommandFiles = new ArrayList<>();
    private boolean unsavedCommands = false;

    public CommandManager(TurtleGraphics turtleGraphics) {
        this.turtleGraphics = turtleGraphics;
    }

    public void addCommand(String command) {
        String lower = command.toLowerCase().trim();
        if (!(lower.equals("save image") || lower.equals("save command") ||
                lower.equals("load image") || lower.equals("load command"))) {
            commandHistory.add(command);
            unsavedCommands = true;// Mark that there are unsaved command changes
        }

        turtleGraphics.updateCommandTextArea(commandHistory);// Update the JTextArea in the GUI to reflect the updated command list
    }

    public void saveCommands() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Save Commands to File");

        while (true) {
            int userSelection = fileChooser.showSaveDialog(turtleGraphics.getMainFrame());

            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // User cancelled the dialog
            }

            File fileToSave = fileChooser.getSelectedFile();

            // Ensure the file ends with .txt
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }

            if (fileToSave.exists() && !recentCommandFiles.contains(fileToSave.getAbsolutePath())) {
                int choice = JOptionPane.showConfirmDialog(turtleGraphics.getMainFrame(),
                        "This file already exists and was not loaded.\n" +
                                "Do you want to overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);

                if (choice != JOptionPane.YES_OPTION) {
                    continue; // Let user choose a different file
                }
            }

            // Save the commands to the file
            try (PrintWriter out = new PrintWriter(fileToSave)) {
                for (String cmd : commandHistory) {
                    out.println(cmd);
                }
                addToRecentFiles(fileToSave.getAbsolutePath());
                markCommandsAsSaved();
                turtleGraphics.getToolbar().updateRecentCommands(recentCommandFiles);
                turtleGraphics.displayMessage("Commands saved.");
            } catch (IOException e) {
                turtleGraphics.displayMessage("Failed to save commands.");
                e.printStackTrace(System.err);
            }

            break;
        }
    }


    public void loadCommands() {
        if (unsavedCommands) {
            int result = JOptionPane.showConfirmDialog(null,
                    "You have unsaved commands. Save before loading new file?",
                    "Unsaved Commands", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION) return;
            if (result == JOptionPane.YES_OPTION) saveCommands();
        }

        if (!recentCommandFiles.isEmpty()) {
            String recentPath = recentCommandFiles.get(recentCommandFiles.size() - 1);
            File recentFile = new File(recentPath);
            if (recentFile.exists()) {
                int choice = JOptionPane.showConfirmDialog(turtleGraphics.getMainFrame(),
                        "Load most recent file?\n" + recentPath,
                        "Load Recent File",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    loadAndExecuteFile(recentFile);
                    return;
                }
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Load Command File");
        int userSelection = fileChooser.showOpenDialog(turtleGraphics.getMainFrame());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadAndExecuteFile(selectedFile);
        }
    }

    public void loadAndExecuteFile(File file) {
        if (file == null || !file.exists()) {
            turtleGraphics.displayMessage("File not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String command;
            // Clear canvas and command history
            turtleGraphics.clear();
            commandHistory.clear();
            unsavedCommands = false;
            turtleGraphics.updateCommandTextArea(commandHistory); // Clear the JTextArea

            while ((command = reader.readLine()) != null) {
                command = command.trim();
                if (!command.isEmpty()) {
                    turtleGraphics.processCommand(command); // This updates both the list and JTextArea
                    System.out.println("Executed: " + command);
                }
            }
            turtleGraphics.updateCommandTextArea(commandHistory);
            markCommandsAsSaved();
            addToRecentFiles(file.getAbsolutePath());
            turtleGraphics.getToolbar().updateRecentCommands(recentCommandFiles);
            turtleGraphics.displayMessage("Commands loaded from file.");
        } catch (IOException e) {
            turtleGraphics.displayMessage("Failed to load commands.");
            e.printStackTrace(System.err);
        }
    }


    private void addToRecentFiles(String path) {
        path = path.trim();
        if (!recentCommandFiles.contains(path)) {
            recentCommandFiles.add(path);
        }
    }

    //return List of recent command file names.
    public List<String> getRecentCommandFiles() {
        return recentCommandFiles;
    }

    //return List of executed commands.
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    //Marks the command history as saved, indicating no unsaved changes.
    public void markCommandsAsSaved() {
        unsavedCommands = false;
    }

    //return true if there are unsaved commands, false otherwise.
    public boolean hasUnsavedCommands() {
        return unsavedCommands;
    }

    //Removes the most recently added command from the command history, if any.
    public void removeLastCommand() {
        if (!commandHistory.isEmpty()) {
            commandHistory.remove(commandHistory.size() - 1);
        }

    }

    public void clearAllCommands() {
        commandHistory.clear();
        unsavedCommands=true;
    }

}
