package com.njfu.huangxiao.map.shapefile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Point implements Shapefile {
    private Box Globalbox;
    private BufferedInputStream shapefileStream;
    private List<PointPart> points=new ArrayList<>();
    private double limitScale=0;
    public List<PointPart> getPoints() {
        return points;
    }

    public class PointPart implements ShapefilePart{
        private Spot spot;

        public Spot getSpot() {
            return spot;
        }
    }


    public Point(BufferedInputStream shapefileStream, Box GlobalBox){
        this.Globalbox=GlobalBox;
        this.shapefileStream=shapefileStream;
        create();
    }

    @Override
    public void setLimitScale(double limitScale) {
        this.limitScale=limitScale;
    }

    @Override
    public double getLimitScale() {
        return limitScale;
    }

    @Override
    public Box getGlobalBox() {
        return Globalbox;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void create() {
        try {
            Log.d(TAG, "create: 点开始读取");
            while (shapefileStream.available() > 0) {
                PointPart pointPart=new PointPart();
                pointPart.spot=readpoint(shapefileStream);
                points.add(pointPart);
            }
            Log.d(TAG, "create: 点读取完毕");
        }catch (IOException e){

        }finally {
            try {
                shapefileStream.close();
            }catch (IOException e){
                Log.e(TAG, "create: 文件关闭失败");
            }
        }
    }

    private Spot readpoint(BufferedInputStream shapefileStream){
        Reader.read4(shapefileStream);
        Reader.read4(shapefileStream);
        int type=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        double x=Reader.bytesToDouble(Reader.read8(shapefileStream));
        double y=Reader.bytesToDouble(Reader.read8(shapefileStream));
        return new Spot(x,y);
    }
}
