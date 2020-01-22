package com.example.battleshipgame.Grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.battleshipgame.Activities.GameActivity;
import com.example.battleshipgame.Models.CellMode;
import com.example.battleshipgame.Models.Field;
import com.example.battleshipgame.Models.MoveType;
import com.example.battleshipgame.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class FieldView extends View {

    private Paint PurplePaint;
    private Paint RedPaint;
    private Paint GreyishPaint;
    private Paint FieldGridPaint;
    private Paint GreyPaint;
    private Paint borderPaint;

    private int cellWidth;
    private int cellHeight;

    private Field field;
    private CurrentFieldMode fieldMode;
    private final Context context;

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
        GreyPaint = new Paint();
        GreyPaint.setColor(ContextCompat.getColor(context, R.color.colorGrey));
        borderPaint = new Paint();
        borderPaint.setStrokeWidth(7);
        borderPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDarkest));
    }

    public void createField()
    {
        field = new Field();
        this.fieldMode = CurrentFieldMode.CREATION;
    }

    public void initializeField(CurrentFieldMode mode)
    {
        field = new Field();
        this.fieldMode = mode;
    }

    public void setFieldMode(CurrentFieldMode mode)
    {   this.fieldMode = mode;  }


    public void updateField(Field field)
    {
        this.field = field;
        // the view's appearance may need to be changed, the view will call invalidate().
        // invalidate() were called, the framework will take care of measuring, laying out, and drawing the tree as appropriate.
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
                    if (fieldMode != CurrentFieldMode.OPPONENT && fieldMode != CurrentFieldMode.READONLY)
                    {
                        canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, PurplePaint);
                    }
                }
                else if (field.getCell(i, j) == CellMode.MISS)
                {
                    canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, GreyishPaint);
                    canvas.drawCircle(i  * cellWidth + 0.5f * cellWidth, j * cellHeight + 0.5f * cellHeight, cellWidth / 7, GreyPaint);
                }
                else if (field.getCell(i , j) == CellMode.HIT)
                {
                    canvas.drawRect(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight, RedPaint);
                    Drawable d = getResources().getDrawable(R.drawable.hit_fire, null);
                    d.setBounds(i*cellWidth, j * cellHeight, (i+1) * cellWidth, (j+1) * cellHeight);
                    d.draw(canvas);
                }
            }
        }

        for (int i = 1; i < field.width; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, FieldGridPaint);
        }

        for (int i = 1; i < field.height; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, FieldGridPaint);
        }

        canvas.drawLine(0, 0, 0, height, borderPaint);
        canvas.drawLine(width, 0, width, height, borderPaint);
        canvas.drawLine(0, 0, width, 0, borderPaint);
        canvas.drawLine(0, height, width, height, borderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (fieldMode != CurrentFieldMode.READONLY && event.getAction() == MotionEvent.ACTION_DOWN) {

            int x = (int) event.getX() / cellWidth;
            int y = (int) event.getY() / cellHeight;
            if (x < field.width && y < field.height) {

                if (fieldMode == CurrentFieldMode.CREATION) {
                    if (field.getCell(x, y) == CellMode.EMPTY) {
                        field.setCellMode(CellMode.SHIP, x, y);
                    } else
                        field.setCellMode(CellMode.EMPTY, x, y);
                    if (!new FieldChecker(field).checkField())
                        showError();

                } else if (fieldMode == CurrentFieldMode.OPPONENT){
                    if (field.getCell(x, y) == CellMode.EMPTY) {
                        field.setCellMode(CellMode.MISS, x, y);
                        ((GameActivity)this.context).updatingMove(MoveType.MISS);
                    } else if (field.getCell(x, y) == CellMode.SHIP) {
                        //new FieldChecker(field).checkIfShipIsKilled(x, y);
                        field.setCellMode(CellMode.HIT, x, y);
                        ((GameActivity)this.context).updatingMove(MoveType.HIT);
                    }

                }
            }
        }
        invalidate();
        return true;
    }

    public boolean finalCheck()
    {
        return new FieldChecker(field).finalCheck();
    }

    private void showError()
    {
       /* Toast toast = Toast.makeText(context,
                "Incorrect placement for ships.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(context);
        catImageView.setImageResource(R.drawable.kitty_wow);
        toastContainer.addView(catImageView, 0);
        toast.show();*/
        Snackbar snackbar = Snackbar.make(findViewById(R.id.player_field), "Incorrect placement for ships.", BaseTransientBottomBar.LENGTH_SHORT);
        snackbar.show();
    }


}
