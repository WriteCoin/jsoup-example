package ru.mai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class YandexTest {
    private String agent;
    private String referrer;

    public String getAgent() {
        return this.agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getReferrer() {
        return this.referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public YandexTest(String agent, String referrer) {
        this.agent = agent;
        this.referrer = referrer;
    }

    public void run() {
        String site = "https://yandex.ru/";
        try {
            Document doc = Jsoup.connect(site)
                    .userAgent(agent)
                    .referrer(referrer)
                    .get();
            System.out.println("Название сайта: " + doc.title());
            System.out.println();
            System.out.println("Список новостей:");
            Elements newsPanel = doc.select("div.news__panel.mix-tabber-slide2__panel");
            for (Element element : newsPanel.select("span.news__item-content")) {
                System.out.println(element.text());
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Ошибка парсинга сайта " + site);
        }
    }
}
