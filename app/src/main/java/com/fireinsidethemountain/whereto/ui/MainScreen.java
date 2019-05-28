package com.fireinsidethemountain.whereto.ui;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import com.fireinsidethemountain.whereto.R;
import com.fireinsidethemountain.whereto.model.ProgramClient;



public class MainScreen extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {



    private Button _food;
    private Button _accommodation;
    private Button _facilities;
    private Button _events;
    private Button _credits;
    private Button _profile;
    private AlphaAnimation _buttonClick = new AlphaAnimation(1f, 0.8f);

    private ActionBarDrawerToggle _toggle;
    private DrawerLayout _drawerLayout;

    private ProgramClient _programClient = ProgramClient.getInstance();
    private Fragment _mapScreenFragment = new MapScreen();
    private Fragment _foodModuleFragment = new FoodModule();
    private Fragment _currentFragment;

    private FragmentManager _fragmentManager = getSupportFragmentManager();
    private FragmentTransaction _fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        _buttonClick.setDuration(300);
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _toggle = new ActionBarDrawerToggle(this, _drawerLayout, R.string.open, R.string.close);
        _drawerLayout.addDrawerListener(_toggle);
        _toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _food = (Button) findViewById(R.id.foodButton);
        _food.setOnClickListener(this);

        _accommodation = (Button) findViewById(R.id.stayButton);
        _accommodation.setOnClickListener(this);
        _events = (Button) findViewById(R.id.eventsButton);
        _events.setOnClickListener(this);
        _facilities = (Button) findViewById(R.id.facilitiesButton);
        _facilities.setOnClickListener(this);
        _credits = (Button) findViewById(R.id.creditsButton);
        _credits.setOnClickListener(this);
        _profile = (Button) findViewById(R.id.profileButton);
        _profile.setOnClickListener(this);

        _fragmentTransaction = _fragmentManager.beginTransaction();
        _fragmentTransaction.add(R.id.screen_area, _foodModuleFragment);
        _fragmentTransaction.add(R.id.screen_area, _mapScreenFragment);
        _currentFragment = _mapScreenFragment;
        _fragmentTransaction.show(_mapScreenFragment);
        _fragmentTransaction.commit();

    }



    @Override
    public void onClick(View view) {

        Fragment fragment = null;


        if (view == _food) {
            view.startAnimation(_buttonClick);
            //fragment = new FoodModule();
            fragment = _foodModuleFragment;
            Log.d("tag", "onComplete: kurwa3");
        } else if (view == _profile) {
            fragment = _mapScreenFragment;
            view.startAnimation(_buttonClick);
        } else if (view == _credits) {
            view.startAnimation(_buttonClick);
        } else if (view == _accommodation) {
            view.startAnimation(_buttonClick);
        } else if (view == _events) {
            view.startAnimation(_buttonClick);
        } else if (view == _facilities) {
            view.startAnimation(_buttonClick);
        }

        if (fragment != null) {
            _fragmentTransaction = _fragmentManager.beginTransaction();
            _fragmentTransaction.hide(_currentFragment);
            _currentFragment = fragment;
            _fragmentTransaction.show(_currentFragment);
            _fragmentTransaction.commit();
            _drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
