# SimpleDBW
A simple to use persistente storage wrapper around [Badger](https://github.com/dgraph-io/badger) for Android.

### Download

#### AAR download
You can download the latest AAR release [here](https://github.com/bh90210/SimpleDBW/releases). Then you have to manually import the library (```simpledbw.aar```) in your Android project.

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

```
byte[] returnedvalue = db.View("key".getBytes());
```

This can be for example converted to string and used with TextView like so

```
String text = new String(returnedvalue);
exampleTextView.setText(text);
```

#### Delete key

```db.Delete("key".getBytes());```

#### Iterate

##### Prefix

Iterated over the database quering a specific prefix

```ArrayMap<byte[], byte[]> entries =  db.ViewPrefix("prefix".getBytes());```

and you get an ArrayMap with all the associated keys and values. Please check Android's [documentation](https://developer.android.com/reference/android/support/v4/util/ArrayMap) for more information and usage.

Delete all keys assosiated with spedific prefix.

```db.PrefixDrop("prefix".getBytes());```
                

##### Dump returns an ArrayMap with every entry in the database.

```ArrayMap<byte[], byte[]> entries =  db.DumpAll();```

it can be used for example to inflate a TableLayout

ie. 
```
ArrayMap<byte[], byte[]> tables =  db.DumpAll();
TableLayout dump = findViewById(R.id.ret_vals);
dump.removeAllViews();
Iterator it = tables.entrySet().iterator();

while (it.hasNext()) {
  ArrayMap.Entry pair = (ArrayMap.Entry)it.next();
  LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
  View row = inflater.inflate(R.layout.row, null);
                    dump.addView(row);
   //View rowID = row.findViewById(R.id.tableraw);
   TextView keyfield = row.findViewById(R.id.printkey);
   TextView valuefield = row.findViewById(R.id.printvalue);
   String key = new String((byte[]) pair.getKey());
   String value = new String((byte[]) pair.getValue());
   keyfield.setText(key);
   valuefield.setText(value);
    it.remove(); // avoids a ConcurrentModificationException
}
```         

#### Monotonically increasing integers

You can get monotonically increasing integers with strong durability

```long nextInt = db.Mii("monotonic_key".getBytes());```

Reset the count by deleting the key
```db.Delete("monotonic_key".getBytes());```

#### Drop all
```db.DropDB();```

#### Pre-populated entries

Place this on your module's main activity onCreate function. Please note if you drop the DB or delete the key the next time the app starts and opens the database these keys *will* regenerate.

```db.PrePopulate("PREFIX_sample".getBytes(), "sample_value".getBytes());```

ie. 
```
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DB init
        // Add Lifecycle Observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DBWhelper());
        // call the class
        Database db = new Database();
        // add some dummy pre-populated values
        db.PrePopulate("PREFIX_sample".getBytes(), "sample_value".getBytes());
        db.PrePopulate("PREFIX_dummy".getBytes(), "dummy_value".getBytes());
```

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
