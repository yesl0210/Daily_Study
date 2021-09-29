import java.io.*;

public class UDLR {

	public static void main(String[] args) throws Exception {
		// �Ϸ��� ��ɿ� ���� ��ü�� �̵���Ŵ --> '�ùķ��̼�' ����
		// O(N = ��ɾ� ����) : ���� Ƚ���� ��ɾ� ������ �����.
		
		// R R R U D D
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int size = Integer.parseInt(br.readLine());
		String[] command = br.readLine().split(" ");
		int x = 1, y = 1;
		System.out.println("��ɾ� : ");
		for(String com : command) {
			System.out.print(com);
		}
		System.out.println();
		
		// L, R, U, D�� ���� �̵� ����
		int[] dx = {0,0,-1,1};
		int[] dy = {-1,1,0,0};
		char[] moveTypes = {'L', 'R', 'U', 'D'};
		
		for(String com : command) {
			int nx = x, ny = y;
			System.out.println("��ɾ� : "+com+", ���� ��ġ - x : "+x+", y : "+y);
			for(int j=0;j<moveTypes.length;j++) {
				if(com.charAt(0) == moveTypes[j]) {
					nx += dx[j];
					ny += dy[j];
				}
			}
			System.out.print("�̵��� ��ġ - nx : "+nx+", ny : "+ny);
			if(nx<1 || nx > size || ny<1 || ny>size) {
				System.out.println("\t�̵� ����\n");
				continue;
			}
			x = nx;
			y = ny;
			System.out.println("\t�̵� ����\n");
		}
		
		System.out.println("���� ���� : "+x+","+y);
	}

}
