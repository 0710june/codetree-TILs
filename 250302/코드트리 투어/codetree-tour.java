
import java.util.*;
import java.io.*;

public class Main {

    static int n, m;
    static ArrayList<int[]>[] graph;
    static int[] dir;
    static final int INF = Integer.MAX_VALUE;
    static PriorityQueue<Product> recomm = new PriorityQueue<>();
    static HashMap<Integer, Product> products = new HashMap<>();
    static StringBuilder sb = new StringBuilder();

    static class Product implements Comparable<Product> {
        int id;
        int revenue;
        int dest;
        int cost;
        int margin;
        boolean isDeleted;

        Product(int id, int revenue, int dest, int cost) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.cost = cost;
            if(cost == INF) this.margin = INF * -1;
            else this.margin = this.revenue - this.cost;
        }

        public void changeCost(int cost) {
            this.cost = cost;
            if(cost == INF) this.margin = INF * -1;
            else this.margin = this.revenue - this.cost;
        }

        public void deleteProduct() {
            isDeleted = true;
        }

        @Override
        public int compareTo(Product o) {
            if(this.margin == o.margin) {
                return Integer.compare(this.id, o.id);
            } else {
                return Integer.compare(o.margin, this.margin);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int Q = Integer.parseInt(br.readLine());

        for (int t = 0; t < Q; t++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int q = Integer.parseInt(st.nextToken());
            switch (q) {
                case 100:
                    init(st);
                    dijkstra(0);
                    break;
                case 200:
                    addProduct(st);
                    break;
                case 300:
                    deleteProduct(st);
                    break;
                case 400:
                    recommProduct();
                    break;
                case 500:
                    changeStart(st);
                    break;
            }
        }

        System.out.print(sb.toString());
    }

    public static void init(StringTokenizer st) {
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        dir = new int[n];
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int i = 0; i < m; i++) {
            int v = Integer.parseInt(st.nextToken());
            int u = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            graph[v].add(new int[]{u, w});
            graph[u].add(new int[]{v, w});
        }
    }

    public static void dijkstra(int start) {
        Arrays.fill(dir, INF);
        dir[start] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (o1,o2) -> o1[1] - o2[1]
        );
        pq.add(new int[] {start, 0});

        while(!pq.isEmpty()) {
            int[] cur = pq.poll();

            if(cur[1] > dir[cur[0]]) continue;

            for(int[] i : graph[cur[0]]) {
                if(dir[i[0]] < cur[1] + i[1]) continue;
                pq.add(new int[] {i[0], cur[1]+i[1]});
                dir[i[0]] = cur[1] + i[1];
            }
        }
    }

    public static void addProduct(StringTokenizer st) {
        int id = Integer.parseInt(st.nextToken());
        int revenue = Integer.parseInt(st.nextToken());
        int dest = Integer.parseInt(st.nextToken());
        Product p = new Product(id, revenue, dest, dir[dest]);

        products.put(id, p);
        recomm.add(p);
    }

    public static void deleteProduct(StringTokenizer st) {
        int id = Integer.parseInt(st.nextToken());
        if(!products.containsKey(id)) return;
        products.get(id).deleteProduct();
    }

    public static void recommProduct() {
        while(!recomm.isEmpty()) {
            Product p = recomm.peek();
            if(p.isDeleted) {
                recomm.poll();
                products.remove(p.id);
                continue;
            }
            if(p.margin < 0) break;
            recomm.poll();
            sb.append(p.id).append("\n");
            products.remove(p.id);
            return;
        }
        sb.append(-1).append("\n");
    }

    public static void changeStart(StringTokenizer st) {
        recomm.clear();
        int start = Integer.parseInt(st.nextToken());
        dijkstra(start);
        for(int id : products.keySet()) {
            Product p = products.get(id);
            if(p.isDeleted) continue;
            p.changeCost(dir[p.dest]);
            recomm.add(p);
        }
    }
}
