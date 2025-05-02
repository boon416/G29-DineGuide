package com.example.dineguide;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpinActivity extends AppCompatActivity {

    private ImageView spinWheelImage;
    private Button spinButton;
    private Random random = new Random();
    private float lastAngle = 0f;
    private boolean spinning = false;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private final List<String> restaurantOptions = Arrays.asList(
            "Restoran Karim", "Jayne Kitchen Golden Fried Chicken Nasi Lemak", "Restoran Maha Maju", "Top Spice Mala Hotpot Restaurant",
            "Ah Boy Diner Kampar", "Sushi Mentai", "McDonald's Kampar DT"
    );

    private final int NUM_SEGMENTS = restaurantOptions.size();
    private final float ANGLE_PER_SEGMENT = 360f / NUM_SEGMENTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spin);

        spinWheelImage = findViewById(R.id.spinWheelImage);
        spinButton = findViewById(R.id.spinButton);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        user = FirebaseAuth.getInstance().getCurrentUser(); // Initialize Firebase User

        if (spinWheelImage == null || spinButton == null) {
            Toast.makeText(this, "Error: Layout views not found!", Toast.LENGTH_LONG).show();
            if (spinButton != null) spinButton.setEnabled(false);
            return;
        }

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinning) {
                    spinTheWheel();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void spinTheWheel() {
        if (restaurantOptions.isEmpty()) {
            Toast.makeText(this, "No food options available.", Toast.LENGTH_SHORT).show();
            return;
        }

        spinning = true;
        spinButton.setEnabled(false);

        float randomAngleOffset = random.nextFloat() * ANGLE_PER_SEGMENT - (ANGLE_PER_SEGMENT / 2);
        float targetAngle = (360f * 5) + (random.nextInt(NUM_SEGMENTS) * ANGLE_PER_SEGMENT) + randomAngleOffset;

        float finalAngle = targetAngle;

        RotateAnimation rotateAnimation = new RotateAnimation(
                lastAngle,
                lastAngle + finalAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                float finalRestingAngle = (lastAngle + finalAngle) % 360;
                String selectedFood = getResult(finalRestingAngle);
                // Save the spin result to Firestore
                saveSpinResult(selectedFood);

                Intent intent = new Intent(SpinActivity.this, ResultActivity.class);
                intent.putExtra("SELECTED_FOOD", selectedFood);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_up, R.anim.stay);

                lastAngle = finalRestingAngle;
                spinning = false;
                spinButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        spinWheelImage.startAnimation(rotateAnimation);
    }

    private String getResult(float finalRestingAngle) {
        float pointerAngle = 270;
        float normalizedAngle = (finalRestingAngle - pointerAngle + 360) % 360;
        int segmentIndex = (int) (normalizedAngle / ANGLE_PER_SEGMENT);
        segmentIndex = Math.max(0, Math.min(segmentIndex, NUM_SEGMENTS - 1));
        return restaurantOptions.get(segmentIndex);
    }

    private void saveSpinResult(String result) {
        if (user == null) {
            // User is not logged in. You could show a message or skip saving.
            return;
        }

        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("result", result);
        historyEntry.put("timestamp", new Timestamp(new Date()));

        db.collection("users")
                .document(user.getUid())
                .collection("spin_history")
                .add(historyEntry)
                .addOnSuccessListener(docRef -> {
                    // Optionally, notify the user that the result was saved
                    //Toast.makeText(this, "Result saved to history.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save result to history.", Toast.LENGTH_SHORT).show();
                });
    }
}