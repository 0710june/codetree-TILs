import java.io.*;
import java.util.*;

public class Main {
    static int N, T;
    static Member[][] memberMap;
    static boolean[][] isGrouped;
    static boolean[][] isDamaged;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer stk;
    static int[] dx = {0, 0, -1, 1};
    static int[] dy = {-1, 1, 0, 0};

    // 민트 초코 우유
    public static void main(String[] args) throws IOException {
        init();
        for (int i = 0; i < T; i++) {
            morning();
            List<Group> groups = lunch();
            evening(groups);
            resetDay();
            afterEvening();
        }
    }

    private static void afterEvening() {
        int[] result = new int[8];

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                int type = findWho(memberMap[i][j]);
                result[type] += memberMap[i][j].value;
            }
        }

        for (int i = 1; i <= 7; i++) {
            System.out.print(result[i] + " ");
        }
        System.out.println();
    }

    private static void resetDay() {
        isDamaged = new boolean[N + 1][N + 1];
        isGrouped = new boolean[N + 1][N + 1];
    }

    private static void init() throws IOException {
        stk = new StringTokenizer(br.readLine());
        N = Integer.parseInt(stk.nextToken());
        T = Integer.parseInt(stk.nextToken());
        isGrouped = new boolean[N + 1][N + 1];
        isDamaged = new boolean[N + 1][N + 1];
        memberMap = new Member[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            String line = br.readLine();
            for (int j = 1; j <= N; j++) {
                memberMap[i][j] = new Member(i, j);
                memberMap[i][j].set.add(line.charAt(j - 1));
            }
        }

        for (int i = 1; i <= N; i++) {
            stk = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                memberMap[i][j].value = Integer.parseInt(stk.nextToken());
            }

        }
    }

    private static void morning() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                memberMap[i][j].value++;
            }
        }
    }

    private static List<Group> lunch() {
        List<Group> groups = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (!isGrouped[i][j]) {
                    groups.add(findGroup(i, j));
                }
            }
        }
        for (Group group : groups) {
            int type = findWho(group.getLeader());
            Member leader = group.getLeader();
            group.setLeader(leader);
            group.setTypeCode(type);
            group.setTypeCodeSize(leader.set.size());
            for (Member member : group.members) {
                if (leader.equals(member))
                    continue;
                member.value--;
                leader.value++;
            }
        }
        return groups;
    }

    private static Group findGroup(int startY, int startX) {
        Group curGroup = new Group();
        Member startMember = memberMap[startY][startX];
        curGroup.members.add(startMember);
        isGrouped[startY][startX] = true;
        Deque<Member> deque = new ArrayDeque<>();
        deque.add(startMember);

        while (!deque.isEmpty()) {
            Member curMember = deque.poll();
            for (int i = 0; i < 4; i++) {
                int nxtY = curMember.y + dy[i];
                int nxtX = curMember.x + dx[i];

                if (1 > nxtY || N < nxtY || 1 > nxtX || N < nxtX)
                    continue;

                if (isGrouped[nxtY][nxtX])
                    continue;

                if (!checkSameSet(curMember.y, curMember.x, nxtY, nxtX))
                    continue;

                Member newMember = memberMap[nxtY][nxtX];
                isGrouped[nxtY][nxtX] = true;
                curGroup.members.add(newMember);
                deque.add(newMember);
            }
        }
        return curGroup;
    }

    private static int findWho(Member member) {

        // 민트초코우유 민트초코 민트우유 초코우유 우유 초코 민트
        // TCM        TC      TM     CM     M    C   T
        //  1         2        3     4     5     6    7
        if (member.set.size() == 3) {
            //민트초코우유
            return 1;
        } else if (member.set.size() == 2) {
            if (member.set.contains('T') && member.set.contains('C')) {
                return 2;
            }
            if (member.set.contains('M') && member.set.contains('T')) {
                return 3;
            }
            if (member.set.contains('C') && member.set.contains('M')) {
                return 4;
            }
        } else {
            if (member.set.contains('M')) {
                return 5;
            }
            if (member.set.contains('C')) {
                return 6;
            }
            if (member.set.contains('T')) {
                return 7;
            }
        }
        return -1;
    }

    private static boolean checkSameSet(int y, int x, int nxtY, int nxtX) {
        if (memberMap[y][x].set.size() != memberMap[nxtY][nxtX].set.size())
            return false;
        return memberMap[y][x].set.containsAll(memberMap[nxtY][nxtX].set);
    }

    private static void evening(List<Group> groups) {
        groups.sort((a, b) -> {
            if (a.typeCodeSize != b.typeCodeSize)
                return Integer.compare(a.typeCodeSize, b.typeCodeSize);

            return a.compareTo(b);
        });

        // 전파
        for (Group group : groups) {
            // Member leader = group.getLeader(); 여기가 문제 -> 이유 : 전파 과정 중에 value가 바뀌어서 이 시점에서 원래 리더보다 더 큰 학생이 생길 수 있음.
            Member leader = group.leader;
            if (isDamaged[leader.y][leader.x])
                continue;

            int power = leader.getPower();
            int dir = (power + 1) % 4;

            int nxtY = leader.y;
            int nxtX = leader.x;
            int curType = findWho(leader);
            do {
                nxtY += dy[dir];
                nxtX += dx[dir];

                if (1 > nxtY || N < nxtY || 1 > nxtX || N < nxtX) {
                    break;
                }

                Member target = memberMap[nxtY][nxtX];
                int nxtType = findWho(target);

                if (curType == nxtType)
                    continue;

                if (power > target.value) {
                    target.value++;
                    power -= target.value;
                    sameSet(leader, target);
                    isDamaged[target.y][target.x] = true;
                } else {
                    addSet(leader, target);
                    target.value += power;
                    isDamaged[target.y][target.x] = true;
                    power = 0;
                }
                if (power == 0)
                    break;

            } while (true);
        }
    }

    private static void addSet(Member leader, Member target) {
        target.set.addAll(leader.set);
    }

    private static void sameSet(Member leader, Member target) {
        target.set.clear();
        target.set.addAll(leader.set);
    }

    static class Group implements Comparable<Group> {
        List<Member> members = new ArrayList<>();
        int typeCode; // 조합 코드: 1~7
        int typeCodeSize;
        Member leader;

        public void setLeader(Member member) {
            this.leader = member;
        }

        public void setTypeCodeSize(int typeCodeSize) {
            this.typeCodeSize = typeCodeSize;
        }

        public void add(Member m) {
            members.add(m);
        }

        public void setTypeCode(int typeCode) {
            this.typeCode = typeCode;
        }

        public Member getLeader() {
            return Collections.min(members);
        }

        @Override
        public int compareTo(Group o) {
            return this.getLeader().compareTo(o.getLeader());
        }
    }

    static class Member implements Comparable<Member> {
        int x, y, value;
        Set<Character> set = new HashSet<>();

        public Member(int y, int x) {
            this.y = y;
            this.x = x;
        }

        public int getPower() {
            int power = this.value - 1;
            this.value = 1;
            return power;
        }

        @Override
        public int compareTo(Member o) {
            if (this.value != o.value) return Integer.compare(o.value, this.value);
            if (this.y != o.y) return Integer.compare(this.y, o.y);
            return Integer.compare(this.x, o.x);
        }
    }

}