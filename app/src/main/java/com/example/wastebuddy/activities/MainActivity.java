package com.example.wastebuddy.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.ActivityMainBinding;
import com.example.wastebuddy.fragments.HomeFragment;
import com.example.wastebuddy.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    BottomNavigationView mBottomNavigationView;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBottomNavigationView = mBinding.bottomNavigationView;
        setBottomNavItemSelectedListener();
    }

    public void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(mBinding.containerFrameLayout.getId(), fragment).commit();
    }

    private void setBottomNavItemSelectedListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.homeMenuItem:
                        fragment = new HomeFragment();
                        break;
                    case R.id.searchMenuItem:
                    default:
                        fragment = new SearchFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(mBinding.containerFrameLayout.getId(), fragment).commit();
                return true;
            }
        });
        // Set default selection
        mBottomNavigationView.setSelectedItemId(R.id.homeMenuItem);
    }
}