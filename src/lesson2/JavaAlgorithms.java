package lesson2;

import kotlin.Pair;
import lesson1.IntegerCache;
import lesson1.Stopwatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static lesson1.JavaTasks.parseIntLines;

@SuppressWarnings("unused")
public class JavaAlgorithms {
    private static final int BUY_AND_SELL_CONST = 17;
    /**
     * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
     * Простая
     *
     * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
     * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
     *
     * 201
     * 196
     * 190
     * 198
     * 187
     * 194
     * 193
     * 185
     *
     * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
     * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть позже первого.
     * Вернуть пару из двух моментов.
     * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
     * Например, для приведённого выше файла результат должен быть Pair(3, 4)
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public Pair<Integer, Integer> optimizeBuyAndSell(String inputName) {
        Stopwatch.start();

        int[] array = Arrays.stream(parseIntLines(inputName)).mapToInt(i -> i).toArray();
        int[] res = maxProfit(array);

        Stopwatch.stop("optimizeBuyAndSell");

        return new Pair<>(res[0] + 1, res[1] + 1);
    }

    private static int[] ar;
    private static int[] price;

    private static int[] maxProfit(int[] prices) {
        price = prices;
        int n = prices.length;
        ar = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            ar[i] = prices[i + 1] - prices[i];
        }
        return maxSubArray(0, ar.length);
    }

    private static int[] maxSubArray(int l, int r) {
        int con = r - l;
        if (con < BUY_AND_SELL_CONST) {
            int[] max = new int[3];
            for (int i = l; i <= r; i++) {
                for (int j = i + 1; j <= r; j++) {
                    if (price[j] - price[i] > max[2]) {
                        max[2] = price[j] - price[i];
                        max[0] = i;
                        max[1] = j;
                    }
                }
            }
            return max;
        }
        if (l >= r - 1) return new int[] { l, r, ar[l] };
        int m = (l + r) >>> 1;
        int[] left = maxSubArray(l, m);
        int[] right = maxSubArray(m, r);
        int[] mid = maxMidArray(l, r, m);
        int[] res;
        if (left[2] >= right[2] && left[2] >= mid[2]) {
            res = left;
        } else if (right[2] >= left[2] && right[2] >= mid[2]) {
            res = right;
        } else {
            res = mid;
        }
        return res;
    }

    private static int[] maxMidArray(int l, int r, int m) {
        int leftSum = 0;
        int sum = 0;
        int leftIndex = m;
        for (int i = m; i >= l; i--) {
            sum += ar[i];
            if (sum > leftSum) {
                leftSum = sum;
                leftIndex = i;
            }
        }
        sum = 0;
        int rightSum = 0;
        int rightIndex = m + 1;
        for (int i = m + 1; i < r; i++) {
            sum += ar[i];
            if (sum > rightSum) {
                rightSum = sum;
                rightIndex = i;
            }
        }
        return new int[] { leftIndex, rightIndex + 1, leftSum + rightSum };
    }
    /*
      Потребление памяти: O(n), чтобы хранить все числа в массиве.
      Асимптотическая сложность: на каждом шаге массив разбивается на правый и левый, и для каждой половины рекурсивно
      вызывается тот же алгоритм + уходит линейное время на то, чтобы найти максимальный подмассив, проходящий
      через центр. Когда размер массива достигает значения < определённой константы, рекрусивные вызовы прекращаются
      и остаток массива обрабатывается перебором за O(k ^ 2), а значит время работы:
      T(n) = { O(k ^ 2), при n < k
             { 2 * T(n / 2) + O(n)
      Найдём асимптотическую сложность: Нахождение максимального подмассива среди n / k массивов перебором займёт
      n / k * O(k ^ 2) = O(n * k). Построим дерево
                                                    cn
                                                   /  \
                                                cn/2 cn/2
                                                .........
                                              |   |   |   |
                                             k^2 k^2 k^2 k^2
      Высота дерева ceil(lg(n / k)), на каждом уровне, кроме последнего выполняется cn операций =>
      O(n * k + n * lg(n / k))
     */

