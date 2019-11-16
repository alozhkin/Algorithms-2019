package lesson1;

import org.jetbrains.annotations.NotNull;

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
    private static class Time implements Comparable<Time> {

        private enum Meridiem {
            AM,
            PM;

            Meridiem of(String str) {
                if (str.toLowerCase().equals("am")) {
                    return AM;
                } else if (str.toLowerCase().equals("pm")) {
                    return PM;
                } throw new IllegalArgumentException("There is no Meridiem " + str);
            }
        }

        private static final Comparator<Time> comparator =
                Comparator.comparing(Time::getMeridiem).thenComparing(it -> it.hours % 12)
                        .thenComparing(Time::getMinutes).thenComparing(Time::getSeconds);

        int hours;
        int minutes;
        int seconds;
        Meridiem meridiem;

        Time(String time) {
            String[] strings = time.split(" ");
            String[] numbers = strings[0].split(":");
            hours = Integer.parseInt(numbers[0]);
            minutes = Integer.parseInt(numbers[1]);
            seconds = Integer.parseInt(numbers[2]);
            meridiem = Meridiem.valueOf(strings[1]);
        }

        int getHours() {
            return hours;
        }

        int getMinutes() {
            return minutes;
        }

        int getSeconds() {
            return seconds;
        }

        Meridiem getMeridiem() {
            return meridiem;
        }

        @Override
        public int compareTo(@NotNull Time o) {
            return comparator.compare(this, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Time time = (Time) o;
            return hours == time.hours &&
                    minutes == time.minutes &&
                    seconds == time.seconds &&
                    meridiem == time.meridiem;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hours, minutes, seconds, meridiem);
        }

        @Override
        public String toString() {
            return String.format("%02d:%02d:%02d %s", hours, minutes, seconds, meridiem);
        }
    }


    public static void sortTimes(String inputName, String outputName) throws IOException {
        List<Time> times = parseTimes(inputName);
        Collections.sort(times);
        writeInFileTimes(times, outputName);
    }

    private static List<Time> parseTimes(String inputName) throws IOException {
        List<Time> times = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new FileReader(new File(inputName)))) {
            for (String line = input.readLine(); line != null; line = input.readLine()) {
                times.add(new Time(line));
            }
        }
        return times;
    }

    private static void writeInFileTimes(List<Time> times, String outputName) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(new File(outputName)))) {
            for (Time time : times) {
                output.write(time.toString());
                output.newLine();
            }
        }
    }
    /*
      память: O(n) на хранение времени
      время работы: O(n*log(n)) на сортировку листа
     */

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
    private static class Address implements Comparable<Address> {
        private static final Comparator<Address> comparator =
                Comparator.comparing(Address::getStreetName).thenComparing(Address::getNumber);
        private final String streetName;
        private final int number;

        private Address(String streetName, int number) {
            this.streetName = streetName;
            this.number = number;
        }

        String getStreetName() {
            return streetName;
        }

        int getNumber() {
            return number;
        }

        @Override
        public int compareTo(@NotNull Address o) {
            return comparator.compare(this, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return number == address.number &&
                    Objects.equals(streetName, address.streetName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(streetName, number);
        }

        @Override
        public String toString() {
            return streetName + " " + number;
        }
    }


    private static class House {
        private final SortedSet<Citizen> residents = new TreeSet<>();

        void addResident(Citizen citizen) {
            residents.add(citizen);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            House house = (House) o;
            return residents.equals(house.residents);
        }

        @Override
        public int hashCode() {
            return Objects.hash(residents);
        }

        @Override
        public String toString() {
            return residents.stream().map(Citizen::toString).collect(Collectors.joining(", "));
        }
    }


    private static class Citizen implements Comparable<Citizen> {
        private static final Comparator<Citizen> comparator =
                Comparator.comparing(Citizen::getLastName).thenComparing(Citizen::getName);
        private final String name;
        private final String lastName;

        Citizen(String lastName, String name) {
            this.name = name;
            this.lastName = lastName;
        }

        String getName() {
            return name;
        }

        String getLastName() {
            return lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Citizen citizen = (Citizen) o;
            return Objects.equals(name, citizen.name) &&
                    Objects.equals(lastName, citizen.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, lastName);
        }

        @Override
        public String toString() {
            return lastName + " " + name;
        }

        @Override
        public int compareTo(@NotNull Citizen o) {
            return comparator.compare(this, o);
        }
    }


    public static void sortAddresses(String inputName, String outputName) throws IOException {
        Map<Address, House> houses = parseHouses(inputName);
        writeInFileSortedHouses(houses, outputName);
    }

    private static Map<Address, House> parseHouses(String inputName) throws IOException {
        Map<Address, House> houses = new HashMap<>();
        try (BufferedReader input = new BufferedReader(new FileReader(new File(inputName)))) {
            for (String line = input.readLine(); line != null; line = input.readLine()) {
                String[] splittedLine = line.split(" ");
                Citizen citizen = new Citizen(splittedLine[0], splittedLine[1]);
                Address address = new Address(splittedLine[3], Integer.parseInt(splittedLine[4]));
                houses.putIfAbsent(address, new House());
                houses.get(address).addResident(citizen);
            }
        }
        return houses;
    }

    private static void writeInFileSortedHouses(Map<Address, House> houses, String outputName) throws IOException {
        SortedSet<Address> sortedAddresses = new TreeSet<>(houses.keySet());
        try (BufferedWriter output = new BufferedWriter(new FileWriter(new File(outputName)))) {
            for (Address address: sortedAddresses) {
                output.write(address.toString() + " - " + houses.get(address).toString());
                output.newLine();
            }
        }
    }
    /*
      память: O(n) на хранение адресов и жителей
      время работы: O(n*log(n)) n раз вставляем в TreeSet (красно-чёрное дерево) за log(n), сортируем адреса за n*log(n)
     */
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
    public static void sortTemperatures(String inputName, String outputName) throws IOException {
        int[] tempsCount = parseTemp(inputName);
        writeInFileTemps(tempsCount, outputName);
    }

    private static int[] parseTemp(String inputName) throws IOException {

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
                    tempsCount[num + negativeIntervalSize]++;
                } else {
                    tempsCount[negativeIntervalSize - num]++;
                }

            }
        }

        Stopwatch.stop("parseTemp");

        return tempsCount;
    }

    private static void writeInFileTemps(int[] temps, String outputName) throws IOException {
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
    public static void sortSequence(String inputName, String outputName) throws IOException {
        Integer[] sequence = parseIntLines(inputName);
        Integer[] res = findMostFrequent(sequence);
        writeInFileSeq(sequence, res[0], res[1], outputName);
    }

    public static Integer[] parseIntLines(String inputName) throws IOException {
        Stopwatch.start();

        List<Integer> sequence = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(new File(inputName)))) {
            String line;
            while ((line = input.readLine()) != null) {
                Integer num = Integer.parseInt(line);
                sequence.add(IntegerCache.getFromCache(num));
            }
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

    private static void writeInFileSeq(Integer[] sequence, Integer num, Integer frequency, String outputName)
            throws IOException {

        Stopwatch.start();

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(outputName, true))) {
            for (Integer el: sequence) {
                if (!el.equals(num)) wr.write(el + "\n");
            }
            for (int i = 0; i < frequency; i++) {
                wr.write(num + "\n");
            }
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
     * Задан отсортированный массив firstVertex и второй массив second,
     * первые firstVertex.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * firstVertex = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        int firstIndex = 0;
        int secondIndex = first.length;
        for (int i = 0; i < second.length; i++) {
            if (secondIndex == second.length
                    || (firstIndex < first.length && first[firstIndex].compareTo(second[secondIndex]) <= 0))
            {
                second[i] = first[firstIndex];
                firstIndex++;
            } else {
                second[i] = second[secondIndex];
                secondIndex++;
            }
        }
    }
    /*
      память: O(n) на хранение массивов
      время работы: O(n) обходим второй массив
     */
}
