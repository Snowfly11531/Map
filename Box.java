package com.njfu.huangxiao.map.shapefile;

/**
 * Created by hasee on 2018/1/19.
 */

public class Box {
    public double xMin;
    public double yMin;
    public double xMax;
    public double yMax;
    public Box(double xMin,double yMin,double xMax,double yMax){
        this.xMax=xMax;
        this.xMin=xMin;
        this.yMax=yMax;
        this.yMin=yMin;
    }
    public double getxMin(){
        return this.xMin;
    }
    public double getxMax(){
        return this.xMax;
    }
    public double getyMin(){
        return this.yMin;
    }
    public double getyMax(){
        return this.yMax;
    }
}
