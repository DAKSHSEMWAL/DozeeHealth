package com.motishare.dozeecodeforhealth.core;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.interfaces.Constants;
import com.motishare.dozeecodeforhealth.interfaces.RetrofitInterface;
import com.motishare.dozeecodeforhealth.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.motishare.dozeecodeforhealth.interfaces.Constants.LOADER_TYPE.NORMAL;
import static com.motishare.dozeecodeforhealth.interfaces.Constants.LOADER_TYPE.PAGINATION;
import static com.motishare.dozeecodeforhealth.interfaces.Constants.LOADER_TYPE.SWIPE;

public class BaseActivity extends AppCompatActivity implements Constants {

    public SharedPref sp;
    public Activity mContext;
    public ConstraintLayout loader, loadMore;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Call<ResponseBody> call = null;
    public RetrofitInterface api = null;
    public Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.stays);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }*/
        initializeVariables();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initializeVariables() {
        mContext = BaseActivity.this;
        sp = new SharedPref(mContext);
        api = retrofitCall().create(RetrofitInterface.class);
        gson = new GsonBuilder().setLenient().create();
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr != null && (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected());
    }

    public void showLoader(Constants.LOADER_TYPE loader_type) {
        if (loader_type == NORMAL)
            loader.setVisibility(View.VISIBLE);
        else if (loader_type == PAGINATION)
            loadMore.setVisibility(View.VISIBLE);
        else if (loader_type == SWIPE)
            swipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoader(Constants.LOADER_TYPE loader_type) {
        if (loader_type == NORMAL)
            loader.setVisibility(View.GONE);
        else if (loader_type == PAGINATION)
            loadMore.setVisibility(View.GONE);
        else if (loader_type == SWIPE)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }

    public void openKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && mContext.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*overridePendingTransition(R.anim.stays, R.anim.slide_down);*/
        if (call != null)
            call.cancel();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @NotNull
    private Retrofit retrofitCall() {

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(1, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(30, TimeUnit.SECONDS) // write timeout
                .readTimeout(30, TimeUnit.SECONDS); // read timeout
        httpClient.addInterceptor(logging);


        return new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
    }
    
}