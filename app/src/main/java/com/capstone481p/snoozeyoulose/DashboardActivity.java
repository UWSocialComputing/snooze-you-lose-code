package com.capstone481p.snoozeyoulose;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.capstone481p.snoozeyoulose.ui.chat.ChatFragment;
import com.capstone481p.snoozeyoulose.ui.home.HomeFragment;
import com.capstone481p.snoozeyoulose.ui.profile.ProfileFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    // NOTE: The ids from each navigation item may
    // change upon changes to xml files. Use the
    // Log statement with the tag "SCREEN" to debug
    // and replace these variables.
    private static final int HOME_BUTTON = 2131362182;
    private static final int PROFILE_BUTTON = 2131362184;
    private static final int CHAT_BUTTON = 2131362180;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);

        // Retrieves the extra that will be added to the intent if you are
        // exiting the chat activity
        String startingFragment = getIntent().getStringExtra("desired_fragment");
        if(startingFragment != null && startingFragment.equals("chat")){
            // Loads chat fragment if returning from chat activity
            actionBar.setTitle("Chats");
            navigationView.setSelectedItemId(R.id.nav_chat);
            Log.d("DEBUG", "Reading desired fragment and switching");
            ChatFragment fragment = new ChatFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment, "");
            fragmentTransaction.commit();
        } else {
            // Default screen for everything else is the home fragment
            actionBar.setTitle("Home");
            navigationView.setSelectedItemId(R.id.nav_home);
            HomeFragment fragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment, "");
            fragmentTransaction.commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            // Keep this log statement in case navigation item ids change
            Log.d("SCREEN", "Change screen: "+ menuItem.getItemId() );

            // Switching fragment based on navigation bar selection
            switch (menuItem.getItemId()) {

                case HOME_BUTTON:
                    actionBar.setTitle("Home");
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case PROFILE_BUTTON:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragmentP = new ProfileFragment();
                    FragmentTransaction fragmentTransactionP = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionP.replace(R.id.content, fragmentP, "");
                    fragmentTransactionP.commit();
                    return true;

                case CHAT_BUTTON:
                    actionBar.setTitle("Chats");
                    ChatFragment listFragment = new ChatFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, listFragment, "");
                    fragmentTransaction3.commit();
                    return true;

            }
            return false;
        }
    };

    // The following methods allow for signing out via the built in options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(task -> {
                        Toast.makeText(DashboardActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                .show();
                        finish();
                    });
        }
        return true;
    }
}