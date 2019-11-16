package lesson2

import util.PerfResult
import util.estimate
import java.io.BufferedWriter
import java.io.File
import java.util.*
import kotlin.system.measureNanoTime
import kotlin.test.assertEquals

abstract class AbstractAlgorithmsTests {

    private val minPrice = 42

    private val maxPrice = 99999

    private fun generatePrices(size: Int): Pair<Int, Int> {
        val random = Random()
        val prices = mutableListOf<Int>()
        for (index in 1..size) {
            val price = minPrice + 1 + random.nextInt(maxPrice - 1 - minPrice)
            prices += price
        }
        val firstIndex = random.nextInt(size)
        val secondIndex = random.nextInt(size).let {
            when (it) {
                firstIndex -> if (firstIndex == size - 1) firstIndex - 1 else firstIndex + 1
                else -> it
            }
        }
        val (minIndex, maxIndex) =
            if (firstIndex < secondIndex) firstIndex to secondIndex else secondIndex to firstIndex
        prices[minIndex] = minPrice
        prices[maxIndex] = maxPrice

        fun BufferedWriter.writePrices() {
            for (price in prices) {
                write(price.toString())
                newLine()
            }
            close()
        }

        File("temp_prices.txt").bufferedWriter().writePrices()
        return minIndex + 1 to maxIndex + 1
    }

    private fun testGeneratedPrices(totalSize: Int): PerfResult<Unit> {
        try {
            val expectedAnswer = generatePrices(totalSize)
            var actualAnswer = -1 to -1
            val time = measureNanoTime { actualAnswer = JavaAlgorithms.optimizeBuyAndSell("temp_prices.txt") }
            assertEquals(expectedAnswer, actualAnswer)
            return PerfResult(size = totalSize, data = Unit, time = time)
        } finally {
            File("temp_prices.txt").delete()
        }
    }

    fun optimizeBuyAndSell(optimizeBuyAndSell: (String) -> Pair<Int, Int>) {
        assertEquals(1 to 2, optimizeBuyAndSell("input/my_buysell3.txt"))
        assertEquals(1 to 1, optimizeBuyAndSell("input/my_buysell2.txt"))
        assertEquals(3 to 4, optimizeBuyAndSell("input/buysell_in1.txt"))
        assertEquals(8 to 12, optimizeBuyAndSell("input/buysell_in2.txt"))
        assertEquals(3 to 4, optimizeBuyAndSell("input/buysell_in3.txt"))
        assertEquals(1 to 2, optimizeBuyAndSell("input/my_test_buysell.txt"))
        try {
            val expectedAnswer = generatePrices(1000)
            assertEquals(expectedAnswer, optimizeBuyAndSell("temp_prices.txt"))
        } finally {
            File("temp_prices.txt").delete()
        }
        try {
            val expectedAnswer = generatePrices(100000)
            assertEquals(expectedAnswer, optimizeBuyAndSell("temp_prices.txt"))
        } finally {
            File("temp_prices.txt").delete()
        }

        val perf = estimate(listOf(1_000, 10_000, 100_000, 1_000_000, 10_000_000)) {
            testGeneratedPrices(it)
        }
        println("sortSequence: $perf")
    }

    fun josephTask(josephTask: (Int, Int) -> Int) {
        assertEquals(1, josephTask(1, 1))
        assertEquals(2, josephTask(2, 1))
        assertEquals(3, josephTask(8, 5))
        assertEquals(28, josephTask(40, 3))
        var menNumber = 2
        for (i in 1..20) {
            assertEquals(1, josephTask(menNumber, 2))
            menNumber *= 2
        }
        assertEquals(50_000_000, josephTask(50_000_000, 1))
        assertEquals(4_580, josephTask(50_000, 49_999))
        assertEquals(4_682, josephTask(50_000, 45))
        assertEquals(8372, josephTask(50_000, 12))
        assertEquals(422037375, josephTask(451_666_013, 42))
    }

