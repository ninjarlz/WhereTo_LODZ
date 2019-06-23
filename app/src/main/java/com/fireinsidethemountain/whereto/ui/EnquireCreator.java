package com.fireinsidethemountain.whereto.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.Enquire;
import com.fireinsidethemountain.whereto.model.ProgramClient;


public class EnquireCreator extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner _spinner;
    private String[] _spinnerPaths;
    private Enquire.EnquireType _currentType;
    private EditText _enquireContent;
    private Button _postEnquireButton;
    private Fragment _previousFragment;
    private MainScreen _mainScreen;
    private InputMethodManager _imm;
    private View _view;
    private ProgramClient _programClient = ProgramClient.getInstance();


    public void setPreviousFragment (Fragment previousFragment) {
        _previousFragment = previousFragment;
    }

    public Fragment getPreviousFragment() {
        return _previousFragment;
    }

    public void setCurrentType(Enquire.EnquireType currentType) {
        _currentType = currentType;
        _spinner.setSelection(_currentType.ordinal());
    }

    public Spinner getSpinner() {
        return _spinner;
    }

    public void resetEnquireContent() {
        _enquireContent.setText("");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.enquire_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _spinnerPaths = new String[] {
                getResources().getString(R.string.food),
                getResources().getString(R.string.stay),
                getResources().getString(R.string.events),
                getResources().getString(R.string.facilities)};
        _spinner = view.findViewById(R.id.spinner_enquire_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, _spinnerPaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);
        _spinner.setOnItemSelectedListener(this);
        _enquireContent = view.findViewById(R.id.enquire_content);
        _postEnquireButton = view.findViewById(R.id.postEnquire);
        _postEnquireButton.setOnClickListener(this);
        _mainScreen = (MainScreen) getActivity();
        _view = view;
        _imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        if (view == _postEnquireButton) {
            String enquireContent = _enquireContent.getText().toString().trim();
            if (enquireContent.length() >= 8) {
                _imm.hideSoftInputFromWindow(_view.getWindowToken(), 0);
                _programClient.writeNewPost(enquireContent, _currentType);
                Toast.makeText(getContext(), getResources().getString(R.string.yourInquiryPosted), Toast.LENGTH_SHORT).show();
                _mainScreen.setCurrentFragment(_previousFragment);
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.yourInquiry), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setCurrentType(Enquire.EnquireType.values()[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
