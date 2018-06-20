package com.njfu.huangxiao.map.shapefile;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.os.Message;
import android.service.quicksettings.Tile;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;

public class Mapview1 extends View {
    private Context context;
    private Box ExternalBox=new Box(0.0,0.0,.0,0);
    private float cmWidth;
    private float cmHeight;
    private double centerX;
    private ExecutorService pool=Executors.newCachedThreadPool();
    private int x;
    private int y;
    private Boolean PointFlag;
    private Spot sp=new Spot(0,0);
    private double preX;
    private double preY;
    private Matrix tileMatrix=new Matrix();
    private double dis;
    private double midX;
    private double midY;
    private float disx;
    private float disy;
    private double scale;
    private double porprotion;
    private String tileRootPath;
    private Spot ClickSpot;
    private Boolean isClick=false;
    private int Clickid=-1;
    private Bitmap BitTileMap;
    private TileMap tileMap;
    private int click_z_index=0;
    private double centerY;
    private int iniLevel;
    private List<Shapefile> shapefiles=new ArrayList<>();
    private List<Bitmap> viewBitmaps;
    private Boolean isFirst=true;
    private Boolean isup=true;
    private Matrix matrix;
    private int canvasWidth;
    private int canvasHeight;
    private TileFromToandLevel tileFromToandLevel;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };
    private class TileFromToandLevel{
        private int fromX;
        private int fromY;
        private int toX;
        private int toY;
        private int level;
        public TileFromToandLevel(int fromX,int fromY,int toX,int toY,int level){
            this.fromX=fromX;this.fromY=fromY;
            this.toX=toX;this.toY=toY;this.level=level;
        }
    }
    public Mapview1(Context context){
        super(context);
        this.context=context;
    }
    public Mapview1(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context=context;
    }

    public void setClick_z_index(int click_z_index){
        this.click_z_index=click_z_index;
    }
    public void setCenterandLevel(double x,double y,int level){
        double resolution=Resolution.Resolution(level);
        this.centerX=x;
        this.centerY=y;
        this.iniLevel=level;
    }
    public void addShapefile(Shapefile shapefile){
        this.shapefiles.add(shapefile);
        invalidate();
    }

    public void drawshapefile(){
        viewBitmaps=new ArrayList<>();
        scale=(ExternalBox.xMax-ExternalBox.xMin)*111110/cmWidth;
        Log.d(TAG, "drawpolygone: 开始画shapefile"+scale);
        int z_index=0;
        for (Shapefile shapefile:
             shapefiles) {
            DrawShapefile drawmap;
            if(isClick&&z_index==click_z_index) {
                drawmap = new DrawShapefile(shapefile, ExternalBox, canvasWidth, canvasHeight,scale, ClickSpot);
            }else{
                drawmap = new DrawShapefile(shapefile, ExternalBox, canvasWidth, canvasHeight,scale);
            }
            Bitmap viewBitmap;
            if((viewBitmap=drawmap.draw())!=null) {
                viewBitmaps.add(viewBitmap);
            }
            z_index++;
        }

    }
    public void setTileMap(TileMap tileMap,String tileRootPath){
        this.tileMap=tileMap;
        this.tileRootPath=tileRootPath;
    }
    public void drawTile(final int x,final int y,final int level,final String tileRootPath,final int fromX,final int fromY){
        Bitmap bitmap;
        bitmap=tileMap.getLocalTileMap(x,y,level,tileRootPath);
        if(bitmap!=null){
            Canvas canvas=new Canvas(BitTileMap);
            Matrix matrix=new Matrix();
            matrix.postTranslate(256*(x-fromX),256*(y-fromY));
            canvas.drawBitmap(bitmap,matrix,null);
        }else {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Canvas canvas = new Canvas(BitTileMap);
                    Bitmap bitmap = tileMap.getWebTileMap(x, y, level, tileRootPath);
                    Matrix matrix = new Matrix();
                    matrix.postTranslate(256 * (x - fromX), 256 * (y - fromY));
                    canvas.drawBitmap(bitmap, matrix, null);
                    System.gc();
                    bitmap=null;
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            });
        }
        System.gc();
        bitmap=null;
    }

    public void drawTileMap(){
        double s=0;
        int i=1;
        for(;i<=18;i++){
            s=Resolution.Resolution(i);
            if((ExternalBox.xMax-ExternalBox.xMin)/(s*256)>2||i==18)break;
        }
        final int level=i;
        final int fromX = (int)Math.ceil((180 + ExternalBox.getxMin())/(s*256))-1;
        final int fromY = (int)Math.ceil((90-ExternalBox.getyMax())/(s*256))-1;
        final int toX = (int)Math.ceil((180 + ExternalBox.getxMax())/(s*256))-1;
        final int toY = (int)Math.ceil((90-ExternalBox.getyMin())/(s*256))-1;
        tileFromToandLevel=new TileFromToandLevel(fromX,fromY,toX,toY,level);
        this.BitTileMap= Bitmap.createBitmap(256*(toX-fromX+1), 256*(toY-fromY+1), Bitmap.Config.ARGB_8888);
        for(x=fromX;x<=toX;x++){
            for(y=fromY;y<=toY;y++){
                Log.d(TAG, "drawTileMap: fsfd");
                drawTile(x,y,level,tileRootPath,fromX,fromY);
            }
        }
    }

    public void setTileMatrix() {
        Box tilebox=new Box(
                tileFromToandLevel.fromX*Resolution.Resolution(tileFromToandLevel.level)*256-180,
                90-(tileFromToandLevel.toY+1)*Resolution.Resolution(tileFromToandLevel.level)*256,
                (tileFromToandLevel.toX+1)*Resolution.Resolution(tileFromToandLevel.level)*256-180,
                90-tileFromToandLevel.fromY*Resolution.Resolution(tileFromToandLevel.level)*256
        );
        Log.e(TAG, "setTileMatrix: "+tilebox.getxMin()+","+ExternalBox.xMin);
        tilebox=new Box(
                Reverse.GeoXToCanvasX(ExternalBox,canvasWidth,canvasHeight,tilebox.xMin),
                Reverse.GeoYToCanvasY(ExternalBox,canvasWidth,canvasHeight,tilebox.yMax),
                Reverse.GeoXToCanvasX(ExternalBox,canvasWidth,canvasHeight,tilebox.xMax),
                Reverse.GeoYToCanvasY(ExternalBox,canvasWidth,canvasHeight,tilebox.yMin)
        );
        float scaleX=(float) ((tilebox.xMax-tilebox.xMin)/BitTileMap.getWidth());
        float scaleY=(float)((tilebox.yMax-tilebox.yMin)/BitTileMap.getHeight());
        tileMatrix.setScale(scaleX,scaleY);
        tileMatrix.postTranslate((float)tilebox.xMin,(float)tilebox.yMin);
        Log.e(TAG, "setTileMatrix: "+tilebox.getxMin()+","+tilebox.getxMax());
    }

    @Override
    public void draw(Canvas canvas) {
        if(isFirst){
            canvasWidth=getMeasuredWidth();
            canvasHeight=getMeasuredHeight();
            cmWidth=Px2Cm(context,canvasWidth);
            cmHeight=Px2Cm(context,canvasHeight);
            double resolution=Resolution.Resolution(iniLevel);
            double xmin=centerX-(canvasWidth/2)*resolution;
            double xmax=centerX+(canvasWidth/2)*resolution;
            double ymin=centerY-(canvasHeight/2)*resolution;
            double ymax=centerY+(canvasHeight/2)*resolution;
            ExternalBox = new Box(xmin,ymin,xmax,ymax);
            isFirst=false;
        }
        if(isup){
            drawTileMap();
            setTileMatrix();
            drawshapefile();
            matrix=new Matrix();
            isup=false;
        }
        canvas.drawBitmap(BitTileMap,tileMatrix,null);
        for (Bitmap viewBitmap:
                viewBitmaps) {
            canvas.drawBitmap(viewBitmap,matrix,null);
        }
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawCircle((float)sp.x,(float)sp.y,10,paint);
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            PointFlag=true;
            sp.x=event.getX();
            preX=event.getX();
            sp.y=event.getY();
            preY=event.getY();
        }else if(event.getAction()== MotionEvent.ACTION_POINTER_2_DOWN){
            PointFlag=false;
            dis = (float) Math.sqrt(Math.pow(event.getX(0) - event.getX(1),2)+ Math.pow(event.getY(0)-event.getY(1),2));
            preX=0.0f;
            preY=0.0f;
            midX=(event.getX(0)+event.getX(1))/2;
            midY=(event.getY(0)+event.getY(1))/2;
        }else if(event.getAction()== MotionEvent.ACTION_MOVE) {
            if (PointFlag && event.getPointerCount() == 1) {
                float x = event.getX();
                float y = event.getY();
                disx = x - (float) sp.x;
                disy = y - (float) sp.y;
                sp.x = x;
                sp.y = y;
                double trueX =  (disx*(ExternalBox.getxMax() - ExternalBox.getxMin()) / (float) getMeasuredWidth());
                double trueY = (disy*(ExternalBox.getyMax() - ExternalBox.getyMin()) / (float) getMeasuredHeight());
                ExternalBox.xMin -= trueX;
                ExternalBox.xMax -= trueX;
                ExternalBox.yMin += trueY;
                ExternalBox.yMax += trueY;
                matrix.postTranslate(disx, disy);
                tileMatrix.postTranslate(disx,disy);
                invalidate();
            } else if (!PointFlag && event.getPointerCount() == 2) {
                float nodis = (float) Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                porprotion = nodis / dis;
                dis = nodis;
                double X1 = midX * ((ExternalBox.getxMax() - ExternalBox.getxMin()) /  getMeasuredWidth());
                double X2 = (ExternalBox.getxMax() - ExternalBox.getxMin()) - X1;
                double Y2 =  midY * ((ExternalBox.getyMax() - ExternalBox.getyMin()) / getMeasuredHeight());
                double Y1 = (ExternalBox.getyMax() - ExternalBox.getyMin()) - Y2;
                ExternalBox.xMin = ExternalBox.xMin + X1 - X1 /  porprotion;
                ExternalBox.xMax = ExternalBox.xMax - X2+ X2 /  porprotion;
                ExternalBox.yMin = ExternalBox.yMin + Y1 - Y1 /  porprotion;
                ExternalBox.yMax = ExternalBox.yMax - Y2 + Y2 /  porprotion;
                matrix.postScale((float) porprotion, (float) porprotion, (float) midX, (float) midY);//将位图通过matrix进行放大
                tileMatrix.postScale((float) porprotion, (float) porprotion, (float) midX, (float) midY);
                invalidate();
            }
        }else if(event.getAction()== MotionEvent.ACTION_UP) {
            if(Math.abs(sp.x-preX)<=3&&Math.abs(sp.y-preY)<=3){
                Spot x=Reverse.CanvasPointToGeoPoint(ExternalBox,canvasWidth,canvasHeight,sp);
                Toast.makeText(context,"选中小班数：30个",Toast.LENGTH_LONG).show();
                isClick=true;
                ClickSpot=Reverse.CanvasPointToGeoPoint(ExternalBox,canvasWidth,canvasHeight,new Spot(sp.x,sp.y));
            }
            invalidate();
            isup=true;
        }
        return true;
    }
    public static int PxTodp(Context context,float value){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(value/scale+0.5f);
    }
    public static float Px2Cm(Context context,int value){
        return 0.015875f*PxTodp(context,value);
    }
}

