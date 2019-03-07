package widget.com.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
    private boolean isAnimationRunning;
    private boolean isExpanded = true;
    private Integer duration = 300;
    private FrameLayout contentLayout;
    private FrameLayout headerLayout;
    private RelativeLayout container;
    private ImageButton arrowBtn;
    private Animation animation;
    private ExpandableLayout.OnExpandedListener listener;
    private int headerLayoutRes = -1, contentLayoutRes = -1;
    private Drawable arrowIconRes;
    private TypedArray attributesArray;
    private boolean startExpanded, headerBold;
    private Context context;
    private TextView defaultContentTV, defaultHeaderTV;
    private float header_text_size, content_text_size;

    public ExpandableLayout(Context context) {
        super(context);
        this.context = context;
        initViews(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttributes(context, attrs);
        initViews(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttributes(context, attrs);
        initViews(context);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        attributesArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        headerLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_header_layout, -1);
        contentLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_content_layout, -1);
        duration = attributesArray.getInt(R.styleable.ExpandableLayout_duration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        arrowIconRes = attributesArray.getDrawable(R.styleable.ExpandableLayout_arrow_icon);
        startExpanded = attributesArray.getBoolean(R.styleable.ExpandableLayout_startExpanded, false);
        header_text_size = attributesArray.getDimension(R.styleable.ExpandableLayout_header_text_size, -1);
        content_text_size = attributesArray.getInt(R.styleable.ExpandableLayout_content_text_size, -1);
        headerBold = attributesArray.getBoolean(R.styleable.ExpandableLayout_header_bold, false);
    }

    private void initViews(final Context context) {
        final View rootView = View.inflate(context, R.layout.expandable_layout, this);
        headerLayout = rootView.findViewById(R.id.view_expandable_header_layout);
        contentLayout = rootView.findViewById(R.id.view_expandable_content_layout);
        container = rootView.findViewById(R.id.container);
        headerLayout.setOnClickListener(this::onLayoutClicked);
        inflateInnerViews(context);
        if (!startExpanded)
            toggle(false);
    }

    private void inflateInnerViews(Context context) {
        if (headerLayoutRes == -1)
            inflateDefaultHeader(context);
        else inflateHeader(context, headerLayoutRes);
        if (contentLayoutRes == -1)
            inflateDefaultContent(context);
        else inflateContent(context, contentLayoutRes);

        if (attributesArray != null)
            attributesArray.recycle();
    }

    private void inflateDefaultHeader(Context context) {
        headerLayout.addView(inflateView(context, R.layout.default_header));
        arrowBtn = headerLayout.findViewById(R.id.arrow);
        defaultHeaderTV = (headerLayout.findViewById(R.id.headerTV));
        setDrawableBackground(arrowBtn, arrowIconRes);
        if (attributesArray == null)
            return;
        String headerTxt = attributesArray.getString(R.styleable.ExpandableLayout_header_title);
        int headerTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_header_color, Color.BLACK);
        setDefaultHeaderTitle(headerTxt, headerTextColor);
        Drawable headerIcon = attributesArray.getDrawable(R.styleable.ExpandableLayout_header_icon);
        setArrowDrawable(headerIcon);
        setHeaderTextSize(header_text_size);
        setContentTextSize(content_text_size);
        setHeaderTextStyle(Typeface.defaultFromStyle(headerBold ? Typeface.BOLD : Typeface.NORMAL));
    }

    public void setHeaderTextStyle(Typeface typeface) {
        defaultHeaderTV.setTypeface(typeface);
    }

    public void setContentTextSize(float textSize) {
        if (textSize != -1)
            defaultContentTV.setTextSize(textSize);
    }

    public void setHeaderTextSize(float textSize) {
        if (textSize != -1)
            defaultHeaderTV.setTextSize(textSize);
    }

    private void inflateDefaultContent(Context context) {
        contentLayout.addView(inflateView(context, R.layout.default_content));
        defaultContentTV = (contentLayout.findViewById(R.id.contentTV));
        if (attributesArray == null)
            return;
        String contentTxt = attributesArray.getString(R.styleable.ExpandableLayout_content_text);
        defaultContentTV.setText(contentTxt);
        int contentTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_content_color, Color.BLACK);
        setDefaultContentTitle(contentTxt, contentTextColor);
    }

    private void inflateHeader(Context context, int viewID) {
        headerLayout.removeAllViews();
        headerLayout.addView(inflateView(context, viewID));
    }

    private void inflateContent(Context context, int viewID) {
        contentLayout.removeAllViews();
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
        toggle(true);
    }

    public void toggle(boolean smoothAnimate) {
        if (!isAnimationRunning) {
            if (isExpanded)
                collapse(smoothAnimate);
            else
                expand(smoothAnimate);
        }
    }

    private void startArrowRotation(int animationType) {
        if (arrowBtn == null)
            return;
        RotateAnimation arrowAnimation = AnimationUtils.getInstance().getArrowAnimation(animationType, duration);
        arrowBtn.startAnimation(arrowAnimation);
    }

    private void expand(boolean smoothAnimate) {
        if (expandNotNecessary())
            return;
        contentLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        animateViews(contentLayout, headerLayout.getMeasuredHeight(), contentLayout.getMeasuredHeight()
                , EXPANDING, smoothAnimate);
    }

    /**
     * in case the default content was added and the text on
     **/
    private boolean expandNotNecessary() {
        return defaultContentTV != null && (defaultContentTV.getText() == null || defaultContentTV.getText().toString().isEmpty());
    }

    private void collapse(boolean smoothAnimate) {
        contentLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        animateViews(contentLayout, contentLayout.getMeasuredHeight(), contentLayout.getMeasuredHeight(),
                COLLAPSING, smoothAnimate);
    }

    private void animateViews(final View view, final int initialHeight, final int distance, final int animationType, boolean smooth) {
        isAnimationRunning = true;
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    isExpanded = animationType != COLLAPSING;
                    updateListener(view, animationType);
                    isAnimationRunning = false;
                }
                view.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(smooth ? duration : 0);

        startAnimation(animation);
        startArrowRotation(animationType);
    }

    private void updateListener(View view, int animationType) {
        if (listener != null) {
            if (animationType == EXPANDING) {
                listener.onExpandChanged(view, true);
            } else {//in case it is getting collapsed by attribute startExpanded then updating listener isn't required
                if (!startExpanded)
                    startExpanded = true;
                else
                    listener.onExpandChanged(view, false);
            }
        }
    }

    public void setHeaderLayout(int layoutRes) {
        headerLayoutRes = layoutRes;
        inflateHeader(context, layoutRes);
    }

    public void setContentLayout(int layoutRes) {
        contentLayoutRes = layoutRes;
        inflateContent(context, layoutRes);
    }

    public void setArrowDrawable(Drawable drawable) {
        if (drawable != null && headerLayout != null && headerLayout.findViewById(R.id.arrow) != null)
            ((ImageButton) headerLayout.findViewById(R.id.arrow)).setImageDrawable(drawable);
    }


    public void setDefaultHeaderTitle(String title, int headerTextColor) {
        setDefaultHeaderTitle(title);
        setDefaultHeaderTextColor(headerTextColor);
    }

    public void setDefaultHeaderTextColor(int headerTextColor) {
        if (defaultHeaderTV != null) {
            defaultHeaderTV.setTextColor(headerTextColor);
        }
    }

    public void setDefaultHeaderTitle(String title) {
        if (defaultHeaderTV != null) {
            defaultHeaderTV.setText(title);
        }
    }

    public void setDefaultContentTitle(String title, int contentTextColor) {
        setDefaultContentTitle(title);
        setDefaultContentTextColor(contentTextColor);
    }

    public void setDefaultContentTitle(String title) {
        if (defaultContentTV != null) {
            defaultContentTV.setText(title);
        }
    }

    public void setDefaultContentTextColor(int contentTextColor) {
        if (defaultContentTV != null) {
            defaultContentTV.setTextColor(contentTextColor);
        }
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        animation.setAnimationListener(animationListener);
    }

    public void setOnExpandedListener(ExpandableLayout.OnExpandedListener listener) {
        this.listener = listener;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public View getHeaderLayoutView() {
        return headerLayout;
    }

    public View getContentLayoutView() {
        return contentLayout;
    }

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }
}

