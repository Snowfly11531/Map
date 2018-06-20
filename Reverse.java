package com.njfu.huangxiao.map.shapefile;

public class Reverse {
    public static Spot GeoPointToCanvasPoint(Box box,int width,int height,Spot spot){
        double x=width*(spot.x-box.getxMin())/(box.getxMax()-box.getxMin());
        double y=height*(box.getyMax()-spot.y)/(box.getyMax()-box.getyMin());
        Spot CanvasPoint=new Spot(x,y);
        return CanvasPoint;
    }
    public static double GeoXToCanvasX(Box box,int width,int height,double x){
        return width*(x-box.getxMin())/(box.getxMax()-box.getxMin());
    }
    public static double GeoYToCanvasY(Box box,int width,int height,double y){
        return height*(box.getyMax()-y)/(box.getyMax()-box.getyMin());
    }
    public static Spot CanvasPointToGeoPoint(Box box,int width,int height,Spot spot){
        double x=(box.xMax-box.xMin)*spot.x/width+box.xMin;
        double y=box.yMax-(box.yMax-box.yMin)*spot.y/height;
        Spot GeoPoint=new Spot(x,y);
        return GeoPoint;
    }
}
