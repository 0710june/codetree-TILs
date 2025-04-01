import java.io.*;
import java.util.*;

public class Main {

    static int N, M, K;
    static Point EXIT;
    static int[][][] map;
    static ArrayList<Point> plist = new ArrayList<>();
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static class Point {
        int x, y, cnt;
        boolean isEscaped;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            int curDist = getDistToExit(x, y);
            int min = Integer.MAX_VALUE;
            int idx = -1;
            for(int i=0; i<4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if(!InRange(nx, ny) || map[nx][ny][0] > 0) continue;
                int nextDist = getDistToExit(nx, ny);
                if(curDist <= nextDist) continue;
                if(nextDist >= min) continue;
                min = nextDist;
                idx = i;
            }

            if(idx == -1) return;
            map[x][y][1] -= 1;
            x += dx[idx];
            y += dy[idx];
            cnt += 1;
            if(x == EXIT.x && y == EXIT.y) {
                isEscaped = true;
            } else {
                map[x][y][1] += 1;
            }
        }

        public void rotate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equal(int x, int y) {
            return x == this.x && y == this.y;
        }

        @Override
        public String toString() {
            return x+" "+y;
        }
    }

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N+1][N+1][2];

        for(int i=1; i<=N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=1; j<=N; j++) {
                map[i][j][0] = Integer.parseInt(st.nextToken());
            }
        }

        for(int i=0; i<M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            plist.add(new Point(x, y));
            map[x][y][1] += 1;
        }

        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        EXIT = new Point(x, y);
        map[x][y][1] = -1;

        for(int i=1; i<=K; i++) {
            // 1. 전원 탈출 확인
            boolean isAllEscaped = true;
            for(Point p : plist) {
                if(!p.isEscaped) {
                    isAllEscaped = false;
                    break;
                }
            }
            if(isAllEscaped) break;

            // 1. 이동
            for(Point p : plist) {
                if(p.isEscaped) continue;
                p.move();
            }

            // 2. 회전
            rotate();
        }

        int sum = 0;
        for(Point p : plist) {
            sum += p.cnt;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(sum).append("\n").append(EXIT.toString());
        System.out.print(sb.toString());
    }

    public static void rotate() {
        int[] p = findPoint();
        int x = p[0];
        int y = p[1];
        int r = p[2];
        int[][][] tmp = new int[r][r][2];

        for(int i=0; i<r; i++) {
            for(int j=0; j<r; j++) {
                if(map[x+i][y+j][0] > 0) tmp[j][r-1-i][0] = map[x+i][y+j][0] - 1;
                if(map[x+i][y+j][1] != 0) {
                    tmp[j][r-1-i][1] = map[x+i][y+j][1];
                    if(map[x+i][y+j][1] > 0) {
                        for(Point person : plist) {
                            if(person.equal(x+i,y+j)) person.rotate(x+j, y+r-1-i);
                        }
                    } else {
                        EXIT.rotate(x+j, y+r-1-i);
                    }
                }
            }
        }

        for(int i=0; i<r; i++) {
            for(int j=0; j<r; j++) {
                map[x+i][y+j][0] = tmp[i][j][0];
                map[x+i][y+j][1] = tmp[i][j][1];
            }
        }
    }

    public static int[] findPoint() {
        int r = 2;
        while(true) {
            for(int i=EXIT.x-r+1; i<=EXIT.x; i++) {
                for(int j=EXIT.y-r+1; j<=EXIT.y; j++) {
                    if(!InRange(i,j)) continue;
                    if(isSqure(i, j, r)) {
                        return new int[] {i, j, r};
                    }
                }
            }
            r++;
        }
    }

    public static boolean isSqure(int x, int y, int r) {
        for(int i=x; i<=x+r-1; i++) {
            for(int j=y; j<=y+r-1; j++) {
                if(map[i][j][1] > 0) return true;
            }
        }
        return false;
    }

    public static int getDistToExit(int x, int y) {
        return Math.abs(EXIT.x - x) + Math.abs(EXIT.y - y);
    }

    public static boolean InRange(int x, int y) {
        return x >= 1 && y >= 1 && x <= N && y <= N;
    }
}
