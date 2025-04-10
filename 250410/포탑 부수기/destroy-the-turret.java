import java.util.*;
import java.io.*;

// 포탑 부수기
public class Main {

    static int N, M, K;
    static int[][] map;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static int[] bx = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
    static int[] by = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
    static ArrayList<Tower> towers = new ArrayList<>();
    static HashMap<Integer, Tower> towermap = new HashMap<>();
    static Tower attacker, target;
    static ArrayList<int[]> attackPoint;

    static class Tower implements Comparable<Tower> {
        int x, y, damage, turn, sum;
        boolean isDestroied;

        Tower(int x, int y, int damage) {
            this.x = x;
            this.y = y;
            this.damage = damage;
            this.sum = x + y;
        }

        @Override
        public int compareTo(Tower o) {
            if(this.damage != o.damage) return this.damage - o.damage;
            if(this.turn != o.turn) return (this.turn - o.turn) * -1;
            if(this.sum != o.sum) return (this.sum - o.sum) * -1;
            if(this.y != o.y) return (this.y - o.y) * -1;
            return 0;
        }

        public void damaged() {
            if(x == target.x && y == target.y) damage -= attacker.damage;
            else damage -= (attacker.damage / 2);

            if(damage <= 0) {
                damage = 0;
                isDestroied = true;
            }

            map[x][y] = damage;
        }

        public void selected(int turn) {
            this.turn = turn;
            damage += N + M;
            map[x][y] = damage;
        }

        public void repaired() {
            damage += 1;
            map[x][y] = damage;
        }
    }

    static class Point {
        int x, y, d;
        ArrayList<int[]> path;

        Point(int x, int y, int d, ArrayList<int[]> path) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.path = path;
        }
    }

    public static void main(String[] args) throws Exception {

        init();

        for(int k=1; k<=K; k++) {
            // 1. 공격자 선정
            select(k);
            if(attacker == target) break;

            // 2. 공격자 공격
            attackTarget();

            // 3. 포탑 정비
            repairTower();
        }

        // 가장 강한 포탑의 공격력 출력
        printDamage();
    }

    private static void printDamage() {
        int answer = 0;
        for(Tower tower : towers) {
            if(tower.isDestroied || tower.damage < answer) continue;
            answer = tower.damage;
        }
        System.out.print(answer);
    }

    private static void repairTower() {
        attackPoint.add(new int[] {attacker.x, attacker.y});
        HashSet<Integer> keySet = new HashSet<>(towermap.keySet());
        for(int[] point : attackPoint) {
            int key = point[0] * M + point[1];
            keySet.remove(key);
        }

        for(int key : keySet) {
            if(towermap.get(key).isDestroied) continue;
            towermap.get(key).repaired();
        }
    }

    private static void attackTarget() {
        attackPoint = new ArrayList<>();
        if(!canLaser()) canBomb();
        for(int[] path : attackPoint) {
            towermap.get(path[0] * M + path[1]).damaged();
        }
    }

    private static void canBomb() {
        int x = target.x;
        int y = target.y;

        for(int i=0; i<9; i++) {
            int nx = (x + bx[i] + N) % N;
            int ny = (y + by[i] + M) % M;

            if(map[nx][ny] == 0) continue;
            if(attacker.x == nx && attacker.y == ny) continue;
            attackPoint.add(new int[] {nx, ny});
        }
    }

    private static boolean canLaser() {
        boolean[][] visited = new boolean[N][M];
        PriorityQueue<Point> q = new PriorityQueue<>(
                (p1, p2) -> p1.d - p2.d
        );
        q.add(new Point(attacker.x, attacker.y, 0, new ArrayList<>()));
        visited[attacker.x][attacker.y] = true;

        while(!q.isEmpty()) {
            Point cur = q.poll();

            if(cur.x == target.x && cur.y == target.y) {
                attackPoint = cur.path;
                return true;
            }

            for(int i=0; i<4; i++) {
                int nx = (cur.x + dx[i] + N) % N;
                int ny = (cur.y + dy[i] + M) % M;

                if(map[nx][ny] == 0 || visited[nx][ny]) continue;
                ArrayList<int[]> npath = new ArrayList<>(cur.path);
                npath.add(new int[] {nx, ny});
                q.add(new Point(nx, ny, i, npath));
                visited[nx][ny] = true;
            }
        }

        return false;
    }

    private static void select(int turn) {
        Collections.sort(towers);
        int idx = 0;
        while(idx < towers.size() && towers.get(idx).isDestroied) {
            idx++;
        }
        attacker = towers.get(idx);
        attacker.selected(turn);

        idx = towers.size()-1;
        while(idx >= 0 && towers.get(idx).isDestroied) {
            idx--;
        }
        target = towers.get(idx);
    }

    private static void init() throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][M];
        for(int i=0; i<N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if(map[i][j] > 0) {
                    Tower tower = new Tower(i, j, map[i][j]);
                    towers.add(tower);
                    towermap.put(i*M+j, tower);
                }
            }
        }
    }
}
