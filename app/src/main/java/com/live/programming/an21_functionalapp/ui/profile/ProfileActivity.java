package com.live.programming.an21_functionalapp.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.live.programming.an21_functionalapp.AppConstants;
import com.live.programming.an21_functionalapp.LocalSession;
import com.live.programming.an21_functionalapp.MainDrawerActivity;
import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.databinding.ActivityProfileBinding;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding profileBinding;
    EditText editName, editEmail, editAbout, editContact, editAltEmail, editGender;
    ImageView imgView, btnCamera, btnGallery;
    FloatingActionButton fabEdit;
    LocalSession session;
    String userId, picUrl = "", downURL;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        imgView = profileBinding.profilePic;
        fabEdit = profileBinding.picEdit;
        editName = profileBinding.profileName;
        editEmail = profileBinding.profileEmail;
        editAbout = profileBinding.profileAbout;
        editContact = profileBinding.profileContact;
        editAltEmail = profileBinding.profileAltEmail;
        editGender = profileBinding.profileGender;

        session = new LocalSession(ProfileActivity.this);
        userId = session.readData(AppConstants.UID);

        fs = FirebaseFirestore.getInstance();

        getAllUserInfo();

        fabEdit.setOnClickListener(v -> {
            // from here we will open a bottom modal to show camera & gallery option
            BottomSheetDialog sheet = new BottomSheetDialog(ProfileActivity.this);
            // layout mentioned here which will be against the bottom sheet
            sheet.setContentView(getLayoutInflater().inflate(R.layout.profile_pic_options, null));
            btnCamera = sheet.findViewById(R.id.open_camera);
            btnGallery = sheet.findViewById(R.id.open_gallery);
            sheet.show();

            btnCamera.setOnClickListener(v1 -> {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLaunch.launch(camera);
            });

            btnGallery.setOnClickListener(v1 -> galleryLaunch.launch("image/*"));

        });

    }

    private void getAllUserInfo() {
        // for fetching data from fire store
        fs.collection("UserDetails")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        DocumentSnapshot ds = task.getResult();
                        editName.setText(ds.getString(AppConstants.UNAME));
                        editGender.setText(ds.getString(AppConstants.U_GENDER));
                        editEmail.setText(ds.getString(AppConstants.U_EMAIL));
                        editContact.setText(ds.getString(AppConstants.U_PHONE));
                    } else
                        showNotifier("Try after sometime, Server is busy");
                })
                .addOnFailureListener(e -> showNotifier(e.getMessage()));
    }

    private void showNotifier(String msg) {
        Snackbar.make(ProfileActivity.this, profileBinding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    // through this overridden method we can apply menu in the activity / screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_change, menu); // configuring menu file
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change_info) {
            String name = editName.getText().toString();
            String phone = editContact.getText().toString();
            String altEmail = editAltEmail.getText().toString();
            String about = editAbout.getText().toString();

            updateData(name, phone, altEmail, about);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData(String name, String phone, String altEmail, String about) {
        Map<String, Object> updateUser = new HashMap<>();
        uploadToStorage();
        new Handler().postDelayed(() -> {
            if (!picUrl.isEmpty()) {
                Log.i("TAG", "update: "+downURL);
                if (downURL == null)
                    showNotifier("Press again the Save button");
                else
                    updateUser.put(AppConstants.U_PIC, downURL);
            }
            updateUser.put(AppConstants.UNAME, name);
            updateUser.put(AppConstants.U_ALT_EMAIL, altEmail);
            updateUser.put(AppConstants.U_PHONE, phone);
            updateUser.put(AppConstants.U_ABOUT, about);

            fs.collection("UserDetails")
                    .document(userId)
                    .update(updateUser)
                    .addOnCompleteListener(task -> {
                        if (task.isComplete()) {
                            showNotifier("Updated");
                            // navigate to drawer
                            startActivity(new Intent(ProfileActivity.this, MainDrawerActivity.class));
                        } else
                            showNotifier("Try after sometime, Server is busy");

                    })
                    .addOnFailureListener(e -> showNotifier(e.getMessage()));
        }, 4000);


    }

    private void uploadToStorage() {
        File file = new File(picUrl);
        showNotifier(file.toString());
        FirebaseStorage fst = FirebaseStorage.getInstance(); // instance create
        StorageReference ref = fst.getReference().child(getString(R.string.app_name)+"/" + file.getName()); // folder ref access

        ref.putFile(Uri.fromFile(file))    // actual file upload
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        // receiving the uploaded image path
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downURL = uri.getPath();
                                Log.i("TAG", "onSuccess: "+downURL);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    //  getDownload(ref, ref.putFile(Uri.fromFile(file)));
                    }
                })
                .addOnFailureListener(e -> showNotifier(e.getMessage()));
    }

   /* private void getDownload(StorageReference ref, UploadTask uploadTask) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException(); // display toast here
            }

            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                downURL = task.getResult().getPath();
        });
    }*/

    // 2 parameters - 1. content, 2. result uri
    ActivityResultLauncher<String> galleryLaunch = registerForActivityResult(new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imgView.setImageURI(result);
                    picUrl = Uri.parse(result.getPath()).getPath(); // image path access and saving in variable
                }
            });

    ActivityResultLauncher<Intent> cameraLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    imgView.setImageBitmap(bitmap);
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                            getContentResolver(), bitmap, "xxxx", null));
                    picUrl = getRealPathFromURI(uri);
                }
            });

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}