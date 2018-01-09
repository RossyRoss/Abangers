package com.example.pc_user.abang.registration;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc_user.abang.R;
import com.example.pc_user.abang.utils.Utility;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnNext;
    private ImageView regImage;
    private ImageButton regBtnImage;
    private String userChoosenTask;
    private EditText etusername, etuserpass,etuseremail, etuserfullname, etuseraddr, etusercontact;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etusername = (EditText) findViewById(R.id.etusername);
        etuserpass = (EditText) findViewById(R.id.etpassword);
        etuseremail = (EditText) findViewById(R.id.etemail);
        etuserfullname = (EditText) findViewById(R.id.etfullname);
        etuseraddr = (EditText) findViewById(R.id.etaddress);
        etusercontact = (EditText) findViewById(R.id.etcontactnumber);
        btnNext = (Button) findViewById(R.id.btnnext);
        regImage = (ImageView) findViewById(R.id.imgview);
        regBtnImage = (ImageButton) findViewById(R.id.btncamera);
        regBtnImage.setOnClickListener(this);
        try {
            refIds();

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = etusername.getText().toString().trim();
                    String password = etuserpass.getText().toString().trim();
                    String email = etuseremail.getText().toString().trim();
                    String name = etuserfullname.getText().toString().trim();
                    String addr = etuseraddr.getText().toString().trim();
                    String contact = etusercontact.getText().toString().trim();
                    if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(name) ||
                            !TextUtils.isEmpty(addr) || !TextUtils.isEmpty(contact)) {
                        if(hasImage(regImage)) {
                            moveToContinuation();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please pick a profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Input all fields!", Toast.LENGTH_SHORT).show();
                    }

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable!=null);

        if(hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    private void moveToContinuation() {
        String username = etusername.getText().toString().trim();
        String password = etuserpass.getText().toString().trim();
        String email = etuseremail.getText().toString().trim();
        String name = etuserfullname.getText().toString().trim();
        String addr = etuseraddr.getText().toString().trim();
        String contact = etusercontact.getText().toString().trim();

        Intent moveToNext = new Intent(getApplicationContext(), RegisterContinuationActivity.class);
        Drawable drawable = regImage.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        moveToNext.putExtra("picture", b);
        moveToNext.putExtra("username", username);
        moveToNext.putExtra("password", password);
        moveToNext.putExtra("email", email);
        moveToNext.putExtra("name", name);
        moveToNext.putExtra("addr", addr);
        moveToNext.putExtra("contact", contact);
        startActivity(moveToNext);
    }

    private void refIds() {
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
                selectImage();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RegisterActivity.this);

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
        regImage.setImageURI(uri);
        regImage.setBackgroundResource(0);

    }

    private void onSelectFromGalleryResult(Intent data) {
       Uri uri = data.getData();
        regImage.setImageURI(uri);
        regImage.setBackgroundResource(0);
    }
}