    /**
     * Задача Иосифа Флафия.
     * Простая
     *
     * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
     *
     * 1 2 3
     * 8   4
     * 7 6 5
     *
     * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
     * Человек, на котором остановился счёт, выбывает.
     *
     * 1 2 3
     * 8   4
     * 7 6 х
     *
     * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
     * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
     *
     * 1 х 3
     * 8   4
     * 7 6 Х
     *
     * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
     *
     * 1 Х 3
     * х   4
     * 7 6 Х
     *
     * 1 Х 3
     * Х   4
     * х 6 Х
     *
     * х Х 3
     * Х   4
     * Х 6 Х
     *
     * Х Х 3
     * Х   х
     * Х 6 Х
     *
     * Х Х 3
     * Х   Х
     * Х х Х
     *
     * Общий комментарий: решение из Википедии для этой задачи принимается,
     * но приветствуется попытка решить её самостоятельно.
     */
    static public int josephTask(int menNumber, int choiceInterval) {
        if (menNumber == 1) return 1;

        LinkedList<Integer> circle = new LinkedList<>();
        for (int i = 1; i <= menNumber; i++) {
            circle.add(IntegerCache.getFromCache(i));
        }
        Iterator it = circle.listIterator();

        while (circle.size() != 1) {
            for (int i = 0; i < choiceInterval; i++) {
                if (!it.hasNext()) it = circle.listIterator();
                it.next();
            }
            it.remove();
        }

        return circle.getFirst();
    }
    //todo подумать

    /**
     * Наибольшая общая подстрока.
     * Средняя
     *
     * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
     * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
     * Если общих подстрок нет, вернуть пустую строку.
     * При сравнении подстрок, регистр символов *имеет* значение.
     * Если имеется несколько самых длинных общих подстрок одной длины,
     * вернуть ту из них, которая встречается раньше в строке first.
     */
    static public String longestCommonSubstring(String first, String second) {
        String a = first.length() > second.length() ? first : second;
        String b = first.length() <= second.length() ? first : second;
        int[] prev = new int[b.length()];
        int[] current = new int[b.length()];
        int maxLength = 0;
        int begin = 0;
        int end = 0;

        for (int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {
                if (a.charAt(i) != b.charAt(j)) {
                    current[j] = 0;
                } else {
                    current[j] = (j > 0 ? prev[j - 1] : 0) + 1;
                }
                if (current[j] > maxLength) {
                    maxLength = current[j];
                    begin = j - maxLength + 1;
                    end = j + 1;
                }
            }
            int[] t = prev;
            prev = current;
            current = t;
        }

        return b.substring(begin, end);
    }
    /*
      память: два массива размером длины наименьшей строки + 1 - O(длина наименьшей строки),
      время работы: цикл по строке а, вложенный цикл по строке b - O(длина a * длина b)
     */
    /**
     * Число простых чисел в интервале
     * Простая
     *
     * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
     * Если limit <= 1, вернуть результат 0.
     *
     * Справка: простым считается число, которое делится нацело только на 1 и на себя.
     * Единица простым числом не считается.
     */
    static public int calcPrimesNumber(int limit) {
        if (limit < 2) return 0;
        int[] nums = eratosthenesSieve(limit);
        return Arrays.stream(nums).sum();
    }

    private static int[] eratosthenesSieve(int limit) {
        int[] nums = new int[(int) Math.ceil(limit / 2.0)];

        for (int i = 0; i < (int) Math.ceil(limit / 2.0); i++) {
            nums[i] = 1;
        }

        for (int k = 3; k * k <= limit; k += 2) {
            if (nums[k / 2] == 1) {
                for (int l = k * k; l <= limit; l += k) {
                    if ((l & 1) == 1) nums[l >> 1] = 0;
                }
            }
        }
        return nums;
    }
    /*
       память: храним все нечётные числа в массиве, O(n)
       время: сначала делаем n/3 операций, потом n/5 и т.д. n/3 + n/5 + n/7 + ... + 1 = n * lg(lg(n))
     */

