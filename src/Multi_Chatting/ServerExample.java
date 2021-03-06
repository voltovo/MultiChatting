package Multi_Chatting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.security.ntlm.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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
		try {
			//connections 컬렉션으로부터 반복자를 얻어낸다
			Iterator<Client> iterator = connections.iterator();
			
			while(iterator.hasNext()) {
				//Client를 하나씩 얻는다
				Client client = iterator.next();
				//Client가 가지고 있는 Socket을 닫는다
				client.socket.close();
				//connections 컬렉션에서 Client를 제거 한다
				iterator.remove();
			}
			
			//serverSocket 닫기
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			
			//executorService 종료
			if(executorService != null && !executorService.isShutdown()) {
				executorService.isShutdown();
			}
			
			//작업 스레드는 UI를 변경하지 못하므로 Platform.runLater() 사용
			Platform.runLater(()->{
				displayText("[서버 멈춤]");
				//stop 버튼의 글자를 start로 변경
				btnStartStop.setText("start");
			});
		} catch (Exception e) {
			
		}
	}
	
	
	//데이터 통신 코드
	class Client {
		Socket socket;
		
		//Client 생성자 선언
		Client(Socket socket){
			this.socket = socket;
			receive();
		}
		
		//데이터를 받기 위해
		void receive() {
			//받기 위해 runnable 정의
			Runnable runnable = new Runnable() {
				//run() 재정의
				@Override
				public void run() {
					try {
						while(true) {
							//받은 데이터를 저장할 byte[]배열인 byteArr 생성
							byte[] byteArr = new byte[100];
							//socket으로 부터 inputStream 얻기
							InputStream inputStream = socket.getInputStream();
							
							//클라이언트가 데이터를 보내기 전까지 블로킹, 데이터를 받으면 byteArr에 저장한 후 받은 
							//바이트 개수를 readByteCnt에 저장
							int readByteCnt = inputStream.read(byteArr);
							
							//클라이언트가 정상적으로 Socket의 close()를 호출 했을 경우,
							//read()메소드는 -1을 리턴, 이 경우 강제 IOException 발생
							if(readByteCnt == -1) {
								throw new IOException();
							}
							
							//정상적으로 데이터 받았을 경우 
							String message = "[요청 처리 : " + socket.getRemoteSocketAddress() + 
									": " + Thread.currentThread().getName() + " ]";
							
							//문자열 출력
							Platform.runLater(()->displayText(message));
							
							//UTF-8로 디코딩한 문자열 얻기
							String data = new String(byteArr, 0, readByteCnt, "UTF-8");
							
							//문자열을 모든 클라이언트에게 보내기 위해 connections 에 저장된 Client를 하나씩 얻어 send() 메소드 호출
							for(Client client : connections) {
								client.send(data);
							}
						}
					} catch (Exception e) {
						try {
							//예외가 발생하면 connections 컬렉션에서 Client 객체 제거
							connections.remove(Client.this);
							
							String message = "[클라이언트 통신 안됨 : " + 
											socket.getRemoteSocketAddress() +
											": " + Thread.currentThread().getName() + " ]";
							//문자열 출력
							Platform.runLater(()->displayText(message));
							//socket 닫는다
							socket.close();
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
				}
			};
			//스레드풀의 작업 스레드에서 연결 수락 작업을 처리하기 위해 submit() 호출
			executorService.submit(runnable);
		}
		
		//데이터를 보내기 위해
		void send(String data) {
			
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						//보낼 문자열로부터 UTF-8로인토딩한 바이트 배열을 얻는다
						byte[] byteArr = data.getBytes("UTF-8");
						//소켓에서 출력스트림을 얻는다
						OutputStream outputStream = socket.getOutputStream();
						//바이트 배열을 매개값으로 해서 write() 호출
						outputStream.write(byteArr);
						//출력 스트림의 내부 버퍼를 완전히 비우도록 flush() 호출
						outputStream.flush();
					} catch (Exception e) {
						try {
							//예외처리
							String message = "[클라이언트 통신 안됨: " + 
											socket.getRemoteSocketAddress() + ": " + 
											Thread.currentThread().getName() + " ]";
							Platform.runLater(()->displayText(message));
							//connections 컬렉션에서 예외가 발생한 client를 제거
							connections.remove(Client.this);
							//socket을 닫는다
							socket.close();
						} catch (Exception e2) {
						}
					}
					
				}
			};
			
			//스레드풀의 작업 스레드에서 연결 수락 작업을 처리하기 위해 submit() 호출
			executorService.submit(runnable);
			
		}
	}

	TextArea txtDisplay;
	Button btnStartStop;
	
	@Override
	public void start(Stage primaryStage)throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		
		//start와 stop 버튼을 클릭했을 때 이벤트 처리 코드
		btnStartStop.setOnAction(e->{
			if(btnStartStop.getText().equals("start")) {
				startServer();
			}
			else if(btnStartStop.getText().equals("stop")) {
				stopServer();
			}
		});
		
		root.setBottom(btnStartStop);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		//윈도우 우측 상단 닫기 버튼을 클릭했을 때 이벤트 처리 코드
		primaryStage.setOnCloseRequest(event->stopServer());
		primaryStage.show();
	}
	
	//작업 스레드의 작업 처리 내용을 출력할 때 호출하는 메소드
	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

