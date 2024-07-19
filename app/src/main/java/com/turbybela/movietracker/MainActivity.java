package com.turbybela.movietracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button addButton;
    ListView listView;
    ArrayList<MovieEntry> items;
    EntryCollection adapter;
    SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreference = getSharedPreferences("movies", MODE_PRIVATE);

        Initialize_MovieTracker();
    }

    private void Initialize_MovieTracker() {
        addButton = findViewById(R.id.AddButton);
        listView = findViewById(R.id.entry_list);

        // Create a list of items
        items = new ArrayList<>();
        Map<String, ?> all = sharedPreference.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith("__")) continue;

            items.add(MovieEntry.EntryFromResource(key, (String)value));
        }

        if (sharedPreference.getString("__latest", "").isBlank()){
            SharedPreferences.Editor e = sharedPreference.edit();
            e.putString("__latest", "");
            for (MovieEntry item : items){
                if (item.GetVisibility()){
                    e.putString("__latest", item.getName());
                    break;
                }
            }
            e.apply();
        }

        // Create and set the adapter
        adapter = new EntryCollection(this, items, sharedPreference);
        listView.setAdapter(adapter);
        //NotificationActionReceiver.SetAdapter(adapter);

        addButton.setOnClickListener(v -> showAddDialog());
    }


    private void showAddDialog(){
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.add_dialog, null);

        // Find views in the dialog layout
        EditText editText = dialogView.findViewById(R.id.editText);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Enter Text")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle OK button click
                    String inputText = editText.getText().toString();

                    boolean name_good = true;
                    if(inputText.isBlank()){
                        Toast.makeText(this, "Enter entry name!", Toast.LENGTH_LONG).show();
                        name_good = false;
                    } else if (inputText.startsWith("__")) {
                        Toast.makeText(this, "Remove '__' prefix from entry name!", Toast.LENGTH_LONG).show();
                        name_good = false;
                    } else {
                        for (MovieEntry mv : items){
                            if (mv.getName().equals(inputText)){
                                Toast.makeText(this, "Entry with same name already exists!", Toast.LENGTH_LONG).show();
                                name_good = false;
                                break;
                            }
                        }
                    }

                    if (name_good){
                        items.add(new MovieEntry(inputText, 1, 1, true));
                        adapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putString(inputText, "1,1,1");
                        editor.apply();

                        //NotificationHelper.GetInstance().UpdateNotification();
                    }


                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle Cancel button click
                    dialog.cancel();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to stop the foreground service
    private void stopForegroundService() {
        Intent serviceIntent = new Intent(this, NotificationHelper.class);
        stopService(serviceIntent);
    }

}
