package com.turbybela.movietracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieEntry {
    private final String MovieName;
    private int Season;
    private int Episode;
    private boolean isVisible;

    public static MovieEntry EntryFromResource(String name, String rest){
        String[] split = ((String) rest).split(",");
        return new MovieEntry(name, Integer.parseInt(split[0]), Integer.parseInt(split[1]), (split[2].equals("1")));
    }

    public MovieEntry(String name, int season, int episode, boolean isVisible) {
        this.MovieName = name;
        this.Season = season;
        this.Episode = episode;
        this.isVisible = isVisible;
    }

    public String getName() {
        return this.MovieName;
    }

    public int GetSeason() {return this.Season; }
    public int GetEpisode() {return this.Episode; }

    public void IncrementEpisode(){
        Episode += 1;
    }
    public void DecrementEpisode(){
        Episode -= 1;
        if (Episode <= 0){
            Episode = 1;
        }
    }
    public void IncrementSeason(){
        Season+=1;
        Episode = 1;
    }
    public void DecrementSeason(){

        Season -= 1;
        if (Season <= 0) {
            Season = 1;
            Episode = 1;
        }
    }
    public boolean GetVisibility() { return isVisible; }
    public void ToggleVisibility() { isVisible = !isVisible; }
    public String DataOut(){
        return GetSeason() + "," + GetEpisode() + "," + (GetVisibility() ? "1" : "0");
    }
}