    /**
     * Балда
     * Сложная
     *
     * В файле с именем inputName задана матрица из букв в следующем формате
     * (отдельные буквы в ряду разделены пробелами):
     *
     * И Т Ы Н
     * К Р А Н
     * А К В А
     *
     * В аргументе words содержится множество слов для поиска, например,
     * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
     *
     * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
     * и вернуть множество найденных слов. В данном случае:
     * ТРАВА, КРАН, АКВА, НАРТЫ
     *
     * И т Ы Н     И т ы Н
     * К р а Н     К р а н
     * А К в а     А К В А
     *
     * Все слова и буквы -- русские или английские, прописные.
     * В файле буквы разделены пробелами, строки -- переносами строк.
     * Остальные символы ни в файле, ни в словах не допускаются.
     */
    static public Set<String> baldaSearcher(String inputName, Set<String> words) {

        Table table = parseBalda(inputName);

        Stopwatch.start();

        words = words.stream().filter(word -> !wordHasInvalidChars(word, table)).collect(Collectors.toSet());

        Set<String> rightWords = new HashSet<>();

        //ищу в слове самую редкую букву в таблице, разбиваю строку по ней на две, переворачиваю первую и ищу по ним
        //в таблице. Самая редкая буква является началом обеих подстрок
        for (String str: words) {
            int minOccurrence = Integer.MAX_VALUE;
            int startCharacterIndex = 0;
            Character startCharacter = ' ';

            for (int i = 0; i < str.length(); i++) {
                Character character = str.charAt(i);
                int characterOccurrence = table.getOccurrence(character);

                if (characterOccurrence < minOccurrence) {
                    minOccurrence = table.getOccurrence(str.charAt(i));
                    startCharacterIndex = i;
                    startCharacter = character;
                }
            }

            String secondHalf = str.substring(startCharacterIndex);

            for (Cell cell: table.getCells(startCharacter)) {
                WordBuilder res = find(cell, new WordBuilder(), secondHalf, table);
                if (res != null) {
                    if (startCharacterIndex == 0) {
                        rightWords.add(str);
                        break;
                    } else {
                        String firstHalf = new StringBuilder(str.substring(0, startCharacterIndex + 1))
                                .reverse().toString();
                        if (findFirst(cell, res, firstHalf, secondHalf, table)) {
                            rightWords.add(str);
                            break;
                        }

                    }
                }
            }
        }

        Stopwatch.stop("baldaSearcher");

        return rightWords;
    }

    private static boolean wordHasInvalidChars(String word, Table tb) {
        Map<Character, Integer> wordOccurrence = new HashMap<>();
        for (Character ch: word.toCharArray()) {
            wordOccurrence.merge(ch, 1, Integer::sum);
        }
        for (Character ch: wordOccurrence.keySet()) {
            if (wordOccurrence.get(ch) > tb.getOccurrence(ch)) return true;
        }
        return false;
    }

