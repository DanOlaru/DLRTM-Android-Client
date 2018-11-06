package longmoneyoffshore.dlrtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.US_ANONYMIZER_PREFIX;

public class AppSettings extends Activity {

    String anonPrefSetting = "";
    boolean scannerPrefSetting;
    boolean changedOccurred = false;
    EditText anonPref;
    Switch scannerPref;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        anonPref = (EditText) findViewById(R.id.anonymizer_pre_val);
        scannerPref = (Switch) findViewById(R.id.scanner_capability_switch);

        String TAG = "DLRTMPreferences";
        //File f = new File("/data/data/"+this.getPackageName() +"/shared_prefs/"+this.getPackageName()+"_preferences.xml");

        pref = getApplicationContext().getSharedPreferences("preferences.xml", 0); // 0 - for private mode
        editor = pref.edit();

        //read from pref file and set in window

        anonPrefSetting = pref.getString("anonymizer_prefix", "xxx"); // getting String
        //Log.d("ANON PREF SETTING", anonPrefSetting);

        scannerPrefSetting = pref.getBoolean("scanner_preference", true); // getting boolean
        //Log.d("SCANNER PREF", " " + scannerPrefSetting);

        anonPref.setText(anonPrefSetting);
        GLOBAL_ANONYMIZER_PREFIX = anonPrefSetting;

        scannerPref.setChecked(scannerPrefSetting);
        GLOBAL_SCANNER_SETTING = scannerPrefSetting;

        //Log.d(TAG, "the prefix is: " + anonPrefSetting + " and the scanner setting is " + GLOBAL_SCANNER_SETTING);

        /*
        if (f.exists()) {

            Log.d(TAG, "EXISTS " + "/data/data/"+this.getPackageName() +"/shared_prefs/"+this.getPackageName()+"_preferences.xml");

            try {
                FileReader fileRead = new FileReader(f);
                //fileRead.toString();


                BufferedReader bufferedReader = new BufferedReader(fileRead);

                String line = null;

                while((line = bufferedReader.readLine()) != null) {
                    Log.d(TAG, line + " \n");
                }
            }catch (IOException e) {Log.d(TAG, e.getLocalizedMessage());}

        }*/


        anonPref.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changedOccurred = true;

                //Log.d("CHANGE PREFIX", "TO " +  anonPref.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scannerPref.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changedOccurred = true;

                //Log.d("CHANGE SCANNER", " PREFERENCE TO " + scannerPref.isChecked());

            }
        });

        ImageButton savePrefButton = (ImageButton) findViewById(R.id.savePreferencesBtn);

        savePrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackIntent = new Intent(AppSettings.this, OrderListActivity.class);

                if (changedOccurred) {
                    storePrefs(editor);

                }
                //else Log.d("NO CHANGE OCCURRED", "SAVING CHANGE");

                Log.d("GLOBAL VALUES", "the prefix is: " + GLOBAL_ANONYMIZER_PREFIX + " and the scanner setting is " + GLOBAL_SCANNER_SETTING);
                //startActivity(goBackIntent);
            }
        });

    }

    @Override
    public void onBackPressed () {
        if (changedOccurred) {
            storePrefs(editor);
        }

        super.onBackPressed();
        return;
    }


    public void getPrefs (SharedPreferences pref) {

        pref.getString("anonymizer_prefix", anonPrefSetting); // getting String
        pref.getBoolean("scanner preference", scannerPrefSetting); // getting boolean

        //pref.getInt("key_name", -1); // getting Integer
        //pref.getFloat("key_name", null); // getting Float
        //pref.getLong("key_name", null); // getting Long


    }

    public void storePrefs (SharedPreferences.Editor editor) {
        //Log.d("CHANGE OCCURRED", "SAVING CHANGE");
        anonPrefSetting = anonPref.getText().toString();
        scannerPrefSetting = scannerPref.isChecked();

        GLOBAL_ANONYMIZER_PREFIX = anonPrefSetting;
        GLOBAL_SCANNER_SETTING = scannerPrefSetting;


        editor.putString("anonymizer_prefix", anonPrefSetting);
        editor.putBoolean("scanner_preference", scannerPrefSetting);
        editor.commit(); // commit changes

    }

}
