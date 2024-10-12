import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {

    static int[][][] board = new int[4][4][3];
    static int[] packman = new int[2];
    static int max;
    static int[] path = new int[3];
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};
    static int[] mx = {-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] my = {0, -1, -1, -1, 0, 1, 1, 1};
    static ArrayList<Monster> mlist = new ArrayList<>();
    static ArrayList<Ghost> glist = new ArrayList<>();
    static ArrayDeque<Monster> mq = new ArrayDeque<>();

    public static void main(String[] args) throws Exception {
        int turn = init();

        for (int i = 0; i < turn; i++) {
            // 1. 몬스터 복제 시도
            for (Monster m : mlist) {
                m.duplicate();
            }

            // 2. 몬스터 이동
            for (Monster m : mlist) {
                m.move();
            }

            // 3. 팩맨 이동
            max = 0;
            movePackman(packman[0], packman[1], 0, 0, new int[3]);
            board[packman[0]][packman[1]][0]--;
            for (int p : path) {
                packman[0] += dx[p];
                packman[1] += dy[p];
                while (board[packman[0]][packman[1]][1] > 0) {
                    glist.add(new Ghost(packman[0], packman[1]));
                    board[packman[0]][packman[1]][1]--;
                }
            }
            board[packman[0]][packman[1]][0]++;

            // 4. 몬스터 시체 소멸
            for (Ghost g : glist) {
                g.remove();
            }

            // 5. 몬스터 복제 완성
            while (!mq.isEmpty()) {
                Monster m = mq.poll();
                board[m.x][m.y][1]++;
            }
        }

        System.out.println(countMonster());
    }

    public static int init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int m = Integer.parseInt(st.nextToken());
        int t = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken()) - 1;
        int c = Integer.parseInt(st.nextToken()) - 1;
        packman = new int[]{r, c};
        board[r][c][0] = 1;

        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            r = Integer.parseInt(st.nextToken()) - 1;
            c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            board[r][c][1]++;
            mlist.add(new Monster(r, c, d));
        }

        return t;
    }

    private static boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x < 4 && y < 4;
    }

    private static void movePackman(int r, int c, int step, int mcnt, int[] p) {
        if (step == 3) {
            if (mcnt > max) {
                max = mcnt;
                System.arraycopy(p, 0, path, 0, 3);
            }
            return;
        }

        for (int j = 0; j < 4; j++) {
            int nx = r + dx[j];
            int ny = c + dy[j];
            p[step] = j;
            if (!inRange(nx, ny)) continue;
            movePackman(nx, ny, step + 1, mcnt + board[nx][ny][1], p);
        }
    }

    private static int countMonster() {
        int cnt = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j][1] > 0) {
                    cnt += board[i][j][1];
                }
            }
        }
        return cnt;
    }

    static class Ghost {
        int x, y, turn;

        Ghost(int x, int y) {
            this.x = x;
            this.y = y;
            this.turn = 2;
            board[x][y][2]++;
        }

        public void remove() {
            if (turn == 0) board[x][y][2] -= 1;
            else turn--;
        }
    }

    static class Monster {
        int x, y, dir;

        Monster(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        public void duplicate() {
            mq.add(new Monster(x, y, dir));
        }

        public void move() {
            for (int i = 0; i < 8; i++) {
                int nx = x + mx[dir];
                int ny = y + my[dir];
                if (!inRange(nx, ny) || board[nx][ny][0] == 1 || board[nx][ny][2] == 1) {
                    dir = (dir + 1) % 8;
                } else {
                    board[x][y][1] -= 1;
                    x = nx;
                    y = ny;
                    board[x][y][1] += 1;
                    break;
                }
            }
        }
    }
}