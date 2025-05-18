import java.util.*;
import java.io.*;

// 싸움땅
public class Main {

    static int n, m, k;
    static int[][] map;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static ArrayList<Player> players = new ArrayList<>();
    static HashMap<Integer, PriorityQueue<Integer>> gunsInMap = new HashMap<>();
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws Exception {

        init();

        for(int i=0; i<k; i++) {
            for(Player p : players) {
                move(p);
            }
        }

        System.out.print(printPoint());
    }

    private static void init() throws Exception {
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        map = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= n; j++) {
                int gun = Integer.parseInt(st.nextToken());
                gunsInMap.put((i - 1) * n + j, new PriorityQueue<>((o1, o2) -> Integer.compare(o2, o1)));
                if (gun == 0) continue;
                gunsInMap.get((i - 1) * n + j).add(gun);
            }
        }

        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            players.add(new Player(i, x, y, d, s));
            map[x][y] = i;
        }
    }

    private static void move(Player p) {
        map[p.x][p.y] = 0;
        int nx = p.x + dx[p.d];
        int ny = p.y + dy[p.d];
        if(!inRange(nx, ny)) {
            p.d = (p.d + 2) % 4;
            nx = p.x + dx[p.d];
            ny = p.y + dy[p.d];
        }
        p.x = nx;
        p.y = ny;
        // 플레이어가 없다면 총 획득
        if(map[nx][ny] == 0) {
            map[p.x][p.y] = p.idx;
            PriorityQueue<Integer> guns = gunsInMap.get(getKey(nx, ny));
            if(guns.isEmpty()) return;
            // 총이 없다면 가장 강한 총 획득
            if(p.g == 0) {
                p.g = guns.poll();
            }
            // 총이 있다면 총 교체
            else {
                // 현재 총이 가장 강하면 종료
                if(p.g > guns.peek()) return;
                else {
                    guns.add(p.g);
                    p.g = guns.poll();
                }
            }
        }
        // 플레이어가 있다면 싸움
        else {
            fight(p, players.get(map[nx][ny] - 1));
        }
    }

    private static void fight(Player p1, Player p2) {
        int p1Power = p1.s + p1.g;
        int p2Power = p2.s + p2.g;

        Player winner;
        Player loser;

        if(p1Power == p2Power) {
            if(p1.s > p2.s) {
                winner = p1;
                loser = p2;
            }else{
                winner = p2;
                loser = p1;
            }
        }else{
            if(p1Power > p2Power) {
                winner = p1;
                loser = p2;
                winner.p += p1Power - p2Power;
            }else {
                winner = p2;
                loser = p1;
                winner.p += p2Power - p1Power;
            }
        }

        moveLoser(loser);

        map[winner.x][winner.y] = winner.idx;
        PriorityQueue<Integer> guns = gunsInMap.get(getKey(winner.x, winner.y));
        if(guns.isEmpty()) return;
        if(winner.g == 0) {
            winner.g = guns.poll();
        }else{
            guns.add(winner.g);
            winner.g = guns.poll();
        }
    }

    private static void moveLoser(Player loser) {
        // 총 내려놓기
        PriorityQueue<Integer> guns;
        if(loser.g != 0) {
            guns = gunsInMap.get(getKey(loser.x, loser.y));
            guns.add(loser.g);
            loser.g = 0;
        }

        // 이동
        map[loser.x][loser.y] = 0;
        int nx = loser.x + dx[loser.d];
        int ny = loser.y + dy[loser.d];
        while(!inRange(nx, ny) || map[nx][ny] != 0) {
            loser.d = (loser.d + 1) % 4;
            nx = loser.x + dx[loser.d];
            ny = loser.y + dy[loser.d];
        }

        // 움직이고 가장 강한 총 얻기
        loser.x = nx;
        loser.y = ny;
        map[loser.x][loser.y] = loser.idx;
        guns = gunsInMap.get(getKey(loser.x, loser.y));
        if(guns.isEmpty()) return;
        loser.g = guns.poll();
    }

    private static String printPoint() {
        StringBuilder sb = new StringBuilder();

        for(Player p : players) {
            sb.append(p.p).append(" ");
        }

        return sb.toString();
    }

    private static boolean inRange(int x, int y) {
        return x >= 1 && x <= n && y >= 1 && y <= n;
    }

    private static int getKey(int x, int y) {
        return (x - 1) * n + y;
    }

    static class Player {
        int idx, x, y, d, s, g, p;

        Player(int idx, int x, int y, int d, int s) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.d = d;
            this.s = s;
        }
    }
}
