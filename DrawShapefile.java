package com.njfu.huangxiao.map.shapefile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DrawShapefile {
    private int type;
    private Shapefile shapefile;
    private int canvasWidth;
    private int canvasHeight;
    private Box ExternalBox;
    private Boolean isChoose=false;
    private Spot ClickPoint;
    private double scale;
    public DrawShapefile(Shapefile shapefile, Box ExternalBox,int canvasWidth,int canvasHeight,double scale){
        this.ExternalBox=ExternalBox;
        this.shapefile=shapefile;
        this.type=getType(shapefile);
        this.canvasHeight=canvasHeight;
        this.canvasWidth=canvasWidth;
        this.scale=scale;
    }
    public DrawShapefile(Shapefile shapefile,Box ExternalBox,int canvasWidth,int canvasHeight,double scale,Spot ClickPoint){
        this.ExternalBox=ExternalBox;
        this.shapefile=shapefile;
        this.type=getType(shapefile);
        this.canvasHeight=canvasHeight;
        this.canvasWidth=canvasWidth;
        isChoose=true;
        this.ClickPoint=ClickPoint;
        this.scale=scale;
    }
    private int getType(Shapefile shapefile){
        return shapefile.getType();
    }
    public Bitmap draw(){
        switch (this.type){
            case 1:
                Log.d(TAG, "draw: 开始画点");
                return drawPoint();
            case 3:
                Log.d(TAG, "draw: 开始画线");
                return drawPolyline();
            case 5:
                Log.d(TAG, "draw: 开始画面");
                return drawPolygone();
            default:
                return null;
        }
    }
    public Bitmap drawPolyline(){
        if(scale<shapefile.getLimitScale()||shapefile.getLimitScale()==0) {
            Polyline polyline=(Polyline) shapefile;
            List<Polyline.PolylinePart> polylineParts=polyline.getPolylineParts();
            List<Integer> lists=Indexes.rectIndexes(shapefile,ExternalBox);
            Bitmap viewBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(viewBitmap);
            Path path = new Path();
            Paint paint = setPaint(Color.RED, Paint.Style.STROKE, 8);
            for (int i = 0; i < lists.size(); i++) {
                Polyline.PolylinePart polylinePart = polylineParts.get(lists.get(i));
                int[] part = polylinePart.getParts();
                path = setPath(path, polylinePart);
            }
            canvas.drawPath(path, paint);
            paint=setPaint(Color.YELLOW, Paint.Style.STROKE, 5);
            canvas.drawPath(path,paint);
            if(isChoose){
                int Clickid=Indexes.pointIndexes(shapefile,lists,ClickPoint,ExternalBox,canvasWidth,canvasHeight);
                Log.d(TAG, "drawPolyline:"+Clickid);
                if(Clickid!=-1){
                    Path hightLightPath=new Path();
                    paint=setPaint(Color.GREEN, Paint.Style.STROKE,12);
                    Polyline.PolylinePart polylinePart=polylineParts.get(Clickid);
                    hightLightPath=setPath(hightLightPath,polylinePart);
                    canvas.drawPath(hightLightPath,paint);
                }
            }
            return viewBitmap;
        }else{
            return null;
        }
    }
    public Bitmap drawPolygone(){
        if(scale<shapefile.getLimitScale()||shapefile.getLimitScale()==0){
            Polygone polygone=(Polygone)shapefile;
            List<Polygone.PolygonePart> polygoneParts=polygone.getPolygoneParts();
            List<Integer> lists=Indexes.rectIndexes(shapefile,ExternalBox);
            Bitmap viewBitmap=Bitmap.createBitmap(canvasWidth,canvasHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(viewBitmap);
            Path path=new Path();
            Paint paint=setPaint(Color.argb(150,0,0,255), Paint.Style.STROKE,5);
            for(int i=0;i<lists.size();i++){
                Polygone.PolygonePart polygonePart=polygoneParts.get(lists.get(i));
                int[] part=polygonePart.getParts();
                path=setPath(path,polygonePart,part);
            }
            canvas.drawPath(path,paint);
            if(isChoose){
                int Clickid=Indexes.pointIndexes(shapefile,lists,ClickPoint,ExternalBox,canvasWidth,canvasHeight);
                if(Clickid!=-1){
                    Path hightLightPath=new Path();
                    paint=setPaint(Color.GREEN, Paint.Style.STROKE,8);
                    Polygone.PolygonePart polygonePart=polygoneParts.get(Clickid);
                    int[] part=polygonePart.getParts();
                    hightLightPath=setPath(hightLightPath,polygonePart,part);
                    canvas.drawPath(hightLightPath,paint);
                }
            }
            return viewBitmap;
        }else{
            return null;
        }
    }
    public Bitmap drawPoint(){
        if(scale<shapefile.getLimitScale()||shapefile.getLimitScale()==0) {
            Bitmap viewBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(viewBitmap);
            Point point = (Point) this.shapefile;
            List<Spot> spots = new ArrayList<>();
            List<Integer> lists = Indexes.rectIndexes(shapefile, ExternalBox);
            for (int i = 0; i < lists.size(); i++) {
                Spot spot = point.getPoints().get(lists.get(i)).getSpot();
                spot = Reverse.GeoPointToCanvasPoint(ExternalBox, canvasWidth, canvasHeight, spot);
                spots.add(spot);
            }
            for (Spot spot :
                    spots) {
                canvas.drawCircle((float) spot.x, (float) spot.y, 8, setPaint(Color.BLUE, Paint.Style.FILL, 10));
            }
            if (isChoose) {
                int key = Indexes.pointIndexes(shapefile, lists, ClickPoint, ExternalBox, canvasWidth, canvasHeight);
                if (key != -1) {
                    canvas.drawCircle((float) spots.get(key).x, (float) spots.get(key).y, 13, setPaint(Color.GREEN, Paint.Style.FILL, 10));
                }
            }
            return viewBitmap;
        }else {
            return null;
        }
    }
    public Paint setPaint(int color, Paint.Style style,int width){
        Paint paint=new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        paint.setAntiAlias(true);
        return paint;
    }
    public Path setPath(Path path,Polygone.PolygonePart polygonePart,int[] part){
        for(int k=0;k<part.length-1;k++) {
            for (int j = part[k]; j < part[k+1]; j++) {
                Spot canvaspoint = Reverse.GeoPointToCanvasPoint(ExternalBox, canvasWidth, canvasHeight, polygonePart.getSpots()[j]);
                if (j == 0) {
                    path.moveTo((float) canvaspoint.x, (float) canvaspoint.y);
                    //Log.e(TAG, "setPath: "+canvaspoint.y);
                } else {
                    path.lineTo((float) canvaspoint.x, (float) canvaspoint.y);
                }
            }
        }
        return path;
    }
    public Path setPath(Path path, Polyline.PolylinePart polylinePart){
        for (int j = 0; j < polylinePart.getSpots().length; j++) {
            Spot canvaspoint = Reverse.GeoPointToCanvasPoint(ExternalBox, canvasWidth, canvasHeight, polylinePart.getSpots()[j]);
            if (j == 0) {
                path.moveTo((float) canvaspoint.x, (float) canvaspoint.y);
                //Log.e(TAG, "setPath: "+canvaspoint.y);
            } else {
                path.lineTo((float) canvaspoint.x, (float) canvaspoint.y);
            }
        }
        return path;
    }
}
