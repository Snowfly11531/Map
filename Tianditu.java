package com.njfu.huangxiao.map.shapefile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Tianditu implements TileMap {
    @Override
    public Bitmap getLocalTileMap(int x, int y, int level, String rootPath) {
        Bitmap bitmap=null;
        String Path=rootPath+"/tile/" + level + "/" + x + "/" + y + "/tile.png";
        File pngfile=new File(Path);
        if(pngfile.exists()) {
            BufferedInputStream bufferedInputStream=null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(pngfile));
            }catch (IOException e){

            }
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            try {
                bufferedInputStream.close();
            }catch (IOException e){

            }
            return bitmap;
        }else{
            return bitmap;
        }
    }

    @Override
    public Bitmap getWebTileMap(int x, int y, int level, String rootPath) {
        Bitmap bitmap;
        final String url = "http://t0.tianditu.com/DataServer?T=img_c&x=" + x + "&y=" + y + "&l=" + level;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        byte[] bytes=null;
        try {
            Response response = client.newCall(request).execute();
            bytes=response.body().bytes();
        }catch (IOException e){
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        File file = new File(rootPath);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = new File(rootPath+"/tile/");
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = new File(rootPath+"/tile/" + level);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = new File(rootPath+"/tile/" + level + "/" + x);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = new File(rootPath+"/tile/" + level + "/" + x + "/" + y);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        file = new File(rootPath+"/tile/" + level + "/" + x + "/" + y + "/tile.png");
        if (!file.exists()) {
            try {
                file.createNewFile();
                try(FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    @Override
    public Bitmap getTileMap(final int x,final int y,final int level,String rootPath) {
        Bitmap bitmap;
        String Path=rootPath+"/x/y/level/tile.png";
        File pngfile=new File(Path);
        if(pngfile.exists()){
            bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/datas/tile/"+level+"/"+x+"/"+y+"/tile.png");
            return bitmap;
        }else {
            final String url = "http://t0.tianditu.com/DataServer?T=img_c&x=" + x + "&y=" + y + "&l=" + level;
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url(url).build();
            byte[] bytes=null;
            try {
                Response response = client.newCall(request).execute();
                bytes=response.body().bytes();
            }catch (IOException e){
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            File file = new File(Environment.getExternalStorageDirectory() + "/datas/");
            if (!file.isDirectory()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/datas/tile/");
            if (!file.isDirectory()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/datas/tile/" + level);
            if (!file.isDirectory()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/datas/tile/" + level + "/" + x);
            if (!file.isDirectory()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/datas/tile/" + level + "/" + x + "/" + y);
            if (!file.isDirectory()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/datas/tile/" + level + "/" + x + "/" + y + "/tile.png");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
    }
}
