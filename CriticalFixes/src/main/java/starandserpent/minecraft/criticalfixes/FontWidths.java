package starandserpent.minecraft.criticalfixes;

import java.util.HashMap;

public class FontWidths {

    private static final HashMap<Character, Integer> charWidths = new HashMap<>() {{
        put(' ', 4);
//        put(' ', 2);
        put('!', 2);
        put('"', 5);
        put('#', 10);
        put('$', 10);
        put('%', 10);
        put('&', 10);
        put('(', 6);
        put(')', 6);
        put('*', 6);
        put('+', 10);
        put(',', 4);
        put('-', 10);
        put('.', 2);
        put('/', 10);
        put('0', 10);
        put('1', 10);
        put('2', 10);
        put('3', 10);
        put('4', 10);
        put('5', 10);
        put('6', 10);
        put('7', 10);
        put('8', 10);
        put('9', 10);
        put(':', 2);
        put(';', 2);
        put('<', 8);
        put('=', 10);
        put('>', 8);
        put('?', 10);
        put('@', 12);
        put('A', 10);
        put('B', 10);
        put('C', 10);
        put('D', 10);
        put('E', 10);
        put('F', 10);
        put('G', 10);
        put('H', 10);
        put('I', 6);
        put('J', 10);
        put('K', 10);
        put('L', 10);
        put('M', 10);
        put('N', 10);
        put('O', 10);
        put('P', 10);
        put('Q', 10);
        put('R', 10);
        put('S', 10);
        put('T', 10);
        put('U', 10);
        put('V', 10);
        put('W', 10);
        put('X', 10);
        put('Y', 10);
        put('Z', 10);

        put('[', 6);
        put('\\', 6);
        put(']', 6);
        put('^', 6);
        put('_', 10);
        put('\'', 10);
//        put('a', 6);
        put('a', 10);
//        put('b', 6);
        put('b', 10);
//        put('c', 6);
        put('c', 10);
//        put('d', 6);
        put('d', 10);
//        put('e', 6);
        put('e', 10);
//        put('f', 5);
        put('f', 8);
//        put('g', 6);
        put('g', 10);
//        put('h', 6);
        put('h', 10);
        put('i', 2);
//        put('j', 6);
        put('j', 10);
//        put('k', 5);
        put('k', 8);
//        put('l', 3);
        put('l', 4);
//        put('m', 6);
        put('m', 10);
//        put('n', 6);
        put('n', 10);
//        put('o', 6);
        put('o', 10);
//        put('p', 6);
        put('p', 10);
//        put('q', 6);
        put('q', 10);
//        put('r', 6);
        put('r', 10);
//        put('s', 6);
        put('s', 10);

//        put('t', 4);
        put('t', 6);

//        put('u', 6);
        put('u', 10);
//        put('v', 6);
        put('v', 10);
//        put('w', 6);
        put('w', 10);
//        put('x', 6);
        put('x', 10);
//        put('y', 6);
        put('y', 10);
//        put('z', 6);
        put('z', 10);

        put('{', 6);
        put('|', 2);
        put('}', 6);
        put('~', 12);
        put('⌂', 14);
        put('Ç', 10);
        put('ü', 10);
        put('é', 10);
        put('â', 10);
        put('ä', 10);
        put('à', 10);
        put('å', 10);
        put('ç', 10);
        put('ê', 10);
        put('ë', 10);
        put('è', 10);
        put('ï', 6);
        put('î', 6);
        put('ì', 4);
        put('Ä', 10);
        put('Å', 10);
        put('É', 10);
        put('æ', 10);
        put('Æ', 10);
        put('ô', 10);
        put('ö', 10);
        put('ò', 10);
        put('û', 10);
        put('ù', 10);
        put('ÿ', 10);
        put('Ö', 10);
        put('Ü', 10);
        put('ø', 10);
        put('£', 10);
        put('Ø', 10);
        put('×', 10);
        put('ƒ', 10);
        put('á', 10);
        put('í', 4);
        put('ó', 10);
        put('ú', 10);
        put('ñ', 10);
        put('Ñ', 10);
        put('ª', 10);
        put('º', 10);
        put('¿', 10);
        put('®', 14);
        put('¬', 10);
        put('½', 14);
        put('¼', 14);
        put('¡', 2);
        put('«', 12);
        put('»', 12);
    }};

    public static int getWidth(char character) {
        return charWidths.getOrDefault(character, 6);
    }

    public static int calculateWordWidth(String word) {
        int width = 0;
        for (char c : word.toCharArray()) {
            width += getWidth(c);
        }
        return width;
    }

}
