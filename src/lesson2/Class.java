package lesson2;

import lesson1.Stopwatch;
import lesson3.Trie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Class {
    static public Set<String> baldaSearcher(String inputName, Set<String> words) {
        Table table = parseBalda(inputName);

        Stopwatch.start();

        Trie trie = new Trie();
        words = words.stream().filter(word -> table.getSize() >= word.length()).collect(Collectors.toSet());
        for (String word: words) {
            trie.add(word);
        }
        for (Char el: table.getChars()) {
            find(el, new WordBuilder(), trie, table);
            if (trie.isEmpty()) {
                Stopwatch.stop("baldaSearcher");

                return words;
            }
        }

        Set<String> res = new HashSet<>();
        for (String word: words) {
            if (!trie.contains(word)) res.add(word);
        }

        Stopwatch.stop("baldaSearcher");

        return res;
    }

    private static Table parseBalda(String inputName) {
        Stopwatch.start();

        Map<Char, Character> values = new HashMap<>();
        Map<Char, Integer> occurrence = new HashMap<>();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputName), StandardCharsets.UTF_8))) {
            int sym = input.read();
            int x = 0;
            int y = 0;
            while (sym != -1) {
                x = 0;
                while (sym != '\n' && sym != -1) {
                    if (sym != ' ') {
                        values.put(new Char(x, y), (char) sym);
                        occurrence.merge(new Char(x, y), 1, Integer::sum);
                        ++x;
                    }
                    sym = input.read();
                }
                ++y;
                sym = input.read();
            }

            Stopwatch.stop("parseBalda");

            return new Table(values, occurrence, x, y);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Table();
    }

    private static void find(Char c, WordBuilder wb, Trie trie, Table table) {
        if (trie.isEmpty()) return;
        wb.append(c, table.getValue(c));
        if (trie.contains(wb.getValue())) {
            trie.remove(wb.getValue());
        } else if (trie.containsPrefix(wb.getValue())){
            for (Char el: table.getChildren(c)) {
                if (el != null && !wb.contains(el)) {
                    find(el, wb, trie, table);
                }
            }
        }
        wb.removeLast();
    }

    private static class Table {
        private int length;
        private int height;
        private int size;
        private Map<Char, Character> values;
        private Map<Char, Integer> occurrence;
        private static Char[] directions = new Char[] {
                new Char(0, 1), new Char(1, 0), new Char(0, -1), new Char(-1, 0)
        };

        private Table() {}

        Table(Map<Char, Character> values, Map<Char, Integer> occurrence, int length, int height) {
            this.length = length;
            this.height = height;
            this.values = values;
            this.size = length * height;
        }

        Set<Char> getChars() {
            return values.keySet();
        }

        Character getValue(Char c) {
            return values.get(c);
        }

        int getSize() {
            return size;
        }

        Char[] getChildren(Char c) {
            Char[] res = new Char[4];
            for (int i = 0; i < directions.length; i++) {
                Char t = c.plus(directions[i]);
                res[i] = (t.x < length && t.y < height && t.x >= 0 && t.y >= 0) ? t : null;
            }
            return res;
        }
    }

    private static class Char {
        int x;
        int y;

        Char(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Char plus(Char o) {
            return new Char(this.x + o.x, this.y + o.y);
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Char aChar = (Char) o;
            return x == aChar.x &&
                    y == aChar.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class WordBuilder {
        List<Char> chars = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        void append(Char c, Character character) {
            chars.add(c);
            value.append(character);
        }

        void removeLast() {
            chars.remove(chars.size() - 1);
            value.deleteCharAt(value.length() - 1);
        }

        boolean contains(Char c) {
            return chars.contains(c);
        }

        String getValue() {
            return value.toString();
        }
    }
}
