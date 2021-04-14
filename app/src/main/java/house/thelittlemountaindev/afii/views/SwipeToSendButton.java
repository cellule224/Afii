package house.thelittlemountaindev.afii.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import house.thelittlemountaindev.afii.R;

public class SwipeToSendButton extends RelativeLayout {

    private static final String TAG = "SwipeButton";
    private static final boolean RIGHT = true;
    private static final boolean LEFT = false;
    SwipeAnimationListener mSwipeAnimationListener;
    RelativeLayout mBackground;
    private ImageView slidingButton;
    private float initialX;
    private boolean active;
    private int initialButtonWidth;

    private Drawable defaultDrawable;
    private Drawable defaultBackground;

    private Drawable rightSwipeDrawable;
    private Drawable rightSwipeBackground;

    private Drawable leftSwipeDrawable;
    private Drawable leftSwipeBackground;

    private long mDuration;


    public SwipeToSendButton(Context context) {
        super(context);
        init(context, null, -1, -1);
    }

    public SwipeToSendButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1, -1);
    }

    public SwipeToSendButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, -1);
    }

    public void setOnSwipeAnimationListener(SwipeAnimationListener swipeAnimationListener) {
        this.mSwipeAnimationListener = swipeAnimationListener;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeToSendButton);

        //defaultBackground = ContextCompat.getDrawable(context, ta.getInteger(R.styleable.SwipeToSendButton_defaultBackground, R.drawable.ic_arrow_up_black_24dp));
        defaultDrawable = ContextCompat.getDrawable(context, ta.getInteger(R.styleable.SwipeToSendButton_defaultDrawable, R.drawable.ic_right_and_left_arrows));

        mBackground = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mBackground.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_swipe_btn)); //R.drawable.shape_rounded
        mBackground.setPadding(32, 32, 32, 32);
        addView(mBackground, layoutParamsView);

        //Add the left text
        final TextView leftText = new TextView(context);
        leftText.setGravity(Gravity.CENTER);
        leftText.setText("RECEVOIR");
        leftText.setTextColor(Color.WHITE);
        leftText.setPadding(16,0,0,0);
        LayoutParams leftlayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        leftlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        mBackground.addView(leftText, leftlayoutParams);

        //Add the right text
        final TextView rightText = new TextView(context);
        rightText.setGravity(Gravity.CENTER);
        rightText.setText("ENVOYER");
        rightText.setTextColor(Color.WHITE);
        rightText.setPadding(0,0,16,0);
        LayoutParams rightlayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        rightlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        mBackground.addView(rightText, rightlayoutParams);




        final ImageView swipeButton = new ImageView(context);
        this.slidingButton = swipeButton;

        rightSwipeDrawable = ContextCompat.getDrawable(getContext(), ta.getInteger(R.styleable.SwipeToSendButton_rightSwipeDrawable, R.drawable.ic_arrow_forward_black_24dp));
        rightSwipeBackground = ContextCompat.getDrawable(context, ta.getInteger(R.styleable.SwipeToSendButton_rightSwipeBackground, R.drawable.bg_swipe_btn)); //R.drawable.shape_button
        leftSwipeDrawable = ContextCompat.getDrawable(getContext(), ta.getInteger(R.styleable.SwipeToSendButton_leftSwipeDrawable, R.drawable.ic_arrow_back_black_24dp));
        leftSwipeBackground = ContextCompat.getDrawable(getContext(), ta.getInteger(R.styleable.SwipeToSendButton_leftSwipeBackground, R.drawable.bg_swipe_btn));

        mDuration = ta.getInteger(R.styleable.SwipeToSendButton_duration, 200);
        slidingButton.setImageDrawable(defaultDrawable);
        slidingButton.setPadding(32, 32, 32, 32);

        LayoutParams layoutParamsButton = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsButton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        swipeButton.setImageDrawable(defaultDrawable);
        swipeButton.setBackground(defaultBackground);

        addView(swipeButton, layoutParamsButton);
        setOnTouchListener(getButtonTouchListener());
    }

    private OnTouchListener getButtonTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (initialX == 0) {
                            initialX = slidingButton.getX();
                        }
                        if (event.getX() > initialX + slidingButton.getWidth() / 2 &&
                                event.getX() + slidingButton.getWidth() / 2 < getWidth()) {
                            slidingButton.setX(event.getX() - slidingButton.getWidth() / 2);
                            Log.d(TAG, "sliding Right");
                        }

                        if (event.getX() < initialX + slidingButton.getWidth() / 2 &&
                                event.getX() + slidingButton.getWidth() / 2 < getWidth()) {
                            slidingButton.setX(event.getX() - slidingButton.getWidth() / 2);
                            Log.d(TAG, "sliding left");
                        }

                        if (event.getX() + slidingButton.getWidth() / 2 > getWidth() &&
                                slidingButton.getX() + slidingButton.getWidth() / 2 < getWidth() + 100) {
                            Log.d(TAG, "stop at right");
                            slidingButton.setX(getWidth() - slidingButton.getWidth());
                        }


                        if (event.getX() + slidingButton.getWidth() / 2 < getWidth() && slidingButton.getX() < 4) {
                            slidingButton.setX(0);
                        }

                        if (event.getX() < slidingButton.getWidth() / 2 &&
                                slidingButton.getX() > 0) {
                            slidingButton.setX(initialX);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (active) {
                            collapseButton();
                        } else {
                            initialButtonWidth = slidingButton.getWidth();

                            if (slidingButton.getX() + slidingButton.getWidth() > getWidth() * 0.75) {
                                expandButton(RIGHT);
                            } else if (slidingButton.getX() - slidingButton.getWidth() < getWidth() * 0.25) {
                                expandButton(LEFT);
                            } else {
                                moveToCenter();
                            }
                        }

                        return true;

                }

                return false;
            }
        };

    }

    private void expandButton(final boolean isUp) {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(slidingButton.getX(), 0);
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                slidingButton.setX(x);
            }
        });


        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                slidingButton.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = slidingButton.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                slidingButton.setLayoutParams(params);
                if (isUp) {
                    slidingButton.setImageDrawable(rightSwipeDrawable);
                    slidingButton.setBackground(rightSwipeBackground);
                } else {
                    slidingButton.setImageDrawable(leftSwipeDrawable);
                    slidingButton.setBackground(leftSwipeBackground);
                }
                slidingButton.setPadding(32, 32, 32, 32);
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = true;
            }
        });

        animatorSet.playTogether(positionAnimator, widthAnimator);
        animatorSet.start();
        mSwipeAnimationListener.onSwiped(isUp);
    }

    private void collapseButton() {
        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                slidingButton.getWidth(),
                initialButtonWidth);

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = slidingButton.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                slidingButton.setLayoutParams(params);
            }
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = false;
                slidingButton.setPadding(32, 32, 32, 32);

                slidingButton.setImageDrawable(defaultDrawable);
                slidingButton.setBackground(defaultBackground);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(widthAnimator);
        animatorSet.start();
    }

    private void moveToCenter() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(slidingButton.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                slidingButton.setX(initialX);
            }
        });
        positionAnimator.setDuration(mDuration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(positionAnimator);
        animatorSet.start();
    }


}