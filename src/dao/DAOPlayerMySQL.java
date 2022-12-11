package dao;

import model.Player;
import utilities.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class DAOPlayerMySQL {
    Connection con = null;


    public DAOPlayerMySQL() {
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


}


