package com.example.apitest;

import android.graphics.Bitmap;
import android.util.LruCache;


public class LruBitmapCache extends LruCache<String, Bitmap> {


    public static int getDefaultLruCacheSize(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;
        return cacheSize;
    }

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public LruBitmapCache(){
        this(getDefaultLruCacheSize());
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return  value.getRowBytes()* value.getHeight()/1024;
    }

    public Bitmap getBitmap(String url){
        return get(url);
    }

    public void  putBitmap(String url,Bitmap bitmap){
        put(url,bitmap);
    }


}
