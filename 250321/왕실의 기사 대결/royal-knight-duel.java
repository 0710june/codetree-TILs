import java.util.*;
import java.io.*;

public class Main {

    static int L, N, Q;
    static int[][][] board;
    static Knight[] knights;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static ArrayList<Knight> moveList;

    static class Knight {
        int idx;
        int startX;
        int endX;
        int startY;
        int endY;
        int hp;
        int damage;

        Knight(int idx, int r, int c, int h, int w, int k) {
            this.idx = idx;
            this.startX = r;
            this.startY = c;
            this.endX = r + h - 1;
            this.endY = c + w - 1;
            this.hp = k;
        }

        public int init() {
            int cnt = 0;
            for(int i=startX; i<=endX; i++) {
                for(int j=startY; j<=endY; j++) {
                    board[i][j][1] = idx;
                    if(board[i][j][0] == 1) cnt++;
                }
            }
            return cnt;
        }

        public void delete() {
            for(int i=startX; i<=endX; i++) {
                for(int j=startY; j<=endY; j++) {
                    board[i][j][1] = 0;
                }
            }
        }

        public void attack(int d) {
            delete();
            startX += dx[d];
            startY += dy[d];
            endX += dx[d];
            endY += dy[d];
            init();
        }

        public void move(int d) {
            delete();
            startX += dx[d];
            startY += dy[d];
            endX += dx[d];
            endY += dy[d];
            damage = init();
            hp -= damage;
            if(hp <= 0) delete();
        }
    }

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        board = new int[L+1][L+1][2];
        for(int i=1; i<=L; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=1; j<=L; j++) {
                board[i][j][0] = Integer.parseInt(st.nextToken());
            }
        }

        knights = new Knight[N+1];
        for(int i=1; i<=N; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());
            knights[i] = new Knight(i, r, c, h, w, k);
            knights[i].init();
        }

        for(int i=0; i<Q; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            moveKnight(idx, d);
        }

        System.out.print(countDamage());
    }

    public static int countDamage() {
        int totalDamage = 0;
        for(int i=1; i<=N; i++) {
            if(knights[i].hp <= 0) continue;
            totalDamage += knights[i].damage;
        }
        return totalDamage;
    }

    public static void moveKnight(int idx, int d) {
        moveList = new ArrayList<>();
        getMovingKnight(idx, d);
        if(!moveList.isEmpty()) {
            for(int i=moveList.size()-1; i>=0; i--) {
                Knight knight = moveList.get(i);
                if(i == 0) knight.attack(d);
                else knight.move(d);
            }
        }
    }

    public static void getMovingKnight(int idx, int d) {
        Knight knight = knights[idx];
        moveList.add(knight);

        int startX, endX, startY, endY;
        if(d == 0) {
            startX = knight.startX - 1;
            endX = knight.startX - 1;
            startY = knight.startY;
            endY = knight.endY;
        }else if(d == 1) {
            startX = knight.startX;
            endX = knight.endX;
            startY = knight.endY + 1;
            endY = knight.endY + 1;
        }else if(d == 2) {
            startX = knight.endX + 1;
            endX = knight.endX + 1;
            startY = knight.startY;
            endY = knight.endY;
        }else {
            startX = knight.startX;
            endX = knight.endX;
            startY = knight.startY - 1;
            endY = knight.startY - 1;
        }

        for(int x=startX; x<=endX; x++) {
            for(int y=startY; y<=endY; y++) {
                if(!inRange(x, y) || board[x][y][0] == 2) {
                    moveList.clear();
                    return;
                }
            }
        }

        for(int x=startX; x<=endX; x++) {
            for(int y=startY; y<=endY; y++) {
                if(board[x][y][1] != 0) getMovingKnight(board[x][y][1], d);
                if(moveList.isEmpty()) return;
            }
        }
    }

    public static boolean inRange(int x, int y) {
        return x >= 1 && y >= 1 && x <= L && y <= L;
    }
}
