package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyNews;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3395】サンマルクホールディングス
 *
 * @author toru1055
 */
public class CompanyNewsCollector3395
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3395;
    private static final String IR_URL = "http://www.saint-marc-hd.com/ir/topics.html";
    private static final String SHOP_URL = "http://www.saint-marc-hd.com/shop_topics/";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("div#contents > table.tb-info > tbody > tr");
        for (Element elem : elements) {
            MyDate aDate = MyDate.parseYmd(elem.select("th").text());
            Element anchor = elem.select("td > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div#contents > div#topicsList > ul#tp-shop > li");
        for (Element elem : elements) {
            MyDate aDate = MyDate.parseYmd(elem.select("span.date").text());
            Element anchor = elem.select("p > a").first();
            String url;
            String title;
            if (anchor == null) {
                url = SHOP_URL + "#" + aDate.toString();
                title = elem.select("p").first().text();
            } else {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }
}
