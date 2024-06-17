package com.example.recipeapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.recipeapp.Fragment.FavoritesFragment;
import com.example.recipeapp.Fragment.HomeFragment;
import com.example.recipeapp.Fragment.SearchFragment;
import com.example.recipeapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish(); // The user can't come back to this page
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            // Reset all icons to baseline
            bottomNavigationView.getMenu().findItem(R.id.nav_home).setIcon(R.drawable.outline_home_24);
            bottomNavigationView.getMenu().findItem(R.id.nav_favorites).setIcon(R.drawable.baseline_bookmark_border_24);
            bottomNavigationView.getMenu().findItem(R.id.nav_search).setIcon(R.drawable.round_search_24);

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    item.setIcon(R.drawable.baseline_home_filled_24);
                    break;
                case R.id.nav_favorites:
                    selectedFragment = new FavoritesFragment();
                    item.setIcon(R.drawable.baseline_bookmark_filled_24);
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    item.setIcon(R.drawable.outline_search_check_24);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
