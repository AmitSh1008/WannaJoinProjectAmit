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
        private boolean isPlaying;
        private DBCollection.Song song;

        public SongInfoEvent() {

        }
        public SongInfoEvent(boolean isPlaying, DBCollection.Song song) {
            this.isPlaying = isPlaying;
            this.song = song;
        }

        public DBCollection.Song getSong() {
            return song;
        }

        public void setSong(DBCollection.Song song) {
            this.song = song;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
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

    public static class ParticipantsChangedInRoom {
        private boolean isParticipants;
        public ParticipantsChangedInRoom(boolean isParticipants) {
            this.isParticipants = isParticipants;
        }
        public boolean isParticipants() {
            return isParticipants;
        }
    }
}
