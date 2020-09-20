package com.motishare.dozeecodeforhealth.utils;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.motishare.dozeecodeforhealth.R;

import org.jetbrains.annotations.NotNull;

public class Dialogs {

    public static Object OnDialoagOkClick;

    @NotNull
    public static AlertDialog alertDialogWithTwoButtons(
            String message,
            String positiveText,
            String negativeText,
            final Context context
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message).setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.setMessage(message).setPositiveButton(positiveText, (dialog, which) -> {

        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

    @NotNull
    public static AlertDialog alertDialogWithOkNonCancellable(
            String message,
            final Context context
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.okay),
                        (dialog, which) -> {

                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

    @NotNull
    public static AlertDialog alertDialogWithTwoButtonsReversed(
            String message,
            String positiveText,
            String negativeText,
            final Context context
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message).setNegativeButton(positiveText, (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.setMessage(message).setPositiveButton(negativeText, (dialog, which) -> {

        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static void alertDialogDeny(String msg, final Context mContext) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final Intent i = new Intent();
                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package:" + mContext.getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                mContext.startActivity(i);
                            }
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    public static AlertDialog alertDialogGPS(String msg, final Context mContext) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.ok),
                        (dialog, id) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            mContext.startActivity(intent);
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try {
            alertDialog.show();
        } catch (Exception e) {
        }
        return alertDialog;
    }


    public static void alertDialog(String message, final Context context) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message)
                .setNegativeButton(R.string.okay,
                        (dialog, id) -> dialog.dismiss());
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void alertDialogNonCancellable(String message, final Context context) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setNegativeButton(R.string.okay,
                        (dialog, id) -> dialog.dismiss());
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void notifyalert(String message, String buttontext, final Context context) {
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_with_one_button, null);
        TextView mes = alertLayout.findViewById(R.id.tile_text);
        Button ok = alertLayout.findViewById(R.id.ok);
        ok.setText(buttontext);
        mes.setText(message);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("");
        alert.setCancelable(false);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        ok.setOnClickListener(v -> dialog.dismiss());
    }

    public static void notifyalert(String message, String buttontext, final Context context,boolean isVisible, OnDialoagOkClick okClick) {
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_with_one_button, null);
        TextView mes = alertLayout.findViewById(R.id.tile_text);
        if(isVisible)
            mes.setVisibility(View.VISIBLE);
        else
            mes.setVisibility(View.GONE);
        mes.setText("Caution");
        TextView tv_subtitle = alertLayout.findViewById(R.id.tv_subtitle);
        tv_subtitle.setVisibility(View.VISIBLE);
        Button ok = alertLayout.findViewById(R.id.ok);
        ok.setText(buttontext);
        tv_subtitle.setText(message);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("");
        alert.setCancelable(false);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            okClick.onItemClick(ok);
        });

    }

    public static void notifyalerttwobutton(String message, String buttontext,String buttontext2, final Context context,boolean isVisible, OnDialoagOkClick okClick) {
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_with_two_button, null);
        TextView mes = alertLayout.findViewById(R.id.tile_text);
        if(isVisible)
            mes.setVisibility(View.VISIBLE);
        else
            mes.setVisibility(View.GONE);
        mes.setText("Caution");
        TextView tv_subtitle = alertLayout.findViewById(R.id.tv_subtitle);
        tv_subtitle.setVisibility(View.VISIBLE);
        Button ok = alertLayout.findViewById(R.id.ok);
        Button cancel = alertLayout.findViewById(R.id.cancel);
        ok.setText(buttontext);
        cancel.setText(buttontext2);
        tv_subtitle.setText(message);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("");
        alert.setCancelable(false);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            okClick.onItemClick(ok);
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }


    public interface OnDialoagOkClick {
        void onItemClick(Button ok);
    }

}