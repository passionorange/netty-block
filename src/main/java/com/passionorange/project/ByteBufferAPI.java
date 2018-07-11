package com.passionorange.project;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufferAPI {
	
	public static void what_is_heap_backed_byte_buffer() {
		ByteBuf heapbuffer = Unpooled.buffer(1024);
		heapbuffer.writeBytes("Hello World".getBytes());
		if(!heapbuffer.hasArray()) {
			throw new RuntimeException("No Array backing found for:" + heapbuffer);
		}
		byte[] array = heapbuffer.array();
		//Returns the offset of the first byte within the backing byte array of
	    // this buffer.
		int offset = heapbuffer.arrayOffset() + heapbuffer.readerIndex();
		//Returns the number of readable bytes which is equal to
	    // {@code (this.writerIndex - this.readerIndex)}.
		int length = heapbuffer.readableBytes();
		CharSequence charSequence = heapbuffer.getCharSequence(offset, length, Charset.defaultCharset());
		System.out.println(charSequence);
	}
	
	public static void main(String[] args) {
		what_is_heap_backed_byte_buffer();
	}
	
}
