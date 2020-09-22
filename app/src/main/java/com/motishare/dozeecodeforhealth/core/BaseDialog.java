package com.motishare.dozeecodeforhealth.core;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.motishare.dozeecodeforhealth.interfaces.Constants;
import com.motishare.dozeecodeforhealth.interfaces.RetrofitInterface;
import com.motishare.dozeecodeforhealth.utils.SharedPref;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.motishare.dozeecodeforhealth.interfaces.Constants.LOADER_TYPE.NORMAL;


public class BaseDialog extends DialogFragment implements Constants {

    public SharedPref sp;
    public Activity mContext;
    public ConstraintLayout loader = null;
    public RetrofitInterface api = null;
    public Call<ResponseBody> call = null;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables();
    }

    /**
     * method to initialize all the required public variables
     */
    private void initializeVariables() {
        mContext = getActivity();
        sp = new SharedPref(mContext);
        api = retrofitCall().create(RetrofitInterface.class);
    }

    public void showLoader(Constants.LOADER_TYPE loader_type) {
        if (loader_type == NORMAL)
            loader.setVisibility(View.VISIBLE);
    }

    public void hideLoader(Constants.LOADER_TYPE loader_type) {
        if (loader_type == NORMAL)
            loader.setVisibility(View.GONE);
    }

    private Retrofit retrofitCall() {

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(1, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(30, TimeUnit.SECONDS) // write timeout
                .readTimeout(30, TimeUnit.SECONDS); // read timeout
        httpClient.addInterceptor(logging);


        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();

        return retrofit;
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

    /*@Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (call != null)
            call.cancel();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (call != null)
            call.cancel();
    }*/
}