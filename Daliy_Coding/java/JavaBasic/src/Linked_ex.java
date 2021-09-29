import java.util.*;

public class Linked_ex {
	
	// 2���� ����Ʈ�� �̿��� ���� ��� ǥ��
	public static int[][] graph1 = {
			{0,7,5,8},
			{7,0,100,2},
			{5,100,0,3}
	};
	
	// ���� 3���� ���� ����Ʈ ǥ��
	public static ArrayList<ArrayList<Node>> graph2 = new ArrayList<ArrayList<Node>>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// ���� ��� �׷��� ��� - graph 1
		System.out.println("* graph1 *");
		for (int i=0;i<graph1.length;i++) {
			for(int j=0;j<graph1[i].length;j++) {
				System.out.print(graph1[i][j]+" ");
			}
			System.out.println();
		}
		
		// ���� ����Ʈ ���� - graph2
		System.out.println("\n* graph2 *");
		// �׷��� �ʱ�ȭ
		for (int i=0 ; i<3;i++) { // �� 3�� �߰����ֱ�
			graph2.add(new ArrayList<Node>());
		}
		System.out.println("graph2 : "+ graph2);
		
		// node 0�� ����� node ���� ���� (��� index, �Ÿ�)
		graph2.get(0).add(new Node(1,7));
		graph2.get(0).add(new Node(2,5));
		
		// node 1�� ����� node ���� ���� (��� index, �Ÿ�)
		graph2.get(1).add(new Node(0,7));
		graph2.get(1).add(new Node(1,5));
		graph2.get(1).add(new Node(2,4));
		
		// node 2�� ����� node ���� ���� (��� index, �Ÿ�)
		graph2.get(2).add(new Node(0,5));
		
		for (int i=0;i<graph2.size();i++) {
			for (int j=0; j<graph2.get(i).size();j++) {
				graph2.get(i).get(j).show();
			}
			System.out.println();
		}
	}
}

//���� ����Ʈ
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


