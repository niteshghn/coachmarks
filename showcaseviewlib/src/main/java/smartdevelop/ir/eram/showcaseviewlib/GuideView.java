package smartdevelop.ir.eram.showcaseviewlib;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 */

public class GuideView extends FrameLayout {


    private static final float INDICATOR_LENGTH = 20;
    private int btnDrawable;

    private float density;
    private View target;
    private RectF rect;
    private GuideMessageView mMessageView;
    private boolean isTop;
    private Gravity mGravity;
    private DismissType dismissType;
    int marginGuide;
    private boolean mIsShowing;
    private GuideListener mGuideListener;
    int xMessageView = 0;
    int yMessageView = 0;

    final int ANIMATION_DURATION = 400;
    final Paint emptyPaint = new Paint();
    final Paint paintLine = new Paint();
    final Paint paintCircle = new Paint();
    final Paint paintCircleInner = new Paint();
    final Paint mPaint = new Paint();
    final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Xfermode XFERMODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private float lineLength = 40;
    private WindowShape viewWindowShape;

    public interface GuideListener {
        void onDismiss(View view);
    }

    public enum Gravity {
        auto, center, left, right, bottom, top
    }

    public enum DismissType {
        outside, anywhere, targetView
    }

    public enum WindowShape {
        circle, rectangle
    }

