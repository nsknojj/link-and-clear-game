package connecting;

import java.util.ArrayList;
import java.lang.*;

public class Map {
    static final int RC=Connecting.RC;
    static final int N=16;
    final static int blank=16;    
    static int [][] map=new int[RC][RC];
    static int lastx=0, lasty=0;
    static int total;
    //清理map
    static void clear() {
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++)
                map[i][j]=0;
    }
    //随机函数，随机一列数字，变换成二维矩阵之后画出游戏界面
    static void setRandom() {
        total=RC*RC-blank;
        ArrayList<Integer> list=new ArrayList<Integer>();
        for (int i=0;i<blank;i++) list.add(0);
        for (int i=0;i<total/2;i++){
            int type=(int)(Math.random()*N+1);
            list.add(type);
            list.add(type);
        }
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++) {
                int index=(int)(Math.random()*list.size());
                map[i][j]=list.get(index);
                list.remove(index);
            }
        Connecting.setBoard();
    }
    //随机打乱
    static void randomShuffle() {
        int [] sum=new int[N+1];
        ArrayList<Integer> list=new ArrayList<Integer>();
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++)
                list.add(map[i][j]);
        for (int i=0;i<RC;i++)
            for (int j=0;j<RC;j++) {
                int index=(int)(Math.random()*list.size());
                map[i][j]=list.get(index);
                list.remove(index);
            }
        Connecting.setBoard();
    }    
    //消除函数，消除一对数字，同时把总的块数减2
    static void reduce(int i, int j, int k, int l) {
        search(i, j, k, l);
        map[k][l]=map[i][j]=0;
        Connecting.erase(k, l, i, j, x3, y3, x4, y4);
        total-=2;
        if (total==0) Connecting.winGame();
    }
    //处理点击事件
    public static void click(int i, int j) {
        if (map[i][j]>0) {
            if(lastx>0||lasty>0)
                Connecting.setRim(lastx , lasty, false);  
            Connecting.setRim(i, j, true);
            // reduce
            if ((i!=lastx||j!=lasty)&&(map[i][j]==map[lastx][lasty])) {
                connect[i][j][lastx][lasty]=3;
                dfs(i, j, i, j, -1, 0);
                if (connect[i][j][lastx][lasty]<3){
                    reduce(i, j, lastx, lasty);
                }
            }
            lastx=i;
            lasty=j;
        }
    }
    //处理提示事件，寻找能连的块
    static void help() {
        for(int i=0;i<RC;i++)
            for(int j=0;j<RC;j++)
                if (map[i][j]>0){
                    for (int k=0;k<RC;k++)
                        for(int l=0;l<RC;l++)
                            connect[i][j][k][l]=3;
                    dfs(i, j, i, j, -1, 0);
                    for (int k=0;k<RC;k++)
                        for(int l=0;l<RC;l++)
                            if (map[k][l]==map[i][j]&&(i!=k||j!=l)) {
                                if (connect[i][j][k][l]<3){
                                    reduce(i, j, k, l);
                                    return;
                                }
                            }
                }
    }
    
    static int [][][][] connect=new int[RC][RC][RC][RC];
    //dfs求出点与点之间是否连通
    static void dfs(int x0, int y0, int curx, int cury, int lastdir, int dis)
    {
            if(lastdir==-1) //如果还没有进行搜索，那么递归向四个方向搜索，转弯次数为1
            {
                    dfs(x0,y0,curx-1,cury,1,0);
                    dfs(x0,y0,curx+1,cury,2,0);
                    dfs(x0,y0,curx,cury-1,3,0);
                    dfs(x0,y0,curx,cury+1,4,0);
            }
            if(curx<0||cury<0||curx>11||cury>11)return; //边界情况
            if(map[curx][cury] ==map[x0][y0]&&dis<3)    //搜索到了某个和起始位置值相同的点并且转弯次数小于3
            {
                    connect[x0][y0][curx] [cury] =dis;
            }
            if(map[curx] [cury] !=0)return;
            if(dis==3)return;
            if(lastdir==1)  //如果上一次已经朝某个方向搜索过，这个方向记录的转弯次数不变，其他方向加一
            {
                    dfs(x0,y0,curx-1,cury,1,dis);
                    dfs(x0,y0,curx+1,cury,2,dis+1);
                    dfs(x0,y0,curx,cury-1,3,dis+1);
                    dfs(x0,y0,curx,cury+1,4,dis+1);
            }
            if(lastdir==2)
            {
                    dfs(x0,y0,curx-1,cury,1,dis+1);
                    dfs(x0,y0,curx+1,cury,2,dis);
                    dfs(x0,y0,curx,cury-1,3,dis+1);
                    dfs(x0,y0,curx,cury+1,4,dis+1);			
            }
            if(lastdir==3)
            {
                    dfs(x0,y0,curx-1,cury,1,dis+1);
                    dfs(x0,y0,curx+1,cury,2,dis+1);
                    dfs(x0,y0,curx,cury-1,3,dis);
                    dfs(x0,y0,curx,cury+1,4,dis+1);			
            }
            if(lastdir==4)
            {
                    dfs(x0,y0,curx-1,cury,1,dis+1);
                    dfs(x0,y0,curx+1,cury,2,dis+1);
                    dfs(x0,y0,curx,cury-1,3,dis+1);
                    dfs(x0,y0,curx,cury+1,4,dis);			
            }
    }
    
    final static int [][] D=new int[][] {{1,0},{-1,0},{0,1},{0,-1}};
    public static int x3=-1, x4=-1, y3=-1, y4=-1;
    // 枚举法求出两个块的实际路径
    static void search(int x0, int y0, int x1, int y1)
    {
        x3=-1; y3=-1;
        x4=-1; y4=-1;        
        boolean flag=false;
        for(int i=0;i<RC;i++) {
            flag=true;
            for(int j=x0+1;j<=i;j++)
                if (map[j][y0]>0) flag=false;
            for(int j=i;j<x0;j++)
                if (map[j][y0]>0) flag=false;
            for(int j=x1+1;j<=i;j++)
                if (map[j][y1]>0) flag=false;
            for(int j=i;j<x1;j++)
                if (map[j][y1]>0) flag=false;
            for(int j=y0;j<=y1;j++)
                if ((map[i][j]>0)&&((i!=x0)||(j!=y0))&&((i!=x1)||(j!=y1))) flag=false;
            if (flag) {
                x3=i; y3=y1;
                x4=i; y4=y0;
                return;
            }
        }
        
        for(int i=0;i<RC;i++) {
            flag=true;
            for(int j=y0+1;j<=i;j++)
                if (map[x0][j]>0) flag=false;
            for(int j=i;j<y0;j++)
                if (map[x0][j]>0) flag=false;
            for(int j=y1+1;j<=i;j++)
                if (map[x1][j]>0) flag=false;
            for(int j=i;j<y1;j++)
                if (map[x1][j]>0) flag=false;
            for(int j=x0;j<=x1;j++)
                if ((map[j][i]>0)&&((j!=x0)||(i!=y0))&&((j!=x1)||(i!=y1))) flag=false;
            if (flag) {
                x3=x1; y3=i;
                x4=x0; y4=i;
                return;
            }
        }
    }	

}
