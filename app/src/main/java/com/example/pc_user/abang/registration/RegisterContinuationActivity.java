package com.example.pc_user.abang.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.pc_user.abang.R;
import com.example.pc_user.abang.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterContinuationActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageButton camera1;
    private ImageButton camera2;
    private Button registerBtn;
    private String userChoosenTask;
    private RadioGroup radioGroup;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int holder = 0;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase, mDatabaseDetail;
    private StorageReference mStorage;
    private Uri holder1 = null;
    private Uri holder2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_continuation);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("UHFile");
        mDatabaseDetail = FirebaseDatabase.getInstance().getReference("UDFile");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        imageView1 = (ImageView) findViewById(R.id.imgview);
        imageView2 = (ImageView) findViewById(R.id.imgview2);
        registerBtn = (Button) findViewById(R.id.btnreg);
        radioGroup = (RadioGroup) findViewById(R.id.userType);
        camera1 = (ImageButton) findViewById(R.id.btncamera);
        camera2 = (ImageButton) findViewById(R.id.btncamera2);
        camera1.setOnClickListener(this);
        camera2.setOnClickListener(this);
        refIds();
        //Button ni
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasImage(imageView1) || !hasImage(imageView2) || radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Fill necessary info", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerAccount();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void registerAccount() {
        String name = null;
        String pass = null;
        String email = null;
        String username = null;
        String addr = null;
        String contact = null;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            name = bundle.getString("name");
            pass = bundle.getString("password");
            email = bundle.getString("email");
            username = bundle.getString("username");
            addr = bundle.getString("addr");
            contact = bundle.getString("contact");
            byte[] b = bundle.getByteArray("picture");
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), pass, Toast.LENGTH_SHORT).show();
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        final String finalUsername = username;
        final String finalPass = pass;
        final String finalName = name;
        final String finalAddr = addr;
        final String finalEmail = email;
        assert email != null;
        assert pass != null;
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //add data to UHFile
                            String id = user.getUid();
                            UHFile newUser = new UHFile(id, finalUsername, finalPass,"AC");
                            mDatabase.child(id).setValue(newUser);
                            UDFile newUserDetail = new UDFile(id, finalName, finalAddr, finalEmail, "AC");
                            mDatabaseDetail.child(id).setValue(newUserDetail);
                            StorageReference filepath1 = mStorage.child("Photos").child(holder1.getLastPathSegment());
                            filepath1.putFile(holder1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Successfully Requested!", Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Technical difficulties!", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable!=null);

        if(hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public void refIds() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Account");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btncamera:
                holder =1;
                selectImage();
                break;
            case R.id.btncamera2:
                selectImage();
                holder = 2;
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterContinuationActivity.this);
        builder.setTitle("Choose picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RegisterContinuationActivity.this);

                if(items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if(result) {
                        cameraIntent();
                    }
                }
                else if(items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if(result) {
                        galleryIntent();
                    }
                } else if(items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo")) {
                        cameraIntent();
                    } else if(userChoosenTask.equals("Choose from Gallery")) {
                        galleryIntent();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if(requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            imageView1.setImageURI(uri);
            holder1 = uri;
            imageView1.setBackgroundResource(0);
        }
        else {
            imageView2.setImageURI(uri);
            holder2 = uri;
            imageView2.setBackgroundResource(0);
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            imageView1.setImageURI(uri);
            holder1 = uri;
            imageView1.setBackgroundResource(0);
        }
        else {
            imageView2.setImageURI(uri);
            holder2 = uri;
            imageView2.setBackgroundResource(0);
        }
    }
}
