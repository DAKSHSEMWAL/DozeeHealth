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
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlyBloodoxygenDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlyBloodpressureDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlyBreathrateDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlyHeartDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlyRecoveryDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogMonthlySleeprateDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogProfileDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogSleepscoreDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogStresslevelDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyBloodoxygenDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyBloodpressureDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyBreathrateDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyHeartDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklyRecoveryDetailsBinding;
import com.motishare.dozeecodeforhealth.databinding.DialogWeeklySleeprateDetailsBinding;
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
    DialogWeeklyBloodoxygenDetailsBinding dialogWeeklyBloodoxygenDetailsBinding;
    DialogWeeklyBloodpressureDetailsBinding dialogWeeklyBloodpressureDetailsBinding;
    DialogWeeklySleeprateDetailsBinding dialogWeeklySleeprateDetailsBinding;
    DialogWeeklyRecoveryDetailsBinding dialogWeeklyRecoveryDetailsBinding;
    DialogMonthlySleeprateDetailsBinding dialogMonthlySleeprateDetailsBinding;
    DialogMonthlyBloodoxygenDetailsBinding dialogMonthlyBloodoxygenDetailsBinding;
    DialogMonthlyHeartDetailsBinding dialogMonthlyHeartDetailsBinding;
    DialogMonthlyBreathrateDetailsBinding dialogMonthlyBreathrateDetailsBinding;
    DialogMonthlyBloodpressureDetailsBinding dialogMonthlyBloodpressureDetailsBinding;
    DialogMonthlyRecoveryDetailsBinding dialogMonthlyRecoveryDetailsBinding;
    DialogProfileDetailsBinding dialogProfileDetailsBinding;
    QuestionModel questionModel;
    int c = 0;
    int selected = 0;
    String current, crrentweek, currentmonth;
    String firstday = "", lastday = "";

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
            if (bretemp != 0 && brc != 0) {
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

    private void setMonthData(@NotNull String currentdate) {
        SimpleDateFormat df = new SimpleDateFormat(DATE4, Locale.getDefault());
        Date date;
        try {
            date = df.parse(currentdate);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            cal.setTime(date);
            List<String> dates = new ArrayList<>();
            Date c = Calendar.getInstance().getTime();
            String month = "";
            String today = getDate(DATE20, DATE4, c.toString());
            for (int i = cal.getActualMinimum(Calendar.DAY_OF_MONTH); i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                if (cal.getTime().before(c) || cal.getTime().compareTo(c) == 0) {
                    dates.add(getDate(DATE20, DATE4, cal.getTime().toString()));
                }
                if (i == cal.getActualMinimum(Calendar.DAY_OF_MONTH)) {
                    firstday = getDate(DATE20, DATE21, cal.getTime().toString());
                }
                if (i == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    lastday = getDate(DATE20, DATE21, cal.getTime().toString());
                }
                month = getDate(DATE20, DATE22, cal.getTime().toString());
            }
            if (dates.contains(today)) {
                binding.todaydate.setText(String.format("This Month - %s-%s %s", firstday, lastday, month));
                binding.next.setVisibility(View.GONE);
            } else {
                binding.todaydate.setText(String.format("%s-%s %s", firstday, lastday, month));
                binding.next.setVisibility(View.VISIBLE);
            }
            currentmonth = String.format("%s-%s %s", firstday, lastday, month);
            Log.e("Date", dates.toString());
            Log.e("Date", "" + cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            List<UserData> tempList2 = new ArrayList<>();
            for (UserData a : userData) {
                if (a.getTime() != null) {
                    Date c1 = new Date(Long.parseLong(a.getTime()) * 1000);
                    String te = getDate(DATE20, DATE4, c1.toString());
                    for (String b : dates) {
                        if (te.equals(b)) {
                            tempList2.add(a);
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
            Log.e("Temp List ize", "" + tempList2.size());
            for (UserData a : tempList2) {
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
            if (bretemp != 0 && brc != 0) {
                userData.setBreathRate(bretemp / brc);
                binding.breathRatetext.setText(String.format(Locale.getDefault(), "%d", userData.getBreathRate()));
                setBreathrate(userData.getBreathRate());
            } else {
                Log.e("Here", "here");
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
            this.tempList.addAll(tempList2);
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
            tempList = new ArrayList<>();
            setWeekData(current);
        });
        binding.monthly.setOnClickListener(v -> {
            selected = 2;
            c = 0;
            binding.daily.setBackground(null);
            binding.weekly.setBackground(null);
            binding.monthly.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tab_background));
            tempList = new ArrayList<>();
            setMonthData(current);
        });
        binding.more1.setOnClickListener(v -> {
            if (selected == 0) {
                showHeartDialog();
            } else if (selected == 1) {
                showHeartWeekDialog();
            } else if (selected == 2) {
                showMonthlyHeartDialog();
            }
        });
        binding.more2.setOnClickListener(v -> {
            if (selected == 0) {
                showBreathDialog();
            } else if (selected == 1) {
                showBreathRateDialog();
            } else if (selected == 2) {
                showMonthlyBreathRateDialog();
            }
        });
        binding.more3.setOnClickListener(v -> {
            if (selected == 0) {
                showBloodOxygen();
            } else if (selected == 1) {
                showBloodOxygenDialog();
            } else if (selected == 2) {
                showMonthlyBloodOxygenDialog();
            }
        });
        binding.more4.setOnClickListener(v -> {
            if (selected == 0) {
                showBloodPressure();
            } else if (selected == 1) {
                showBloodPressureDialog();
            } else if (selected == 2) {
                showMonthlyBloodPresuureDialog();
            }
        });
        binding.more5.setOnClickListener(v -> {
            if (selected == 0) {
                showSleepRate();
            } else if (selected == 1) {
                showSleepRateDialog();
            } else if (selected == 2) {
                showMonthlySleepRateDialog();
            }
        });
        binding.more6.setOnClickListener(v -> {
            if (selected == 0) {
                showStressLevel();
            } else if (selected == 1) {
                showRecoveryDialog();
            } else if (selected == 2) {
                showMotnhlyRecoveryDialog();
            }
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
            } else if (selected == 2) {
                //c=c+6;
                calendar.add(Calendar.MONTH, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setMonthData(today);
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
            } else if (selected == 2) {
                //c=c+6;
                calendar.add(Calendar.MONTH, c);
                String today = getDate(DATE20, DATE4, calendar.getTime().toString());
                setMonthData(today);
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

    private void showBloodOxygenDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklyBloodoxygenDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_bloodoxygen_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklyBloodoxygenDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklyBloodoxygenDetailsBinding.getRoot());
        dialogWeeklyBloodoxygenDetailsBinding.time.setText(crrentweek);
        setBloodOxygenLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklyBloodoxygenDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showBloodPressureDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklyBloodpressureDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_bloodpressure_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklyBloodpressureDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklyBloodpressureDetailsBinding.getRoot());
        dialogWeeklyBloodpressureDetailsBinding.time.setText(crrentweek);
        setBloodPresureLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklyBloodpressureDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodpressureDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklyBloodpressureDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklyBloodpressureDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showRecoveryDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklyRecoveryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_recovery_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklyRecoveryDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklyRecoveryDetailsBinding.getRoot());
        dialogWeeklyRecoveryDetailsBinding.time.setText(crrentweek);
        setRecoveryLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklyRecoveryDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyRecoveryDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklyRecoveryDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklyRecoveryDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showSleepRateDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWeeklySleeprateDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_weekly_sleeprate_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogWeeklySleeprateDetailsBinding.imgBg);
        dialog.setContentView(dialogWeeklySleeprateDetailsBinding.getRoot());
        dialogWeeklySleeprateDetailsBinding.time.setText(crrentweek);
        setSleepLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogWeeklySleeprateDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogWeeklySleeprateDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogWeeklySleeprateDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogWeeklySleeprateDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMonthlySleepRateDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlySleeprateDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_sleeprate_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlySleeprateDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlySleeprateDetailsBinding.getRoot());
        dialogMonthlySleeprateDetailsBinding.time.setText(currentmonth);
        setMonthlySleepLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogMonthlySleeprateDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlySleeprateDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlySleeprateDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlySleeprateDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMonthlyBloodOxygenDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlyBloodoxygenDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_bloodoxygen_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlyBloodoxygenDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlyBloodoxygenDetailsBinding.getRoot());
        dialogMonthlyBloodoxygenDetailsBinding.time.setText(currentmonth);
        setMotnthBloodOxygenLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogMonthlyBloodoxygenDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlyBloodoxygenDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlyBloodoxygenDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMonthlyBreathRateDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlyBreathrateDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_breathrate_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlyBreathrateDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlyBreathrateDetailsBinding.getRoot());
        dialogMonthlyBreathrateDetailsBinding.time.setText(crrentweek);
        setMonthlyBreathLineData();
        dialogMonthlyBreathrateDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlyBreathrateDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlyBreathrateDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlyBreathrateDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMonthlyBloodPresuureDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlyBloodpressureDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_bloodpressure_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlyBloodpressureDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlyBloodpressureDetailsBinding.getRoot());
        dialogMonthlyBloodpressureDetailsBinding.time.setText(crrentweek);
        setMonthlyBloodPresureLineData();
        dialogMonthlyBloodpressureDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlyBloodpressureDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlyBloodpressureDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlyBloodpressureDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMotnhlyRecoveryDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlyRecoveryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_recovery_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlyRecoveryDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlyRecoveryDetailsBinding.getRoot());
        dialogMonthlyRecoveryDetailsBinding.time.setText(crrentweek);
        setMonthlyRecoveryLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogMonthlyRecoveryDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlyRecoveryDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlyRecoveryDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlyRecoveryDetailsBinding.getRoot(), false, dialog);
                return true;
            }

            return false;
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void showMonthlyHeartDialog() {

        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMonthlyHeartDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_monthly_heart_details, null, false);
        Dali.create(mContext).load(binding.getRoot()).blurRadius(17).into(dialogMonthlyHeartDetailsBinding.imgBg);
        dialog.setContentView(dialogMonthlyHeartDetailsBinding.getRoot());
        dialogMonthlyHeartDetailsBinding.time.setText(currentmonth);
        setMonthlyHeartLineData();
        /*dialogWeeklyBloodoxygenDetailsBinding.knowMore.setOnClickListener(v -> {
            revealShowImage(dialogWeeklyBloodoxygenDetailsBinding.getRoot(), false, dialog);
        });*/
        dialogMonthlyHeartDetailsBinding.drop.setOnClickListener(v -> {
            revealShowImage(dialogMonthlyHeartDetailsBinding.getRoot(), false, dialog);
        });

        dialog.setOnShowListener(dialogInterface -> revealShowImage(dialogMonthlyHeartDetailsBinding.getRoot(), true, null));

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShowImage(dialogMonthlyHeartDetailsBinding.getRoot(), false, dialog);
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
                breathList.add(a.getBreathRate());
                if (a.getBreathRate() > 15 || a.getBreathRate() < 8) {
                    c++;
                }
            }
        }
        dialogWeeklyBreathrateDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (breathList.size() != 0) {
            dialogWeeklyBreathrateDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(breathList)));
            dialogWeeklyBreathrateDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(breathList)));
            dialogWeeklyBreathrateDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getBreathRate()));
        }
        dialogWeeklyBreathrateDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyBreathrateDetailsBinding.chart.setPinchZoom(true);
        renderBreathData();
    }

    private void setBloodOxygenLineData() {
        List<Integer> bloodoxygen = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                bloodoxygen.add(a.getO2());
                if (a.getO2() < 90) {
                    c++;
                }
            }
        }
        dialogWeeklyBloodoxygenDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (bloodoxygen.size() != 0) {
            dialogWeeklyBloodoxygenDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(bloodoxygen)));
            dialogWeeklyBloodoxygenDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(bloodoxygen)));
            dialogWeeklyBloodoxygenDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getO2()));
        }
        dialogWeeklyBloodoxygenDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyBloodoxygenDetailsBinding.chart.setPinchZoom(true);
        renderBloodOxygen();
    }

    private void setBloodPresureLineData() {
        List<Integer> systole = new ArrayList<>();
        List<Integer> diastole = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBP() != null) {
                systole.add(a.getBP().getSystole());
                diastole.add(a.getBP().getDiastole());
                if (a.getBP().getSystole() >= 140 && a.getBP().getDiastole() <= 90) {
                    c++;
                }
            }
        }
        dialogWeeklyBloodpressureDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (systole.size() != 0) {
            dialogWeeklyBloodpressureDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d\n%d", Collections.min(systole), Collections.min(diastole)));
            dialogWeeklyBloodpressureDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d\n%d", Collections.max(systole), Collections.max(diastole)));
            dialogWeeklyBloodpressureDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d\n%d", mode.getBP().getSystole(), mode.getBP().getDiastole()));
        }
        dialogWeeklyBloodpressureDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyBloodpressureDetailsBinding.chart.setPinchZoom(true);
        renderBlodPressure();
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

    private void setSleepLineData() {
        List<Integer> sleeprate = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getSleepscore() != null) {
                sleeprate.add(a.getSleepscore());
                if (a.getSleepscore() < 80) {
                    c++;
                }
            }
        }
        dialogWeeklySleeprateDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (sleeprate.size() != 0) {
            dialogWeeklySleeprateDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(sleeprate)));
            dialogWeeklySleeprateDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(sleeprate)));
            dialogWeeklySleeprateDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getSleepscore()));
        }
        dialogWeeklySleeprateDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklySleeprateDetailsBinding.chart.setPinchZoom(true);
        renderSleepRate();
    }

    private void setMonthlyBreathLineData() {
        List<Integer> breathList = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                breathList.add(a.getBreathRate());
                if (a.getBreathRate() > 15 || a.getBreathRate() < 8) {
                    c++;
                }
            }
        }
        dialogMonthlyBreathrateDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (breathList.size() != 0) {
            dialogMonthlyBreathrateDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(breathList)));
            dialogMonthlyBreathrateDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(breathList)));
            dialogMonthlyBreathrateDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getBreathRate()));
        }
        dialogMonthlyBreathrateDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlyBreathrateDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyBreathData();
    }

    private void setMonthlyBloodPresureLineData() {
        List<Integer> systole = new ArrayList<>();
        List<Integer> diastole = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBP() != null) {
                systole.add(a.getBP().getSystole());
                diastole.add(a.getBP().getDiastole());
                if (a.getBP().getSystole() >= 140 && a.getBP().getDiastole() <= 90) {
                    c++;
                }
            }
        }
        dialogMonthlyBloodpressureDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (systole.size() != 0) {
            dialogMonthlyBloodpressureDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d\n%d", Collections.min(systole), Collections.min(diastole)));
            dialogMonthlyBloodpressureDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d\n%d", Collections.max(systole), Collections.max(diastole)));
            dialogMonthlyBloodpressureDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d\n%d", mode.getBP().getSystole(), mode.getBP().getDiastole()));
        }
        dialogMonthlyBloodpressureDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlyBloodpressureDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyBloodPressure();
    }

    private void setMotnthBloodOxygenLineData() {
        List<Integer> bloodoxygen = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                bloodoxygen.add(a.getO2());
                if (a.getO2() < 90) {
                    c++;
                }
            }
        }
        dialogMonthlyBloodoxygenDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (bloodoxygen.size() != 0) {
            dialogMonthlyBloodoxygenDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(bloodoxygen)));
            dialogMonthlyBloodoxygenDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(bloodoxygen)));
            dialogMonthlyBloodoxygenDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getO2()));
        }
        dialogMonthlyBloodoxygenDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlyBloodoxygenDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyBloodOxygen();
    }

    private void setMonthlyHeartLineData() {
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
        dialogMonthlyHeartDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (heartList.size() != 0) {
            dialogMonthlyHeartDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(heartList)));
            dialogMonthlyHeartDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(heartList)));
            dialogMonthlyHeartDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getHeartRate()));
        }
        dialogMonthlyHeartDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlyHeartDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyHeartData();
    }

    private void setMonthlySleepLineData() {
        List<Integer> sleeprate = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getSleepscore() != null) {
                sleeprate.add(a.getSleepscore());
                if (a.getSleepscore() < 80) {
                    c++;
                }
            }
        }
        dialogMonthlySleeprateDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (sleeprate.size() != 0) {
            dialogMonthlySleeprateDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(sleeprate)));
            dialogMonthlySleeprateDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(sleeprate)));
            dialogMonthlySleeprateDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getSleepscore()));
        }
        dialogMonthlySleeprateDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlySleeprateDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyleepRate();
    }

    private void setRecoveryLineData() {
        List<Integer> recoveryrate = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getRecovery() != null) {
                recoveryrate.add(a.getRecovery());
                if (a.getRecovery() < 80) {
                    c++;
                }
            }
        }
        dialogWeeklyRecoveryDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (recoveryrate.size() != 0) {
            dialogWeeklyRecoveryDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(recoveryrate)));
            dialogWeeklyRecoveryDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(recoveryrate)));
            dialogWeeklyRecoveryDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getSleepscore()));
        }
        dialogWeeklyRecoveryDetailsBinding.chart.setTouchEnabled(true);
        dialogWeeklyRecoveryDetailsBinding.chart.setPinchZoom(true);
        renderRecoveryRate();
    }

    private void setMonthlyRecoveryLineData() {
        List<Integer> recoveryrate = new ArrayList<>();
        int c = 0;
        for (UserData a : tempList) {
            if (a.getRecovery() != null) {
                recoveryrate.add(a.getRecovery());
                if (a.getRecovery() < 80) {
                    c++;
                }
            }
        }
        dialogMonthlyRecoveryDetailsBinding.perss.setText(String.format(Locale.getDefault(), "%d times out of healthy range", c));
        if (recoveryrate.size() != 0) {
            dialogMonthlyRecoveryDetailsBinding.minavg.setText(String.format(Locale.getDefault(), "%d", Collections.min(recoveryrate)));
            dialogMonthlyRecoveryDetailsBinding.maxavg.setText(String.format(Locale.getDefault(), "%d", Collections.max(recoveryrate)));
            dialogMonthlyRecoveryDetailsBinding.avgavg.setText(String.format(Locale.getDefault(), "%d", mode.getSleepscore()));
        }
        dialogMonthlyRecoveryDetailsBinding.chart.setTouchEnabled(true);
        dialogMonthlyRecoveryDetailsBinding.chart.setPinchZoom(true);
        renderMonthlyRecoveryRate();
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
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(65f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));
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

    public void renderSleepRate() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklySleeprateDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);


        LimitLine ll1 = new LimitLine(80f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogWeeklySleeprateDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(40f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklySleeprateDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLSleepRateDate();
    }

    public void renderRecoveryRate() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklyRecoveryDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        LimitLine ll1 = new LimitLine(90f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogWeeklyRecoveryDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(190f);
        leftAxis.setAxisMinimum(70f);
        leftAxis.setLabelCount(15);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklyRecoveryDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLRecoveryRateDate();
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
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(8f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));
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

    public void renderBloodOxygen() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklyBloodoxygenDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        LimitLine ll1 = new LimitLine(94f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogWeeklyBloodoxygenDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(90f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklyBloodoxygenDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLBloodOxygen();
    }

    public void renderBlodPressure() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogWeeklyBloodpressureDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        LimitLine ll1 = new LimitLine(130f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(80f);
        ll2.setLineWidth(1f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        YAxis leftAxis = dialogWeeklyBloodpressureDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(140f);
        leftAxis.setAxisMinimum(60f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogWeeklyBloodpressureDetailsBinding.chart.getAxisRight().setEnabled(false);
        setLBloodPressure();

    }

    public void renderMonthlyRecoveryRate() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogMonthlyRecoveryDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(90f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogMonthlyRecoveryDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(190f);
        leftAxis.setAxisMinimum(70f);
        leftAxis.setLabelCount(15);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlyRecoveryDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMRecoveryRateDate();
    }

    public void renderMonthlyBreathData() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogMonthlyBreathrateDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(12f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(8f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));
        ll2.enableDashedLine(10f, 10f, 0f);

        YAxis leftAxis = dialogMonthlyBreathrateDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(5f);
        leftAxis.setLabelCount(15);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlyBreathrateDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMLBreathData();
    }

    public void renderMonthlyHeartData() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogMonthlyHeartDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(55f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(65f);
        ll2.setLineWidth(1f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));
        ll2.enableDashedLine(10f, 10f, 0f);

        YAxis leftAxis = dialogMonthlyHeartDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(40f);
        leftAxis.setLabelCount(15);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlyHeartDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMHeartData();
    }

    public void renderMonthlyleepRate() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);


        XAxis xAxis = dialogMonthlySleeprateDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(80f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogMonthlySleeprateDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(40f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlySleeprateDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMSleepRateDate();
    }

    public void renderMonthlyBloodOxygen() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogMonthlyBloodoxygenDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(94f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));


        YAxis leftAxis = dialogMonthlyBloodoxygenDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(90f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlyBloodoxygenDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMBloodOxygen();
    }

    public void renderMonthlyBloodPressure() {
        LimitLine llXAxis = new LimitLine(10f);
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextColor(R.color.White);
        llXAxis.setTextSize(10f);

        XAxis xAxis = dialogMonthlyBloodpressureDetailsBinding.chart.getXAxis();
        xAxis.setAxisMaximum(Float.parseFloat(Common.getDate(DATE21, DATE24, lastday)));
        xAxis.setAxisMinimum(Float.parseFloat(Common.getDate(DATE21, DATE24, firstday)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);

        LimitLine ll1 = new LimitLine(130f);
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        LimitLine ll2 = new LimitLine(80f);
        ll2.setLineWidth(1f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLineColor(ContextCompat.getColor(mContext, R.color.Healthy));

        YAxis leftAxis = dialogMonthlyBloodpressureDetailsBinding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(140f);
        leftAxis.setAxisMinimum(60f);
        leftAxis.setLabelCount(10);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);

        dialogMonthlyBloodpressureDetailsBinding.chart.getAxisRight().setEnabled(false);
        setMBloodPressure();

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
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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

    private void setLBloodPressure() {

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> values1 = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getBP() != null) {
                values.add(new Entry(c, a.getBP().getSystole()));
                values1.add(new Entry(c, a.getBP().getDiastole()));
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
        dialogWeeklyBloodpressureDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1, set2;
        if (dialogWeeklyBloodpressureDetailsBinding.chart.getData() != null &&
                dialogWeeklyBloodpressureDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklyBloodpressureDetailsBinding.chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) dialogWeeklyBloodpressureDetailsBinding.chart.getData().getDataSetByIndex(1);
            set1.setValues(values);
            set2.setValues(values1);
            set1.setDrawIcons(false);
            set2.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set2.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set2.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set2.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.Healthy));
            set2.setColor(ContextCompat.getColor(mContext, R.color.Persimmon));
            set1.setCircleRadius(3f);
            set2.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set2.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set2.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set2.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set2.setLabel("");
            set1.setFormSize(15.f);
            set2.setFormSize(15.f);
            dialogWeeklyBloodpressureDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklyBloodpressureDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set2 = new LineDataSet(values1, "");
            set1.setDrawIcons(false);
            set2.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set2.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.Healthy));
            set2.setColor(ContextCompat.getColor(mContext, R.color.Persimmon));
            set1.setLineWidth(1f);
            set2.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set2.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set2.setDrawCircleHole(false);
            set1.setLabel("");
            set2.setLabel("");
            set1.setValueTextSize(9f);
            set2.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set2.setFormLineWidth(1f);
            set1.setValueTextColor(Color.WHITE);
            set2.setValueTextColor(Color.WHITE);
            set1.setFormSize(15.f);
            set2.setFormSize(15.f);
            LineData data = new LineData(set1, set2);
            dialogWeeklyBloodpressureDetailsBinding.chart.setData(data);
        }
        dialogWeeklyBloodpressureDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklyBloodpressureDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBloodpressureDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBloodpressureDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklyBloodpressureDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklyBloodpressureDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklyBloodpressureDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setLSleepRateDate() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getSleepscore() != null) {
                Log.e("Data", "" + a.getSleepscore());
                values.add(new Entry(c, a.getSleepscore()));
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
        dialogWeeklySleeprateDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1;
        if (dialogWeeklySleeprateDetailsBinding.chart.getData() != null &&
                dialogWeeklySleeprateDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklySleeprateDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogWeeklySleeprateDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklySleeprateDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogWeeklySleeprateDetailsBinding.chart.setData(data);
        }
        dialogWeeklySleeprateDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklySleeprateDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklySleeprateDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklySleeprateDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklySleeprateDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklySleeprateDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklySleeprateDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setLBloodOxygen() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getO2() != null) {
                Log.e("Data", "" + a.getO2());
                values.add(new Entry(c, a.getO2()));
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
        dialogWeeklyBloodoxygenDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1;
        if (dialogWeeklyBloodoxygenDetailsBinding.chart.getData() != null &&
                dialogWeeklyBloodoxygenDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklyBloodoxygenDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogWeeklyBloodoxygenDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklyBloodoxygenDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogWeeklyBloodoxygenDetailsBinding.chart.setData(data);
        }
        dialogWeeklyBloodoxygenDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklyBloodoxygenDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBloodoxygenDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyBloodoxygenDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklyBloodoxygenDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklyBloodoxygenDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklyBloodoxygenDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setLRecoveryRateDate() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getRecovery() != null) {
                Log.e("Data", "" + a.getRecovery());
                values.add(new Entry(c, a.getRecovery()));
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
        dialogWeeklyRecoveryDetailsBinding.chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) (value - 1));
            }
        });
        LineDataSet set1;
        if (dialogWeeklyRecoveryDetailsBinding.chart.getData() != null &&
                dialogWeeklyRecoveryDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogWeeklyRecoveryDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogWeeklyRecoveryDetailsBinding.chart.getData().notifyDataChanged();
            dialogWeeklyRecoveryDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogWeeklyRecoveryDetailsBinding.chart.setData(data);
        }
        dialogWeeklyRecoveryDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogWeeklyRecoveryDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyRecoveryDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogWeeklyRecoveryDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogWeeklyRecoveryDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogWeeklyRecoveryDetailsBinding.chart.getDescription().setEnabled(false);
        dialogWeeklyRecoveryDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setLBreathData() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                Log.e("Data", "" + a.getBreathRate());
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
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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

    private void setMHeartData() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getHeartRate() != null) {
                values.add(new Entry(c, a.getHeartRate()));
                c = c + 1;
            }
        }
        LineDataSet set1;
        if (dialogMonthlyHeartDetailsBinding.chart.getData() != null &&
                dialogMonthlyHeartDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlyHeartDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlyHeartDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlyHeartDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlyHeartDetailsBinding.chart.setData(data);
        }
        dialogMonthlyHeartDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlyHeartDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyHeartDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyHeartDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlyHeartDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlyHeartDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlyHeartDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setMSleepRateDate() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getSleepscore() != null) {
                Log.e("Data", "" + a.getSleepscore());
                values.add(new Entry(c, a.getSleepscore()));
                c = c + 1;
            }
        }
        LineDataSet set1;
        if (dialogMonthlySleeprateDetailsBinding.chart.getData() != null &&
                dialogMonthlySleeprateDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlySleeprateDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogMonthlySleeprateDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlySleeprateDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlySleeprateDetailsBinding.chart.setData(data);
        }
        dialogMonthlySleeprateDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlySleeprateDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlySleeprateDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlySleeprateDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlySleeprateDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlySleeprateDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlySleeprateDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setMLBreathData() {
        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getBreathRate() != null) {
                Log.e("Data", "" + a.getBreathRate());
                values.add(new Entry(c, a.getBreathRate()));
                c = c + 1;
            }
        }
        LineDataSet set1;
        if (dialogMonthlyBreathrateDetailsBinding.chart.getData() != null &&
                dialogMonthlyBreathrateDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlyBreathrateDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogMonthlyBreathrateDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlyBreathrateDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlyBreathrateDetailsBinding.chart.setData(data);
        }
        dialogMonthlyBreathrateDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlyBreathrateDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBreathrateDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBreathrateDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlyBreathrateDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlyBreathrateDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlyBreathrateDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setMBloodPressure() {

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> values1 = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getBP() != null) {
                values.add(new Entry(c, a.getBP().getSystole()));
                values1.add(new Entry(c, a.getBP().getDiastole()));
                c = c + 1;
            }
        }
        LineDataSet set1, set2;
        if (dialogMonthlyBloodpressureDetailsBinding.chart.getData() != null &&
                dialogMonthlyBloodpressureDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlyBloodpressureDetailsBinding.chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) dialogMonthlyBloodpressureDetailsBinding.chart.getData().getDataSetByIndex(1);
            set1.setValues(values);
            set2.setValues(values1);
            set1.setDrawIcons(false);
            set2.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set2.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set2.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set2.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.Healthy));
            set2.setColor(ContextCompat.getColor(mContext, R.color.Persimmon));
            set1.setCircleRadius(3f);
            set2.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set2.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set2.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set2.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set2.setLabel("");
            set1.setFormSize(15.f);
            set2.setFormSize(15.f);
            dialogMonthlyBloodpressureDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlyBloodpressureDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set2 = new LineDataSet(values1, "");
            set1.setDrawIcons(false);
            set2.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set2.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.Healthy));
            set2.setColor(ContextCompat.getColor(mContext, R.color.Persimmon));
            set1.setLineWidth(1f);
            set2.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set2.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set2.setDrawCircleHole(false);
            set1.setLabel("");
            set2.setLabel("");
            set1.setValueTextSize(9f);
            set2.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set2.setFormLineWidth(1f);
            set1.setValueTextColor(Color.WHITE);
            set2.setValueTextColor(Color.WHITE);
            set1.setFormSize(15.f);
            set2.setFormSize(15.f);
            LineData data = new LineData(set1, set2);
            dialogMonthlyBloodpressureDetailsBinding.chart.setData(data);
        }
        dialogMonthlyBloodpressureDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlyBloodpressureDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBloodpressureDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBloodpressureDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlyBloodpressureDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlyBloodpressureDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlyBloodpressureDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setMBloodOxygen() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getO2() != null) {
                Log.e("Data", "" + a.getO2());
                values.add(new Entry(c, a.getO2()));
                c = c + 1;
            }
        }
        LineDataSet set1;
        if (dialogMonthlyBloodoxygenDetailsBinding.chart.getData() != null &&
                dialogMonthlyBloodoxygenDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlyBloodoxygenDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogMonthlyBloodoxygenDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlyBloodoxygenDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlyBloodoxygenDetailsBinding.chart.setData(data);
        }
        dialogMonthlyBloodoxygenDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlyBloodoxygenDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBloodoxygenDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyBloodoxygenDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlyBloodoxygenDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlyBloodoxygenDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlyBloodoxygenDetailsBinding.chart.getLegend().setEnabled(false);
    }

    private void setMRecoveryRateDate() {

        ArrayList<Entry> values = new ArrayList<>();
        int c = 1;
        for (UserData a : tempList) {
            if (a.getRecovery() != null) {
                Log.e("Data", "" + a.getRecovery());
                values.add(new Entry(c, a.getRecovery()));
                c = c + 1;
            }
        }
        LineDataSet set1;
        if (dialogMonthlyRecoveryDetailsBinding.chart.getData() != null &&
                dialogMonthlyRecoveryDetailsBinding.chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) dialogMonthlyRecoveryDetailsBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
            set1.setHighLightColor(R.color.ShipCove);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.WHITE);
            set1.setLabel("");
            set1.setFormSize(15.f);
            dialogMonthlyRecoveryDetailsBinding.chart.getData().notifyDataChanged();
            dialogMonthlyRecoveryDetailsBinding.chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setCircleColor(Color.WHITE);
            set1.setColor(ContextCompat.getColor(mContext, R.color.ShipCove));
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
            dialogMonthlyRecoveryDetailsBinding.chart.setData(data);
        }
        dialogMonthlyRecoveryDetailsBinding.chart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.White)); // left y-axis
        dialogMonthlyRecoveryDetailsBinding.chart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyRecoveryDetailsBinding.chart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.White));
        dialogMonthlyRecoveryDetailsBinding.chart.getAxisLeft().setDrawGridLines(false);
        dialogMonthlyRecoveryDetailsBinding.chart.getXAxis().setDrawGridLines(false);
        dialogMonthlyRecoveryDetailsBinding.chart.getDescription().setEnabled(false);
        dialogMonthlyRecoveryDetailsBinding.chart.getLegend().setEnabled(false);
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

}