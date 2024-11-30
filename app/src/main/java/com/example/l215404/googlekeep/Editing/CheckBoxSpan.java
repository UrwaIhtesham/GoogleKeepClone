package com.example.l215404.googlekeep.Editing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class CheckBoxSpan extends ReplacementSpan {
    private boolean isChecked;

    public CheckBoxSpan(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText("[ ]"); // Placeholder width for checkbox
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        String checkbox = isChecked ? "[✔]" : "[ ]";
        canvas.drawText(checkbox, x, y, paint);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
