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
 * 企業名：【3083】シーズメン
 *
 * @author toru1055
 */
public class CompanyNewsCollector3083
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3083;
    private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/3083/ja/announcement/announcement_2.xml";
    private static final String PR_URL = "http://www.csmen.co.jp/news/topics/index.php";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("#news");
        for (Element elem : elements) {
            String aTxt = elem.select("div.date").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("a").first();
            String title = elem.select("div#title").text();
            String url = PR_URL + "#" + anchor.attr("name");
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
