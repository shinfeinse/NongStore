package umn.ac.id.fbtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import umn.ac.id.fbtest.Model.User;

public class ViewProfile extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private String Uid;
    private TextView DisplayName, Identification, Company;
    private CircleImageView imvViewProfile;
    private Button Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        imvViewProfile = (CircleImageView) findViewById(R.id.view_profile_image);
        DisplayName = (TextView) findViewById(R.id.tvNamaProfile);
        Identification = (TextView) findViewById(R.id.tvIdentificationProfile);
        Company = (TextView) findViewById(R.id.tvCompanyProfile);
        Edit = (Button) findViewById(R.id.editProfileInfo);
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
            }
        });
        fAuth = FirebaseAuth.getInstance();
        Uid = fAuth.getCurrentUser().getUid();
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    DisplayName.setText(user.getNama());
                    Identification.setText(user.getIdentification());
                    Company.setText(user.getCompany());
                    Picasso.get().load(user.getImage()).into(imvViewProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}