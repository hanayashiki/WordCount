package wordCount;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

class Token {
    private String string;

    enum Tag {
        WORD,
        SPLITTER
    };
    private Tag tag;

    Token(String string, Tag tag) {
        this.string = string;
        this.tag = tag;
    }

    public String getString() {
        return string;
    }

    public Tag getTag() {
        return tag;
    }

    public boolean isWord() {
        if (string.length() < 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!WordCount.isAlpha(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}

class Phrase implements Comparable<Phrase> {
    private LinkedList<String> words = new LinkedList<>();
    private int count;
    public static WordCount wordCount;

    public int getCount() {
        return wordCount.phraseCountResult.get(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (String string : words) {
            hash += string.hashCode();
        }
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Phrase)) {
            return false;
        } else {
            if (((Phrase) object).words.size() != this.words.size()) {
                return false;
            } else {
                Iterator<String> iter1 = ((Phrase) object).words.iterator();
                Iterator<String> iter2 = this.words.iterator();
                while (iter1.hasNext()) {
                    String str1 = iter1.next();
                    String str2 = iter2.next();
                    if (!str1.equals(str2)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }
    public void add(String string) {
        words.add(string);
    }
    public int getSize() {
        return words.size();
    }
    public Phrase generateNextBase() {
        Phrase newPhrase = new Phrase();
        Iterator<String> iter = this.words.iterator();
        iter.next();
        while (iter.hasNext()) {
            newPhrase.add(iter.next());
        }
        return newPhrase;
    }
    @Override
    public String toString() {
        return String.join(" ", words);
    }
    public int compareTo(Phrase phrase) {
        return new Integer(getCount()).compareTo(phrase.getCount());
    }
}

public class WordCount extends AsciiCount {
    public HashMap<String, Integer> wordCountResult;
    public HashMap<Phrase, Integer> phraseCountResult;
    public int lineCount = 0;
    public int asciiCount = 0;
    private int phraseLength = 1;

    private boolean noneEmptyLine = false;

    public WordCount(InputStream inputStream) {
        super(inputStream);
    }
    public WordCount(InputStream inputStream, int phraseLength) {
        super(inputStream);
        this.phraseLength = phraseLength;
        Phrase.wordCount = this;
    }
    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    public static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }
    private int read() throws IOException {
        int c = inputStreamReader.read();
        if ((char)c == '\n' || (c == -1)) {
            if (noneEmptyLine) {
                lineCount++;
                noneEmptyLine = false;
            }
        }
        if (isAscii((char)c)) {
            asciiCount++;
        }
        if ((char)c != '\t' && (char)c != ' ' && (char)c != '\n'  && (char)c != '\r' && noneEmptyLine == false) {
            // System.out.println((char)c);
            noneEmptyLine = true;
        }
        return c;
    }
    private Token getToken() throws IOException {
        StringBuilder tokenString = new StringBuilder();
        char c;
        boolean wordStart = false;
        while ((c = (char)read()) != (char)-1) {
            if (wordStart == false) {
                if (!isAlpha(c)) {
                    if (isDigit(c)) {
                        char last = c;
                        while (true) {
                            last = c;
                            c = (char)read();
                            if (c == (char)-1) {
                                return null;
                            }
                            if (!(isAlpha(last) || isDigit(last)) && isAlpha(c)) {
                                tokenString.append(Character.toLowerCase(c));
                                wordStart = true;
                                break;
                            }
                        }
                    } else {
                        continue;
                    }
                } else {
                    tokenString.append(Character.toLowerCase(c));
                    wordStart = true;
                }
            } else {
                if ((isAlpha(c) || isDigit(c))) {
                    tokenString.append(Character.toLowerCase(c));
                } else {
                    return new Token(tokenString.toString(), Token.Tag.WORD);
                }
            }
        }
        if (wordStart) {
            return new Token(tokenString.toString(), Token.Tag.WORD);
        } else {
            return null;
        }
    }
    public void countWords() throws IOException {
        HashMap<String, Integer> wordResult = new HashMap<>();
        HashMap<Phrase, Integer> phraseResult = new HashMap<>();
        Phrase phrase = new Phrase();
        Token token;
        while ((token = getToken()) != null) {
            if (token.isWord()) {
                if (!wordResult.containsKey(token.getString())) {
                    wordResult.put(token.getString(), 1);
                } else {
                    wordResult.put(token.getString(), wordResult.get(token.getString()) + 1);
                }
                phrase.add(token.getString());
                // System.out.println(phrase.getSize());
                if (phrase.getSize() == phraseLength) {
                    if (!phraseResult.containsKey(phrase)) {
                        phraseResult.put(phrase, 1);
                    } else {
                        int count = phraseResult.get(phrase);
                        phraseResult.put(phrase, count + 1);
                    }
                    phrase = phrase.generateNextBase();
                }
            } else {
                phrase = new Phrase();
            }
        }
        wordCountResult = wordResult;
        phraseCountResult = phraseResult;
    }
}

