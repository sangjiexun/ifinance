package jp.thotta.ifinance.utilizer;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.PredictedStockHistory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 株価予測の結果クラス.
 *
 * @author toru1055
 */
public class PredictedStockPrice {
    public JoinedStockInfo joinedStockInfo;
    public int stockId; //pk
    public MyDate predictedDate; //pk
    public long predictedMarketCap;
    public boolean isStableStock;

    public PredictedStockPrice(int stockId,
                               MyDate predictedDate,
                               long predictedMarketCap,
                               boolean isStableStock,
                               JoinedStockInfo joinedStockInfo) {
        this.stockId = stockId;
        this.predictedDate = predictedDate.copy();
        this.predictedMarketCap = predictedMarketCap;
        this.isStableStock = isStableStock;
        this.joinedStockInfo = joinedStockInfo;
    }

    public PredictedStockPrice(JoinedStockInfo joinedStockInfo,
                               StockPricePredictor spp, StockStatsFilter filter) {
        this.joinedStockInfo = joinedStockInfo;
        this.stockId = joinedStockInfo.dailyStockPrice.stockId;
        this.predictedDate = MyDate.getToday();
        this.predictedMarketCap = spp.predict(joinedStockInfo);
        this.isStableStock = filter.isNotable(joinedStockInfo);
    }

    @Override
    public String toString() {
        return String.format(
                "%s（%4d）[%s > %s]\n" +
                        "予想株価[%.1f円], 現在株価[%.1f円], スコア[%.1f倍]\n" +
                        "%s\n" +
                        "PER[%.2f倍], 業種NetPER[%.2f倍], 配当利回り[%.2f％], 自己資本比率[%.2f％]\n" +
                        "営業利益[%d百万円], 1年成長率[%.2f％], 2年成長率[%.2f％] \n" +
                        "営業利益率[%.1f％], " +
                        "純利益[%d百万円], 今期純利益(会社予想)[%d百万円], 今期予想成長率[%.2f％]\n" +
                        "平均年齢[%.4f歳], 平均年収[%.4f万円], 設立年月日[%s]\n" +
                        "企業特色：%s\n" +
                        "決算推移：http://minkabu.jp/stock/%4d/consolidated \n" +
                        "決算発表日[%s]\n",
                joinedStockInfo.companyProfile.companyName,
                stockId, businessCategory(), smallBusinessCategory(),
                predStockPrice(), joinedStockInfo.actualStockPrice(),
                undervaluedScore(),
                joinedStockInfo.dailyStockPrice.getDescription(),
                joinedStockInfo.per(), businessCategoryNetPer(),
                joinedStockInfo.dividendYieldPercent(),
                joinedStockInfo.ownedCapitalRatioPercent(),
                joinedStockInfo.corporatePerformance.operatingProfit,
                growthRate1(), growthRate2(),
                joinedStockInfo.corporatePerformance.operatingProfitRate() * 100,
                joinedStockInfo.corporatePerformance.netProfit,
                joinedStockInfo.estimateNetProfit(),
                estimateNetGrowthRate(),
                joinedStockInfo.companyProfile.averageAge,
                joinedStockInfo.companyProfile.averageAnnualIncomeMan(),
                joinedStockInfo.companyProfile.foundationDate,
                joinedStockInfo.companyProfile.companyFeature,
                stockId,
                joinedStockInfo.corporatePerformance.announcementDate);
    }

    public String getJoinKey() {
        return String.format("%4d", stockId);
    }

    public String getDescription() {
        String descFormat =
                "%s" +
                        "予想株価[%.1f円], 割安スコア[%.1f倍]\n";
        return String.format(
                descFormat,
                joinedStockInfo.getDescription(),
                predStockPrice(), undervaluedScore());
    }

    public double estimateNetGrowthRate() {
        return 100 * joinedStockInfo.estimateNetGrowthRate();
    }

    public double growthRate1() {
        //return 100 * joinedStockInfo.growthRate1();
        return 100 * joinedStockInfo.growthRateOperatingProfit1();
    }

    public double growthRate2() {
        //return 100 * joinedStockInfo.growthRate2();
        return 100 * joinedStockInfo.growthRateOperatingProfit2();
    }

    public double businessCategoryNetPer() {
        return 1.0 / joinedStockInfo.businessCategoryStats.netPerInverse.median();
    }

    /**
     * 業種.
     */
    public String businessCategory() {
        return joinedStockInfo.companyProfile.businessCategory;
    }

    public String smallBusinessCategory() {
        return joinedStockInfo.companyProfile.smallBusinessCategory;
    }

    /**
     * 割安スコアを出力.
     */
    public double undervaluedScore() {
        long diff = predictedMarketCap - joinedStockInfo.dailyStockPrice.marketCap;
        return (double) diff / joinedStockInfo.dailyStockPrice.marketCap + 1.0;
    }

    /**
     * 割安スコアを出力.
     */
    public double undervaluedRate() {
        long diff = predictedMarketCap - joinedStockInfo.dailyStockPrice.marketCap;
        return (double) diff / joinedStockInfo.dailyStockPrice.marketCap;
    }

    /**
     * 予測株価を出力.
     */
    public double predStockPrice() {
        return (double) (predictedMarketCap * 1000000) / joinedStockInfo.dailyStockPrice.stockNumber;
    }

    /**
     * 最新の予測株価情報を取得.
     *
     * @return 予測株価情報のリスト
     */
    public static List<PredictedStockPrice> selectLatests(Connection c)
            throws SQLException, ParseException {
        List<PredictedStockPrice> pspList = new ArrayList<PredictedStockPrice>();
        Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(c);
        Map<String, PredictedStockHistory> pshMap = PredictedStockHistory.selectPast(c, 0);
        for (String k : pshMap.keySet()) {
            PredictedStockHistory psh = pshMap.get(k);
            JoinedStockInfo jsi = jsiMap.get(psh.getJoinKey());
            if (jsi != null && jsi.hasEnough()
                    && psh.hasEnough()
                //&& psh.isStableStock
                    ) {
                PredictedStockPrice psp = new PredictedStockPrice(
                        psh.stockId, psh.predictedDate,
                        psh.predictedMarketCap, psh.isStableStock, jsi);
                pspList.add(psp);
            }
        }
        return pspList;
    }

    /**
     * 最新の予測株価リストをMapに変換して返す.
     *
     * @return 予測株価情報のMap
     */
    public static Map<String, PredictedStockPrice> selectLatestMap(Connection c)
            throws SQLException, ParseException {
        Map<String, PredictedStockPrice> pspMap =
                new HashMap<String, PredictedStockPrice>();
        List<PredictedStockPrice> pspList = selectLatests(c);
        for (PredictedStockPrice psp : pspList) {
            String k = psp.getJoinKey();
            pspMap.put(k, psp);
        }
        return pspMap;
    }

}
