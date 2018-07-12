package passionorange.demo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Sample class to show different types of byte buffers
 */
public class ByteBufferAPIDemo {
	
	/**
	 * Heap byte buffer, backed by an array
	 */
	public static void this_is_example_of_heap_backed_byte_buffer() {
		//creates a heap buffer
		ByteBuf heapbuffer = Unpooled.buffer(1024);
		heapbuffer.writeBytes("Hello World".getBytes());
		heap_buffer_should_have_a_backing_array(heapbuffer);
		CharSequence charSequence = readFromByteBuf(heapbuffer,heapbuffer.arrayOffset());
		System.out.println(charSequence);
	}
	
	/**
	 * Heap Buffer always have a backing array on heap
	 * @param heapbuffer
	 */
	private static void heap_buffer_should_have_a_backing_array(ByteBuf heapbuffer) {
		if (!heapbuffer.hasArray()) {
			throw new RuntimeException("No Array backing found for:" + heapbuffer);
		}
	}
	
	/**
	 * Read the byte buf 
	 * @param byteBuf
	 * @param initial_offset
	 * @return
	 */
	private static CharSequence readFromByteBuf(ByteBuf byteBuf,int initial_offset) {
		// Returns the offset of the first byte within the backing byte array of
		// this buffer.
		int offset = initial_offset + byteBuf.readerIndex();
		// Returns the number of readable bytes which is equal to
		// {@code (this.writerIndex - this.readerIndex)}.
		int length = byteBuf.readableBytes();
		//getCharSequence takes an offset, readCharSequence doesn't need it, will always start from readIndex
		CharSequence charSequence = byteBuf.getCharSequence(offset, length, Charset.defaultCharset());
		return charSequence;
	}
	
	public static void this_is_example_of_direct_byte_buffer() {
		ByteBuf directBuffer = Unpooled.directBuffer(1024);
		directBuffer.writeBytes("Hello World - Direct Buffer outside jvm heap space".getBytes());
		CharSequence charSequence = readFromByteBuf(directBuffer,0);
		System.out.println(charSequence);
	}

	public static void main(String[] args) {
		this_is_example_of_heap_backed_byte_buffer();
		this_is_example_of_direct_byte_buffer();
	}

}
