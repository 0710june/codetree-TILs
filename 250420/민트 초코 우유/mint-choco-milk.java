import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static int N,T;
    static Member[][] memberMap;
    static boolean[][] isGrouped;
    static boolean[][] isDamaged;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer stk;
    static int [] dx = {0,0,-1,1};
    static int [] dy = {-1,1,0,0};

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
        int [] result = new int[8];

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                int type = findWho(memberMap[i][j]);
                result[type] += memberMap[i][j].value;
            }
        }

        for (int i = 1; i <= 7 ; i++) {
            System.out.print(result[i]+" ");
        }
        System.out.println();
    }

    private static void resetDay() {
        isDamaged = new boolean[N+1][N+1];
        isGrouped = new boolean[N+1][N+1];
    }

    private static void init() throws IOException {
        stk = new StringTokenizer(br.readLine());
        N = Integer.parseInt(stk.nextToken());
        T = Integer.parseInt(stk.nextToken());
        isGrouped = new boolean[N+1][N+1];
        isDamaged = new boolean[N+1][N+1];
        memberMap = new Member[N+1][N+1];
        for (int i = 1; i <= N; i++) {
            String line = br.readLine();
            for (int j = 1; j <= N; j++) {
                memberMap[i][j] = new Member(i, j);
                memberMap[i][j].set.add(line.charAt(j-1));
            }
        }

        for (int i = 1; i <= N; i++) {
            stk = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                memberMap[i][j].value = Integer.parseInt(stk.nextToken());
            }

        }
    }

    private static void morning(){
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                memberMap[i][j].value++;
            }
        }
    }

    private static List<Group> lunch(){
        List<Group> groups = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if(!isGrouped[i][j]){
                    groups.add(findGroup(i,j));
                }
            }
        }
        for (Group group : groups) {
            int type = findWho(group.getLeader());
            Member leader = group.getLeader();
            group.setTypeCode(type);
            group.setTypeCodeSize(memberMap[leader.y][leader.x].set.size());
            for(Member member : group.members){
                if(leader.equals(member))
                    continue;
                memberMap[member.y][member.x].value--;
                memberMap[leader.y][leader.x].value++;
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

        while(!deque.isEmpty()){
            Member curMember = deque.poll();
            for (int i = 0; i < 4; i++) {
                int nxtY = curMember.y + dy[i];
                int nxtX = curMember.x + dx[i];

                if(1 > nxtY || N < nxtY || 1 > nxtX || N < nxtX)
                    continue;

                if(isGrouped[nxtY][nxtX])
                    continue;

                if(!checkSameSet(curMember.y,curMember.x,nxtY,nxtX))
                    continue;

                Member newMember = memberMap[nxtY][nxtX];
                isGrouped[nxtY][nxtX] = true;
                curGroup.members.add(newMember);
                deque.add(newMember);
            }
        }
        return curGroup;
    }
    private static int findWho(Member member){

        // 민트초코우유 민트초코 민트우유 초코우유 우유 초코 민트
        // TCM        TC      TM     CM     M    C   T
        //  1         2        3     4     5     6    7
        if(memberMap[member.y][member.x].set.size() == 3){
            //민트초코우유
            return 1;
        }
        else{
            if(memberMap[member.y][member.x].set.size() == 2){
                if(memberMap[member.y][member.x].set.contains('T') && memberMap[member.y][member.x].set.contains('C')){
                    return 2;
                }
                if(memberMap[member.y][member.x].set.contains('M') && memberMap[member.y][member.x].set.contains('T')){
                    return 3;
                }
                if(memberMap[member.y][member.x].set.contains('C') && memberMap[member.y][member.x].set.contains('M')){
                    return 4;
                }
            }else{
                if(memberMap[member.y][member.x].set.contains('M')){
                    return 5;
                }
                if(memberMap[member.y][member.x].set.contains('C')){
                    return 6;
                }
                if(memberMap[member.y][member.x].set.contains('T')){
                    return 7;
                }
            }
        }
        return -1;
    }

    private static boolean checkSameSet(int y, int x, int nxtY, int nxtX) {
        if(memberMap[y][x].set.size() != memberMap[nxtY][nxtX].set.size())
            return false;
        return memberMap[y][x].set.containsAll(memberMap[nxtY][nxtX].set) && memberMap[nxtY][nxtX].set.containsAll(memberMap[y][x].set);
    }

    private static void evening(List<Group> groups){
        groups.sort((a, b) -> {

            if (a.typeCodeSize != b.typeCodeSize)
                return Integer.compare(a.typeCodeSize, b.typeCodeSize);

            int aVal = memberMap[a.getLeader().y][a.getLeader().x].value;
            int bVal = memberMap[b.getLeader().y][b.getLeader().x].value;
            if (aVal != bVal)
                return Integer.compare(bVal, aVal);

            if (a.getLeader().y != b.getLeader().y)
                return Integer.compare(a.getLeader().y, b.getLeader().y);

            return Integer.compare(a.getLeader().x, b.getLeader().x);
        });

        // 전파
        for (Group group : groups) {
            Member leader = group.getLeader();
            if(isDamaged[leader.y][leader.x])
                continue;
            int power = leader.getPower();
            int dir = (power+1) % 4;

            int nxtY = leader.y;
            int nxtX = leader.x;
            int curType = findWho(leader);
            do {
                nxtY += dy[dir];
                nxtX += dx[dir];

                if (1 > nxtY || N < nxtY || 1 > nxtX || N < nxtX)
                    break;

                Member target = memberMap[nxtY][nxtX];
                int nxtType = findWho(target);

                if(curType == nxtType)
                    continue;

                if(power > memberMap[target.y][target.x].value){
                    power -= (memberMap[target.y][target.x].value + 1);
                    sameSet(leader,target);
                    memberMap[target.y][target.x].value++;
                    isDamaged[target.y][target.x] = true;
                }else{
                    addSet(leader,target);
                    memberMap[target.y][target.x].value += power;
                    isDamaged[target.y][target.x] = true;
                    power = 0;
                }
                if(power == 0)
                    break;

            }while (true);

        }

    }

    private static void addSet(Member leader, Member target) {
        memberMap[target.y][target.x].set.addAll(memberMap[leader.y][leader.x].set);
    }

    private static void sameSet(Member leader, Member target) {
        memberMap[target.y][target.x].set.clear();
        memberMap[target.y][target.x].set.addAll(memberMap[leader.y][leader.x].set);

    }

    static class Group implements Comparable<Group> {
        List<Member> members = new ArrayList<>();
        int typeCode; // 조합 코드: 1~7
        int typeCodeSize;
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
            return Collections.min(members, Comparator
                    .comparingInt((Member m) -> -m.value)                // 신앙심 높은 순
                    .thenComparingInt(m -> m.y)                          // 행 번호 작은 순
                    .thenComparingInt(m -> m.x)                          // 열 번호 작은 순
            );
        }

        public int getHoly(){
            int result = 0;
            for(Member member : members){
                result += memberMap[member.y][member.x].value;
            }
            return result;
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
        public int getPower(){
            int power = memberMap[y][x].value - 1;
            memberMap[y][x].value = 1;
            return power;
        }
        @Override
        public int compareTo(Member o) {
            int thisValue = memberMap[this.y][this.x].value;
            int otherValue = memberMap[o.y][o.x].value;

            if (thisValue != otherValue) return Integer.compare(otherValue,thisValue);
            if (this.y != o.y) return Integer.compare(this.y, o.y);
            return Integer.compare(this.x, o.x);
        }
    }

}