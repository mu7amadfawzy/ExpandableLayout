package widget.com.expandablelayout;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

public class DimenUtils {
    private static DimenUtils dimenUtils;

    public static DimenUtils getInstance() {
        if (dimenUtils == null)
            dimenUtils = new DimenUtils();
        return dimenUtils;
    }

    public void setTextSize(float textSize, TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX
                , determineTextSize(textView.getTypeface(), textSize));
    }

    private int determineTextSize(Typeface font, float allowableHeight) {
        Paint p = new Paint();
        p.setTypeface(font);

        int size = (int) allowableHeight;
        p.setTextSize(size);

        float currentHeight = calculateHeight(p.getFontMetrics());

        while (size != 0 && (currentHeight) > allowableHeight) {
            p.setTextSize(size--);
            currentHeight = calculateHeight(p.getFontMetrics());
        }

        if (size == 0) {
            return (int) allowableHeight;
        }
        return size;
    }

    private float calculateHeight(Paint.FontMetrics fm) {
        return fm.bottom - fm.top;
    }
}
