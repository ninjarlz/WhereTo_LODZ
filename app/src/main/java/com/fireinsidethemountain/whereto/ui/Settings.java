package com.fireinsidethemountain.whereto.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.fireinsidethemountain.whereto.R;

public class Settings extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner _spinner;
    private String[] _spinnerPaths;
    private MainScreen _mainScreen;
    private MapScreen _mapScreen;
    private SharedPreferences _pref;
    private SharedPreferences.Editor _editor;
    private Configuration _conf;



    public Spinner getSpinner() {
        return _spinner;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _spinnerPaths = new String[] {
                "English",
                "Polski"};
        _spinner = view.findViewById(R.id.spinner_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, _spinnerPaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);
        _spinner.setOnItemSelectedListener(this);
        _mainScreen = (MainScreen) getActivity();
        _mapScreen = _mainScreen.getMapScreenFragment();
        _pref = _mainScreen.getPref();
        _conf = _mainScreen.getConf();
        setLanguageOnSpinner(_conf.locale.getLanguage());
    }



    public void setLanguageOnSpinner(String lang) {
        switch (lang) {
            case "en":
                _spinner.setSelection(0);
                break;
            case "pl":
                _spinner.setSelection(1);
                break;
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                if (!_conf.locale.getLanguage().equals("en")) {
                    _editor = _pref.edit();
                    _editor.putString("lang_code", "en");
                    _editor.commit();
                    _mainScreen.setLocale("en");
                }
                break;
            case 1:
                if (!_conf.locale.getLanguage().equals("pl")) {
                    _editor = _pref.edit();
                    _editor.putString("lang_code", "pl");
                    _editor.commit();
                    Log.d("tag", "onComplete: " + _pref.getString("lang_code", "en"));
                    _mainScreen.setLocale("pl");
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
