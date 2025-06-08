import java.util.*;
import java.io.*;

public class Main {

    static int[][][] board;
    static boolean[][] visited;
    static ArrayList<Point>[] rails;
    static int[] tail;
    static int n,m,k;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static int answer;

    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        init();

        for(int round = 1; round <= k; round++) {
            moveRail();

            throwBall(round);

            //System.out.println(round+" "+answer);
        }

        System.out.print(answer);
    }

    private static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        board = new int[n+1][n+1][2];
        visited = new boolean[n+1][n+1];
        rails = new ArrayList[m+1];
        tail = new int[m+1];
        for(int i=1; i<=m; i++) {
            rails[i] = new ArrayList<>();
        }

        int idx = 1;
        for(int i=1; i<=n; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=1; j<=n; j++) {
                board[i][j][0] = Integer.parseInt(st.nextToken());
                if(board[i][j][0] == 1) rails[idx++].add(new Point(i, j));
            }
        }

        for(int i=1; i<=m; i++) {
            Point p = rails[i].get(0);
            dfs(i, p.x, p.y);
        }
    }

    private static void dfs(int idx, int x, int y) {
        visited[x][y] = true;
        board[x][y][1] = idx;
        if(board[x][y][0] == 3) tail[idx] = rails[idx].size() - 1;

        for(int i=0; i<4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if(!inRange(nx, ny) || board[nx][ny][0] == 0 || visited[nx][ny]) continue;

            if(rails[idx].size() == 1 && board[nx][ny][0] != 2) continue;

            rails[idx].add(new Point(nx, ny));
            dfs(idx, nx, ny);
        }
    }

    private static boolean inRange(int x, int y) {
        return x >= 1 && x <= n && y >= 1 && y <= n;
    }

    private static void moveRail() {
        for(int i=1; i<=m; i++) {
            ArrayList<Point> rail = rails[i];
            Point last = rail.get(rail.size() - 1);
            rail.remove(rail.size() - 1);
            rail.add(0, last);
        }

        for(int i=1; i<=m; i++) {
            for(int j=0; j<rails[i].size(); j++) {
                Point p = rails[i].get(j);
                if(j == 0) board[p.x][p.y][0] = 1;
                else if(j < tail[i]) board[p.x][p.y][0] = 2;
                else if(j == tail[i]) board[p.x][p.y][0] = 3;
                else board[p.x][p.y][0] = 4;
            }
        }

        //printBoard();
    }

    private static void throwBall(int round) {
        // 여기서 틀림
        // round %= (n * 4);
        round = (round - 1) % (n * 4) + 1;

        if(round <= n) {
            // 왼쪽에서 오른쪽
            for(int i=1; i<=n; i++) {
                if(board[round][i][1] != 0 && board[round][i][0] <= 3) {
                    answer += getScore(round, i);
                    reverseRail(round, i);
                    break;
                }
            }
        }else if(round <= n * 2) {
            // 아래에서 위쪽
            round -= n;
            for(int i=n; i>=1; i--) {
                if(board[i][round][1] != 0 && board[i][round][0] <= 3) {
                    answer += getScore(i, round);
                    reverseRail(i, round);
                    break;
                }
            }
        }else if(round <= n * 3) {
            // 오른쪽에서 왼쪽
            round -= n*2;
            for(int i=n; i>=1; i--) {
                if(board[n-round+1][i][1] != 0 && board[n-round+1][i][0] <= 3) {
                    answer += getScore(n-round+1, i);
                    reverseRail(n-round+1, i);
                    break;
                }
            }
        }else {
            // 위에서 아래쪽
            round -= n*3;
            for(int i=1; i<=n; i++) {
                if(board[i][n-round+1][1] != 0 && board[i][n-round+1][0] <= 3) {
                    answer += getScore(i, n-round+1);
                    reverseRail(i, n-round+1);
                    break;
                }
            }
        }
    }

    private static int getScore(int x, int y) {
        int idx = board[x][y][1];
        int cnt = 1;
        for (Point p : rails[idx]) {
            if (p.x == x && p.y == y) break;
            cnt++;
        }
        return cnt * cnt;
    }

    private static void reverseRail(int x, int y) {
        int idx = board[x][y][1];
        ArrayList<Point> newRail = new ArrayList<>();
        ArrayList<Point> befRail = rails[idx];

        for(int i=tail[idx]; i>=0; i--) {
            newRail.add(befRail.get(i));
        }

        for(int i=befRail.size()-1; i>tail[idx]; i--) {
            newRail.add(befRail.get(i));
        }

        rails[idx] = newRail;
        for(int i=0; i<rails[idx].size(); i++) {
            Point p = rails[idx].get(i);
            if(i == 0) board[p.x][p.y][0] = 1;
            else if(i < tail[idx]) board[p.x][p.y][0] = 2;
            else if(i == tail[idx]) board[p.x][p.y][0] = 3;
            else board[p.x][p.y][0] = 4;
        }
    }

    private static void printBoard() {
        StringBuilder sb = new StringBuilder();
        for(int i=1; i<=n; i++) {
            for(int j=1; j<=n; j++) {
                sb.append(board[i][j][0]).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
