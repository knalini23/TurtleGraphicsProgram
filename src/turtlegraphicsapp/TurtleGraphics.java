package turtlegraphicsapp;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphics extends LBUGraphics {
    private final CommandHandler commandHandler;
    private ToolbarPanel toolbar;
    private final CommandManager commandManager;
    private final ImageManager imageManager;
    private JFrame mainFrame;
    private JTextArea commandTextArea;

    public TurtleGraphics() {
        commandManager = new CommandManager(this);
        imageManager = new ImageManager(this);
        commandHandler = new CommandHandler(this, commandManager, imageManager);
        setupUI();
        drawOn();
        new Thread(this::listenForConsoleCommands, "ConsoleCommandThread").start();

        toolbar.updateRecentCommands(commandManager.getRecentCommandFiles());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void about() {
        super.about();
    }

    private void setupUI() {
        mainFrame = new JFrame("Turtle Graphics");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boolean unsavedImage = imageManager.hasUnsavedChanges();
                boolean unsavedCommands = commandManager.hasUnsavedCommands();

                if (unsavedImage || unsavedCommands) {
                    int option = JOptionPane.showConfirmDialog(
                            mainFrame,
                            "You have unsaved work. Do you want to save before exiting?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                        return; // User chose cancel — don't exit
                    }

                    if (option == JOptionPane.YES_OPTION) {
                        if (unsavedImage) imageManager.saveImage();
                        if (unsavedCommands) commandManager.saveCommands();
                    }
                    // If NO → do nothing and continue to exit
                }

                mainFrame.dispose();
                System.exit(0);
            }
        });

        this.setPreferredSize(new Dimension(800, 400));
        this.setMaximumSize(new Dimension(800, 400));
        this.setMinimumSize(new Dimension(800, 400));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(this, gbc);

        toolbar = new ToolbarPanel(new ToolbarActionListener(),
                imageManager.getRecentImages(),
                commandManager.getRecentCommandFiles());

        mainFrame.getContentPane().add(toolbar, BorderLayout.NORTH);
        mainFrame.getContentPane().add(wrapper);

        setupImageSelectionPanel();
        setupColorSelectionPanel();

        mainFrame.setSize(800, 450);
        mainFrame.setResizable(true);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }


    private JToggleButton createTurtleImageButton(String imageFile, List<JToggleButton> allButtons) {
        String ImageDirectory = "C:/Users/karna/IdeaProjects/oop-portfolio-2025-knalini23/AssignmentTurtleGraphicsProgram/src/turtlegraphicsapp/Images"; // Path to your local images folder
        String imagePath = new File(ImageDirectory, imageFile).getAbsolutePath();

        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JToggleButton button = new JToggleButton(scaledIcon);
        button.setPreferredSize(new Dimension(64, 64));
        button.setToolTipText("Set Turtle Image: " + imageFile);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        button.addActionListener(e -> {
            try {
                setTurtleImage(imagePath);
                repaint();
                displayMessage("Turtle image set to " + imageFile);
            } catch (Exception ex) {
                displayMessage("Error setting turtle image: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }

            // Update button border color on click
            for (JToggleButton b : allButtons) {
                b.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }
            button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        });

        return button;
    }

    private JButton createCustomColorButton() {
        // Create the custom color button (a round button with a color chooser dialog)
        JButton customColorButton = new RoundButton(Color.LIGHT_GRAY, "custom", e -> {
            Color chosen = JColorChooser.showDialog(mainFrame, "Select Pen Colour", Color.BLACK);
            if (chosen != null) {
                String cmd = String.format("pen %d %d %d", chosen.getRed(), chosen.getGreen(), chosen.getBlue());
                processCommand(cmd);
            }
        });

        customColorButton.setText("🎨"); // Emoji icon for the button
        return customColorButton;
    }

    private void setupColorSelectionPanel() {
        // Horizontal box layout for color buttons
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
        colorPanel.setBorder(null);
        colorPanel.setBackground(Color.DARK_GRAY);
        colorPanel.setMinimumSize(colorPanel.getPreferredSize());

        // Add color buttons with spacing
        colorPanel.add(new RoundButton(Color.RED, "red", e -> processCommand("red")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(new RoundButton(Color.GREEN, "green", e -> processCommand("green")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(new RoundButton(Color.ORANGE, "orange", e -> processCommand("orange")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(new RoundButton(Color.PINK, "pink", e -> processCommand("pink")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(new RoundButton(Color.CYAN, "cyan", e -> processCommand("cyan")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(new RoundButton(Color.WHITE, "white", e -> processCommand("white")));
        colorPanel.add(Box.createHorizontalStrut(10));
        colorPanel.add(createCustomColorButton());

        // Wrapper panel to center the colorPanel at bottom
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        wrapperPanel.add(colorPanel, gbc);

        // Add to the bottom of the main frame
        mainFrame.getContentPane().add(wrapperPanel, BorderLayout.SOUTH);
    }



    private void setupImageSelectionPanel() {
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setBorder(null);
        imagePanel.setBackground(Color.DARK_GRAY);
        imagePanel.setPreferredSize(new Dimension(80, 225));
        String[] imageFiles = {"dot.png", "orc.png", "ellipse.png", "turtle.png"};

        ButtonGroup buttonGroup = new ButtonGroup();
        List<JToggleButton> allButtons = new ArrayList<>();

        for (String imageFile : imageFiles) {
            JToggleButton button = createTurtleImageButton(imageFile, allButtons);
            buttonGroup.add(button);
            imagePanel.add(button);
            allButtons.add(button);
        }

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(Color.DARK_GRAY); // Match or customize
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        wrapperPanel.add(imagePanel, gbc);

        mainFrame.getContentPane().add(wrapperPanel, BorderLayout.WEST);
    }

    private JButton createUndoButton() {
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            commandTextArea.setText("");
            if (commandManager != null) {
                if (!commandManager.getCommandHistory().isEmpty()) {
                    commandManager.removeLastCommand();
                    commandHandler.undoLastDrawCommand();

                    updateCommandTextArea(commandManager.getCommandHistory());
                } else {
                    displayMessage("No commands to undo.");
                }
            }
        });
        return undoButton;
    }
    private JButton createClearAllButton() {
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> {
            if (commandManager != null) {
                commandManager.clearAllCommands();

                clear();
                reset();
                drawOn();

                updateCommandTextArea(commandManager.getCommandHistory());
            }
        });
        return clearAllButton;
    }

    private void setupCommandTextArea() {
        if (commandTextArea != null) return;

        commandTextArea = new JTextArea(10, 30);
        commandTextArea.setEditable(false);
        commandTextArea.setBorder(BorderFactory.createTitledBorder("Command History"));

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(commandTextArea, BorderLayout.CENTER);

        JButton undoButton = createUndoButton();
        JButton clearAllButton = createClearAllButton();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.add(undoButton);
        buttonPanel.add(clearAllButton);
        innerPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(innerPanel);

        mainFrame.getContentPane().add(scrollPane, BorderLayout.EAST);
        mainFrame.revalidate();
        mainFrame.repaint();
    }


    private void listenForConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Enter command: ");
                String command = scanner.nextLine();

                if (command.trim().equalsIgnoreCase("exit")) {
                    System.out.println("Exiting program...");
                    break;
                }

                processCommand(command);
            } catch (Exception e) {
                System.out.println("Error reading input. Exiting...");
                break;
            }
        }
        scanner.close();
    }

    public void updateCommandTextArea(List<String> commands) {
        setupCommandTextArea();
        StringBuilder builder = new StringBuilder();
        for (String command : commands) {
            builder.append(command).append("\n");
        }
        SwingUtilities.invokeLater(() -> commandTextArea.setText(builder.toString()));
    }

    @Override
    public void processCommand(String command) {
        boolean success = commandHandler.executeCommand(command, true);
        if(success){
            SwingUtilities.invokeLater(() -> updateCommandTextArea(commandManager.getCommandHistory()));
        }
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public ToolbarPanel getToolbar() {
        return toolbar;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private class ToolbarActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "Save Image" -> imageManager.saveImage();

                case "Load Image" -> imageManager.loadImageWithPrompt();

                case "Save Commands" -> commandManager.saveCommands();

                case "Load Commands" -> commandManager.loadCommands();

                default -> {
                    if (command.startsWith("load_recent_image:")) {
                        String imagePath = command.split(":", 2)[1];
                        imageManager.loadImageFromFile(new File(imagePath));
                    } else if (command.startsWith("load_recent_commands:")) {
                        String commandPath = command.split(":", 2)[1];
                        commandManager.loadAndExecuteFile(new File(commandPath));
                    }
                }
            }
        }
    }
}