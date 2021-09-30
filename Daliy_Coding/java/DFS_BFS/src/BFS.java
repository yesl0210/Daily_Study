import java.util.*;

public class BFS {
	public static boolean[] visited = new boolean[9];
	public static ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
	
	public static void bfs(int start) {
		Queue<Integer> queue = new LinkedList<>();
		queue.offer(start);
		visited[start] = true; // 현재 node를 방문처리
		
		while(!queue.isEmpty()) { // Queue가 빌 때까지 반복
			int x = queue.poll(); // Queue에서 하나의 원소를 뽑아 return
			System.out.print(x + " ");
			
			// 해당 원소와 연결된, 아직 방문하지 않은 원소들을 queue에 넣기
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
        // 그래프 초기화
        for (int i = 0; i < 9; i++) {
            graph.add(new ArrayList<Integer>());
        }
        
        nodeSet();
        bfs(1);
	}
	
	public static void nodeSet(){
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
