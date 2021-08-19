package com.eju.cy.audiovideo.dto;

public class ImageMsgDto {

    private  String previewImagePath; //预览图片地址
    private  String  originalImagePath;//原始图片地址


    public String getPreviewImagePath() {
        return previewImagePath;
    }

    public void setPreviewImagePath(String previewImagePath) {
        this.previewImagePath = previewImagePath;
    }

    public String getOriginalImagePath() {
        return originalImagePath;
    }

    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }
}
