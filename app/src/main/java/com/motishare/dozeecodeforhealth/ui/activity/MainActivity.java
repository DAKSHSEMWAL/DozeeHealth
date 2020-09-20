package com.motishare.dozeecodeforhealth.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.google.gson.reflect.TypeToken;
import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.core.BaseActivity;
import com.motishare.dozeecodeforhealth.databinding.ActivityMainBinding;
import com.motishare.dozeecodeforhealth.model.BloodPressure;
import com.motishare.dozeecodeforhealth.model.QuestionModel;
import com.motishare.dozeecodeforhealth.model.UserData;
import com.motishare.dozeecodeforhealth.model.UserModel;
import com.motishare.dozeecodeforhealth.utils.Common;
import com.motishare.dozeecodeforhealth.utils.Dialogs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.motishare.dozeecodeforhealth.utils.Common.getDate;
import static com.motishare.dozeecodeforhealth.utils.Common.setToolbarWithBackAndTitle;

public class MainActivity extends BaseActivity {
    private LOADER_TYPE loader_type = LOADER_TYPE.NORMAL;
    UserModel userModel;
    List<UserData> userData = new ArrayList<>();
    public ActivityMainBinding binding;
    QuestionModel questionModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_main);
        loader = (ConstraintLayout) binding.loader.getRoot();
        getData();
        initView();
        getDetails();
        getQuestion();
        implementListeners();
    }

    private void setViews() {
        binding.mToolbar.titleTv.setText(userModel.getName());
    }

    private void setData() {
        Date c = Calendar.getInstance().getTime();
        String today = getDate(DATE20, DATE4, c.toString());
        int c2 = 0;
        for (UserData a : userData) {
            c2++;
            Log.e("UserData", a.toString());
            if (a.getTime() != null) {
                Date c1 = new Date(Long.parseLong(a.getTime()) * 1000);
                String date = getDate(DATE20, DATE4, c1.toString());
                Log.e("Date", date + " " + today);
                if (!today.equals(date)) {
                    binding.stresslevel.setText("-");
                    binding.heartRatetext.setText("-");
                    binding.breathRatetext.setText("-");
                    binding.o2text.setText("-");
                    binding.Diastole.setText("-");
                    binding.Systole.setText("-");
                    binding.sleepratetext.setText("-");

                } else {
                    binding.stresslevel.setText(String.format(Locale.getDefault(), "%d", a.getRecovery()));
                    binding.breathRatetext.setText(String.format(Locale.getDefault(), "%d", a.getBreathRate()));
                    binding.o2text.setText(String.format(Locale.getDefault(), "%d", a.getO2()));
                    binding.sleepratetext.setText(String.format(Locale.getDefault(), "%d", a.getSleepscore()));
                    if (a.getBloodPressure() != null) {
                        setBloodpressureIndicator(a.getBloodPressure());
                        binding.Diastole.setText(String.format(Locale.getDefault(), "%d", a.getBloodPressure().getDiastole()));
                        binding.Systole.setText(String.format(Locale.getDefault(), "%d", a.getBloodPressure().getSystole()));
                    } else {
                        binding.Diastole.setText("-");
                        binding.Systole.setText("-");
                    }
                    if (a.getHeartRate() != null) {
                        binding.heartRatetext.setText(String.format(Locale.getDefault(), "%d", a.getHeartRate()));
                        setHeartRateLevel(a.getHeartRate());
                    } else {
                        binding.heartRatetext.setText("-");
                    }
                    if (a.getRecovery() != null) {
                        binding.stresslevel.setText(String.format(Locale.getDefault(), "%d", a.getRecovery()));
                        setHeartRateLevel(a.getHeartRate());
                    } else {
                        binding.stresslevel.setText("-");
                    }
                }
            }
        }
    }

    private void setQuestion(){
        binding.questiona.setText(String.format("%s, %s. %s", questionModel.getGreeting(), userModel.getName().substring(0, userModel.getName().indexOf(' ')), questionModel.getQuestion()));
        /*binding.questiona.setText(String.format("%s, %s.%s", questionModel.getGreeting(), userModel.getName(), questionModel.getQuestion()));*/
    }

    private void setHeartRateLevel(Integer heartRate) {
        if (heartRate < 45 && heartRate > 75) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        } else if ((heartRate >= 45 && heartRate <= 54) || (heartRate >= 66 && heartRate <= 75)) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (heartRate >= 55 && heartRate <= 65) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        }
    }

    private void setStressLevel() {
    }

    private void setBloodpressureIndicator(@NotNull BloodPressure bloodPressure) {
        if (bloodPressure.getSystole() <= 130 && bloodPressure.getDiastole() <= 80) {
            binding.bloodpressurelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else if (bloodPressure.getSystole() > 130 && bloodPressure.getSystole() < 140 && bloodPressure.getDiastole() > 80 && bloodPressure.getDiastole() < 90) {
            binding.bloodpressurelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (bloodPressure.getSystole() >= 140 && bloodPressure.getDiastole() <= 90) {
            binding.bloodpressurelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        } else {
            Log.e("Hi", "Ho4" + (bloodPressure.getSystole() > 130 && bloodPressure.getSystole() < 140));
        }
    }

    private void implementListeners() {
        binding.share.setOnClickListener(view -> {
            File file = Common.BitmapToFile(Common.screenShot(binding.view), mContext);
            Uri uri = FileProvider.getUriForFile(mContext, "com.motishare.dozeecodeforhealth.provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(mContext)
                    .setType("image/jpg")
                    .setStream(uri)
                    .setChooserTitle(R.string.share_title)
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            mContext.startActivity(intent);
        });
        MenuItem whatsapp = binding.navView.getMenu().findItem(R.id.navigation_whatsapp);
        MenuItem call = binding.navView.getMenu().findItem(R.id.navigation_call);
        MenuItem email = binding.navView.getMenu().findItem(R.id.navigation_email);
        whatsapp.setOnMenuItemClickListener(menuItem1 -> {
            openWhatsApp();
            return false;
        });
        email.setOnMenuItemClickListener(menuItem1 -> {
            sendEmail();
            return false;
        });
        call.setOnMenuItemClickListener(menuItem1 -> {
            Call();
            return false;
        });
    }

    private void initView() {
        setToolbarWithBackAndTitle(mContext, "Risi Shah", false, 0);
        Date c = Calendar.getInstance().getTime();
        binding.todaydate.setText(String.format("Today - %s", getDate(DATE20, DATE4, c.toString())));
    }

    private void getData() {
        showLoader(loader_type);

        call = api.userdetails();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Type type = new TypeToken<UserModel>() {
                    }.getType();
                    if (response.code() == 200) {
                        assert response.body() != null;
                        userModel = gson.fromJson(response.body().string(), type);
                        setViews();
                        Log.e("Data", userModel.toString());
                    } else {
                        Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext, false, ok -> {

                        });
                    }
                } catch (Exception e) {
                    Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext);
                    e.printStackTrace();
                }
                hideLoader(loader_type);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                hideLoader(loader_type);
            }
        });
    }

    private void getQuestion() {
        showLoader(loader_type);

        call = api.userquestion();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Type type = new TypeToken<QuestionModel>() {
                    }.getType();
                    if (response.code() == 200) {
                        assert response.body() != null;
                        questionModel = gson.fromJson(response.body().string(), type);
                        setQuestion();
                        Log.e("Data", questionModel.toString());
                    } else {
                        Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext, false, ok -> {

                        });
                    }
                } catch (Exception e) {
                    Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext);
                    e.printStackTrace();
                }
                hideLoader(loader_type);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                hideLoader(loader_type);
            }
        });
    }

    private void getDetails() {
        showLoader(loader_type);

        call = api.userdata();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Type type = new TypeToken<ArrayList<UserData>>() {
                    }.getType();
                    if (response.code() == 200) {
                        assert response.body() != null;
                        userData = gson.fromJson(response.body().string(), type);
                        setData();
                    } else {
                        Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext, false, ok -> {

                        });
                    }
                } catch (Exception e) {
                    Dialogs.notifyalert(getString(R.string.server_error_msg), "OK", mContext);
                    e.printStackTrace();
                }
                hideLoader(loader_type);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                hideLoader(loader_type);
            }
        });
    }

    public void openWhatsApp() {
        PackageManager pm = getPackageManager();
        try {
            String toNumber = "+918884436933";
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "You don't have whatsapp installed on this phone", Toast.LENGTH_LONG).show();

        }
    }

    public void sendEmail() {
        String to = "support@dozee.io";
        String subject = userModel.getName() + " Need support with my Dozee app";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public void Call() {
        Uri u = Uri.parse("tel:" + "+91-8884436933");
        Intent i = new Intent(Intent.ACTION_DIAL, u);

        try {
            startActivity(i);
        } catch (SecurityException s) {
            Toast.makeText(this, s.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

}