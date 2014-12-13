package ru.ifmo.md.lesson8;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by german on 12.12.14.
 */
public class SettingsDialog extends DialogFragment {
    public static final String UPDATE_WHICH_BUNDLE_KEY = "UPDATE_WHICH";

    private final CharSequence[] intervalTitles = new CharSequence[] {
            "Manually",
            "5 seconds",
            "30 minutes",
            "1 hour",
            "6 hour",
            "12 hour",
            "24 hour"
    };
    private final long[] intervalValues = new long[] {
            -1,
            5 * 1000,
            30 * 60 * 1000,
            1 * 60 * 50 * 1000,
            6 * 60 * 50 * 1000,
            12 * 60 * 50 * 1000,
            24 * 60 * 50 * 1000
    };

    private int newCheckedItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences prefs = getActivity ().getPreferences(0);
        int checkedItem = prefs.getInt(UPDATE_WHICH_BUNDLE_KEY, 0);
        newCheckedItem = -1;
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Choose the automatic updating interval")
                .setPositiveButton("OK", new OnOKListener())
                .setSingleChoiceItems(intervalTitles, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCheckedItem = which;
                    }
                });
        return adb.create();
    }

    class OnOKListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final SharedPreferences prefs = getActivity ().getPreferences(0);
            if (newCheckedItem != -1) {
                prefs.edit().putInt(UPDATE_WHICH_BUNDLE_KEY, newCheckedItem).commit();
                setAutoUpdating();
            }
            dialog.dismiss();
        }
    }

    public void setAutoUpdating() {
        final SharedPreferences prefs = getActivity().getPreferences(0);
        int checked = prefs.getInt(UPDATE_WHICH_BUNDLE_KEY, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        if (checked != 0) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + intervalValues[checked],
                    intervalValues[checked],
                    pendingIntent
            );
        }
    }
}
