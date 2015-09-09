package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.common.MyDate;

/**
 * 過去一週間の株価上昇率とニュースを送信.
 * @author toru1055
 */
public class WeeklyNewsReport {
  Connection conn;

  public WeeklyNewsReport(Connection c) {
    this.conn = c;
  }

  /**
   * レポート実行.
   */
  public void report(int past) throws SQLException, ParseException {
    Map<String, List<CompanyNews>> cnMap = CompanyNews.selectMapByPast(conn, past);
    final Map<String, DailyStockPrice> pastDspMap = DailyStockPrice.selectPasts(conn, past);
    final Map<String, DailyStockPrice> latestDspMap = DailyStockPrice.selectLatests(conn);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, CompanyProfile> prMap = CompanyProfile.selectAll(conn);
    List<String> keys = new ArrayList<String>();
    for(String k : cnMap.keySet()) {
      keys.add(k);
    }
    Collections.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String k1, String k2) {
        double liftRatio1 = getLiftRatio(k1, pastDspMap, latestDspMap);
        double liftRatio2 = getLiftRatio(k2, pastDspMap, latestDspMap);
        return liftRatio1 < liftRatio2 ? -1 : 1;
      }
    });
    for(String k : keys) {
      System.out.println("======= " + k + " =======");
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      double liftRatio = getLiftRatio(k, pastDspMap, latestDspMap);
      System.out.println(
          String.format("[過去%d日間の株価上昇率: %.1f％]", 
            past, liftRatio * 100)
          );
      List<CompanyNews> cnList = cnMap.get(k);
      if(jsi == null) {
        System.out.println(profile.getDescription());
      } else {
        System.out.println(jsi.getDescription());
      }
      for(CompanyNews news : cnList) {
        System.out.println(news.getDescription() + "\n");
      }
    }
  }

  static double getLiftRatio(String k, 
      Map<String, DailyStockPrice> pastDspMap, 
      Map<String, DailyStockPrice> latestDspMap) {
    DailyStockPrice pastDsp = pastDspMap.get(k);
    DailyStockPrice latestDsp = latestDspMap.get(k);
    if(pastDsp != null && latestDsp != null) {
      double liftRatio = (double)(latestDsp.marketCap - pastDsp.marketCap) / pastDsp.marketCap;
      return liftRatio;
    } else {
      return 0.0;
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      WeeklyNewsReport reporter = new WeeklyNewsReport(c);
      if(args.length == 0) {
        reporter.report(14);
      } else {
        int past = Integer.parseInt(args[0]);
        reporter.report(past);
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }
}