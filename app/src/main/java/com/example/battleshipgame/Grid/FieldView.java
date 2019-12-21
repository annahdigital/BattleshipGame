package com.example.battleshipgame.Grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.battleshipgame.Models.CellMode;
import com.example.battleshipgame.Models.Field;
import com.example.battleshipgame.R;

public class FieldView extends View {
    private int cellWidth;
    private int cellHeight;

    private Paint PurplePaint;
    private Paint RedPaint;
    private Paint GreyishPaint;
    private Paint FieldGridPaint;

    private Field field;
    private CurrentFieldMode fieldMode;
    private Context context;

    public FieldView(Context context)
    {
        super(context, null);
        this.context = context;

    }

    public FieldView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        this.context = context;
        PurplePaint = new Paint();
        PurplePaint.setColor(ContextCompat.getColor(context, R.color.ship_color));
        RedPaint = new Paint();
        RedPaint.setColor(ContextCompat.getColor(context,R.color.attacked_red));
        GreyishPaint = new Paint();
        GreyishPaint.setColor(ContextCompat.getColor(context,R.color.missed_greyish));
        FieldGridPaint = new Paint();
        FieldGridPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDarkest));
    }

    public void createField()
    {
        field = new Field();
        this.fieldMode = CurrentFieldMode.CREATION;
    }

    public void setFieldMode(CurrentFieldMode mode)
    {   this.fieldMode = mode;  }

    public void updateField(Field field)
    {
        this.field = field;
        invalidate();
    }

    public void setField(Field field, CurrentFieldMode mode)
    {
        this.fieldMode = mode;
        this.field = field;
    }

    public Field getField()
    {   return field;   }


    private void getSizes()
    {
        cellHeight = getHeight() / field.height;
        cellWidth = getWidth() / field.width;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int previous_height, int previous_width)
    {
        super.onSizeChanged(width, height, previous_height, previous_width);
        getSizes();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);
        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < field.height; i++)
        {
            for (int j = 0; j < field.width; j++)
            {
                if (field.getCell(i, j) == CellMode.SHIP)
                {
                    if (fieldMode != CurrentFieldMode.OPPONENT)
                    {
                        canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, PurplePaint);
                    }
                }
                else if (field.getCell(j, i) == CellMode.MISS)
                {
                    canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, GreyishPaint);
                }
                else if (field.getCell(j, i) == CellMode.HIT)
                {
                    canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, RedPaint);
                }
            }
        }

        for (int i = 1; i < field.width; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, FieldGridPaint);
        }

        for (int i = 1; i < field.height; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, FieldGridPaint);
        }

        canvas.drawLine(0, 0, 0, height, FieldGridPaint);
        canvas.drawLine(width, 0, width, height, FieldGridPaint);
        canvas.drawLine(0, 0, width, 0, FieldGridPaint);
        canvas.drawLine(0, height, width, height, FieldGridPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (fieldMode != CurrentFieldMode.READONLY && event.getAction() == MotionEvent.ACTION_DOWN) {

            int x = (int) event.getX() / cellWidth;
            int y = (int) event.getY() / cellHeight;

            if (fieldMode == CurrentFieldMode.CREATION) {
                if (field.getCell(x, y) == CellMode.EMPTY)
                {
                    field.setCellMode(CellMode.SHIP, x, y);
                }
                else
                    field.setCellMode(CellMode.EMPTY, x, y);

            }
            else {
                if (field.getCell(x, y) == CellMode.EMPTY)
                {
                    field.setCellMode(CellMode.MISS, x, y);
                    //updategrid
                }
                else if (field.getCell(x, y) == CellMode.SHIP)
                {
                    field.setCellMode(CellMode.HIT, x, y);
                    //updategrid
                }

            }
        }
        invalidate();
        return true;
    }



}
