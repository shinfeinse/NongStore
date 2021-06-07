package umn.ac.id.fbtest.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import umn.ac.id.fbtest.AddProductActivity;
import umn.ac.id.fbtest.Model.User;
import umn.ac.id.fbtest.R;
import umn.ac.id.fbtest.ViewHolder.MemberViewHolder;

public class MemberFragment extends Fragment {

    private DatabaseReference MemberRef;
    private FirebaseAuth fAuth;
    private RecyclerView recyclerView;
    private String Uid, Company;
    RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member,
                container, false);
        fAuth = FirebaseAuth.getInstance();
        Uid = fAuth.getCurrentUser().getUid();
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Company = user.getCompany();
                    MemberRef = FirebaseDatabase.getInstance().getReference().child("Company").child(Company);

                    Paper.init(getContext());

                    recyclerView = view.findViewById(R.id.recycler_menu);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FirebaseRecyclerOptions<User> options =
                            new FirebaseRecyclerOptions.Builder<User>()
                                    .setQuery(MemberRef, User.class)
                                    .build();


                    FirebaseRecyclerAdapter<User, MemberViewHolder> adapter =
                            new FirebaseRecyclerAdapter<User, MemberViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull User model)
                                {
                                    holder.txtName.setText(model.getNama());
                                    holder.txtIdentification.setText(model.getIdentification());
                                    Picasso.get().load(model.getImage()).into(holder.MemberView);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                                {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_item_layout, parent, false);
                                    MemberViewHolder holder = new MemberViewHolder(view);
                                    return holder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}