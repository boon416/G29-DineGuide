package com.example.dineguide;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private Button btnClearHistory;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        db              = FirebaseFirestore.getInstance();
        user            = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please login to view history", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHistory();

        btnClearHistory.setOnClickListener(v -> clearUserHistory());
    }

    private void loadHistory() {
        db.collection("users")
                .document(user.getUid())
                .collection("spin_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener((QuerySnapshot query) -> {
                    List<String> entries = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Map<String, Object> map = doc.getData();
                        String food = (String) map.get("result");
                        Timestamp ts = doc.getTimestamp("timestamp");
                        String when = ts != null
                                ? DateFormat.getDateTimeInstance().format(ts.toDate())
                                : "";
                        entries.add(when + " → " + food);
                    }
                    historyListView.setAdapter(
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_list_item_1,
                                    entries
                            )
                    );
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load history",
                        Toast.LENGTH_SHORT
                ).show());
    }

    private void clearUserHistory() {
        CollectionReference histRef = db
                .collection("users")
                .document(user.getUid())
                .collection("spin_history");

        histRef.get()
                .addOnSuccessListener((QuerySnapshot snaps) -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : snaps.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(__ -> {
                                Toast.makeText(
                                        this,
                                        "History cleared",
                                        Toast.LENGTH_SHORT
                                ).show();
                                loadHistory();
                            })
                            .addOnFailureListener(e -> Toast.makeText(
                                    this,
                                    "Failed to clear history",
                                    Toast.LENGTH_SHORT
                            ).show());
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Error clearing history",
                        Toast.LENGTH_SHORT
                ).show());
    }
}


