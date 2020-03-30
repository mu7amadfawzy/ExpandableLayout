package widget.com.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import widget.com.expandablecardview.R;
import widget.com.expandablecardview.databinding.ExpandableLayoutBinding;

import static widget.com.expandablelayout.AnimationUtils.COLLAPSING;
import static widget.com.expandablelayout.AnimationUtils.EXPANDING;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
public class ExpandableLayout extends LinearLayout {
    private static int expandedPos = -1;
    private Animation animation;
    private ExpandableLayout.OnExpandedListener listener;
    private int duration = 300, itemPosition, headerLayoutRes = -1, contentLayoutRes = -1, headerTextStyle = Typeface.NORMAL, contentTextStyle = Typeface.NORMAL;
    private Drawable arrowIconDrawable;
    private TypedArray attributesArray;
    private boolean isExpanded, startExpanded, hideArrow, showContentFirstLine;
    private Context context;
    private float header_text_size, content_size, arrow_width, arrow_height, headerPadding, contentPadding, arrowMargin = -1;
    private ExpandableLayoutBinding binding;
    private String headerFontPath, contentFontPath;
    private ViewDataBinding customHeaderBinding, customContentBinding;
    private LinearLayoutManager linearLayoutManager;
    private View assArrowView;

    public ExpandableLayout(Context context) {
        super(context);
        init(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public static void onAttachedToRecycler() {
        expandedPos = -1;
    }

    private void init(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initViews(context);
        checkStart();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        attributesArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        headerLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_header_layout, -1);
        contentLayoutRes = attributesArray.getResourceId(R.styleable.ExpandableLayout_content_layout, -1);
        headerFontPath = attributesArray.getString(R.styleable.ExpandableLayout_header_font);
        contentFontPath = attributesArray.getString(R.styleable.ExpandableLayout_content_font);
        duration = attributesArray.getInt(R.styleable.ExpandableLayout_duration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        arrowIconDrawable = attributesArray.getDrawable(R.styleable.ExpandableLayout_arrow_icon);
        startExpanded = attributesArray.getBoolean(R.styleable.ExpandableLayout_startExpanded, false);
        header_text_size = attributesArray.getDimension(R.styleable.ExpandableLayout_header_text_size, -1);
        arrow_width = attributesArray.getDimension(R.styleable.ExpandableLayout_arrow_width, -1);
        arrow_height = attributesArray.getDimension(R.styleable.ExpandableLayout_arrow_height, -1);
        hideArrow = attributesArray.getBoolean(R.styleable.ExpandableLayout_hideArrow, false);
        content_size = attributesArray.getDimension(R.styleable.ExpandableLayout_content_size, -1);
        headerTextStyle = getTypeFace(attributesArray.getInt(R.styleable.ExpandableLayout_header_text_style, Typeface.NORMAL));
        contentTextStyle = getTypeFace(attributesArray.getInt(R.styleable.ExpandableLayout_content_style, Typeface.NORMAL));
        headerPadding = Math.round(attributesArray.getDimension(R.styleable.ExpandableLayout_header_padding, -1));
        contentPadding = Math.round(attributesArray.getDimension(R.styleable.ExpandableLayout_content_padding, -1));
        arrowMargin = Math.round(attributesArray.getDimension(R.styleable.ExpandableLayout_arrow_margin, -1));
        showContentFirstLine = attributesArray.getBoolean(R.styleable.ExpandableLayout_showContentFirstLine, false);
        if (contentLayoutRes != -1)
            showContentFirstLine = false;//showContentFirstLine is only in case default content
    }

    private void initViews(Context context) {
        binding = inflateView(context, R.layout.expandable_layout, this);
        binding.headerLayout.setHideArrow(hideArrow);
        setArrowParams();
        binding.headerLayout.setDrawable(arrowIconDrawable);
        binding.headerLayout.getRoot().setOnClickListener(this::onHeaderClicked);
        inflateInnerViews(context);
    }

    private void checkStart() {
        if (startExpanded) {
            expand(false);
            return;
        }
        if (isDefaultContent() && showContentFirstLine)
            showContentFirstLine();
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
        inflateHeader(context);
        inflateContent(context);
        clearAttributes();
    }

    private void clearAttributes() {
        if (attributesArray != null)
            attributesArray.recycle();
    }

    private void inflateContent(Context context) {
        if (isDefaultContent())
            setDefaultContent();
        else inflateContent(context, contentLayoutRes);
    }

    private void inflateHeader(Context context) {
        if (isDefaultHeader())
            setHeaderTitle();
        else inflateHeader(context, headerLayoutRes);
    }

    private void setHeaderTitle() {
        binding.setDefaultHeader(true);
        if (attributesArray == null)
            return;
        String headerTxt = attributesArray.getString(R.styleable.ExpandableLayout_exp_title);
        int headerTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_header_color, Color.BLACK);
        setHeaderTitle(headerTxt, headerTextColor, header_text_size, headerTextStyle);
        if (headerPadding != -1)
            setPadding(binding.headerLayout.getRoot(), headerPadding, headerPadding, headerPadding, headerPadding);
        if (headerFontPath != null)
            binding.headerLayout.setFontPath(headerFontPath);
        setArrowMargin();

    }

    private void setDefaultContent() {
        binding.setDefaultContent(true);
        if (attributesArray == null)
            return;
        String contentTxt = attributesArray.getString(R.styleable.ExpandableLayout_exp_content);
        int contentTextColor = attributesArray.getColor(R.styleable.ExpandableLayout_content_color, Color.BLACK);
        setDefaultContent(contentTxt, contentTextColor, contentTextStyle, content_size);
        if (contentPadding != -1)
            setPadding(getContentView(), contentPadding, contentPadding, contentPadding, contentPadding);
        if (headerFontPath != null)
            binding.setFontPath(contentFontPath);
    }

    private void setPadding(View view, float left, float top, float right, float bottom) {
        view.setPadding(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    private void setArrowMargin() {
        if (arrowMargin == -1) return;
        MarginLayoutParams params = (MarginLayoutParams) binding.headerLayout.arrow.getLayoutParams();
        int marginInt = Math.round(arrowMargin);
        params.setMargins(marginInt, marginInt, marginInt, marginInt);
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
//        binding.headerLayout.headerLayout.removeAllViews();
        customHeaderBinding = inflateView(context, viewID, binding.headerLayout.headerLayout);
    }

    private void inflateContent(Context context, int viewID) {
        binding.setDefaultContent(false);
//        binding.contentLayout.removeAllViews();
        customContentBinding = inflateView(context, viewID, binding.contentLayout);
    }

    private void onHeaderClicked(View v) {
        toggle(true);
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean smoothAnimate) {
        if (isExpanded)//measuring content height again and expanding to it
            handleMotion(getContentView(), getContentMeasuredHeight(), measureContentHeight(), EXPANDING, smoothAnimate);
        else collapse(smoothAnimate);
    }


    public void checkItemState(int itemPosition) {
        if (itemPosition == expandedPos && !isExpanded)
            expand(false);
        else if (itemPosition != expandedPos && isExpanded)
            collapse(false);
    }

    public void toggle(boolean smoothAnimate) {
        if (isExpanded)
            collapse(smoothAnimate);
        else
            expand(smoothAnimate);
    }

    public void collapse(boolean smoothAnimate) {
        int contentHeight = getContentMeasuredHeight();
        handleMotion(getContentView(), contentHeight, contentHeight, COLLAPSING, smoothAnimate);
    }

    public void expand(boolean smoothAnimate) {
        handleMotion(getContentView(), showContentFirstLine ? measureContentHeight() : 0, measureContentHeight(), EXPANDING, smoothAnimate);
    }

    private void handleMotion(final View view, final int initialHeight, final int distance, final int animationType, boolean smooth) {
        isExpanded = animationType == EXPANDING;
        checkRecyclerCase();
        if (isDefaultContent()) animateDefaultContentLines(animationType, initialHeight, smooth);
        else animateCustomContent(view, initialHeight, distance, animationType, smooth);
        if (!hideArrow) animateArrow(animationType, duration);
    }

    private void animateDefaultContentLines(int animationType, int initialHeight, boolean smooth) {
        AnimationUtils.getInstance().animateTextViewMaxLinesChange(binding.contentTV, initialHeight
                , animationType == EXPANDING ? Integer.MAX_VALUE
                        : (showContentFirstLine ? 1 : 0), (smooth ? duration : 0)
                , animationType == EXPANDING, listener);
    }

    private void showContentFirstLine() {
        binding.contentTV.setMaxLines(1);
        binding.contentTV.setVisibility(VISIBLE);
    }

    private void animateCustomContent(View view, int initialHeight, int distance, int animationType, boolean smooth) {
        initialHeight = animationType == EXPANDING ? 0 : initialHeight;
        int targetHeight = animationType == EXPANDING ? (initialHeight + distance) : 0;
        AnimationUtils.getInstance().animateViewHeight(view, initialHeight
                , targetHeight, smooth ? duration : 0, animationType == EXPANDING, listener);
    }

    private void setViewHeight(View view, float height) {
        view.getLayoutParams().height = Math.round(height);
        view.requestLayout();
    }

    private int measureContentHeight() {
        return getMeasuredHeight(getContentView());
    }

    private int getContentMeasuredHeight() {
        return getContentView().getMeasuredHeight();
    }

    private int getMeasuredHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    /**
     * used for recyclerView of expandable items
     * to handle one-expanded per time case by passing by all visible items in layoutManger and collapse it
     **/
    private void checkRecyclerCase() {
        if (linearLayoutManager == null || !isExpanded)
            return;
        boolean atLeastOneCollapsed = false;
        expandedPos = itemPosition;
        for (int i = linearLayoutManager.findFirstVisibleItemPosition(); i < linearLayoutManager.findLastVisibleItemPosition(); i++) {
            ExpandableLayout expandableLayout = linearLayoutManager.findViewByPosition(i).findViewById(getId());
            if (expandableLayout != this && expandableLayout.isExpanded()) {
                atLeastOneCollapsed = true;
                expandableLayout.collapse(false);
            }
        }
        if (!atLeastOneCollapsed)
            expandedPos = -1;
    }

    private void updateListener(View view, int animationType) {
        if (listener != null) {
            listener.onExpandChanged(view, animationType == EXPANDING);
        }
    }

    private void animateArrow(int animationType, Integer duration) {
        AnimationUtils.getInstance().rotateAnimation(getArrowViewToAnimate(), animationType == EXPANDING, duration);
    }

    private View getArrowViewToAnimate() {
        return assArrowView == null ? binding.headerLayout.arrow : assArrowView;
    }

    public ExpandableLayout setAsArrow(View view) {
        assArrowView = view;
        return this;
    }

    private View getContentView() {
        return isDefaultContent() ? binding.contentTV : binding.contentLayout;
    }

    private <T extends ViewDataBinding> T inflateView(Context context, int viewID, ViewGroup root) {
        return DataBindingUtil.inflate(LayoutInflater.from(context),
                viewID, root, true);
    }

    private void setDrawableBackground(ImageView imageView, Drawable drawable) {
        if (drawable != null && imageView != null) {

        }
    }

    public ExpandableLayout setHeaderTitle(int layoutRes) {
        headerLayoutRes = layoutRes;
        inflateHeader(context, layoutRes);
        return this;
    }

    public ExpandableLayout setContentLayout(int layoutRes) {
        contentLayoutRes = layoutRes;
        inflateContent(context, layoutRes);
        return this;
    }

    public ExpandableLayout setArrowDrawable(Drawable drawable) {
        if (drawable != null)
            binding.headerLayout.setDrawable(drawable);
        return this;
    }

    private void setDefaultContent(String title, int textColor, int contentTextStyle, float content_size) {
        setDefaultContent(title, textColor);
        setContentTextStyle(contentTextStyle);
        setContentTextSize(content_size);
    }

    public ExpandableLayout setHeaderTitle(String title, int headerTextColor) {
        setTitle(title);
        setHeaderLayoutTextColor(headerTextColor);
        return this;
    }

    public ExpandableLayout setTitle(String title) {
        binding.headerLayout.setTitle(title);
        return this;
    }

    private void setHeaderTitle(String title, int headerTextColor, float header_text_size, int headerTextStyle) {
        setHeaderTitle(title, headerTextColor);
        setHeaderTextStyle(headerTextStyle);
        setHeaderTextSize(header_text_size);
    }

    public ExpandableLayout setDefaultContent(String title, int textColor) {
        setContent(title);
        setDefaultContentTextColor(textColor);
        return this;
    }

    public ExpandableLayout setContent(String title) {
        binding.setContentText(title);
        binding.contentTV.setText(title);
        return this;
    }

    public ExpandableLayout setDefaultContentTextColor(int contentTextColor) {
        binding.contentTV.setTextColor(contentTextColor);
        return this;
    }

    public ExpandableLayout setContentTextSize(float textSize) {
        if (textSize != -1)
            setTextSize(textSize, binding.contentTV);
        return this;
    }

    public void setTextSize(float textSize, TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * used for recyclerView of expandable items
     * to handle one-expanded per time case
     **/
    public void setRecyclerItem(LinearLayoutManager linearLayoutManager, int itemPosition) {
        this.linearLayoutManager = linearLayoutManager;
        this.itemPosition = itemPosition;
        checkItemState(itemPosition);
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
            setTextSize(textSize, binding.headerLayout.headerTV);
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

    public void onDetachedFromRecyclerView() {
        expandedPos = -1;
    }

    private boolean isDefaultContent() {
        return contentLayoutRes == -1;
    }

    private boolean isDefaultHeader() {
        return headerLayoutRes == -1;
    }

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }

}

