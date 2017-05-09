package khandelwal.ishan.tag;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.disklrucache.DiskLruCache;

public class PhotoGrid extends Activity {

    private SharedPreferences preferences;

    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    private static LruCache<String, Bitmap> mMemeoryCache;
    GridView grid;
    NewGridViewAdapter adapter;
    File file;
    int imageSize;
    String imagePath;

    //LRU Cache
    private LruCache<String, Bitmap> mMemoryCache;




    public static Activity fa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_main);

        //LRU Cache
        final int maxMemorySize = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemorySize/4;





        mMemeoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
        //

        fa = this;

        //Preferences
        preferences = getSharedPreferences("Setting Preferences", Context.MODE_PRIVATE);
        imageSize = preferences.getInt("size", 100);
        imagePath = preferences.getString("path", "SDImageTutorial");

        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + imagePath);

            // Create a new folder if no folder named SDImageTutorial exist
            file.mkdirs();
        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }
        }

        // Locate the GridView in gridView_main.xml
        grid = (GridView) findViewById(R.id.gridview);

        // Pass String arrays to GridViewAdapter Class
        adapter = new NewGridViewAdapter(this, FilePathStrings, FileNameStrings, imageSize, file);
        // Set the GridViewAdapter to the GridView
        grid.setAdapter(adapter);

        // Capture gridView item click
        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(PhotoGrid.this, ViewImage.class);
                // Pass String arrays FilePathStrings
                i.putExtra("filepath", FilePathStrings);
                // Pass String arrays FileNameStrings
                i.putExtra("filename", FileNameStrings);
                // Pass click position
                i.putExtra("position", position);
                startActivity(i);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), DeveloperSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmapFromCache(String key)
    {
        return mMemeoryCache.get(key);
    }

    public static void putBitmapToCache(String key, Bitmap bitmap)
    {
       /* if(getBitmapFromCache(key) == null)
        {
            mMemeoryCache.put(key, bitmap);
        }*/
        Log.i("Adding to cache", "" + key);
        try
        {
            mMemeoryCache.put(key, bitmap);
        }catch (NullPointerException e)
        {
            Log.e("Null Pointer for: ","Key = " + key + " , Bitmap = " + bitmap);
        }

    }



}
