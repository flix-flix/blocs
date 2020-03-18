# Global description of the packages

## Main

**Create a new Frame** (window.Fen)

## window

**Frame of this project**

- **Fen** : Frame of the program
- **Displayable** : Interface allowing to display an "application" (getPanel(), resize(), stop()/resume())
- **Keyboard** : Interface handling the inputs (keyboard and mouse)

## data

**Representation of the data**

- **Items properties**
    - **Item** : Store the properties of an item
    - **ItemTable** : Static access to the properties of all items
    - **ItemTableClient** : Static access to the properties of all items (textures/language dependant inclued)

<br>

- **Organize the cubes**
    - **Cube** : Store the data of one cube (1x1x1)
    - **Chunk** : Store 10x50x10 cubes
    - **Map** : Store chunks

<br>

- **Additional data**
    - **Multibloc** : List of cubes acting together
    - **Unit**
    - **Building**
    - **Resource**

<br>

- **Dynamic**
    - **TickClock** : 20 times/second (each 50ms) make the data perform a step of their current action
    - **PathFinding**

<br>

- **Generation**
    - **WorldGeneration** : Generates map

## graphicEngine

**Homemade graphic engine (need a lot of optimization but do the job...)**

- **Engine** : Core
- **Line/Quadri** : 2D objects to draw
- **Point3D/Camera** : Data
- **Vector/Matrix** : Mathematical calculations
- **structures/** : Class/Interface to be extended/implemented by data to be used by this engine

## environment

**Store the 3D representation of the data**

- **textures/** : Store the textures of the cubes
- **extendsData/** : Store the texturized data
- **extendsEngine/** : Store the texturized data in the "graphic engine format"

**Handle the display and the modification of the data**

- **Environment3D** : Handle interractions between the graphic engine, the data and the display panel
- **PanEnvironment** : Display the graphic representation and the engine performances
- **KeyboardEnvironment** : Handle camera movements/rotation and interactions with cubes

## game

**Game interface**

- **Game** : (Environement3D) (Displayable) Handle the interactions between the user and the server
- **ServerListener** : Connect to server
- **panels/menus/** : Menu interface

## editor

**Tool to easily edit the textures**

- **EditorManager** : (Environement3D) (Displayable) Handle the interactions between the user and the editors
- **EditorCubeTexture, EditorMultiCubes** : Available editors
- **history/** : Store the user modifications and allow to undo/redo them

## server

**Application to support multiplayer**

- **Server** : Handle interactions between all the players and the data
- **send/** : Define possible client/server interactions
- **game/** : Intercept data modifications
- **model/** : Handle connexion

## dataManager

**Tool to easily edit the data**

- **DataManager** : (Displayable) Handle the interactions between the user and the data
- **lines/** : Display the properties of one item on one line

## mainMenu

**Panel giving access to the other applications**

- **MainMenu** : (Displayable) 
- **server/** : Panels for server management (start/stop/join)

## utils

**Utils that can be re-used for other projects**

- **Utils** : Regroup usefull comands (read, write, ...)
- **yaml/** : YAML parser/encoder
- **panels/** : Differents extends of JPanel (Buttons, PopUp, Grid, Tips display)
- **TextPlus** : Representation of a text with different colors/fonts and containing images

## utilsBlocks

**Utils for this project**
