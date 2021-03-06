package com.agility.game.Utils;

public class PrettyLevel {
    static int last = 2000;
    static int numbers[]  = {1, 4, 5, 9, 10, 50, 100, 500, 1000 };
    static String letters[]  = { "I", "IV", "V", "IX", "X", "L", "C", "D", "M"};

        static int convertRomanToInt(String romanNumeral){
            int integerValue = 0;
            for (int i = 0; i < romanNumeral.length(); i++){
                char ch = romanNumeral.charAt( i );
                int number = letterToNumber( ch );
                if ( number == -1){
                    throw new NumberFormatException("Invalid format");
                }
                if (last<number)
                    number-=last- 1;
                integerValue += number;
                last = number;
            }
            return integerValue;
        }
        private static int letterToNumber(char letter){
            switch (letter) {
                case 'I':  return 1;
                case 'V':  return 5;
                case 'X':  return 10;
                case 'L':  return 50;
                case 'C':  return 100;
                case 'D':  return 500;
                case 'M':  return 1000;
                default:   return -1;
            }
        }
        public static String toPretty(int number){
            String romanValue = "";
            int N = number;
            while ( N > 0 ){
                for (int i = 0; i < numbers.length; i++){
                    if ( N < numbers[i] ){
                        N -= numbers[i-1];
                        romanValue += letters[i-1];
                        break;
                    }
                }
            }
            return romanValue;
        }
    }
