package com.njfu.huangxiao.map.shapefile;

import android.util.Log;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;

public class Indexes {
    private static Boolean IsInternal(Spot spot, Spot[] spots){
        int count=0;
        for(int i=0;i<spots.length-1;i++){
            if((spots[i].y > spot.y && spots[i+1].y < spot.y)||(spots[i].y < spot.y && spots[i+1].y > spot.y)){
                if(spots[i].x<spot.x&&spots[i+1].x<spot.x){

                }else if(spots[i].x>spot.x&&spots[i+1].x>spot.x){
                    count++;
                }else{
                    double x1= spots[i].x;
                    double y1= spots[i].y;
                    double x2= spots[i+1].x;
                    double y2= spots[i+1].y;
                    double y= spot.y;
                    double x=x1+(x2-x1)*(y-y1)/(y2-y1);
                    if(x>spot.x){
                        count++;
                    }
                }
            }
        }
        if(count%2==0){
            return false;
        }else{
            return true;
        }
    }
    public static int pointIndexes(Shapefile shapefile,List<Integer> lists,Spot touchSpot,
                                   Box ExternalBox,int canvasWidth,int canvasHeight){ //Externalox应该为地理坐标，touchspot也应为地理坐标
        int index=-1;
        switch (shapefile.getType()) {
            case 5:
                Polygone polygone=(Polygone)shapefile;
                List<Polygone.PolygonePart> polygoneParts=polygone.getPolygoneParts();
                for(int i=0;i<lists.size();i++) {
                    Polygone.PolygonePart polygonePart = polygoneParts.get(lists.get(i));
                    Box box=polygonePart.getPolygonePartsBox();
                    if(box.getxMin()<= touchSpot.x&&box.getxMax()>= touchSpot.x&&
                            box.getyMin()<= touchSpot.y&&box.getyMax()>= touchSpot.y) {
                        if (IsInternal(touchSpot, polygonePart.getSpots())) {
                            index = lists.get(i);
                            break;
                        }
                    }
                }
                return index;
            case 3:
                Polyline polyline=(Polyline)shapefile;
                List<Polyline.PolylinePart> polylineParts=polyline.getPolylineParts();
                touchSpot=Reverse.GeoPointToCanvasPoint(ExternalBox,canvasWidth,canvasHeight,touchSpot);
                double pointToLineMinDistance=50;
                for(int i=0;i<lists.size();i++){
                    Polyline.PolylinePart polylinePart=polylineParts.get(lists.get(i));
                    Spot[] spots=polylinePart.getSpots();
                    spots=ReverseSpots(ExternalBox,canvasWidth,canvasHeight,spots);
                    if(pointToLineDis(touchSpot,spots)<pointToLineMinDistance){
                        pointToLineMinDistance=pointToLineDis(touchSpot,spots);
                        index=lists.get(i);
                    }
                }
                return index;
            case 1:
                Point point=(Point)shapefile;
                List<Point.PointPart> pointParts=point.getPoints();
                touchSpot=Reverse.GeoPointToCanvasPoint(ExternalBox,canvasWidth,canvasHeight,touchSpot);
                Spot spot0=pointParts.get(0).getSpot();
                spot0=Reverse.GeoPointToCanvasPoint(ExternalBox,canvasWidth,canvasHeight,spot0);
                double minDistance=Math.sqrt(Math.pow((touchSpot.x-spot0.x),2)+Math.pow((touchSpot.y-spot0.y),2));
                index=0;
                for(int i=1;i<lists.size();i++){
                    Spot spot=pointParts.get(lists.get(i)).getSpot();
                    spot=Reverse.GeoPointToCanvasPoint(ExternalBox,canvasWidth,canvasHeight,spot);
                    double distance=Math.sqrt(Math.pow((touchSpot.x-spot.x),2)+Math.pow((touchSpot.y-spot.y),2));
                    minDistance=minDistance<distance ? minDistance : distance;
                    index=minDistance<distance ? index :i;
                }
                if(minDistance>50){
                    index=-1;
                }
                return index;
            default:
                return index;
        }
    }
    public static List<Integer> rectIndexes(Shapefile shapefile,Box box){ //box为地理坐标
        List<Integer> lists=new ArrayList<>();
        switch (shapefile.getType()){
            case 5:
                Polygone polygone=(Polygone)shapefile;
                List<Polygone.PolygonePart> polygoneParts=polygone.getPolygoneParts();
                for(int i=0;i<polygoneParts.size();i++){
                    Box part_box=polygoneParts.get(i).getPolygonePartsBox();
                    if(!(part_box.xMax<=box.xMin||
                            part_box.xMin>=box.xMax||
                            part_box.yMax<=box.yMin||
                            part_box.yMin>=box.yMax)){
                        lists.add(i);
                    }
                }
                return lists;
            case 3:
                Polyline polyline=(Polyline) shapefile;
                List<Polyline.PolylinePart> polylineParts=polyline.getPolylineParts();
                for(int i=0;i<polylineParts.size();i++){
                    Box part_box=polylineParts.get(i).getPolylinePartsBox();
                    if(!(part_box.xMax<=box.xMin||
                            part_box.xMin>=box.xMax||
                            part_box.yMax<=box.yMin||
                            part_box.yMin>=box.yMax)){
                        lists.add(i);
                    }
                }
                return lists;
            case 1:
                Point point=(Point)shapefile;
                List<Point.PointPart> pointParts=point.getPoints();
                for (int i=0;i<pointParts.size();i++){
                    Spot spot=pointParts.get(i).getSpot();
                    if(spot.x>=box.xMin&&
                            spot.x<=box.xMax&&
                            spot.y>=box.yMin&&
                            spot.y<=box.yMax){
                        lists.add(i);
                    }
                }
                return lists;
            default:
                return lists;
        }
    }
    public static Spot[] ReverseSpots(Box ExternalBox,int canvasWidth,int canvasHeight,Spot[] spots){
        Spot[] spots1=new Spot[spots.length];
        int i=0;
        for (Spot spot:
                spots) {
            spots1[i]=Reverse.GeoPointToCanvasPoint(ExternalBox,canvasWidth,canvasHeight,spot);
            i++;
        }
        return spots1;
    }
    private static double pointToLineDis(Spot touchSpot,Spot[] spots){
        double minDistance=50;
        for(int i=0;i<spots.length-1;i++){
            if(pointToLine(spots[i].x,spots[i].y,spots[i+1].x,spots[i+1].y,
                    touchSpot.x,touchSpot.y)<minDistance){
                minDistance=pointToLine(spots[i].x,spots[i].y,spots[i+1].x,spots[i+1].y, touchSpot.x,touchSpot.y);
            }
        }
        return minDistance;
    }
    private static double lineSpace(double x1,double y1,double x2,double y2){
        return Math.sqrt(Math.pow((y2-y1),2)+Math.pow((x2-x1),2));
    }
    private static double pointToLine(double x1, double y1, double x2, double y2, double x0,
                              double y0) {
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }
}
