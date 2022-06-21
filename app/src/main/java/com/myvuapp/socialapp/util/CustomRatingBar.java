package com.myvuapp.socialapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;

import com.myvuapp.socialapp.R;

public class CustomRatingBar extends androidx.appcompat.widget.AppCompatRatingBar {

    @Nullable
    private Bitmap mSampleTile;

    public CustomRatingBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setProgressDrawable(createProgressDrawable());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mSampleTile != null) {
            final int width = mSampleTile.getWidth() * getNumStars();
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), getMeasuredHeight());
        }
    }

    protected LayerDrawable createProgressDrawable() {
        final Drawable backgroundDrawable = createBackgroundDrawableShape();
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                backgroundDrawable,
                backgroundDrawable,
                createProgressDrawableShape()
        });

        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.secondaryProgress);
        layerDrawable.setId(2, android.R.id.progress);
        return layerDrawable;
    }

    protected Drawable createBackgroundDrawableShape() {
        final Bitmap tileBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.ic_star));
        if (mSampleTile == null) {
            mSampleTile = tileBitmap;
        }
        final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        final BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable.getPaint().setShader(bitmapShader);
        return shapeDrawable;
    }

    protected Drawable createProgressDrawableShape() {
        final Bitmap tileBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.ic_star_fill));
        final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        final BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable.getPaint().setShader(bitmapShader);
        return new ClipDrawable(shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
    }

    Shape getDrawableShape() {
        final float[] roundedCorners = new float[]{5, 5, 5, 5, 5, 5, 5, 5};
        return new RoundRectShape(roundedCorners, null, null);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
