package com.bottom.footballtv;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bottom.footballtv.databinding.ActivityMainBinding;
import com.bottom.footballtv.ui.games.GamesFragment;
import com.bottom.footballtv.ui.home.HomeFragment;
import com.bottom.footballtv.ui.more.MoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment activeFragment;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Fragment homeFragment = new HomeFragment();
        Fragment gamesFragment = new GamesFragment();
        Fragment moreFragment = new MoreFragment();

        BottomNavigationView navigationView = binding.navView;

        FragmentManager fm = getSupportFragmentManager();

        final Fragment[] active = {homeFragment};
        activeFragment = homeFragment;

        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, moreFragment, "3").hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, gamesFragment, "2").hide(gamesFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, homeFragment, "1").commit();

        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                } else if (id == R.id.navigation_games) {
                    fm.beginTransaction().hide(active[0]).show(gamesFragment).commit();

                } else if (id == R.id.navigation_more) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    fm.beginTransaction().hide(active[0]).show(homeFragment).commit();
                    active[0] = homeFragment;

                    activeFragment = homeFragment;
                    return true;
                } else if (id == R.id.navigation_games) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().hide(active[0]).show(gamesFragment).commit();
                    active[0] = gamesFragment;

                    activeFragment = gamesFragment;
                    return true;
                } else if (id == R.id.navigation_more) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().hide(active[0]).show(moreFragment).commit();
                    active[0] = moreFragment;

                    activeFragment = moreFragment;
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (activeFragment instanceof MoreFragment) {
            binding.navView.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }
}