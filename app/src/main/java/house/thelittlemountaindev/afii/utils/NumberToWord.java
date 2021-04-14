package house.thelittlemountaindev.afii.utils;

/**
 * Created by Charlie One on 10/6/2017.
 */

public class NumberToWord {

        private static final String[] specialNames = {
                "",
                " milles",
                " million",
                " billion",
                " trillion",
                " quadrillion",
                " quintillion"
        };

        private static final String[] tensNames = {
                "",
                " dix",
                " vingt",
                " trente",
                " quarante",
                " cinquante",
                " soixante",
                " soixante-dix",
                " quatre-vingts",
                " quatre-vingts-dix"
        };

        private static final String[] numNames = {
                "",
                " un",
                " deux",
                " trois",
                " quatre",
                " cinq",
                " six",
                " sept",
                " huite",
                " neuf",
                " dix",
                " onze",
                " douze",
                " treize",
                " quatorze",
                " quinze",
                " seize",
                " dix-sept", //watch out for dix +number
                " dix-huit", //watch out
                " dix-neuf" //watch out
        };

    private String convertLessThanOneThousand(int number) {
        String current;

        if (number % 100 < 20){
            current = numNames[number % 100];
            number /= 100;
        }
        else {
            current = numNames[number % 10];
            number /= 10;

            current = tensNames[number % 10] + current;
            number /= 10;
        }
        if (number == 0) return current;
        return numNames[number] + " cent" + current;
    }

    public String convert(int number) {

        if (number == 0) { return "zéro"; }

        String prefix = "";

        if (number < 0) {
            number = -number;
            prefix = "moins";
        }

        String current = "";
        int place = 0;

        do {
            int n = number % 1000;
            if (n != 0){
                String s = convertLessThanOneThousand(n);
                current = s + specialNames[place] + current;
            }
            place++;
            number /= 1000;
        } while (number > 0);

        return (prefix + current).trim();
    }

}
