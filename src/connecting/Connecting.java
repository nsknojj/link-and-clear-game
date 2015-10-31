package connecting;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author nsknojj
 */
public class Connecting extends JFrame {
    //全局变量定义
    static Font font = new Font("宋体", 0, 15 );
    static final int RC=12;
    static RButton [][] block=new RButton[RC][RC]; 
    static Graphics g;
    static final int rsize=46;
    static final int px=25, py=100;
    static JPanel toolBar=new JPanel();
    static JPanel actionBar=new JPanel();
    static JPanel playGround=new JPanel();  
    static JButton btShuffle=new JButton();
    static JButton btHelp=new JButton();
    static Connecting game;
    static int shuffleTime, helpTime;
    // 构造函数定义
    public Connecting() {
        this.setSize(700, 650);
        this.setTitle("连连看 by 张闻涛 陈子卿");
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        // set component
        getContentPane().add("North", toolBar);
        getContentPane().add("East",actionBar);
        getContentPane().add("Center", playGround);
        // 设置游戏界面的大小等属性
        toolBar.setPreferredSize(new Dimension(700, 50));
        actionBar.setPreferredSize(new Dimension(100, 600));
        playGround.setPreferredSize(new Dimension(600, 600));
        // set border
        Border border=BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK);
        toolBar.setBorder(border);
        actionBar.setBorder(border);
        playGround.setBorder(border);
        //playGround.setBackground(Color.BLACK);
        //actionBar.setBackground(Color.YELLOW);
        
