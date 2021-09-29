import java.util.*;

public class Queue_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Queue<Integer> queue = new LinkedList<>();
		
		System.out.println("Queue : ���Լ���");
		
		// �߰�
		queue.add(1); // ������� ���� -> ���ܹ߻�
		queue.offer(2); // ������� ���� -> false return
		queue.offer(3);
		queue.offer(4);
		queue.offer(5);
		System.out.println("queue : "+queue);
		System.out.println("peek : "+queue.peek()); // ��ü�� �������� �б⸸ ��
		System.out.println("queue : "+queue);
		System.out.println(queue.poll());
		System.out.println("queue : "+queue); // ��ü queue���� ���� �� ��ȯ
		
		while(!queue.isEmpty()) { // queue�� �� ������ ���
			System.out.println("return : "+queue.poll()+", queue : "+queue);
		}
		System.out.println("Size : "+queue.size());
		
	}

}
