package ru.mai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Парсер сайта EpicWar для автоматической оценки карт игры WarCraft III
 */
public class EpicWarRateParser {
    // адрес сайта
    private static final String SITE = "https://www.epicwar.com";
    // максимальная длина строки автора
    private static final Integer LENGTH = 32;
    // входной поток
    private static final Scanner IN = new Scanner(System.in);

    //общий флаг ошибки
    private boolean errorFlag = false;

    private Document doc;
    // имя автора
    private String author;
    // оценка картам
    private String rate;
    // макс. страница
    private int maxPage;
    // первая введенная страница
    private int firstPageIndex;
    // последняя введенная страница
    private int lastPageIndex;

    /**
     * Запуск сценария парсера
     */
    public void run() {
        connect();
        if (errorFlag) {
            return;
        }

        author = readAuthor();

        rate = readRate();

        maxPage = getMaxPage();
        if (errorFlag) {
            return;
        }

        firstPageIndex = readFirstPage();

        lastPageIndex = readLastPage();

        rateMaps();
    }

    /**
     * подключение к сайту (возможна ошибка)
     */
    private void connect() {
        try {
            doc = Jsoup.connect(SITE).get();
        } catch (Exception e) {
            System.out.println("Ошибка подключения к сайту " + SITE);
            errorFlag = true;
        }
    }

    /**
     * Чтение автора из консоли
     * @return имя автора
     */
    private String readAuthor() {
        System.out.println("Введите автора карт:");
        String name = IN.nextLine();
        if (name.length() > LENGTH) {
            System.out.println("Слишком длинное имя автора!");
            return readAuthor();
        }
        return name;
    }

    /**
     * Ввод оценки карты
     * @return строка оценки в виде 1 или -1
     */
    private String readRate() {
        System.out.println("Введите цифру оценки карты (1 - \"хорошо\", -1 - \"плохо\"):");
        String rate = IN.nextLine();
        if (!rate.equals("1") && !rate.equals("-1")) {
            System.out.println("Оценка записывается в виде 1 или -1!");
            return readRate();
        }
        return rate;
    }

    /**
     * определение макс. страницы в EpicWar
     * @return maxPage
     */
    private int getMaxPage() {
        int result;
        try {
            Elements pages = doc.select("td[align*=center].nav");
            String resultStr = pages.select("a").last().text();
            result = Integer.parseInt(resultStr);
        } catch (Exception e) {
            System.out.println("Не удалось получить данные максимальной страницы сайта");
            errorFlag = true;
            return 0;
        }
        return result;
    }

    /**
     * Ввод первой страницы для интервала обработки
     * @return число номер страницы
     */
    private int readFirstPage() {
        System.out.println("Введите номер начальной страницы в EpicWar для интервала обработки (не менее 1 и не более " + maxPage + "):");
        int page;
        try {
            page = Integer.parseInt(IN.nextLine());
            String message = "";
            if (page < 1) {
                message = "менее 1";
            } else if (page > maxPage) {
                message = "более " + maxPage;
            }
            if (!message.isEmpty()) {
                System.out.println("Номер страницы не " + message + "!");
                return readFirstPage();
            }
        } catch (Exception e) {
            System.out.println("Не удалось обработать ввод");
            return readFirstPage();
        }
        return page;
    }

    /**
     * Ввод последней страницы для интервала обработки
     * @return число номер страницы
     */
    private int readLastPage() {
        System.out.println("Введите номер конечной страницы в EpicWar для интервала обработки (не менее " + firstPageIndex + " и не более " + maxPage + "):");
        int page;
        try {
            page = Integer.parseInt(IN.nextLine());
            String message = "";
            if (page < firstPageIndex) {
                message = "менее " + firstPageIndex;
            } else if (page > maxPage) {
                message = "более " + maxPage;
            }
            if (!message.isEmpty()) {
                System.out.println("Номер страницы не " + message + "!");
                return readLastPage();
            }
        } catch (Exception e) {
            System.out.println("Не удалось обработать ввод");
            return readLastPage();
        }
        return page;
    }

    /**
     * Оценить карты по полученным данным
     */
    private void rateMaps() {
        for (int pageIndex = firstPageIndex; pageIndex <= lastPageIndex; pageIndex++) {
            System.out.println("Загрузка страницы " + pageIndex);
            try {
                Document pageDoc = Jsoup.connect(SITE + "/maps/?page=" + pageIndex + "&sort=time&order=desc").get();
                Elements mapLinks = pageDoc.select("td[align*=center].listentry a");
                Elements mapData = pageDoc.select("td[valign*=top].listentry b");
                Elements mapAuthors = new Elements();
                for (int i = 0; i < mapData.size(); i++) {
                    if (i % 2 != 0) {
                        mapAuthors.add(mapData.get(i));
                    }
                }
                for (int i = 0; i < mapLinks.size(); i++) {
                    if (author.equals(mapAuthors.get(i).text())) {
                        rateMap(SITE + mapLinks.get(i).attr("href"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Не удалось обработать страницу " + pageIndex);
                errorFlag = true;
                return;
            }
        }
    }

    /**
     * Оценить карту по url-ссылке
     * @param url ссылка
     */
    private void rateMap(String url) {
        try {
            Document mapDoc = Jsoup.connect(url).get();
            Elements boldElements = mapDoc.select("td[valign*=top].listentry b");
            if (boldElements.get(2).text().equals("Rate this map:")) {
                Elements links = mapDoc.select("td[valign*=top].listentry a");
                for (Element link : links) {
                    String style = link.select("span").attr("style");
                    String rateLink = "";
                    if (style.equals("color:#3366ff") && rate.equals("1")) {
                        rateLink = link.attr("href");
                    } else if (style.equals("color:#ff6633") && rate.equals("-1")) {
                        rateLink = link.attr("href");
                    }
                    if (!rateLink.equals("")) {
                        String resultLink = SITE + rateLink;
                        System.out.println("Оценка карты " + boldElements.get(0).text() + " " + resultLink);
                        browse(resultLink);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Не удалось обработать страницу карты");
            errorFlag = true;
        }
    }

    /**
     * Перейти по ссылке
     * @param url ссылка
     */
    private void browse(String url) {
        try {
            Desktop d = Desktop.getDesktop();

            d.browse(new URI(url));
        } catch (IOException e) {
            System.out.println("Не удалось перейти по ссылке оценки");
            errorFlag = true;
        } catch (URISyntaxException e) {
            System.out.println("Ошибка в синтаксисе ссылки оценки");
            errorFlag = true;
        }
    }
}
