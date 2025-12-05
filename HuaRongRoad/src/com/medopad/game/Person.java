package com.medopad.game;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;

class Person extends JButton {
    int PersonNum;
    int widthCells;  // 占用格子横向数量
    int heightCells; // 占用格子纵向数量
    Color c = new Color(255, 245, 170);

    Person(int PersonNum, String name, int widthCells, int heightCells){
        super(name);
        this.PersonNum = PersonNum;
        this.widthCells = widthCells;
        this.heightCells = heightCells;
        setBackground(c);
    }

    // 获取方块占用的所有格子坐标（左上角坐标）
    public Point[] getOccupiedPoints() {
        Point[] pts = new Point[widthCells * heightCells];
        int idx = 0;
        for(int i=0; i<widthCells; i++){
            for(int j=0; j<heightCells; j++){
                pts[idx++] = new Point(getX() + i*50, getY() + j*50);
            }
        }
        return pts;//每一个棋子一个point数组，储存所有的格子的坐标
    }
}
