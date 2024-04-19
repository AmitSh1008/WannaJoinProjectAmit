package com.example.wannajoin.Utilities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.wannajoin.Utilities.FBRef.refGenres;
import static com.example.wannajoin.Utilities.FBRef.refSingers;
import static com.example.wannajoin.Utilities.FBRef.refSongs;

import android.util.Log;

import com.example.wannajoin.Managers.FirebaseStorageManager;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class FBBuilder {

    public static void songsBuilder()
    {
        String[] names = { "I was never there", "Never go back", "An eisai ena asteri", "Hymn For The Weekend", "I Wanna Be Yours", "34+35", "Let me down slowly", "Moth To A Flame", "2002", "Under The Influence", "Outside", "לבד על המיטה", "מתגעגעת", "אהבה", "מתי את חוזרת", "Rockstar", "Circles", "Blinding Lights", "Watermelon Sugar", "Bad Guy", "Señorita", "Someone You Loved", "Dance Monkey", "Shape of You", "I Dont Care", "Old Town Road", "Believer", "Memories", "Lose You To Love Me", "Dont Start Now", "Rain On Me", "No Time To Die", "Cardigan", "Head & Heart", "Breaking Me", "Say So", "Falling", "you broke me first", "Savage Love" };
        String[] singers = { "The Weeknd", "Dennis Lloyd", "Nikos Vertis", "Coldplay", "Arctic Monkeys", "Ariana Grande", "Alec Benjamin", "The Weeknd", "Anne Marie", "Chris Brown", "Calvin Harris", "עומר אדם", "נתן גושן", "אושר כהן", "אייל גולן", "Post Malone", "Post Malone", "The Weeknd", "Harry Styles", "Billie Eilish", "Camila Cabello", "Lewis Capaldi", "Tones and I", "Ed Sheeran", "Ed Sheeran", "Lil Nas X", "Imagine Dragons", "Maroon 5", "Selena Gomez", "Dua Lipa", "Lady Gaga", "Billie Eilish", "Taylor Swift", "Joel Corry", "Topic", "Doja Cat", "Harry Styles", "Tate McRae", "Jason Derulo" };
        int[] years = { 2018, 2019, 2011, 2016, 2013, 2020, 2018, 2021, 2018, 2022, 2014, 2022, 2019, 2023, 2023, 2018, 2019, 2019, 2020, 2019, 2019, 2019, 2019, 2017, 2019, 2019, 2017, 2019, 2019, 2019, 2020, 2020, 2020, 2020, 2020, 2020, 2019, 2020, 2020 };
        String[] lengths = { "4:01", "2:56", "4:38", "4:20", "3:04", "2:53", "2:57", "4:13", "3:14", "2:56", "3:45", "2:35", "3:05", "3:27", "3:50", "2:38", "3:35", "3:20", "2:53", "3:14", "3:11", "3:02", "3:29", "3:53", "3:39", "1:53", "3:24", "3:09", "3:27", "3:03", "3:02", "4:02", "3:59", "2:46", "2:46", "4:58", "2:39", "2:19", "2:51" };
        String[] genres = { "R&B", "Pop", "Pop", "R&B & Pop", "R&B", "R&B & Pop", "Pop", "Pop", "Pop", "R&B", "Pop", "מזרחית", "Pop", "מזרחית", "מזרחית", "Hip Hop", "Pop", "R&B", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Hip Hop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Pop", "Hip Hop" };
        String[] images = { "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FI%20was%20never%20there.jpeg?alt=media&token=a39e6afc-8fe7-4ae8-87c1-5f79ed67e9e8",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FNever%20Go%20Back.jpg?alt=media&token=6bd700a5-12ad-48c7-b449-94aaf9c8c43f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FAn%20eisai%20ena%20asteri.jpeg?alt=media&token=52b33fb7-abb9-4d7c-86d6-ccb12af43201",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FHymn%20For%20The%20Weekend.jpeg?alt=media&token=5720b47a-bdd6-4b99-b55f-813307733515",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FI%20Wanna%20Be%20Yours.jpeg?alt=media&token=be181d26-d316-4394-9dfe-39fcb45cf7e4",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F34%2B35.jpeg?alt=media&token=543ff804-6698-478a-b3a9-244086f2b278",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FLet%20me%20down%20slowly.jpeg?alt=media&token=14452c5a-b3c6-47e4-8e95-11176a45669e",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FMoth%20To%20A%20Flame.jpeg?alt=media&token=42d2637a-bf76-48a2-a262-9670dbb22fc4",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F2002.jpeg?alt=media&token=cae93cf8-efe3-4778-bad3-70195699e6d5",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FUnder%20The%20Influence.jpeg?alt=media&token=1ca5e207-b8cb-4d29-834d-1cb5c39599da",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FOutside.jpeg?alt=media&token=f344b0cc-bbad-4004-8ee8-a98b674aaad3",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F%D7%9C%D7%91%D7%93%20%D7%A2%D7%9C%20%D7%94%D7%9E%D7%99%D7%98%D7%94.jpg?alt=media&token=d7d946f1-bd42-412d-a8c3-23f8f5fc2440",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F%D7%9E%D7%AA%D7%92%D7%A2%D7%92%D7%A2%D7%AA.jpeg?alt=media&token=7a4a87bc-20a4-4267-a34a-bc344cb3a279",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F%D7%90%D7%94%D7%91%D7%94.jpeg?alt=media&token=ae8c32e8-7b5d-4160-99d5-023bd7106b81",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2F%D7%9E%D7%AA%D7%99%20%D7%90%D7%AA%20%D7%97%D7%95%D7%96%D7%A8%D7%AA.jpeg?alt=media&token=4e857458-b4f0-4ff0-931d-3ed4ed963c00",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FRockstar.jpeg?alt=media&token=f5346779-e1f7-4d06-b145-69a16f8b9a1e",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FCircles.jpeg?alt=media&token=49496def-1d5e-4409-a6f9-89e55eace40c",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FBlinding%20Lights.jpeg?alt=media&token=d8bf438e-6255-41b6-bdfd-fc684e05c09b",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FWatermelon%20Sugar.jpeg?alt=media&token=ea785539-57b0-4571-8932-7951392d380f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FBad%20Guy.jpeg?alt=media&token=62e23eb0-7011-4a44-8ace-c7df4eb75a7a",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FSe%C3%B1orita.jpeg?alt=media&token=18a8b4fa-6b46-4974-9f33-4de058538ac3",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FSomeone%20You%20Loved.jpeg?alt=media&token=501eb7da-177b-4edb-b230-1a45598f2f70",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FDance%20Monkey.jpeg?alt=media&token=18f09776-3342-4969-a613-95bb0ed2f2b2",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FShape%20of%20You.jpeg?alt=media&token=5b077bd9-d4c4-4d7d-a700-ee950421b749",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FI%20Dont%20Care.jpeg?alt=media&token=1fc424d4-fd1d-4c01-8843-ee0218c07c3a",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FOld%20Town%20Road.jpeg?alt=media&token=81327e26-8fec-40ae-a82a-fadd5f2cf62f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FBeliever.jpeg?alt=media&token=dd3ba5d3-49a4-4669-bd3d-0b1ccd9ecb01",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FMemories.jpeg?alt=media&token=741f5e9c-cf04-442f-ba91-b1a60db73b30",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FLose%20You%20To%20Love%20Me.jpeg?alt=media&token=6ec77119-5c40-45fa-bed9-6e5f78998427",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FDont%20Start%20Now.jpeg?alt=media&token=7b4e1847-30b7-40b3-b549-950289f85f9c",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FRain%20On%20Me.jpeg?alt=media&token=4ac6daee-dc05-4263-9ec3-d8490aabdb7f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FNo%20Time%20To%20Die.jpeg?alt=media&token=6f217b2f-d757-49d9-8754-584fa2393d10",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FCardigan.jpeg?alt=media&token=f49dc061-14de-4e42-8772-018644f92d09",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FHead%20%26%20Heart.jpeg?alt=media&token=f5ea173b-8c65-43b1-8d53-dc057ec1ca02",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FBreaking%20Me.jpeg?alt=media&token=6962fcf1-d4a9-4c4c-94d3-5c2b89886bb1",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FSay%20So.jpeg?alt=media&token=0cc1d3af-93c4-406f-9d78-f45c3da731a3",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FFalling.jpeg?alt=media&token=cf4e93d4-c6b4-4641-9882-e5a40ad800d8",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2Fyou%20broke%20me%20first.jpeg?alt=media&token=2033c51d-1198-44f0-8dc0-6db8f891b2b9",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FSongsImages%2FSavage%20Love.jpeg?alt=media&token=65f4bae0-e429-4fc7-b4cd-24472142faa2"
        };
        String[] links = { "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FThe%20Weeknd%20-%20I%20Was%20Never%20There.mp3?alt=media&token=206bcb87-4518-4967-9171-ccb2359a24ab",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FDennis%20Lloyd%20-%20Never%20Go%20Back.mp3?alt=media&token=06783815-bfb2-4799-ace2-baed9ebd4950",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FNikos%20Vertis%20-%20An%20eisai%20ena%20asteri.mp3?alt=media&token=746ff2fa-23a5-4c51-ac93-01f2e3787db6",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FColdplay%20-%20Hymn%20For%20The%20Weekend.mp3?alt=media&token=163d14be-dd6d-400b-854b-3c53a3d940ee",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FArctic%20Monkeys%20-%20I%20Wanna%20Be%20Yours.mp3?alt=media&token=72593033-9ae8-47ae-b729-6efa91231a1f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FAriana%20Grande%20-%2034%2B35.mp3?alt=media&token=0a0722e3-ce82-420f-884f-c5b53a418842",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FAlec%20Benjamin%20-%20Let%20Me%20Down%20Slowly.mp3?alt=media&token=1f82e491-5261-475c-8a04-0aa6380d01cf",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FThe%20Weeknd%20-%20Moth%20To%20A%20Flame.mp3?alt=media&token=c9d5d463-7984-45c8-9ed7-f37ace3bb626",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FAnne%20Marie%20-%202002.mp3?alt=media&token=7b628206-46cb-420c-a646-892da232fe2f",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FChris%20Brown%20-%20Under%20The%20Influence.mp3?alt=media&token=bde52f0a-2b10-4fbb-82de-390b733cd797",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FCalvin%20Harris%20-%20Outside.mp3?alt=media&token=9962efd1-21a3-477b-9a7a-fc7d837ee90a",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2F%D7%A2%D7%95%D7%9E%D7%A8%20%D7%90%D7%93%D7%9D%20-%20%D7%9C%D7%91%D7%93%20%D7%A2%D7%9C%20%D7%94%D7%9E%D7%99%D7%98%D7%94.mp3?alt=media&token=326d9159-c12e-4657-8c47-89f9ec52b99b",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2F%D7%A0%D7%AA%D7%9F%20%D7%92%D7%95%D7%A9%D7%9F%20-%20%D7%9E%D7%AA%D7%92%D7%A2%D7%92%D7%A2%D7%AA.mp3?alt=media&token=b3cb7a08-33e7-41e2-86c5-c1eb447a0997",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2F%D7%90%D7%95%D7%A9%D7%A8%20%D7%9B%D7%94%D7%9F%20-%20%D7%90%D7%94%D7%91%D7%94.mp3?alt=media&token=b29474a1-0de4-42b8-b4c4-e106c89489eb",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2F%D7%90%D7%99%D7%99%D7%9C%20%D7%92%D7%95%D7%9C%D7%9F%20-%20%D7%9E%D7%AA%D7%99%20%D7%90%D7%AA%20%D7%97%D7%95%D7%96%D7%A8%D7%AA.mp3?alt=media&token=1c511f5d-35e1-436a-b9e6-2a19b8684aa2",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FPost%20Malone%20-%20rockstar.mp3?alt=media&token=279176b4-37f4-49b3-ba0d-e7f59f374ece",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FPost%20Malone%20-%20Circles.mp3?alt=media&token=b9f90761-7f05-4ae0-ad78-8f8787c64744",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FThe%20Weeknd%20-%20Blinding%20Lights.mp3?alt=media&token=966f3842-154e-4e1f-8d07-95b20f6424b0",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FHarry%20Styles%20-%20Watermelon%20Sugar.mp3?alt=media&token=7bdc8f1d-a8a2-41f6-a1da-d939402a62ed",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FBillie%20Eilish%20-%20bad%20guy.mp3?alt=media&token=ee0b663e-b4e7-4a55-870b-0cf26f2a88b2",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FCamila%20Cabello%20-%20Senorita.mp3?alt=media&token=0635711f-fa46-49f9-9add-aff293481604",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FLewis%20Capaldi%20-%20Someone%20You%20Loved.mp3?alt=media&token=f6fe7816-5a9a-4840-8dc4-d526a0779a60",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FTONES%20AND%20I%20-%20DANCE%20MONKEY.mp3?alt=media&token=b12baa4a-6cdd-42c8-962d-8e538abbbbaa",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FEd%20Sheeran%20-%20Shape%20of%20You.mp3?alt=media&token=c3cf83fa-4886-4881-b509-0cfedf61f874",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FEd%20Sheeran%20-%20I%20Dont%20Care.mp3?alt=media&token=a956246f-2983-4f84-9fdb-9baab5eacd6c",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FLil%20Nas%20X%20-%20Old%20Town%20Road.mp3?alt=media&token=766e5b18-b00a-42a7-b7ab-9b9d3fb26b4c",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FImagine%20Dragons%20-%20Believer.mp3?alt=media&token=3528c4b0-5b62-4279-8c39-8668613d737a",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FMaroon%205%20-%20Memories.mp3?alt=media&token=091ff52f-1748-4a5d-8a80-4e4da817e6db",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FSelena%20Gomez%20-%20Lose%20You%20To%20Love%20Me.mp3?alt=media&token=43ec6370-e2da-41a0-8a72-e0a6f0994e9a",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FDua%20Lipa%20-%20Dont%20Start%20Now.mp3?alt=media&token=55982263-f33e-41e7-b8bd-0497b998651d",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FLady%20Gaga%20-%20Rain%20On%20Me.mp3?alt=media&token=3ad8e9a3-9b2f-4c4b-8d70-c533fe26094b",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FBillie%20Eilish%20-%20No%20Time%20To%20Die.mp3?alt=media&token=84a77e1b-a87c-4817-80e8-6cdbc952ca25",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FTaylor%20Swift%20-%20cardigan.mp3?alt=media&token=e042d702-ea0a-41b0-bd81-a82c5482e994",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FJoel%20Corry%20-%20%20Head%20%26%20Heart.mp3?alt=media&token=68b0f96e-47d6-46c1-8915-c199deaa9259",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FTopic%20-%20Breaking%20Me.mp3?alt=media&token=d1281635-b089-45ec-878d-e4714c56d8c0",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FDoja%20Cat%20-%20Say%20So.mp3?alt=media&token=d8426150-1024-4702-b0f1-854076f68439",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FHarry%20Styles%20-%20Falling.mp3?alt=media&token=3cc79c59-88b7-4769-b821-26543feced55",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FTate%20McRae%20-%20you%20broke%20me%20first.mp3?alt=media&token=ae51fb86-4324-4cf1-848c-81b55b9a679e",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Songs%2FJason%20Derulo%20-%20Savage%20Love.mp3?alt=media&token=f929290a-8046-4a7a-aaff-091ce0970380 "
        };
        for (int i = 0; i < 39; i++)
        {
            final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
            DBCollection.Song song = new DBCollection.Song(pushId, names[i],singers[i],years[i],lengths[i],genres[i], images[i],links[i]);
            refSongs.child(pushId).setValue(song);
        }
    }

    public static void singersBuilder()
    {
        String[] names = { "The Weeknd", "Dennis Lloyd", "Nikos Vertis", "Coldplay", "Arctic Monkeys", "Ariana Grande", "Alec Benjamin", "Anne Marie", "Chris Brown", "Calvin Harris", "עומר אדם", "נתן גושן", "אושר כהן", "אייל גולן", "Post Malone", "Harry Styles", "Billie Eilish", "Camila Cabello", "Lewis Capaldi", "Tones and I", "Ed Sheeran", "Lil Nas X", "Imagine Dragons", "Maroon 5", "Selena Gomez", "Dua Lipa", "Lady Gaga", "Taylor Swift", "Joel Corry", "Topic", "Doja Cat", "Tate McRae", "Jason Derulo" };
        String[] songs = { "I was never there,Moth To A Flame,Blinding Lights", "Never go back", "An eisai ena asteri", "Hymn For The Weekend", "I Wanna Be Yours", "34+35", "Let me down slowly", "2002", "Under The Influence", "Outside", "לבד על המיטה", "מתגעגעת", "אהבה", "מתי את חוזרת", "Rockstar,Circles", "Watermelon Sugar,Falling", "Bad Guy,No Time To Die", "Señorita", "Someone You Loved", "Dance Monkey", "I Dont Care,Shape of You", "Old Town Road", "Believer", "Memories", "Lose You To Love Me", "Dont Start Now", "Rain On Me", "Cardigan", "Head & Heart", "Breaking Me", "Say So", "you broke me first", "Savage Love" };

        FirebaseStorageManager.getFilesFromSubfolder("Images/SingersImages/", new FirebaseStorageManager.FirebaseStorageCallback() {
            @Override
            public void onFilesReceived(Map<String, String> fileTokenMap) {
                int singersCounter = 0;
                for (Map.Entry<String, String> entry : fileTokenMap.entrySet()) {
                    Log.d(TAG, "File: " + entry.getKey() + ", Access Token: " + entry.getValue());
                    String[] names = { "The Weeknd", "Dennis Lloyd", "Nikos Vertis", "Coldplay", "Arctic Monkeys", "Ariana Grande", "Alec Benjamin", "Anne Marie", "Chris Brown", "Calvin Harris", "עומר אדם", "נתן גושן", "אושר כהן", "אייל גולן", "Post Malone", "Harry Styles", "Billie Eilish", "Camila Cabello", "Lewis Capaldi", "Tones and I", "Ed Sheeran", "Lil Nas X", "Imagine Dragons", "Maroon 5", "Selena Gomez", "Dua Lipa", "Lady Gaga", "Taylor Swift", "Joel Corry", "Topic", "Doja Cat", "Tate McRae", "Jason Derulo" };
                    String[] songs = { "I was never there,Moth To A Flame,Blinding Lights", "Never go back", "An eisai ena asteri", "Hymn For The Weekend", "I Wanna Be Yours", "34+35", "Let me down slowly", "2002", "Under The Influence", "Outside", "לבד על המיטה", "מתגעגעת", "אהבה", "מתי את חוזרת", "Rockstar,Circles", "Watermelon Sugar,Falling", "Bad Guy,No Time To Die", "Señorita", "Someone You Loved", "Dance Monkey", "I Dont Care,Shape of You", "Old Town Road", "Believer", "Memories", "Lose You To Love Me", "Dont Start Now", "Rain On Me", "Cardigan", "Head & Heart", "Breaking Me", "Say So", "you broke me first", "Savage Love" };
                    final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
                    DBCollection.Singer singer = new DBCollection.Singer(pushId, names[singersCounter],songs[singersCounter],fileTokenMap.get(names[singersCounter]));
                    singersCounter++;
                    refSingers.child(pushId).setValue(singer);
                }
            }
           /* @Override
            public void onFilesReceived(Map<String, String> fileTokenMap) {
                for (int i = 0; i < 33; i++) {
                    DBCollection.Singer singer = new DBCollection.Singer(names[i], songs[i], fileTokenMap.get(names[i]));
                    final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
                    refSingers.child(pushId).setValue(singer);
                }
            }*/

            @Override
            public void onFailure(Exception e) {
                // Handle failure here
                Log.e(TAG, "Error retrieving files: " + e.getMessage());
            }
        });
    }

    public static void genresBuilder()
    {
        String[] genres = { "Pop", "R&B", "מזרחית", "Hip Hop"};
        String[] songs = { "Never go back,An eisai ena asteri,Hymn For The Weekend,34+35,Let me down slowly,Moth To A Flame,2002,Outside,Circles,Watermelon Sugar,Bad Guy,Señorita,Someone You Loved,Dance Monkey,Shape of You,I Dont Care,Believer,Memories,Lose You To Love Me,Dont Start Now,Rain On Me,No Time To Die,Cardigan,Head & Heart,Breaking Me,Say So,Falling,you broke me first", "I was never there,Hymn For The Weekend,I Wanna Be Yours,34+35,Under The Influence,Blinding Lights", "לבד על המיטה,מתגעגעת,אהבה,מתי את חוזרת", "Rockstar,Old Town Road,Savage Love" };
        String[] images = { "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FGenresImages%2FPop.jpeg?alt=media&token=494d4805-7015-431a-bee7-f2734b807019",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FGenresImages%2FRandB.jpeg?alt=media&token=b6999ecb-c31e-4e2a-95c9-0a29c46e01ab",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FGenresImages%2F%D7%9E%D7%96%D7%A8%D7%97%D7%99%D7%AA.jpeg?alt=media&token=186c16fa-112e-4be2-96f6-4c9a2ca216b5",
                "https://firebasestorage.googleapis.com/v0/b/wannajoin-ecec8.appspot.com/o/Images%2FGenresImages%2FHipHop.jpeg?alt=media&token=712d9371-7415-4e51-b854-7c25fe171fc2"};
        for (int i = 0; i < 4; i++)
        {
            final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
            DBCollection.Genre genre = new DBCollection.Genre(pushId, genres[i],songs[i],images[i]);
            refGenres.child(pushId).setValue(genre);
        }
    }

}