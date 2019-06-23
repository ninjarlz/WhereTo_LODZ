package com.fireinsidethemountain.whereto.ui;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.ProgramClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class MainScreen extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


    private ImageView _profileImage;
    private Button _food;
    private Button _accommodation;
    private Button _facilities;
    private Button _events;
    private Button _credits;
    private Button _profile;
    private Button _home;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);

    private ActionBarDrawerToggle _toggle;
    private DrawerLayout _drawerLayout;

    private ProgramClient _programClient = ProgramClient.getInstance();
    private FirebaseAuth _auth = FirebaseAuth.getInstance();

    private MapScreen _mapScreenFragment = new MapScreen();
    private Credits _creditsFragment = new Credits();
    private EnquireCreator _enquireCreatorFragment = new EnquireCreator();
    private FoodModule _foodModuleFragment = new FoodModule();
    private MainMenu _mainMenuFragment;
    private Settings _settingsFragment = new Settings();
    private AnswerCreator _answerCreator;
    private Fragment _currentFragment;
    private FragmentManager _fragmentManager = getSupportFragmentManager();
    private FragmentTransaction _fragmentTransaction;

    private SharedPreferences _pref;
    private Resources _resources;
    private Configuration _conf;

    public SharedPreferences getPref() {
        return _pref;
    }

    public Configuration getConf() {
        return _conf;
    }

    public Settings getSettingsFragment () {
        return _settingsFragment;
    }

    public MapScreen getMapScreenFragment () {
        return _mapScreenFragment;
    }

    public EnquireCreator getEnquireCreatorFragment () {
        return _enquireCreatorFragment;
    }

    public Fragment getAnswerCreatorFragment () {
        return _answerCreator;
    }

    public Fragment getCurrentFragment() {
        return _currentFragment;
    }

    public void setCurrentFragment(Fragment fragment) {
        if (fragment != null) {
            if(fragment != _currentFragment) {
                _fragmentTransaction = _fragmentManager.beginTransaction();
                _fragmentTransaction.hide(_currentFragment);
                _currentFragment = fragment;
                _fragmentTransaction.show(_currentFragment);
                _fragmentTransaction.commit();
            }
            _drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        _resources = getResources();
        _conf = _resources.getConfiguration();
        if (_pref.contains("lang_code")) {
            String lang = _pref.getString("lang_code", "en");
            String currentLang = _conf.locale.getLanguage();
            if (!currentLang.equals(lang)) {
                setLocale(lang);
            }
        }
        setContentView(R.layout.activity_main_screen);
        _buttonClick.setDuration(300);
        _drawerLayout = findViewById(R.id.drawer_layout);
        _toggle = new ActionBarDrawerToggle(this, _drawerLayout, R.string.open, R.string.close);
        _drawerLayout.addDrawerListener(_toggle);
        _toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        _profileImage = findViewById(R.id.profile);

        String imgUrl = _auth.getCurrentUser().getPhotoUrl().toString();
        Glide.with(this).load(imgUrl).apply(RequestOptions.circleCropTransform()).into(_profileImage);

        _food = findViewById(R.id.foodButton);
        _food.setOnClickListener(this);
        _home = findViewById(R.id.home);
        _home.setOnClickListener(this);
        _accommodation = findViewById(R.id.stayButton);
        _accommodation.setOnClickListener(this);
        _events = findViewById(R.id.eventsButton);
        _events.setOnClickListener(this);
        _facilities = findViewById(R.id.facilitiesButton);
        _facilities.setOnClickListener(this);
        _credits = findViewById(R.id.creditsButton);
        _credits.setOnClickListener(this);
        _profile = findViewById(R.id.profileButton);
        _profile.setOnClickListener(this);

        _mainMenuFragment = _mapScreenFragment.getMainMenu();
        _answerCreator = _mapScreenFragment.getAnswerCreator();


        _fragmentTransaction = _fragmentManager.beginTransaction();
        _fragmentTransaction.add(R.id.screen_area, _creditsFragment);
        _fragmentTransaction.hide(_creditsFragment);
        _fragmentTransaction.add(R.id.screen_area, _enquireCreatorFragment);
        _fragmentTransaction.hide(_enquireCreatorFragment);
        _fragmentTransaction.add(R.id.screen_area, _foodModuleFragment);
        _fragmentTransaction.hide(_foodModuleFragment);
        _fragmentTransaction.add(R.id.screen_area, _settingsFragment);
        _fragmentTransaction.hide(_settingsFragment);
        _fragmentTransaction.add(R.id.screen_area, _mapScreenFragment);
        _currentFragment = _mapScreenFragment;
        _fragmentTransaction.commit();

    }



    @Override
    public void onClick(View view) {

        Fragment fragment = null;

        if (view == _food) {
            view.startAnimation(_buttonClick);
            fragment = _foodModuleFragment;
            Log.d("tag", "onComplete: kurwa3");
        } else if (view == _profile) {
            view.startAnimation(_buttonClick);
        } else if (view == _credits) {
            view.startAnimation(_buttonClick);
            fragment = _creditsFragment;
        } else if (view == _accommodation) {
            view.startAnimation(_buttonClick);
        } else if (view == _events) {
            view.startAnimation(_buttonClick);
        } else if (view == _facilities) {
            view.startAnimation(_buttonClick);
        } else if (view == _home) {
            view.startAnimation(_buttonClick);
            _mapScreenFragment.setCurrentFragment(_mainMenuFragment);
            fragment = _mapScreenFragment;
        }
        setCurrentFragment(fragment);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (_currentFragment == _mapScreenFragment) {
            if (_mapScreenFragment.getCurrentFragment() == _answerCreator) {
                setCurrentFragment(_answerCreator.getPreviousFragment());
            } else {
                super.onBackPressed();
            }
        } else if (_currentFragment == _foodModuleFragment) {
            _mapScreenFragment.setCurrentFragment(_mainMenuFragment);
            setCurrentFragment(_mapScreenFragment);
        } else if (_currentFragment == _creditsFragment) {
            _mapScreenFragment.setCurrentFragment(_mainMenuFragment);
            setCurrentFragment(_mapScreenFragment);
        } else if (_currentFragment == _enquireCreatorFragment) {
            setCurrentFragment(_enquireCreatorFragment.getPreviousFragment());
        } else if (_currentFragment == _settingsFragment) {
            setCurrentFragment(_mapScreenFragment);
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        DisplayMetrics dm = _resources.getDisplayMetrics();
        Configuration conf = _resources.getConfiguration();
        conf.locale = locale;
        _resources.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainScreen.class);
        startActivity(refresh);
        finish();
    }

}