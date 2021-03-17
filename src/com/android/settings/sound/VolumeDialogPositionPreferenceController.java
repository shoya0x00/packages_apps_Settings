/*
 * Copyright (C) 2021 Yet Another AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.sound;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;

/**
 * A simple preference controller for volume dialog position
 */
public class VolumeDialogPositionPreferenceController extends AbstractPreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY = "volume_panel_on_left";

    private SwitchPreference mPreference;

    public VolumeDialogPositionPreferenceController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = (SwitchPreference) screen.findPreference(KEY);

        // Get config_audioPanelOnLeftSide from SystemUI
        Context sysUiContext;
        try {
            sysUiContext = mContext.createPackageContext("com.android.systemui",
                    Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (NameNotFoundException e) {
            // Nothing to do, If SystemUI was not found you have bigger issues :)
            sysUiContext = mContext;
        }
        Resources sysUiRes = sysUiContext.getResources();
        int resId = sysUiRes.getIdentifier("config_audioPanelOnLeftSide", "bool", "com.android.systemui");
        boolean isLeft = sysUiRes.getBoolean(resId);

        boolean value = Settings.System.getIntForUser(mContext.getContentResolver(),
                KEY, isLeft ? 1 : 0, UserHandle.USER_CURRENT) == 1;
        mPreference.setChecked(value);
        mPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Settings.System.putIntForUser(mContext.getContentResolver(),
                KEY, (Boolean) newValue ? 1 : 0, UserHandle.USER_CURRENT);
        return true;
    }
}
