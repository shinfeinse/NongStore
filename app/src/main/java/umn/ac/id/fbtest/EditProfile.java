package umn.ac.id.fbtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import umn.ac.id.fbtest.Model.User;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText InputUserName, InputUserIdentification, InputUserCompany;
    private ImageView InputUserImage;
    private Button saveBtn;
    private FirebaseAuth fAuth;
    private String Uid, Name, Identification, Company, downloadImageUrl;
    private StorageReference UserImagesRef;
    private DatabaseReference UserRef, CompanyRef;
    private ProgressDialog loadingBar;
    private Uri ImageUri;
    private static final int GalleryPick = 1;
    private static final int CameraPick = 1;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        fAuth = FirebaseAuth.getInstance();
        Uid = fAuth.getCurrentUser().getUid();

        UserImagesRef = FirebaseStorage.getInstance().getReference().child("User Images");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        CompanyRef = FirebaseDatabase.getInstance().getReference().child("Company");

        saveBtn = (Button) findViewById(R.id.saveProfileInfo);
        InputUserImage = (ImageView) findViewById(R.id.edit_profile_image);
        InputUserName = (EditText) findViewById(R.id.etEditNama);
        InputUserIdentification = (EditText) findViewById(R.id.etEditIdentification);
        InputUserCompany = (EditText) findViewById(R.id.etEditCompany);
        loadingBar = new ProgressDialog(this);

        UserRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    InputUserName.setText(user.getNama());
                    Picasso.get().load(user.getImage()).into(InputUserImage);
                    InputUserIdentification.setText(user.getIdentification());
                    InputUserCompany.setText(user.getCompany());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
            }
        });


        InputUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenDialog();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateUserData();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void OpenDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_camera)
                .setTitle("Choose Source")
                .setMessage("Where do you want to get your image ?")
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OpenGallery();
                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OpenCamera();
                    }
                })
                .show();
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    private void OpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CameraPick);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputUserImage.setImageURI(ImageUri);
        } else if(requestCode==CameraPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            File f = new File(currentPhotoPath);
            ImageUri = Uri.fromFile(f);
            InputUserImage.setImageURI(ImageUri);
        }
    }


    private void ValidateUserData()
    {
        Name = InputUserName.getText().toString();
        Identification = InputUserIdentification.getText().toString();
        Company = InputUserCompany.getText().toString();

        if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this, "Please write user display name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Identification))
        {
            Toast.makeText(this, "Please write user company identification...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Company))
        {
            Toast.makeText(this, "Please write which company u're working with...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (ImageUri == null)
            {
                StoreUserInformationNoImage();
            } else {
                StoreUserInformation();
            }

        }
    }



    private void StoreUserInformation()
    {
        loadingBar.setTitle("Saving your profile");
        loadingBar.setMessage("Please wait while we're saving your profile");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final StorageReference filePath = UserImagesRef.child(ImageUri.getLastPathSegment() + Uid + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(EditProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(EditProfile.this, "User Images uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(EditProfile.this, "got the User images Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveUserInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void StoreUserInformationNoImage()
    {
        loadingBar.setTitle("Saving your profile");
        loadingBar.setMessage("Please wait while we're saving your profile");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        SaveUserInfoToDatabaseNoImage();
    }

    private void SaveUserInfoToDatabaseNoImage()
    {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("nama", Name);
        userMap.put("identification", Identification);
        userMap.put("company", Company);

        HashMap<String, Object> companyMap = new HashMap<>();
        companyMap.put("nama", Name);
        companyMap.put("identification", Identification);

        UserRef.child(Uid).updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            CompanyRef.child(Company).child(Uid).updateChildren(companyMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(EditProfile.this, "Data added to company successfully..", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                                Intent intent = new Intent(EditProfile.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                loadingBar.dismiss();
                                                String message = task.getException().toString();
                                            }
                                        }
                                    });

                            loadingBar.dismiss();
                            Toast.makeText(EditProfile.this, "Profile is updated successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(EditProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SaveUserInfoToDatabase()
    {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("image", downloadImageUrl);
        userMap.put("nama", Name);
        userMap.put("identification", Identification);
        userMap.put("company", Company);

        HashMap<String, Object> companyMap = new HashMap<>();
        companyMap.put("nama", Name);
        companyMap.put("identification", Identification);
        companyMap.put("image", downloadImageUrl);

        UserRef.child(Uid).updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            CompanyRef.child(Company).child(Uid).updateChildren(companyMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(EditProfile.this, "Data added to company successfully..", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                                Intent intent = new Intent(EditProfile.this, HomeActivity.class);
                                                startActivity(intent);

                                            }
                                            else
                                            {
                                                loadingBar.dismiss();
                                                String message = task.getException().toString();
                                            }
                                        }
                                    });

                            loadingBar.dismiss();
                            Toast.makeText(EditProfile.this, "Profile is updated successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(EditProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

