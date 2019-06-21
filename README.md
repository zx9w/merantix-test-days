# merantix-test

Project done during test days at Merantix.

## Assignment

Build a canvas component in reagent that can load in a picture and has the following functionality:

 - When the user clicks on the canvas start tracking the mouse
 - While tracking draw a line on the canvas following mouse movement
 - When the user clicks again stop tracking and connect the two clicked points with a line

I signed an NDA so I can't say more than that.

## Feedback

Generally good but the clojurescript team was too small to support a junior and since my experience has primarily been with pure structures (i.e. the "functional core" of an application) or backend (side-effects like database and filesystem are more familiar to me than the browser DOM) it wasn't a good fit.

# Build tool boilerplate

## Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449) once Figwheel starts up. 

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).


### Optional development tools

Start the browser REPL:

```
$ lein repl
```
The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```


## Building for release

```
lein do clean, uberjar
```

