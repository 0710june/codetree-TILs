import java.util.*;
import java.io.*;

// 민트 초코 우유
public class Main {

    static int N, T;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static Student[][] students;
    static boolean[][] visited;
    static StringBuilder sb = new StringBuilder();
    static ArrayList<Student>[] leaders = new ArrayList[3];

    static class Student implements Comparable<Student>{
        int x, y;
        boolean mint, choco, milk;
        int faith, earnest, dir;
        boolean isAttacked;

        Student(int x, int y, boolean mint, boolean choco, boolean milk) {
            this.x = x;
            this.y = y;
            this.mint = mint;
            this.choco = choco;
            this.milk = milk;
        }

        public void setFaith(int faith) {
            this.faith = faith;
        }

        public boolean isEqualFood(Student student) {
            return this.mint == student.mint && this.choco == student.choco && this.milk == student.milk;
        }

        public int getFoodIdx() {
            if(mint && !choco && !milk) return 0;
            else if(!mint && choco && !milk) return 1;
            else if(!mint && !choco && milk) return 2;
            else if(!mint && choco && milk) return 3;
            else if(mint && !choco && milk) return 4;
            else if(mint && choco && !milk) return 5;
            else if(mint && choco && milk) return 6;
            else return 7;
        }

        public void setEarnest() {
            this.dir = faith % 4;
            this.earnest = faith - 1;
            this.faith = 1;
        }

        public void strongSpread(Student leader) {
            this.mint = leader.mint;
            this.choco = leader.choco;
            this.milk = leader.milk;
            this.isAttacked = true;
        }

        public void weakSpread(Student leader) {
            if(leader.mint) this.mint = true;
            if(leader.choco) this.choco = true;
            if(leader.milk) this.milk = true;
            this.isAttacked = true;
        }

        @Override
        public int compareTo(Student o) {
            if(this.faith != o.faith) return Integer.compare(o.faith, this.faith);
            if(this.x != o.x) return Integer.compare(this.x, o.x);
            return Integer.compare(this.y, o.y);
        }
    }

    public static void main(String[] args) throws Exception {

        // 0. 초기화
        init();

        for(int t=0; t<T; t++) {
            for(int i=0; i<3; i++) {
                leaders[i] = new ArrayList<>();
            }

            // 1. 대표자 선정
            morningAndLunch();

            // 2. 전파
            dinner();
        }

        System.out.print(sb.toString());
    }

    private static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());
        students = new Student[N][N];
        for(int i=0; i<N; i++) {
            String str = br.readLine();
            for(int j=0; j<N; j++) {
                char c = str.charAt(j);
                if(c == 'T') students[i][j] = new Student(i, j, true, false, false);
                else if(c == 'C') students[i][j] = new Student(i, j, false, true, false);
                else students[i][j] = new Student(i, j, false, false, true);
            }
        }

        for(int i=0; i<N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<N; j++) {
                int faith = Integer.parseInt(st.nextToken());
                students[i][j].setFaith(faith);
            }
        }
    }

    private static void morningAndLunch() {
        visited = new boolean[N][N];
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                if(visited[i][j]) continue;
                findLeader(i, j);
            }
        }
    }

    private static void findLeader(int x, int y) {
        ArrayList<Student> group = new ArrayList<>();
        group.add(students[x][y]);
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[] {x, y});
        visited[x][y] = true;

        while(!q.isEmpty()) {
            int[] cur = q.poll();

            for(int i=0; i<4; i++) {
                int nx = cur[0] + dx[i];
                int ny = cur[1] + dy[i];

                if(!inRange(nx, ny) || visited[nx][ny]) continue;
                if(!students[nx][ny].isEqualFood(students[x][y])) continue;
                group.add(students[nx][ny]);
                q.add(new int[] {nx, ny});
                visited[nx][ny] = true;
            }
        }

        Collections.sort(group);
        Student leader = group.get(0);
        leader.faith += group.size();

        leaders[leader.getFoodIdx() / 3].add(leader);
    }

    private static void dinner() {
        for(int i=0; i<3; i++) {
            Collections.sort(leaders[i]);
        }

        for(ArrayList<Student> group : leaders) {
            if(group.isEmpty()) continue;
            for(Student leader : group) {
                if(leader.isAttacked) continue;
                leader.setEarnest();
                spread(leader);
            }
        }

        int[] cnt = new int[7];
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                cnt[students[i][j].getFoodIdx()] += students[i][j].faith;
                if(students[i][j].isAttacked) students[i][j].isAttacked = false;
            }
        }

        for(int i=6; i>=0; i--) {
            sb.append(cnt[i]).append(" ");
        }
        sb.append("\n");
    }

    private static void spread(Student leader) {
        int x = leader.x;
        int y = leader.y;
        while(leader.earnest > 0) {
            x += dx[leader.dir];
            y += dy[leader.dir];

            if(!inRange(x, y)) {
                leader.earnest = 0;
                break;
            }
            if(leader.isEqualFood(students[x][y])) continue;

            // 강한 전파
            if(leader.earnest > students[x][y].faith) {
                students[x][y].faith += 1;
                leader.earnest -= students[x][y].faith;
                students[x][y].strongSpread(leader);
            }
            // 약한 전파
            else{
                students[x][y].faith += leader.earnest;
                leader.earnest = 0;
                students[x][y].weakSpread(leader);
            }

            if(leader.earnest == 0) break;
        }
    }

    private static boolean inRange(int x, int y) {
        return x >= 0 && y >=0 && x < N && y < N;
    }
}
