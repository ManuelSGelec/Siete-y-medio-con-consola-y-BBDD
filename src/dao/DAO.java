package dao;


import java.sql.SQLException;

public interface DAO {


    //void deleteEmployee(int idEmployee) throws SQLException;
    void addBD(String query) throws SQLException;
    void updateBD(String query) throws SQLException;
    void closeConnection() throws SQLException ;
}
