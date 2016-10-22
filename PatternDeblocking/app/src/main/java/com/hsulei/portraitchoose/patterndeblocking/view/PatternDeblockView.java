package com.hsulei.portraitchoose.patterndeblocking.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hsulei.portraitchoose.patterndeblocking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 46697 on 2016/10/19.
 * 九宫格解锁图案
 */

public class PatternDeblockView extends View {

    private final static String TAG = "PatternDeblockView";
    //最小值
    private float min;
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

    //被选中的点
    private List<Point> mChoosePoints;

    private float movingX;
    private float movingY;

    //用于判断是ActionDown时有点被选中
    private boolean isChosen;
    //用于判断ActionDown时是否在重复点中
    private boolean isRepeat;
    //用于判读是否已经结束
    private boolean isFinish;
    //用于判断是否错误我
    private boolean isError;


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
        mChoosePoints = new ArrayList<Point>();

        //设置半径
        mRadius = 40;
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);//设置线宽
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
        drawLine(canvas, movingX, movingY);

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

        min = Math.min(width, height);//获取最小值
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
        //获得当其移动位置
        movingX = event.getX();
        movingY = event.getY();
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isFinish) {
                    point = choosePoints(movingX, movingY);//判断落下的点是否在9个点上
                    if (null != point) {
                        mChoosePoints.add(point);//如果不为空加入选中的集合中
                        isChosen = true;// 表示已经与了选择
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //移动时有两情况需要进考虑，1：在点的范围时需要把点选中，2：重复点不计算，3：一条线上 不能跳过选择
                if (!isFinish) {
                    isRepeat = checkIsRepeat(movingX, movingY);
                    if (!isRepeat) {//如果不在已经选中的点中，判读是否在点阵中
                        point = choosePoints(movingX, movingY);
                        //进行跳过选择
                        if (null != point) {
                            mChoosePoints.add(point);
                        }
                        parsePoint();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                //对点数进行判读 如果连起来的点数少于5设置成
                int len = mChoosePoints.size();
                if (len < 5) {
                    isError = true;
                    for (int i = 0; i < len; i++) {
                        point = mChoosePoints.get(i);
                        point.state = Point.ERRORSTATE;
                    }
                }
                break;
        }

        postInvalidate();// 进行重绘
        return true;
    }

    /**
     * 进行跳过添加
     */
    private void parsePoint() {
        int len = mChoosePoints.size();
        //如果被选中的点个数不大于1
        if (len <= 1) {
            return;
        }

        //拿到最后最后两个Point
        Point a = mChoosePoints.get(len - 1);
        Point b = mChoosePoints.get(len - 2);

        Point p = null;
        float x = 0;
        float y = 0;

        if (a.x == b.x && Math.abs(a.y - b.y) == min / 2) {//在同一条竖线
            Log.i(TAG, "a.x=b.x");
            x = a.x;
            y = a.y - (a.y - b.y) / 2;
        } else if (Math.abs(a.x - b.x) == min / 2 && a.y == b.y) {//在同一条直线
            x = a.x - (a.x - b.x) / 2;
            y = a.y;
        } else if (Math.abs(a.x - b.x) == min / 2 && Math.abs(a.y - b.y) == min / 2) {//在同一条斜线上
            x = a.x - (a.x - b.x) / 2;
            y = a.y - (a.y - b.y) / 2;
        }
        p = choosePoints(x, y);
        Log.i(TAG, "X:" + x + ";;;;;Y:" + y);
        if (null != p) {
            p.state = Point.PRESSEDSTATE;
            mChoosePoints.add(len - 1, p);
        }
    }


    private void drawLine(Canvas canvas, float movingX, float movingY) {
        float secondX = 0;
        float secondY = 0;
        float firstX = 0;
        float firstY = 0;
        if (isError) {
            mPaint.setColor(mErrorLineColor);//设置错误时的线颜色
        } else {
            mPaint.setColor(mPressedLineColor);
        }
        int len = mChoosePoints.size();

        if (len >= 1) {
            firstX = mChoosePoints.get(0).x;
            firstY = mChoosePoints.get(0).y;
        }
        //画点中的线
        for (int i = 1; i < len; i++) {
            secondX = mChoosePoints.get(i).x;
            secondY = mChoosePoints.get(i).y;

            canvas.drawLine(firstX, firstY, secondX, secondY, mPaint);
            firstX = secondX;
            firstY = secondY;
        }

        if (!isFinish) {
            //画点到点的线
            if (len >= 1) {
                canvas.drawLine(firstX, firstY, movingX, movingY, mPaint);
            }
        }

    }

    /**
     * 判断是否在重复点中
     *
     * @param movingX
     * @param movingY
     * @return
     */
    private boolean checkIsRepeat(float movingX, float movingY) {
        int len = mChoosePoints.size();
        for (int i = 0; i < len; i++) {
            if (Point.isInPoint(mChoosePoints.get(i), movingX, movingY, mRadius)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (
     * 在点阵中找到点
     *
     * @param movingX
     * @param movingY
     * @return
     */
    private Point choosePoints(float movingX, float movingY) {
        Log.i(TAG, "movingX:" + movingX + ";;;;;movingY:" + movingY);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoints[i][j];
                if (Point.isInPoint(point, movingX, movingY, mRadius)) {
                    //修改点的状态(被选中)
                    point.state = Point.PRESSEDSTATE;
                    return point;
                }
            }
        }
        return null;
    }

}

/**
 * 点的代表
 */
class Point {
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

    /**
     * 判断坐标是否在点的范围之间
     *
     * @param point
     * @param movingX
     * @param movingY
     * @param radius
     * @return
     */
    public static boolean isInPoint(Point point, float movingX, float movingY, float radius) {
        if (Math.sqrt((movingX - point.x) * (movingX - point.x) + (movingY - point.y) * (movingY - point.y)) <= radius) {
            return true;
        } else {
            return false;
        }
    }

}