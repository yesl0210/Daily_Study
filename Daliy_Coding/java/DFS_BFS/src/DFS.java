import java.util.*;

public class DFS {
	// O(N = 데이터의 개수)
	
	public static boolean[] visited = new boolean[9];
	public static ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();

	public static void dfs(int start) {
		visited[start] = true; // 현재 node 방문 처리
		System.out.print(start + " ");
		// 현재 node와 연결된 다른 노드를 재귀적으로 방문
		for(int link : graph.get(start)) {
			// stack(graph)의 최상단 node에 방문하지 않은 node 가 있다면
			// 해당 node를 stack에 넣고(= 재귀함수 호출) 방문 처리
			if(!visited[link]) dfs(link);
		}
		// 방문하지 않은 인접 노드가 없으면 -> 함수 종료 = 스택에서 최상단 노드를 꺼낸다.
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 그래프 초기화
		int size = 9;
		for (int i=0;i<size;i++) {
			graph.add(new ArrayList<Integer>());
		}

		NodeSet();
		dfs(1);
	}
	
	public static void NodeSet() {
        // 노드 1에 연결된 노드 정보 저장 
        graph.get(1).add(2);
        graph.get(1).add(3);
        graph.get(1).add(8);
        
        // 노드 2에 연결된 노드 정보 저장 
        graph.get(2).add(1);
        graph.get(2).add(7);
        
        // 노드 3에 연결된 노드 정보 저장 
        graph.get(3).add(1);
        graph.get(3).add(4);
        graph.get(3).add(5);
        
        // 노드 4에 연결된 노드 정보 저장 
        graph.get(4).add(3);
        graph.get(4).add(5);
        
        // 노드 5에 연결된 노드 정보 저장 
        graph.get(5).add(3);
        graph.get(5).add(4);
        
        // 노드 6에 연결된 노드 정보 저장 
        graph.get(6).add(7);
        
        // 노드 7에 연결된 노드 정보 저장 
        graph.get(7).add(2);
        graph.get(7).add(6);
        graph.get(7).add(8);
        
        // 노드 8에 연결된 노드 정보 저장 
        graph.get(8).add(1);
        graph.get(8).add(7);		
	}

}