    fun longestCommonSubstring(longestCommonSubstring: (String, String) -> String) {
        assertEquals(" ", longestCommonSubstring("  qwerty", "uio p[] asdf ghjk zxcv bnm"))
        assertEquals("", longestCommonSubstring("ea", ""))
        assertEquals("a", longestCommonSubstring("a", "a"))
        assertEquals("a", longestCommonSubstring("awww", "adrrr"))
        assertEquals("a", longestCommonSubstring("wwwa", "adrrr"))
        assertEquals("a", longestCommonSubstring("wwwa", "drrra"))
        assertEquals("a", longestCommonSubstring("awww", "drrra"))
        assertEquals("123456789", longestCommonSubstring("23212312345678902314442", "9hfg123456789hfg"))
        assertEquals("", longestCommonSubstring("мой мир", "я"))
        assertEquals("", longestCommonSubstring("AAAA", "a"))
        assertEquals("зд", longestCommonSubstring("здравствуй мир", "мы здесь"))
        assertEquals("СЕРВАТОР", longestCommonSubstring("ОБСЕРВАТОРИЯ", "КОНСЕРВАТОРЫ"))
        assertEquals(
            "огда ", longestCommonSubstring(
                """
Мой дядя самых честных правил,
Когда не в шутку занемог,
Он уважать себя заставил
И лучше выдумать не мог.
Его пример другим наука;
Но, боже мой, какая скука
С больным сидеть и день и ночь,
Не отходя ни шагу прочь!
Какое низкое коварство
Полуживого забавлять,
Ему подушки поправлять,
Печально подносить лекарство,
Вздыхать и думать про себя:
Когда же черт возьмет тебя!
                """.trimIndent(),
                """
Так думал молодой повеса,
Летя в пыли на почтовых,
Всевышней волею Зевеса
Наследник всех своих родных.
Друзья Людмилы и Руслана!
С героем моего романа
Без предисловий, сей же час
Позвольте познакомить вас:
Онегин, добрый мой приятель,
Родился на брегах Невы,
Где, может быть, родились вы
Или блистали, мой читатель;
Там некогда гулял и я:
Но вреден север для меня
                """.trimIndent()
            )
        )
        assertEquals(
            "(с) Этот весь длинный-длинный текст является цитатой из Пушкина, поэма \"Руслан и Людмила\"",
            longestCommonSubstring(
                File("input/ruslan_ludmila_1.txt").readText(),
                File("input/ruslan_ludmila_2.txt").readText()
            ).trim()
        )
    }

    fun calcPrimesNumber(calcPrimesNumber: (Int) -> Int) {
        assertEquals(0, calcPrimesNumber(-1))
        assertEquals(0, calcPrimesNumber(1))
        assertEquals(1, calcPrimesNumber(2))
        assertEquals(2, calcPrimesNumber(4))
        assertEquals(4, calcPrimesNumber(10))
        assertEquals(8, calcPrimesNumber(20))
        assertEquals(1000, calcPrimesNumber(7920))
        assertEquals(1229, calcPrimesNumber(10000))
        assertEquals(2262, calcPrimesNumber(20000))
        assertEquals(5133, calcPrimesNumber(50000))
        assertEquals(9592, calcPrimesNumber(100000))
        assertEquals(17984, calcPrimesNumber(200000))
        assertEquals(33860, calcPrimesNumber(400000))
        assertEquals(49098, calcPrimesNumber(600000))
        assertEquals(56543, calcPrimesNumber(700000))
        assertEquals(63951, calcPrimesNumber(800000))
        assertEquals(71274, calcPrimesNumber(900000))
        assertEquals(78498, calcPrimesNumber(1000000))
        assertEquals(148933, calcPrimesNumber(2000000))
        assertEquals(348513, calcPrimesNumber(5000000))
        assertEquals(664579, calcPrimesNumber(10000000))
    }

