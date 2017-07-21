package com.ex.link.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by User on 2017-06-30.
 *
 * DB 접속 Conifguration을 저장하는 HikariCP 클래스이다.
 *
 */
@Configuration
public class HikariCP {


    private static DataSource datasource = null;

    public static DataSource getDataSource() {

        if(datasource == null)
        {
            HikariConfig config = new HikariConfig("/db/hikari.properties"); // "hikari.properties"에서 커넥션 정보를 가져온다. 즉, 커스터마이징을 할 때에는 "hikari.properties"를 수정하면 된다.
            config.setMaximumPoolSize(10); // hikariCP의 성능은 빠르기 때문에 PoolSize를 크게 지정하지 하지 않아도 된다.
            config.setAutoCommit(false);
            datasource = new HikariDataSource(config);
        }
        return datasource;
    }

}


