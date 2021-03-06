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
 * 企業名：【6094】フリークアウト
 *
 * @author toru1055
 */
public class CompanyNewsCollector6094
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6094;
    private static final String IR_URL = "AS08108";
    private static final String PR_URL = "https://www.fout.co.jp/news-list/";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXjStorageId(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        MyDate myDate = MyDate.getToday();
        Elements elements = doc.select("#contents > section > div > article");
        for (Element elem : elements) {
            String aTxt = elem.select("dl > dt > time").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            if (aDate == null) {
                aDate = myDate;
            } else {
                myDate = aDate;
            }
            Element anchor = elem.select("dl > dd > p > a").first();
            String title = elem.select("dl > dd > p").text();
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

}
