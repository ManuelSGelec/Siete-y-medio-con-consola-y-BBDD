package model;

import dao.DAOMazoMySQL;

import java.sql.SQLException;
import java.util.ArrayList;

public class CardsDeck {
    private ArrayList<Card> cardsDeck = new ArrayList<>();
    private ArrayList<Integer> numCartes ;
    DAOMazoMySQL n = new DAOMazoMySQL();
    /**
     * Method to create the cards deck every time you want to play
     * Create Cards from two vectors (number and suit) and add them to the deck ArrayList
     */
    public CardsDeck () {

        try {
            cardsDeck = n.getCars();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Metode per repartir un nova carta aleatoria a la ma
     * @return --> Carta donada
     */
    public Card getCardFromDeck() throws SQLException {
        Card cartadonada;

        int numcarta = (int) ((Math.random()*cardsDeck.size()-1+1));

        cartadonada= cardsDeck.get(numcarta);
        cardsDeck.remove(cartadonada);
        n.updateCartasUsada(cartadonada.getCardCode());
        return cartadonada;
    }

    /**
     * Helper method that returns a random card from Deck that has not been given before
     * @return --> Card not given before
     */
    private int comprovarNumCartes (){
        boolean trobada ;
        int numcarta;
        do{
            trobada = false;
            numcarta = (int) (Math.random() * 40 + 1);
            if (numCartes.isEmpty()){
                trobada = false;
            }else {
                for (Integer x : numCartes) {
                    if (numcarta == x) {
                        System.out.println("carta"+x+" cartaRandom"+numcarta);
                        trobada = true;
                    }
                }
            }

        }while(trobada);
        numCartes.add(numcarta-1);
        return numcarta-1;
    }
}
