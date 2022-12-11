package dao;

import model.Card;
import utilities.ConnectionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOManoMySQL {
    Connection con = null;
    DAOMazoMySQL daoMazoMySQL = new DAOMazoMySQL();
    public DAOManoMySQL() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            con = ConnectionDB.getInstance();
        } catch (
                SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public ArrayList<Card> getManosCars(String jugador, boolean es_banco) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT d.* FROM deck AS d,hand AS h  WHERE d.id_carta=h.id_carta AND h.nombre_jugador=? AND h.es_banca=?")) {
            ArrayList<Card> manos = new ArrayList<>();
            stmt.setString(1, jugador);
            stmt.setBoolean(2, es_banco);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int palo = rs.getInt("palo");
                boolean esFigura = rs.getBoolean("es_figura");
                daoMazoMySQL.regenerateCards(esFigura, nombre, palo);
                manos.add(daoMazoMySQL.regenerateCards(esFigura, nombre, palo));
            }
            return manos;
        }
    }

    public void resetMano(String nombre_jugador) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("delete from hand where nombre_jugador=? ")) {
            stmt.setString(1, nombre_jugador);
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to hand cards record");
            }
        }
    }
    public float sumaMano (boolean es_banca,String nombre_jugador) throws SQLException {

        float suma = 0;

        try (PreparedStatement stmt = con.prepareStatement("SELECT SUM(d.valor) as TOTAL FROM deck AS d,hand AS h  WHERE d.id_carta=h.id_carta AND h.nombre_jugador=?AND h.es_banca=?")) {
            stmt.setString(1, nombre_jugador);
            stmt.setBoolean(2, es_banca);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                suma = rs.getFloat("TOTAL");
            }

        }
        return suma;

    }



}



