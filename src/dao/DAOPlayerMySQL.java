package dao;

import model.Player;
import utilities.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

public class DAOPlayerMySQL implements IdaoPlayerMySQL {
    private Connection con = null;

    public DAOPlayerMySQL() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            con = ConnectionDB.getInstance();
        } catch (SQLException throwables) {
            System.out.println("error de conexion com la BBDD");
            throwables.printStackTrace();
        }
    }

    public void addPlayer(String nombre) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("insert into player values (?,null,null,null,1)")) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
        }
    }

    @Override
    public void updatePlayer(Player jugador) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("update player set pratidas_jugadas = ? , ganadas  = ?, perdidas = ?  WHERE nombre = ?")) {
            stmt.setInt(1, jugador.getPlayedGames());
            stmt.setInt(2, jugador.getWonGames());
            stmt.setInt(3, jugador.getLostGames());
            stmt.setString(4, jugador.getName());
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update player record");
            }
        }
    }

    @Override
    public ArrayList<Player> getPlayers() throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM player ")) {
            ArrayList<Player> players = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
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

    @Override
    public boolean getPartidaFinalizado(String nombre) throws SQLException {
        boolean finalizado = false;
        try (PreparedStatement stmt = con.prepareStatement("SELECT * from player WHERE nombre = ?")) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                finalizado = rs.getBoolean("finalizado");
            }
        }
        return finalizado;
    }

    @Override
    public void updatePartidaFinalizado(String nombre, boolean partida) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("UPDATE  player SET finalizado = ? WHERE nombre = ? ")) {
            stmt.setBoolean(1, partida);
            stmt.setString(2, nombre);
            stmt.executeUpdate();
        }
    }

    public void getHallOfFame() {
        int contador = 1;
        System.out.println("*******************Hall Of Fame*******************\n" +
                "************Top 3 of the best players*************");
        try (PreparedStatement stmt = con.prepareStatement("SELECT (p.ganadas - p.perdidas) AS total, p.* FROM player AS p ORDER BY total DESC LIMIT 3 ")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String nombre = rs.getString("nombre");
                int ganadas = rs.getInt("ganadas");
                int perdidas = rs.getInt("perdidas");
                System.out.println("         --- position "+contador+"---");
                System.out.println(String.format("Player: %s \nPunctuation: %s wins / %s loses\n",nombre, ganadas, perdidas));

                contador++;
            }
            System.out.println("**************************************************");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


