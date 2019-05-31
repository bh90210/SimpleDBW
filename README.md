# SimpleDBW
A very simple to use persistente storage (key/value) wrapper around [Badger](https://github.com/dgraph-io/badger) for Android.

### Download

#### Jitpack
WIP

#### Jcenter
WIP

#### ARR download
You can download the latest ARR release [here](https://github.com/bh90210/SimpleDBW/releases). Then you have to manually import the library (```simpledbw.arr```) in your Android project.

### Usage

#### initialisation 

The library uses lifecycle-extensions. In order for the database to work you have to include the dependacy in your module's build.gradle.

implementation "androidx.lifecycle:lifecycle-extensions:2.0.0"

Then in your module's main activity onCreate function simple add this line

ProcessLifecycleOwner.get().getLifecycle().addObserver(new DBWhelper());

#### Set value

#### Get value

#### Iterate

#### Monotonically increasing integers

#### Complete example

### Build Go source

Unfortunatly gomobile does not support gomodules at the moment so you have to work within $GOPATH.

First ensure you have gomobile and badger installed. For more information click [here](https://godoc.org/golang.org/x/mobile/cmd/gomobile) and [here](https://github.com/dgraph-io/badger).

* ```go get github.com/dgraph-io/badger/...```

and

* ```go get golang.org/x/mobile/cmd/gomobile```
* ```gomobile init```

Then

* run ```go get https://github.com/bh90210/SimpleDBW``` 
* and ```gomobile bind -o simpledbw/dbwrapper.aar -target=android $GOPATH/src/github.com/bh90210/SimpleDBW``` 
