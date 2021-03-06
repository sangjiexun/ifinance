package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.NewsReminderWeb;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.PredictedStockPrice;

import java.util.List;

public class ReportPrinter {

    public static void printHtmlHeader(String subject) {
        System.out.println("To: ifinance-users");
        System.out.println("Subject: " + subject + "(" + MyDate.getToday() + ")");
        System.out.println("Content-Type: text/html; charset=\"utf-8\"\n");
        System.out.println("<html><body><h1>" + subject + "</h1>");
    }

    public static void printHtmlFooter() {
        System.out.println("</body></html>");
    }

    public static void printStockDescriptions(JoinedStockInfo jsi,
                                              CompanyProfile profile,
                                              CompanyNews rankingNews,
                                              DailyStockPrice dsp,
                                              PredictedStockPrice psp,
                                              List<CompanyNews> cnList,
                                              CompanyNewsCollector coll) {
        if (psp != null) {
            System.out.println(psp.getDescription());
        } else {
            if (jsi != null) {
                System.out.println(jsi.getDescription());
            } else {
                if (profile == null || dsp == null) {
                    System.out.println("この銘柄はデータベースに存在しません");
                    return;
                } else {
                    System.out.println(profile.getDescription() + "\n");
                    System.out.println(dsp.getDescription() + "\n");
                }
            }
        }
        if (rankingNews != null) {
            System.out.println(rankingNews.getDescription() + "\n");
        }
        System.out.println("■この銘柄の直近ニュース");
        if (cnList != null && cnList.size() > 0) {
            for (CompanyNews news : cnList) {
                if (news != null) {
                    System.out.println(news.getDescription() + "\n");
                }
            }
        } else {
            if (coll == null) {
                System.out.println("この銘柄はまだクロールしていません\n");
            } else {
                System.out.println("直近のニュースはありません\n");
            }
        }
    }

    public static void printReminderList(
            List<NewsReminderWeb> reminderList) {
        if (reminderList != null && reminderList.size() > 0) {
            System.out.println("■この銘柄のリマインド");
            for (NewsReminderWeb reminder : reminderList) {
                System.out.println(reminder.newsId + ": " +
                        reminder.message + "(" +
                        reminder.remindDate.toString() + ")");
            }
        }
    }

}
