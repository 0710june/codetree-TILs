import java.util.*;
import java.io.*;

// 팩맨
public class Main {

    static int m, t;
    static Point packman;
    static int[] dx = {0, -1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dy = {0, 0, -1, -1, -1, 0, 1, 1, 1};
    static int[] px = {-1, 0, 1, 0};
    static int[] py = {0, -1, 0, 1};
    static int[][][] board = new int[5][5][9];
    static ArrayList<int[]> eggs = new ArrayList<>();
    static ArrayList<int[]> ghosts = new ArrayList<>();
    static int[] packmanPath = new int[3];
    static int max;
    static ArrayList<int[]> moveList;

    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        init();

        for(int turn=1; turn<=t; turn++) {
            // 1. 몬스터 복제 시도
            createEgg();

            // 2. 몬스터 이동
            moveMonster();

            // 3. 팩맨 이동
            movePackman();

            // 4. 몬스터 시체 소멸
            removeGhost();

            // 5. 몬스터 복제 완성
            duplicateMonster();
        }

        System.out.print(countMonster());
    }

    private static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        m = Integer.parseInt(st.nextToken());
        t = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        packman = new Point(r, c);

        for(int i=0; i<m; i++) {
            st = new StringTokenizer(br.readLine());
            r = Integer.parseInt(st.nextToken());
            c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            board[r][c][d] += 1;
        }
    }

    // 1. 몬스터 복제 시도
    private static void createEgg() {
        for(int i=1; i<=4; i++) {
            for(int j=1; j<=4; j++) {
                for(int k=1; k<=8; k++) {
                    if(board[i][j][k] > 0) eggs.add(new int[] {i, j, k, board[i][j][k]});
                }
            }
        }
    }

    // 2. 몬스터 이동
    private static void moveMonster() {
        moveList = new ArrayList<>();
        for(int i=1; i<=4; i++) {
            for(int j=1; j<=4; j++) {
                for(int k=1; k<=8; k++) {
                    if(board[i][j][k] > 0) move(i, j, k);
                }
            }
        }

        for(int[] ml : moveList) {
            int x = ml[0];
            int y = ml[1];
            int d = ml[2];
            int cnt = ml[3];
            int nx = ml[4];
            int ny = ml[5];
            int nd = ml[6];
            board[nx][ny][nd] += cnt;
            board[x][y][d] -= cnt;
        }
    }

    private static void move(int x, int y, int d) {
        int nd = d;
        for(int i=1; i<=7; i++) {
            int nx = x + dx[nd];
            int ny = y + dy[nd];

            // 격자를 벗어나는 경우
            if(!inRange(nx, ny)
                    || (nx == packman.x && ny == packman.y)
                    || board[nx][ny][0] > 0
            ) {
                nd = (nd % 8) + 1;
                continue;
            }

            moveList.add(new int[] {x, y, d, board[x][y][d], nx, ny, nd});
            break;
        }
    }

    // 3. 팩맨 이동
    private static void movePackman() {
        max = Integer.MIN_VALUE;
        dfs(packman.x, packman.y, 0, 0, new int[3]);

        for(int d : packmanPath) {
            packman.x += px[d];
            packman.y += py[d];
            for(int i=1; i<=8; i++) {
                board[packman.x][packman.y][0] += board[packman.x][packman.y][i];
                board[packman.x][packman.y][i] = 0;
            }
            if(board[packman.x][packman.y][0] > 0) {
                ghosts.add(new int[] {packman.x, packman.y, board[packman.x][packman.y][0], 2});
            }
        }
    }

    private static void dfs(int x, int y, int step, int cnt, int[] path) {
        if(step == 3) {
            if(cnt > max) {
                max = cnt;
                System.arraycopy(path, 0, packmanPath, 0, 3);
            }
            return;
        }

        for(int i=0; i<4; i++) {
            int nx = x + px[i];
            int ny = y + py[i];

            if(!inRange(nx, ny)) continue;

            int sum = 0;
            int[] tmp = new int[9];
            System.arraycopy(board[nx][ny], 0, tmp, 0, 9);
            for(int j=1; j<=8; j++) {
                sum += board[nx][ny][j];
                board[nx][ny][j] = 0;
            }
            path[step] = i;
            dfs(nx, ny, step+1, cnt+sum, path);
            System.arraycopy(tmp, 0, board[nx][ny], 0, 9);
        }
    }

    // 4. 몬스터 시체 소멸
    private static void removeGhost() {
        for(int i=ghosts.size()-1; i>=0; i--) {
            int[] ghost = ghosts.get(i);
            int x = ghost[0];
            int y = ghost[1];
            int cnt = ghost[2];
            int turn = ghost[3];

            if(turn == 0) {
                board[x][y][0] -= cnt;
                ghosts.remove(i);
            }
            else {
                ghost[3] -= 1;
            }
        }
    }

    // 5. 몬스터 복제 완성
    private static void duplicateMonster() {
        for(int i=eggs.size()-1; i>=0; i--) {
            int[] egg = eggs.get(i);
            int x = egg[0];
            int y = egg[1];
            int d = egg[2];
            int cnt = egg[3];

            board[x][y][d] += cnt;
            eggs.remove(i);
        }
    }

    // 6. 살아남은 몬스터 개수 출력
    private static int countMonster() {
        int sum = 0;
        for(int i=1; i<=4; i++) {
            for(int j=1; j<=4; j++) {
                for(int k=1; k<=8; k++) {
                    if(board[i][j][k] > 0) sum += board[i][j][k];
                }
            }
        }
        return sum;
    }

    private static boolean inRange(int x, int y) {
        return x >= 1 && x <= 4 && y >= 1 && y <= 4;
    }
}
