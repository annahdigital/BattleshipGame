package com.example.battleshipgame.Models;

public class Field {

    public final int height;
    public final int width;
    private final CellMode[][] cells;

    public Field()
    {
        height = 10;
        width = 10;
        cells = new CellMode[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                cells[i][j] = CellMode.EMPTY;
    }

// --Commented out by Inspection START (8.1.20 14.40):
//    public CellMode[][] getCells()
//    {   return cells;   }
// --Commented out by Inspection STOP (8.1.20 14.40)

    public CellMode getCell(int x, int y)
    {   return cells[x][y];     }

    public void setCellMode(CellMode mode, int x, int y)
    {   cells[x][y] = mode;     }
}