        // place button at PLAYGROUND
        playGround.setLayout(new GridLayout(RC, RC));
        //添加144个按钮作为连连看的游戏主界面，初始化为不可见
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++){
                // set block
                block[i][j]=new RButton();
                block[i][j].setVisible(false);
                block[i][j].setActionCommand("" + (i*RC+j));
                block[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        int offset=Integer.parseInt(((JButton)e.getSource()).getActionCommand());
                        Map.click(offset/RC, offset%RC);
                    }
                });
                playGround.add(block[i][j]);
            }
        
        // place button at TOOLBAR
        toolBar.setLayout(new GridLayout(1,0));
        //添加开始游戏按钮，点击后调用start()函数开始游戏
        JButton btStart=new JButton("开始游戏");
        btStart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    start();
                }
        });
        btStart.setFont(font);
        toolBar.add(btStart);
        toolBar.add(timeLabel);
        toolBar.add(bestLabel);
        timeLabel.setFont(font);
        bestLabel.setFont(font);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);           
        bestLabel.setHorizontalAlignment(SwingConstants.CENTER);           
        // place button at ACTIONBAR
        actionBar.setLayout(new GridLayout(0,1));
        //设置按钮的属性，设置计时器等的属性
        btShuffle.setText("<html>随机打乱<br>剩余"+shuffleTime+"次</html>");
        btShuffle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    if (shuffleTime>0) {
                        shuffleTime--;
                        Map.randomShuffle();
                        btShuffle.setText("<html>随机打乱<br>剩余"+shuffleTime+"次</html>");
                    }
                }
        });
        btShuffle.setFont(font);
        actionBar.add(btShuffle);
        // 设置提示信息
        btHelp.setText("<html>提示<br>剩余"+helpTime+"次</html>");
        btHelp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    if (helpTime>0) {
                        helpTime--;
                        Map.help();
                        btHelp.setText("<html>提示<br>剩余"+helpTime+"次</html>");
                    }
                }
        });
        btHelp.setFont(font);
        actionBar.add(btHelp);
        
        this.validate();
    }
    // 提取图标的函数
    static ImageIcon getImageIcon(String path, int width, int height) {
        ImageIcon icon=new ImageIcon(path);
        if (width==0||height==0) return icon;
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
        return icon;
    }
    // 根据Map来布置board
    static void setBoard(){
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++){
                setBlock(i,j);
            }
    }
    // 设置1个块的状态
    static void setBlock(int i, int j){
        int type=Map.map[i][j];
        if (type==0) block[i][j].setVisible(false);
        else {
            block[i][j].setVisible(true);
            // get block's image, set size
            ImageIcon btnImg=getImageIcon("img/"+type+".jpg", 50, 50);
            block[i][j].setIcon(btnImg);
            block[i][j].setRim(false);
            block[i][j].repaint();
        }
    }
    // display rim
    //点击到某个块之后设置这个地方被点击了
    //通过JButton的继承类RButton来绘制红框
    static void setRim(int i,int j, boolean _rim) {
        block[i][j].setRim(_rim);
        block[i][j].repaint();
    }
    
    // erase block
    //给定四个点，分别为起始点&终止点（这两个点是要消除的点对）
    //第三个和第四个点是可选的参数，表示至多2次转弯的位置
    //之后依次连接这几个点，并把需要匹配的点对消除重绘
    static void erase(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        if (x3!=-1) {
            draw(x1, y1, x3, y3);
            if (x4!=-1) {
                draw(x3, y3, x4, y4);
                draw(x4, y4, x2, y2);
            }
            else draw(x3, y3, x2, y2);
        }
        else draw(x1, y1, x2, y2);
        try{Thread.sleep(100);}catch(Exception e){}
        block[x1][y1].setVisible(false);
        block[x2][y2].setVisible(false);
        game.repaint();
    }
    // 绘制一条线
    static void draw(int x1, int y1, int x2, int y2) {
        Point p1=block[x1][y1].getLocation();
        Point p2=block[x2][y2].getLocation();
        int i1=(int)p1.getX()+px, j1=(int)p1.getY()+py;
        int i2=(int)p2.getX()+px, j2=(int)p2.getY()+py;
        g.setColor(Color.BLUE);
        g.drawLine(i1, j1, i2, j2);
        if (x1==x2){
            g.drawLine(i1, j1+1, i2, j2+1);
            g.drawLine(i1, j1-1, i2, j2-1);            
        }
        else
        if (y1==y2){
            g.drawLine(i1+1, j1, i2+1, j2);
            g.drawLine(i1-1, j1, i2-1, j2);

        }
        //try{Thread.sleep(200);}catch(Exception e){}
    }
    // 结算成绩
    static void calcScore() throws IOException {
        File file=new File("log/best.txt");
        DataInputStream din=new DataInputStream(new FileInputStream(file));
        try{best=Integer.parseInt(din.readUTF());}catch(EOFException e){}
        if (totalTime<best) {
            best=totalTime;
            DataOutputStream dout=new DataOutputStream(new FileOutputStream(file));            
            dout.writeUTF(String.valueOf(best));
        }
        setBest();
    }
    // 成功消除所有方块
    static void winGame() {
        startF=false;
        JDialog dialog = new JDialog(game);
        dialog.setSize(300, 200);
        dialog.setLocation(game.getWidth()/2-dialog.getWidth()/2, game.getHeight()/2-dialog.getHeight()/2);
        dialog.setVisible(true);
        JLabel winLabel=new JLabel();
        winLabel.setFont(font);
        winLabel.setText("你赢了");
        if (totalTime<best) {
            winLabel.setText("你赢了 你打败了最高记录！");
        }
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);        
        dialog.add(winLabel);
        dialog.show();
        try {
            calcScore();
        } catch (IOException ex) {
            Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // 游戏开始的函数
    void start(){
        shuffleTime=3;
        helpTime=99;
        btShuffle.setText("<html>随机打乱<br>剩余"+shuffleTime+"次</html>");
        btHelp.setText("<html>提示<br>剩余"+helpTime+"次</html>");
        Map.setRandom();
        totalTime=0;
        startF=true;
        setTime();
    }
    
    static boolean startF=false;
    static JLabel timeLabel=new JLabel("此处是计时器");
    static JLabel bestLabel=new JLabel("最短时间");
    static int totalTime=0, best=0xFFFFFF;
    // 设置计时器
    static void setTime(){
        int a=totalTime/60, b=totalTime%60;
        String s=" ";
        if (a<10) s+="0";
        s=s+a+":";
        if (b<10) s+="0";
        s=s+b;
        timeLabel.setText(s);
    }
    // 设置最佳成绩
    static void setBest(){
        if (best>3600) {
            bestLabel.setText("最短时间 N/A");
            return;
        }
        int a=best/60, b=best%60;
        String s=" ";
        if (a<10) s+="0";
        s=s+a+":";
        if (b<10) s+="0";
        s=s+b;
        bestLabel.setText("最短时间 "+s);
    }    
    
    public static void main(String[] args) {
        Map.clear();
        game=new Connecting();
        g=game.getGraphics();
        totalTime=0xFFFFFF;
        try {
            calcScore();
        } catch (IOException ex) {
            Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
        }
        totalTime=0;
        while(true){
            try{Thread.sleep(1000);}catch(Exception e){}
            if (startF) totalTime++;
            setTime();
        }                
    }
    
}