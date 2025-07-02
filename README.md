# 🐢 TurtleGraphics - Java Drawing Application

A feature-rich Java-based drawing tool inspired by Logo programming and turtle graphics. This application allows users to control a turtle using commands to draw on a canvas, with additional functionalities like file saving/loading, fractals, custom shapes, and animated features.
GUI & Drawing powered by LBUGraphics.

📦 Setting up LBUGraphics.jar in IntelliJ IDEA
Create a lib/ folder in your project directory (if it doesn't already exist).
Place the downloaded LBUGraphics.jar inside the lib/ folder.
In IntelliJ IDEA:
Go to File → Project Structure → Modules → Dependencies
Click the ➕ icon → JARs or directories
Select lib/LBUGraphics.jar
Click OK and then Apply
---

## ✨ Features

- ✅ **Command-based Turtle Movement**
  - `about`,`forward`, `left`, `right`, `penup`, `pendown`, `reset`, `clear`
- 🎨 **Drawing Tools**
  - `color <name|r,g,b>`, `penwidth <size>`, `pencolour r,g,b`
- 🟡 **Color Panel (Bottom Toolbar)**
  - Quick-access color buttons
  - Select a custom color using color picker
  - Powered by `RoundButton.java`
- 🖼️ **Save & Load**
  - Save/load image (`PNG`, `JPG`)
  - Save/load command history (`.txt`)
- 💾 **Recent Files**
  - Dynamic recent command/image dropdowns
- 🔁 **Undo/Clear Support**
- 🔄 **Cycle Colours & Fun Commands**
  - `circle`, `cycleColours`, `dance`
- 🧩 **Custom Shapes**
  - `square <size>`, `triangle <s1>,<s2>,<s3>`
- 🌳 **Fractals (Animated)**
  - Fractal Tree
- 🧠 **Smart Command Handler**
  - Input parsing, validation, and modular command execution
- 🖥️ **GUI with Toolbar**
  - Intuitive interface with icon-based toolbar
  - Help popup for command instructions
  - color panel which help change pen color and choose custom colors
- ❗ **Exit Confirmation**
  - Prompt to save unsaved image/command before exiting

---

## 📁 Project Structure
TurtleGraphics/
├── MainClass.java # Entry point
├── TurtleGraphics.java # Main UI & controller (extends LBUGraphics)
├── ToolbarPanel.java # Toolbar with buttons & recent files
├── RoundButton.java # Implements color panel at the bottom
├── CommandHandler.java # Handles drawing logic and command parsing
├── CommandManager.java # Manages command history, saving/loading
├── ImageManager.java # Handles image I/O operations
├── Images/
└── README.md

## 🚀 How to Run
**Clone the repository**

```bash
git clone https://github.com/knalini23/TurtleGraphics.git
cd TurtleGraphics

Open in IDE (e.g., IntelliJ IDEA or Eclipse)
or
Compile manually:
java MainClass
Start Drawing!

Type commands in the input field

Click OK or press Enter


