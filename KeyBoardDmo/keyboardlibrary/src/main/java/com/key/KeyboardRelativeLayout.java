package com.key;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Created by Administrator on 2017/4/29.
 * 软键盘快捷键（类似UC浏览器软键盘上面的快捷键）
 */

public class KeyboardRelativeLayout extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener, View.OnClickListener, ClipDialogFragment.OnClipItemClickListener {

    private final int CURSOR_LEFT = 1;
    private final int CURSOR_RIGHT = 2;
    private TextView mKey1;
    private TextView mKey2;
    private TextView mKey3;
    private TextView mKey4;
    private RelativeLayout mKeyLayout;
    private SeekBar mSeekBar;
    private EditText mEditText;
    private GestureDetector mSeekBarGestureDetector;
    private boolean isLongPress = false;
    private boolean isSeekTouchUp = false;
    private int mCursorPosition;
    private int mSeekBarWidth;
    private int mSeekBarProgress;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private ClipDialogFragment clipDialogFragment;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Selection.setSelection(mEditText.getText(), mCursorPosition, Math.max(Selection.getSelectionEnd(mEditText.getText()) - 1, 0));
                    if (mEditText.getSelectionEnd() != 0)
                        sendEmptyMessageDelayed(CURSOR_LEFT, 150);
                    break;
                case 2:
                    Selection.setSelection(mEditText.getText(), mCursorPosition, Math.max(Math.min(Selection.getSelectionEnd(mEditText.getText()) + 1, mEditText.length()), 0));
                    if (mEditText.getSelectionEnd() != mEditText.length())
                        sendEmptyMessageDelayed(CURSOR_RIGHT, 150);
                    break;
            }
        }
    };
    private AppCompatActivity mActivity;

    public KeyboardRelativeLayout(Context context) {
        this(context, null);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.yj_keyboard_layout, this);
        mSeekBarGestureDetector = new GestureDetector(context, new SeekBarGestureDetector());
        clipDialogFragment = new ClipDialogFragment();
        clipDialogFragment.setOnClipItemCickListener(this);
        registerReceiver();

    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }

    public void setActivity(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void setUpWithEditText(EditText editText) {
        this.mEditText = editText;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mKey1 = (TextView) findViewById(R.id.key_1);
        mKey2 = (TextView) findViewById(R.id.key_2);
        mKey3 = (TextView) findViewById(R.id.key_3);
        mKey4 = (TextView) findViewById(R.id.key_4);
        mKey1.setOnClickListener(this);
        mKey2.setOnClickListener(this);
        mKey3.setOnClickListener(this);
        mKey4.setOnClickListener(this);
        findViewById(R.id.copy_tab).setOnClickListener(this);
        mKeyLayout = (RelativeLayout) findViewById(R.id.key_body_layout);
        mSeekBar = (SeekBar) findViewById(R.id.fg_srt_forum_item_seek);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setOnTouchListener(this);

        mSeekBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mSeekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSeekBarWidth = mSeekBar.getMeasuredWidth();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isLongPress && seekBar.isPressed()) {
            int dx = progress - mSeekBarProgress;
            if (dx <= 0) {
                mHandler.removeMessages(CURSOR_RIGHT);
                Selection.setSelection(mEditText.getText(), mCursorPosition, Math.max(Selection.getSelectionEnd(mEditText.getText()) - Math.abs(dx), 0));
                if (progress == 0 && mEditText.getSelectionEnd() != 0) {
                    mHandler.sendEmptyMessageDelayed(CURSOR_LEFT, 150);
                }
            } else {
                mHandler.removeMessages(CURSOR_LEFT);
                Selection.setSelection(mEditText.getText(), mCursorPosition, Math.max(Math.min(Selection.getSelectionEnd(mEditText.getText()) + Math.abs(dx), mEditText.length()), 0));
                if (progress == 41 && mEditText.getSelectionEnd() != mEditText.length()) {
                    mHandler.sendEmptyMessageDelayed(CURSOR_RIGHT, 150);
                }
            }
            mSeekBarProgress = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeekBarProgress = seekBar.getProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(CURSOR_LEFT);
        mHandler.removeMessages(CURSOR_RIGHT);
        if (isLongPress) {
        } else {
            mSeekBar.setProgress(21);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mSeekBarGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && isLongPress) {
            if (Shake.shake(v)) {
                return false;
            }
            isSeekTouchUp = true;
            stopSeekAnim(getMeasuredWidth() - (int) (getResources().getDisplayMetrics().density * 32));

        }
        return false;
    }

    public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            mKey1.setVisibility(View.VISIBLE);
            mKey2.setText("/");
            mKey3.setText(".cn");
            mSeekBar.setEnabled(true);
            DrawableCompat.setTint(mSeekBar.getProgressDrawable(), Color.parseColor("#C0C4C5"));
        } else {
            mKey2.setText("www.");
            mKey3.setText(".m");
            mKey1.setVisibility(View.GONE);
            mSeekBar.setEnabled(false);
            DrawableCompat.setTint(mSeekBar.getProgressDrawable(), Color.parseColor("#e1e5e6"));
        }
    }

    @Override
    public void onClick(View view) {
        if (Shake.shake(view))
            return;
        int id = view.getId();
        if (id == R.id.copy_tab) {
            if (clipDialogFragment != null)
                clipDialogFragment.show(mActivity.getSupportFragmentManager(), ClipDialogFragment.class.getName());
        } else {
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            mEditText.getText().replace(Math.min(start, end), Math.max(start, end), ((TextView) view).getText());
            mEditText.setSelection(Math.min(start, end) + ((TextView) view).getText().length());
        }

    }

    public void registerReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        getContext().registerReceiver(mScreenStatusReceiver, filter);
    }

    public void unRegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        getContext().unregisterReceiver(mScreenStatusReceiver);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterReceiver();
    }

    /**
     * SeekBar 展开动画
     *
     * @param startWidth
     */
    private void startSeekAnim(int startWidth) {
        isLongPress = true;
        ViewWrapper viewWrapper = new ViewWrapper(mSeekBar);
        ObjectAnimator mStartAnim = ObjectAnimator.ofInt(viewWrapper, "width", startWidth, getMeasuredWidth() - (int) (getResources().getDisplayMetrics().density * 32));
        mStartAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSeekBar.setProgress(21);
            }
        });
        mStartAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isSeekTouchUp)
                    mKeyLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isSeekTouchUp = false;

            }
        });
        mStartAnim.setDuration(300);
        mStartAnim.setInterpolator(new FastOutSlowInInterpolator());
        mStartAnim.start();
    }

    /**
     * SeekBar 结束动画
     *
     * @param startWidth
     */
    private void stopSeekAnim(int startWidth) {
        final int progress = mSeekBar.getProgress();
        ViewWrapper viewWrapper = new ViewWrapper(mSeekBar);
        final ObjectAnimator mStopAnim = ObjectAnimator.ofInt(viewWrapper, "width", startWidth, mSeekBarWidth);
        mStopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSeekBar.setProgress((int) (progress - (progress - 21) * animation.getAnimatedFraction()));
                ViewCompat.setAlpha(mKeyLayout, animation.getAnimatedFraction());
            }
        });
        mStopAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isLongPress = false;
                requireNonNull(mEditText, "editText can't be null");
                if (mEditText.getSelectionEnd() != mEditText.getSelectionStart()) {
                    EditorCompat.startSelectActionMode(mEditText);

                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mKeyLayout.setVisibility(View.VISIBLE);

            }
        });

        mStopAnim.setDuration(200);
        mStopAnim.setInterpolator(new FastOutSlowInInterpolator());
        mStopAnim.start();

    }

    @Override
    public void onClipItemClick(View view) {
        int start = mEditText.getSelectionStart();
        int end = mEditText.getSelectionEnd();
        mEditText.getText().replace(Math.min(start, end), Math.max(start, end), ((TextView) view).getText());
        mEditText.setSelection(Math.min(start, end) + ((TextView) view).getText().length());
    }

    class SeekBarGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            requireNonNull(mEditText, "editText can't be null");
            EditorCompat.stopTextActionMode(mEditText);
            return super.onDown(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            requireNonNull(mEditText, "editText can't be null");
            VibratorCompat.Vibrate(getContext(), 100);
            mCursorPosition = mEditText.getSelectionStart();
            EditorCompat.positionAtCursorOffset(mEditText);
            startSeekAnim(mSeekBarWidth);

        }
    }

    class ScreenStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    stopSeekAnim(getMeasuredWidth());
                    break;
            }
        }
    }

}
