package dao;

import model.*;
import utilities.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class DAOMazoMySQL implements IdaoMazoMySQL {

    private CardFace[] eFiguras = CardFace.values();
    private CardSuit[] ePalos = CardSuit.values();
    private Connection con = null;

    public DAOMazoMySQL() throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        con = ConnectionDB.getInstance();
    }

    @Override
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

    @Override
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

    @Override
    public void updateCartasUsada(String cartaCodigo) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update deck set usado = true  WHERE codigo = ? ")) {
            stmt.setString(1, cartaCodigo);
            stmt.executeUpdate();
        }
    }

    @Override
    public void resetMazo() throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update deck set usado = false   ")) {
            stmt.executeUpdate();
        }
    }

    @Override
    public void addCartaMano(String nombre, int id_carta, Boolean esBank) throws SQLException {

        try (PreparedStatement stmt = con.prepareStatement("insert into hand values(?,?,?)")) {
            stmt.setString(1, nombre);
            stmt.setInt(2, id_carta);
            stmt.setBoolean(3, esBank);
            stmt.executeUpdate();
        }
    }

    @Override
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

}
