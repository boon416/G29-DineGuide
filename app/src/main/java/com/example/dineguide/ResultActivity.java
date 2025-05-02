package com.example.dineguide;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {

    private TextView resultTextView;
    private Button closeButton;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;
    private String selectedRestaurantName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultTextView = findViewById(R.id.resultTextView);
        closeButton = findViewById(R.id.closeButton);
        recyclerView = findViewById(R.id.resultRecyclerView);

        // RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantList = new ArrayList<>();
        adapter = new RestaurantAdapter(restaurantList,  this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Get the selected restaurant name from the intent
        selectedRestaurantName = getIntent().getStringExtra("SELECTED_FOOD");

        if (selectedRestaurantName != null) {
            displayResultText(selectedRestaurantName);
            loadRestaurants(selectedRestaurantName);
        } else {
            loadRestaurants(null); // Load all restaurants if no name is passed
        }

        closeButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
        });

    }
    private void displayResultText(String restaurantName){
        resultTextView.setText("How about \n" + restaurantName + "?");
    }

    private void loadRestaurants(String filterName) {
        Query query = db.collection("restaurants");

        if (filterName != null && !filterName.isEmpty()) {
            // Filter by the restaurant name
            query = query.whereEqualTo("name", filterName);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    restaurantList.clear();
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

    @Override
    public void finish() {
        super.finish();
    }
}
