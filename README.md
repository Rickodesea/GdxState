# GdxState
A helper library for managing game states in a libgdx project

1. gdxstate.jar: The library
2. gdxstate-doc.zip: java-doc

## Library Source
GdxState.java, GdxStateManager.java, GdxStateRenderer.java, GdxStateDescriptor.java, AssetLabel.java, Component.java, Debug.java, Pojo.java

## How to Set it up in an ApplicationListener class
Demo-Game.java, Demo2-Game.java

## Convenient State Class
Demo-GameState.java

## Functions
1. Allows you to assign short labels to refer to assets.  In libGdx, you refer to assets using only filepath.
2. Allows you to run as many states you want on an array stack.
3. Allows you to switch from one state to another or cancel a running state with one call.
4. Debug output to see manipulations behind the scenes.
5. Attach assets to each states - more than one state can share an asset and asynchronous loading of assets
6. Create components - data shared by all states.
7. Automatically disposes disposable components.
8. Access to state's assets' data: progress, done
9. Pause rendering on external interruption.  In libGdx, even though pause is called, rendering continues
10. Pass data to states when you run them
11. Set time limit for states. E.G. you can tell a state to run for 5seconds and after five seconds it is cancelled by the renderer
