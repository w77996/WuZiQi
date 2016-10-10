package wu.com.wuziqi;

import android.content.Context;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */
public class WuZiQiPanel extends View {

    private int mPanelwidth;//棋盘的宽高
    private float mLineheight; //每一行的高度
    private int MAX_LINE =10;//十行的棋
    private int MAX_COUNT_IN_LINE =5;//棋子五个数
    Paint paint  = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private boolean mIsWhitePice = true;//判断是否是白旗
    private float PieceOfLineHeight=3*1.f/4;//每个棋子站3/4的LineHeight

    private boolean mWhiteWinner;
    private boolean IsGameOver;


    private List<Point> mWhitePieceList = new ArrayList<>();
    private List<Point> mBlackPieceList = new ArrayList<>();

    public WuZiQiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //通过widthMeasureSpec拿到宽度尺寸大小
        //widthMeasureSpec,heightMeasureSpec 模式和尺寸组合在一起的数值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//得到宽尺寸
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//等到宽模式

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, MeasureSpec.AT_MOST。

         MeasureSpec.EXACTLY是精确尺寸，当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。

         MeasureSpec.AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。

         MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式。
         */

        int width = Math.min(widthSize,heightSize);//正方形

        if(widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width,width);//设置实际大小
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("onSizeChanged w",w+"");
        mPanelwidth = w;
        mLineheight = mPanelwidth*1.0f/MAX_LINE;//转化成浮点数后分为10,求出每个方格的高度

        int pieceSize =(int) (mLineheight*PieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,pieceSize,pieceSize,false);//设置宽高
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,pieceSize,pieceSize,false);//设置宽高
    }

    /**
     * 绘制视图
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        cheakGameOver();
    }

    /**
     * 判断游戏结束，核心算法
     */
    private void cheakGameOver() {

        boolean whiteWin = cheakWinner(mWhitePieceList);//检查白子的五子
        boolean blackWin = cheakWinner(mBlackPieceList);
        if(whiteWin||blackWin){
            IsGameOver = true;
            mWhiteWinner = whiteWin;
            String text = mWhiteWinner?"白棋":"黑棋";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
        
    }

    /**
     * 判断白子是否有五子
     * @param point
     * @return
     */
    private boolean cheakWinner(List<Point> point) {

        for(Point p:point){
            int x =p .x;
            int y = p.y;
           boolean win =  checkHorizontal(x,y,point);
            if(win) return true;
            win =checkLeft(x,y,point);
            if(win) return true;
            win =checkRight(x,y,point);
            if(win) return true;
            win =checkVertical(x,y,point);
            if(win) return true;
        }
        return false;
    }

    /**
     * 判断横向棋子是否五子连珠
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> point) {
        int count =1;
        //判断左边的棋子
        for(int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x-i,y))){
                count++;
            }else{
                break;
            }
        }
        //判断右边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    /**
     * 纵向
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> point) {
        int count =1;

        for(int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }
        }
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    /**
     * 左斜
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkLeft(int x, int y, List<Point> point) {
        int count =1;
        //判断上的棋子
        for(int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }
        }
        //判断右边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x+i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    /**
     * 右斜
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkRight(int x, int y, List<Point> point) {
        int count =1;
        //判断上的棋子
        for(int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }
        }
        //判断右边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(point.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }
    /**
     * 绘制棋子
     * (1-PieceOfLineHeight)/2  是棋子两边距离LineHeight的长度
     * @param canvas
     */
    private void drawPiece(Canvas canvas) {
        for(int i=0;i<mWhitePieceList.size();i++){
            Point white = mWhitePieceList.get(i);
            canvas.drawBitmap(mWhitePiece,(white.x+(1-PieceOfLineHeight)/2)*mLineheight,(white.y+(1-PieceOfLineHeight)/2)*mLineheight,null);
        }
        for(int i=0;i<mBlackPieceList.size();i++){
            Point black = mBlackPieceList.get(i);
            canvas.drawBitmap(mBlackPiece,(black.x+(1-PieceOfLineHeight)/2)*mLineheight,(black.y+(1-PieceOfLineHeight)/2)*mLineheight,null);
        }
    }

    /**
     * 绘制棋盘
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelwidth;//拿到棋盘宽度
        float lineheight = mLineheight;

        for(int i=0;i<MAX_LINE;i++){
            int starx = (int)(lineheight/2);//x轴起点
            int endx = (int)(w-lineheight/2);//x轴终点

            int y = (int)((0.5+i)*lineheight);//y
          // Log.d("y",y+"");
            canvas.drawLine(starx,y,endx,y,paint);
            canvas.drawLine(y,starx,y,endx,paint);
        }
    }

    /**
     * 复写onTouchEvent事件对用户点击事件进行处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(IsGameOver)    //游戏结束就不再落子
            return false;
        int action = event.getAction();
        if(action == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x,y);
            if(mBlackPieceList.contains(p)||mWhitePieceList.contains(p)){
                return false;
            }
            //加入List中
            if(mIsWhitePice){
                mWhitePieceList.add(p);
            }else{
                mBlackPieceList.add(p);
            }
            mIsWhitePice = !mIsWhitePice;
            invalidate();//重绘


        }
        return true;//表示事件被消费
    }

    /**
     * 处理点击事件的取值，防止图片偏移
     * @param x
     * @param y
     * @return
     */
    private Point getValidPoint(int x, int y) {

        return new Point((int)(x/mLineheight), (int) (y/mLineheight));
    }

    /**
     * 初始化方法
     *
     */
    private void init(){
        paint.setColor(0x88000000);//半透明灰色
        paint.setAntiAlias(true);//防锯齿
        paint.setDither(true);//防抖动
        paint.setStyle(Paint.Style.STROKE);//画线

        mWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("onFinishInflate","onFinishInflate");
    }
}
