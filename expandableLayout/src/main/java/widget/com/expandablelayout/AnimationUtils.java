package widget.com.expandablelayout;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
public class AnimationUtils {
    final static int COLLAPSING = 0;
    final static int EXPANDING = 1;
    private static AnimationUtils animationUtils;

    public static AnimationUtils getInstance() {
        if (animationUtils == null)
            animationUtils = new AnimationUtils();
        return animationUtils;
    }

    RotateAnimation getRotateAnimation(final int animationType, long animDuration) {
        RotateAnimation arrowAnimation = animationType == EXPANDING ?
                new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);
        arrowAnimation.setDuration(animDuration);
        return arrowAnimation;
    }
}
