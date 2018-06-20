package com.njfu.huangxiao.map.shapefile;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Reader {
    public static int bytesToIntLittle(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16)
                | ((src[3] & 0xFF)<<24));
        return value;
    }
    public static int bytesToIntBig(byte[] src) {
        int value;
        value = (int) ( ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF));
        return value;
    }
    public static double bytesToDouble(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
    public static byte[] read4(BufferedInputStream input){
        byte[] bytes=new byte[4];
        for(int i=0;i<4;i++){
            try {
                bytes[i] = (byte) input.read();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return bytes;
    }
    public static byte[] read8(BufferedInputStream input){
        byte[] bytes=new byte[8];
        for(int i=0;i<8;i++){
            try {
                bytes[i] = (byte) input.read();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return bytes;
    }
    public int read4big(BufferedInputStream input){
        return bytesToIntBig(read4(input));
    }
    public int read4little(BufferedInputStream input){
        return bytesToIntLittle(read4(input));
    }
    public double read8Double(BufferedInputStream input){
        byte[] bytes=new byte[8];
        for(int i=0;i<8;i++){
            try{
                bytes[i]=(byte)input.read();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return bytesToDouble(bytes);
    }
}
