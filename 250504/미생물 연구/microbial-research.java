import java.util.*;
import java.io.*;

// 미생물 연구
public class Main {

    static int N, Q;
    static int[][] board;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static boolean[][] visited;
    static HashMap<Integer, ArrayList<int[]>> micros;
    static HashSet<Integer> deleteMicro;
    static int[] microCnt;
    static boolean[][] pair;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        board = new int[N][N];
        StringBuilder sb = new StringBuilder();

        for(int q=1; q<=Q; q++) {
            st = new StringTokenizer(br.readLine());
            int r1 = Integer.parseInt(st.nextToken());
            int c1 = Integer.parseInt(st.nextToken());
            int r2 = Integer.parseInt(st.nextToken());
            int c2 = Integer.parseInt(st.nextToken());

            // 1. 미생물 투입
            setMicro(r1, c1, r2, c2, q);

            // 2. 미생물 분해 확인
            checkDividedMicro();

            // 3. 미생물 이동
            moveMicro();

            // 4. 점수 계산
            if(q == 1) sb.append(0).append("\n");
            else sb.append(calcPoint()).append("\n");
        }

        System.out.print(sb.toString());
    }

    private static int calcPoint() {
        pair = new boolean[Q+1][Q+1];
        int sum = 0;
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                int cur = board[i][j];
                if(cur == 0) continue;

                for(int k=0; k<4; k++) {
                    int ni = i + dx[k];
                    int nj = j + dy[k];
                    if(!inRange(ni, nj)) continue;

                    int next = board[ni][nj];
                    if(next == 0 || next == cur) continue;

                    int a = Math.min(cur, next);
                    int b = Math.max(cur, next);

                    if(pair[a][b]) continue;
                    pair[a][b] = true;
                    sum += microCnt[a] * microCnt[b];
                }
            }
        }

        return sum;
    }

    private static void moveMicro() {
        int[][] tmp = new int[N][N];
        while(!micros.isEmpty()) {
            int maxKey = -1;
            int maxSize = -1;
            for(int key : micros.keySet()) {
                int size = micros.get(key).size();
                if(size > maxSize || (size == maxSize && key < maxKey)) {
                    maxKey = key;
                    maxSize = size;
                }
            }
            move(maxKey, tmp);
            micros.remove(maxKey);
        }
        board = tmp;
    }

    private static void move(int q, int[][] tmp) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for(int[] point : micros.get(q)) {
            minX = Math.min(minX, point[0]);
            minY = Math.min(minY, point[1]);
        }

        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                int offsetX = i - minX;
                int offsetY = j - minY;
                if(canMove(q, offsetX, offsetY, tmp)) {
                    for(int[] point : micros.get(q)) {
                        int nx = point[0] + offsetX;
                        int ny = point[1] + offsetY;
                        tmp[nx][ny] = q;
                    }
                    return;
                }
            }
        }
        microCnt[q] = 0;
    }

    private static boolean canMove(int q, int minX, int minY, int[][] tmp) {
        for(int[] point : micros.get(q)) {
            int nx = point[0] + minX;
            int ny = point[1] + minY;
            if(!inRange(nx, ny) || tmp[nx][ny] != 0) return false;
        }
        return true;
    }

    private static void checkDividedMicro() {
        visited = new boolean[N][N];
        micros = new HashMap<>();
        deleteMicro = new HashSet<>();
        microCnt = new int[Q+1];

        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                if(visited[i][j] || board[i][j] == 0) continue;
                bfs(i, j);
            }
        }

        for(int q : deleteMicro) {
            for(int[] p : micros.get(q)) {
                board[p[0]][p[1]] = 0;
            }
            micros.remove(q);
            microCnt[q] = 0;
        }
    }

    private static void bfs(int x, int y) {
        ArrayDeque<int[]> deq = new ArrayDeque<>();
        visited[x][y] = true;
        deq.add(new int[] {x, y});
        int q = board[x][y];
        if(!micros.containsKey(q)) micros.put(q, new ArrayList<>());
        else deleteMicro.add(q);
        micros.get(q).add(new int[] {x, y});

        while(!deq.isEmpty()) {
            int[] cur = deq.poll();

            for(int i=0; i<4; i++) {
                int nx = cur[0] + dx[i];
                int ny = cur[1] + dy[i];

                if(!inRange(nx, ny) || visited[nx][ny]) continue;
                if(board[nx][ny] != q) continue;

                visited[nx][ny] = true;
                deq.add(new int[]{nx, ny});
                micros.get(q).add(new int[]{nx, ny});
            }
        }
        microCnt[q] = micros.get(q).size();
    }

    private static void setMicro(int r1, int c1, int r2, int c2, int q) {
        for(int i=r1; i<r2; i++) {
            for(int j=c1; j<c2; j++) {
                board[i][j] = q;
            }
        }
    }

    private static boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x < N && y <N;
    }
}
