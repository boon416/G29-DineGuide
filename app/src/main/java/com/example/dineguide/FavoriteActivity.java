package com.example.dineguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> favoriteList;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteList = new ArrayList<>();
        adapter = new RestaurantAdapter(favoriteList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadFavorites();
        } else {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadFavorites() {
        db.collection("users").document(user.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Restaurant restaurant = doc.toObject(Restaurant.class);
                            if (restaurant != null) {
                                restaurant.setPinned(true); // 记得标成pinned状态
                                favoriteList.add(restaurant);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load favorites.", Toast.LENGTH_SHORT).show();
                });
    }
}
