package com.dialogapp.dialog.ui.settings;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class SettingsFragment extends PreferenceFragmentCompatDividers {
    private SettingsFragmentEventListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SettingsFragmentEventListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement SettingsFragmentEventListener");
        }
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            setDividerPreferences(DIVIDER_PADDING_CHILD | DIVIDER_CATEGORY_AFTER_LAST | DIVIDER_CATEGORY_BETWEEN);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference locationPreference = findPreference("pref_locationPermission");
        locationPreference.setVisible(Build.VERSION.SDK_INT >= 23);
        locationPreference.setOnPreferenceClickListener(preference -> {
            listener.onPreferenceClicked();
            return true;
        });
    }

    public interface SettingsFragmentEventListener {
        void onPreferenceClicked();
    }
}
