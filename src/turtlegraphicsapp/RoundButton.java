package turtlegraphicsapp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// A custom JButton subclass that draws a round, colored button with no text.
public class RoundButton extends JButton {
    private final Color fillColor;

    public RoundButton(Color color, String command, ActionListener commandHandler) {
        this.fillColor = color;
        setPreferredSize(new Dimension(40, 40));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setToolTipText(command);// Set a tooltip (shown on hover) using the command name

        addActionListener(commandHandler);
    }

    // Paint the button's background (a filled circle)
    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(fillColor.darker());
        } else {
            g.setColor(fillColor);
        }
        g.fillOval(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // Paint the circular border around the button
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
    }

    // Override the hitbox to match the circular shape of the button
    @Override
    public boolean contains(int x, int y) {
        int radius = getWidth() / 2;
        // Check if the (x, y) point lies inside the circle using the distance formula
        return (x - radius) * (x - radius) + (y - radius) * (y - radius) <= radius * radius;
    }
}

