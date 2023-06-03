package com.capstone481p.snoozeyoulose.ui.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone481p.snoozeyoulose.DashboardActivity;
import com.capstone481p.snoozeyoulose.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView profile, close;
    TextView name;
    EditText msg;
    ImageButton send, attach;
    FirebaseAuth firebaseAuth;
    String uid, fid, image;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    private static final int IMAGE_GALLERY_REQUEST = 300;
    private static final int IMAGE_CAMERA_REQUEST = 400;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageUri = null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();

        // Setting aspects of the chat layout
        profile = findViewById(R.id.profiletv);
        name = findViewById(R.id.nameptv);
        close = findViewById(R.id.close_chat);
        msg = findViewById(R.id.messaget);
        send = findViewById(R.id.sendmsg);
        attach = findViewById(R.id.attachbtn);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        uid = getIntent().getStringExtra("uid");

        firebaseDatabase = FirebaseDatabase.getInstance();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkUserStatus();
        users = firebaseDatabase.getReference("Users");

        // Handles adding images in the chat bar by clicking the image button
        attach.setOnClickListener(v -> showImagePicDialog());

        // Sends the content of the text bar on clicking the send button
        send.setOnClickListener(v -> {
            notify = true;
            String message = msg.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(ChatActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
            } else {
                sendMessage(message);
            }
            msg.setText("");
        });

        // Back button sends user back to Dashboard Activity
        close.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, DashboardActivity.class);
            intent.putExtra("desired_fragment", "chat");
            startActivity(intent);
        });

        Query userQuery = users.orderByChild("uid").equalTo(uid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Sets the information about the receiver at the top of the chat activity
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String nameRet = "" + dataSnapshot1.child("name").getValue();
                    image = "" + dataSnapshot1.child("image").getValue();
                    name.setText(nameRet);
                    try {
                        // Finds the image in drawable based on its tag
                        int imageID = ChatActivity.this.getResources().getIdentifier(image, "drawable", ChatActivity.this.getPackageName());
                        Glide.with(ChatActivity.this).load(imageID).placeholder(R.drawable.baseline_star_24).into(profile);
                    } catch (Exception ignored) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readMessages();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Sets up the recycler view with all of the chats between the current user and the
     * person they are currently chatting with
     */
    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                // Reads through the chats in the database
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    assert modelChat != null;
                    // Creates an adapter for each chat and adds it to the recycler view
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, image);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates a dialog asking a user to select an image from the camera or gallery
     */
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {

            // Checks for and requests permissions
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    /**
     * Handles the result of permission requests
     *
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storagePermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && storagePermissionGranted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean storagePermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storagePermissionGranted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Prepares to send an image with a message if the permissions have been granted
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                assert data != null;
                imageUri = data.getData();
                try {
                    sendImageMessage(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == IMAGE_CAMERA_REQUEST) {
                try {
                    sendImageMessage(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Formatting image messages and adding them to the chats database
     * @param imageURI the image
     * @throws IOException
     */
    private void sendImageMessage(Uri imageURI) throws IOException {
        notify = true;
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        final String timestamp = "" + System.currentTimeMillis();
        String path = "ChatImages/" + "post" + timestamp; // filename
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
        final byte[] data = arrayOutputStream.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
        ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            dialog.dismiss();
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()) {
                DatabaseReference re = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", fid);
                hashMap.put("receiver", uid);
                hashMap.put("message", downloadUri);
                hashMap.put("timestamp", timestamp);
                hashMap.put("type", "images");
                re.child("Chats").push().setValue(hashMap);
                final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(fid);
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ref1.child("id").setValue(fid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(fid).child(uid);
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ref2.child("id").setValue(uid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(e -> {

        });
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAMERA_REQUEST);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST);
    }

    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    /**
     * Adds the message to the data base so it loads on both the sender and receiver screens
     * @param message the message being sent
     */
    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", fid);
        hashMap.put("receiver", uid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");
        databaseReference.child("Chats").push().setValue(hashMap);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(fid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref1.child("id").setValue(fid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(fid).child(uid);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    ref2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            fid = user.getUid();
        }

    }
}