import java.util.*;

public class Queue_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Queue<Integer> queue = new LinkedList<>();
		
		System.out.println("Queue : 선입선출");
		
		// 추가
		queue.add(1); // 저장공간 부족 -> 예외발생
		queue.offer(2); // 저장공간 부족 -> false return
		queue.offer(3);
		queue.offer(4);
		queue.offer(5);
		System.out.println("queue : "+queue);
		System.out.println("peek : "+queue.peek()); // 객체를 삭제없이 읽기만 함
		System.out.println("queue : "+queue);
		System.out.println(queue.poll());
		System.out.println("queue : "+queue); // 객체 queue에서 삭제 및 반환
		
		while(!queue.isEmpty()) { // queue가 빌 때까지 계속
			System.out.println("return : "+queue.poll()+", queue : "+queue);
		}
		System.out.println("Size : "+queue.size());
		
	}

}
