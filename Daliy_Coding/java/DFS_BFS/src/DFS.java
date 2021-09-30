import java.util.*;

public class DFS {
	// O(N = �������� ����)
	
	public static boolean[] visited = new boolean[9];
	public static ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();

	public static void dfs(int start) {
		visited[start] = true; // ���� node �湮 ó��
		System.out.print(start + " ");
		// ���� node�� ����� �ٸ� ��带 ��������� �湮
		for(int link : graph.get(start)) {
			// stack(graph)�� �ֻ�� node�� �湮���� ���� node �� �ִٸ�
			// �ش� node�� stack�� �ְ�(= ����Լ� ȣ��) �湮 ó��
			if(!visited[link]) dfs(link);
		}
		// �湮���� ���� ���� ��尡 ������ -> �Լ� ���� = ���ÿ��� �ֻ�� ��带 ������.
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// �׷��� �ʱ�ȭ
		int size = 9;
		for (int i=0;i<size;i++) {
			graph.add(new ArrayList<Integer>());
		}

		NodeSet();
		dfs(1);
	}
	
	public static void NodeSet() {
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
