package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;

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
 * 企業名：【4752】昭和システムエンジニアリング
 * @author toru1055
 */
public class CompanyNewsCollector4752
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 4752;
  private static final String IR_URL = "http://www.showa-sys-eng.co.jp/news.html";
  private static final String PR_URL = "";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    String targetUrl = IR_URL;
    int type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
    Document doc = Scraper.getHtml(targetUrl);
    Elements dtList = doc.select("#news > dl > dt");
    Elements ddList = doc.select("#news > dl > dd");
    for(int i = 0; i < dtList.size(); i++) {
      Element dt = dtList.get(i);
      Element dd = ddList.get(i);
      String aTxt = dt.text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy年MM月dd日"));
      Element anchor = dd.select("a").first();
      String title = dd.text();
      String url = targetUrl + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
        title = anchor.text();
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = type;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}