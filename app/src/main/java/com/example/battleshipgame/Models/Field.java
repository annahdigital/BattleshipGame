package com.example.battleshipgame.Models;

public class Field {

    public int height;
    public int width;
    private CellMode[][] cells;

    public Field()
    {
        height = 10;
        width = 10;
        cells = new CellMode[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                cells[i][j] = CellMode.EMPTY;
    }

    public CellMode[][] getCells()
    {   return cells;   }

    public CellMode getCell(int x, int y)
    {   return cells[x][y];     }

    public void setCellMode(CellMode mode, int x, int y)
    {   cells[x][y] = mode;     }
}
