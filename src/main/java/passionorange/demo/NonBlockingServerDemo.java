package passionorange.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Example of a non blocking server, uses JAVA NIO
 * use telnet 3001 to test
 */
public class NonBlockingServerDemo {

	Selector selector = null;

	public void openConnectionAndBindPort() {
		// A selectable channel for stream-oriented listening sockets.
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			// Retrieves a server socket associated with this channel.
			ServerSocket socket = serverChannel.socket();
			InetSocketAddress address = new InetSocketAddress(3001);
			// If the address is null, then the system will pick up an ephemeral port and a
			// valid local address to bind the socket.
			socket.bind(address);
			selector = Selector.open();
			// Registers this channel with OP_ACCEPT,Operation-set bit for socket-accept
			// operations.
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// no-op
		}
	}

	public void acceptClients() {
		while (true) {
			SelectionKey key = null;
			try {
				// Selects a set of keys whose corresponding channels are ready for I/O
				// operations.
				// This method performs a blocking operation.
				selector.select();
				// returns this selector's selected-key set.
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				// A token representing the registration of a channel to a selector
				System.out.println(readyKeys);
				Iterator<SelectionKey> iterator = readyKeys.iterator();
				while (iterator.hasNext()) {
					key = iterator.next();
					//extremely important to remove this, else will try to reporcess existing messages. 
					iterator.remove();
					// Tests whether this key's channel is ready to accept a new socket
					// If this key's channel does not support socket-accept operations then always
					// return false
					if (key.isAcceptable()) {
						// Returns the channel for which this key was created.
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						// Accepts a connection made to this channel's socket.
						SocketChannel client = server.accept();
						client.configureBlocking(false);
						final ByteBuffer msg = ByteBuffer.wrap("Hello World ! \r\n".getBytes());
						// Registers this channel with the given selector, returning a selection key.
						client.register(selector, SelectionKey.OP_WRITE, msg);
					}
					//by calling register, we registered this selector to be ready for write operation above.
					if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer buffer = (ByteBuffer) key.attachment();
						while (buffer.hasRemaining()) {
							if (client.write(buffer) == 0) {
								break;
							}
						}
						client.close();
					}
				}
			} catch (IOException e) {
				// no-op
				if (key != null) {
					// Requests that the registration of this key's channel with its selector
					key.cancel();
					try {
						// Closes this channel.
						key.channel().close();
					} catch (IOException cex) {
						// ignore on close
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		NonBlockingServerDemo nonBlockingServerDemo = new NonBlockingServerDemo();
		nonBlockingServerDemo.openConnectionAndBindPort();
		nonBlockingServerDemo.acceptClients();
	}
}
