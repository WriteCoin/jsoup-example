package ru.mai;

import org.jsoup.Jsoup;

public class Program {
    public static void main(String[] args) {
        WikipediaTest wikipediaTest = new WikipediaTest();
        wikipediaTest.run();

        YandexTest yandexTest = new YandexTest("Chrome/4.0.249.0 Safari/532.5", "http://www.google.com");
        yandexTest.run();

        EpicWarRateParser epicWarRateParser = new EpicWarRateParser();
        epicWarRateParser.run();
    }
}
