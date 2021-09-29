import java.util.*;
import java.io.*;

public class ArrayList_Ex {

	public static void main(String[] args) {
		// ArrayList 선언
		System.out.println("* Initialize *");
		ArrayList<Integer> arr1 = new ArrayList<Integer>();
		for(int i=2;i<=10;i=i+2) {
			arr1.add(i);
		}
		System.out.println("arr1 : "+arr1);
		
		// ArrayList 생성시 원소도 함께 선언
		System.out.println("\n* Initialize with item *");
		ArrayList<Integer> arr2 = new ArrayList<Integer>(Arrays.asList(1,3,5,7,9));
		System.out.println("arr2 : "+arr2);
		
		// ArrayList item 추가
		System.out.println("\n* Add item *");
		arr1.add(11);
		arr1.add(0,0); // index 0에 item 0 삽입
		System.out.println("arr1 : "+arr1);
		
		// ArrayList item 제거
		System.out.println("\n* Remove item *");
		arr1.remove(arr1.size()-1);
		System.out.println("arr1 : "+arr1);
		
		ArrayList<Integer> empty = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
		System.out.println("empty : "+empty);
		empty.clear();
		System.out.println("empty : "+empty);
		
		// ArrayList 출력
		System.out.println("\n* Print item *");
		for (int i : arr1) {
			System.out.print(i+" ");
		}
		
		// ArrayList 값 검색
		System.out.println("\n* Find item *");
		System.out.println("Is there a 2 ? : "+arr1.contains(2));
		System.out.println("Is there a 3 ? : "+arr1.contains(3));
		System.out.println("index of 2 ? : "+arr1.indexOf(2));
		
		ArrayList<Integer> arr3 = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,5));
		System.out.println("arr3 : "+arr3);
		System.out.println("index of 5 ? : "+arr3.indexOf(5));
		System.out.println("index of 5 ? : "+arr3.lastIndexOf(5));
		
		// ArrayList 깊은 복사
		System.out.println("\n* Deep copy *");
		ArrayList<Integer> copyArr = new ArrayList<Integer>();
		copyArr.addAll(arr1);
		System.out.println("copyArr : "+copyArr);
		
		// ArrayList sort
		System.out.println("\n* Sort *");
		ArrayList<Integer> arr4 = new ArrayList<Integer>(Arrays.asList(6,2,5,3,4,1));
		System.out.println("arr4 : "+arr4);
		arr4.sort(null);
		System.out.println("arr4 : "+arr4);
		
		// ArrayList <-> String 배열
		System.out.println("\n* Convert *");
		ArrayList<String> arr5 = new ArrayList<String>(Arrays.asList("4","2","5","3","2","1"));
		System.out.println("arr5 : "+arr5);
		
		// ArrayList -> String 배열
		String[] str = new String[arr5.size()];
		arr5.toArray(str);
		for (String i : str) {
			System.out.print(i+" ");
		}
		
		// String 배열 -> ArrayList
		ArrayList<String> convArr = new ArrayList<String>(Arrays.asList(str));
		System.out.println("\nconvArr : "+convArr);
		
		// ArrayList Reverse
		System.out.println("\n* Reverse *");
		System.out.println("arr1 : "+arr1);
		Collections.reverse(arr1);
		System.out.println("arr1 : "+arr1);
		
		// ArrayList 최대, 최소
		System.out.println("\n* Max, Min *");
		System.out.println("arr1 Max : "+ Collections.max(arr1));
		System.out.println("arr1 Min : "+ Collections.min(arr1));		
		
		// ArrayList 빈도수
		System.out.println("arr3 frequency(5) : "+Collections.frequency(arr3, 5));
		
		// 중복 제거
		System.out.println("\n* Remove redundancy *");
		System.out.println("arr3 : "+arr3);
		HashSet<Integer> duplicate = new HashSet<Integer>(arr3);
		arr3 = new ArrayList<Integer>(duplicate);
		System.out.println("arr3 : "+arr3);
	}

}
