package com.ex.link;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.Main;

import java.util.TimeZone;

/**
 * Created by sijo2 on 2017-06-29.
 */
public class ServerRouteBuilder extends RouteBuilder {

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
                /*
                *
                *  Message의 Header를 읽어들여서 업무형태를 파악하고 업무형태 별로 정의된 bean Class를 실행시켜준다.
                *
                */
                .setHeader("MessageRoute")
                    .method(MessageRouter.class, "getDestinationsFromMessage") // 업무 타입에 따라 Message의 Endpoint를 정해서 리턴해줌
                .recipientList(header("MessageRoute")).ignoreInvalidEndpoints(); // ignoreInvalidEndpoints는 정의된 Endpoints가 아니면 메시지를 무시하는 메소드다.
                // 여기서 파일 명을 정함

        from("direct:D07")  // 업무형태가 D07일 경우 실행되는 프로세스를 정의해 놓았다.
                .bean(dbConnect.class, "select")
                .to("file:D:\\output");

        /*

            Quartz 사용법
            quartz2://timerName?options
            quartz2://groupName/timerName?options
            quartz2://groupName/timerName?cron=expression
            quartz2://timerName?cron=expression

            Cron 사용법 정의
            초 분 시간 일 월 요일 (년도[option])으로 구성된다.
            Camel에서는 + 를 붙여서 space를 대체한다.

            표현식
            / (interval(간격)을 의미한다. 예시 0/10   <-- 10초 당 실행)  앞의 숫자는 시작 기준으로 0부터 시작하여 10초 간격을 의미한다.
            - (term(기간)을 의미한다.  예시 12-17  <-- 12에서 17시까지)
            ? (설정값 없음)
            * (전체 허용)

            예시 0/10+*+8-10+?+*+*
            의미 매년, 매월, 어떤 요일이든, 8~10시 사이에, 어떤 분이든, 10초 간격으로 Task를 수행하는 Timer

         */
        // 매 10초당한번씩  0 0-5 14 * * ?

        from("quartz2://foo?cron=0+0/1+*+?+*+*")
                .bean(CreateMessage.class, "cmsMessage")
                //.bean(dbConnect.class, "insert")
                .setHeader(Exchange.FILE_NAME, constant("data-20170719"))
                .to("file:D:\\output");

        /*

            file
            file:// 파일 위치

            PollingConsumer : 특정 위치를 Polling 하면서 데이터가 생성되면 해당하는 Task를 수행하도록 하는 것

            예시 in Java
            Endpoint endpoint = context.getEndpoint("activemq:my.queue");
            PollingConsumer consumer = endpoint.createPollingConsumer();
            Exchange exchange = consumer.receive();

            즉, Queue나 파일의 변형을 주기적으로 감시(?)하면서 변화 양상이 있을 때 receive 혹은 Task를 수행하도록 유도하는 기법

            예시  from("file:D:\\output\\?fileName=data-${date:now:yyyyMMdd}&scheduler=quartz2&scheduler.cron=0/10+*+*+*+*+?&delete=true")
            의미  D:\\output\\ 폴더에 있는 data-${date:now:yyyyMMdd} 파일을 스케쥴러를 활용하여 cron timer 10초마다 실행해주고 실행 후에는 삭제해준다.

         */
        // 파일을 consumer로 쓰기 때문에 사용 후에는 사라진다.
        from("file:D:\\output\\?fileName=data-${date:now:yyyyMMdd}&scheduler=quartz2&scheduler.cron=5/10+*+*+*+*+?&delete=true")
                .bean(CreateMessage.class, "cmsMessage");
                //.bean(dbConnect.class, "insert")
    }
}
