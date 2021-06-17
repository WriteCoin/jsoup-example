package ru.mai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaTest {
    private String lang = "en";

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void run() {
        String site = "https://" + lang + ".wikipedia.org/";
        try {
            Document doc = Jsoup.connect(site).get();
            System.out.println("Название сайта: " + doc.title());
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                System.out.println(headline.attr("title"));
                System.out.println(headline.absUrl("href"));
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Ошибка парсинга HTML-сайта " + site);
        }

    }
}
