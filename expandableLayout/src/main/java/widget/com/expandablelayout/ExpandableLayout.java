package widget.com.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import widget.com.expandablecardview.R;

import static widget.com.expandablelayout.AnimationUtils.COLLAPSING;
import static widget.com.expandablelayout.AnimationUtils.EXPANDING;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
public class ExpandableLayout extends RelativeLayout {
    private Boolean isAnimationRunning = false;
    private Boolean isOpened = false;
    private Integer duration;
    private FrameLayout contentLayout;
    private FrameLayout headerLayout;
    private RelativeLayout container;
    private ImageButton arrowBtn;
    private Animation animation;
    private ExpandableLayout.OnExpandedListener listener;
    private int headerLayoutRes;
    private int contentLayoutRes;
    private Drawable arrowIconRes;
    private TypedArray attributesArray;

    public ExpandableLayout(Context context) {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        initViews(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
        initViews(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        attributesArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        headerLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_headerLayout, -1);
        contentLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_contentLayout, -1);
        duration = attributesArray.getInt(R.styleable.ExpandableLayout_duration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        arrowIconRes = attributesArray.getDrawable(R.styleable.ExpandableLayout_arrow_icon);
    }

    private void initViews(final Context context, AttributeSet attrs) {
        final View rootView = View.inflate(context, R.layout.expandable_layout, this);
        headerLayout = rootView.findViewById(R.id.view_expandable_headerlayout);
        contentLayout = rootView.findViewById(R.id.view_expandable_contentLayout);
        container = rootView.findViewById(R.id.container);
        container.setOnClickListener(this::onLayoutClicked);
        inflateInnerViews(context);
    }

    private void inflateInnerViews(Context context) {
        if (headerLayoutRes == -1)
            inflateDefaultHeader(context);
        if (contentLayoutRes == -1)
            inflateDefaultContent(context);
        else {
            inflateHeader(context, headerLayoutRes);
            inflateContent(context, contentLayoutRes);
        }
        attributesArray.recycle();
    }

    private void inflateDefaultHeader(Context context) {
        headerLayout.addView(inflateView(context, R.layout.default_header));
        arrowBtn = headerLayout.findViewById(R.id.arrow);
        setDrawableBackground(arrowBtn, arrowIconRes);
        TextView headerTV = (headerLayout.findViewById(R.id.headerTV));
        String headerTxt = attributesArray.getString(R.styleable.ExpandableLayout_headerText);
        headerTV.setText(headerTxt);
        Drawable headerIcon = attributesArray.getDrawable(R.styleable.ExpandableLayout_header_icon);
        if (headerIcon != null)
            ((ImageButton) headerLayout.findViewById(R.id.headerIcon)).setImageDrawable(headerIcon);
        int headerTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_header_color, Color.BLACK);
        if (headerTV != null)
            headerTV.setTextColor(headerTextColor);
    }

    private void inflateDefaultContent(Context context) {
        contentLayout.addView(inflateView(context, R.layout.default_content));
        TextView contentTV = (contentLayout.findViewById(R.id.contentTV));
        String contentTxt = attributesArray.getString(R.styleable.ExpandableLayout_contentText);
        contentTV.setText(contentTxt);
        int contentTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_content_color, Color.BLACK);
        if (contentTV != null)
            contentTV.setTextColor(contentTextColor);
    }

    private void inflateHeader(Context context, int viewID) {
        headerLayout.addView(inflateView(context, viewID));
    }

    private void inflateContent(Context context, int viewID) {
        contentLayout.addView(inflateView(context, viewID));
    }

    private View inflateView(Context context, int viewID) {
        final View view = View.inflate(context, viewID, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return view;
    }

    private void setDrawableBackground(ImageButton imageButton, Drawable drawable) {
        if (drawable != null) {
            imageButton.setVisibility(VISIBLE);
            imageButton.setBackground(drawable);
        }
    }

    private void onLayoutClicked(View v) {
        if (!isAnimationRunning) {
            if (contentLayout.getVisibility() == VISIBLE)
                collapse(contentLayout);
            else
                expand(contentLayout);

            setAnimatingStateEnabled();
        }
    }

    private void setAnimatingStateEnabled() {
        isAnimationRunning = true;
        new Handler().postDelayed(() -> isAnimationRunning = false, duration);
    }

    private void expand(final View view) {
        container.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        animateViews(view, container.getHeight(), container.getMeasuredHeight()
                , EXPANDING);
    }

    private void startArrowRotation(int animationType) {
        if (arrowBtn == null)
            return;
        RotateAnimation arrowAnimation = AnimationUtils.getInstance().getArrowAnimation(animationType, duration);
        arrowBtn.startAnimation(arrowAnimation);
    }

    private void collapse(final View view) {
        view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        animateViews(view, view.getMeasuredHeight(), contentLayout.getMeasuredHeight(), COLLAPSING);
    }

    private void animateViews(final View view, final int initialHeight, final int distance, final int animationType) {
        if (animationType == EXPANDING)
            view.setVisibility(VISIBLE);
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    if (animationType == COLLAPSING)
                        view.setVisibility(GONE);
                    isOpened = animationType != COLLAPSING;
                    updateListener(view, animationType);
                }
                view.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));
                contentLayout.requestLayout();

                contentLayout.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);

        startAnimation(animation);
        startArrowRotation(animationType);
    }

    private void updateListener(View view, int animationType) {
        if (listener != null) {
            if (animationType == EXPANDING) {
                listener.onExpandChanged(view, true);
            } else {
                listener.onExpandChanged(view, false);
            }
        }
    }

    public Boolean isOpened() {
        return isOpened;
    }

    public void show() {
        if (!isAnimationRunning) {
            expand(contentLayout);
            isAnimationRunning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = false;
                }
            }, duration);
        }
    }

    public FrameLayout getHeaderLayout() {
        return headerLayout;
    }

    public FrameLayout getContentLayout() {
        return contentLayout;
    }

    public void hide() {
        if (!isAnimationRunning) {
            collapse(contentLayout);
            isAnimationRunning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = false;
                }
            }, duration);
        }
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        animation.setAnimationListener(animationListener);
    }

    public void setOnExpandedListener(ExpandableLayout.OnExpandedListener listener) {
        this.listener = listener;
    }

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }
}

