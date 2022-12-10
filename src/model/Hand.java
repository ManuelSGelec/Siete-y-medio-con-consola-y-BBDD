package model;

import dao.DAOMySQL;

import java.sql.SQLException;
import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards;
    private float handValue;
    DAOMySQL dao=new DAOMySQL();

    /**
     * Constructor per crear un ArrayList de Cartes (Ma)
     */
    public Hand(){

        cards = new ArrayList<Card>();
    }

    /**
     * Getter per saber el valor de la Ma
     * @return --> Valor Ma
     */
    public float getValue() {
        return handValue;
    }

    /**
     * Metode per afegir carta a la ma i calcular de nou el valor de la ma
     * @param carta --> Carta donada
     */
    public void addCard(Card carta,boolean es_banca,String nombre_jugador) throws SQLException {
        cards.add(carta);
        calculateHandValue(es_banca,nombre_jugador);
    }

    public void addCards(ArrayList<Card> manos,boolean es_banca,String nombre_jugador) throws SQLException {
        this.cards=manos;
        calculateHandValue(es_banca,nombre_jugador);
    }

    /**
     * Metode helpper per calcular el valor de la ma
     */
    private void calculateHandValue (boolean es_banca,String nombre_jugador) throws SQLException {
        float x ;
        x=dao.sumaMano (es_banca,nombre_jugador);
        this.handValue =x;
    }

    /**
     * Metode ToString per veure la ma i el valor de la propia
     * @return --> Ma sencera i el seu valor
     */
    @Override
    public String toString() {
        return " " + cards +
                "\n  Valor de la Ma: " + handValue ;

    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

}
