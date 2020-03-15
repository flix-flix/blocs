# Blocs

**RTS game with blocs** *(Real-Time Strategy)*


## Author

**Félix B**

## Disclaimer

**This project is a "Work In Progress"**

One of its purposes is to display the things i'm able to do

But I code it for pleasure, so :

* Some commits are "ugly"/tote since I mainly use git for backup and history
* Some parts of code are non optimized, contains errors or have similar functionalities to existant Java class... (they are: waiting for next iterations to be improved, done quick to test other parts, preferring the reduction of coding time rather than execution time...) 
* Some parts may miss comments
* Some packages needs to be reorganized

## Maven

*Require Java 8*

```bash
mvn package
java -jar target/Blocs-0.0.1-SNAPSHOT.jar
```

## Available features

**Game**

* Unit management (move, harvest/store and build)
* Map-edition 
* Chat
* **/!\** There is currently no gameplay available (fight, objectives, ...)

**Server**

* Multiplayer
* LAN
* **/!\** Joining player may encouter an issue if actions are performed during its connection
* **/!\** Server requests must be improved to prevent cheat

**Editors**

* Cubes textures
* Multi-cubes structures
* Data (GUI for YAML files)
* Texture-packs manager

**Main Menu**

* Language selection
* Key manager

## Description

**src/readMe.md** contains a short description of the contents of the packages

To see some "standalone" code-parts:

* **src/data/map/unit/PathFinding** : Path finding algorithm
* **src/utils/TextPlus** : Representation of a text with different colors/fonts and containing images (used in **utils/panels/PanHelp**)

"Special mention" for the difference between debug() and debugBefore() in **src/utils/Utils** :-)
