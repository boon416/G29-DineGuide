package com.example.dineguide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private Context context;

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.name.setText(restaurant.getName() != null ? restaurant.getName() : "Unnamed Restaurant");
        holder.location.setText(restaurant.getLocation() != null ? restaurant.getLocation() : "Unknown Location");

        // ✨ 根据 pinned 状态改变按钮文字
        holder.btnPin.setText(restaurant.isPinned() ? "❤️ Unpin" : "🤍 Pin");

        holder.btnPin.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // 还没登录，跳去Login
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            } else {
                String userId = user.getUid();
                String restaurantId = restaurant.getName(); // 暂时用name作为id，如果有真正id更好
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (!restaurant.isPinned()) {
                    // 收藏 (Pin)
                    db.collection("users").document(userId)
                            .collection("favorites")
                            .document(restaurantId)
                            .set(restaurant)
                            .addOnSuccessListener(aVoid -> {
                                restaurant.setPinned(true);
                                notifyItemChanged(holder.getAdapterPosition());
                                Toast.makeText(context, "Added to Favorites!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // 取消收藏 (Unpin)
                    db.collection("users").document(userId)
                            .collection("favorites")
                            .document(restaurantId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                restaurant.setPinned(false);
                                notifyItemChanged(holder.getAdapterPosition());
                                Toast.makeText(context, "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        // 正确处理 rating
        String rating = restaurant.getRating();
        holder.rating.setText("⭐ " + (rating != null ? rating : "0.0"));

        holder.price.setText(restaurant.getPriceRange() != null ? restaurant.getPriceRange() : "--");
        holder.description.setText(restaurant.getDescription() != null ? restaurant.getDescription() : "No description available.");

        // ✨ Cuisine 处理
        if (restaurant.getCuisine() != null && !restaurant.getCuisine().isEmpty()) {
            String cuisineText = String.join(", ", restaurant.getCuisine());
            holder.cuisine.setText(cuisineText);
        } else {
            holder.cuisine.setText("No Cuisine Info");
        }

        // 加载图片
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(restaurant.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }

        // 地图按钮
        holder.btnMap.setOnClickListener(v -> {
            String address = restaurant.getLocation();
            if (address != null && !address.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("restaurant", restaurant);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, rating, price, description, cuisine;
        ImageView image;
        Button btnMap, btnPin, btnDetails;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            location = itemView.findViewById(R.id.textLocation);
            rating = itemView.findViewById(R.id.textRating);
            price = itemView.findViewById(R.id.textPriceRange);
            description = itemView.findViewById(R.id.textDescription);
            cuisine = itemView.findViewById(R.id.textCuisine);
            image = itemView.findViewById(R.id.imageRestaurant);
            btnMap = itemView.findViewById(R.id.btnMap);
            btnPin = itemView.findViewById(R.id.btnPin);
            btnDetails = itemView.findViewById(R.id.btnDetails); // ✨ 预留 See Details按钮
        }
    }

    public void updateList(List<Restaurant> newList) {
        restaurantList = newList;
        notifyDataSetChanged();
    }

}
