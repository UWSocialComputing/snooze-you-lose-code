package com.capstone481p.snoozeyoulose;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This activity primarily handles signing into the application

        // Check if someone is signed in, start sign in otherwise
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            Toast.makeText(this,
                            "Welcome " + FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getDisplayName(),
                            Toast.LENGTH_LONG)
                    .show();
            // Loads account information
            moveToDashboard();
        }


        // Logs the Firebase installation id for your emulator/device,
        // Necessary for some Firebase console-side testing
        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Installations", "Installation ID: " + task.getResult());
                    } else {
                        Log.e("Installations", "Unable to get Installation ID");
                    }
                });

    }

    /**
     * Starts the dashboard activity with the user's information once signed in
     */
    public void moveToDashboard(){

        logNewUser();
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();

    }

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
                        Toast.makeText(MainActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                .show();
                        finish();
                    });
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                                "You have been signed in. Welcome!",
                                Toast.LENGTH_LONG)
                        .show();

                moveToDashboard();
            } else {
                Toast.makeText(this,
                                "We were unable to sign you in.",
                                Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    /**
     * Saves user information into the database
     */
    private void logNewUser(){

        String picture = "";

        // Randomly selects a profile image for each user
        int randomPicture = (int)(Math.random() * 5);

        switch (randomPicture){
            case 0:
                picture = "profile_1";
                break;
            case 1:
                picture = "profile_2";
                break;
            case 2:
                picture = "profile_3";
                break;
            case 3:
                picture = "profile_4";
                break;
            case 4:
                picture = "profile_5";
                break;

        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        String uid = user.getUid();
        String name = user.getDisplayName();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("uid", uid);
        hashMap.put("name", name);
        hashMap.put("image", picture);
        hashMap.put("accountability", "");
        hashMap.put("bedTime", "");
        hashMap.put("wakeupTime", "");
        hashMap.put("awakeCount", "0");
        hashMap.put("sleepCount", "0");
        hashMap.put("counter", "0");
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference reference = database.getReference("Users");

        reference.child(uid).setValue(hashMap);
    }


}