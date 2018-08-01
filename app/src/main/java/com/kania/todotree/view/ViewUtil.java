package com.kania.todotree.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kania.todotree.R;

public class ViewUtil {
    public static void changeStatusbarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            int flag = decorView.getSystemUiVisibility();
            flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flag);
        }
    }

    public static void setMenuItemColor(MenuItem item, int color) {
        Drawable d = item.getIcon();
        setDrawableColor(d, color);
        item.setIcon(d);
    }

    public static void setCheckBoxColor(AppCompatCheckBox checkBox, int targetColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] {  android.R.attr.state_enabled }, // enabled
                        new int[] { -android.R.attr.state_enabled }  // disabled
                },
                new int[] {
                        targetColor,
                        Color.LTGRAY
                }
        );
        checkBox.setSupportButtonTintList(colorStateList);
    }

    public static void setButtonColor(Button btn, int color) {
        Drawable d = btn.getBackground();
        setDrawableColor(d, color);
        btn.setBackgroundDrawable(d);
    }

    public static void setImageButtonColor(ImageButton btn, int color) {
        Drawable d = btn.getDrawable();
        setDrawableColor(d, color);
        btn.setImageDrawable(d);
    }

    public static void setEditColor(EditText edit, int color) {
        Drawable d = edit.getBackground();
        setDrawableColor(d, color);
        edit.setBackgroundDrawable(d);
    }

    public static void setDialogButtonColor(final AlertDialog dialog, final int positiveColor,
                                            final int neutralColor, final int negativeColor) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dlg) {
                Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setTextColor(positiveColor);
                Button btnNeutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                btnNeutral.setTextColor(neutralColor);
                Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                btnNegative.setTextColor(negativeColor);
            }
        });
    }

    public static void setDrawableColor(Drawable d, int color) {
        d.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setIndentation (View v, int depth) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            int newLeftMargin = /*p.rightMargin + */(depth * 70); // right never changed
            p.setMargins(newLeftMargin, p.topMargin, p.rightMargin, p.bottomMargin);
            v.requestLayout();
        }
    }
}
