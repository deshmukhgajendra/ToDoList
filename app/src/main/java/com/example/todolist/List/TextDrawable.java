package com.example.todolist.List;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;

    public class TextDrawable extends Drawable {
        private final Paint paint;
        private final String text;

        public TextDrawable(String text) {
            this.text = text;
            paint = new Paint();
            paint.setColor(Color.BLACK); // Set text color
            paint.setTextSize(64); // Set text size
            paint.setAntiAlias(true);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawText(text, 0, paint.getTextSize(), paint);
        }

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {}

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (paint.measureText(text) + 0.5f);
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) (paint.getTextSize() + 0.5f);
        }
    }


