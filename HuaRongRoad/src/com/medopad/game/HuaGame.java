package com.medopad.game;

import com.medopad.Util.PuzzleParser;
import com.medopad.game.Snapshot.Snapshot;

import java.util.Stack;
import com.medopad.game.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.Stack;

class HuaGame extends Frame implements MouseListener, MouseMotionListener, ActionListener {
    // 保存每一步前的棋盘状态
    Stack<java.awt.Point[]> history = new Stack<>();//point是java自带的类包含一个x坐标一个y坐标
    String[][] arr;

    // 记录撤销按钮状态
    boolean undoUsed = false;

    private Timer autoTimer;  // 定时器，用于自动执行
    private boolean autoRunning = false;  // 标记是否正在运行自动模式


    Person[] person = new Person[10];
    int step;//记录玩家移动步数
    int w;//当前关卡

    Person draggingPerson = null;
    int startX, startY;   // 鼠标按下时
    int origX, origY;     // 方块初始位置


    Button left = new Button();
    Button right = new Button();
    Button above = new Button();
    /*Button below = new Button();*/ //四条游戏边界
    Button below_left = new Button();
    Button below_middle = new Button();
    Button below_right = new Button();

    Button restart = new Button("restore");
    Button exit = new Button("exit");

    Button auto  =  new Button("Start Auto");
    Button next_step = new Button("next step");

    Button one = new Button("task 1");
    Button two = new Button(" task 2");
    Button three = new Button("task 3");
    Button four = new Button("task 4");
    Button five = new Button("task 5");//一共五关

    JLabel stepLabel = new JLabel("step:");
    JLabel stepNumLabel = new JLabel("0");//步数显示

    public static void main(String[] args) {
        new HuaGame(1);//启动游戏自动跳转第一关
    }

    public HuaGame(int model) {

        initPerson();//初始化人物
        initGUI();//初始化界面
        choose(model);
        printBoard();
    }


