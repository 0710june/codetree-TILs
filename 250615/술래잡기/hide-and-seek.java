import java.util.*;
import java.io.*;

// 술래잡기
public class Main {

    static int n, m, h, k;
    static int[][] board;
    static boolean[][] tree;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int[] rx = {1, 0, -1, 0};
    static int[] ry = {0, 1, 0, -1};
    static ArrayList<Fugitive> fugitives = new ArrayList<>();
    static int answer;
    static Tagger tagger;

    static class Tagger {
        int x, y, d;
        boolean isRev;
        int cnt,  turn, moveCnt;

        Tagger(int x, int y) {
            this.x = x;
            this.y = y;
            cnt = 1;
            turn = 2;
            moveCnt = 1;
        }

        public void move() {
            x += dx[d];
            y += dy[d];

            if(x == 1 && y == 1) {
                isRev = !isRev;
                cnt = n-1;
                turn = 1;
                moveCnt = n;
                d = 0;
                return;
            }

            cnt -= 1;
            if(cnt == 0) {
                turn -= 1;
                if (turn == 0) {
                    moveCnt += 1;
                    turn = 2;
                }
                cnt = moveCnt;
                d = (d + 1) % 4;
            }
        }

        public void moveRev() {
            x += rx[d];
            y += ry[d];

            if(x == n/2+1 && y == n/2+1) {
                isRev = !isRev;
                cnt = 1;
                turn = 2;
                moveCnt = 1;
                d = 0;
                return;
            }

            cnt -= 1;
            if(cnt == 0) {
                turn -= 1;
                if (turn == 0) {
                    moveCnt -= 1;
                    turn = 2;
                }
                cnt = moveCnt;
                d = (d + 1) % 4;
            }
        }

        public int catchFugitives() {
            int cnt = 0;
            int tx = x, ty = y;
            for(int i=0; i<3; i++) {
                if(i != 0) {
                    if(!isRev) {
                        tx += dx[d];
                        ty += dy[d];
                    } else {
                        tx += rx[d];
                        ty += ry[d];
                    }
                }

                if(!inRange(tx, ty)) break;
                if(tree[tx][ty] || board[tx][ty] == 0) continue;

                cnt += board[tx][ty];
                for(Fugitive f : fugitives) {
                    if(f.x == tx && f.y == ty) f.isCaught = true;
                }
                board[tx][ty] = 0;
            }
            return cnt;
        }
    }

    static class Fugitive {
        int x, y, d;
        boolean isCaught;

        Fugitive(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }

        public void move() {
            if(isCaught || !canMove()) return;
            int nx = x + dx[d];
            int ny = y + dy[d];
            if(!inRange(nx, ny)) d = (d + 2) % 4;
            nx = x + dx[d];
            ny = y + dy[d];
            if(nx == tagger.x && ny == tagger.y) return;
            board[x][y] -= 1;
            board[nx][ny] += 1;
            x = nx;
            y = ny;
        }

        private boolean canMove() {
            return Math.abs(x - tagger.x) + Math.abs(y - tagger.y) <= 3;
        }
    }

    public static void main(String[] args) throws Exception {
        init();

//        System.out.println(tagger.x+" : "+tagger.y);
        for(int i=1; i<=k; i++) {
            moveFugitives();

            moveTagger(i);
//            System.out.println(tagger.x+" : "+tagger.y);

            if(isOver()) break;
        }

        System.out.print(answer);
    }

    private static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        board = new int[n+1][n+1];
        tree = new boolean[n+1][n+1];
        tagger = new Tagger(n/2+1, n/2+1);

        for(int i=0; i<m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            fugitives.add(new Fugitive(x, y, d));
            board[x][y] += 1;
        }

        for(int i=0; i<h; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            tree[x][y] = true;
        }
    }

    private static void moveFugitives() {
        for(Fugitive f : fugitives) {
            f.move();
        }
    }

    private static void moveTagger(int turn) {
        if(!tagger.isRev) tagger.move();
        else tagger.moveRev();
        int score = tagger.catchFugitives();
//        System.out.println(turn+"턴 : "+(turn*score));
        answer += turn * score;
    }

    private static boolean isOver() {
        for(int i=1; i<=n; i++) {
            for(int j=1; j<=n; j++) {
                if(board[i][j] > 0) return false;
            }
        }
        return true;
    }

    private static boolean inRange(int x, int y) {
        return x >= 1 && x <= n && y >= 1 && y <= n;
    }
}
