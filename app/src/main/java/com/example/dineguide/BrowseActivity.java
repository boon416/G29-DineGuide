package com.example.dineguide;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        recyclerView = findViewById(R.id.recyclerRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantList = new ArrayList<>();
        adapter = new RestaurantAdapter(restaurantList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadRestaurants();

        EditText editSearch = findViewById(R.id.editSearch);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 不用管
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不用管
            }
        });

    }

    private void loadRestaurants() {
        db.collection("restaurants")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Restaurant restaurant = doc.toObject(Restaurant.class);
                            if (restaurant != null) {
                                restaurantList.add(restaurant);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Mapping error for a restaurant.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (restaurantList.isEmpty()) {
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load restaurants.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void filter(String text) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().toLowerCase().contains(text.toLowerCase()) ||
                    restaurant.getCuisine().toString().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }

        adapter.updateList(filteredList);
    }




}
