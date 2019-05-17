package test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketChunkedInput;
import io.netty.handler.stream.ChunkedStream;

import java.io.*;

public class MessageHandler {
    private String text;
    private ChannelHandlerContext ctx;
    private byte[] response;

    public MessageHandler(ChannelHandlerContext ctx, String text){
        this.ctx = ctx;
        this.text = text;
    }

    private static byte[] readFileToByteArray(File file) throws Exception{
        FileInputStream fis = null;

        byte[] bArray = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();
        return bArray;
    }

    public void handle(){
        try {
            File file = new File(this.text);
            byte[] bArray = readFileToByteArray(file);
            sendMessage(bArray);
        }
        catch (Exception ex){
            sendBadAnswer(getErrorBlob());
        }



    }


    public void sendBadAnswer(byte[] msg){
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public byte[] getErrorBlob() {
        return "$ERROR".getBytes();
    }

    public void sendMessage(byte[] msg) {
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }
}
