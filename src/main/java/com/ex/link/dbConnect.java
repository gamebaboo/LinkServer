package com.ex.link;

import com.ex.link.db.HikariCP;
import org.springframework.context.ApplicationContext;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by User on 2017-06-30.
 *
 * DB접속할 수 있는 Class
 *
 */
@Service
public class dbConnect {

    private Connection connection = null;
    private PreparedStatement pstmt = null;
    private ResultSet resultSet = null;             // Query에 Result를 담는 Set
    private DataSource datasource = null;           // DataSource 를 통해 DB 접속정보 저장
    private StringBuilder sb = new StringBuilder(); // sb를 써서 성능에 대한 부분도 고려하였다. addBatch()를 통해 다량의 데이터를 처리하는 것도 가능하다.

    public String select(String message) throws SQLException {

        try
        {
            datasource = HikariCP.getDataSource();    // DB 연결 Config를 불러온다.
            connection = datasource.getConnection();  // DB 연결정보를 connection에 저장하셔 활용
            /*
            *    DB 연결 완료
            */
            pstmt = connection.prepareStatement("SELECT * FROM data"); // Query가 들어가는 부분
            System.out.println("The Connection Object is of Class: " + connection.getClass());
            resultSet = pstmt.executeQuery(); // 쿼리실행의 값들을 가져오는 부분이다.


            while (resultSet.next())
            {
                sb.append("Complete:" + resultSet.getString(1) + "," + resultSet.getString(2)+"\n");
                System.out.println(resultSet.getString(1) + "," + resultSet.getString(2));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            pstmt.close();         // PrepareStatement 종료
            connection.close();    // Connection 종료
            return sb.toString();  // StringBuffer 리턴
        }
    }

    public String insert(String mess) throws SQLException {

        try
        {
            datasource = HikariCP.getDataSource();    // DB 연결 Config를 불러온다.
            connection = datasource.getConnection();  // DB 연결정보를 connection에 저장하셔 활용

            /*
            *    DB 연결 완료
            */

            System.out.println("The Connection Object is of Class: " + connection.getClass());
            pstmt = connection.prepareStatement(
                    "INSERT INTO data (data_type, date) VALUES (?,?)"
            );

            //insert data
            pstmt.setString(1,"D07");
            pstmt.setString(2,"20170701");

            // Execute Update
            pstmt.executeUpdate();

            System.out.println("Record is inserted into DBUSER table!");
            connection.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            pstmt.close();
            connection.close();
            return mess;
        }
    }
}
