package com.example.projects.blogengine.utility;

public class ImageSizeConverter {
    public static long getImageSize(String size){
        long imageSize = 0;
        if (size.contains("KB")){
            imageSize = Integer.parseInt(size.substring(0, size.indexOf("KB"))) * 1024;
        } else if (size.contains("MB")){
            imageSize = Integer.parseInt(size.substring(0, size.indexOf("MB"))) * 1024 * 1024;
        } else {
            imageSize = Integer.parseInt(size);
        }
        return imageSize;
    }
}
