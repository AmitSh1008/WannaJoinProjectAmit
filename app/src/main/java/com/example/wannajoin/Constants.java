package com.example.wannajoin;

public class Constants {
    public interface MESSANGER {
        public static final int     TO_SERVICE_HELLO                                = 301;
        public static final int     TO_SERVICE_PLAY_SONG                            = 302;

        public static final int     FROM_SERVICE_HELLO                              = 1;
        public static final int     FROM_SERVICE_PLAY_SONG                          = 2;
        public static final int     SERVICE_TO_ALARM_STOP_SONG                      = 3;
        public static final int     SERVICE_TO_PLAYING_NOW_GET_INFO                 = 4;


        public static final int     ALARM_TO_SERVICE_STOP_SONG                      = 501;

        public static final int     PLAYING_NOW_TO_SERVICE_GET_INFO                 = 801;
        public static final int     PLAYING_NOW_TO_SERVICE_PLAY_PAUSE               = 802;

    }

    public interface ACTION {
        public static final String SERVICE_STOP_ACTION = "SERVICE_STOP_MUSIC_ACTION";
        public static final String SERVICE_BUILD_ACTION = "SERVICE_BUILD_ACTION";
    }
}
