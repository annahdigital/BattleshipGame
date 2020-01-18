package com.example.battleshipgame.Grid;

import com.example.battleshipgame.Models.CellMode;
import com.example.battleshipgame.Models.Field;


class FieldChecker {

    private Field field;

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
        else {
            return (nearbyShipCellsVertical <= 2 && nearbyShipCellsHorizontal == 0 || nearbyShipCellsVertical == 0 && nearbyShipCellsHorizontal <= 2);
        }
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



}
