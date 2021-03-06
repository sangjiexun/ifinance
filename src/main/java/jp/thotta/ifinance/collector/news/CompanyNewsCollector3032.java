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
 * 企業名：【3032】ゴルフ・ドゥ
 *
 * @author toru1055
 */
public class CompanyNewsCollector3032
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3032;
    private static final String IR_URL = "http://www.golfdo.jp/ir/ir07.shtml";
    private static final String PR_URL = "http://www.golfdo.jp/corporate/c_news.html";
    private static final String SHOP_URL = "http://www.golfdo.jp/tenpo/s_news.html";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("div.mgn_info > table:nth-child(1) > tbody > tr > td:nth-child(3) > div:nth-child(2) > table > tbody > tr");
        for (Element elem : elements) {
            String aTxt = elem.select("td > div.t3 > font > strong").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("td > div > a").first();
            String title = elem.select("td > div > a").text();
            String url = IR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
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
        Elements elements = doc.select("div.mgn_info > table:nth-child(1) > tbody > tr > td:nth-child(3) > table:nth-child(3) > tbody > tr > td > table > tbody > tr");
        for (Element elem : elements) {
            String aTxt = elem.select("td:nth-child(1) > div > font > strong").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("td > div > a").first();
            String title = elem.select("td > div > a").text();
            String url = PR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div.mgn_info > table:nth-child(1) > tbody > tr > td:nth-child(3) > table:nth-child(3) > tbody > tr > td > table > tbody > tr");
        for (Element elem : elements) {
            String aTxt = elem.select("td:nth-child(1) > div > font > strong").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("td > div > a").first();
            String title = elem.select("td > div > a").text();
            String url = SHOP_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(360)) > 0) {
                newsList.add(news);
            }
        }
    }

}
