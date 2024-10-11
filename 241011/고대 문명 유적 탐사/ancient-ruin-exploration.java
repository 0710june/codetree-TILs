import java.util.*;
import java.io.*;

public class Main {

    static int K, M;
    static int[][] ruin = new int[5][5];
    static ArrayDeque<Integer> wall = new ArrayDeque<>();
    static StringBuilder answer = new StringBuilder();
    static int[] dx = {0, -1, 0, 1};
    static int[] dy = {-1, 0, 1, 0};
    static PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> o1[1] == o2[1] ? (o1[0] - o2[0]) * -1 : o1[1] - o2[1]);

    public static void main(String[] args) throws Exception {

        init();

        // 탐사진행
         for(int i=0; i<K; i++) {
             int piece = explore();
             if(piece > 0) {
                 if(answer.length() == 0) answer.append(piece);
                 else answer.append(" ").append(piece);
             }else break;
         }

        System.out.print(answer.toString());
    }

    private static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        StringTokenizer st = new StringTokenizer(br.readLine());

        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        for(int i=0; i<5; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<5; j++) {
                ruin[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        while(st.hasMoreTokens()) {
            wall.add(Integer.parseInt(st.nextToken()));
        }
    }

    private static int explore() {
        // 1. 유물 1차 획득이 가장 큰 최적의 포인트 찾기
        int piece = 0;
        int[] point = new int[3];

        for(int i=1; i<=3; i++) {
            for(int j=1; j<=3; j++) {
                int[][] board = new int[5][5];
                for(int k=0; k<5; k++) {
                    System.arraycopy(ruin[k], 0, board[k], 0, 5);
                }

                int cnt = 3;
                int angle = 0;
                while(cnt-- > 0) {
                    angle += 90;
                    rotate(board, i, j);
                    int tmp = getRelic(board);
                    if(tmp > piece) {
                        piece = tmp;
                        point = new int[]{i, j, angle};
                    } else if(tmp == piece) {
                        if(angle < point[2]) {
                            point = new int[]{i, j, angle};
                        } else if(angle == point[2]) {
                            if(j < point[1]) {
                                point = new int[]{i, j, angle};
                            } else if(j == point[1] && i < point[0]) {
                                point = new int[]{i, j, angle};
                            }
                        }
                    }
                }
            }
        }

        // 2. 유물 1차 획득 가능 한 유적 상태
        int rotcnt = point[2] / 90;
        while(rotcnt-- > 0){
            rotate(ruin, point[0], point[1]);
        }

        // 3. 벽면 숫자 채워 넣기 & 유물 연쇄 획득
        int addPiece = getRelic(ruin);
        while(addPiece > 0) {
            while(!pq.isEmpty()) {
                int[] tmpPoint = pq.poll();
                ruin[tmpPoint[0]][tmpPoint[1]] = wall.poll();
            }

            addPiece = getRelic(ruin);
            piece += addPiece;
        }

        return piece;
    }

    private static void rotate(int[][] board, int x, int y) {
        for(int i=0; i<3; i++) {
            int tmp = board[x-1][y+i-1];
            board[x-1][y+i-1] = board[x+1][y+i-1];
            board[x+1][y+i-1] = tmp;
        }

        for(int i=0; i<3; i++) {
            int c = 0;
            for(int j=i; j<3; j++) {
                int tmp = board[x+i-1][y+j-1];
                board[x+i-1][y+j-1] = board[x+i-1+c][y+j-1-c];
                board[x+i-1+c][y+j-1-c] = tmp;
                c++;
            }
        }
    }

    private static int getRelic(int[][] board) {
        int piece = 0;
        boolean[][] visited = new boolean[5][5];
        pq.clear();

        for(int i=0; i<5; i++) {
            for(int j=0; j<5; j++) {
                if(visited[i][j]) continue;
                int target = board[i][j];
                int cnt = 1;
                ArrayDeque<int[]> q = new ArrayDeque<>();
                ArrayDeque<int[]> tmp = new ArrayDeque<>();
                q.add(new int[] {i, j});
                tmp.add(new int[] {i, j});
                visited[i][j] = true;
                while(!q.isEmpty()) {
                    int[] point = q.poll();
                    for(int k=0; k<4; k++) {
                        int nx = point[0] + dx[k];
                        int ny = point[1] + dy[k];
                        if(!inRange(nx,ny) || board[nx][ny] != target || visited[nx][ny]) continue;
                        q.add(new int[] {nx, ny});
                        tmp.add(new int[]{nx, ny});
                        cnt++;
                        visited[nx][ny] = true;
                    }
                }
                if(cnt >= 3) {
                    piece += cnt;
                    while(!tmp.isEmpty()) pq.add(tmp.poll());
                }
            }
        }

        return piece;
    }

    private static boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x < 5 && y < 5;
    }
}