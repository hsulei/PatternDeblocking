package com.hsulei.portraitchoose.patterndeblocking.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hsulei.portraitchoose.patterndeblocking.R;

/**
 * Created by 46697 on 2016/10/19.
 * 九宫格解锁图案
 */

public class PatternDeblockView extends View {
    // 按下时的颜色
    private int mPressedColor = Color.BLUE;
    //错误时的颜色
    private int mErrorColor = Color.RED;
    //平常的颜色
    private int mNormalColor = Color.GRAY;
    //滑动时的线颜色
    private int mPressedLineColor = Color.BLUE;
    //错误时的线颜色
    private int mErrorLineColor = Color.RED;

    private int width;
    private int height;

    //圆半径
    private float mRadius;

    private Point[][] mPoints;

    //画笔
    private Paint mPaint;

    public PatternDeblockView(Context context) {
        super(context);
        initAttr(context, null);
    }

    public PatternDeblockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public PatternDeblockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PatternDeblockView);
            int len = array.getIndexCount();
            for (int i = 0; i < len; i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.PatternDeblockView_pressed_color:
                        mPressedColor = array.getColor(attr, Color.BLUE);
                        break;
                    case R.styleable.PatternDeblockView_error_color:
                        mErrorColor = array.getColor(attr, Color.RED);
                        break;
                    case R.styleable.PatternDeblockView_normal_color:
                        mNormalColor = array.getColor(attr, Color.GRAY);
                        break;
                    case R.styleable.PatternDeblockView_pressed_line_color:
                        mPressedLineColor = array.getColor(attr, Color.BLUE);
                        break;
                    case R.styleable.PatternDeblockView_error_line_color:
                        mErrorLineColor = array.getColor(attr, Color.RED);
                        break;
                }
            }
        }
        //设置半径
        mRadius = 20;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //没有进行初始化
        if (mPoints == null) {
            // 进行初始化
            initPaints();
        }

        drawPoints(canvas, mPoints);
        super.onDraw(canvas);
    }

    /**
     * 画点
     *
     * @param canvas
     * @param mPoints
     */
    private void drawPoints(Canvas canvas, Point[][] mPoints) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoints[i][j];
                if (point.state == Point.NORMALSTATE) {
                    mPaint.setColor(mNormalColor);
                } else if (point.state == Point.PRESSEDSTATE) {
                    mPaint.setColor(mPressedColor);
                } else {
                    mPaint.setColor(mErrorColor);
                }
                canvas.drawCircle(point.x, point.y, mRadius, mPaint);
            }
        }
    }

    /**
     * 对点进行初始化
     */
    private void initPaints() {

        float offsetX = 0;
        float offsetY = 0;
        int min = Math.min(width, height);//获取最小值
        if (width >= height) {//如果宽比高大
            offsetX = (width - height) / 2;
        } else {//如果宽比高小
            offsetY = (height - width) / 2;
        }
        mPoints = new Point[3][3];

        //初始化完成
        mPoints[0][0] = new Point(offsetX + min / 4, offsetY + min / 4);
        mPoints[0][1] = new Point(offsetX + min / 2, offsetY + min / 4);
        mPoints[0][2] = new Point(offsetX + min - min / 4, offsetY + min / 4);

        mPoints[1][0] = new Point(offsetX + min / 4, offsetY + min / 2);
        mPoints[1][1] = new Point(offsetX + min / 2, offsetY + min / 2);
        mPoints[1][2] = new Point(offsetX + min - min / 4, offsetY + min / 2);

        mPoints[2][0] = new Point(offsetX + min / 4, offsetY + min - min / 4);
        mPoints[2][1] = new Point(offsetX + min / 2, offsetY + min - min / 4);
        mPoints[2][2] = new Point(offsetX + min - min / 4, offsetY + min - min / 4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 测量高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int height = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = 200;
        }
        return height;
    }

    /**
     * 测量宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int width = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = 200;
        }
        return width;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }

    /**
     * 点的代表
     */
    public class Point {
        //圆有三种属性 0:未按下  1:按下 2:错误
        public final static int NORMALSTATE = 0;
        public final static int PRESSEDSTATE = 1;
        public final static int ERRORSTATE = 2;

        //状态
        public int state = NORMALSTATE;
        //x坐标
        public float x;
        //y坐标
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }

}
