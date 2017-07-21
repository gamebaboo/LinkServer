package com.ex.link;


import org.apache.camel.Exchange;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

/**
 * Created by 송시조 on 2017-07-11.
 *
 * 메시지를 분리해주는 역할을 하는 Class
 *
 */
public class MessageRouter {

    public String getDestinationsFromMessage(Exchange exchange) {


        StringBuilder sb = new StringBuilder(exchange.getIn().getBody().toString());
        System.out.println("Message : "  + exchange.getIn().getBody().toString());


        //String orderType = sb.substring(1,4); 업무 타입
        exchange.getOut().setHeader(Exchange.FILE_NAME, constant(sb.substring(1,12))); // FileName을 설정
        return "direct:" + sb.substring(1,4); // 업무 타입으로 분기

    }
}
