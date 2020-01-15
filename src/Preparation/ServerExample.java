package Preparation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {

	public static void main(String[] args) {
		//마지막에 close 하기 위해서  상단에 생성
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			//서버 소켓 객체 생성
			serverSocket = new ServerSocket();
			//서버 소켓 포트 지정
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
			
			while(true) {
				System.out.println("[연결 기다림]");
				
				//클라이언트 연결 수락
				socket = serverSocket.accept();
				//클라이언트 IP 정보 얻기 위해서 InetSocketAddress 객체 생성
				InetSocketAddress isa = (InetSocketAddress)socket.getRemoteSocketAddress();
				System.out.println("[연결 수락함] " + isa.getHostName());
				
				//메세지 보내고 받기
				byte[] bytes = null;
				String message = null;
				
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
				
				//메세지 보내기 위해서 OutPutStream 객체 생성
				OutputStream os = socket.getOutputStream();
				//서버에게 보낼 메세지
				message = "Hello Client";
				//UTF-8 로 인코딩
				bytes = message.getBytes("UTF-8");
				//outStream 이용해서 메세지 쓰기
				os.write(bytes);
				//보내기
				os.flush();
				System.out.println("[데이터 보내기 성공]");
				
				//객체 닫기
				os.close();
				is.close();
				socket.close();
				
				
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		//ServerSocket 가 닫혀있지 않을 경우
		if(!serverSocket.isClosed()) {
			try {
				//serverSocket 닫기
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
