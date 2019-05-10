package test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {


    WebSocketServerInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
//        pipeline.addLast(new ChunkedWriteHandler()); // browsers do not support this yet
        pipeline.addLast(new WebSocketServerHandler());
    }
}
