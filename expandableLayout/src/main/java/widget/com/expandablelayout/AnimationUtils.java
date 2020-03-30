package widget.com.expandablelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
class AnimationUtils {
    final static int COLLAPSING = 0;
    final static int EXPANDING = 1;
    private static AnimationUtils animationUtils;

    static AnimationUtils getInstance() {
        if (animationUtils == null)
            animationUtils = new AnimationUtils();
        return animationUtils;
    }

    void rotateAnimation(View view, final boolean expanding, long animDuration) {
        float toDegrees = expanding ? 180 : 0;
        if (animDuration == 0) {
            view.setRotation(toDegrees);
            return;
        }
        float fromDegrees = expanding ? 0 : 180;
        RotateAnimation arrowAnimation = new RotateAnimation(fromDegrees, toDegrees
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        arrowAnimation.setFillAfter(true);
        arrowAnimation.setDuration(animDuration);
        view.startAnimation(arrowAnimation);
    }

    void animateTextViewMaxLinesChange(final TextView textView, int initialHeight, final int maxLines
            , final int duration, boolean expanding, ExpandableLayout.OnExpandedListener listener) {
        if (duration == 0) {
            textView.setMaxLines(maxLines);
            textView.setVisibility(maxLines == 0 ? View.GONE : View.VISIBLE);
            return;
        }
//        final int initialHeight = textView.getMeasuredHeight();
        textView.setMaxLines(maxLines);
        textView.measure(View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED));
        final int endHeight = textView.getMeasuredHeight();
        ObjectAnimator animation = ObjectAnimator.ofInt(textView, "maxHeight", initialHeight, endHeight);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null && expanding)
                    listener.beforeExpand();
                textView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (maxLines == 0) textView.setVisibility(View.GONE);
                if (textView.getMaxHeight() == endHeight) {
                    textView.setMaxLines(maxLines);
                }
                if (listener != null)
                    listener.onExpandChanged(textView, expanding);
            }
        });
        animation.setDuration(duration).start();
    }

    void animateViewHeight(View view, int initialHeight, int targetHeight, long duration, boolean expanding, ExpandableLayout.OnExpandedListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofInt(view, new Property<View, Integer>(Integer.class, "height") {
            @Override
            public Integer get(View view) {
                return view.getHeight();
            }

            @Override
            public void set(View view, Integer value) {
                view.getLayoutParams().height = value;
                view.setLayoutParams(view.getLayoutParams());
            }
        }, initialHeight, targetHeight);
        animator.setDuration(duration);
        animator.setInterpolator(targetHeight < initialHeight ? new DecelerateInterpolator() : new AccelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null && expanding)
                    listener.beforeExpand();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.onExpandChanged(view, expanding);
            }
        });
        animator.start();
    }
}
