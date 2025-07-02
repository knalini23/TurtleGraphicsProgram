package turtlegraphicsapp;

import uk.ac.leedsbeckett.oop.LBUGraphics;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class CommandHandler {
    // Dependencies needed to manipulate turtle graphics and manage state
    private final LBUGraphics turtle;
    private final CommandManager commandManager;
    private final ImageManager imageManager;
    private final List<CommandState> drawHistory = new ArrayList<>();// Keeps track of drawing-related command history
    private boolean firstDrawingCommandDone = false;



    // Constructor to initialize core components
    public CommandHandler(LBUGraphics turtle,CommandManager commandManager, ImageManager imageManager) {
        this.turtle = turtle;
        this.commandManager = commandManager;
        this.imageManager = imageManager;
    }

    private static class CommandState {
        String command;
        Color penColor;

        CommandState(String command, Color penColor) {
            this.command = command;
            this.penColor = penColor;
        }
    }


    // executes a user command
    public boolean handleCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        String mainCommand = parts[0].toLowerCase();
        String parameter = parts.length > 1 ? parts[1] : "";
        boolean isValid = true;
        boolean isDrawingCommand = false;

        Color currentPenColor = turtle.getPenColour();

        switch (mainCommand) {
            case "about" -> {
                turtle.about();
                turtle.displayMessage("Nalini Karna");
            }
            case "penup" -> turtle.drawOff();
            case "pendown" -> turtle.drawOn();
            case "left", "right" -> {
                isValid = handleTurn(mainCommand, parameter);
                isDrawingCommand = isValid;
            }
            case "forward", "move" -> {
                isValid = handleMove(parameter, false);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;
            }
            case "reverse" -> {
                isValid = handleMove(parameter, true);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;

            }
            case "reset" -> { turtle.reset(); turtle.drawOn(); isDrawingCommand = isValid; }
            case "clear" -> clearAndMoveToLeft();

            case "orange" -> {
                turtle.setPenColour(Color.ORANGE);
                isDrawingCommand = isValid;
            }
            case "green" -> {
                turtle.setPenColour(Color.GREEN);
                isDrawingCommand = isValid;
            }
            case "red" -> {
                turtle.setPenColour(Color.RED);
                isDrawingCommand = isValid;
            }
            case "white" -> {
                turtle.setPenColour(Color.WHITE);
                isDrawingCommand = isValid;
            }
            case "pink" -> {
                turtle.setPenColour(Color.PINK);
                isDrawingCommand = isValid;
            }
            case "cyan" -> {
                turtle.setPenColour(Color.CYAN);
                isDrawingCommand = isValid;
            }
            case "cyclecolours" -> turtle.cycleColours();
            case "dance" -> {
                isValid = handleDance(parameter);
                isDrawingCommand = isValid;
            }

            case "circle" -> {
                isValid = handleCircle(parameter);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;
            }
            case "square" -> {
                isValid = handleSquare(parameter);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;
            }
            case "pen" -> {
                isValid = handlePenRGB(parts);
                isDrawingCommand = isValid;
            }
            case "penwidth" -> isValid = handlePenWidth(parameter);
            case "triangle" -> {
                isValid = handleTriangle(parts);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;
            }

            case "fractal" -> {
                isValid = handleFractal(parts);
                if (isValid) {
                    drawHistory.add(new CommandState(command, currentPenColor));
                }
                isDrawingCommand = isValid;
            }

            case "save" -> isValid = handleSave(parameter);
            case "load" -> isValid = handleLoad(parameter);

            default -> {
                isValid = false;
                turtle.displayMessage("Unknown command: " + command);
            }

        }

        if (isValid) {
            if (!mainCommand.equals("about")) {
                turtle.displayMessage("Last command: " + command);
            }
        }

        // If it's a drawing command, mark image as unsaved
        if (isDrawingCommand) {
            if (!firstDrawingCommandDone) {
                turtle.setPenColour(Color.RED);
                turtle.setStroke(1);
                firstDrawingCommandDone = true;
            }

            if (turtle instanceof TurtleGraphics tg) {
                tg.getImageManager().markImageAsUnsaved();
            }
        }
        return isValid;
    }

    // Clears canvas and repositions turtle to the left-center
    private void clearAndMoveToLeft() {
        turtle.clear();
        turtle.setxPos(30);
        turtle.setyPos(turtle.getHeight() / 2);
        turtle.pointTurtle(180);
        turtle.drawOn();}

    // Handles left/right turning
    private boolean handleTurn(String direction, String parameter) {
        try {
            int degrees = parameter.isEmpty() ? 90 : Integer.parseInt(parameter);
            if (degrees < 0 || degrees > 360) {
                turtle.displayMessage("Turn between 0 and 360 degree.");
                return false;
            }

            if (direction.equals("left")) {
                turtle.right(degrees);
            } else {
                turtle.left(degrees);
            }

            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Error: Degree must be a number");
            return false;
        }
    }

    // Handles forward/reverse movement
    private boolean handleMove(String parameter, boolean reverse) {
        if (parameter.isEmpty()) {
            turtle.displayMessage("Error: 'move or reverse' command requires a distance parameter.");
            return false;
        }

        try {
            int distance = Integer.parseInt(parameter);
            if (distance < 0) {
                turtle.displayMessage("Error: Distance must be a positive value.");
                return false;
            }

            // Calculate intended movement
            double radians = Math.toRadians(turtle.getDirection());
            int dx = (int) Math.round(Math.cos(radians) * distance);
            int dy = (int) Math.round(Math.sin(radians) * distance);

            int newX = turtle.getxPos() + (reverse ? -dx : dx);
            int newY = turtle.getyPos() + (reverse ? -dy : dy);

            int panelWidth = turtle.getWidth();
            int panelHeight = turtle.getHeight();

            // Prevent going out of bounds
            if (newX < 0 || newY < 0 || newX > panelWidth || newY > panelHeight) {
                turtle.displayMessage("Move blocked: out of bounds.");
                return false;
            }
            turtle.forward(reverse ? -distance : distance);
            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Distance must be a number");
            return false;
        }
    }

    // Makes the turtle dance a specified number of times
    private boolean handleDance(String parameter) {
        try {
            int moves = Integer.parseInt(parameter);
            if (moves < 1 || moves > 100) {
                turtle.displayMessage("Dance moves must be between 1 and 100.");
                return false;
            }
            turtle.dance(moves);
            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Invalid dance parameter.");
            return false;
        }
    }

    private boolean handleCircle(String parameter) {
        try {
            int radius = Integer.parseInt(parameter);
            if (radius < 1 || radius > 500) {
                turtle.displayMessage("Radius must be between 1 and 500.");
                return false;
            }
            turtle.circle(radius);
            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Invalid circle radius.");
            return false;
        }
    }

    private boolean handleSquare(String parameter) {
        if (parameter.isEmpty()) {
            turtle.displayMessage("Error: 'square' command requires a length parameter.");
            return false;
        }

        try {
            int length = Integer.parseInt(parameter);
            if (length <= 0) {
                turtle.displayMessage("Error: Length must be positive.");
                return false;
            }

            int startX = turtle.getxPos();
            int startY = turtle.getyPos();
            int startDirection = turtle.getDirection();

            // Draw the square by moving forward and turning 90 degrees
            for (int i = 0; i < 4; i++) {
                turtle.forward(length);
                turtle.left(90);
            }

            // Return to the original position and direction
            turtle.setxPos(startX);
            turtle.setyPos(startY);
            turtle.pointTurtle(startDirection);// Reset to starting direction
            turtle.pointTurtle(180);
            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Error: Invalid length parameter.");
            return false;
        }
    }


    private boolean handlePenRGB(String [] parts) {
        if (parts.length != 4) {
            turtle.displayMessage("Invalid pen command. Usage: pen <0> <0> <0>");
            return false;
        }

        try {
            int r = Integer.parseInt(parts[1].trim());
            int g = Integer.parseInt(parts[2].trim());
            int b = Integer.parseInt(parts[3].trim());

            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                throw new IllegalArgumentException();
            }

            turtle.setPenColour(new Color(r, g, b));
            return true;
        } catch (Exception e) {
            turtle.displayMessage("Invalid RGB values. Use numbers 0–255.");
            return false;
        }
    }

    private boolean handlePenWidth(String parameter) {
        try {
            int width = Integer.parseInt(parameter.trim());
            if (width < 1 || width > 50) {
                turtle.displayMessage("Pen width must be between 1 and 50.");
                return false;
            }
            turtle.setStroke(width);
            return true;
        } catch (NumberFormatException e) {
            turtle.displayMessage("Invalid pen width.");
            return false;
        }
    }

    private boolean handleTriangle(String[] parts) {
        if (parts.length == 2) {
            // Single size -> Equilateral triangle
            try {
                int size = Integer.parseInt(parts[1].trim());

                for (int i = 0; i < 3; i++) {
                    turtle.forward(size);
                    turtle.left(120);
                }
                return true;
            } catch (NumberFormatException e) {
                turtle.displayMessage("Invalid number format for triangle size.");
                return false;
            }
        } else if (parts.length == 4) {
            // for three sides parameter
            try {
                int a = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                int c = Integer.parseInt(parts[3].trim());

                double angleA = Math.acos((b * b + c * c - a * a) / (2.0 * b * c));
                double angleB = Math.acos((a * a + c * c - b * b) / (2.0 * a * c));
                double angleC = Math.PI - angleA - angleB;

                int degA = (int) Math.round(Math.toDegrees(angleA));
                int degB = (int) Math.round(Math.toDegrees(angleB));
                int degC = (int) Math.round(Math.toDegrees(angleC));

                turtle.forward(a);
                turtle.left(180 - degC);
                turtle.forward(b);
                turtle.left(180 - degA);
                turtle.forward(c);
                turtle.left(180-degB);

                return true;
            } catch (NumberFormatException e) {
                turtle.displayMessage("Invalid number format in triangle sides.");
                return false;
            } catch (Exception e) {
                turtle.displayMessage("Error in triangle parameters.");
                return false;
            }
        } else {
            turtle.displayMessage("Invalid triangle command. Usage: triangle <size> or triangle <a> <b> <c>");
            return false;
        }
    }

    private Color getGradientColor(int index) {
        float hue = (float) index / 10;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    private void pause() {
        try {
            Thread.sleep(5); // Adjust delay to control animation speed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean handleFractal(String[] parts) {
        if (parts.length < 3) {
            turtle.displayMessage("Usage: fractal tree <depth>");
            return false;
        }

        String type = parts[1].toLowerCase();
        int depth;
        try {
            depth = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            turtle.displayMessage("Fractal depth must be a number.");
            return false;
        }

        turtle.setTurtleSpeed(10);

        if (type.equals("tree")) {
            drawFractalTree(depth, 60);
        } else {
            turtle.displayMessage("Unknown fractal type. Use: tree");
            return false;
        }
        turtle.setTurtleSpeed(5);
        return true;
    }


    private void drawFractalTree(int depth, double length) {
        if (depth == 0) return;

        Color color = getGradientColor(depth);
        turtle.setPenColour(color);

        turtle.forward((int) length);
        pause();

        turtle.left(30);
        drawFractalTree(depth - 1, length * 0.7);
        turtle.right(30);

        turtle.right(30);
        drawFractalTree(depth - 1, length * 0.7);
        turtle.left(30);

        turtle.forward((int) -length);
        pause();
    }

    private boolean handleSave(String parameter) {
        if (parameter.isEmpty()) {
            turtle.displayMessage("Error: Save what? Usage: save image or save commands");
            return false;
        }

        switch (parameter.toLowerCase()) {
            case "image" -> {
                imageManager.saveImage();
                return true;
            }
            case "commands" -> {
                commandManager.saveCommands();
                return true;
            }
            default -> {
                turtle.displayMessage("Unknown save option. Use: save image OR save commands");
                return false;
            }
        }
    }

    private boolean handleLoad(String parameter) {
        if (parameter.isEmpty()) {
            turtle.displayMessage("Error: Load what? Usage: load image or load commands");
            return false;
        }

        switch (parameter.toLowerCase()) {
            case "image" -> {
                imageManager.loadImageWithPrompt();
                return true;
            }
            case "commands" -> {
                commandManager.loadCommands();
                return true;
            }
            default -> {
                turtle.displayMessage("Unknown load option. Use: load image OR load commands");
                return false;
            }
        }
    }

    public void undoLastDrawCommand() {
        if (drawHistory.isEmpty()) return;

        drawHistory.remove(drawHistory.size() - 1);
        turtle.clear();
        turtle.reset();
        turtle.drawOn();

        firstDrawingCommandDone = false;

        // Reapply the last valid pen color after clearing the canvas
        if (!drawHistory.isEmpty()) {
            CommandState lastCommandState = drawHistory.get(drawHistory.size() - 1);
            turtle.setPenColour(lastCommandState.penColor);
        }
        List<String> drawHistoryCopy = new ArrayList<>();
        for (CommandState commandState : drawHistory) {
            drawHistoryCopy.add(commandState.command); // Add only the command part
        }

        // Execute each command again from the history
        for (String cmd : drawHistoryCopy) {
            executeCommand(cmd, false);
        }
    }

    public boolean executeCommand(String command, boolean com) {
        boolean valid = handleCommand(command);
        if (valid && com && turtle instanceof TurtleGraphics tg) {
            tg.getCommandManager().addCommand(command);
        }
        return valid;
    }
}