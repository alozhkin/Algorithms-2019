package lesson1;

import kotlin.NotImplementedError;

import java.io.*;
import java.util.*;

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
        int[] tempsCount = parseTemp(inputName);
        writeInFileTemps(tempsCount, outputName);
    }

    private static int[] parseTemp(String inputName) {

        Stopwatch.start();

        int negativeIntervalSize = 2730;

        int[] tempsCount = new int[2730 + 5000 + 1];

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
                    tempsCount[num + negativeIntervalSize] += 1;
                } else {
                    tempsCount[negativeIntervalSize - num] += 1;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stopwatch.stop("parseTemp");

        return tempsCount;
    }

    private static void writeInFileTemps(int[] temps, String outputName) {
        Stopwatch.start();

        int negativeIntervalSize = 2730;

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(outputName))) {
            for (int i = 0; i < temps.length; i++) {
                for (int j = 0; j < temps[i]; j++) {
                    int num;
                    if (i < negativeIntervalSize) {
                        wr.write("-");
                        num = negativeIntervalSize - i;
                    } else {
                        num = i - negativeIntervalSize;
                    }
                    wr.write(num / 10 + "." + num % 10 + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stopwatch.stop("writeInFileTemps");
    }

    /*
    Потребление памяти зависит от размера буферов в BufferedInputStream и BufferWriter (b), а так же от размера
    интервала температур (t), как O(b + t). Для каждой строки в файле выполняется считывание, изменение элем в массиве,
    которое занимает O(1), запись в файл, что в итоге даёт O(n) сложность.
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
        Integer[] res = findMostFrequent(sequence);
        writeInFileSeq(sequence, res[0], res[1], outputName);
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

    private static Integer[] findMostFrequent(Integer[] sequence) {
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

        return new Integer[] {res, resCount};
    }

    private static void writeInFileSeq(Integer[] sequence, Integer num, Integer frequency, String outputName) {
        Stopwatch.start();

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(outputName, true))) {
            for (Integer el: sequence) {
                if (!el.equals(num)) wr.write(el + "\n");
            }
            for (int i = 0; i < frequency; i++) {
                wr.write(num + "\n");
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
    Асимпотическая сложность: парсит за O(n), находит самый встречающийся за O(n), печатает за O(n), сложность O(n)
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
