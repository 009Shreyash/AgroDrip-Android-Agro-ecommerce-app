package com.agrodrip.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.core.content.FileProvider;

import com.agrodrip.BuildConfig;
import com.agrodrip.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Function {

    public static void showMessage(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction(R.string.txt_close, view1 -> {

                })
                .setActionTextColor(view.getResources().getColor(android.R.color.holo_red_light))
                .show();
    }


    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {

        Uri bmpUri = null;
        try {
            // This way, you don't need to request external read/write permission.
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public static byte[] getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    static public String dateFormate(String date) {

        SimpleDateFormat format = new SimpleDateFormat();
        if (date.contains(".")) {
            format = new SimpleDateFormat("dd.MMM.yyyy");
        } else if (date.contains("-")) {
            format = new SimpleDateFormat("dd-MM-yyyy");
        } else {
            return date;
        }

        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy");

        return format.format(newDate);
    }

}
