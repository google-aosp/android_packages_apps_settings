/*
 * Copyright (C) 2016 RR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.aosp;

import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Build;
import com.android.settings.util.AbstractAsyncSuCMDProcessor;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import com.android.settings.util.Helpers;
import dalvik.system.VMRuntime;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.List;
import com.android.settings.Utils;

import java.io.File;
import java.io.IOException;
import java.io.DataOutputStream;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class MiscSettings extends SettingsPreferenceFragment  implements OnPreferenceChangeListener{

private static final String DT2W = "dt2w";


private SwitchPreference mDt2w;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aosp_misc);
	  	final ContentResolver resolver = getActivity().getContentResolver();

  		//DT2W
        mDt2w = (SwitchPreference) findPreference(DT2W);
        mDt2w.setOnPreferenceChangeListener(this);

 	 		if (CMDProcessor.runShellCommand("cat sys/android_touch/doubletap2wake").getStdout().contains("1")) {
            mDt2w.setChecked(true);
            mDt2w.setSummary(R.string.dt2w_on_title);
        	} else {
            mDt2w.setChecked(false);
            mDt2w.setSummary(R.string.dt2w_off_title);
         	}

		}

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.APPLICATION;
    }

	@Override
    public void onResume() {
        super.onResume();
    }

     private void setDt2wEnabled(String status) {
         SharedPreferences.Editor editor = getContext().getSharedPreferences("dt2w_pref", Context.MODE_PRIVATE).edit();
         editor.putString("dt2w", status);
         editor.apply();
     }

	 @Override
     public boolean onPreferenceChange(Preference preference, Object value) {
     ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mDt2w) {
            if (value.toString().equals("true")) {
                CMDProcessor.runShellCommand("echo 1 > /sys/android_touch/doubletap2wake");
                mDt2w.setSummary(R.string.dt2w_on_title);
                setDt2wEnabled("true");
            } else if (value.toString().equals("false")) {
                CMDProcessor.runShellCommand("echo 0 > /sys/android_touch/doubletap2wake");
                mDt2w.setSummary(R.string.dt2w_off_title);
                setDt2wEnabled("false");
            }
            return true;
         }
        return false;
     }
}

