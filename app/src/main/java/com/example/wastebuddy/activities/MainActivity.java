package com.example.wastebuddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivityMainBinding;
import com.example.wastebuddy.fragments.CreateItemFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    FloatingActionButton mCreateItemButton;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mCreateItemButton = mBinding.createItemButton;
        mCreateItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new CreateItemFragment());
                mCreateItemButton.hide();
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(mBinding.containerFrameLayout.getId(), fragment).commit();
    }
}