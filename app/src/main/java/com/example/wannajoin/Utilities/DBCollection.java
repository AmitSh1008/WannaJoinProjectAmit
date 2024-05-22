package com.example.wannajoin.Utilities;

import static com.example.wannajoin.Utilities.FBRef.refPlaylists;

import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Song song = (Song) o;
            return year == song.year &&
                    Objects.equals(id, song.id) &&
                    Objects.equals(name, song.name) &&
                    Objects.equals(singer, song.singer) &&
                    Objects.equals(duration, song.duration) &&
                    Objects.equals(genre, song.genre) &&
                    Objects.equals(image, song.image) &&
                    Objects.equals(link, song.link);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, singer, year, duration, genre, image, link);
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
        private DBCollection.Song currentSong;
        private String currentSongStartTime;
        private int currentSongPausedTime;
        private boolean currentSongEnd;
        private boolean isPlaying;

        public Room() {
        }

        public Room(String id, String name, String owner, int maxParts) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.participants = new ArrayList<>();
            this.maxParts = maxParts;
            this.playlist = FirebaseDatabase.getInstance().getReference().push().getKey();
            refPlaylists.child(this.playlist).setValue(new Playlist(this.playlist));
            this.isPlaying = false;
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

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public String getPlaylist() {
            return playlist;
        }

        public void setPlaylist(String playlist) {
            this.playlist = playlist;
        }

        public DBCollection.Song getCurrentSong() {
            return currentSong;
        }

        public void setCurrentSong(DBCollection.Song currentSong) {
            this.currentSong = currentSong;
        }

        public String getCurrentSongStartTime() {
            return currentSongStartTime;
        }

        public void setCurrentSongStartTime(String currentSongStartTime) {
            this.currentSongStartTime = currentSongStartTime;
        }

        public int getCurrentSongPausedTime() {
            return currentSongPausedTime;
        }

        public void setCurrentSongPausedTime(int currentSongPausedTime) {
            this.currentSongPausedTime = currentSongPausedTime;
        }

        public boolean isCurrentSongEnd() {
            return currentSongEnd;
        }

        public void setCurrentSongEnd(boolean currentSongEnd) {
            this.currentSongEnd = currentSongEnd;
        }
    }

    public static class Playlist{
        private String id;
        private ArrayList<String> songs;

        public Playlist() {
        }

        public Playlist(String id) {
            this.id = id;
            this.songs = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ArrayList<String> getSongs() {
            return songs;
        }

        public void setSongs(ArrayList<String> songs) {
            this.songs = songs;
        }

    }

    public static class PrivatePlaylist {
        private String id;
        private String name;
        private ArrayList<String> songs;
        private String creationDate;

        public PrivatePlaylist() {
        }

        public PrivatePlaylist(String id, String name) {
            this.id = id;
            this.name = name;
            this.songs = new ArrayList<>();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
            this.creationDate = sdf.format(calendar.getTime());
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
