package com.njfu.huangxiao.map.shapefile;

import android.graphics.Bitmap;

public interface TileMap {
    public Bitmap getTileMap(int x,int y,int level,String rootPath);
    public Bitmap getWebTileMap(int x,int y,int level,String rootPath);
    public Bitmap getLocalTileMap(int x,int y,int level,String rootPath);
}
