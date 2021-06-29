package academy.mindswap.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	private ExecutorService service;

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start(8001);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		service = Executors.newCachedThreadPool();

		while (true) {
			Socket clientSocket = serverSocket.accept();
			service.submit(new ConnectionHandler(clientSocket));
		}
	}

	public class ConnectionHandler implements Runnable {
		private final Socket socket;
		private BufferedReader in;

		public ConnectionHandler(Socket socket) throws IOException {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String request = in.readLine();
				System.out.println(request);
				dealWithRequests(request, socket);

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void dealWithRequests(String request, Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String[] requestInString = request.split(" ");
		switch (requestInString[1]) {
			case "/":
			case "/homepage":
			case "/index":
				File index = new File("resources/index.html");
				FileInputStream inputStreamBytes = new FileInputStream(index);

				out.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: text/html; charset=UTF-8\r\n" +
						"Content-Length: " + index.length() + "\r\n\r\n");
				byte[] fileBytes = new byte[(int) index.length()];
				inputStreamBytes.read(fileBytes);

				out.write(fileBytes);
				break;
			case "/img":
				File img = new File("resources/img.html");
				inputStreamBytes = new FileInputStream(img);

				out.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: text/html; charset=UTF-8\r\n" +
						"Content-Length: " + img.length() + "\r\n\r\n");

				fileBytes = new byte[(int) img.length()];
				inputStreamBytes.read(fileBytes);
				out.write(fileBytes);
				break;
			case "/resources/img.jpg":
				img = new File("resources/img.jpg");
				inputStreamBytes = new FileInputStream(img);

				out.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: img/jpg; charset=UTF-8\r\n" +
						"Content-Length: " + img.length() + "\r\n\r\n");

				fileBytes = new byte[(int) img.length()];
				inputStreamBytes.read(fileBytes);
				out.write(fileBytes);
				break;
			default:
				File notFound = new File("resources/error.html");
				inputStreamBytes = new FileInputStream(notFound);
				out.writeBytes("HTTP/1.0 404 Not Found\r\n" +
						"Content-Type: text/html; charset=UTF-8\r\n" +
						"Content-Length: " + notFound.length() + "\r\n\r\n");

				fileBytes = new byte[(int) notFound.length()];
				inputStreamBytes.read(fileBytes);
				out.write(fileBytes);
				break;
		}

	}

}
