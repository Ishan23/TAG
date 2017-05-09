package khandelwal.ishan.tag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Ishan on 23-01-2016.
 */
public class BitmapAsyncTask extends AsyncTask<File,Void,Bitmap> {

    WeakReference<ImageView> imageViewReference;
    private String[] filepath;
    private int image_size;
    private int position;
    private File mImageFile;


    public BitmapAsyncTask(ImageView imageView, String[] filepath, int imageSize, int position)
    {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.filepath = filepath;
        image_size = imageSize;
        this.position = position;

    }

    @Override
    protected Bitmap doInBackground(File... files) {
        //return BitmapFactory.decodeFile(files[0].getAbsolutePath());
        // Decode the filepath with BitmapFactory followed by the position
        //mImageFile = files[position];

        //Bitmap bmp = decodeSampledBitmapFromFile(filepath[position], image_size, image_size);
        mImageFile = new File(filepath[position]);
        Log.d("Testing", "1");

        // Check Lru cache in background thread
        Bitmap bmp = PhotoGrid.getBitmapFromCache(filepath[position]);

        if (bmp == null) {
            // Process as normal
            Log.i("Not found in cache","Not found in cache");
            bmp = decodeSampledBitmapFromFile(filepath[position], image_size, image_size);
        }
        else {
            Log.i("found in cache",""+bmp);
        }
        // Add final bitmap to caches

        PhotoGrid.putBitmapToCache(filepath[position],bmp);
        return  bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        //commented out before concurrency check
        /*if(bitmap!=null && imageViewReference!=null)
        {
            ImageView imageView =imageViewReference.get();
            if(imageView!=null)
            {
                imageView.setImageBitmap(bitmap);
            }
        }*/

        if(isCancelled())
        {
            bitmap = null;
        }
        if(bitmap!=null && imageViewReference!=null)
        {
            ImageView imageView = imageViewReference.get();
            BitmapAsyncTask bitmapAsyncTask = NewGridViewAdapter.getBitmapWorkerTask(imageView);
            if(this==bitmapAsyncTask && imageView!=null)
            {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);



        //Bitmap image = BitmapFactory.decodeFile(filePath, options);

        Log.d("options.outWidth", ""+options.outWidth);
        Log.d("options.outHeight", ""+options.outHeight);



        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(filePath, options);
        try
        {
            return Bitmap.createScaledBitmap(image,reqWidth,reqHeight,true);
        }
        catch (Exception e)
        {
            //Log.e("error",e.getMessage());
            return image;
        }

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

    public  File getImageFile()
    {
        return mImageFile;
    }



}
