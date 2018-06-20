package com.njfu.huangxiao.map.shapefile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by hasee on 2018/1/20.
 */

public class Polygone implements Shapefile{
    private Box Globalbox;
    private BufferedInputStream shapefileStream;
    private List<PolygonePart> polygoneParts =new ArrayList<>();
    private double limitScale=0;

    public class PolygonePart implements ShapefilePart{
        private Box PolygonePartsBox;
        private int NumParts;
        private int NumPoints;
        private Spot[] spots;
        private int[] parts;
        public Box getPolygonePartsBox(){
            return PolygonePartsBox;
        }
        public int getNumParts(){
            return NumParts;
        }
        public int getNumPoints(){
            return NumPoints;
        }
        public Spot[] getSpots(){
            return spots;
        }
        public int[] getParts(){return parts;}
    }

    public Polygone(BufferedInputStream shapefileStream, Box GlobalBox){
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
       return 5;
    }

    public Box getGlobalbox() {
        return Globalbox;
    }

    public List<PolygonePart> getPolygoneParts() {
        return polygoneParts;
    }

    @Override
    public void create() {
        try {
            Log.d(TAG, "create: 面开始读取");
            while (shapefileStream.available()>0) {
                polygoneParts.add(readpart(shapefileStream));
            }
            Log.d(TAG, "create: 面读取完毕");
        }catch (IOException e){

        }finally {
            try {
                shapefileStream.close();
            }catch (IOException e){
                Log.d(TAG, "create: 文件关闭失败");
            }
        }
    }

    private PolygonePart readpart(BufferedInputStream shapefileStream){
        Reader.read4(shapefileStream);
        Reader.read4(shapefileStream);
        int type=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        PolygonePart polygonePart=new PolygonePart();
        double[] box=new double[4];
        for(int i=0;i<4;i++){
            box[i]=Reader.bytesToDouble(Reader.read8(shapefileStream));
        }
        polygonePart.PolygonePartsBox=new Box(box[0],box[1],box[2],box[3]);
        polygonePart.NumParts=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        polygonePart.NumPoints=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        polygonePart.parts=new int[polygonePart.getNumParts()+1];
        int i=0;
        for(i=0;i<polygonePart.getNumParts();i++){
            polygonePart.parts[i]=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        }
        polygonePart.parts[i]=polygonePart.getNumPoints();
        polygonePart.spots=new Spot[polygonePart.NumPoints];
        for(i=0;i<polygonePart.getNumPoints();i++){
            double x=Reader.bytesToDouble(Reader.read8(shapefileStream));
            double y=Reader.bytesToDouble(Reader.read8(shapefileStream));
            polygonePart.spots[i]=new Spot(x,y);
        }
        return polygonePart;
        //Log.e(TAG, "readpart:"+polygonePart.getNumPoints());
    }
}
