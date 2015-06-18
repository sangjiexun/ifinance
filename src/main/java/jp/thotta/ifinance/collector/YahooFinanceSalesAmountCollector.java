package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.collector.yj_finance.TextParser;
import jp.thotta.ifinance.model.CorporatePerformance;
import java.util.Map;
import java.util.Calendar;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * {@link SalesAmountCollector}の
 * Y!Financeデータ取得用実装.
 *
 * @author toru1055
 */
public class YahooFinanceSalesAmountCollector implements SalesAmountCollector {
  private static final int YJ_FINANCE_KD = 46;
  private YahooFinancePageIterator iter;

  /**
   * コンストラクタ.
   */
  public YahooFinanceSalesAmountCollector() {
    iter = new YahooFinancePageIterator(YJ_FINANCE_KD);
  }

  /**
   * イテレーションを始めるページIDを指定.
   * @param page ページID
   */
  public void setStartPage(int page) {
    if(page < 1) { page = 1; }
    iter.setCurrentPage(page);
  }

  public void appendSalesAmounts(
      Map<String, CorporatePerformance> m) throws IOException {
    while(iter.hasNext()) {
      Document doc = iter.next();
      Elements records = doc
        .select("table.rankingTable")
        .select("tr.rankingTabledata.yjM");
      for(Element tr : records) {
        Elements cols = tr.select("td");
        if(cols.size() == 9) {
          int stockId = TextParser.parseStockId(cols.get(1).text());
          long salesAmount = TextParser.parseFinancialAmount(cols.get(6).text());
          Calendar settlingYM = TextParser.parseYearMonth(cols.get(7).text());
          int settlingYear = settlingYM.get(Calendar.YEAR);
          int settlingMonth = settlingYM.get(Calendar.MONTH) + 1;
          String codeYearMonth = String.format("%4d,%4d/%2d", stockId, settlingYear, settlingMonth);
          CorporatePerformance cp;
          if(m.containsKey(codeYearMonth)) {
            cp = m.get(codeYearMonth);
          } else {
            cp = new CorporatePerformance(
                stockId, 
                settlingYear, 
                settlingMonth);
          }
          cp.salesAmount = salesAmount;
          m.put(codeYearMonth, cp);
        } else {
          throw new IOException("Table column number was changed: " + tr);
        }
      }
    }
  }
}
