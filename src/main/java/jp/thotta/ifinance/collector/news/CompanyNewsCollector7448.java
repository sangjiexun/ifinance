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

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【7448】ジーンズメイト
 *
 * @author toru1055
 */
public class CompanyNewsCollector7448
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7448;
    private static final String IR_URL = "http://www.jeansmate.co.jp/ir/";
    private static final String PR_URL = "http://www.jeansmate.co.jp/news/";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("#contents > main > div.irNews-list > div > a");
        for (Element elem : elements) {
            String aTxt = elem.select("time").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem;
            String title = elem.select("p.irNews-title").text();
            String url = IR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("#contents > main > div > article > a");
        for (Element elem : elements) {
            String aTxt = elem.select("div > time").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem;
            String title = elem.select("h3.newsList-title").text();
            String url = PR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
                newsList.add(news);
            }
        }
    }

}
