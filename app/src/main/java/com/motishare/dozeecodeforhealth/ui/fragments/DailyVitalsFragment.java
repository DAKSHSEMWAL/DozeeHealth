package com.motishare.dozeecodeforhealth.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.motishare.dozeecodeforhealth.R;
import com.motishare.dozeecodeforhealth.core.BaseFragment;
import com.motishare.dozeecodeforhealth.databinding.FragmentBlankBinding;
import com.motishare.dozeecodeforhealth.ui.activity.MainActivity;
import com.motishare.dozeecodeforhealth.utils.Common;

import java.io.File;

public class DailyVitalsFragment extends BaseFragment implements View.OnClickListener {

    FragmentBlankBinding binding;

    private static DailyVitalsFragment dailyVitalsFragment;
    private LOADER_TYPE loader_type = LOADER_TYPE.NORMAL;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blank, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        implementListeners();
    }

    private void initialize() {
    }

    private void implementListeners() {

        if(getActivity() instanceof MainActivity)
        {
            ((MainActivity) getActivity()).binding.share.setOnClickListener(view -> {
                File file=Common.BitmapToFile(Common.screenShot(((MainActivity) getActivity()).binding.getRoot()),mContext);
                Uri uri = FileProvider.getUriForFile(mContext, "com.motishare.dozeecodeforhealth.provider", file);
                Intent intent = ShareCompat.IntentBuilder.from(mContext)
                        .setType("image/jpg")
                        .setStream(uri)
                        .setChooserTitle(R.string.share_title)
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                mContext.startActivity(intent);
            });
        }

    }

    public DailyVitalsFragment getInstance() {

        if (dailyVitalsFragment == null)
            dailyVitalsFragment = new DailyVitalsFragment();
        return dailyVitalsFragment;

    }

    @Override
    public void onDetach() {

        super.onDetach();

    }

    @Override
    public void onClick(View view) {

    }
}