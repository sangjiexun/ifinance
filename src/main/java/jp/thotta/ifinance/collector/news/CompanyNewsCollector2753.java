package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyNews;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【2753】あみやき亭
 *
 * @author toru1055
 */
public class CompanyNewsCollector2753
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2753;
    private static final String IR_URL = "";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.amiyakitei.co.jp/shopk.html";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div.wrap > div.amm > div.amc > div#bk1483");
        int shopNumber = elements.size();
        MyDate aDate = new MyDate(2100, 1, 1);
        String title = "あみやき亭の店舗数が【" + shopNumber + "】になりました";
        String url = SHOP_URL + "#shopNum/" + shopNumber;
        CompanyNews news = new CompanyNews(stockId, url, aDate);
        news.title = title;
        news.createdDate = MyDate.getToday();
        news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
        if (news.hasEnough()
                && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
            newsList.add(news);
        }
    }

}
