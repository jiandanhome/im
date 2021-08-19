package com.eju.cy.audiovideo.component.picture.imageEngine.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.ImageEngine;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * {@link ImageEngine} implementation using Picasso.
 * <p>
 * Picasso.with(TUIKit.getAppContext())
 */

public class PicassoEngine implements ImageEngine {


    /**
     * 加载圆角图片
     *
     * @param imageView
     * @param filePath
     * @param radius
     */
    public static void loadCornerImage(ImageView imageView, String filePath, final float radius) {
        Picasso.get().load(filePath).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

                Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), radius, radius, paint);
                if (source != output) {
                    source.recycle();
                }
                return output;
            }

            @Override
            public String key() {
                return "circle";
            }
        }).into(imageView);
    }


    public static void loadImage(ImageView imageView, String uri) {
        if (uri == null || "".equals(uri)) {
            return;
        }
        Picasso.get()
                .load(uri)
                .into(imageView);
    }


    public static void clear(ImageView imageView) {
        Picasso.get().cancelRequest(imageView);
    }


    public static void loadImage(ImageView imageView, Uri uri) {
        if (uri == null || "".equals(uri)) {
            return;
        }
        Picasso.get()
                .load(uri)
                .error(R.drawable.default_user_icon)
                .into(imageView);
    }


    public static void loadImageEror(ImageView imageView, String uri) {
        if (uri == null || "".equals(uri)) {
            return;
        }
        Picasso.get()
                .load(uri)
                .error(R.drawable.default_head)
                .into(imageView);
    }

    public static void loadImageEror(ImageView imageView, Object uri) {
        if (uri == null || "".equals(uri)) {
            return;
        }

        if (uri instanceof String) {
            String imgUrl = (String) uri;

            Picasso.get()
                    .load(imgUrl)
                    .error(R.drawable.default_head)
                    .into(imageView);
        }

        if (uri instanceof Integer) {
            int imgUrl = (int) uri;

            Picasso.get()
                    .load(imgUrl)
                    .error(R.drawable.default_head)
                    .into(imageView);
        }


    }


    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Picasso.get().load(uri).placeholder(placeholder)
                .resize(resize, resize)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 Uri uri) {
        loadThumbnail(context, resize, placeholder, imageView, uri);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Picasso.get().load(uri).resize(resizeX, resizeY).priority(Picasso.Priority.HIGH)
                .centerInside().into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        loadImage(context, resizeX, resizeY, imageView, uri);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
