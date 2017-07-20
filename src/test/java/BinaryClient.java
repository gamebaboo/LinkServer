package com.ex.link;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.Main;

/**
 * Created by sijo2 on 2017-06-29.
 */
public class BinaryClient extends RouteBuilder {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.setApplicationContextUri("META-INF/spring/app-context.xml");
        main.run(args);
    }

    @Override
    public void configure() throws Exception {

        /*
        Netty 라이브러리를 활용

        ## Option ##
          sync=false (값을 주지 않음, Req-Res 관계일 때 true 값을 설정)
          allowDefaultCodec=false (ByteBuf로 동작하는 Codec을 Netty에서 제공하는 StringCodec을 사용하여 설정 변경)
          #(anything) app-context에 정의된 Bean을  지정할 수 있는 공간

        */
        from("netty4:tcp://localhost:7000?allowDefaultCodec=false&encoder=#stringEncoder&decoder=#stringDecoder")
                .bean(CreateMessage.class, "cmsMessage") // CMS 메시지가 들어온다고 가정
                //.setHeader("endpoint")
                //.method(MessageRouter.class, "getDestinationsFromMessage")
                //.bean(dbConnect.class, "select")
                .setHeader(Exchange.FILE_NAME, constant("data"))
                .to("file:D:\\output");

        from("netty4:tcp://localhost:8888?allowDefaultCodec=false&encoder=#stringEncoder&decoder=#stringDecoder")
                .bean(CreateMessage.class, "cmsMessage")
                //.bean(dbConnect.class, "insert")
                .setHeader(Exchange.FILE_NAME, constant("data"))
                .to("file:D:\\output");

    }
}
