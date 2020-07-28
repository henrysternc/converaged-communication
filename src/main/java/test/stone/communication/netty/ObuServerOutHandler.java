package test.stone.communication.netty;

import io.netty.channel.ChannelFuture;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import io.netty.buffer.ByteBuf;
@Slf4j
public class ObuServerOutHandler extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

		if (msg instanceof byte[]) {
			byte[] bytesWrite = (byte[]) msg;
			ByteBuf buf = ctx.alloc().buffer(bytesWrite.length);

			buf.writeBytes(bytesWrite);
			ctx.writeAndFlush(buf).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					log.info("写成功！");
				}
			});
		}

	}
}
