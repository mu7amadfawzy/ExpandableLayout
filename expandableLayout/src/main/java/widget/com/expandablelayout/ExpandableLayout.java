package widget.com.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import widget.com.expandablecardview.R;

import static widget.com.expandablelayout.AnimationUtils.COLLAPSING;
import static widget.com.expandablelayout.AnimationUtils.EXPANDING;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */
public class ExpandableLayout extends LinearLayout {
    public static final int DEFAULT_ANIM_DURATION = 350;
    private String title;
    private View innerView;
    private ViewGroup containerView;
    private ImageButton arrowBtn;
    private ImageButton headerIcon;
    private TextView textViewTitle;
    private TypedArray typedArray;
    private int innerViewRes;
    private Drawable iconDrawable;
    private Drawable arrowDrawable;
    private int titleColor;
    private CardView card;
    private long animDuration = DEFAULT_ANIM_DURATION;
    private boolean isExpanded = false;
    private boolean isExpanding = false;
    private boolean isCollapsing = false;
    private boolean expandOnClick = false;
    private boolean startExpanded = false;

    private int previousHeight = 0;

    private OnExpandedListener listener;

    private OnClickListener defaultClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isExpanded()) collapse();
            else expand();
        }
    };

    public ExpandableLayout(Context context) {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttributes(context, attrs);
        initView(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttributes(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        //Inflating View
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_cardview, this);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        title = typedArray.getString(R.styleable.ExpandableLayout_title);
        iconDrawable = typedArray.getDrawable(R.styleable.ExpandableLayout_icon);
        arrowDrawable = typedArray.getDrawable(R.styleable.ExpandableLayout_arrow_icon);
        titleColor = typedArray.getColor(R.styleable.ExpandableLayout_title_color, Color.BLACK);
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableLayout_inner_view, View.NO_ID);
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableLayout_expandOnClick, false);
        animDuration = typedArray.getInteger(R.styleable.ExpandableLayout_animationDuration, DEFAULT_ANIM_DURATION);
        startExpanded = typedArray.getBoolean(R.styleable.ExpandableLayout_startExpanded, false);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        arrowBtn = findViewById(R.id.arrow);
        textViewTitle = findViewById(R.id.title);
        headerIcon = findViewById(R.id.icon);
        initViews();
        //Setting attributes
        applyAttributes();
    }

    private void initViews() {
        card = findViewById(R.id.card);
        containerView = findViewById(R.id.viewContainer);
    }

    private void applyAttributes() {
        if (!TextUtils.isEmpty(title)) textViewTitle.setText(title);
        setInnerView(innerViewRes);
        setDrawableBackground(headerIcon, iconDrawable);
        setDrawableBackground(arrowBtn, arrowDrawable);
        textViewTitle.setTextColor(titleColor);
        setElevation(Utils.convertDpToPixels(getContext(), 4));
        checkExpandState();
    }

    private void checkExpandState() {
        if (startExpanded) {
            setAnimDuration(0);
            expand();
            setAnimDuration(animDuration);
        }
        if (expandOnClick) {
            card.setOnClickListener(defaultClickListener);
            arrowBtn.setOnClickListener(defaultClickListener);
        }
    }

    private void setDrawableBackground(ImageButton imageButton, Drawable drawable) {
        if (drawable != null) {
            imageButton.setVisibility(VISIBLE);
            imageButton.setBackground(drawable);
        }
    }

    public void expand() {
        final int initialHeight = card.getHeight();

        if (!isMoving()) {
            previousHeight = initialHeight;
        }
        card.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = card.getMeasuredHeight();

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING);
        }
    }

    public void collapse() {
        int initialHeight = card.getMeasuredHeight();

        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING);
        }

    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void animateViews(final int initialHeight, final int distance, final int animationType) {

        Animation expandAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    //Setting isExpanding/isCollapsing to false
                    isExpanding = false;
                    isCollapsing = false;

                    if (listener != null) {
                        if (animationType == EXPANDING) {
                            listener.onExpandChanged(card, true);
                        } else {
                            listener.onExpandChanged(card, false);
                        }
                    }
                }

                card.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));
                card.findViewById(R.id.viewContainer).requestLayout();

                containerView.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        RotateAnimation arrowAnimation = AnimationUtils.getInstance().getArrowAnimation(initialHeight,
                distance, animationType, animDuration);

        expandAnimation.setDuration(animDuration);

        isExpanding = animationType == EXPANDING;
        isCollapsing = animationType == COLLAPSING;

        startAnimation(expandAnimation);
        Log.d("epxandedCardView", "Started animation: " + (animationType == EXPANDING ? "Expanding" : "Collapsing"));
        arrowBtn.startAnimation(arrowAnimation);
        isExpanded = animationType == EXPANDING;

    }

    private boolean isExpanding() {
        return isExpanding;
    }

    private boolean isCollapsing() {
        return isCollapsing;
    }

    private boolean isMoving() {
        return isExpanding() || isCollapsing();
    }

    public void setOnExpandedListener(OnExpandedListener listener) {
        this.listener = listener;
    }

    public void removeOnExpandedListener() {
        this.listener = null;
    }

    public void setTitle(String title) {
        if (textViewTitle != null) textViewTitle.setText(title);
    }

    public void setTitle(int resId) {
        if (textViewTitle != null) textViewTitle.setText(resId);
    }

    public void setIcon(Drawable drawable) {
        if (headerIcon != null) {
            headerIcon.setBackground(drawable);
            iconDrawable = drawable;
        }
    }

    public void setArrowIcon(Drawable drawable) {
        if (arrowBtn != null) {
            arrowBtn.setBackground(drawable);
            arrowDrawable = drawable;
        }
    }

    public void setArrowIcon(int resId) {
        if (arrowBtn != null) {
            arrowDrawable = ContextCompat.getDrawable(getContext(), resId);
            arrowBtn.setBackground(iconDrawable);
        }
    }

    public void setIcon(int resId) {
        if (headerIcon != null) {
            iconDrawable = ContextCompat.getDrawable(getContext(), resId);
            headerIcon.setBackground(iconDrawable);
        }
    }

    public View getInnerView() {
        return innerView;
    }

    public void setInnerView(int resId) {
        ViewStub stub = findViewById(R.id.viewStub);
        stub.setLayoutResource(resId);
        innerView = stub.inflate();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (arrowBtn != null) arrowBtn.setOnClickListener(l);
        super.setOnClickListener(l);
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
    }

    /**
     * Interfaces
     */

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }

}
