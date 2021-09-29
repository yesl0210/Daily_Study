import java.util.*;

public class Linked_ex {
	
	// 2차원 리스트를 이용해 인접 행렬 표현
	public static int[][] graph1 = {
			{0,7,5,8},
			{7,0,100,2},
			{5,100,0,3}
	};
	
	// 행이 3개인 인접 리스트 표현
	public static ArrayList<ArrayList<Node>> graph2 = new ArrayList<ArrayList<Node>>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// 인접 행렬 그래프 출력 - graph 1
		System.out.println("* graph1 *");
		for (int i=0;i<graph1.length;i++) {
			for(int j=0;j<graph1[i].length;j++) {
				System.out.print(graph1[i][j]+" ");
			}
			System.out.println();
		}
		
		// 인접 리스트 예제 - graph2
		System.out.println("\n* graph2 *");
		// 그래프 초기화
		for (int i=0 ; i<3;i++) { // 행 3개 추가해주기
			graph2.add(new ArrayList<Node>());
		}
		System.out.println("graph2 : "+ graph2);
		
		// node 0에 연결된 node 정보 저장 (노드 index, 거리)
		graph2.get(0).add(new Node(1,7));
		graph2.get(0).add(new Node(2,5));
		
		// node 1에 연결된 node 정보 저장 (노드 index, 거리)
		graph2.get(1).add(new Node(0,7));
		graph2.get(1).add(new Node(1,5));
		graph2.get(1).add(new Node(2,4));
		
		// node 2에 연결된 node 정보 저장 (노드 index, 거리)
		graph2.get(2).add(new Node(0,5));
		
		for (int i=0;i<graph2.size();i++) {
			for (int j=0; j<graph2.get(i).size();j++) {
				graph2.get(i).get(j).show();
			}
			System.out.println();
		}
	}
}

//인접 리스트
class Node {
	private int index;
	private int distance;
	
	public Node(int index, int distance) {
		this.index = index;
		this.distance = distance;
	}
	
	public void show() {
		System.out.print("(" + this.index + "," + this.distance + ") ");
	}
}


