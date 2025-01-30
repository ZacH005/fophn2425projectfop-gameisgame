
# Semester Project

A Top-Down Maze Runner, Thrill, 2D game made using libGdx framework and programmed completely in Java. 

## How to run the game
Clone the project and run it in any JAVA IDE e.g. IntelliJ IDEA
## How to play the game
When you reach the gate after you collect all gems, you win the level. If you run into an enemy , you will get hurt. THe enemies are running across the screen at random locations and random speeds. Use the arrow keys to move left, right, forward or backward or WASD. Use the Spacebar key or the left mouse button to attack. You can break a cracked wall after you collect a golden axe. The water is toxic and it will hurt you. There are skull-bombs, be careful not to hit them because they will hurt you too. 
# Tools used in this game
### 1. LIBGDX Framework
### 2. IntelliJ IDEA
### 3. Tiled Level Editor
### 4. Universal LPC Spritesheet Generator 
### 5. Particle Effects By Raeleus & Crykn
### 6. Fruity Loops Studio 20
### 7. Piskel Sprite Editor

# Project Structure 

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
# Class Hierarchy
The game follows an MVC-inspired structure with different layers:
## Assets
Contains the media used in the game
## Main Entry Point
#### DesktopLauncher (in de.tum.cit.fop.maze)
Initializes the game window and starts the ScreenManager.
## Game Core
### Managers
Manages different game screens (Main Menu, Gameplay, Game Over).

Manages different instrumental layers and sound effects.

Manages Maps and collision

### Screens
Contains the main views and logic.

### Entity
Contains the logic for the main characters and the enemies
Including the mechanisms to save the infromation related to the objects that are generated from the subclasses of the entity class.

### Abilities
Contains the logic for the Collectables and the PowerUps
Including the mechanisms to spawn and despawn the objects that are generated from the subclasses of the Collectable class.

![UML Diagram](srcFinal.png)

## EXTRA FEATURES
### 1. Vertical Reorchestration
### 2. Projectile-Thrower Entity
### 3. Pathfidning / Entity Automation
### 4. Offline File Handling
### 5. Static & Interactive Particle System Implementation
### 6. Post-Dispose Interactivity With Entities
### 7. Dual-Nature Collision System 
### 8. Customized Animations For Entities
### 9. Customized SFX & Music
### 10. Interactive Output Unit Control
### 11. Multi-Layered Batched UI
