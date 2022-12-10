package dao;

import model.*;
import utilities.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class DAOMySQL {

    private CardFace[] eFiguras = CardFace.values();
    private CardSuit[] ePalos = CardSuit.values();
    Connection con = null;

    public DAOMySQL() {

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

    private Card regenerateCards(boolean esFigura, String nombre, int palo) {
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


    public void addPlayer(String nombre) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("insert into player values (?,null,null,null,1)")) {
            stmt.setString(1, nombre);
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to delete a employee record");
            } else {
                System.out.println("player with created");
            }
        }
    }

    public void updatePlayer(Player jugador) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update player set pratidas_jugadas = ? , ganadas  = ?, perdidas = ?  WHERE nombre = ?")) {
            System.out.println("guardar");
            stmt.setInt(1, jugador.getPlayedGames());
            stmt.setInt(2, jugador.getWonGames());
            stmt.setInt(3, jugador.getLostGames());
            stmt.setString(4, jugador.getName());
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update player record");
            }
        }
    }

    public ArrayList<Player> getPlayers() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            ArrayList<Player> players = new ArrayList<>();
            String query = "SELECT * FROM player ";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int pratidas_jugadas = rs.getInt("pratidas_jugadas");
                int ganadas = rs.getInt("ganadas");
                int perdidas = rs.getInt("perdidas");

                players.add(new Player(nombre, pratidas_jugadas, ganadas, perdidas));
            }
            return players;
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
                regenerateCards(esFigura, nombre, palo);
                manos.add(regenerateCards(esFigura, nombre, palo));
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

    public boolean getPartidaFinalizado(String nombre) throws SQLException {
        boolean finalizado = false;
        try (PreparedStatement stmt = con.prepareStatement("SELECT * from player WHERE nombre = ?")) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                finalizado = rs.getBoolean("finalizado");
            }
        }
        System.out.println(finalizado);
        return finalizado;
    }
    public void updatePartidaFinalizado(String nombre,boolean partida) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("UPDATE  player SET finalizado = ? WHERE nombre = ? ")) {
            stmt.setBoolean(1, partida);
            stmt.setString(2, nombre);
            stmt.executeUpdate();
        }
    }

    public void closeConnection() throws SQLException {
        con.close();
    }
}
