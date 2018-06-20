package com.njfu.huangxiao.map.shapefile;

/**
 * Created by snowfly on 18-2-5.
 */

public class Resolution {
    public static double Resolution(int level){
        switch (level){
            case 1:
                return (0.70312500015485435);
            case 2:
                return  (0.35156250007742718);
            case 3:
                return  (0.17578125003871359);
            case 4:
                return  (0.0878906250193568);
            case 5:
                return  (0.0439453125096784);
            case 6:
                return  (0.0219726562548392);
            case 7:
                return (0.0109863281274196);
            case 8:
                return  (0.0054931640637098);
            case 9:
                return (0.0027465820318549957);
            case 10:
                return  (0.0013732910159274978);
            case 11:
                return  (0.00068664549607834132);
            case 12:
                return  (0.00034332275992416907);
            case 13:
                return  (0.00017166136807812298);
            case 14:
                return  (8.5830684039061379E-05);
            case 15:
                return  (4.2915342019530649E-05);
            case 16:
                return  (2.1457682893727977E-05);
            case 17:
                return  (1.0728841446864E-05);
            case 18:
                return  (5.3644207234319882E-06);
            case 19:
                return  (2.6822103617159941E-06);
            case 20:
                return  (1.341105180858E-06);
            default:
                return  (0.0);
        }
    }
}
