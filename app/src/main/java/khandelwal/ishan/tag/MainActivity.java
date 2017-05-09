package khandelwal.ishan.tag;

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

public class MainActivity extends Activity {

    private SharedPreferences preferences;

    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    private static LruCache<String, Bitmap> mMemeoryCache;
    GridView grid;
    GridViewAdapter adapter;
    File file;
    int imageSize;
    String imagePath;

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
/*            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "SDImageTutorial");*/
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

        // Locate the GridView in gridview_main.xml
        grid = (GridView) findViewById(R.id.gridview);

        // Pass String arrays to LazyAdapter Class
        adapter = new GridViewAdapter(this, FilePathStrings, FileNameStrings, imageSize, file);
        // Set the LazyAdapter to the GridView
        grid.setAdapter(adapter);

        // Capture gridview item click
        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(MainActivity.this, ViewImage.class);
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

    public SharedPreferences getPreferences()
    {
        return preferences;
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
        try
        {
            mMemeoryCache.put(key, bitmap);
        }catch (NullPointerException e)
        {
            Log.e("Null Pointer for: ","Key = " + key + " , Bitmap = " + bitmap);
        }

    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth, BitmapFactory.Options options) {
        int width = options.outWidth;
        int height = options.outHeight;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
// create a matrix for the manipulation
        Matrix matrix = new Matrix();
// resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
// recreate the new Bitmap
        try{
            Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        }catch (Exception e)
        {
            Log.e("Error", "getResizeBitmap");
            return image;
        }

    }
}
