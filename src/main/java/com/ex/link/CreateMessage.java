package com.ex.link;

import org.springframework.stereotype.Service;

/**
 * Created by User on 2017-06-30.
 *
 * 임의의 Message를 생성하여 Test용도로 사용하는 Class
 *
 */
@Service
public class CreateMessage {

    public String cmsMessage(String message){
        String str = "HD0720170630 DD0720170630";
        //            Header       Data
        System.out.println("프린트");
        return str;

    }
}
