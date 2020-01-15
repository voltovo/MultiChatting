package Preparation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientExample {

	public static void main(String[] args) {
		//마지막에 close 하기 위해서 상단에 생성
		Socket socket = null;
		try {
			//Socket 생성
			socket = new Socket();
			System.out.println("[연결 요청]");
			
			//서버쪽에 연결 요청
			socket.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[연결 성공]");
			
			//메세지 보내고 받기
			byte[] bytes = null;
			String message = null;
			
			//메세지 보내기 위해서 OutPutStream 객체 생성
			OutputStream os = socket.getOutputStream();
			//서버에게 보낼 메세지
			message = "Hello Server";
			//UTF-8 로 인코딩
			bytes = message.getBytes("UTF-8");
			//outStream 이용해서 메세지 쓰기
			os.write(bytes);
			//보내기
			os.flush();
			System.out.println("[데이터 보내기 성공]");
			
			//메세지 받기 위해서 InputStream 객체 생성
			InputStream is = socket.getInputStream();
			//서버에서 보낸 메세지 받을 수 있는 범위 지정
			bytes = new byte[100];
			//byte 배열에 저장하고 읽은 바이트 수 리턴
			//상대방이 비정상적으로 종료했을 경우 IOException 발생
			int readByteCnt = is.read(bytes);

			//상대방이 정상적으로 Socket의 close()를 호출했을 경우
			if(readByteCnt == -1) {
				//강제로 IOException 발생
				throw new IOException();
			}
			
			//메세지 디코딩 해서 받기
			message = new String(bytes, 0 , readByteCnt, "UTF-8");
			System.out.println("[데이터 받기 성공]: " + message);
			
			//객체 닫기
			os.close();
			is.close();
			
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		//소켓이 닫혀 있지 않은 경우 연결 끊기
			if(!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

	}

}
