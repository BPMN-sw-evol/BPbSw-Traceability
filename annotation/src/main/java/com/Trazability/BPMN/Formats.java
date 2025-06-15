package com.Trazability.BPMN;

public class Formats {
    public static String formatElementName(String name) {
        StringBuilder formattedNameBuilder = new StringBuilder();
        boolean newWord = true;
        int lettersCount = 0;

        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                if (newWord) {
                    formattedNameBuilder.append(Character.toUpperCase(c));
                    newWord = false;
                    lettersCount++;
                } else if (lettersCount < 3) {
                    formattedNameBuilder.append(Character.toLowerCase(c));
                    lettersCount++;
                }
            } else if (c == ' ') {
                newWord = true;
                lettersCount = 0;
            }
        }

        return formattedNameBuilder.toString();
    }

    public static String formatLaneName(String name) {
        StringBuilder formattedNameBuilder = new StringBuilder();
        boolean newWord = true;

        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                if (newWord) {
                    formattedNameBuilder.append(Character.toUpperCase(c));
                    newWord = false;
                } else {
                    formattedNameBuilder.append(Character.toLowerCase(c));
                }
            } else if (Character.isWhitespace(c)) {
                newWord = true;
                formattedNameBuilder.append(c);
            }
        }

        return formattedNameBuilder.toString().replaceAll("\\s+", "");
    }
}
