package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【2374】セントケア・ホールディング
 * @author toru1055
 */
public class CompanyNewsCollector2374
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2374;
  private static final String IR_URL = "";
  private static final String PR_URL = "http://www.saint-care.com/information/";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("#contentsLayoutLeftInner > section > ul > li");
    for(Element elem : elements) {
      String aTxt = elem.select("span.time").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy.MM.dd"));
      Element anchor = elem.select("a").first();
      String title = elem.select("span.text").first().ownText();
      String url = PR_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}