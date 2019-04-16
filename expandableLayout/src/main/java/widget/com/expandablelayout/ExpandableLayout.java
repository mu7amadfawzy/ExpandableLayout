package widget.com.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import widget.com.expandablecardview.R;
import widget.com.expandablecardview.databinding.DefaultContentBinding;
import widget.com.expandablecardview.databinding.ExpandableLayoutBinding;
import widget.com.expandablecardview.databinding.HeaderLayoutBinding;

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
    private Animation animation;
    private ExpandableLayout.OnExpandedListener listener;
    private int headerLayoutRes = -1, contentLayoutRes = -1, headerTextStyle = Typeface.NORMAL, contentTextStyle = Typeface.NORMAL;
    private int headerPadding = -1, contentPadding = -1;
    private Drawable arrowIconRes;
    private TypedArray attributesArray;
    private boolean startExpanded;
    private Context context;
    private float header_text_size, content_text_size, arrow_width, arrow_height;
    private int contentMeasuredHeight;
    private HeaderLayoutBinding headerLayoutBinding;
    private ExpandableLayoutBinding binding;
    private boolean hideArrow;
    private DefaultContentBinding defaultContentBinding;

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

    @BindingAdapter({"bind:src"})
    public static void setImageBackground(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("bind:visibility")
    public static void visibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        attributesArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        headerLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_header_layout, -1);
        contentLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_content_layout, -1);
        duration = attributesArray.getInt(R.styleable.ExpandableLayout_duration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        arrowIconRes = attributesArray.getDrawable(R.styleable.ExpandableLayout_arrow_icon);
        startExpanded = attributesArray.getBoolean(R.styleable.ExpandableLayout_startExpanded, false);
        header_text_size = attributesArray.getDimension(R.styleable.ExpandableLayout_header_text_size, -1);
        arrow_width = attributesArray.getDimension(R.styleable.ExpandableLayout_arrow_width, -1);
        arrow_height = attributesArray.getDimension(R.styleable.ExpandableLayout_arrow_height, -1);
        hideArrow = attributesArray.getBoolean(R.styleable.ExpandableLayout_hideArrow, false);
        content_text_size = attributesArray.getInt(R.styleable.ExpandableLayout_content_text_size, -1);
        headerTextStyle = getTypeFace(attributesArray.getInt(R.styleable.ExpandableLayout_header_text_style, Typeface.NORMAL));
        contentTextStyle = getTypeFace(attributesArray.getInt(R.styleable.ExpandableLayout_content_text_style, Typeface.NORMAL));
        headerPadding = Math.round(attributesArray.getDimension(R.styleable.ExpandableLayout_content_padding, -1));
        contentPadding = Math.round(attributesArray.getDimension(R.styleable.ExpandableLayout_content_padding, -1));
    }

    private void initViews(final Context context) {
        binding = (ExpandableLayoutBinding) inflateView(context, R.layout.expandable_layout, this, true);
        headerLayoutBinding = (HeaderLayoutBinding) inflateView(context, R.layout.header_layout, binding.headerLayout, true);
        headerLayoutBinding.setHideArrow(hideArrow);
        headerLayoutBinding.setCustomHeader(false);
        setArrowParams();
        setDrawableBackground(headerLayoutBinding.arrow, arrowIconRes);
        binding.headerLayout.setOnClickListener(this::onLayoutClicked);
        inflateInnerViews(context);
        if (!startExpanded)
            toggle(false);
    }

    public void setArrowWidthHeight(float arrow_width, float arrow_height) {
        this.arrow_width = arrow_width;
        this.arrow_height = arrow_height;
        setArrowParams();
    }

    private int getMeasuredHeight(View view) {
        view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return view.getMeasuredHeight();
    }

    private void setArrowParams() {
        if (hideArrow)
            return;
        float width = -1, height = -1;
        if (arrow_width != -1)
            width = arrow_width;
        if (arrow_height != -1)
            height = arrow_height;
        if (width == -1 && height == -1)
            return;
        setParams(headerLayoutBinding.arrow, width, height);

    }

    private void setParams(View view, float width, float height) {
        RelativeLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (width != -1)
            params.width = Math.round(width);
        if (height != -1)
            params.height = Math.round(height);
        view.setLayoutParams(params);
    }

    private void inflateInnerViews(Context context) {
        if (headerLayoutRes == -1)
            setDefaultHeader(context);
        else inflateHeader(context, headerLayoutRes);
        if (contentLayoutRes == -1)
            inflateDefaultContent(context);
        else inflateContent(context, contentLayoutRes);

        if (attributesArray != null)
            attributesArray.recycle();
    }

    private void setDefaultHeader(Context context) {
        if (attributesArray == null)
            return;
        String headerTxt = attributesArray.getString(R.styleable.ExpandableLayout_header_title);
        int headerTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_header_color, Color.BLACK);
        setDefaultHeader(headerTxt, headerTextColor, header_text_size, headerTextStyle);
        Drawable headerIcon = attributesArray.getDrawable(R.styleable.ExpandableLayout_header_icon);
        setArrowDrawable(headerIcon);
        if (headerPadding != -1)
            binding.headerLayout.setPadding(headerPadding, headerPadding, headerPadding, headerPadding);
    }

    private void inflateDefaultContent(Context context) {
        defaultContentBinding = (DefaultContentBinding) inflateView(context, R.layout.default_content, binding.contentLayout, true);
        if (attributesArray == null)
            return;
        String contentTxt = attributesArray.getString(R.styleable.ExpandableLayout_content_text);
        int contentTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_content_color, Color.BLACK);
        setDefaultContent(contentTxt, contentTextColor, contentTextStyle, content_text_size);
        if (contentPadding != -1)
            binding.headerLayout.setPadding(contentPadding, contentPadding, contentPadding, contentPadding);
        contentMeasuredHeight = getMeasuredHeight(binding.contentLayout);
    }

    private int getTypeFace(int typeface) {
        switch (typeface) {
            case 0:
                return Typeface.NORMAL;
            case 1:
                return Typeface.BOLD;
            case 2:
                return Typeface.ITALIC;
        }
        return typeface;
    }

    public void setHeaderTextStyle(int typeface) {
        headerLayoutBinding.headerTV.setTypeface(Typeface.defaultFromStyle(typeface));
    }

    private void setContentTextStyle(int typeface) {
        defaultContentBinding.contentTV.setTypeface(Typeface.defaultFromStyle(typeface));
    }

    private void inflateHeader(Context context, int viewID) {
        headerLayoutBinding.headerLayout.removeAllViews();
        inflateView(context, viewID, headerLayoutBinding.headerLayout, true);
        headerLayoutBinding.setCustomHeader(true);
    }

    private void inflateContent(Context context, int viewID) {
        binding.contentLayout.removeAllViews();
        inflateView(context, viewID, binding.contentLayout, true);
        contentMeasuredHeight = getMeasuredHeight(binding.contentLayout);
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

    private ViewDataBinding inflateView(Context context, int viewID, ViewGroup root, boolean attachToRoot) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                viewID, root, attachToRoot);
        return binding;
    }

    private void setDrawableBackground(ImageButton imageButton, Drawable drawable) {
        if (drawable != null && imageButton != null) {
            imageButton.setVisibility(VISIBLE);
            imageButton.setBackground(drawable);
        }
    }

    private void startArrowRotation(int animationType) {
        RotateAnimation arrowAnimation = AnimationUtils.getInstance()
                .getArrowAnimation(animationType, duration);
        headerLayoutBinding.arrow.startAnimation(arrowAnimation);
    }

    private void expand(boolean smoothAnimate) {
//        if (expandNotNecessary())
//            return;
        animateViews(binding.contentLayout, 0, contentMeasuredHeight
                , EXPANDING, smoothAnimate);
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

    private void collapse(boolean smoothAnimate) {
        animateViews(binding.contentLayout, contentMeasuredHeight, contentMeasuredHeight,
                COLLAPSING, smoothAnimate);
    }

    /**
     * in case the default content was added and the text on
     **/
//    private boolean expandNotNecessary() {
//        return defaultContentTV != null && (defaultContentTV.getText() == null || defaultContentTV.getText().toString().isEmpty());
//    }
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

    public void setArrowDrawable(Drawable drawable) {
        if (drawable != null)
            headerLayoutBinding.setDrawable(drawable);
    }

    private void setDefaultContent(String title, int textColor, int contentTextStyle, float content_text_size) {
        setDefaultContent(title, textColor);
        setContentTextStyle(contentTextStyle);
        setContentTextSize(content_text_size);
    }

    public void setDefaultHeader(String title, int headerTextColor) {
        headerLayoutBinding.setTitle(title);
        setDefaultHeaderTextColor(headerTextColor);
    }

    private void setDefaultHeader(String title, int headerTextColor, float header_text_size, int headerTextStyle) {
        setDefaultHeader(title, headerTextColor);
        setHeaderTextSize(header_text_size);
        setHeaderTextStyle(headerTextStyle);
    }

    public void setDefaultContent(String title, int textColor) {
        defaultContentBinding.setTitle(title);
        setDefaultContentTextColor(textColor);
        contentMeasuredHeight = getMeasuredHeight(binding.contentLayout);
    }

    public void setDefaultContentTextColor(int contentTextColor) {
        defaultContentBinding.contentTV.setTextColor(contentTextColor);
    }

    public void setContentTextSize(float textSize) {
        if (textSize != -1)
            defaultContentBinding.contentTV.setTextSize(textSize);
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

    public void setHeaderTextSize(float textSize) {
        if (textSize != -1)
            headerLayoutBinding.headerTV.setTextSize(textSize);
    }

    public void setDefaultHeaderTextColor(int headerTextColor) {
        headerLayoutBinding.headerTV.setTextColor(headerTextColor);
    }

    public View getHeaderLayoutView() {
        return binding.headerLayout;
    }

    public View getContentLayoutView() {
        return binding.contentLayout;
    }

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }
}

