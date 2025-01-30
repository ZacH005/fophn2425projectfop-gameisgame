
# 🎮 Semester Project: Top-Down Maze Runner (2D Game)

A **thrilling** top-down **maze runner** game built using the **LibGDX framework** and programmed in **Java**.

## 🚀 How to Run the Game
1. **Clone the repository**:
   ```sh
   git clone https://go39pen@artemis.tum.de/git/FOPHN2425PROJECTFOP/fophn2425projectfop-gameisgame.git
2. **Open the project in any Java IDE (e.g., IntelliJ IDEA).**
3. **If you are on Windows or Linux, please remove the -XstartOnFirstThread VM option in the run configuration:**
## 🕹️ How to Play
### **Step 1: Objective**
- Collect all **gems** and reach the **exit gate** to complete the level.
- Avoid **enemies**, **toxic water**, and **skullbombs**—they will hurt you!
### **Step 2: Instructions**
- **Explore the maze** while collecting items.
- **Avoid or defeat enemies** by attacking them.
- **Use power-ups** (e.g., Golden Axe to break walls).
- **Find the exit gate** after collecting all necessary items.
### **Step 3: Controls**
- **Arrow Keys / WASD** → Move **left, right, up, down**.
- **Spacebar / Left Mouse Click** → Attack.

### **Step 4: Game Mechanics**
- Collect a **Golden Axe** to break cracked walls.
- Avoid **toxic water** as it damages you.
- Watch out for **skull-bombs**—they explode!
## 🛠️ Tools & Technologies Used
1. **LibGDX Framework** → Game engine for rendering, physics, and input handling.
2. **IntelliJ IDEA** → Java IDE used for development.
3. **Tiled Level Editor** → Software for designing and creating game levels.
4. **Universal LPC Spritesheet Generator** → Tool for generating character sprites.
5. **Particle Effects by Raeleus & Crykn** → Used for creating visual effects.
6. **Fruity Loops Studio 20** → Software for composing background music and sound effects.
7. **Piskel Sprite Editor** → Pixel art tool used for creating sprites.

## 📂 Project Structure 

```

├───.gradle
├───.idea
│   ├───libraries
│   ├───modules
│   ├───runConfigurations
│   ├───shelf (Uncommitted changes)
├───assets
│   ├───animations
│   ├───craft
│   ├───icons
│   ├───music
│   ├───particles
│   ├───shaders
│   ├───templateAssets
│   └───TiledMaps
├───core
│   ├───build
│   └───src
├───desktop
│   ├───build
│   ├───resources
│   └───src
```

---

## 🔎 Class Hierarchy
The game follows an **MVC-inspired architecture**, dividing logic into different layers:

### 📁 **Assets**
- Stores all **media files** (images, sounds, music, animations).

### 🏁 **Main Entry Point**
#### `DesktopLauncher` (Located in `de.tum.cit.fop.maze`)
- Initializes the game window.
- Starts the `ScreenManager`.

### 🎮 **Game Core**
#### 📌 **Managers**
- **Screen Manager** → Handles different game screens (Main Menu, Gameplay, Game Over).
- **Sound Manager** → Controls music and sound effects.
- **Map Manager** → Handles level design and collision detection.

#### 🖥️ **Screens**
- Contains the main UI views and game logic.

#### 🦸 **Entity System**
- Defines **player, enemies, and NPCs**.
- Stores object-related data and behaviors.

#### ✨ **Abilities**
- Manages **collectibles and power-ups**.
- Implements **spawn & despawn mechanics** for items.

### 📝 UML Diagram:
![UML Diagram](./srcFinal.png)

---

## 🎯 Extra Features
✅ **Vertical Reorchestration**<br>
✅ **Projectile-Throwing Enemies**<br>
✅ **Pathfinding & Entity Automation**<br>
✅ **Offline File Handling**<br>
✅ **Static & Interactive Particle Systems**<br>
✅ **Post-Dispose Interactivity**<br>
✅ **Dual-Nature Collision System**<br>
✅ **Custom Animations for Entities**<br>
✅ **Unique Sound Effects & Music**<br>
✅ **Multi-Layered Batched UI System**

---

