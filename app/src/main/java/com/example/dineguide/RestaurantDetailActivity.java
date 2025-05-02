package com.example.dineguide;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ImageView imageRestaurant;
    private TextView textName, textLocation, textCuisine, textRating, textPriceRange, textDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // 找layout里的View
        imageRestaurant = findViewById(R.id.detailImageRestaurant);
        textName = findViewById(R.id.detailTextName);
        textLocation = findViewById(R.id.detailTextLocation);
        textCuisine = findViewById(R.id.detailTextCuisine);
        textRating = findViewById(R.id.detailTextRating);
        textPriceRange = findViewById(R.id.detailTextPriceRange);
        textDescription = findViewById(R.id.detailTextDescription);

        // 拿到传过来的 Restaurant
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        if (restaurant != null) {
            textName.setText(restaurant.getName());
            textLocation.setText(restaurant.getLocation());
            textRating.setText("⭐ " + restaurant.getRating());
            textPriceRange.setText(restaurant.getPriceRange());
            textDescription.setText(restaurant.getDescription());

            if (restaurant.getCuisine() != null && !restaurant.getCuisine().isEmpty()) {
                textCuisine.setText(String.join(", ", restaurant.getCuisine()));
            } else {
                textCuisine.setText("No Cuisine Info");
            }

            if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(restaurant.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageRestaurant);
            } else {
                imageRestaurant.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
