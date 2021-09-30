import java.util.*;

public class BFS {
	public static boolean[] visited = new boolean[9];
	public static ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
	
	public static void bfs(int start) {
		Queue<Integer> queue = new LinkedList<>();
		queue.offer(start);
		visited[start] = true; // ���� node�� �湮ó��
		
		while(!queue.isEmpty()) { // Queue�� �� ������ �ݺ�
			int x = queue.poll(); // Queue���� �ϳ��� ���Ҹ� �̾� return
			System.out.print(x + " ");
			
			// �ش� ���ҿ� �����, ���� �湮���� ���� ���ҵ��� queue�� �ֱ�
			for(int link : graph.get(x)) {
				if(!visited[link]) {
					queue.offer(link);
					visited[link] = true;
				}
			}
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        // �׷��� �ʱ�ȭ
        for (int i = 0; i < 9; i++) {
            graph.add(new ArrayList<Integer>());
        }
        
        nodeSet();
        bfs(1);
	}
	
	public static void nodeSet(){
		  // ��� 1�� ����� ��� ���� ���� 
        graph.get(1).add(2);
        graph.get(1).add(3);
        graph.get(1).add(8);
        
        // ��� 2�� ����� ��� ���� ���� 
        graph.get(2).add(1);
        graph.get(2).add(7);
        
        // ��� 3�� ����� ��� ���� ���� 
        graph.get(3).add(1);
        graph.get(3).add(4);
        graph.get(3).add(5);
        
        // ��� 4�� ����� ��� ���� ���� 
        graph.get(4).add(3);
        graph.get(4).add(5);
        
        // ��� 5�� ����� ��� ���� ���� 
        graph.get(5).add(3);
        graph.get(5).add(4);
        
        // ��� 6�� ����� ��� ���� ���� 
        graph.get(6).add(7);
        
        // ��� 7�� ����� ��� ���� ���� 
        graph.get(7).add(2);
        graph.get(7).add(6);
        graph.get(7).add(8);
        
        // ��� 8�� ����� ��� ���� ���� 
        graph.get(8).add(1);
        graph.get(8).add(7);

	}

}
