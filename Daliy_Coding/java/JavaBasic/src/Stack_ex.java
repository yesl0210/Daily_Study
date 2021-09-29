import java.util.*;

public class Stack_ex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Stack<Integer> stack = new Stack<>();
		
		System.out.println("Stack : 후입선출");
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.push(4);
		System.out.println("stack : "+stack);
		System.out.println("pop : "+stack.pop()+", stack : "+stack);
		stack.push(5);
		System.out.println("peek : "+stack.peek()+", stack : "+stack);
		stack.push(6);
		System.out.println("stack : "+stack);
		while(!stack.isEmpty()) {
			System.out.println("pop : "+stack.pop()+", stack : "+stack);
		}
		
		

	}

}
