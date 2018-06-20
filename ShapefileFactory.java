package com.njfu.huangxiao.map.shapefile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class ShapefileFactory{
    public static Box GlobalBox;
    public static int Type;
    private static final int Point=1;
    private static final int Polyline=3;
    private static final int polygon=5;
    public Shapefile read(String path) {
        BufferedInputStream shapefileStream;
        try {
            InputStream inputStream = new FileInputStream(path);
            shapefileStream = new BufferedInputStream(inputStream);
        }catch (IOException e){
            Log.e(TAG, "read:haha ");
            e.printStackTrace();
            shapefileStream=null;
        }
        if(shapefileStream!=null) {
            Log.d(TAG, "开始读取数据类型");
            try {
                for (int i = 0; i < 32; i++) {
                    shapefileStream.read();
                }
                Type=Reader.bytesToIntLittle(Reader.read4(shapefileStream));
                double[] box=new double[4];
                for(int i=0;i<4;i++){
                    box[i]=Reader.bytesToDouble(Reader.read8(shapefileStream));
                }
                GlobalBox=new Box(box[0],box[1],box[2],box[3]);
                for(int i=68;i<100;i++){
                    shapefileStream.read();
                }
                switch (Type){
                    case Point:
                        Log.d(TAG, "点类型");
                        return new Point(shapefileStream,GlobalBox);
                    case Polyline:
                        Log.d(TAG, "线类型");
                        return new Polyline(shapefileStream,GlobalBox);
                    case polygon:
                        Log.d(TAG, "面类型");
                        return new Polygone(shapefileStream,GlobalBox);
                    default:
                        Log.d(TAG, "未了解类型");
                        return null;
                }
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
