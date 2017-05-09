package khandelwal.ishan.tag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class DeveloperSettings extends Activity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPrefsEditor;

    private EditText editText_imageSize;
    private Button button_setSize;
    private EditText editText_imagePath;
    private Button button_setPath;
    int image_size;
    String image_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_settings);

        final MainActivity mainActivity = new MainActivity();
        mainActivity.finish();
       //mSharedPreferences= mainActivity.getPreferences();
        mSharedPreferences = getSharedPreferences("Setting Preferences", Context.MODE_PRIVATE);
        mPrefsEditor = mSharedPreferences.edit();
        mSharedPreferences.getInt("size", 10);
        mSharedPreferences.getString("path", "SDImageTutorial");

        image_size = mSharedPreferences.getInt("size",10);
        image_path = mSharedPreferences.getString("path", "SDImageTutorial");


        editText_imageSize = (EditText) findViewById(R.id.imageSize);
        editText_imageSize.setText(String.valueOf(image_size));
        editText_imagePath = (EditText) findViewById(R.id.setPath);
        editText_imagePath.setText(image_path);

        button_setSize = (Button)findViewById(R.id.buttonSetSize);
        button_setPath = (Button)findViewById(R.id.buttonSetPath);


        button_setSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    image_size = Integer.valueOf(editText_imageSize.getText().toString());
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Error1",Toast.LENGTH_SHORT).show();
                }
                mPrefsEditor.putInt("size", Integer.valueOf(image_size));
                mPrefsEditor.apply();
                MainActivity.fa.finish();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        button_setPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    image_path = editText_imagePath.getText().toString();
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Error2",Toast.LENGTH_SHORT).show();
                }
                mPrefsEditor.putString("path", image_path);
                mPrefsEditor.apply();

                PhotoGrid.fa.finish();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), PhotoGrid.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
