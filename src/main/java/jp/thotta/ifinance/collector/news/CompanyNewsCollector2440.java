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
 * 企業名：【2440】ぐるなび
 *
 * @author toru1055
 */
public class CompanyNewsCollector2440
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2440;
    private static final String IR_URL = "http://www.gnavi.co.jp/company/ir/news/";
    private static final String PR_URL = "http://www.gnavi.co.jp/company/release/index.xml";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = IR_URL;
        int type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("#main > div.sectionLv1 > div > dl > dt");
        Elements ddList = doc.select("#main > div.sectionLv1 > div > dl > dd");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = dd.select("ul > li > a").first();
            String title = dd.select("ul > li > a").text();
            String url = targetUrl + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = type;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    }

}
