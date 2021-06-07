package umn.ac.id.fbtest.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import umn.ac.id.fbtest.AddProductActivity;
import umn.ac.id.fbtest.DetailProductActivity;
import umn.ac.id.fbtest.Model.Products;
import umn.ac.id.fbtest.Model.User;
import umn.ac.id.fbtest.R;
import umn.ac.id.fbtest.ViewHolder.ProductViewHolder;

public class ProductFragment extends Fragment {

    private DatabaseReference ProductsRef;
    private FirebaseAuth fAuth;
    private RecyclerView recyclerView;
    private String Uid, Company;
    RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product,
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
                    ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Company);
                    FloatingActionButton Add = (FloatingActionButton) view.findViewById(R.id.fab);
                    Add.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            startActivity(new Intent(getContext(), AddProductActivity.class));
                        }
                    });
                    Paper.init(getContext());

                    recyclerView = view.findViewById(R.id.recycler_menu);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef, Products.class)
                                    .build();


                    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                                {
                                    holder.txtProductName.setText(model.getPname());
                                    holder.txtProductDescription.setText(model.getDescription());
                                    holder.txtProductPrice.setText("Rp. " + model.getPrice());
                                    Picasso.get().load(model.getImage()).into(holder.imageView);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), DetailProductActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                                {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                                    ProductViewHolder holder = new ProductViewHolder(view);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}