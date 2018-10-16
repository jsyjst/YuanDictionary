package cn.jsyjst.dictionary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.listener.OnWordsChangeListener;

/**
 * Created by 残渊 on 2018/4/22.
 */

public class IndexView extends View {


    public IndexView(Context context) {
        super(context);
    }

    public IndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     *  索引字母数组
     */
    private String[] word = {
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };
    /**
     *  当前选择的索引字母的下标
     */
    private int chooseWordIndex=-1;
    /**
     *  画笔
     */
    private Paint mPaint=new Paint();

    /**
     * 索引字母绘制大小
     */
    private int wordSize=30;

    private OnWordsChangeListener onWordsChangeListener;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         *  获得控件高度
         */
        int viewHeight = getHeight();
        /**
         *  获得控件宽度
         */
        int viewWidth = getWidth();
        /**
         *  控件高度除以索引字母个数得到每个索引字母的高度
         */
        int heightPerWord = viewHeight / word.length;
        /**
         *  通过循环每个索引字母，并绘制出来
         */
        for (int i = 0; i < word.length; i++) {
            /**
             *  设置画笔颜色、画笔绘制文字粗细和大小，设置抗锯齿
             */
            mPaint.setColor(Color.GRAY);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setTextSize(wordSize);
            mPaint.setAntiAlias(true);
            /**
             *  如果当前选择的索引字母下标和循环到的索引字母下标相同
             */

            if (chooseWordIndex == i) {

                /**
                 * 设置画笔颜色，绘制文字大小和加粗
                 */
                mPaint.setColor(Color.BLUE);
                mPaint.setTextSize(wordSize);
                mPaint.setFakeBoldText(true);

            }
            /**
             *  索引字母的相对于控件的x坐标，此处算法结果为居中
             */
            float xPos = viewWidth / 2 - mPaint.measureText(word[i]) / 2;
            /**
             * 索引字母的相对于控件的y坐标，索引字母的高度乘以索引字母下标+1即为y坐标
             */
            float yPos = heightPerWord* (i+1) ;
            /**
             *  绘制索引字母
             */
            canvas.drawText(word[i], xPos, yPos, mPaint);
            /**
             *  重置画笔，为绘制下一个索引字母做准备
             */
            mPaint.reset();

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        /**
         * 获得触摸后的动作
         */
        int action = event.getAction();
        /**
         *  获得触摸点的Y轴坐标
         */
        float touchYPos=event.getY();

        /**
         * 控件高度除以索引字母的个数得到每个索引字母的高度（这里进行int强转），触摸点的Y轴坐标除以每个索引字母的高度就得到触摸到的索引字母的下标
         */

        int touchIndex= (int) (touchYPos/getHeight()*word.length);


        switch (action){
            /**
             *  当触摸的动作为按下或者按下移动时
             */

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                /**
                 * 设置背景颜色
                 */
                setBackgroundResource(R.color.gray);
                /**
                 *  设置当前选的索引字母的下标值为当前选择的值
                 */
               chooseWordIndex=touchIndex;
                /**
                 *  如果接口存在和索引下标值合法，执行接口方法，传入当前触摸的索引字母，供外部调用接收
                 */

                if(onWordsChangeListener!=null&&touchIndex<word.length&&touchIndex>-1){
                    onWordsChangeListener.wordsChange(word[touchIndex]);
                }
                /**
                 * 重新绘制控件，即重新执行onDraw函数
                 */
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundResource(R.color.white);
                /**
                 * 当停止触摸控件的时候，将当前选择的索引字母下标值设为-1
                 */
                chooseWordIndex=-1;
                invalidate();
                break;
            default:
                break;
        }

        /**
         *  返回true表明该触摸事件处理完毕不分发出去
         */
        return true;
    }

    public void setOnWordsChangeListener(OnWordsChangeListener onWordsChangeListener) {
        this.onWordsChangeListener=onWordsChangeListener;
    }
}