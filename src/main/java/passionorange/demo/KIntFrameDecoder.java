package passionorange.demo;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Decodes k ints at a time.  
 *
 */
public class KIntFrameDecoder extends ByteToMessageDecoder {
	int k;

	KIntFrameDecoder(int k) {
		this.k = k;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.readableBytes() >= k * Integer.SIZE/Byte.SIZE) {
			//Transfers this buffer's data to a newly created buffer starting at readIndex
			Integer buf = in.readInt(); 
			out.add(buf);
		}  
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.SIZE/Byte.SIZE);
		//Creates a new big-endian Java heap buffer with reasonably small initial capacity, which expands its capacity boundlessly on demand.
		ByteBuf buf = Unpooled.buffer();
		for (int i = 0; i < 10; i++) {
            buf.writeInt(i);
        }
		//Returns a buffer which shares the whole region of this buffer.
	    //Modifying the content of the returned buffer or this buffer affects
	    //each other's content while they maintain separate indexes and marks.
		ByteBuf input = buf.duplicate();
		//takes a channel inbound handler adaptor
		EmbeddedChannel channel = new EmbeddedChannel(new KIntFrameDecoder(5));
		//retain increases the reference count by 1
		channel.writeInbound(input);
		channel.finish();
		
		Integer value;
		while((value = channel.readInbound())!=null) {
			System.out.println(value + " ");
		}
		channel.close();

	}

}
