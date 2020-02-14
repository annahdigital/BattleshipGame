package com.example.battleshipgame.FieldView;

import com.example.battleshipgame.Models.CellMode;
import com.example.battleshipgame.Models.Field;


class FieldChecker {

    private final Field field;

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

    FieldChecker(Field field)
    {
        this.field = field;
    }

    boolean finalCheck(){
        if (checkField())
        {
            return battleshipsCount == currentBattleshipsCount && cruisersCount == currentCruisersCount &&
                    torpedosCount == currentTorpedosCount && destroyersCount == currentDestroyersCount;
        }
        else return false;
    }

    boolean checkField()
    {
        correctCells = new boolean[field.height][field.width];
        currentBattleshipsCount = 0;
        currentCruisersCount = 0;
        currentDestroyersCount = 0;
        currentTorpedosCount = 0;

        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++)
                if (!checkCell(i, j))
                    return false;
        return true;
    }

    private boolean checkCell(int i, int j)
    {
        if (!checkNeighbors(i, j)) {
            return false;
        } else
        {
            correctCells[i][j] = true;
            return true;
        }

    }

    private boolean checkNearbyCells(int i, int j, boolean checkLength)
    {
        int nearbyShipCellsHorizontal = 0, nearbyShipCellsVertical = 0;

        if (i > 0) {

            if (field.getCell(i - 1, j) == CellMode.SHIP)
                nearbyShipCellsHorizontal++;
            if (j + 1 < field.height)
                if (field.getCell(i - 1, j + 1) == CellMode.SHIP)
                    return false;
            if (j > 0)
                if (field.getCell(i - 1, j - 1) == CellMode.SHIP)
                    return false;
        }

        if (j + 1 < field.height)
            if (field.getCell(i, j+1) == CellMode.SHIP)
                nearbyShipCellsVertical++;
        if (j - 1 >= 0)
            if (field.getCell(i, j - 1) == CellMode.SHIP)
                nearbyShipCellsVertical++;
        if (i + 1 < field.width)
        {
            if (field.getCell(i + 1, j) == CellMode.SHIP)
                nearbyShipCellsHorizontal++;
            if (j + 1 < field.height)
                if (field.getCell(i + 1, j + 1) == CellMode.SHIP)
                    return false;
            if (j - 1 >= 0)
                if (field.getCell(i + 1, j - 1) == CellMode.SHIP)
                    return false;
        }

        if (checkLength) {
            if (nearbyShipCellsVertical == 0 && nearbyShipCellsHorizontal == 0) {
                currentBattleshipsCount++;
                return true;
            } else if (nearbyShipCellsVertical <= 2 && nearbyShipCellsHorizontal == 0 || nearbyShipCellsVertical == 0 && nearbyShipCellsHorizontal <= 2)
                return checkLength(i, j);
            else
                return false;
        }
        else
            return (nearbyShipCellsVertical <= 2 && nearbyShipCellsHorizontal == 0 || nearbyShipCellsVertical == 0 && nearbyShipCellsHorizontal <= 2);
    }

    private boolean checkNeighbors(int i, int j)
    {
        if (correctCells[i][j])
            return true;
        if (field.getCell(i, j) == CellMode.EMPTY)
            return true;
        return checkNearbyCells(i, j, true);
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
                if (checkNearbyCells(iter, j, false))
                    correctCells[iter][j] = true;
                else return false;
                shipLength++;
                iter--;
            }
        }

        else if (rightCell == CellMode.SHIP)
        {
            int iter = i + 1;
            while (iter < field.width && field.getCell(iter, j) == CellMode.SHIP)
            {
                if (checkNearbyCells(iter, j, false))
                    correctCells[iter][j] = true;
                else return false;
                shipLength++;
                iter++;
            }
        }

        else if (upperCell == CellMode.SHIP)
        {
            int iter = j - 1;
            while (iter >= 0 && field.getCell(i, iter) == CellMode.SHIP)
            {
                if (checkNearbyCells(i, iter, false))
                    correctCells[i][iter] = true;
                else return false;
                shipLength++;
                iter--;
            }
        }

        else if (lowerCell == CellMode.SHIP)
        {
            int iter = j + 1;
            while (iter < field.height && field.getCell(i, iter) == CellMode.SHIP)
            {
                if (checkNearbyCells(i, iter, false))
                    correctCells[i][iter] = true;
                else return false;
                shipLength++;
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
/*
    public void checkIfShipIsKilled(int i, int j)
    {
        if (field.getCell(i, j) != CellMode.SHIP)
            return;
        CellMode leftCell = null, rightCell = null, upperCell = null, lowerCell = null;
        if (i + 1 < field.width)
            rightCell = field.getCell(i+1, j);
        if (j - 1 >= 0)
            upperCell = field.getCell(i, j-1);
        if (j + 1 < field.height)
            lowerCell = field.getCell(i, j+1);
        if (i - 1 >= 0)
            leftCell = field.getCell(i-1, j);

        if (leftCell != CellMode.SHIP && rightCell != CellMode.SHIP && upperCell != CellMode.SHIP && lowerCell != CellMode.SHIP
            && leftCell != CellMode.HIT && rightCell != CellMode.HIT && upperCell != CellMode.HIT && lowerCell != CellMode.HIT)
        {
            if (rightCell != null)
                field.setCellMode(CellMode.MISS,i+1, j);
            if (upperCell != null)
                field.setCellMode(CellMode.MISS, i, j-1);
            if (lowerCell != null)
                field.setCellMode(CellMode.MISS, i, j+1);
            if (leftCell != null)
                field.setCellMode(CellMode.MISS,i-1, j);
            if (i - 1 >= 0 && j + 1 < field.height)
                field.setCellMode(CellMode.MISS, i - 1, j + 1);
            if (i - 1 >= 0 && j - 1 >= 0)
                field.setCellMode(CellMode.MISS, i - 1, j - 1);
            if (i + 1 < field.width && j + 1 < field.height)
                field.setCellMode(CellMode.MISS, i + 1, j + 1);
            if (i + 1 < field.width && j - 1 >= 0)
                field.setCellMode(CellMode.MISS, i + 1, j - 1);
            Log.println(Log.ERROR, "kek", "start");
            return;
        }

        int endX = i, endY = j, startX = i, startY = j;

        if (leftCell != CellMode.HIT && rightCell != CellMode.HIT && upperCell != CellMode.HIT && lowerCell != CellMode.HIT)
            return;
        if (leftCell == CellMode.SHIP || rightCell == CellMode.SHIP || upperCell == CellMode.SHIP || lowerCell == CellMode.SHIP)
            return;

        if (leftCell == CellMode.HIT)
        {
            startX = i - 1;
            while (startX -1 >= 0 && field.getCell(startX - 1, j) == CellMode.HIT)
                startX--;
            Log.println(Log.ERROR, "kek", String.valueOf(field.getCell(startX, j)));
            if (startX >= 0 && field.getCell(startX, j) == CellMode.SHIP)
                return;
        }
        if (rightCell == CellMode.HIT)
        {
            endX = i + 1;
            while (endX + 1 < field.width && field.getCell(endX + 1, j) == CellMode.HIT)
                endX++;
            Log.println(Log.ERROR, "kek", String.valueOf(field.getCell(endX, j)));
            if (endX < field.width && field.getCell(endX, j) == CellMode.SHIP)
                return;
        }
        if (upperCell == CellMode.HIT)
        {
            startY = j - 1;
            while (startY - 1 >= 0 && field.getCell(i, startY - 1) == CellMode.HIT)
                startY--;
            Log.println(Log.ERROR, "kek", String.valueOf(field.getCell(i, startY)));
            if (startY  >= 0 && field.getCell(i, startY) == CellMode.SHIP)
                return;
        }
        if (lowerCell == CellMode.HIT)
        {
            endY = j + 1;
            while (endY + 1 < field.height && field.getCell(i, endY + 1) == CellMode.HIT)
                endY++;
            Log.println(Log.ERROR, "kek", String.valueOf(field.getCell(i, endY)));
            if (endY < field.height && field.getCell(i, endY) == CellMode.SHIP)
                return;
        }

        if (endX + 1 < field.width && field.getCell(endX + 1, endY) != CellMode.SHIP && field.getCell(endX + 1, endY) != CellMode.HIT)
            field.setCellMode(CellMode.MISS,endX + 1, endY);
        if (endY - 1 >= 0  && field.getCell(endX, endY - 1) != CellMode.SHIP && field.getCell(endX, endY - 1) != CellMode.HIT)
            field.setCellMode(CellMode.MISS, endX, endY - 1);
        if (endY + 1 < field.height  && field.getCell(endX, endY + 1) != CellMode.SHIP && field.getCell(endX, endY + 1) != CellMode.HIT)
            field.setCellMode(CellMode.MISS, endX, endY + 1);
        if (endX - 1 >= 0 && field.getCell(endX - 1, endY) != CellMode.SHIP && field.getCell(endX - 1, endY) != CellMode.HIT)
            field.setCellMode(CellMode.MISS,endX - 1, endY);
        if (endX - 1 >= 0 && endY + 1 < field.height)
            field.setCellMode(CellMode.MISS, endX - 1, endY + 1);
        if (endX - 1 >= 0 && endY - 1 >= 0)
            field.setCellMode(CellMode.MISS, endX - 1, endY - 1);
        if (endX + 1 < field.width && endY + 1 < field.height)
            field.setCellMode(CellMode.MISS, endX + 1, endY + 1);
        if (endX + 1 < field.width && endY - 1 >= 0)
            field.setCellMode(CellMode.MISS, endX + 1, endY - 1);

        Log.println(Log.ERROR, "kek!", "what");


    }

*/

}
