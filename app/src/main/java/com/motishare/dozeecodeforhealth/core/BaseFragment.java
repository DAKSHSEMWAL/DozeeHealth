package com.motishare.dozeecodeforhealth.core;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class BaseFragment extends Fragment implements Constants{

    public SharedPref sp;
    //private static Socket mSocket;
    public Activity mContext;
    public ConstraintLayout loader, loadMore;
    public SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayoutManager layoutManager;
    public Call<ResponseBody> call = null;
    public RetrofitInterface api = null;
    public Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables();
    }


    private void initializeVariables() {
        mContext = getActivity();
        sp = new SharedPref(mContext);
        api = retrofitCall().create(RetrofitInterface.class);
        gson = new GsonBuilder().setLenient().create();
        layoutManager = new LinearLayoutManager(getContext());

        /*if(sp.getBoolean(IS_USER_LOGGED_IN))
            initializeSocket();*/
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

    /**
     * check internet connection
     *
     * @return is internet working or not
     */
    public boolean checkInternetConnection() {
        if (mContext == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }


    public void openKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && mContext.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (call != null)
            call.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null)
            call.cancel();
    }

    protected boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(
                mContext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE_LOCATION
        );
    }

}