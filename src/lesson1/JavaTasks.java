package lesson1;

import kotlin.NotImplementedError;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortTimes(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortAddresses(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    public static void sortTemperatures(String inputName, String outputName) {
        Map<Integer, Integer> tempsCount = parseTemp(inputName);
        writeInFileTemps(tempsCount, outputName);
    }

    private static Map<Integer, Integer> parseTemp(String inputName) {
        String y = "tttttttt";
        String u = "tttttttt";

        Stopwatch.start();

        Map<Integer, Integer> tempsCount = new HashMap<>();

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(new File(inputName)))) {
            int sym = input.read();
            while (sym != -1) {
                int num = 0;
                boolean isPositive = true;
                if (sym == '-') {
                    isPositive = false;
                    sym = input.read();
                }

                while (sym != '\n') {
                    if (sym == '.') sym = input.read();
                    num *= 10;
                    num += sym - '0';
                    sym = input.read();
                }
                sym = input.read();

                if (isPositive) {
                    tempsCount.merge(num, 1, Integer::sum);
                } else {
                    tempsCount.merge(-num, 1, Integer::sum);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stopwatch.stop("parseTemp");

        return tempsCount;
    }

    private static void writeInFileTemps(Map<Integer, Integer> temps, String outputName) {
        Stopwatch.start();

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(outputName))) {
            for (Integer key: temps.keySet().stream().sorted().collect(Collectors.toList())) {
                for (int i = 0; i < temps.get(key); i++) {
                    if (key < 0) wr.write("-");
                    wr.write(Math.abs(key) / 10 + "." + Math.abs(key) % 10 + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stopwatch.stop("writeInFileTemps");
    }

    /*
    Потребление памяти зависит от размера буферов в BufferedInputStream и BufferWriter (b), а так же от размера
    интервала температур (t), как O(b + t). Для каждой строки в файле выполняется считывание, поиск в hashmap,
    который занимает в среднем O(1), запись в файл, что в итоге даёт O(n) сложность.
     */
    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    public static void sortSequence(String inputName, String outputName) {
        Integer[] sequence = parseIntLines(inputName);
        Integer num = findMostFrequent(sequence);
        moveElem(sequence, num);
        writeInFileSeq(sequence, outputName);
    }

    public static Integer[] parseIntLines(String inputName) {
        Stopwatch.start();

        List<Integer> sequence = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(new File(inputName)))) {
            String line;
            while ((line = input.readLine()) != null) {
                Integer num = Integer.parseInt(line);
                sequence.add(IntegerCache.getFromCache(num));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stopwatch.stop("parseIntLines");

        return sequence.toArray(new Integer[0]);
    }

    private static Integer findMostFrequent(Integer[] sequence) {
        Stopwatch.start();

        Map<Integer, Integer> integerCount = new HashMap<>();
        for (Integer num: sequence) {
            Integer t = integerCount.getOrDefault(num, 0);
            integerCount.put(num, IntegerCache.getFromCache(t + 1));
        }
        Integer res = -1;
        Integer resCount = -1;
        for (Map.Entry<Integer, Integer> entry: integerCount.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (value > resCount || value.equals(resCount) && key < res) {
                res = key;
                resCount = value;
            }
        }

        Stopwatch.stop("findMostFrequent");

        return res;
    }

    private static void moveElem(Integer[] sequence, Integer num) {
        Stopwatch.start();
        mergeSort0(sequence, num, 0, sequence.length);
        Stopwatch.stop("moveElem");
    }

    private static void mergeSort0(Integer[] array, Integer num, int l, int r) {
        if (l >= r - 1) return;
        int m = (l + r) >>> 1;
        mergeSort0(array, num, l, m);
        mergeSort0(array, num, m, r);
        merge(array, num, l, m, r);
    }

    private static void merge(Integer[] array, Integer num, int l, int m, int r) {
        int i = l;
        int j = m;
        while (!array[i].equals(num) && i < m) i++;
        while (j < r) array[i++] = array[j++];
        while (i < r) array[i++] = num;
    }

    private static void writeInFileSeq(Integer[] sequence, String outputName) {
        Stopwatch.start();

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(outputName, true))) {
            for (Integer el: sequence) {
                wr.write(el + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stopwatch.stop("writeInFileSeq");
    }

    /*
    Потребление памяти зависит от числового интервала (для каждого числа придётся создавать объект Integer),
    от количества чисел в последовательности (придётся хранить указатели на объекты в массиве), общая память
    O(interval + n).
    Асимпотическая сложность: парсит за O(n), находит самый встречающийся за O(n), при сдвиге элементов по сути
    происходит mergesort, которая требует O(n * lgn), сложность: O(n * lgn)
     */
    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
