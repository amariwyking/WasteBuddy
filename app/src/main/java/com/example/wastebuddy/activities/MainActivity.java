package com.example.wastebuddy.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.ActivityMainBinding;
import com.example.wastebuddy.fragments.HomeFragment;
import com.example.wastebuddy.fragments.ScannerFragment;
import com.example.wastebuddy.fragments.SearchFragment;
import com.example.wastebuddy.fragments.UserFragment;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseQuery;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment mActiveFragment;
    SearchFragment mSearchFragment;

    ActivityMainBinding mBinding;
    BottomNavigationView mBottomNavigationView;
    PersistentSearchView searchView;

    List<Item> mResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        searchView = mBinding.persistentSearchView;
        mResults = new ArrayList<>();
        configureSearchView();

        mBottomNavigationView = mBinding.bottomNavigationView;
        mBottomNavigationView.inflateMenu(User.isSignedIn()
                ? R.menu.bottom_navigation_menu
                : R.menu.bottom_navigation_menu_guest);
        setBottomNavItemSelectedListener();
    }

    public void replaceFragment(Fragment fragment) {
        if (!(fragment instanceof HomeFragment) && !(fragment instanceof SearchFragment)) {
            hideSearchView();
        }

        if (mBottomNavigationView.getVisibility() == View.GONE)
            showBottomNav();

        if (fragment instanceof ScannerFragment) hideBottomNav();

        fragmentManager
                .beginTransaction()
                .replace(mBinding.containerFrameLayout.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setBottomNavItemSelectedListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeMenuItem) {
                mActiveFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.searchMenuItem) {
                mActiveFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.profileMenuItem) {
                UserFragment fragment = new UserFragment();
                Bundle bundle = new Bundle();
                bundle.putString(User.KEY_UID,
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                fragment.setArguments(bundle);
                mActiveFragment = fragment;
            }

            replaceFragment(mActiveFragment);
            return true;
        });
        // Set default selection
        mBottomNavigationView.setSelectedItemId(R.id.homeMenuItem);
    }

    private void configureSearchView() {
        searchView.setVoiceInputButtonEnabled(false);
        searchView.setSuggestionsDisabled(true);
        searchView.showRightButton();


        searchView.setOnLeftBtnClickListener(view -> {
            // Handle the left button click
            Toast.makeText(MainActivity.this, "Left button click", Toast.LENGTH_SHORT).show();
        });


        searchView.setOnRightBtnClickListener(view -> Navigation.switchFragment(MainActivity.this
                , ScannerFragment.newInstance(ScannerFragment.TASK_SEARCH)));

        searchView.setRightButtonDrawable(getDrawable(R.drawable.ic_barcode_scanner_icon));

        searchView.setOnSearchConfirmedListener((searchView, queryInput) -> {
            // Handle a search confirmation. This is the place where you'd
            // want to perform a search against your data provider.

            // 1. Perform query against Parse
            query(queryInput);
            // Display list of items in recycler view
            searchView.collapse();

            mSearchFragment = new SearchFragment(mResults);
            replaceFragment(mSearchFragment);
        });

        // Disabling the suggestions since they are unused in
        // the simple implementation
    }

    // TODO: Reimplement with Firestore Query
    private void query(String input) {
        // Specify which class to query
        /*ParseQuery<Item> parseQuery = ParseQuery.getQuery(Item.class);
        parseQuery.include(Item.KEY_AUTHOR);
        parseQuery.whereContains(Item.KEY_NAME_LOWERCASE, input.toLowerCase());
        parseQuery.findInBackground((items, e) -> {
            if (e != null) {
                Log.e(TAG, "Problem  with querying items", e);
                return;
            }
            for (Item item : items) {
//                Log.i(TAG,
//                        "Item: " + item.getName() + ", Name: " + item.getAuthor().getUsername());
            }
            mResults.clear();
            mSearchFragment.updateData(items);
        });*/
    }

    public void showBottomNav() {
        if (mBottomNavigationView.getVisibility() == View.GONE)
            mBottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNav() {
        if (mBottomNavigationView.getVisibility() == View.VISIBLE)
            mBottomNavigationView.setVisibility(View.GONE);
    }

    public void showSearchView() {
        if (searchView.getVisibility() == View.GONE)
            searchView.setVisibility(View.VISIBLE);
    }

    public void hideSearchView() {
        if (searchView.getVisibility() == View.VISIBLE)
            searchView.setVisibility(View.GONE);
    }
}