package com.motishare.dozeecodeforhealth.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
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
        File filesDir = context.getFilesDir();
        File imageFile = null;
        try {
            imageFile = File.createTempFile(timeStamp,".jpg",filesDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("common", "Error writing bitmap", e);
        }

        assert imageFile != null;
        return imageFile;
    }


    /*public static Bitmap createDrawableFromView(Context context, @NotNull View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // view.draw(canvas);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return bitmap;
    }*/

    public static Bitmap screenShot(@NotNull View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return bitmap;
    }

}
