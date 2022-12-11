package dao;

import model.*;
import utilities.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class DAOMazoMySQL {

    private CardFace[] eFiguras = CardFace.values();
    private CardSuit[] ePalos = CardSuit.values();
    Connection con = null;

    public DAOMazoMySQL() {

        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            con = ConnectionDB.getInstance();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Card> getCars() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            ArrayList<Card> cars = new ArrayList<>();
            String query = "SELECT * FROM deck where usado <> 1";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int palo = rs.getInt("palo");
                boolean esFigura = rs.getBoolean("es_figura");
                regenerateCards(esFigura, nombre, palo);
                cars.add(regenerateCards(esFigura, nombre, palo));
            }
            return cars;
        }
    }

    public Card regenerateCards(boolean esFigura, String nombre, int palo) {
        Card card;
        if (esFigura) {
            card = new FacedCard(eFiguras[Integer.parseInt(nombre)], ePalos[palo]);
            return card;
        } else {
            card = new NumeredCard(Integer.parseInt(nombre), ePalos[palo]);
            return card;
        }
    }

    public void updateCartasUsada(String cartaCodigo) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update deck set usado = true  WHERE codigo = ? ")) {
            stmt.setString(1, cartaCodigo);
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update card record");
            }
        }
    }

    public void resetMazo() throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update deck set usado = false   ")) {
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to reset card record");
            }
        }
    }



    public void addCartaMano(String nombre, int id_carta, Boolean esBank) throws SQLException {

        try (PreparedStatement stmt = con.prepareStatement("insert into hand values(?,?,?)")) {
            stmt.setString(1, nombre);
            stmt.setInt(2, id_carta);
            stmt.setBoolean(3, esBank);
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to delete a employee record");
            } else {
                System.out.println("player with created");
            }
        }
    }

    public int getIDCarta(String cod_Carta) throws SQLException {
        int id = 0;
        try (PreparedStatement stmt = con.prepareStatement("SELECT id_carta FROM deck WHERE codigo = ?")) {
            stmt.setString(1, cod_Carta);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id_carta");
            }

        }
        return id;
    }



    public void closeConnection() throws SQLException {
        con.close();
    }
}
