package com.njfu.huangxiao.map.shapefile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Polyline implements Shapefile {
    private Box Globalbox;
    private BufferedInputStream shapefileStream;
    private List<Polyline.PolylinePart> polylineParts=new ArrayList<>();
    private double limitScale=0;
    public List<Polyline.PolylinePart> getPolylineParts(){
        return polylineParts;
    }

    public class PolylinePart implements ShapefilePart{
        private Box PolylinePartsBox;
        private int NumParts;
        private int NumPoints;
        private Spot[] spots;
        private int[] parts;
        public Box getPolylinePartsBox(){
            return PolylinePartsBox;
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

    @Override
    public void setLimitScale(double limitScale) {
        this.limitScale = limitScale;
    }

    @Override
    public double getLimitScale() {
        return limitScale;
    }

    public Polyline(BufferedInputStream shapefileStream, Box GlobalBox){
        this.Globalbox=GlobalBox;
        this.shapefileStream=shapefileStream;
        create();
    }

    @Override
    public Box getGlobalBox() {
        return Globalbox;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public void create() {
        try {
            Log.d(TAG, "create: 线开始读取");
            while (shapefileStream.available()>0) {
                polylineParts.add(readPart(shapefileStream));
            }
            Log.d(TAG, "create: 线读取完毕");
        }catch (IOException e){

        }finally {
            try {
                shapefileStream.close();
            }catch (IOException e){
                Log.d(TAG, "create: 文件关闭失败");
            }
        }
    }
    public PolylinePart readPart(BufferedInputStream shapefileStream){
        Reader.read4(shapefileStream);
        Reader.read4(shapefileStream);
        int type=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        PolylinePart polylinePart=new PolylinePart();
        double[] box=new double[4];
        for(int i=0;i<4;i++){
            box[i]=Reader.bytesToDouble(Reader.read8(shapefileStream));
        }
        polylinePart.PolylinePartsBox=new Box(box[0],box[1],box[2],box[3]);
        polylinePart.NumParts=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        polylinePart.NumPoints=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        polylinePart.parts=new int[polylinePart.getNumParts()+1];
        int i=0;
        for(i=0;i<polylinePart.getNumParts();i++){
            polylinePart.parts[i]=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
        }
        polylinePart.parts[i]=polylinePart.getNumPoints();
        polylinePart.spots=new Spot[polylinePart.NumPoints];
        for(i=0;i<polylinePart.getNumPoints();i++){
            double x=Reader.bytesToDouble(Reader.read8(shapefileStream));
            double y=Reader.bytesToDouble(Reader.read8(shapefileStream));
            polylinePart.spots[i]=new Spot(x,y);
        }
        return polylinePart;
    }
}
