package com.github.florent37.materialtextfield;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class MaterialTextField extends FrameLayout {
    protected InputMethodManager inputMethodManager;

    protected TextView label;
    protected View card;
    protected ImageView image;
    protected ImageView imageButton;
    protected EditText editText;
    protected ViewGroup editTextLayout;

    protected int labelTopMargin = -1;
    protected boolean expanded = false;

    protected int ANIMATION_DURATION = -1;
    protected boolean OPEN_KEYBOARD_ON_FOCUS = true;
    protected int labelColor = -1;
    protected int imageDrawableId = -1;
    protected int imageButtonDrawableId = -1;
    protected int cardCollapsedHeight = -1;
    protected boolean hasFocus = false;
    protected int backgroundColor = -1;

    protected boolean notCollapsible = false;
    protected boolean showNotCollapsibleKeyboard = false;
    protected int showNotCollapsibleKeyboardDelay = 100;


    protected float reducedScale = 0.2f;

    public MaterialTextField(Context context) {
        super(context);
        init();
    }

    public MaterialTextField(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
        init();
    }

    public MaterialTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context, attrs);
        init();
    }

    protected void init() {
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void toggle() {
        if (expanded) {
            reduce();
        } else {
            expand();
        }
    }

    public void reduce() {
        if (expanded) {
            final int heightInitial = getContext().getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_final);

            ViewCompat.animate(label)
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .translationY(0)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(image)
                .alpha(0)
                .scaleX(0.4f)
                .scaleY(0.4f)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(imageButton)
                    .alpha(0)
                    .scaleX(0.4f)
                    .scaleY(0.4f)
                    .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(editText)
                .alpha(1f)
                .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(View view) {
                        float value = ViewCompat.getAlpha(view); //percentage
                        card.getLayoutParams().height = (int) (value * (heightInitial - cardCollapsedHeight) + cardCollapsedHeight);
                        card.requestLayout();
                    }
                })
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        if (expanded) {
                            editText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (!expanded) {
                            editText.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) { }
                });

            ViewCompat.animate(card)
                .scaleY(reducedScale)
                .setDuration(ANIMATION_DURATION);

            if (editText.hasFocus()) {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
            }

            expanded = false;
        }
    }

    public void expand() {
        if (!expanded) {
            ViewCompat.animate(editText)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(card)
                .scaleY(1f)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(label)
                .alpha(0.4f)
                .scaleX(0.7f)
                .scaleY(0.7f)
                .translationY(-labelTopMargin)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(image)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(imageButton)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(ANIMATION_DURATION);

            if (editText != null) {
                editText.requestFocus();
            }

            if (OPEN_KEYBOARD_ON_FOCUS) {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }

            expanded = true;
        }
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public View getCard() {
        return card;
    }

    public TextView getLabel() {
        return label;
    }

    public ImageView getImage() {
        return image;
    }

    public ImageView getImageButton() {
        return imageButton;
    }

    public EditText getEditText() {
        return editText;
    }

    public ViewGroup getEditTextLayout() {
        return editTextLayout;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public boolean isCollapsible() {
        return !notCollapsible;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;

        if (hasFocus) {
            expand();
            editText.postDelayed(new Runnable() {
                public void run() {
                    editText.requestFocusFromTouch();
                    inputMethodManager.showSoftInput(editText, 0);
                }
            }, 300);
        } else {
            reduce();
        }
    }

    protected void handleAttributes(Context context, AttributeSet attrs) {
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextField);

            {
                ANIMATION_DURATION = styledAttrs.getInteger(R.styleable.MaterialTextField_mtf_animationDuration, 400);
            }
            {
                OPEN_KEYBOARD_ON_FOCUS = styledAttrs.getBoolean(R.styleable.MaterialTextField_mtf_openKeyboardOnFocus, false);
            }
            {
                labelColor = styledAttrs.getColor(R.styleable.MaterialTextField_mtf_labelColor, -1);
            }
            {
                imageDrawableId = styledAttrs.getResourceId(R.styleable.MaterialTextField_mtf_image, -1);
            }
            {
                imageButtonDrawableId = styledAttrs.getResourceId(R.styleable.MaterialTextField_mtf_search, -1);
            }
            {
                cardCollapsedHeight = styledAttrs.getDimensionPixelOffset(R.styleable.MaterialTextField_mtf_cardCollapsedHeight, context.getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_initial));
            }
            {
                hasFocus = styledAttrs.getBoolean(R.styleable.MaterialTextField_mtf_hasFocus, false);
            }
            {
                backgroundColor = styledAttrs.getColor(R.styleable.MaterialTextField_mtf_backgroundColor, -1);
            }
            {
                notCollapsible = styledAttrs.getBoolean(R.styleable.MaterialTextField_mtf_notCollapsible, false);
            }
            {
                showNotCollapsibleKeyboard = styledAttrs.getBoolean(R.styleable.MaterialTextField_mtf_showNotCollapsibleKeyboard, false);
            }
            {
                imageButtonDrawableId = styledAttrs.getResourceId(R.styleable.MaterialTextField_mtf_showNotCollapsibleKeyboardDelay, 100);
            }

            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected EditText findEditTextChild() {
        if (getChildCount() > 0 && getChildAt(0) instanceof EditText) {
            return (EditText) getChildAt(0);
        }
        return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editText = findEditTextChild();
        if (editText == null) {
            return;
        }

        addView(LayoutInflater.from(getContext()).inflate(R.layout.mtf_layout, this, false));

        editTextLayout = (ViewGroup) findViewById(R.id.mtf_editTextLayout);
        removeView(editText);
        editTextLayout.addView(editText);

        label = (TextView) findViewById(R.id.mtf_label);
        ViewCompat.setPivotX(label, 0);
        ViewCompat.setPivotY(label, 0);

        if (editText.getHint() != null) {
            label.setText(editText.getHint());
            editText.setHint("");
        }

        card = findViewById(R.id.mtf_card);

        if (backgroundColor != -1) {
            card.setBackgroundColor(backgroundColor);
        }

        final int expandedHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_final);
        final int reducedHeight = cardCollapsedHeight;

        reducedScale = (float) (reducedHeight * 1.0 / expandedHeight);
        ViewCompat.setScaleY(card, reducedScale);
        ViewCompat.setPivotY(card, expandedHeight);

        image = (ImageView) findViewById(R.id.mtf_image);
        ViewCompat.setAlpha(image, 0);
        ViewCompat.setScaleX(image, 0.4f);
        ViewCompat.setScaleY(image, 0.4f);

        imageButton = (ImageView) findViewById(R.id.mtf_search);
        ViewCompat.setAlpha(imageButton, 0);
        ViewCompat.setScaleX(imageButton, 0.4f);
        ViewCompat.setScaleY(imageButton, 0.4f);


        ViewCompat.setAlpha(editText, 0f);
        editText.setBackgroundColor(Color.TRANSPARENT);

        labelTopMargin = FrameLayout.LayoutParams.class.cast(label.getLayoutParams()).topMargin;

        customizeFromAttributes();

        if (!notCollapsible) {
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
            setHasFocus(hasFocus);
        }
        else {
            expand();

            if (showNotCollapsibleKeyboard) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setHasFocus(true);
                    }
                }, showNotCollapsibleKeyboardDelay);
            }

        }


    }

    protected void customizeFromAttributes() {
        if (labelColor != -1) {
            this.label.setTextColor(labelColor);
        }

        if (imageDrawableId != -1) {
            this.image.setImageDrawable(ContextCompat.getDrawable(getContext(), imageDrawableId));
        }

        if (imageButtonDrawableId != -1) {
            this.imageButton.setImageDrawable(ContextCompat.getDrawable(getContext(), imageButtonDrawableId));
        } else {
            this.imageButton.setVisibility(View.INVISIBLE);
        }

    }

}
