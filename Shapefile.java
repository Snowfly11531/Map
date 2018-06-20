package com.njfu.huangxiao.map.shapefile;

import java.io.BufferedInputStream;
import java.util.List;

public interface Shapefile {
    void create();
    int getType();
    interface ShapefilePart{}
    void setLimitScale(double limitScale);
    double getLimitScale();
    Box getGlobalBox();
}
