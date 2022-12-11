package model;

import dao.DAOManoMySQL;
import dao.DAOMazoMySQL;
import dao.DAOPlayerMySQL;
import utilities.ControlPregunta;

import java.sql.SQLException;
import java.util.ArrayList;

public class Game {

    private CardsDeck cardsDeck;
    private Hand handPlayer;
    private Hand handBanker;
    private Player player;
    private boolean gameOver = false;
    private String loser = "";
    private String winner = "";
    private DAOMazoMySQL dao = new DAOMazoMySQL();
    private DAOPlayerMySQL daoPlayerMySQL=new DAOPlayerMySQL();
    private DAOManoMySQL daoManoMySQL= new DAOManoMySQL();
    public Game(Player player) throws SQLException {
        this.player = player;
        cardsDeck = new CardsDeck();
        handPlayer = new Hand();
        handBanker = new Hand();

        if (checkPartidafinalizada()) {

            ArrayList<Card> manoBanco = daoManoMySQL.getManosCars(player.getName(), true);
            ArrayList<Card> manoJugador = daoManoMySQL.getManosCars(player.getName(), false);
            handPlayer.addCards(manoJugador, false, player.getName());
            handBanker.addCards(manoBanco, true, player.getName());
            manoJugador.forEach(card -> {
                try {
                    dao.updateCartasUsada(card.getCardCode());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            manoBanco.forEach(card -> {
                try {
                    dao.updateCartasUsada(card.getCardCode());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        } else {
            dao.resetMazo();
            daoManoMySQL.resetMano(player.getName());

            Card card = cardsDeck.getCardFromDeck();
            dao.addCartaMano(player.getName(), dao.getIDCarta(card.getCardCode()), false);

            handPlayer.addCard(card, false, player.getName());

            card = cardsDeck.getCardFromDeck();
            dao.addCartaMano(player.getName(), dao.getIDCarta(card.getCardCode()), true);

            handBanker.addCard(card, true, player.getName());
        }
    }


    public Card playerTurn() throws SQLException {
        Card card = cardsDeck.getCardFromDeck();
        dao.addCartaMano(player.getName(), dao.getIDCarta(card.getCardCode()), false);
        handPlayer.addCard(card, false, player.getName());
        if (handPlayer.getValue() > 7.5f) {
            this.gameOver = true;

            checkWinner();
        }
        return card;
    }

    public void bankerTurn() throws SQLException {
        while (!this.getGameOver()) {
            if (handBanker.getValue() >= handPlayer.getValue()) {
                this.gameOver = true;
            } else {
                Card card = cardsDeck.getCardFromDeck();
                dao.addCartaMano(player.getName(), dao.getIDCarta(card.getCardCode()), true);
                handBanker.addCard(card, true, player.getName());
            }
        }
        checkWinner();
    }

    private boolean checkPartidafinalizada() throws SQLException {
        boolean answer = false;

        if (!daoPlayerMySQL.getPartidaFinalizado(player.getName())) {
            return preguntaRecuperar();
        }
        return answer;
    }

    private boolean preguntaRecuperar() {
        return ControlPregunta.askBoolean("¿ Wants to recover the game ? [Y]yes [N]Not", "Y", "N");
    }



    private void checkWinner() throws SQLException {
        if (this.handPlayer.getValue() > 7.5) {
            this.winner = "Banker";
            this.loser = player.getName();
            player.lostGame();
            return;
        }
        if (this.handBanker.getValue() > 7.5) {
            this.loser = "Banker";
            this.winner = player.getName();
            player.gameWon();
            return;
        }
        if (this.handBanker.getValue() >= this.handPlayer.getValue()) {
            this.winner = "Banker";
            this.loser = player.getName();
            player.lostGame();
        } else {
            this.loser = "Banker";
            this.winner = player.getName();
            player.gameWon();
        }
        daoPlayerMySQL.updatePlayer(player);
    }


    public String getPlayerName() {
        return this.player.getName();
    }

    public String getLoser() {
        return this.loser;
    }

    public Hand getPlayerHand() {
        return this.handPlayer;
    }

    public Hand getBankerHand() {
        return this.handBanker;
    }

    public boolean getGameOver() {
        return this.gameOver;

    }

    public String getWinner() {
        return this.winner;
    }

}