    private static Table parseBalda(String inputName) {
        Stopwatch.start();

        Map<Cell, Character> values = new HashMap<>();
        Map<Character, Integer> occurrence = new HashMap<>();
        Map<Character, List<Cell>> findCellMap = new HashMap<>();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputName), StandardCharsets.UTF_8))) {
            int sym = input.read();
            int x = 0;
            int y = 0;
            while (sym != -1) {
                x = 0;
                while (sym != '\n' && sym != -1) {
                    if (sym != ' ') {
                        Character charsym = (char) sym;
                        Cell newCell = new Cell(x, y);
                        findCellMap.putIfAbsent(charsym, new ArrayList<>());
                        findCellMap.get(charsym).add(newCell);
                        values.put(newCell, charsym);
                        occurrence.merge(charsym, 1, Integer::sum);
                        ++x;
                    }
                    sym = input.read();
                }
                ++y;
                sym = input.read();
            }

            Stopwatch.stop("parseBalda");

            return new Table(values, occurrence, findCellMap, x, y);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Table();
    }

    //следит, чтобы в процессе алгоритма не создавались одни и те же подстроки. Для этого для каждой посещённой
    //клетки хранит массив клеток (подстроку), из которых мы к ней приходили
    private static class Watcher {
        Map<Cell, List<String>> map = new HashMap<>();

        void put(Cell c, String str) {
            map.putIfAbsent(c, new ArrayList<>());
            map.get(c).add(str);
        }

        boolean contains(Cell c, String str) {
            map.putIfAbsent(c, new ArrayList<>());
            return map.get(c).contains(str);
        }
    }

    //рекурсивно ищет строчку в таблице, начиная с заданной координаты
    private static WordBuilder find(Cell c, WordBuilder wb, String str, Table table) {
        return find0(c, wb, str, 0, table, new Watcher());
    }
    private static WordBuilder find0(Cell c, WordBuilder wb, String str, Integer index, Table table, Watcher w) {
        if (w.contains(c, wb.getValue())) {
            return null;
        }
        w.put(c, wb.getValue());

        wb.append(c, table.getValue(c));
        if (wb.getValue().equals(str)) {
            return wb;
        } else {
            for (Cell el: table.getNeighbours(c)) {
                if (!wb.contains(el) && table.getValue(el).equals(str.charAt(index + 1))) {
                    WordBuilder res = find0(el, wb, str, index + 1, table, w);
                    if (res != null) return res;
                }
            }
        }
        wb.removeLast();

        return null;
    }

    //поиск для перевёрнутой первой подстроки
    private static boolean findFirst(Cell c, WordBuilder wb, String str, String suffix, Table table) {
        return findFirst0(c, wb, str, suffix, 0, table, new Watcher());
    }

    private static boolean findFirst0(Cell c, WordBuilder wb, String str, String secondString,
                                      Integer index, Table table, Watcher w) {
        if (w.contains(c, wb.getValue())) {
            return false;
        }
        w.put(c, wb.getValue());

        wb.append(c, table.getValue(c));
        if (wb.getValue().equals(secondString + str)) {
            return true;
        } else if (index < str.length() - 1) {
            for (Cell el : table.getNeighbours(c)) {
                if (!wb.contains(el) && table.getValue(el).equals(str.charAt(index + 1))) {
                    if (findFirst0(el, wb, str, secondString, index + 1, table, w)) return true;
                }
            }
        }
        wb.removeLast();

        return false;
    }

    //координаты в таблице
    private static class Cell {
        int x;
        int y;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Cell plus(Cell o) {
            return new Cell(this.x + o.x, this.y + o.y);
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
            Cell aCell = (Cell) o;
            return x == aCell.x &&
                    y == aCell.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    //таблица, связывает координаты и значения, буквы и координаты, хранит встречаемость букв.
    private static class Table {
        private int length;
        private int height;
        private int size;
        private Map<Cell, Character> values;
        private Map<Character, Integer> occurrence;
        private Map<Character, List<Cell>> findCellMap;
        private static Cell[] directions = new Cell[] {
                new Cell(0, 1), new Cell(1, 0), new Cell(0, -1), new Cell(-1, 0)
        };

        private Table() {}

        Table(Map<Cell, Character> values, Map<Character, Integer> occurrence,
              Map<Character, List<Cell>> findCellMap, int length, int height) {
            this.length = length;
            this.height = height;
            this.values = values;
            this.size = length * height;
            this.occurrence = occurrence;
            this.findCellMap = findCellMap;
        }

        public int getLength() {
            return length;
        }

        public int getHeight() {
            return height;
        }

        Set<Cell> getCells() {
            return values.keySet();
        }

        Character getValue(Cell c) {
            return values.get(c);
        }

        int getSize() {
            return size;
        }

        //возвращает всех валидных в балде соседей
        List<Cell> getNeighbours(Cell c) {
            List<Cell> res = new ArrayList<>();
            for (Cell direction : directions) {
                Cell t = c.plus(direction);
                if (t.x < length && t.y < height && t.x >= 0 && t.y >= 0) {
                    res.add(t);
                }
            }
            return res;
        }

        int getOccurrence(Character ch) {
            return occurrence.get(ch) != null ? occurrence.get(ch) : 0;
        }

        List<Cell> getCells(Character ch) {
            return findCellMap.get(ch);
        }

        boolean hasCharacter(Character ch) {
            return occurrence.keySet().contains(ch);
        }
    }

    //замена StringBuilder, нужен чтобы не добавлять в строку буквы с тех клеток, где мы уже были
    private static class WordBuilder {
        List<Cell> cells = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        void append(Cell c, Character character) {
            cells.add(c);
            value.append(character);
        }

        void removeLast() {
            cells.remove(cells.size() - 1);
            value.deleteCharAt(value.length() - 1);
        }

        boolean contains(Cell c) {
            return cells.contains(c);
        }

        String getValue() {
            return value.toString();
        }
    }
    /*
    Худший случай: все клетки одинаковые, все клетки подходят, кроме последней.
    время в худшем случае: пусть клетки, которые начинаются с той же буквы, как и буква в слове, реже всего
    встречающаяся в таблице, называются подозрительными. Очевидно что таких клеток не больше чем
    кол-во символов / алфавит таблицы.
    Теперь посчитаем, сколько операций нам нужно сделать для каждой подозрительной клетки: мы рекурсивно переходим к
    каждому из её соседей, при этом наша задача уменьшается на одну букву. У каждой клетки после первой по три соседа
    что даёт нам 3T(n - 1) + O(1) = O(3 ^ n). А значит время всей функции
    O(число слов * кол-во символов / алфавит таблицы * 3 ^ длина слова). НО, при каждом ходе появляются клетки-соседи
    общие для соседей (не знаю как объяснить, попробуйте нарисовать), а значит множитель 3 ^ n должен иметь константу
    меньше. Насколько меньше я не могу оценить.
    память: храню таблицу O(n). Слежу, чтобы не создавались одинаковые подстроки: в худшем случае вношу в map каждую
    клетку в зоне досягаемости и всевозможные пути к ней. O(длина строки ^ 2 * число путей к клетке). Число путей
    растёт по экспоненте.
     */

    //todo дэлит
    public static void main(String[] args) {
//        try (BufferedWriter wr = new BufferedWriter(new FileWriter("input/all_nouns2.txt"))) {
//            try (BufferedReader br = new BufferedReader(new FileReader("input/all_nouns.txt"))) {
//                String line = br.readLine();
//                while (line != null) {
//                    wr.write(line.toUpperCase() + "\n");
//                    line = br.readLine();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //p();
    }

//    public static void p() {
//        String s = "ОСТАНОВ, РАССУДИТЕЛЬНОСТЬ, УСТОЙ, ОСТРОГ, ИНАЯ, АИ, ФОРС, ЛАПА, СТО, ГУЛЯ, АР, РАСА, АС, СВАТ, СТАВ, АУ, ПОЛ, СТАЖ, СИ, ВОЛОС, МИГ, САНИ, ЛЖА, ТУЯ, СТАН, СИВАЯ, СУД, СУ, ПОЛИС, РАК, ШАР, МИР, ПОТ, СВАН, ТА, МИМ, СИЛЬ, ЛИСТВИЕ, АНОНС, СИМА, ТО, ПРОГРАММИРОВАНИЕ, ШТЕЙН, АНИС, ИСК, ОНА, ГИД, ГРАВИТАЦИЯ, МИЛОВАНИЕ, ОТАРА, ЯСТВИЕ, ЕЛЬ, ТИШЬ, МАМОНТ, УД, ГИТ, УЖ, ЛИНО, ПОНИ, ИВА, СЫРТ, ГА, УС, УТ, ВЫЛЕТ, ПОНОС, ПАР, ПРЯ, АГА, ПАЛ, АУЛ, ЛИС, ФА, РАНА, ГОРА, ЯРД, ФИ, МИРОВАЯ, БЕЛ, ПСИ, КТИТОР, ПАТ, ПРОСОС, СОЛИСТ, ПАС, СОРОК, ОЛИВА, ИГО, ЭТИМОН, ГАТЬ, ОРС, ХИ, ХИНА, ВИС, СИВОСТЬ, НОМ, СЕЙ, НОК, СЕТ, ОСТЬ, ОСА, КВАС, ЯТЬ, ТЯТЯ, ЕР, КИТ, ТОСТ, СЕТЬ, НОС, ОРТ, ФОКС, ОСТ, ПРОК, СИСТР, ТИК, ХОР, СОЛИЛО, ТИР, ТИС, АГАР, ВЫРОСТ, АГАТ, СОРОМ, НОРА, МИОМА, ТОРИ, ТВИН, МЕРА, ТОРН, ЛЬЕ, МОСТ, МОРОК, СИЕ, АСТРА, МОР, ТИГР, БИТ, БИС, ТОРГ, ЛИВАН, ЦУГ, ФОТОН, МОТ, ФОК, ФОН, МАЦА, ГНУ, ГАРТ, НИВА, ВЫНОС, ФОТ, СИР, ВЫЯ, ШУ, ПРОПС, ЛОВ, ИЛ, ШПОН, ТЛЯ, ТРИАС, ДРАГА, ИР, МОПС, НРАВ, ШПРОТ, ВИСТ, МАГ, ОРОК, ЧУШЬ, ШАРМ, АИР, НАСТ, НОНА, ГРОТ, ЭСТ, МАТ, СЫН, ФРЯ, МАР, СЫР, ФАТ, РИС, ЗОНТ, ОСОТ, МОРОКА, ОСОС, ПОРЫВ, МАК, СЫЧ, СОРТ, АПЕЛЬСИН, ВОНА, ПРОСО, ЭТО, ВЫГАР, НОМА, ОХИ, ВОЛ, ТОРИТ, МАРГО, САГО, ТОЙ, ГАМ, ТАРИ, КОН, КОМ, ГРАММ, ВОР, РОТА, ЛИ, ДВА, ПОСОЛ, ВИРА, МРАК, СЭР, ЧИХ, ТИГРА, МИСА, РЕПА, ТАРА, РОСТ, ТОМ, ТОН, РОСТР, ТРОС, ВТОРА, ТОП, ЛЯ, МА, ИВАСИ, РОПОТ, ТОТ, ЛОРИ, ЧИЙ, БЕЛЬ, СОНЬ, МИ, РОСА, АГАМИ, ТРОГ, ИНЕЙ, ДРОГА, ЛАВР, СОМ, УТОР, СПОР, ЖИР, АВИА, ВАЛ, ЙОТ, АРГО, МИРО, ЖИТО, ВАЖ, НИ, ПОРОК, УТЯ, ОМАР, НО, ВАРЯГ, ГУЛ, ЛУГ, ТАУ, ЮГ, ВАШ, ЛУБ, СПОРОК, ХОРТ, ЮЗ, ЛОНО, ЖИРО, КОРМ, ВАР, ПОСТ, СОН, СОЛО, УСТОЙЧИВОСТЬ, СОР, ЮС, ЮТ, СОТ, СИЛОН, ВОИН, ПОМОСТ, АНТ, РИГА, СИЛОС, ПРОТОН, СОЛЬ, ОМ, ПРИВАР, КСИ, ОН, ЛИСА, МЕХ, РАЦИЯ, ИГРА, ОХ, ФОТО, ТОВАР, АРАТ, МОККО, ЯЛ, ДРАТВА, ПА, ЯР, РОМ, ЯС, РОЛ, ИТОГ, ЛИСТ, РОК, РАТЬ, ПЕ, ИОН, КОРА, РОГ, РОВ, ОСТРОГА, САП, ТУК, ОСИЛ, ЛИСТВА, СРОК, РОТ, ГИТАРА, ЭТОТ, РЕ, РОТИК, КУТ, НАВИС, ПРИТОН, ИМАМ, ОСТИТ, САН, РО";;
//        String[] e = s.split(", ");
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < e.length; i++) {
//            String el = e[i];
//            if (i == e.length - 1) {
//                sb.append("\"").append(el).append("\"");
//            } else {
//                sb.append("\"").append(el).append("\"").append(", ");
//            }
//        }
//        System.out.println(sb + "");
//    }
}
