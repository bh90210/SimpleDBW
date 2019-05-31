# SimpleDBW
A very simple to use persistente storage (key/value) wrapper around [Badger](https://github.com/dgraph-io/badger) for Android.

### Download

#### ARR download
You can download the latest ARR release [here](https://github.com/bh90210/SimpleDBW/releases). Then you have to manually import the library (```simpledbw.arr```) in your Android project.

### Usage

#### initialisation 

The library uses lifecycle-extensions. In order for the database to work you have to include the dependacy in your module's build.gradle.

```implementation "androidx.lifecycle:lifecycle-extensions:2.0.0"```

Then in your module's main activity onCreate function simple add this line

```ProcessLifecycleOwner.get().getLifecycle().addObserver(new DBWhelper());```

ie.
```
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DB init
        // Add Lifecycle Observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DBWhelper());
```

#### Set value

```Database db = new Database();```

The database uses byte[] for both keys and values.

```db.Update("key".getBytes(), "value".getBytes());```

#### Get value

A db.View query returns a byte[] with the requested value. If key does not exist it returns "key does not exist".
```byte[] returnedvalue = db.View("".getBytes());```

That can be for example converted to string and used with TextView like so
```String text = new String(returnedvalue);```
```exampleTextView.setText(text);```

#### Delete key

```db.Delete("key".getBytes());```

#### Iterate

* Prefix

You can iterated over the databese quering a specific prefix

```ArrayMap<byte[], byte[]> tables =  db.ViewPrefix(prefix.getText().toString().getBytes());```

and you get an ArrayMap with all the associated keys and values. Please check Android's [documentation](https://developer.android.com/reference/android/support/v4/util/ArrayMap) for more information and usage.
                

* Dump

```ArrayMap<byte[], byte[]> tables =  db.DumpAll();```
                

#### Monotonically increasing integers
```long returnedInt = db.Mii(seed.getText().toString().getBytes());```

```
TextView resetCount = findViewById(R.id.returedint);
resetCount.setText(String.valueOf(returnedInt));
```

Reseting the count by deleting the key
```db.Delete(seed.getText().toString().getBytes());```

#### Drop all
```db.DropDB();```

#### Pre-populated entries
```db.PrePopulate(("PREFIX_sample").getBytes(), ("sample_value").getBytes());```

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
