package com.turbybela.movietracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EntryCollection extends BaseAdapter {
    private final Context context;
    private final List<MovieEntry> items;
    private final SharedPreferences sp;
    private SharedPreferencesHelper sharedPreferencesHelper;

    public EntryCollection(Context context, List<MovieEntry> items, SharedPreferences sp) {
        this.context = context;
        this.items = items;
        this.sp = sp;

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);

        // Register a listener to detect any preference change
        SharedPreferencesHelper.OnSharedPreferenceChangeListener listener =
                (sharedPreferences, key) -> {
                    // Handle preference change here
                    notifyDataSetChanged();
                };
        sharedPreferencesHelper.addOnSharedPreferenceChangeListener(listener);

        // Optional: Register listener for all existing preferences
        sharedPreferencesHelper.registerAllPreferencesListener();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_entry, parent, false);
        }

        TextView EntryName = convertView.findViewById(R.id.entryName);
        TextView SeasonField = convertView.findViewById(R.id.SeasonCount);
        TextView EpisodeField = convertView.findViewById(R.id.EpisodeCount);

        //Button button = convertView.findViewById(R.id.button);
        Button SInc = convertView.findViewById(R.id.SeasonIncrement);
        Button SDec = convertView.findViewById(R.id.SeasonDecrement);

        Button EInc = convertView.findViewById(R.id.EpisodeIncrement);
        Button EDec = convertView.findViewById(R.id.EpisodeDecrement);

        ImageButton Visibility = convertView.findViewById(R.id.Visibility);
        ImageButton RemoveEntry = convertView.findViewById(R.id.DeleteEntry);

        MovieEntry item = items.get(position);

        EntryName.setText(item.getName());
        SeasonField.setText(String.format("S%d", item.GetSeason()));
        EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
        Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);

        EntryName.setOnClickListener(v -> {
            if (!item.GetVisibility())
                Toast.makeText(context, "Hidden entry cannot be shown in notification!", Toast.LENGTH_LONG).show();
            UpdateStorage(item); //  This will set it as latest if visible
        });

        SInc.setOnClickListener(v -> {
            Log.println(Log.INFO, "bwoa", "IncrementSeason");
            item.IncrementSeason();
            SeasonField.setText(String.format("S%d", item.GetSeason()));
            EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
            Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);
            UpdateStorage(item);
        });
        SDec.setOnClickListener(v -> {
            Log.println(Log.INFO, "bwoa", "DecrementSeason");
            item.DecrementSeason();
            SeasonField.setText(String.format("S%d", item.GetSeason()));
            EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
            Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);
            UpdateStorage(item);
        });
        EInc.setOnClickListener(v -> {
            Log.println(Log.INFO, "bwoa", "IncrementEpisode");
            item.IncrementEpisode();
            SeasonField.setText(String.format("S%d", item.GetSeason()));
            EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
            Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);
            UpdateStorage(item);
        });
        EDec.setOnClickListener(v -> {
            Log.println(Log.INFO, "bwoa", "DecrementSeason");
            item.DecrementEpisode();
            SeasonField.setText(String.format("S%d", item.GetSeason()));
            EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
            Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);
            UpdateStorage(item);
        });
        Visibility.setOnClickListener(v -> {
            Log.println(Log.INFO, "bwoa", "Toggled Visibility");
            item.ToggleVisibility();
            SeasonField.setText(String.format("S%d", item.GetSeason()));
            EpisodeField.setText(String.format("Ep%d", item.GetEpisode()));
            Visibility.setImageResource(item.GetVisibility() ? R.drawable.eye_open : R.drawable.eye_slash);
            UpdateStorage(item);
        });
        RemoveEntry.setOnClickListener(v -> {
            showDelDialog(item.getName(), position);
            if (sp.getString("__latest", "").equals(item.getName())){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("__latest", "");
                editor.apply();
            }

            //NotificationHelper.getInstance().ShowNotification();
        });
        return convertView;
    }

    private void UpdateStorage(MovieEntry item){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(item.getName(), item.DataOut());
        if (item.GetVisibility())
            editor.putString("__latest", item.getName());
        else{
            if (sp.getString("__latest", "").equals(item.getName()))
                editor.putString("__latest", "");
        }
        editor.apply();

        //NotificationHelper.getInstance().ShowNotification();
    }

    @Override
    public void notifyDataSetChanged() {
        Map<String, ?> sp_data = sp.getAll();
        // Update list from the map
        for (Map.Entry<String, ?> entry : sp_data.entrySet()) {
            String name = entry.getKey();
            if(name.startsWith("__")) continue;
            String mapData = (String)entry.getValue();

            // Find the corresponding item in the list, if any
            MovieEntry matchingItem = null;
            int index = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getName().equals(name)) {
                    matchingItem = items.get(i);
                    index = i;
                    break;
                }
            }
            for (MovieEntry item : items) {

            }

            if (matchingItem == null) {
                // Item does not exist in the list, add it
                items.add(MovieEntry.EntryFromResource(name, mapData));
            } else {
                // Item exists in both list and map, check if data needs update
                if (!matchingItem.DataOut().equals(mapData)) {
                    matchingItem = MovieEntry.EntryFromResource(name, mapData); // Update data in the list
                    items.set(index, matchingItem);
                }
            }
        }

        // Remove items from the list that are not in the map
        // Remove item from the list
        items.removeIf(item -> !sp_data.containsKey(item.getName()));

        super.notifyDataSetChanged();
    }

    private void showDelDialog(String movie_name, int position){
        // Prompt
        String[] prompt = new String[] {"Are you sure you want to delete movie {", "}?"};

        View dialogView = LayoutInflater.from(context).inflate(R.layout.del_dialog, null);

        // Find views in the dialog layout
        TextView tv = dialogView.findViewById(R.id.deltextView);

        String sb = prompt[0] + movie_name + prompt[1];
        tv.setText(sb);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Enter Text")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle OK button click
                    items.remove(position);
                    this.notifyDataSetChanged();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove(movie_name);
                    if (sp.getString("__latest", "").equals(movie_name))
                        editor.putString("__latest", "");
                    editor.apply();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle Cancel button click
                    dialog.cancel();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
