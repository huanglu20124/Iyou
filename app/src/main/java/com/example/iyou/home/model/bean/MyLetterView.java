package com.example.iyou.home.model.bean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.iyou.R;
import com.example.iyou.tool.PixelUtil;

/**
 * Created by asus on 2017/1/14.
 */
public class MyLetterView extends View {

    public static String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "#" };

    private Paint paint = new Paint();
    /**
     * 表示选中状态
     */
    private int choose = -1;

    private TextView mTextDialog;

    public void setTextDialog(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    private OnTouchingLetterChangedListener listener;

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener listener) {
        this.listener = listener;
    }

    public MyLetterView(Context context) {
        super(context);
    }

    public MyLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取该自定义View的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 单个字母的高度
        int singleHeight = height / letters.length;

        for (int i = 0; i < letters.length; i++) {
            paint.setColor(getResources().getColor(
                    R.color.colorPrimary));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(PixelUtil.sp2px(12, getContext()));

            // 如果选中的话,改变样式和颜色
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }

            // 首先确定每个字母的横坐标的位置，横坐标：该自定义View的一半 -（减去） 单个字母宽度的一半
            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * (i + 1);

            canvas.drawText(letters[i], xPos, yPos, paint);

            // 重置画笔
            paint.reset();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float y = event.getY();

        int oldChoose = choose;
        // 根据y坐标确定当前哪个字母被选中
        int pos = (int) (y / getHeight() * letters.length);

        switch (action) {
            case MotionEvent.ACTION_UP:
                // 当手指抬起时,设置View的背景为白色
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                // 重置为初始状态
                choose = -1;
                // 让View重绘
                invalidate();

                // 将对话框设置为不可见
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }

                break;
            default:
                // 设置右边字母View的背景色
                setBackgroundResource(android.R.drawable.ic_media_play);
                if (pos != oldChoose) {
                    // 如果之前选中的和当前的不一样，需要重绘
                    if (pos >= 0 && pos < letters.length) {
                        if(listener != null) {
                            //当前字母被选中，需要让ListView去更新显示的位置
                            listener.onTouchingLetterChanged(letters[pos]);
                        }
                        //在左边显示选中的字母，该字母放在TextView上，相当于一个dialog
                        if (mTextDialog != null) {
                            mTextDialog.setText(letters[pos]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = pos;
                    }
                }
                break;
        }

        return true;
    }

    /**
     * 该回调接口用于通知ListView更新状态
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
