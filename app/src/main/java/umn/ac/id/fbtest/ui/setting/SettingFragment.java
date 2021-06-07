package umn.ac.id.fbtest.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import umn.ac.id.fbtest.AboutActivity;
import umn.ac.id.fbtest.EditProfile;
import umn.ac.id.fbtest.Model.User;
import umn.ac.id.fbtest.R;
import umn.ac.id.fbtest.ViewProfile;

public class SettingFragment extends Fragment {

    private FirebaseAuth fAuth;
    private String Uid;

    private SettingViewModel mViewModel;
    SharedPreferences sharedPreferences = null;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,
                container, false);
        Button About = (Button) view.findViewById(R.id.btnAbout);
        Button Profile = (Button) view.findViewById(R.id.btnSetProfile);
        About.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });
        Profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fAuth = FirebaseAuth.getInstance();
                Uid = fAuth.getCurrentUser().getUid();
                DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                UserRef.child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            startActivity(new Intent(getContext(), ViewProfile.class));
                        } else {
                            startActivity(new Intent(getContext(), EditProfile.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        Switch switches = (Switch) view.findViewById(R.id.mode);
        sharedPreferences = this.getActivity().getSharedPreferences("night", 0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);
        if(booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switches.setChecked(true);
        }

        switches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switches.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", true);
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switches.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", false);
                    editor.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        // TODO: Use the ViewModel
    }

}