package widget.com.expandablelayout;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */

public class BindingUtil {
    @BindingAdapter({"bind:src"})
    public static void setImageBackground(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("bind:visibility")
    public static void visibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("bind:fontPath")
    public static void fontBold(TextView view, String fontPath) {
        if (fontPath != null) {
            TextView textView = (TextView) view;
            Typeface type = Typeface.createFromAsset(view.getContext().getAssets(), fontPath);
            textView.setTypeface(type, Typeface.BOLD);
        }
    }

}
