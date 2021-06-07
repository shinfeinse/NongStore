package umn.ac.id.fbtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import umn.ac.id.fbtest.Model.Products;
import umn.ac.id.fbtest.Model.User;

public class DetailProductActivity extends AppCompatActivity {
    private Button UpdateItem, DeleteItem;
    private ImageView productImage;
    private TextView productPrice, productName, productDescription;
    private String productId, Uid, Company;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailproduk);

        productId = getIntent().getStringExtra("pid");

        productImage = (ImageView) findViewById(R.id.imvDetail);
        productPrice = (TextView) findViewById(R.id.tvDetailHarga);
        productName = (TextView) findViewById(R.id.tvDetailNama);
        productDescription = (TextView) findViewById(R.id.tvDetailDesc);
        DeleteItem = (Button) findViewById(R.id.btnDetailDelete);

        DeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Company);
                productsRef.child(productId).removeValue();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
                    Company = user.getCompany();
                    getProductDetails(productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProductDetails(String productId){
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Company);
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText("Rp. " + products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
