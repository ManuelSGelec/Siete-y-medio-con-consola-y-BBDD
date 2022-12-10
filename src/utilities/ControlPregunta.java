package utilities;

import java.util.Scanner;

public class ControlPregunta {
    public static void ControlPregunta(){

    }

    public static boolean askBoolean(String question, String answerTrue, String answerFalse) {
        Scanner scan = new Scanner(System.in);
        String answer = "";
        while (!answer.equalsIgnoreCase(answerFalse) && !answer.equalsIgnoreCase(answerTrue)) {
            System.out.println(question);
            answer = scan.nextLine();
        }
        if (answer.equalsIgnoreCase(answerFalse))
            return false;
        else
            return true;

    }


}
