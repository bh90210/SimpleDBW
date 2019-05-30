package com.github.bh90210.sample;

import android.content.Context;
import android.os.Bundle;

import com.github.bh90210.simpledbw.DBWhelper;
import com.github.bh90210.simpledbw.Database;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

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
        db.PrePopulate(("PREFIX_sample").getBytes(), ("sample_value").getBytes());
        db.PrePopulate("PREFIX_dummy".getBytes(), "dummy_value".getBytes());

        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //SimpleDBW db = new SimpleDBW(); // it MUST NOT be used this way, explain in readme
        Button set = findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.key);
                EditText value = findViewById(R.id.value);
                db.Update(key.getText().toString().getBytes(), value.getText().toString().getBytes());
                Snackbar.make(view, "key/value pair set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button get = findViewById(R.id.get);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.keytolookup);
                TextView value = findViewById(R.id.lookupvalue);
                byte[] returnedvalue = db.View(key.getText().toString().getBytes());
                String text = new String(returnedvalue);
                value.setText(text);
            }
        });

        Button del = findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.keytodelete);
                db.Delete(key.getText().toString().getBytes());
                Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button drop = findViewById(R.id.drop);
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.DropDB();
                Snackbar.make(view, "Database dropped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button dump = findViewById(R.id.dump);
        dump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayMap<byte[], byte[]> tables =  db.DumpAll();
                TableLayout dump = findViewById(R.id.ret_vals);
                dump.removeAllViews();
                Iterator it = tables.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    ArrayMap.Entry pair = (ArrayMap.Entry)it.next();
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View row = inflater.inflate(R.layout.row, null);
                    dump.addView(row, i);
                    //View rowID = row.findViewById(R.id.tableraw);
                    TextView keyfield = row.findViewById(R.id.printkey);
                    TextView valuefield = row.findViewById(R.id.printvalue);
                    String key = new String((byte[]) pair.getKey());
                    String value = new String((byte[]) pair.getValue());
                    keyfield.setText(key);
                    valuefield.setText(value);
                    it.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                Snackbar.make(view, "Database dumped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                i = 0;
            }
        });

        Button prefixdump = findViewById(R.id.prefixdump);
        prefixdump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText prefix = findViewById(R.id.prefix);
                ArrayMap<byte[], byte[]> tables =  db.ViewPrefix(prefix.getText().toString().getBytes());
                TableLayout dump = findViewById(R.id.ret_vals);
                dump.removeAllViews();
                Iterator it = tables.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    ArrayMap.Entry pair = (ArrayMap.Entry)it.next();
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    View row = inflater.inflate(R.layout.row, null);
                    dump.addView(row, i);
                    //View rowID = row.findViewById(R.id.tableraw);
                    TextView keyfield = row.findViewById(R.id.printkey);
                    TextView valuefield = row.findViewById(R.id.printvalue);
                    String key = new String((byte[]) pair.getKey());
                    String value = new String((byte[]) pair.getValue());
                    keyfield.setText(key);
                    valuefield.setText(value);
                    it.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                Snackbar.make(view, "Prefix dumped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                i = 0;
            }
        });

        Button prefixdrop = findViewById(R.id.prefixdrop);
        prefixdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText prefix = findViewById(R.id.prefix);
                db.PrefixDrop(prefix.getText().toString().getBytes());
                Snackbar.make(view, "Prefix dropped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button delint = findViewById(R.id.delint);
        delint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText seed = findViewById(R.id.keytomii);
                db.Delete(seed.getText().toString().getBytes());
                TextView resetCount = findViewById(R.id.returedint);
                resetCount.setText("");
                Snackbar.make(view, "Coutner reset", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*
        Button backup = findViewById(R.id.backup);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDBW db = new SimpleDBW();
                db.backup(appdir);
            }
        });

        Button restore = findViewById(R.id.restore);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDBW db = new SimpleDBW();
                db.restore(appdir);
            }
        });
        */
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
