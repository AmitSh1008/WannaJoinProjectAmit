package com.example.wannajoin;

import java.io.Serializable;
import java.util.ArrayList;

public class DBCollection {

    static public class User {
        private String userId;
        private String name;
        private String email;
        private String phoneNum;
        private String status;
        private String image;

        public User() {
        }
        public User(String userId, String name, String email, String phoneNum, String status, String image) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNum = phoneNum;
            this.status = status;
            this.image = image;
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
    }

    static class UserActivitiesInfo
    {
        private ArrayList<String> friends;
        private ArrayList<String> recentlyPlayed;
        private int hearingPoints;
    }

    static class Song
    {
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

        public Song(String name, String singer, int year, String duration, String genre, String image, String link) {
            this.name = name;
            this.singer = singer;
            this.year = year;
            this.duration = duration;
            this.genre = genre;
            this.image = image;
            this.link = link;
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

    static class Singer{
        private String name;
        private String songs;
        private String image;

        public Singer() {
        }

        public Singer(String name, String songs, String image) {
            this.name = name;
            this.songs = songs;
            this.image = image;
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

    static class Genre{
        private String name;
        private String songs;
        private String image;

        public Genre() {
        }

        public Genre(String name, String songs, String image) {
            this.name = name;
            this.songs = songs;
            this.image = image;
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

}
