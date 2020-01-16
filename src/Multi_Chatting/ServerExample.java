package Multi_Chatting;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.security.ntlm.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//javaFx 메인 클래스로 만들기 위해서 Application 상속 받는다 
public class ServerExample extends Application {
	
	//스레드풀인 ExecutorService 선언
	ExecutorService executorService;
	//클라이언틔 연결 수락하는 ServerSocket 선언
	ServerSocket serverSocket;
	//연결된 클라이언트 저장하기 위해서 List 선언, 스레드에 안전한 Vector로 초기화
	List<Client> connections = new Vector<Client>();

	//서버 시작 코드
	void startServer() {
		executorService = Executors.newFixedThreadPool(
				//cpu 코어 수 만큼 스레드를 만들도록 한다.
				Runtime.getRuntime().availableProcessors()
				);
		
		try {
			serverSocket = new ServerSocket();
			//serverSocket을 로컬 컴퓨터 5001 포트와 바인딩 한다.
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
			
			//수락 작업 생성
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					//작업 스레드는 UI를 변경하지 못하므로 Platform.runLater()가 사용
					Platform.runLater(()->{
						//서버 시작을 출력하도록 displayText() 호출
						displayText("[서버 시작]");
						//start 버튼의 글자를 stop로 변경
						btnStartStop.setText("stop");
					});
					
				while(true) {
					try {
						//연결 수락
						Socket socket = serverSocket.accept();
						//클라이언트 IP주소와 스레드 이름 포함된 연결 수락 메세지
						String message = "[연결 수락 : " + socket.getRemoteSocketAddress() +
										" : " + Thread.currentThread().getName() + " ]";
						//메세지 출력하도록 displayText() 호출
						Platform.runLater(()->displayText(message));
						
						//Client 객체 생성
						Client client = new Client(socket);
						//Client 객체를 connections 컬렉션에 추가한다
						connections.add(client);
						//connections 컬렉션에 저장된 Client 객체 수를 출력하도록 displayText() 호출
						Platform.runLater(()->displayText("[연결 개수: " + connections.size() + " ]"));
					} catch (Exception e) {
						if(!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
					
				}
			};
			//스레드풀의 작업 스레드에서 연결 수락 작업을 처리하기 위해 submit() 호출
			executorService.submit(runnable);
		} catch (Exception e) {
			if(!serverSocket.isClosed()) {
				stopServer();
			}
			//serverSocket 닫고 startServer() 종료
			return;
		}
	}
	
	//서버 종료 코드
	void stopServer() {
		
	}
	
	
	//데이터 통신 코드
	class Client {
		
	}


}
