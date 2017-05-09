package khandelwal.ishan.tag;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Ishan on 23-01-2016.
 */

public class NewGridViewAdapter extends BaseAdapter {

    // Declare variables
    private Activity activity;
    private String[] filepath;
    private String[] filename;
    File mFile;
    private int imageSize;

    //Concurrency variables
    private Bitmap placeHolderBitmap;
    private File imagesFile;


    private static LayoutInflater inflater = null;

    public NewGridViewAdapter(Activity a, String[] fpath, String[] fname, int imageSize, File file) {
        activity = a;
        filepath = fpath;
        filename = fname;
        mFile = file;
        this.imageSize = imageSize;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.gridview_item, null);
        // Locate the ImageView in gridview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.image);

        //Bitmap bmp = decodeSampledBitmapFromFile(filepath[position], imageSize, imageSize);

        // Set the decoded bitmap into ImageView
        File file = mFile.listFiles()[position];

        //Commented out for trial period while testing for concurrency
        /*BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(image, filepath, imageSize, position);
        bitmapAsyncTask.execute(file);*/

        try {
            Glide.with(image.getContext()).load(file).asBitmap().centerCrop().into(image);
        }catch (Exception e)
        {
            Log.e("Glide","unable to load image");
        }

/////////////////////////////////////////////////Before Glide///////////////////////////////////
       /* Bitmap bitmap = PhotoGrid.getBitmapFromCache(filepath[position]);
        if(bitmap!=null)
        {
            image.setImageBitmap(bitmap);
        }
        else if(checkBitmapWorkerTask(file,image))
        {
            BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(image, filepath, imageSize, position);
            AsyncDrawable asyncDrawable = new AsyncDrawable(image.getResources(),placeHolderBitmap, bitmapAsyncTask);
            image.setImageDrawable(asyncDrawable);
            bitmapAsyncTask.execute(file);
        }*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////

        return vi;
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

    //For Handling Concurrency
    public static class AsyncDrawable extends BitmapDrawable {
        final WeakReference<BitmapAsyncTask> taskReference;
        public AsyncDrawable(Resources resources,
                             Bitmap bitmap,
                             BitmapAsyncTask bitmapWorkerTask) {
            super(resources, bitmap);
            taskReference = new WeakReference(bitmapWorkerTask);
        }
        public BitmapAsyncTask getBitmapWorkerTask() {
            return taskReference.get();
        }
    }

    public static BitmapAsyncTask getBitmapWorkerTask(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof AsyncDrawable) {
            AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
            return asyncDrawable.getBitmapWorkerTask();
        }
        return null;
    }

    public  static boolean checkBitmapWorkerTask(File imageFile, ImageView imageView)
    {
        BitmapAsyncTask bitmapAsyncTask = getBitmapWorkerTask(imageView);
        if(bitmapAsyncTask!=null)
        {
            final File workerFile = bitmapAsyncTask.getImageFile();
            if(workerFile!=null)
            {
                if(workerFile!=imageFile)
                {
                    bitmapAsyncTask.cancel(true);
                }
                else
                {
                    //Async Task file is the same which the imageView is expecting
                    //so do nothing
                    return false;
                }
            }
        }
        return true;
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
}
