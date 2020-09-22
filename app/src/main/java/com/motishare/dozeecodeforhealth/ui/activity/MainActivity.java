package com.motishare.dozeecodeforhealth.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.reflect.TypeToken;
import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.core.BaseActivity;
import com.motishare.dozeecodeforhealth.databinding.ActivityMainBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogBloodoxygenDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogBloodpressureDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogBreathrateDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogHeartDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogProfileDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogSleepscoreDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogStresslevelDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyBreathrateDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyHeartDetailsBinding;
import com.motishare.dozeecodeforhealth.model.BP;
import com.motishare.dozeecodeforhealth.model.QuestionModel;
import com.motishare.dozeecodeforhealth.model.UserData;
import com.motishare.dozeecodeforhealth.model.UserModel;
import com.motishare.dozeecodeforhealth.utils.Common;
import com.motishare.dozeecodeforhealth.utils.Dialogs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import at.favre.lib.dali.Dali;
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
    List<UserData> tempList = new ArrayList<>();
    List<String> datelists = new ArrayList<>();
    UserData mode;
    public ActivityMainBinding binding;
    DialogHeartDetailsBinding dialogHeartDetailsBinding;
    DialogBreathrateDetailsBinding dialogBreathrateDetailsBinding;
    DialogBloodoxygenDetailsBinding dialogBloodoxygenDetailsBinding;
    DialogBloodpressureDetailsBinding dialogBloodpressureDetailsBinding;
    DialogSleepscoreDetailsBinding dialogSleepscoreDetailsBinding;
    DialogStresslevelDetailsBinding dialogStresslevelDetailsBinding;
    DialogWeeklyHeartDetailsBinding dialogWeeklyDetailsBinding;
    DialogWeeklyBreathrateDetailsBinding dialogWeeklyBreathrateDetailsBinding;
    DialogProfileDetailsBinding dialogProfileDetailsBinding;
    QuestionModel questionModel;
    int c = 0;
    int selected = 0;
    String current, crrentweek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_main);
        loader = (ConstraintLayout) binding.loader.getRoot();
        getData();
        initView();
        getDetails();
        implementListeners();
    }

    private void setViews() {
        binding.mToolbar.titleTv.setText(userModel.getName());
        getQuestion();
    }

    private void setData(@NotNull String currentdate) {
        Date c = Calendar.getInstance().getTime();
        String today = getDate(DATE20, DATE4, c.toString());
        current = currentdate;
        if (currentdate.equals(today)) {
            Log.e("Hi", "Here1");
            binding.todaydate.setText(String.format("Today - %s", currentdate));
            binding.next.setVisibility(View.GONE);
        } else {
            Log.e("Hi", "Here2");
            binding.todaydate.setText(String.format("%s", currentdate));
            binding.next.setVisibility(View.VISIBLE);
        }
        for (UserData a : userData) {
            if (a.getTime() != null) {
                Date c1 = new Date(Long.parseLong(a.getTime()) * 1000);
                String date = getDate(DATE20, DATE4, c1.toString());
                if (!currentdate.equals(date)) {
                    binding.stresslevel.setText("-");
                    binding.heartRatetext.setText("-");
                    binding.breathRatetext.setText("-");
                    binding.o2text.setText("-");
                    binding.Diastole.setText("-");
                    binding.Systole.setText("-");
                    binding.sleepratetext.setText("-");

                } else {
                    Log.e("Hi", a.toString());
                    if (a.getBP() != null) {
                        setBloodpressureIndicator(a.getBP());
                        binding.Diastole.setText(String.format(Locale.getDefault(), "%d", a.getBP().getDiastole()));
                        binding.Systole.setText(String.format(Locale.getDefault(), "%d", a.getBP().getSystole()));
                    } else {
                        binding.Diastole.setText("-");
                        binding.Systole.setText("-");
                    }
                    if (a.getHeartRate() != null && a.getHeartRate() > 0) {
                        binding.heartRatetext.setText(String.format(Locale.getDefault(), "%d", a.getHeartRate()));
                        setHeartRateLevel(a.getHeartRate());
                    } else {
                        binding.heartRatetext.setText("-");
                    }
                    if (a.getBreathRate() != null && a.getBreathRate() > 0) {
                        binding.breathRatetext.setText(String.format(Locale.getDefault(), "%d", a.getBreathRate()));
                        setBreathrate(a.getBreathRate());
                    } else {
                        binding.breathRatetext.setText("-");
                    }
                    if (a.getRecovery() != null && a.getRecovery() > 0) {
                        binding.stresslevel.setText(String.format(Locale.getDefault(), "%d", a.getRecovery()));
                        setStressLevel(a.getRecovery());
                    } else {
                        binding.stresslevel.setText("-");
                    }
                    if (a.getSleepscore() != null && a.getSleepscore() > 0) {
                        binding.sleepratetext.setText(String.format(Locale.getDefault(), "%d", a.getSleepscore()));
                        setSleepScoreLevel(a.getSleepscore());
                    } else {
                        binding.sleepratetext.setText("-");
                    }
                    if (a.getO2() != null && a.getO2() > 0) {
                        binding.o2text.setText(String.format(Locale.getDefault(), "%d", a.getO2()));
                        setOxygen(a.getO2());
                    } else {
                        binding.o2text.setText("-");
                    }
                }
            }
        }
    }

    private void setWeekData(@NotNull String currentdate) {
        SimpleDateFormat df = new SimpleDateFormat(DATE4, Locale.getDefault());
        Date date;
        try {
            date = df.parse(currentdate);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            cal.setTime(date);
            List<String> dates = new ArrayList<>();
            Date c = Calendar.getInstance().getTime();
            String weekstart = "", weekend = "", month = "";
            String today = getDate(DATE20, DATE4, c.toString());
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                cal.set(Calendar.DAY_OF_WEEK, i);
                if (cal.getTime().before(c) || cal.getTime().compareTo(c) == 0) {
                    dates.add(getDate(DATE20, DATE4, cal.getTime().toString()));
                }
                if (i == Calendar.SUNDAY) {
                    weekstart = getDate(DATE20, DATE21, cal.getTime().toString());
                }
                if (i == Calendar.SATURDAY) {
                    weekend = getDate(DATE20, DATE21, cal.getTime().toString());
                }
                month = getDate(DATE20, DATE22, cal.getTime().toString());
            }
            if (dates.contains(today)) {
                binding.todaydate.setText(String.format("This Week - %s-%s %s", weekstart, weekend, month));
                binding.next.setVisibility(View.GONE);
            } else {
                binding.todaydate.setText(String.format("%s-%s %s", weekstart, weekend, month));
                binding.next.setVisibility(View.VISIBLE);
            }
            crrentweek = String.format("%s-%s %s", weekstart, weekend, month);
            List<UserData> tempList = new ArrayList<>();
            for (UserData a : userData) {
                if (a.getTime() != null) {
                    Date c1 = new Date(Long.parseLong(a.getTime()) * 1000);
                    String te = getDate(DATE20, DATE4, c1.toString());
                    for (String b : dates) {
                        if (te.equals(b)) {
                            tempList.add(a);
                        }
                    }
                }
            }
            UserData userData = new UserData();
            int bpc = 0, brc = 0, o2 = 0, hr = 0, st = 0, sl = 0;
            int systemp = 0;
            int distemp = 0;
            int bretemp = 0;
            int oxytemp = 0;
            int hrtemp = 0;
            int sttemp = 0;
            int sltemp = 0;
            Log.e("Temp Date List", dates.toString());
            Log.e("Temp List ize", "" + tempList.size());
            for (UserData a : tempList) {
                Log.e("Temp List", a.toString());
                if (a.getBP() != null) {
                    bpc = bpc + 1;
                    systemp += a.getBP().getSystole();
                    distemp += a.getBP().getDiastole();
                }
                if (a.getBreathRate() != null && a.getBreathRate() > 0) {
                    brc = brc + 1;
                    bretemp += a.getBreathRate();
                }
                if (a.getO2() != null && a.getO2() > 0) {
                    o2 = o2 + 1;
                    oxytemp += a.getO2();
                }
                if (a.getHeartRate() != null && a.getHeartRate() > 0) {
                    hr = hr + 1;
                    hrtemp += a.getHeartRate();
                }
                if (a.getSleepscore() != null && a.getSleepscore() > 0) {
                    sl = sl + 1;
                    sltemp += a.getSleepscore();
                }
                if (a.getRecovery() != null && a.getRecovery() > 0) {
                    st = st + 1;
                    sttemp += a.getRecovery();
                }
            }
            if (systemp != 0 && distemp != 0 && bpc != 0) {
                userData.setBP(new BP(systemp / bpc, distemp / bpc));
                setBloodpressureIndicator(userData.getBP());
                binding.Diastole.setText(String.format(Locale.getDefault(), "%d", userData.getBP().getDiastole()));
                binding.Systole.setText(String.format(Locale.getDefault(), "%d", userData.getBP().getSystole()));
            } else {
                binding.Diastole.setText("-");
                binding.Systole.setText("-");
            }
            if (bretemp != 0 && distemp != 0 && brc != 0) {
                userData.setBreathRate(bretemp / brc);
                binding.breathRatetext.setText(String.format(Locale.getDefault(), "%d", userData.getBreathRate()));
                setBreathrate(userData.getBreathRate());
            } else {
                binding.breathRatetext.setText("-");
            }
            if (oxytemp != 0 && o2 != 0) {
                userData.setO2(oxytemp / o2);
                binding.o2text.setText(String.format(Locale.getDefault(), "%d", userData.getO2()));
                setOxygen(userData.getO2());
            } else {
                binding.o2text.setText("-");
            }
            if (hrtemp != 0 && hr != 0) {
                userData.setHeartRate(hrtemp / hr);
                binding.heartRatetext.setText(String.format(Locale.getDefault(), "%d", userData.getHeartRate()));
                setHeartRateLevel(userData.getHeartRate());
            } else {
                binding.heartRatetext.setText("-");
            }
            if (sltemp != 0 && sl != 0) {
                userData.setSleepscore(sltemp / sl);
                binding.sleepratetext.setText(String.format(Locale.getDefault(), "%d", userData.getSleepscore()));
                setSleepScoreLevel(userData.getHeartRate());
            } else {
                binding.sleepratetext.setText("-");
            }
            if (sttemp != 0 && st != 0) {
                userData.setRecovery(sttemp / st);
                binding.stresslevel.setText(String.format(Locale.getDefault(), "%d", userData.getRecovery()));
                setStressLevel(userData.getRecovery());
            } else {
                binding.stresslevel.setText("-");
            }
            Log.e("User Data", userData.toString());
            mode = userData;
            datelists = dates;
            this.tempList.addAll(tempList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBreathrate(Integer breathRate) {
        if (breathRate < 8 || breathRate > 15) {
            binding.breathratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        } else if (breathRate <= 12) {
            binding.breathratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else {
            binding.breathratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        }
    }

    private void setOxygen(Integer o2) {
        if (o2 > 94) {
            binding.bloodoxygenlevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else if (o2 >= 90 && o2 <= 94) {
            binding.bloodoxygenlevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (o2 < 90) {
            binding.bloodoxygenlevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        }
    }

    private void setSleepScoreLevel(Integer sleepscore) {
        if (sleepscore > 80) {
            binding.sleeplevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else if (sleepscore >= 70 && sleepscore <= 79) {
            binding.sleeplevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (sleepscore < 70) {
            binding.sleeplevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        }
    }

    private void setQuestion() {
        binding.questiona.setText(String.format("%s, %s. %s", questionModel.getGreeting(), userModel.getName().substring(0, userModel.getName().indexOf(' ')), questionModel.getQuestion()));
        /*binding.questiona.setText(String.format("%s, %s.%s", questionModel.getGreeting(), userModel.getName(), questionModel.getQuestion()));*/
        binding.fresh.setText(questionModel.getAnswers().get(0));
        binding.good.setText(questionModel.getAnswers().get(1));
        binding.tired.setText(questionModel.getAnswers().get(2));
    }

    private void setHeartRateLevel(Integer heartRate) {
        if (heartRate < 45 || heartRate > 75) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        } else if ((heartRate >= 45 && heartRate <= 54) || (heartRate >= 66 && heartRate <= 75)) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (heartRate >= 55 && heartRate <= 65) {
            binding.heartratelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        }
    }

    private void setStressLevel(Integer recovery) {
        if (recovery > 90) {
            binding.stresslevell.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else if (recovery >= 75 && recovery <= 89) {
            binding.stresslevell.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Borderline));
        } else if (recovery < 75) {
            binding.stresslevell.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Unhealthy));
        }
    }

    private void setBloodpressureIndicator(@NotNull BP bloodPressure) {
        Log.e("", "" + bloodPressure.getSystole());
        if (bloodPressure.getSystole() < 130 || bloodPressure.getDiastole() < 80) {
            binding.bloodpressurelevel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Healthy));
        } else if ((bloodPressure.getSystole() > 130 && bloodPressure.getSystole() < 140) && (bloodPressure.getDiastole() > 80 && bloodPressure.getDiastole() < 90)) {
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
        binding.daily.setOnClickListener(v -> {
            selected = 0;
            c = 0;
            binding.daily.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tab_background));
            binding.weekly.setBackground(null);
            binding.monthly.setBackground(null);
            setData(current);
        });
        binding.weekly.setOnClickListener(v -> {
            selected = 1;
            c = 0;
            binding.daily.setBackground(null);
            binding.weekly.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tab_background));
            binding.monthly.setBackground(null);
            setWeekData(current);
        });
        binding.monthly.setOnClickListener(v -> {
            selected = 2;
            c = 0;
            binding.daily.setBackground(null);
            binding.weekly.setBackground(null);
            binding.monthly.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tab_background));
        });
        binding.more1.setOnClickListener(v -> {
            if (selected == 0) {
                showHeartDialog();
            } else if (selected == 1) {
                showHeartWeekDialog();
            } else {

            }
        });
        binding.more2.setOnClickListener(v -> {
            if (selected == 0) {
                showBreathDialog();
            } else if (selected == 1) {
                showBreathRateDialog();
            } else {

            }
        });
        binding.more3.setOnClickListener(v -> {
            showBloodOxygen();
        });
        binding.more4.setOnClickListener(v -> {
            showBloodPressure();
        });
        binding.more5.setOnClickListener(v -> {
            showSleepRate();
        });
        binding.more6.setOnClickListener(v -> {
            showStressLevel();
        });
        binding.next.setOnClickListener(v -> {
            c = c + 1;
            Calendar calendar = Calendar.getInstance();
            if (selected == 0) {
                calendar.add(Calendar.DATE, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setData(today);
            } else if (selected == 1) {
                //c=c+6;
                calendar.add(Calendar.WEEK_OF_MONTH, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setWeekData(today);
            }
        });
        binding.prev.setOnClickListener(v -> {
            c = c - 1;
            Calendar calendar = Calendar.getInstance();
            if (selected == 0) {
                calendar.add(Calendar.DATE, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setData(today);
            } else if (selected == 1) {
                //c=c+6;
                calendar.add(Calendar.WEEK_OF_MONTH, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setWeekData(today);
            }
        });
        binding.mToolbar.dropdownMenu.setOnClickListener(v -> {
            showProfile();
        });
    }

    private void initView() {
        setToolbarWithBackAndTitle(mContext, "", false, 0);
        Date c = Calendar.getInstance().getTime();
        String today = getDate(DATE20, DATE4, c.toString());
        binding.todaydate.setText(String.format("Today - %s", today));
        binding.next.setVisibility(View.GONE);
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
                        Date c = Calendar.getInstance().getTime();
                        String today = getDate(DATE20, DATE4, c.toString());
                        if (selected == 0)
                            setData(today);
                        else if (selected == 1)
                            setWeekData(today);
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

    private void revealShowImage(@NotNull View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (binding.getRoot().getX() + (binding.getRoot().getWidth() / 2));
        int cy = (int) (binding.getRoot().getY() + (binding.getRoot().getHeight()));


        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(500);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(500);
            anim.start();
        }

    }

    private void showHeartDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHeartDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_heart_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogHeartDetailsBinding.imgBg);
        dialog.setContentView(dialogHeartDetailsBinding.getRoot());
        dialogHeartDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogHeartDetailsBinding.getRoot(), false, dialog);
        });
        dialogHeartDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogHeartDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogHeartDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogHeartDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showHeartWeekDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklyDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_heart_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklyDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklyDetailsBinding.getRoot());
        dialogWeeklyDetailsBinding.time.setText(crrentweek);
        setHeartLineData();
        /*dialogWeeklyDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklyDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklyDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklyDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showBreathRateDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklyBreathrateDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_breathrate_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklyBreathrateDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklyBreathrateDetailsBinding.getRoot());
        dialogWeeklyBreathrateDetailsBinding.time.setText(crrentweek);
        setBreathLineData();
        /*dialogWeeklyBreathrateDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBreathrateDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklyBreathrateDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBreathrateDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklyBreathrateDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklyBreathrateDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void setBreathLineData() {
        List<Integer> breathList = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                breathList.add(a.getHeartRate());
                if (a.getHeartRate() > 15 || a.getHeartRate() < 8) {
                    c++;
                }
            }
        }
        dialogWeeklyBreathrateDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (breathList.size() != 0) {
            dialogWeeklyBreathrateDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(breathList)));
            dialogWeeklyBreathrateDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(breathList)));
            dialogWeeklyBreathrateDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getHeartRate()));
        }
        dialogWeeklyBreathrateDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyBreathrateDetailsBinding.chart.setPinchZoom(true);
        renderBreathData();
    }

    private void setHeartLineData() {
        List<Integer> heartList = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getHeartRate() != null) {
                heartList.add(a.getHeartRate());
                if (a.getHeartRate() > 65 || a.getHeartRate() < 55) {
                    c++;
                }
            }
        }
        dialogWeeklyDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (heartList.size() != 0) {
            dialogWeeklyDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(heartList)));
            dialogWeeklyDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(heartList)));
            dialogWeeklyDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getHeartRate()));
        }
        dialogWeeklyDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyDetailsBinding.chart.setPinchZoom(true);
        renderHeartData();
    }

    public void renderHeartData() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklyDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        LimitLine ll1 = new LimitLine(55f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(Color.WHITE);

        LimitLine ll2 = new LimitLine(65f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(Color.WHITE);
        ll2.enableDashedLine(10f, 10f, 0f);

        YAxis leftAxis = dialogWeeklyDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(90f);
        leftAxis.setAxisMinimum(40f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklyDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLHeartData();
    }

    public void renderBreathData() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklyBreathrateDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        LimitLine ll1 = new LimitLine(12f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(Color.WHITE);

        LimitLine ll2 = new LimitLine(8f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(Color.WHITE);
        ll2.enableDashedLine(10f, 10f, 0f);

        YAxis leftAxis = dialogWeeklyBreathrateDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(5f);
        leftAxis.setLabelCount(15);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklyBreathrateDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLBreathData();
    }

    private void setLHeartData() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getHeartRate() != null) {
                values.add(new Entry(c, a.getHeartRate()));
                c = c + 1;
            }
        }
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Su");
        xAxisLabel.add("Mo");
        xAxisLabel.add("Tu");
        xAxisLabel.add("We");
        xAxisLabel.add("Th");
        xAxisLabel.add("Fr");
        xAxisLabel.add("Sa");
            /*dialogWeeklyDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    Log.e("dgk",""+value);
                    return xAxisLabel.get((int)value);
                }
            });*/
        dialogWeeklyDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Log.e("dgk", "" + value);
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1;
        if (dialogWeeklyDetailsBinding.chart.getData() != null &&
                dialogWeeklyDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklyDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogWeeklyDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklyDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setLabel("");
            set1.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set1.setValueTextColor(Color.WHITE);
            set1.setFormSize(15.f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            dialogWeeklyDetailsBinding.chart.setData(data);
        }
        dialogWeeklyDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklyDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklyDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklyDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklyDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setLBreathData() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                Log.e("Data",""+a.getBreathRate());
                values.add(new Entry(c, a.getBreathRate()));
                c = c + 1;
            }
        }
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Su");
        xAxisLabel.add("Mo");
        xAxisLabel.add("Tu");
        xAxisLabel.add("We");
        xAxisLabel.add("Th");
        xAxisLabel.add("Fr");
        xAxisLabel.add("Sa");
        dialogWeeklyBreathrateDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1;
        if (dialogWeeklyBreathrateDetailsBinding.chart.getData() != null &&
                dialogWeeklyBreathrateDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklyBreathrateDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogWeeklyBreathrateDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklyBreathrateDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setLabel("");
            set1.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set1.setValueTextColor(Color.WHITE);
            set1.setFormSize(15.f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            dialogWeeklyBreathrateDetailsBinding.chart.setData(data);
        }
        dialogWeeklyBreathrateDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklyBreathrateDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBreathrateDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBreathrateDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklyBreathrateDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklyBreathrateDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklyBreathrateDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void showBreathDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBreathrateDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_breathrate_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogBreathrateDetailsBinding.imgBg);
        dialog.setContentView(dialogBreathrateDetailsBinding.getRoot());
        dialogBreathrateDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogBreathrateDetailsBinding.getRoot(), false, dialog);
        });
        dialogBreathrateDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogBreathrateDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogBreathrateDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogBreathrateDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showBloodOxygen() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBloodoxygenDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_bloodoxygen_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogBloodoxygenDetailsBinding.imgBg);
        dialog.setContentView(dialogBloodoxygenDetailsBinding.getRoot());
        dialogBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });
        dialogBloodoxygenDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogBloodoxygenDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogBloodoxygenDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showBloodPressure() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBloodpressureDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_bloodpressure_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogBloodpressureDetailsBinding.imgBg);
        dialog.setContentView(dialogBloodpressureDetailsBinding.getRoot());
        dialogBloodpressureDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogBloodpressureDetailsBinding.getRoot(), false, dialog);
        });
        dialogBloodpressureDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogBloodpressureDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogBloodpressureDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogBloodpressureDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showSleepRate() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSleepscoreDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_sleepscore_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogSleepscoreDetailsBinding.imgBg);
        dialog.setContentView(dialogSleepscoreDetailsBinding.getRoot());
        dialogSleepscoreDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogSleepscoreDetailsBinding.getRoot(), false, dialog);
        });
        dialogSleepscoreDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogSleepscoreDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogSleepscoreDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogSleepscoreDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showStressLevel() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStresslevelDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_stresslevel_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogStresslevelDetailsBinding.imgBg);
        dialog.setContentView(dialogStresslevelDetailsBinding.getRoot());
        dialogStresslevelDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogStresslevelDetailsBinding.getRoot(), false, dialog);
        });
        dialogStresslevelDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogStresslevelDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogStresslevelDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogStresslevelDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showProfile() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProfileDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_profile_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogProfileDetailsBinding.imgBg);
        dialog.setContentView(dialogProfileDetailsBinding.getRoot());
        if (userModel != null) {
            dialogProfileDetailsBinding.dateofbirth.setText(userModel.getDob());
            dialogProfileDetailsBinding.username.setText(userModel.getName());
            dialogProfileDetailsBinding.mobile.setText(String.format("%s-%s", userModel.getPhone().getCountryCode(), userModel.getPhone().getNumber()));
        }
        dialogProfileDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogProfileDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogProfileDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogProfileDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

}