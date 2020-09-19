package com.motishare.dozeecodeforhealth.ui.activity;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.core.BaseActivity;
import com.motishare.dozeecodeforhealth.databinding.ActivityMainBinding;
import com.motishare.dozeecodeforhealth.utils.Common;

import java.util.Calendar;
import java.util.Date;

import static com.motishare.dozeecodeforhealth.utils.Common.setToolbarWithBackAndTitle;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mContext,R.layout.activity_main);
        initView();
    }

    private void initView() {
        setToolbarWithBackAndTitle(mContext,"Risi Shah",false,0);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        binding.todaydate.setText(String.format("Today - %s", Common.getDate(DATE20, DATE4, c.toString())));
    }
}