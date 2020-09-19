package com.motishare.dozeecodeforhealth.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.interfaces.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.motishare.dozeecodeforhealth.interfaces.Constants.DATE;

public class Common implements Constants {

    public static void setToolbarWithBackAndTitle(Context ctx, String title, Boolean value, int backResource) {
        Toolbar toolbar = ((AppCompatActivity) ctx).findViewById(R.id.toolbar);
        ((AppCompatActivity) ctx).setSupportActionBar(toolbar);
        TextView title_tv = toolbar.findViewById(R.id.title_tv);
        ActionBar actionBar = ((AppCompatActivity) ctx).getSupportActionBar();
        if (actionBar != null) {
            if (backResource != 0)
                toolbar.setNavigationIcon(backResource);
            actionBar.setDisplayShowHomeEnabled(value);
            actionBar.setDisplayShowTitleEnabled(false);
            title_tv.setText(title);
        }
    }

    @NotNull
    public static String getDate(String inputFormat, String outputFormat, String selectedDate) {
        SimpleDateFormat parseDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        Date date = null;
        try {
            date = parseDateFormat.parse(selectedDate);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
        return requiredDateFormat.format(date);
    }

    @NotNull
    public static RequestBody getRequestBodyOfString(String string) {
        return RequestBody.create(string, MediaType.parse("multipart/form-data"));
    }


    /**
     * convert to bitmap to file
     *
     * @param bitmap bitmap to convert
     * @return file image file
     */
    @NotNull
    public static File BitmapToFile(Bitmap bitmap, @NotNull Context context) {
        String timeStamp = new SimpleDateFormat(DATE,Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, imageFileName);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("common", "Error writing bitmap", e);
        }

        return imageFile;
    }


}
