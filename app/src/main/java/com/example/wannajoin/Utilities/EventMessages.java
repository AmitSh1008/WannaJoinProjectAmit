package com.example.wannajoin.Utilities;

public class EventMessages {
    public static class PlaySongEvent
    {
        private DBCollection.Song song;

        public PlaySongEvent() {

        }
        public PlaySongEvent(DBCollection.Song song) {
            this.song = song;
        }

        public DBCollection.Song getSong() {
            return song;
        }

        public void setSong(DBCollection.Song song) {
            this.song = song;
        }
    }

    public static class SingerLibraryEvent
    {
        private DBCollection.Singer singer;


        public SingerLibraryEvent() {
        }

        public SingerLibraryEvent(DBCollection.Singer singer) {
            this.singer = singer;
        }

        public DBCollection.Singer getSinger() {
            return singer;
        }

        public void setSinger(DBCollection.Singer singer) {
            this.singer = singer;
        }
    }

    public static class GenreLibraryEvent
    {
        private DBCollection.Genre genre;


        public GenreLibraryEvent() {
        }

        public GenreLibraryEvent(DBCollection.Genre genre) {
            this.genre = genre;
        }

        public DBCollection.Genre getGenre() {
            return genre;
        }

        public void setGenre(DBCollection.Genre genre) {
            this.genre = genre;
        }
    }

    public static class LibraryResultChangedEvent
    {
        private int size;

        public LibraryResultChangedEvent() {
        }

        public LibraryResultChangedEvent(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public static class SongInfoEvent
    {
        private int state;
        private DBCollection.Song song;
        private int duration;
        private int currentTime;

        public SongInfoEvent() {

        }

        public SongInfoEvent(int state, DBCollection.Song song, int duration, int currentTime) {
            this.state = state;
            this.song = song;
            this.duration = duration;
            this.currentTime = currentTime;
        }

        public DBCollection.Song getSong() {
            return song;
        }

        public void setSong(DBCollection.Song song) {
            this.song = song;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(int currentTime) {
            this.currentTime = currentTime;
        }
    }

    public static class RecentAdded {
        public RecentAdded() {

        }
    }

    public static class FollowersFollowingsChanged {
        private boolean isFollowers;
        public FollowersFollowingsChanged(boolean isFollowers) {
            this.isFollowers = isFollowers;
        }

        public boolean isFollowers() {
            return isFollowers;
        }
    }


    public static class DataChangedInRoom {
        private boolean isParticipants;

        public DataChangedInRoom(boolean isParticipants) {
            this.isParticipants = isParticipants;
        }
        public boolean isParticipants() {
            return isParticipants;
        }
    }

    public static class UserInRoomStateChanged {
        private boolean isInRoom;
        public UserInRoomStateChanged(boolean isInRoom) {
            this.isInRoom = isInRoom;
        }
        public boolean isInRoom() {
            return isInRoom;
        }
    }

    public static class SongStarted {
        private int currentSongDuration;

        public SongStarted(int currentSongDuration) {
            this.currentSongDuration = currentSongDuration;
        }
        public int getCurrentSongDuration() {
            return currentSongDuration;
        }
    }

    public static class SongPausedPlayed {
        private boolean isPlaying;
        private int playtime;

        public SongPausedPlayed(boolean isPlaying, int playtime) {
            this.isPlaying = isPlaying;
            this.playtime = playtime;
        }
        public boolean isPlaying() {
            return isPlaying;
        }

        public int getPlaytime() {
            return playtime;
        }
    }

    public static class SongEnded {
        public SongEnded() {
        }
    }

}
