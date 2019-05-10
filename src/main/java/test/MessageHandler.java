package test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketChunkedInput;
import io.netty.handler.stream.ChunkedStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MessageHandler {
    private String text;
    private ChannelHandlerContext ctx;

    public MessageHandler(ChannelHandlerContext ctx, String text){
        this.ctx = ctx;
        this.text = text;
    }

    private static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }

    public void handle(){
        File file = new File("test.jpeg");
        byte[] bArray = readFileToByteArray(file);

        sendMessage(bArray);
    }

    public void sendMessage(byte[] msg) {
        ChunkedStream stream = new ChunkedStream(new ByteArrayInputStream(msg));
        ChannelFuture sendFuture;
        sendFuture = ctx.write(new WebSocketChunkedInput(stream), ctx.newProgressivePromise());
        sendFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println(future.channel() + " Transfer progress: " + progress);
                } else {
                    System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                System.err.println(future.channel() + " Transfer complete.");
            }
        });

        ctx.flush();

        if (!sendFuture.isSuccess()) {
            System.out.println("Send failed: " + sendFuture.cause());
        }
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }
}