    private void initGUI() {
        setLayout(null);
        setTitle("华容道");
        setBounds(100, 100, 450, 360);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent x) {
                System.exit(0);
            }
        });

        add(this.left);
        this.left.setBounds(49, 49, 5, 260);
        this.left.setBackground(Color.BLACK);
        add(this.right);
        this.right.setBounds(254, 49, 5, 260);
        this.right.setBackground(Color.BLACK);
        add(this.above);
        this.above.setBounds(49, 49, 210, 5);
        this.above.setBackground(Color.BLACK);
		/*add(this.below);
		this.below.setBounds(49, 304, 210, 5);*/
        add(this.below_left);
        this.below_left.setBounds(49, 304, 52, 5);
        this.below_left.setBackground(Color.BLACK);
        add(this.below_middle);
        this.below_middle.setBounds(49 + 52, 304, 104, 5);
        this.below_middle.setBackground(Color.RED);
        add(this.below_right);
        this.below_right.setBounds(49 + 52 + 104, 304, 52, 5);
        this.below_right.setBackground(Color.BLACK);

        add(this.restart);
        this.restart.setBounds(50, 320, 100, 25);
        this.restart.addActionListener(this);
        add(this.exit);
        this.exit.setBounds(160, 320, 100, 25);
        this.exit.addActionListener(this);

        add(this.auto);
        this.auto.setBounds(300, 260, 100, 25);
        this.auto.addActionListener(this);
        add(this.next_step);
        this.next_step.setBounds(300, 230, 100, 25);
        this.next_step.addActionListener(this);

        add(this.one);
        this.one.setBounds(300, 50, 100, 25);
        this.one.addActionListener(this);
        add(this.two);
        this.two.setBounds(300, 80, 100, 25);
        this.two.addActionListener(this);
        add(this.three);
        this.three.setBounds(300, 110, 100, 25);
        this.three.addActionListener(this);
        add(this.four);
        this.four.setBounds(300, 140, 100, 25);
        this.four.addActionListener(this);
        add(this.five);
        this.five.setBounds(300, 170, 100, 25);
        this.five.addActionListener(this);

        add(this.stepLabel);
        this.stepLabel.setBounds(300, 200, 35, 25);
        add(this.stepNumLabel);
        this.stepNumLabel.setBounds(335, 200, 30, 25);
        //各个按钮的设置和显示
    }

    public void initPerson() {
        setLayout(null);

        String[] names = {"曹操", "关羽", "将", "将", "将", "将", "兵", "兵", "兵", "兵"};
        int[][] sizes = {
                {2, 2}, // 曹操
                {2, 1}, // 关羽
                {1, 2}, // 将
                {1, 2}, // 将
                {1, 2}, // 将
                {1, 2}, // 将
                {1, 1}, // 兵
                {1, 1}, // 兵
                {1, 1}, // 兵
                {1, 1}  // 兵
        };

        for (int i = 0; i < names.length; i++) {
            this.person[i] = new Person(i, names[i], sizes[i][0], sizes[i][1]);
            this.person[i].addMouseListener(this);
            this.person[i].addMouseMotionListener(this);
            add(this.person[i]);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {

        draggingPerson = (Person) e.getSource();

        startX = e.getXOnScreen();
        startY = e.getYOnScreen();

        origX = draggingPerson.getX();
        origY = draggingPerson.getY();

        draggingPerson.setBackground(Color.RED);
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        // 不需要实现内容，保持空方法即可
    }


    // 输出当前数字棋盘
    public String[][] getCurrentBoard() {
        int rows = 5; // 横向格子数
        int cols = 4; // 纵向格子数
        String[][] maps = new String[rows+2][cols+2];

        for (int r = 0; r < rows+2; r++)
            for (int c = 0; c < cols+2; c++)
                maps[r][c] = " ";

        for(int r = 0; r < rows+2; r++){
            maps[r][0] = "X";
        }

        for(int c = 0; c < cols+2; c++){
            maps[0][c] = "X";
        }

        for(int r = 0; r < rows+2; r++){
            maps[r][5] = "X";
        }

        for(int c = 0; c < cols+2; c++){
            maps[6][c] = "X";
        }

        maps[6][2] = "Z";
        maps[6][3] = "Z";

        for (int i = 0; i < person.length; i++) {
            int x = (person[i].getX() - 49) / 50;
            int y = (person[i].getY() - 49) / 50;
            for (int dx = 0; dx < person[i].widthCells; dx++) {
                for (int dy = 0; dy < person[i].heightCells; dy++) {
                    int row = y + dy;
                    int col = x + dx;
                    if (i == 0) maps[row+1][col+1] = "D"; // 曹操
                    else if (i == 1) maps[row+1][col+1] = "E"; // 关羽
                    else if (i == 2) maps[row+1][col+1] = "A";
                    else if (i == 3) maps[row+1][col+1] = "H";
                    else if (i == 4) maps[row+1][col+1] = "B";
                    else if (i == 5) maps[row+1][col+1] = "I";
                    else if (i == 6) maps[row+1][col+1] = "C";
                    else if (i == 7) maps[row+1][col+1] = "J";
                    else if (i == 8) maps[row+1][col+1] = "F";
                    else if (i == 9) maps[row+1][col+1] = "G";
                }
            }
        }

        return maps;
    }

    public void applySnapshot(String[][] arr) {

        for (int i = 0; i < person.length; i++) {

            String targetChar = switch (i) {
                case 0 -> "D";
                case 1 -> "E";
                case 2 -> "A";
                case 3 -> "H";
                case 4 -> "B";
                case 5 -> "I";
                case 6 -> "C";
                case 7 -> "J";
                case 8 -> "F";
                case 9 -> "G";
                default -> null;
            };

            outer:
            for (int r = 0; r < arr.length; r++) {
                for (int c = 0; c < arr[0].length; c++) {

                    if (arr[r][c].equals(targetChar)) {
                        // arr 从 [1][1] 开始才是棋盘
                        int px = 54 +(c - 1) * 50;
                        int py = 54 + (r - 1) * 50;

                        person[i].setLocation(px, py);
                        break outer;
                    }
                }
            }
        }
    }


    public void printBoard() {
        String[][] maps = getCurrentBoard();
        System.out.println("当前棋盘：");
        for (int r = 0; r < maps.length; r++) {
            for (int c = 0; c < maps[0].length; c++) {
                System.out.print(maps[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------");
    }


    public void move(Person CurrentPerson, Button paramButton) {
        boolean goon = true;

        int x = CurrentPerson.getX();
        int y = CurrentPerson.getY();

        // 根据方向移动格子数
        if (paramButton == this.below_left || paramButton == this.below_middle || paramButton == this.below_right) {
            y += 50;
        } else if (paramButton == this.above) {
            y -= 50;
        } else if (paramButton == this.left) {
            x -= 50;
        } else if (paramButton == this.right) {
            x += 50;
        }

        // 计算移动后的占用格子
        java.awt.Point[] occupied = new java.awt.Point[CurrentPerson.widthCells * CurrentPerson.heightCells];
        int idx = 0;
        for (int i = 0; i < CurrentPerson.widthCells; i++) {
            for (int j = 0; j < CurrentPerson.heightCells; j++) {
                occupied[idx++] = new java.awt.Point(x + i * 50, y + j * 50);
            }
        }

        // 检查是否碰撞其他方块
        for (int m = 0; m < person.length; m++) {
            if (person[m] == CurrentPerson) continue;
            java.awt.Point[] other = person[m].getOccupiedPoints();
            for (java.awt.Point p1 : occupied) {
                for (java.awt.Point p2 : other) {
                    Rectangle r1 = new Rectangle(p1.x, p1.y, 50, 50);
                    Rectangle r2 = new Rectangle(p2.x, p2.y, 50, 50);//遍历每一个本身和其他棋子的块块，看看有没有碰上
                    if (r1.intersects(r2)) {
                        goon = false;
                        break;
                    }
                }
                if (!goon) break;
            }
            if (!goon) break;
        }

        // 检查是否碰撞边界
        Rectangle bound;
        if (paramButton == this.below_left || paramButton == this.below_middle || paramButton == this.below_right) {
            bound = paramButton.getBounds();
            for (java.awt.Point p : occupied) {
                if (new Rectangle(p.x, p.y, 50, 50).intersects(bound)) {
                    goon = false;
                    break;
                }
            }
        } else {
            bound = paramButton.getBounds();
            for (java.awt.Point p : occupied) {
                if (new Rectangle(p.x, p.y, 50, 50).intersects(bound)) {
                    goon = false;
                    break;
                }
            }
        }

        if (goon) {
            // 保存历史状态
            java.awt.Point[] snapshot = new java.awt.Point[10];
            for(int i=0;i<10;i++){
                snapshot[i] = new java.awt.Point(person[i].getX(), person[i].getY());
            }
            history.push(snapshot);

            CurrentPerson.setLocation(x, y);
            step++;
            stepNumLabel.setText(String.valueOf(step));

            // 移动成功后恢复 restore 按钮
            undoUsed = false;
            restart.setEnabled(true);

            // **打印数字棋盘**
            printBoard();

            System.out.println(person[0].getX());
            System.out.println(person[0].getY());

            if(person[0].getX()==104 && person[0].getY()==204){
                finish();
            }
        }


    }


    @Override
    public void mouseDragged(MouseEvent e) {
        // 拖动过程中不实时移动方块，只更新鼠标位置
        if (draggingPerson == null) return;

        // 可以可选地显示方块随鼠标移动（视觉效果）
        // int dx = e.getXOnScreen() - startX;
        // int dy = e.getYOnScreen() - startY;
        // draggingPerson.setLocation(origX + dx, origY + dy);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggingPerson == null) return;

        int dx = e.getXOnScreen() - startX;
        int dy = e.getYOnScreen() - startY;

        int moveThreshold = 30;  // 移动阈值
        int unit = 50;            // 每格大小

        // 判断方向，只移动一格
        if (Math.abs(dx) > Math.abs(dy)) {
            // 水平移动
            if (dx > moveThreshold) move(draggingPerson, right);
            else if (dx < -moveThreshold) move(draggingPerson, left);
        } else {
            // 垂直移动
            if (dy > moveThreshold) move(draggingPerson, below_middle);
            else if (dy < -moveThreshold) move(draggingPerson, above);
        }

        // 松手后恢复方块原始颜色
        draggingPerson.setBackground(draggingPerson.c);

        // 重置拖动状态
        draggingPerson = null;
    }

    private String printHelper(String s) {
        if (s.equals(PointConstant.V_PIECE_AVATAR_1))
            return PointConstant.V_PIECE;
        if (s.equals(PointConstant.V_PIECE_AVATAR_3))
            return PointConstant.V_PIECE_2;
        if (s.equals(PointConstant.V_PIECE_AVATAR_4))
            return PointConstant.V_PIECE_3;
        if (s.equals(PointConstant.V_PIECE_AVATAR_2))
            return PointConstant.V_PIECE_4;
        if (s.equals(PointConstant.HOR_PIECE_RIGHT))
            return PointConstant.HOR_PIECE_LEFT;
        if (s.equals(PointConstant.D_PIECE_LEFT_TOP) || s.equals(PointConstant.D_PIECE_RIGHT_TOP) || s.equals(PointConstant.D_PIECE_LEFT_BOT) || s.equals(PointConstant.D_PIECE_RIGHT_BOT))
            return PointConstant.D_PIECE;
        return s;
    }

    private String[][] traverseAndGetNext(String[][] maps) {
        Stack<Snapshot> stack = getSnapshots(PuzzleParser.parse(maps));
        String arr[][] = getTopMove(stack);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = printHelper(arr[i][j]);
            }
        }
        System.out.println("arr棋盘：");
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[0].length; c++) {
                System.out.print(arr[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------");
        return arr;// 栈顶即下一步
    }


        private static Stack<Snapshot> getSnapshots(String[][] maps) {
            Snapshot snapshot = new Game(maps).find();
            Stack<Snapshot> stack = new Stack<>();
            while (snapshot != null) {
                stack.add(snapshot);
                snapshot = snapshot.getParent();
            }
            return stack;
        }

    private static String[][] getTopMove(Stack<Snapshot> stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        // 获取第一个（栈顶）Snapshot
        Snapshot top = stack.pop();        // 弹出栈顶

        // 获取第二个 Snapshot（现在栈顶就是原来的第二个）
        Snapshot second = stack.peek();    // 查看，不弹出
        String[][] arr = second.getStringMatrix();
        return arr;// 返回 String[][]
    }

    public void finish() {
        JOptionPane.showMessageDialog(this, "你拯救了 曹操");
        new HuaGame(this.w);
    }

    private void startAutoPlay() {
        // 设置定时器， 每隔1000ms (1秒) 调用一次自动下一步操作
        autoTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用 next_step 的逻辑
                nextStepAction();
            }
        });
        autoTimer.start();  // 启动定时器
        autoRunning = true;  // 更新状态为正在运行
        auto.setLabel("Stop Auto");  // 修改按钮文本为 "停止自动"
    }

    private void stopAutoPlay() {
        if (autoTimer != null && autoTimer.isRunning()) {
            autoTimer.stop();  // 停止定时器
        }
        autoRunning = false;  // 更新状态为未运行
        auto.setLabel("Start Auto");  // 恢复按钮文本为 "开始自动"
    }

    private void nextStepAction() {
        // 保存当前状态到 history（用于恢复）
        Point[] snapshot = new Point[10];
        for (int i = 0; i < 10; i++) {
            snapshot[i] = new Point(person[i].getX(), person[i].getY());
        }
        history.push(snapshot);

        undoUsed = false;
        restart.setEnabled(true);

        // 获取当前棋盘
        String[][] currentMaps = getCurrentBoard();

        // 获取下一步解决方案
        String[][] nextStepMaps = traverseAndGetNext(currentMaps);

        if (nextStepMaps != null) {
            // 应用下一步到 UI
            applySnapshot(nextStepMaps);

            // 更新步数
            step++;
            stepNumLabel.setText(String.valueOf(step));

            printBoard();

            // 检查胜利条件
            if (person[0].getX() == 104 && person[0].getY() == 204) {
                finish();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Button CurrentButton = (Button) e.getSource();

        if (CurrentButton == this.restart) {
            if (!history.isEmpty() && !undoUsed) {
                // 弹出栈顶状态并恢复
                Point[] lastState = history.pop();
                for (int i = 0; i < 10; i++) {
                    person[i].setLocation(lastState[i]);
                }
                step--;
                stepNumLabel.setText(String.valueOf(step));

                // 按钮变灰
                restart.setEnabled(false);
                undoUsed = true;
            }
        } else if (CurrentButton == this.exit) {
            System.exit(0);
        } else if (CurrentButton == this.one) {
            choose(1);
            printBoard();
        } else if (CurrentButton == this.two) {
            choose(2);
            printBoard();
        } else if (CurrentButton == this.three) {
            choose(3);
            printBoard();
        } else if (CurrentButton == this.four) {
            choose(4);
            printBoard();
        } else if (CurrentButton == this.five) {
            choose(5);
            printBoard();
        }

        else if (CurrentButton == this.next_step) {
            // 保存当前状态到 history（用于恢复）
            Point[] snapshot = new Point[10];
            for (int i = 0; i < 10; i++) {
                snapshot[i] = new Point(person[i].getX(), person[i].getY());
            }
            history.push(snapshot);


            undoUsed = false;
            restart.setEnabled(true);


            // 1. 获取当前棋盘
            String[][] currentMaps = getCurrentBoard();

            // 2. 获取下一步解决方案
            String[][] nextStepMaps = traverseAndGetNext(currentMaps);

            if (nextStepMaps != null) {
                // 3. 应用下一步到 UI
                applySnapshot(nextStepMaps);

                // 4. 更新步数
                step++;
                stepNumLabel.setText(String.valueOf(step));

                // 打印棋盘调试
                printBoard();

                System.out.println(person[0].getX());
                System.out.println(person[0].getY());

                if(person[0].getX()==104 && person[0].getY()==204){
                    finish();
                }
            }
        }
        else if (CurrentButton == this.auto) {
            if (autoRunning) {
                // 如果正在运行自动播放，点击停止
                stopAutoPlay();
            } else {
                // 启动自动播放
                startAutoPlay();
            }
        }

    }

    public void choose(int level) {
        // 每一关对应的起始位置
        int[][][] positions = {
                // 关卡1
                {
                        {104, 54}, {104, 154}, {54, 154}, {204, 154}, {54, 54}, {204, 54}, {54, 254}, {204, 254}, {104, 204}, {154, 204}
                },
                // 关卡2
                {
                        {104, 54}, {104, 154}, {54, 204}, {204, 204}, {54, 54}, {204, 54}, {54, 154}, {204, 154}, {104, 204}, {154, 204}
                },
                // 关卡3
                {
                        {104, 54}, {104, 204}, {54, 204}, {204, 204}, {54, 54}, {204, 54}, {54, 154}, {204, 154}, {104, 154}, {154, 154}
                },
                // 关卡4
                {
                        {104, 54}, {104, 154}, {54, 204}, {204, 204}, {54, 104}, {204, 104}, {54, 54}, {204, 54}, {104, 204}, {154, 204}
                },
                // 关卡5
                {
                        {54, 54}, {54, 154}, {54, 204}, {104, 204}, {154, 54}, {204, 54}, {154, 154}, {204, 154}, {154, 204}, {204, 204}
                }
        };

        int[][] pos = positions[level - 1];

        for (int i = 0; i < person.length; i++) {
            person[i].setBounds(pos[i][0], pos[i][1], person[i].widthCells * 50, person[i].heightCells * 50);
        }

        this.w = level;

        // 设置当前关卡
        this.w = level;

        // 重置步数
        step = 0;
        stepNumLabel.setText("0");

        // 清空历史状态栈
        history.clear();

        // 恢复 restore 按钮可用
        undoUsed = false;
        restart.setEnabled(true);
    }

    /*

    private static void traverseAndPrint(String[][] maps) {
        Stack<Snapshot> stack = getSnapshots(PuzzleParser.parse(maps));
        printAllMoves(stack);
    }

    private static Stack<Snapshot> getSnapshots(String[][] maps) {
        Snapshot snapshot = new Game(maps).find();
        Stack<Snapshot> stack = new Stack<>();
        while (snapshot != null) {
            stack.add(snapshot);
            snapshot = snapshot.getParent();
        }
        return stack;
    }

    private static void printAllMoves(Stack<Snapshot> stack) {
        if (null == stack || stack.isEmpty()) {
            System.out.println("There seems no answer for the puzzle :(");
            return;
        }
        int steps = 0;
        while (!stack.empty()) {
            Snapshot temp = stack.pop();
            System.out.println("----------------");
            temp.print();
            steps++;
        }
        System.out.println("total step: "+steps);
    }

    */

}


