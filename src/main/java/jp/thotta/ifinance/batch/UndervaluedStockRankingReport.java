package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.*;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.common.MyDate;

/**
 * 割安銘柄のランキングレポート.
 * @author toru1055
 */
public class UndervaluedStockRankingReport {
  Connection conn;

  public UndervaluedStockRankingReport(Connection c) {
    this.conn = c;
  }

  /**
   * レポート実行.
   * @return レポートが成功したかどうか
   */
  public boolean report() 
    throws SQLException, ParseException {
    List<PredictedStockPrice> pspList = PredictedStockPrice.selectLatests(conn);
    Collections.sort(pspList, new Comparator<PredictedStockPrice>() {
      @Override
      public int compare(PredictedStockPrice p1, PredictedStockPrice p2) {
        return p1.undervaluedScore() > p2.undervaluedScore() ? -1 : 1;
      }
    });
    System.out.println("==== Undervalued Stock Ranking ====");
    int reportCount = 0;
    for(PredictedStockPrice psp : pspList) {
      if(true
          //&& psp.isStableStock
          //&& psp.joinedStockInfo.ownedCapitalRatioPercent() > 30.0
          //&& psp.undervaluedScore() > 1.1
          //&& psp.joinedStockInfo.companyProfile.businessCategory.equals("小売業")
          //&& psp.joinedStockInfo.companyProfile.smallBusinessCategory.equals("レジャー")
          //&& psp.joinedStockInfo.per() < 30.0
          //&& psp.growthRate1() > 0.0
          //&& psp.growthRate2() > psp.growthRate1()
          //&& psp.estimateNetGrowthRate() > 20.0
          //&& psp.joinedStockInfo.operatingProfitDiff1() > 0
          //&& psp.joinedStockInfo.operatingProfitDiff2() > 0
          //&& psp.joinedStockInfo.estimateNetDiff() > 0
          ) {
        if(reportCount++ < 100) {
          String lstr = String.format("[%d] %s", reportCount, psp);
          System.out.println(lstr);
          //System.out.println(psp.jsi.corporatePerformance);
        }
      }
    }
    return (reportCount > 0);
  }

  /**
   * レポート実行.
   * @return レポートが成功したかどうか
   */
  public boolean overvaluedReport() 
    throws SQLException, ParseException {
    List<PredictedStockPrice> pspList = PredictedStockPrice.selectLatests(conn);
    Collections.sort(pspList, new Comparator<PredictedStockPrice>() {
      @Override
      public int compare(PredictedStockPrice p1, PredictedStockPrice p2) {
        return p1.undervaluedScore() < p2.undervaluedScore() ? -1 : 1;
      }
    });
    System.out.println("==== Overvalued Stock Ranking ====");
    int reportCount = 0;
    for(PredictedStockPrice psp : pspList) {
      if(psp.isStableStock && 
          psp.joinedStockInfo.ownedCapitalRatioPercent() > 30.0 &&
          psp.undervaluedScore() < 0.9
          //&& psp.growthRate1() > 0
          //&& psp.growthRate2() > 0
          ) {
        if(reportCount++ < 100) {
          String lstr = String.format("[%d] %s", reportCount, psp);
          System.out.println(lstr);
          //System.out.println(psp.jsi.corporatePerformance);
        }
      }
    }
    return (reportCount > 0);
  }

  public void showPredictedStockPrice(int stockId)
    throws SQLException, ParseException {
    Map<String, List<CompanyNews>> cnMap = CompanyNews.selectMapByPast(conn, 7);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
    StockPricePredictor spp = new LinearStockPricePredictor();
    double rmse = spp.train(jsiFil);
    System.out.println("Train data size = " + jsiFil.size() + ", RMSE = " + rmse);
    StockStatsFilter filter = new StockStatsFilter(jsiMap);
    String k = String.format("%4d", stockId);
    JoinedStockInfo jsi = jsiMap.get(k);
    PredictedStockPrice psp = new PredictedStockPrice(jsi, spp, filter);
    System.out.println(psp);
    List<CompanyNews> cnList = cnMap.get(k);
    System.out.println("■この銘柄の直近ニュース");
    if(cnList != null && cnList.size() > 0) {
      for(CompanyNews news : cnList) {
        System.out.println(news.getDescription() + "\n");
      }
    } else {
      System.out.println("直近のニュースはありません or この銘柄はまだクロールしていません\n");
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      UndervaluedStockRankingReport reporter 
        = new UndervaluedStockRankingReport(c);
      if(args.length == 0) {
        reporter.report();
      } else if(args[0].equals("Overvalued")) {
        reporter.overvaluedReport();
      } else {
        reporter.showPredictedStockPrice(Integer.parseInt(args[0]));
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
