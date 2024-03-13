package com.example.wannajoin;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static void ShowToast(Context context, String info, String color) {

        SpannableStringBuilder biggerText = new SpannableStringBuilder(info);
        biggerText.setSpan(new RelativeSizeSpan(3f), 0, info.length(), 0);
        Toast toast = Toast.makeText(context, Html.fromHtml("<font color='" + color + "'><h3><b>" + biggerText + "</b></h3></font>", Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.TRANSPARENT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        toast.show();
    }
}

