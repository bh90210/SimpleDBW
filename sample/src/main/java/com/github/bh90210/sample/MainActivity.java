package com.github.bh90210.sample;

import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import com.github.bh90210.simpledbw.DBWhelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import dbwrapper.SimpleDBW;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add Lifecycle Observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DBWhelper());

        SimpleDBW db = new SimpleDBW();
        db.prePopulate("SAMPLE_KEY", "sample_value");
        db.prePopulate("dummy", "dummy_value");

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
                SimpleDBW db = new SimpleDBW(); // this is the correct way, look into why tho
                db.update(String.valueOf(key.getText()), String.valueOf(value.getText()));
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
                SimpleDBW db = new SimpleDBW();
                String returnedvalue = db.view(String.valueOf(key.getText()));
                value.setText(returnedvalue);
            }
        });

        Button del = findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.keytodelete);
                SimpleDBW db = new SimpleDBW();
                db.delete(String.valueOf(key.getText()));
                Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button drop = findViewById(R.id.drop);
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDBW db = new SimpleDBW();
                db.dropAll();
                Snackbar.make(view, "Database dropped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button nextint = findViewById(R.id.nextint);
        nextint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText seed = findViewById(R.id.keytomii);
                TextView incredint = findViewById(R.id.returedint);
                SimpleDBW db = new SimpleDBW();
                long returnedInt = db.incInt(String.valueOf(seed.getText()));
                incredint.setText(String.valueOf(returnedInt));
                Snackbar.make(view, "Integer generated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button delint = findViewById(R.id.delint);
        delint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText seed = findViewById(R.id.keytomii);
                SimpleDBW db = new SimpleDBW();
                db.delete(String.valueOf(seed.getText()));
                TextView resetCount = findViewById(R.id.returedint);
                resetCount.setText("");
                Snackbar.make(view, "Coutner reset", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button ttlset = findViewById(R.id.ttlset);
        ttlset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.ttlkey);
                EditText value = findViewById(R.id.ttlvalue);
                SimpleDBW db = new SimpleDBW();
                db.update(String.valueOf(key.getText()), String.valueOf(value.getText()));

                // dialog
                Calendar mcurrentTime = null;
                mcurrentTime = Calendar.getInstance();
                int hour = 0;
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = 0;
                minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        Calendar that = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        if (calendar.getTimeInMillis() <= that.getTimeInMillis()) {
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH + 1));
                        }
                        long selected = calendar.getTimeInMillis();
                        long mili = selected - that.getTimeInMillis();
                        long secs = mili / 1000;
                        //long nanos = TimeUnit.MILLISECONDS.toNanos(mili);

                        // Format the current time. "2012-11-01T22:08:41+00:00"
                        //SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss+zz:zz");
                        //Calendar currentTime_1 = calendar;
                        //String dateString = formatter.format(currentTime_1);

                        SimpleDBW db = new SimpleDBW();
                        db.ttl(String.valueOf(key), String.valueOf(value), String.valueOf(secs));

                        final CountDownTimer countDownTimer = new CountDownTimer(mili, 1000) {
                            TextView retvalue = findViewById(R.id.countdown);

                            public void onTick(long scheinmil) {
                                long timeinseconds = scheinmil / 1000;
                                retvalue.setText(String.valueOf(timeinseconds));
                            }

                            public void onFinish() {
                                Snackbar.make(view, "TTL key expired", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                        }.start();

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });

        Button ttlget = findViewById(R.id.ttlget);
        ttlget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText key = findViewById(R.id.ttlkey);
                TextView retvalue = findViewById(R.id.ttlvalueret);
                SimpleDBW db = new SimpleDBW();
                String returnedTTL = db.view(String.valueOf(key.getText()));
                retvalue.setText(returnedTTL);
            }
        });
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
