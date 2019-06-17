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
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import widget.com.expandablecardview.R;
import widget.com.expandablecardview.databinding.ExpandableLayoutBinding;

import static widget.com.expandablelayout.AnimationUtils.COLLAPSING;
import static widget.com.expandablelayout.AnimationUtils.EXPANDING;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
public class ExpandableLayout extends RelativeLayout {
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
    private ExpandableLayoutBinding binding;
    private boolean hideArrow;
    private String headerFontPath, contentFontPath;
    private ViewDataBinding customHeaderBinding, customContentBinding;

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
        headerFontPath = attributesArray.getString(R.styleable.ExpandableLayout_header_font);
        contentFontPath = attributesArray.getString(R.styleable.ExpandableLayout_content_font);
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
        binding.headerLayout.setHideArrow(hideArrow);
        binding.headerLayout.setCustomHeader(false);
        setArrowParams();
        setDrawableBackground(binding.headerLayout.arrow, arrowIconRes);
        binding.headerLayout.getRoot().setOnClickListener(this::onHeaderClicked);
        inflateInnerViews(context);
        if (startExpanded) startArrowRotation(EXPANDING, 0);
        else toggle(false);
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
        setParams(binding.headerLayout.arrow, width, height);

    }

    private void setParams(View view, float width, float height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (width != -1)
            params.width = Math.round(width);
        if (height != -1)
            params.height = Math.round(height);
        view.setLayoutParams(params);
    }

    private void inflateInnerViews(Context context) {
        if (headerLayoutRes == -1)
            setHeaderTitle(context);
        else inflateHeader(context, headerLayoutRes);
        if (contentLayoutRes == -1)
            setDefaultContent(context);
        else inflateContent(context, contentLayoutRes);

        if (attributesArray != null)
            attributesArray.recycle();
    }

    private void setHeaderTitle(Context context) {
        binding.setDefaultHeader(true);
        if (attributesArray == null)
            return;
        String headerTxt = attributesArray.getString(R.styleable.ExpandableLayout_header_title);
        int headerTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_header_color, Color.BLACK);
        setHeaderTitle(headerTxt, headerTextColor, header_text_size, headerTextStyle);
        if (headerPadding != -1)
            binding.headerLayout.getRoot().setPadding(headerPadding, headerPadding, headerPadding, headerPadding);
        if (headerFontPath != null)
            binding.headerLayout.setFontPath(headerFontPath);

    }

    private void setDefaultContent(Context context) {
        binding.setDefaultContent(true);
        if (attributesArray == null)
            return;
        String contentTxt = attributesArray.getString(R.styleable.ExpandableLayout_content_text);
        int contentTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_content_color, Color.BLACK);
        setDefaultContent(contentTxt, contentTextColor, contentTextStyle, content_text_size);
        if (contentPadding != -1)
            binding.contentLayout.setPadding(contentPadding, contentPadding, contentPadding, contentPadding);
        if (headerFontPath != null)
            binding.setFontPath(contentFontPath);
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
        binding.headerLayout.headerTV.setTypeface(Typeface.defaultFromStyle(typeface));
    }

    private void setContentTextStyle(int typeface) {
        binding.contentTV.setTypeface(Typeface.defaultFromStyle(typeface));
    }

    private void inflateHeader(Context context, int viewID) {
        binding.setDefaultHeader(false);
        binding.headerLayout.headerLayout.removeAllViews();
        customHeaderBinding = inflateView(context, viewID, binding.headerLayout.headerLayout, true);
        binding.headerLayout.setCustomHeader(true);
    }

    private void inflateContent(Context context, int viewID) {
        binding.setDefaultContent(false);
        binding.contentLayout.removeAllViews();
        customContentBinding = inflateView(context, viewID, binding.contentLayout, true);
    }

    private void onHeaderClicked(View v) {
        toggle(true);
    }

    public void toggle(boolean smoothAnimate) {
        if (isExpanded)
            collapse(smoothAnimate);
        else
            expand(smoothAnimate);
    }

    public void collapse(boolean smoothAnimate) {
        int contentMeasuredHeight = getMeasuredHeight(getContentView());
        animateViews(getContentView(), contentMeasuredHeight, contentMeasuredHeight,
                COLLAPSING, smoothAnimate);
    }

    public void expand(boolean smoothAnimate) {
        expand(getMeasuredHeight(getContentView()), smoothAnimate);
    }

    private void expand(int contentHeight, boolean smoothAnimate) {
        animateViews(getContentView(), 0, contentHeight
                , EXPANDING, smoothAnimate);
    }

    private void animateViews(final View view, final int initialHeight, final int distance, final int animationType, boolean smooth) {
        isExpanded = animationType == EXPANDING;
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    updateListener(view, animationType);
                }
                view.getLayoutParams().height =
                        animationType == EXPANDING ? Math.round(initialHeight + (distance * interpolatedTime))
                                : (int) (initialHeight - (distance * interpolatedTime));
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(smooth ? duration : 0);
        startAnimation(animation);
        startArrowRotation(animationType, duration);
    }

    public void refresh(boolean smoothAnimate) {
        if (isExpanded)
            expand(getContentView().getMeasuredHeight(), smoothAnimate);
        else collapse(smoothAnimate);
    }

    private View getContentView() {
        return binding.contentContainer;
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

    private void startArrowRotation(int animationType, Integer duration) {
        RotateAnimation arrowAnimation = AnimationUtils.getInstance()
                .getArrowAnimation(animationType, duration);
        binding.headerLayout.arrow.startAnimation(arrowAnimation);
    }

    public void refresh() {
        refresh(false);
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

    public void setHeaderTitle(int layoutRes) {
        headerLayoutRes = layoutRes;
        inflateHeader(context, layoutRes);
    }

    public void setContentLayout(int layoutRes) {
        contentLayoutRes = layoutRes;
        inflateContent(context, layoutRes);
    }

    public void setArrowDrawable(Drawable drawable) {
        if (drawable != null)
            binding.headerLayout.setDrawable(drawable);
    }

    private void setDefaultContent(String title, int textColor, int contentTextStyle, float content_text_size) {
        setDefaultContent(title, textColor);
        setContentTextStyle(contentTextStyle);
        setContentTextSize(content_text_size);
    }

    public void setHeaderTitle(String title, int headerTextColor) {
        setTitle(title);
        setHeaderLayoutTextColor(headerTextColor);
    }

    public void setTitle(String title) {
        binding.headerLayout.setTitle(title);
    }

    private void setHeaderTitle(String title, int headerTextColor, float header_text_size, int headerTextStyle) {
        setHeaderTitle(title, headerTextColor);
        setHeaderTextSize(header_text_size);
        setHeaderTextStyle(headerTextStyle);
    }

    public void setDefaultContent(String title, int textColor) {
        setContent(title);
        setDefaultContentTextColor(textColor);
    }

    public void setContent(String title) {
        binding.setContentText(title);
    }

    public void setDefaultContentTextColor(int contentTextColor) {
        binding.contentTV.setTextColor(contentTextColor);
    }

    public void setContentTextSize(float textSize) {
        if (textSize != -1)
            binding.contentTV.setTextSize(textSize);
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
            binding.headerLayout.headerTV.setTextSize(textSize);
    }

    public void setHeaderLayoutTextColor(int headerTextColor) {
        binding.headerLayout.headerTV.setTextColor(headerTextColor);
    }

    public View getHeaderLayoutView() {
        return binding.headerLayout.headerLayout;
    }

    public View getContentLayoutView() {
        return binding.contentLayout;
    }

    public ViewDataBinding getHeaderLayoutBinding() {
        return customHeaderBinding;
    }

    public ViewDataBinding getContentLayoutBinding() {
        return customContentBinding;
    }

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }
}

