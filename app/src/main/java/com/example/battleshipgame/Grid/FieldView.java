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

import java.util.ArrayList;

public class FieldView extends View {

    private Paint PurplePaint;
    private Paint RedPaint;
    private Paint GreyishPaint;
    private Paint FieldGridPaint;
    private Paint GreyPaint;

    private int cellWidth;
    private int cellHeight;

    private Field field;
    private CurrentFieldMode fieldMode;
    private final Context context;

    // final values for comparing and validating
    private final static int battleshipsCount = 4;
    private final static int cruisersCount = 3;
    private final static int destroyersCount = 2;
    private final static int torpedosCount = 1;

    private int currentBattleshipsCount = 0;
    private int currentCruisersCount = 0;
    private int currentDestroyersCount = 0;
    private int currentTorpedosCount = 0;
    private boolean [][] correctCells;

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
            if (x < field.width && y < field.height) {

                if (fieldMode == CurrentFieldMode.CREATION) {
                    if (field.getCell(x, y) == CellMode.EMPTY) {
                        field.setCellMode(CellMode.SHIP, x, y);
                    } else
                        field.setCellMode(CellMode.EMPTY, x, y);
                    if (!tryToPlay())
                        showError();

                } else if (fieldMode == CurrentFieldMode.OPPONENT){
                    if (field.getCell(x, y) == CellMode.EMPTY) {
                        field.setCellMode(CellMode.MISS, x, y);
                        ((GameActivity)this.context).updatingMove(MoveType.MISS);
                    } else if (field.getCell(x, y) == CellMode.SHIP) {
                        field.setCellMode(CellMode.HIT, x, y);
                        //ifDestroyed(x, y);
                        ((GameActivity)this.context).updatingMove(MoveType.HIT);
                    }

                }
            }
        }
        invalidate();
        return true;
    }

    /*private void ifDestroyed(int i, int j)
    {
        Log.println(Log.ERROR, "kek", "ff");
        int nearbyShipCells = 0;
        ArrayList<CellMode> neighborsCells = new ArrayList<>();
        if (i > 0) {
            neighborsCells.add(field.getCell(i - 1, j));
        }
        if (j + 1 < field.height)
            neighborsCells.add(field.getCell(i, j+1));
        if (j - 1 >= 0)
            neighborsCells.add(field.getCell(i, j - 1));
        if (i + 1 < field.width)
            neighborsCells.add(field.getCell(i + 1, j));

        for (CellMode neighbor : neighborsCells)
        {
            if (neighbor == CellMode.SHIP)
                nearbyShipCells++;
        }
        Log.println(Log.ERROR, "d", String.valueOf(nearbyShipCells));
        if (nearbyShipCells == 0)
            ((GameActivity)this.context).showShipDestroyed();
    }*/


    public boolean endCreation(){
        if (tryToPlay())
        {
            return battleshipsCount == currentBattleshipsCount && cruisersCount == currentCruisersCount &&
                    torpedosCount == currentTorpedosCount && destroyersCount == currentDestroyersCount;
        }
        else return false;
    }

    private boolean tryToPlay()
    {
        correctCells = new boolean[field.height][field.width];
        currentBattleshipsCount = 0;
        currentCruisersCount = 0;
        currentDestroyersCount = 0;
        currentTorpedosCount = 0;
        for (int i = 0; i < field.height; i++)
        {
            for (int j = 0; j < field.width; j++)
                if (!tryToPlayWithCell(i, j))
                    return false;
        }
        return true;
    }

    private boolean tryToPlayWithCell(int i, int j)
    {
        if (!checkNeighbors(i, j)) {
            return false;
        }
        else
        {
            correctCells[i][j] = true;
            return true;
        }

    }

    private boolean checkNeighbors(int i, int j)
    {
        if (correctCells[i][j])
            return true;
        if (field.getCell(i, j) == CellMode.EMPTY)
            return true;
        int nearbyShipCells = 0;
        ArrayList<CellMode> neighborsCells = new ArrayList<>();
        ArrayList<CellMode> cornerCells = new ArrayList<>();
        if (i > 0) {
            if (!correctCells[i-1][j])
                neighborsCells.add(field.getCell(i - 1, j));
            if (j + 1 < field.height) {
                if (!correctCells[i-1][j+1])
                    cornerCells.add(field.getCell(i - 1, j + 1));
            }
            if (j > 0) {
                if (!correctCells[i-1][j-1])
                    cornerCells.add(field.getCell(i - 1, j - 1));
            }
        }
        if (j + 1 < field.height)
            if (!correctCells[i][j+1])
                neighborsCells.add(field.getCell(i, j+1));
        if (j - 1 >= 0)
            if (!correctCells[i][j-1])
                neighborsCells.add(field.getCell(i, j - 1));
        if (i + 1 < field.width)
        {
            if (!correctCells[i+1][j])
                neighborsCells.add(field.getCell(i + 1, j));
            if (j + 1 < field.height) {
                if (!correctCells[i+1][j+1])
                    cornerCells.add(field.getCell(i + 1, j + 1));
            }
            if (j - 1 >= 0) {
                if (!correctCells[i+1][j-1])
                    cornerCells.add(field.getCell(i + 1, j - 1));
            }
        }
        for (CellMode corner : cornerCells)
        {
            if (corner == CellMode.SHIP) {
                return false;
            }
        }
        for (CellMode neighbor : neighborsCells)
        {
            if (neighbor == CellMode.SHIP)
                nearbyShipCells++;
        }
        if (nearbyShipCells == 0) {
            currentBattleshipsCount++;
            return true;
        }
        else if (nearbyShipCells < 2)
            return checkLength(i, j);
        else {
            return false;
        }
    }

    private boolean checkLength(int i, int j)
    {
        int shipLength = 1;
        CellMode leftCell = null, rightCell = null, upperCell = null, lowerCell = null;
        if (i + 1 < field.width)
            rightCell = field.getCell(i+1, j);
        if (j - 1 >= 0)
            upperCell = field.getCell(i, j-1);
        if (j + 1 < field.height)
            lowerCell = field.getCell(i, j+1);
        if (i - 1 >= 0)
            leftCell = field.getCell(i-1, j);


        if (leftCell == CellMode.SHIP)
        {
            int iter = i - 1;
            while (iter >= 0 && field.getCell(iter, j) == CellMode.SHIP)
            {
                if (correctCells[iter][j])
                    return false;
                shipLength++;
                correctCells[iter][j] = true;
                iter--;
            }
        }

        else if (rightCell == CellMode.SHIP)
        {
            int iter = i + 1;
            while (iter < field.width && field.getCell(iter, j) == CellMode.SHIP)
            {
                if (correctCells[iter][j])
                    return false;
                shipLength++;
                correctCells[iter][j] = true;
                iter++;
            }
        }

        else if (upperCell == CellMode.SHIP)
        {
            int iter = j - 1;
            while (iter >= 0 && field.getCell(i, iter) == CellMode.SHIP)
            {
                if (correctCells[i][iter])
                    return false;
                shipLength++;
                correctCells[i][iter] = true;
                iter--;
            }
        }
        else if (lowerCell == CellMode.SHIP)
        {
            int iter = j + 1;
            while (iter < field.height && field.getCell(i, iter) == CellMode.SHIP)
            {
                if (correctCells[i][iter])
                    return false;
                shipLength++;
                correctCells[i][iter] = true;
                iter++;
            }
        }

        if (shipLength > 4)
            return false;
        else if (shipLength == 2)
            currentCruisersCount++;
        else if (shipLength == 3)
            currentDestroyersCount++;
        else if (shipLength == 4)
            currentTorpedosCount++;
        return true;
    }

    private void showError()
    {
        Toast toast = Toast.makeText(context,
                "Incorrect placement for ships.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(context);
        catImageView.setImageResource(R.drawable.kitty_wow);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }


}
