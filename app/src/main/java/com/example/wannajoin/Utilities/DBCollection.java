package com.example.wannajoin.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DBCollection {

    static public class User implements Serializable {
        private String userId;
        private String name;
        private String email;
        private String phoneNum;
        private String status;
        private String image;
        private ArrayList<String> recents;
        private int hearingPoints;

        public User() {
        }
        public User(String userId, String name, String email, String phoneNum, String status, String image, ArrayList<String> recents, int hearingPoints) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.phoneNum = phoneNum;
            this.status = status;
            this.image = image;
            this.recents = recents;
            this.hearingPoints = hearingPoints;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public ArrayList<String> getRecents() {
            return recents;
        }

        public void setRecents(ArrayList<String> recents) {
            this.recents = recents;
        }

        public int getHearingPoints() {
            return hearingPoints;
        }

        public void setHearingPoints(int hearingPoints) {
            this.hearingPoints = hearingPoints;
        }

        public void addRecentSong(String Uid)
        {
            if (this.recents == null)
            {
                this.recents = new ArrayList<String>();
            }
            if (recents.contains(Uid))
            {
                recents.remove(Uid);
            }
            else if (recents.size() == 5) {
                recents.remove(4);
            }
            this.recents.add(0,Uid);
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static class UserActivitiesInfo
    {
        private ArrayList<String> followers;
        private ArrayList<String> following;
        private ArrayList<String> recentlyPlayed;
        private int hearingPoints;

        public UserActivitiesInfo() {
        }

        public UserActivitiesInfo(ArrayList<String> followers, ArrayList<String> following, ArrayList<String> recentlyPlayed, int hearingPoints) {
            this.followers = followers;
            this.following = following;
            this.recentlyPlayed = recentlyPlayed;
            this.hearingPoints = hearingPoints;
        }

        public ArrayList<String> getFollowers() {
            return followers;
        }

        public void setFollowers(ArrayList<String> followers) {
            this.followers = followers;
        }

        public ArrayList<String> getFollowing() {
            return following;
        }

        public void setFollowing(ArrayList<String> following) {
            this.following = following;
        }

        public ArrayList<String> getRecentlyPlayed() {
            return recentlyPlayed;
        }

        public void setRecentlyPlayed(ArrayList<String> recentlyPlayed) {
            this.recentlyPlayed = recentlyPlayed;
        }

        public int getHearingPoints() {
            return hearingPoints;
        }

        public void setHearingPoints(int hearingPoints) {
            this.hearingPoints = hearingPoints;
        }
    }

    public static class Song
    {
        private String id;
        private String name;
        private String singer;
        private int year;
        private String duration;
        private String genre;
        private String image;
        private String link;

        public Song()
        {

        }

        public Song(String id, String name, String singer, int year, String duration, String genre, String image, String link) {
            this.id = id;
            this.name = name;
            this.singer = singer;
            this.year = year;
            this.duration = duration;
            this.genre = genre;
            this.image = image;
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Singer{

        private String id;
        private String name;
        private String songs;
        private String image;

        public Singer() {
        }

        public Singer(String id, String name, String songs, String image) {
            this.id = id;
            this.name = name;
            this.songs = songs;
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSongs() {
            return songs;
        }

        public void setSongs(String songs) {
            this.songs = songs;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public static class Genre{
        private String id;
        private String name;
        private String songs;
        private String image;

        public Genre() {
        }

        public Genre(String id, String name, String songs, String image) {
            this.id = id;
            this.name = name;
            this.songs = songs;
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSongs() {
            return songs;
        }

        public void setSongs(String songs) {
            this.songs = songs;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }


    }

    public static class Room{
        private String id;
        private String name;
        private String owner;
        private ArrayList<String> participants;
        private int maxParts;
        private String playlist;
        private int currentSong;
        private String currentSongStartTime;
        private boolean isActive;

        public Room() {
        }

        public Room(String id, String name, String owner, int maxParts, boolean isActive) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.participants = new ArrayList<>();
            this.maxParts = maxParts;
            this.isActive = isActive;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public ArrayList<String> getParticipants() {
            return participants;
        }

        public void setParticipants(ArrayList<String> participants) {
            this.participants = participants;
        }

        public int getMaxParts() {
            return maxParts;
        }

        public void setMaxParts(int maxParts) {
            this.maxParts = maxParts;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

    public static class Playlist{
        private String id;
        private String name;
        private ArrayList<String> songs;
        private String creationDate;

        public Playlist() {
        }

        public Playlist(String id, String name) {
            this.id = id;
            this.name = name;
            this.songs = new ArrayList<>();
            Date currentTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            this.creationDate = dateFormat.format(currentTime);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getSongs() {
            return songs;
        }

        public void setSongs(ArrayList<String> songs) {
            this.songs = songs;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }
    }

}
