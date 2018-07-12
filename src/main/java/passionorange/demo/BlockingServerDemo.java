package passionorange.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Talk to server using telnet 3000
 *
 */
public class BlockingServerDemo {

	private ExecutorService es = Executors.newFixedThreadPool(3);

	private class ClientSocketHandler implements Runnable {

		Socket clientSocket;

		ClientSocketHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			OutputStream clientstream = null;
			try {
				System.out.println("Writing to client socket " + clientSocket.getRemoteSocketAddress());
				clientstream = this.clientSocket.getOutputStream();
				clientstream.write("Hello World".getBytes());
				clientstream.flush();
			} catch (IOException e) {
				// no-op
			} finally {
				if (clientstream != null)
					try {
						clientstream.close();
					} catch (IOException e) {
						// no-op
					}
			}
		}

	}

	private ServerSocket socket;

	public void init() {
		try {
			socket = new ServerSocket(3000);
		} catch (IOException e) {
			//no-op
		}
	}

	public void listen() {
		while (true) {
			try {
				final Socket clientSocket = socket.accept();
				ClientSocketHandler clientSocketHandler = new ClientSocketHandler(clientSocket);
				es.submit(clientSocketHandler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		BlockingServerDemo blockingServerDemo = new BlockingServerDemo();
		blockingServerDemo.init();
		blockingServerDemo.listen();
	}

}
