package com.iqb.league;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExampleWriter {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Veritabanı bağlantısını oluştur
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");

            // yearVar değişkenini belirle
            int yearVar = 1997;

            // SQL sorgusunu hazırla
            String sql = "INSERT INTO years (id_, year, age) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 124);
            preparedStatement.setInt(2, yearVar);
            preparedStatement.setInt(3, 2);


            // Sorguyu çalıştır
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Yeni bir satır başarıyla eklendi!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Kaynakları kapat
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
