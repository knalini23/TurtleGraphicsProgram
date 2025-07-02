package turtlegraphicsapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class ToolbarPanel extends JPanel {
    public JButton saveImageBtn, loadImageBtn, saveCommandsBtn, loadCommandsBtn, helpBtn;
    public JComboBox<String> recentImagesDropdown, recentCommandsDropdown;
    private boolean isUpdatingDropdowns = false;

    public ToolbarPanel(ActionListener actionListener, List<String> recentImages, List<String> recentCommands) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.LIGHT_GRAY);

        saveImageBtn = new JButton("💾");
        loadImageBtn = new JButton("🖼️");
        saveCommandsBtn = new JButton("📤");
        loadCommandsBtn = new JButton("📥");

        saveImageBtn.setToolTipText("Save Image");
        loadImageBtn.setToolTipText("Load Image");
        saveCommandsBtn.setToolTipText("Save Commands");
        loadCommandsBtn.setToolTipText("Load Commands");

        saveImageBtn.setActionCommand("Save Image");
        loadImageBtn.setActionCommand("Load Image");
        saveCommandsBtn.setActionCommand("Save Commands");
        loadCommandsBtn.setActionCommand("Load Commands");

        saveImageBtn.addActionListener(actionListener);
        loadImageBtn.addActionListener(actionListener);
        saveCommandsBtn.addActionListener(actionListener);
        loadCommandsBtn.addActionListener(actionListener);

        add(saveImageBtn);
        add(loadImageBtn);
        add(saveCommandsBtn);
        add(loadCommandsBtn);

        // Dropdowns for recent files
        recentImagesDropdown = new JComboBox<>(recentImages.toArray(new String[0]));
        recentCommandsDropdown = new JComboBox<>(recentCommands.toArray(new String[0]));
        recentImagesDropdown.setToolTipText("Recent Images");
        recentCommandsDropdown.setToolTipText("Recent Command Files");

        recentImagesDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String fileName = (value != null) ? new File((String)value).getName() : "";
                return super.getListCellRendererComponent(
                        list,
                        fileName,
                        index,
                        isSelected,
                        cellHasFocus
                );
            }
        });

        recentCommandsDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String fileName = (value != null) ? new File((String)value).getName() : "";
                return super.getListCellRendererComponent(
                        list,
                        fileName,
                        index,
                        isSelected,
                        cellHasFocus
                );
            }
        });

        Dimension dropdownSize = new Dimension(150, 25);
        recentImagesDropdown.setPreferredSize(dropdownSize);
        recentCommandsDropdown.setPreferredSize(dropdownSize);

        recentImagesDropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                recentImagesDropdown.setToolTipText((String) e.getItem());
            }
        });

        recentCommandsDropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                recentCommandsDropdown.setToolTipText((String) e.getItem());
            }
        });

        recentImagesDropdown.addActionListener(e -> {
            if (isUpdatingDropdowns) return;
            String selected = (String) recentImagesDropdown.getSelectedItem();
            if (selected != null && !selected.equals("No recent files")) {
                loadImageBtn.setActionCommand("load_recent_image:" + selected);
                loadImageBtn.doClick();
            }
        });

        recentCommandsDropdown.addActionListener(e -> {
            if (isUpdatingDropdowns) return;
            String selected = (String) recentCommandsDropdown.getSelectedItem();
            if (selected != null && !selected.equals("No recent files")) {
                loadCommandsBtn.setActionCommand("load_recent_commands:" + selected);
                loadCommandsBtn.doClick();
            }
        });

        add(new JLabel("Recent Images:"));
        add(recentImagesDropdown);
        add(new JLabel("Recent Commands:"));
        add(recentCommandsDropdown);

        add(Box.createHorizontalGlue());

        // Help button
        helpBtn = new JButton("🆘 Help");
        helpBtn.setToolTipText("View Help");
        helpBtn.addActionListener(e -> showHelpDialog());
        add(helpBtn);
    }

    public void updateRecentImages(List<String> recentImages) {
        isUpdatingDropdowns = true;
        recentImagesDropdown.removeAllItems();
        if (recentImages == null || recentImages.isEmpty()) {
            recentImagesDropdown.addItem("No recent files");
            recentImagesDropdown.setEnabled(false);
        } else {
            for (String item : recentImages) {
                recentImagesDropdown.addItem(item);
            }
            recentImagesDropdown.setEnabled(true);
        }
        recentImagesDropdown.setToolTipText("Recent Images");
    }

    public void updateRecentCommands(List<String> recentCommands) {
        isUpdatingDropdowns = true;
        recentCommandsDropdown.removeAllItems();
        if (recentCommands == null || recentCommands.isEmpty()) {
            recentCommandsDropdown.addItem("No recent files");
            recentCommandsDropdown.setEnabled(false);
        } else {
            for (String item : recentCommands) {
                recentCommandsDropdown.addItem(item);
            }
            recentCommandsDropdown.setEnabled(true);
        }
        recentCommandsDropdown.setToolTipText("Recent Command Files");
    }

    private void showHelpDialog() {
        String message = """
        🐢 TurtleGraphics Help
        
        🖼️Changing turtle skin: click from images on the left panel
        
        🖌️change pen color: click the color buttons below canvas
        
        ✏️ Commands:
        - forward/move <pixels> : Move forward
        - left <degrees> : Turn counter-clockwise
        - right <degrees> : Turn clockwise
        - penup : Stop drawing when moving
        - pendown : Resume drawing
        - pen 0 0 0 : Set RGB pen colour(Custom colour), range: (0-255)
        - penwidth <width> : Set thickness of lines
        - square <length> : Draw a square and return to original spot
        - triangle <size> : Equilateral triangle
        - triangle <s1>,<s2>,<s3> : Any triangle
        - circle <radius> : Draw a circle
        - dance : Fun animated movement
        - fractal tree/koch <depth> : Draw tree with branches/ draw snowflake
        - save : save image/commands
        - load : load image/commands
        
        🎨Color options:
        - Red  -Green  -White -Orange -Cyan -Pink
       
        🔄 Other:
        - reset : Clears drawing and resets pen settings
        - clear : Clears drawing but keeps current pen settings
        
        💾 Save/Load:
        - Use 💾 and 📤 to save your image and command history
        - Use 🖼️ and 📥 to load image or command file
        - Use the dropdowns to quickly load recent files

        ✅ Tips:
        - Always save your work before loading new files.
        - Use undo button to undo last command (also undoes drawing).
        - Use Clear All button to clear all commands and drawing.
        """;
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 550));

        JOptionPane.showMessageDialog(this, scrollPane, "TurtleGraphics Help", JOptionPane.INFORMATION_MESSAGE);
    }
}