    private GuideView(Context context, View view) {
        super(context);
        setWillNotDraw(false);

        this.target = view;

        density = context.getResources().getDisplayMetrics().density;

        int[] locationTarget = new int[2];
        target.getLocationOnScreen(locationTarget);
        rect = new RectF(locationTarget[0], locationTarget[1],
                locationTarget[0] + target.getWidth(),
                locationTarget[1] + target.getHeight());

        mMessageView = new GuideMessageView(getContext());
        final int padding = (int) (5 * density);
        mMessageView.setPadding(padding, padding, padding, padding);
        mMessageView.setColor(Color.TRANSPARENT);

        addView(mMessageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        setMessageLocation(resolveMessageViewLocation());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setMessageLocation(resolveMessageViewLocation());
                int[] locationTarget = new int[2];
                target.getLocationOnScreen(locationTarget);
                rect = new RectF(locationTarget[0], locationTarget[1],
                        locationTarget[0] + target.getWidth(), locationTarget[1] + target.getHeight());
            }
        });
    }

    private int getNavigationBarSize() {
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private boolean isLandscape() {
        int display_mode = getResources().getConfiguration().orientation;
        return display_mode != Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (target != null) {
            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bitmap);

            float lineWidth = 1 * density;
            float strokeCircleWidth = 1 * density;
            float circleSize = 10 * density;
            float circleInnerSize = 3f * density;

            // created the overlay
            mPaint.setColor(0xdd000000);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
            tempCanvas.drawRect(canvas.getClipBounds(), mPaint);

            // creating the line
            paintLine.setStyle(Paint.Style.FILL);
            paintLine.setColor(Color.parseColor("#55BBEA"));
            paintLine.setStrokeWidth(lineWidth);
            paintLine.setAntiAlias(true);

            // creating the circle
            paintCircle.setStyle(Paint.Style.STROKE);
            paintCircle.setColor(Color.parseColor("#55BBEA"));
            paintCircle.setStrokeCap(Paint.Cap.ROUND);
            paintCircle.setStrokeWidth(strokeCircleWidth);
            paintCircle.setAntiAlias(true);

            // creating inner circle dot
            paintCircleInner.setStyle(Paint.Style.FILL);
            paintCircleInner.setStrokeCap(Paint.Cap.BUTT);
            paintCircleInner.setColor(Color.WHITE);
            paintCircleInner.setAntiAlias(true);

            marginGuide = getMarginGuide();

            float startPoint = getLineStartPoint() + marginGuide;

            float x = getXCoordinate();
            float y = getYCoordinate();

            float stopY = startPoint - lineLength * density;
            float stopX = startPoint + lineLength * density;
            float centerX = stopX + 10 * density;
            float centerY = stopY - 10 * density;

            if (mGravity == Gravity.right || mGravity == Gravity.left) {
                if (mGravity == Gravity.left) {
                    stopX = startPoint - ((lineLength + INDICATOR_LENGTH) * density);
                    centerX = stopX - 10 * density;
                }
                tempCanvas.drawLine(startPoint, y, stopX, y, paintLine);
                tempCanvas.drawCircle(centerX, y, circleSize, paintCircle);
                tempCanvas.drawCircle(centerX, y, circleInnerSize, paintCircleInner);
            } else {
                if (isTop) {
                    stopY = startPoint + (lineLength * density);
                    centerY = stopY + 10 * density;
                }
                tempCanvas.drawLine(x, startPoint, x,
                        stopY
                        , paintLine);
                tempCanvas.drawCircle(x, centerY, circleInnerSize, paintCircleInner);
                tempCanvas.drawCircle(x, centerY, circleSize, paintCircle);
            }
            targetPaint.setXfermode(XFERMODE_CLEAR);
            targetPaint.setAntiAlias(true);
            int rx = 15;
            int ry = 15;
            if (viewWindowShape == WindowShape.circle) {
                rx = (int) (target.getWidth() * 1.5);
                ry = (int) (target.getHeight() * 1.5);
            }
            tempCanvas.drawRoundRect(rect, rx, ry, targetPaint);
            canvas.drawBitmap(bitmap, 0, 0, emptyPaint);
        }
    }

    private float getYCoordinate() {
        if (mGravity == Gravity.top) {
            return rect.top;
        } else if (mGravity == Gravity.bottom) {
            return rect.bottom;
        } else {
            return (rect.bottom + rect.top) / 2;
        }
    }

    private float getXCoordinate() {
        if (mGravity == Gravity.right) {
            return rect.right;
        } else if (mGravity == Gravity.left) {
            return rect.left;
        } else {
            return rect.left / 2 + rect.right / 2;
        }
    }

    private float getLineStartPoint() {
        float lineStartPt = rect.bottom;
        if (mGravity == Gravity.left) {
            lineStartPt = rect.left;
        } else if (mGravity == Gravity.right) {
            lineStartPt = rect.right;
        } else {
            lineStartPt = isTop ? rect.bottom : rect.top;
        }
        return lineStartPt;
    }

    private int getMarginGuide() {
        float margin = 0;
        if (mGravity == Gravity.right || mGravity == Gravity.top) {
            margin = 2 * density;
        } else if (mGravity == Gravity.left || mGravity == Gravity.bottom) {
            margin = -2 * density;
        } else
            margin = (isTop ? 2 * density : -2 * density);

        return (int) margin;

    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void dismiss() {

        AlphaAnimation startAnimation = new AlphaAnimation(1f, 0f);
        startAnimation.setDuration(ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
        mIsShowing = false;
        if (mGuideListener != null) {
            mGuideListener.onDismiss(target);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (dismissType) {

                case outside:
                    if (!isViewContains(mMessageView, x, y)) {
                        dismiss();
                    }
                    break;

                case anywhere:
                    dismiss();
                    break;

                case targetView:
                    if (rect.contains(x, y)) {
                        target.performClick();
                        dismiss();
                    }
                    break;

            }
            return true;
        }
        return false;
    }

    private boolean isViewContains(View view, float rx, float ry) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }

    void setMessageLocation(Point p) {
        mMessageView.setX(p.x);
        mMessageView.setY(p.y);
        requestLayout();
    }


    private Point resolveMessageViewLocation() {

        if (mGravity == Gravity.center) {
            xMessageView = (int) (rect.left - mMessageView.getWidth() / 2 + target.getWidth() / 2);
        } else if (mGravity == Gravity.left) {
            xMessageView = (int) rect.left - mMessageView.getWidth() - target.getWidth();
        } else if (mGravity == Gravity.right) {
            xMessageView = (int) (rect.right + (lineLength + INDICATOR_LENGTH) * density);
        } else {
            xMessageView = (int) (rect.right) - mMessageView.getWidth();
        }

        if (isLandscape()) {
            xMessageView -= getNavigationBarSize();
        }

        if (xMessageView + mMessageView.getWidth() > getWidth())
            xMessageView = getWidth() - mMessageView.getWidth();
        if (xMessageView < 0)
            xMessageView = 0;


        //set message view bottom
        if (mGravity == Gravity.left || mGravity == Gravity.right) {
            yMessageView = (int) (rect.top + rect.bottom) / 2 - (mMessageView.getHeight() / 2);
        } else {

            //set message view bottom
            if (rect.top + (INDICATOR_LENGTH * density) > getHeight() / 2) {
                isTop = false;
                yMessageView = (int) (rect.top - mMessageView.getHeight() - INDICATOR_LENGTH * density - lineLength * density);
            }
            //set message view top
            else {
                isTop = true;
                yMessageView = (int) (rect.top + target.getHeight() + INDICATOR_LENGTH * density + lineLength * density);
            }
        }

        if (yMessageView < 0)
            yMessageView = 0;


        return new Point(xMessageView, yMessageView);
    }


    public void show() {
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setClickable(false);

        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);
        mIsShowing = true;

    }

    public void setTitle(String str) {
        mMessageView.setTitle(str);
    }

    public void setContentText(String str) {
        mMessageView.setContentText(str);
    }

    public void setBtnText(String btnText) {
        mMessageView.setBtnText(btnText);
    }

    public void setContentSpan(Spannable span) {
        mMessageView.setContentSpan(span);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mMessageView.setTitleTypeFace(typeFace);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mMessageView.setContentTypeFace(typeFace);
    }

    public void setBtnDrawableId(int btnDrawable) {
        mMessageView.setButtonBackground(btnDrawable);
    }

    public void setTitleTextSize(int size) {
        mMessageView.setTitleTextSize(size);
    }

    public void setLineLength(int length) {
        lineLength = length;
    }


    public void setContentTextSize(int size) {
        mMessageView.setContentTextSize(size);
    }


    public static class Builder {
        private View targetView;
        private String title, contentText;
        private Gravity gravity;
        private DismissType dismissType;
        private Context context;
        private int titleTextSize;
        private int contentTextSize;
        private Spannable contentSpan;
        private Typeface titleTypeFace, contentTypeFace;
        private GuideListener guideListener;
        private int lineLength;
        private int btnDrawable;
        private String btnText;
        private WindowShape windowShape;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTargetView(View view) {
            this.targetView = view;
            return this;
        }

        public Builder setGravity(Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setWindowShape(WindowShape shape) {
            this.windowShape = shape;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setButtonText(String btnText) {
            this.btnText = btnText;
            return this;
        }

        public Builder setContentSpan(Spannable span) {
            this.contentSpan = span;
            return this;
        }

        public Builder setContentTypeFace(Typeface typeFace) {
            this.contentTypeFace = typeFace;
            return this;
        }

        public Builder setGuideListener(GuideListener guideListener) {
            this.guideListener = guideListener;
            return this;
        }

        public Builder setTitleTypeFace(Typeface typeFace) {
            this.titleTypeFace = typeFace;
            return this;
        }

        public Builder setlineLength(int length) {
            this.lineLength = length;
            return this;
        }

        public Builder setBtnDrawable(int drawableId) {
            this.btnDrawable = drawableId;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setContentTextSize(int size) {
            this.contentTextSize = size;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setTitleTextSize(int size) {
            this.titleTextSize = size;
            return this;
        }

        public Builder setDismissType(DismissType dismissType) {
            this.dismissType = dismissType;
            return this;
        }

        public GuideView build() {
            GuideView guideView = new GuideView(context, targetView);
            guideView.mGravity = gravity != null ? gravity : Gravity.auto;
            guideView.dismissType = dismissType != null ? dismissType : DismissType.targetView;
            guideView.viewWindowShape = windowShape != null ? windowShape : WindowShape.rectangle;

            guideView.setTitle(title);
            if (contentText != null)
                guideView.setContentText(contentText);
            if (titleTextSize != 0)
                guideView.setTitleTextSize(titleTextSize);
            if (contentTextSize != 0)
                guideView.setContentTextSize(contentTextSize);
            if (contentSpan != null)
                guideView.setContentSpan(contentSpan);
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace);
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace);
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener;
            }
            if (lineLength != 0) {
                guideView.setLineLength(lineLength);
            }
            if (btnDrawable != 0) {
                guideView.setBtnDrawableId(btnDrawable);
            }
            if (btnText != null) {
                guideView.setBtnText(btnText);
            }

            return guideView;
        }


    }
}

