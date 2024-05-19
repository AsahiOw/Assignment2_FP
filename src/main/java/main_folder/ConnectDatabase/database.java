/**
 * @Nguyen Ha Tuan Nguyen - s3978072
 * @Luong Tuan Kiet - s3980288
 * @Tran Tuan Minh - s3804812
 * @Nguyen Thanh Tung - s3979489
 * <Ooptional>
 */
package main_folder.ConnectDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    String url = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?user=postgres.mczllvsxcqnfxlewvpkr&password=OOPtional2024";
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
