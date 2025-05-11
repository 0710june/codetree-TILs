import java.util.*;
import java.io.*;

// 루돌프의 반란
public class Main {

    static int N, M, P, C, D;
    static int[][] board;
    static int[] dx = {-1, 0, 1, 0, -1, 1, 1, -1};
    static int[] dy = {0, 1, 0, -1, 1, 1, -1, -1};
    static Rudolf rudolf;
    static ArrayList<Santa> santas = new ArrayList<>();

    static class Rudolf {
        int x, y, d;

        Rudolf(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            board[this.x][this.y] = 0;
            Santa santa = null;
            for(Santa s : santas) {
                if(s.isDead) continue;
                if(santa == null || s.isBetter(santa)) {
                    santa = s;
                }
            }

            int minDist = getDistance(this.x, this.y, santa.x, santa.y);
            for(int i=0; i<8; i++) {
                int nx = this.x + dx[i];
                int ny = this.y + dy[i];
                if(!inRange(nx, ny)) continue;
                int curDist = getDistance(santa.x, santa.y, nx, ny);
                if(curDist >= minDist) continue;
                minDist = curDist;
                this.d = i;
            }
            this.x += dx[d];
            this.y += dy[d];

            // 충돌
            if(board[this.x][this.y] != 0) {
                santa = santas.get(board[this.x][this.y] - 1);
                santa.point += C;
                santa.x += dx[d] * C;
                santa.y += dy[d] * C;
                santa.stun = 2;
                if(!inRange(santa.x, santa.y)) santa.isDead = true;
                else if(board[santa.x][santa.y] == 0) board[santa.x][santa.y] = santa.idx;
                // 상호작용
                else {
                    Santa nextSanta = santas.get(board[santa.x][santa.y]-1);
                    board[santa.x][santa.y] = santa.idx;
                    interaction(nextSanta, d);
                }
            }
            board[this.x][this.y] = -1;
        }
    }

    static class Santa implements Comparable<Santa>{
        int idx, x, y, d;
        int dist, point, stun;
        boolean isDead;

        Santa(int idx, int x, int y) {
            this.idx = idx;
            this.x = x;
            this.y = y;
        }

        public void setDistance() {
            dist = (rudolf.x-x)*(rudolf.x-x) + (rudolf.y-y)*(rudolf.y-y);
        }

        public void move() {
            board[x][y] = 0;
            setDistance();
            int minDist = dist;
            for(int i=0; i<4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if(!inRange(nx, ny) || board[nx][ny] > 0) continue;
                int curDist = getDistance(rudolf.x, rudolf.y, nx, ny);
                if(curDist < minDist) {
                    minDist = curDist;
                    d = i;
                }
            }
            if(minDist == dist) {
                board[x][y] = idx;
                return;
            }
            x += dx[d];
            y += dy[d];

            // 충돌
            if(board[x][y] == -1) {
                stun = 2;
                point += D;
                d = (d + 2) % 4;
                x += dx[d] * D;
                y += dy[d] * D;
                if(!inRange(x, y)) {
                    isDead = true;
                    return;
                }
                if(board[x][y] > 0) {
                    Santa nextSanta = santas.get(board[x][y]-1);
                    board[x][y] = idx;
                    interaction(nextSanta, d);
                }else board[x][y] = idx;
            }
            else board[x][y] = idx;
        }

        public boolean isBetter(Santa s) {
            if(this.dist != s.dist) return this.dist < s.dist;
            if(this.x != s.x) return this.x > s.x;
            else return this.y > s.y;
        }

        @Override
        public int compareTo(Santa o) {
            return this.idx - o.idx;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());
        board = new int[N+1][N+1];

        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        rudolf = new Rudolf(r, c);
        board[r][c] = -1;

        for(int i=0; i<P; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            r = Integer.parseInt(st.nextToken());
            c = Integer.parseInt(st.nextToken());
            Santa santa = new Santa(idx, r, c);
            santas.add(santa);
            board[r][c] = idx;
        }
        Collections.sort(santas);

        StringBuilder sb = new StringBuilder();
        for(int m=1; m<=M; m++) {
            for(Santa santa : santas) {
                if(santa.isDead) continue;
                if(santa.stun > 0) santa.stun -= 1;
                santa.setDistance();
            }

            // 1. 루돌프 움직이기
            rudolf.move();

            // 2. 산타 움직이기
            for(Santa santa : santas) {
                if(santa.isDead) continue;
                if(santa.stun > 0) continue;
                santa.move();
            }

            // 3. 게임 종료 검사
            boolean isAllDead = true;
            sb = new StringBuilder();
            for(Santa santa : santas) {
                if(!santa.isDead) {
                    isAllDead = false;
                    santa.point += 1;
                }
                sb.append(santa.point).append(" ");
            }
            if(isAllDead) break;
        }

        System.out.print(sb.toString());
    }

    public static void interaction(Santa santa, int d) {
        santa.x += dx[d];
        santa.y += dy[d];
        if(!inRange(santa.x, santa.y)) {
            santa.isDead = true;
            return;
        }
        if(board[santa.x][santa.y] == 0) {
            board[santa.x][santa.y] = santa.idx;
            return;
        }
        Santa nextSanta = santas.get(board[santa.x][santa.y] - 1);
        board[santa.x][santa.y] = santa.idx;
        interaction(nextSanta, d);
    }

    public static int getDistance(int r1, int c1, int r2, int c2) {
        return (r1-r2)*(r1-r2)+(c1-c2)*(c1-c2);
    }

    public static boolean inRange(int x, int y) {
        return x >= 1 && y >= 1 && x <= N && y <= N;
    }

}
