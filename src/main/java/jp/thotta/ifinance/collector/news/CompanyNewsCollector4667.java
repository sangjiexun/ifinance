package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【4667】アイサンテクノロジー
 *
 * @author toru1055
 */
public class CompanyNewsCollector4667
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 4667;
    private static final String IR_URL = "http://www.aisantec.co.jp/ir/informations/atom.xml";
    private static final String PR_URL = "http://www.aisantec.co.jp/informations/atom.xml";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "http://www.aisantec.co.jp/publicity/atom.xml";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXmlElement(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXmlElement(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_DEVELOPMENT);
    }

    @Override
    public void parsePublicityList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXmlElement(newsList, stockId, PUBLICITY_URL,
                CompanyNews.NEWS_TYPE_PUBLICITY);
    }

}
