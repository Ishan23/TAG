package khandelwal.ishan.tag;

/**
 * Created by Ishan on 17-01-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class GridViewAdapter extends BaseAdapter {

    // Declare variables
    private Activity activity;
    File mFile;
    private String[] filepath;
    private String[] filename;
    private int image_size;

    private static LayoutInflater inflater = null;

    public GridViewAdapter(Activity a, String[] fpath, String[] fname, int imageSize, File file) {
        activity = a;
        mFile = file;
        filepath = fpath;
        filename = fname;
        image_size = imageSize;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.gridview_item, null);
        // Locate the TextView in gridview_item.xml
        //TextView text = (TextView) vi.findViewById(R.id.text);
        // Locate the ImageView in gridview_item.xml

        ImageView image = (ImageView) vi.findViewById(R.id.image);

        File file = mFile.listFiles()[position];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(filepath[position], options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, image_size, image_size);

        //Async Task
        bmp = MainActivity.getBitmapFromCache(filepath[position]);
        if(bmp != null)
        {
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Log.i("In Cache","Image fetched from cache memory");
            bmp = BitmapFactory.decodeFile(filepath[position]);
            //bmp = getResizedBitmap(bmp, image_size, image_size, options);
            MainActivity.putBitmapToCache(filepath[position], bmp);
        }
        else
        {
            Log.i("Not In Cache","Image fetched from disk memory");
            BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(image, filepath, image_size, position);
            bitmapAsyncTask.execute(file);
        }


        /////////********************************************************************///////////////
       /* // Set file name to the TextView followed by the position
        //text.setText(filename[position]);

        // Decode the filepath with BitmapFactory followed by the position
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filepath[position], options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        Log.d("Image Dimensions",imageHeight + " X " + imageWidth);

        *//*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;*//*

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, image_size, image_size);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //bmp = BitmapFactory.decodeFile(filepath[position], options);

        bmp = MainActivity.getBitmapFromCache(filepath[position]);
        if(bmp == null)
        {
            bmp = BitmapFactory.decodeFile(filepath[position], options);
            bmp = getResizedBitmap(bmp, image_size, image_size, options);
            MainActivity.putBitmapToCache(filepath[position], bmp);
        }

        //MainActivity.putBitmapToCache(filename[position], bmp);

        //Now reduce the size
        //bmp = getResizedBitmap(bmp, image_size, image_size, options);

        // Set the decoded bitmap into ImageView
        image.setImageBitmap(bmp);
        */
    /////////////******************************************************************/////////////////
        return vi;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.i("inSampleSize ",""+inSampleSize);
        return inSampleSize;
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