    fun baldaSearcher(baldaSearcher: (String, Set<String>) -> Set<String>) {
        //outOfMemory из-за watcher. Без него будет считать пару сотен лет, так что без него нельзя.
//        assertEquals(
//            setOf(),
//            baldaSearcher(
//                "input/my_balda5.txt", setOf(
//                    "abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
//                            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbba"
//                )
//            )
//        )
        assertEquals(
            setOf(),
            baldaSearcher("input/my_balda4.txt", setOf("abbbbbbbbbbbbbba"))
        )
        assertEquals(
            setOf("bbab"),
            baldaSearcher("input/my_balda4.txt", setOf("bbab"))
        )
        assertEquals(
            setOf("babbbbb"),
            baldaSearcher("input/my_balda4.txt", setOf("babbbbb"))
        )
        assertEquals(
            setOf(),
            baldaSearcher(
                "input/my_balda.txt", setOf(
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                )
            )
        )
        assertEquals(
            setOf(),
            baldaSearcher("input/my_balda2.txt", setOf("ababt"))
        )
        assertEquals(
            setOf(),
            baldaSearcher("input/my_balda2.txt", setOf("abababababababababababaa"))
        )
        assertEquals(
            setOf("aaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            baldaSearcher("input/my_balda.txt", setOf("aaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        )
        assertEquals(
            setOf(),
            baldaSearcher("input/my_balda.txt", setOf("aaaaaaaab"))
        )
        assertEquals(
            setOf("kotlinpolytech"),
            baldaSearcher("input/my_balda3.txt", setOf("kotlinpolytech"))
        )
        assertEquals(
            setOf("ТРАВА", "КРАН", "АКВА", "НАРТЫ"),
            baldaSearcher("input/balda_in1.txt", setOf("ТРАВА", "КРАН", "АКВА", "НАРТЫ", "РАК"))
        )
        assertEquals(
            setOf("БАЛДА"),
            baldaSearcher("input/balda_in2.txt", setOf("БАЛАБОЛ", "БАЛДА", "БАЛДАЗАВР"))
        )
        assertEquals(
            setOf(
                "АПЕЛЬСИН", "МАРОККО", "ПЕРЕМЕНЫ", "ГРАВИТАЦИЯ",
                "РАССУДИТЕЛЬНОСТЬ", "КОНСТАНТИНОПОЛЬ", "ПРОГРАММИРОВАНИЕ", "ПОМЕХОУСТОЙЧИВОСТЬ", "АППРОКСИМАЦИЯ",
                "ЭЙНШТЕЙН"
            ),
            baldaSearcher(
                "input/balda_in3.txt", setOf(
                    "АПЕЛЬСИН", "МАРОККО", "ЭФИОПИЯ", "ПЕРЕМЕНЫ", "ГРАВИТАЦИЯ",
                    "РАССУДИТЕЛЬНОСТЬ", "БЕЗРАССУДНОСТЬ", "КОНСТАНТИНОПОЛЬ", "СТАМБУЛ", "ПРОГРАММИРОВАНИЕ",
                    "ПРОСТРАНСТВО", "ДИАЛЕКТИКА", "КВАЛИФИКАЦИЯ", "ПОМЕХОУСТОЙЧИВОСТЬ", "КОГЕРЕНТНОСТЬ",
                    "АППРОКСИМАЦИЯ", "ИНТЕРПОЛЯЦИЯ", "МАЙЕВТИКА", "ШРЕДИНГЕР", "ЭЙНШТЕЙН"
                )
            )
        )
        assertEquals(
            setOf(
                "ОСТАНОВ", "РАССУДИТЕЛЬНОСТЬ", "УСТОЙ", "ОСТРОГ", "ИНАЯ", "АИ", "ФОРС", "ЛАПА", "СТО", "ГУЛЯ", "АР",
                "РАСА", "АС", "СВАТ", "СТАВ", "АУ", "ПОЛ", "СТАЖ", "СИ", "ВОЛОС", "МИГ", "САНИ", "ЛЖА", "ТУЯ", "СТАН",
                "СИВАЯ", "СУД", "СУ", "ПОЛИС", "РАК", "ШАР", "МИР", "ПОТ", "СВАН", "ТА", "МИМ", "СИЛЬ", "ЛИСТВИЕ",
                "АНОНС", "СИМА", "ТО", "ПРОГРАММИРОВАНИЕ", "ШТЕЙН", "АНИС", "ИСК", "ОНА", "ГИД", "ГРАВИТАЦИЯ",
                "МИЛОВАНИЕ", "ОТАРА", "ЯСТВИЕ", "ЕЛЬ", "ТИШЬ", "МАМОНТ", "УД", "ГИТ", "УЖ", "ЛИНО", "ПОНИ", "ИВА",
                "СЫРТ", "ГА", "УС", "УТ", "ВЫЛЕТ", "ПОНОС", "ПАР", "ПРЯ", "АГА", "ПАЛ", "АУЛ", "ЛИС", "ФА", "РАНА",
                "ГОРА", "ЯРД", "ФИ", "МИРОВАЯ", "БЕЛ", "ПСИ", "КТИТОР", "ПАТ", "ПРОСОС", "СОЛИСТ", "ПАС", "СОРОК",
                "ОЛИВА", "ИГО", "ЭТИМОН", "ГАТЬ", "ОРС", "ХИ", "ХИНА", "ВИС", "СИВОСТЬ", "НОМ", "СЕЙ", "НОК", "СЕТ",
                "ОСТЬ", "ОСА", "КВАС", "ЯТЬ", "ТЯТЯ", "ЕР", "КИТ", "ТОСТ", "СЕТЬ", "НОС", "ОРТ", "ФОКС", "ОСТ", "ПРОК",
                "СИСТР", "ТИК", "ХОР", "СОЛИЛО", "ТИР", "ТИС", "АГАР", "ВЫРОСТ", "АГАТ", "СОРОМ", "НОРА", "МИОМА",
                "ТОРИ", "ТВИН", "МЕРА", "ТОРН", "ЛЬЕ", "МОСТ", "МОРОК", "СИЕ", "АСТРА", "МОР", "ТИГР", "БИТ", "БИС",
                "ТОРГ", "ЛИВАН", "ЦУГ", "ФОТОН", "МОТ", "ФОК", "ФОН", "МАЦА", "ГНУ", "ГАРТ", "НИВА", "ВЫНОС", "ФОТ",
                "СИР", "ВЫЯ", "ШУ", "ПРОПС", "ЛОВ", "ИЛ", "ШПОН", "ТЛЯ", "ТРИАС", "ДРАГА", "ИР", "МОПС", "НРАВ",
                "ШПРОТ", "ВИСТ", "МАГ", "ОРОК", "ЧУШЬ", "ШАРМ", "АИР", "НАСТ", "НОНА", "ГРОТ", "ЭСТ", "МАТ", "СЫН",
                "ФРЯ", "МАР", "СЫР", "ФАТ", "РИС", "ЗОНТ", "ОСОТ", "МОРОКА", "ОСОС", "ПОРЫВ", "МАК", "СЫЧ", "СОРТ",
                "АПЕЛЬСИН", "ВОНА", "ПРОСО", "ЭТО", "ВЫГАР", "НОМА", "ОХИ", "ВОЛ", "ТОРИТ", "МАРГО", "САГО", "ТОЙ",
                "ГАМ", "ТАРИ", "КОН", "КОМ", "ГРАММ", "ВОР", "РОТА", "ЛИ", "ДВА", "ПОСОЛ", "ВИРА", "МРАК", "СЭР",
                "ЧИХ", "ТИГРА", "МИСА", "РЕПА", "ТАРА", "РОСТ", "ТОМ", "ТОН", "РОСТР", "ТРОС", "ВТОРА", "ТОП", "ЛЯ",
                "МА", "ИВАСИ", "РОПОТ", "ТОТ", "ЛОРИ", "ЧИЙ", "БЕЛЬ", "СОНЬ", "МИ", "РОСА", "АГАМИ", "ТРОГ", "ИНЕЙ",
                "ДРОГА", "ЛАВР", "СОМ", "УТОР", "СПОР", "ЖИР", "АВИА", "ВАЛ", "ЙОТ", "АРГО", "МИРО", "ЖИТО", "ВАЖ",
                "НИ", "ПОРОК", "УТЯ", "ОМАР", "НО", "ВАРЯГ", "ГУЛ", "ЛУГ", "ТАУ", "ЮГ", "ВАШ", "ЛУБ", "СПОРОК", "ХОРТ",
                "ЮЗ", "ЛОНО", "ЖИРО", "КОРМ", "ВАР", "ПОСТ", "СОН", "СОЛО", "УСТОЙЧИВОСТЬ", "СОР", "ЮС", "ЮТ", "СОТ",
                "СИЛОН", "ВОИН", "ПОМОСТ", "АНТ", "РИГА", "СИЛОС", "ПРОТОН", "СОЛЬ", "ОМ", "ПРИВАР", "КСИ", "ОН",
                "ЛИСА", "МЕХ", "РАЦИЯ", "ИГРА", "ОХ", "ФОТО", "ТОВАР", "АРАТ", "МОККО", "ЯЛ", "ДРАТВА", "ПА", "ЯР",
                "РОМ", "ЯС", "РОЛ", "ИТОГ", "ЛИСТ", "РОК", "РАТЬ", "ПЕ", "ИОН", "КОРА", "РОГ", "РОВ", "ОСТРОГА", "САП",
                "ТУК", "ОСИЛ", "ЛИСТВА", "СРОК", "РОТ", "ГИТАРА", "ЭТОТ", "РЕ", "РОТИК", "КУТ", "НАВИС", "ПРИТОН",
                "ИМАМ", "ОСТИТ", "САН", "РО"
            ),
            baldaSearcher(
                "input/balda_in3.txt", File("input/all_nouns.txt").readLines().toSet()
            )
        )
    }
}