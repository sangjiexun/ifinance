package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CorporatePerformanceTest extends TestCase {
    Connection c;
    Statement st;
    CorporatePerformance cp, cp2, cm1, cm2, cm3;
    Map<String, CorporatePerformance> m;

    protected void setUp() {
        cp = new CorporatePerformance(9999, 2015, 3);
        cp.salesAmount = (long) 1000;
        cp2 = new CorporatePerformance(9999, 2015, 3);
        cp2.netProfit = (long) -100;
        cm1 = new CorporatePerformance(1001, 2015, 3);
        cm2 = new CorporatePerformance(1002, 2014, 12);
        cm3 = new CorporatePerformance(1001, 2014, 3);
        cm1.salesAmount = (long) 1000;
        cm1.netProfit = (long) 100;
        cm2.salesAmount = (long) 2000;
        cm2.netProfit = (long) -10;
        cm3.salesAmount = (long) 200;
        cm3.netProfit = (long) 1;
        cm1.announcementDate = new MyDate(2015, 5, 15);
        cm2.announcementDate = new MyDate(2015, 2, 10);
        cm3.announcementDate = new MyDate(2014, 5, 14);
        m = new HashMap<String, CorporatePerformance>();
        m.put(cm1.getKeyString(), cm1);
        m.put(cm2.getKeyString(), cm2);
        m.put(cm3.getKeyString(), cm3);
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            st = c.createStatement();
            CorporatePerformance.dropTable(c);
            CorporatePerformance.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testGetKeyString() {
        assertEquals(cp.getKeyString(), "9999,2015/03");
    }

    public void testExists() {
        try {
            assertFalse(cp.exists(st));
            cp.insert(st);
            assertTrue(cp.exists(st));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testInsertUpdate() {
        try {
            cp.insert(st);
            cp2.readDb(st);
            assertEquals(cp2.salesAmount, cp.salesAmount);
            cp2.update(st);
            cp.readDb(st);
            assertEquals(cp.netProfit, cp2.netProfit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testUpdateMap() {
        try {
            CorporatePerformance.updateMap(m, c);
            Map<String, CorporatePerformance> fromDbMap = CorporatePerformance.selectAll(c);
            for (String k : m.keySet()) {
                CorporatePerformance m_cp = m.get(k);
                CorporatePerformance db_cp = fromDbMap.get(k);
                assertEquals(m_cp, db_cp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSelectLatests() {
        CorporatePerformance cp11 = new CorporatePerformance(1001, 2014, 12);
        CorporatePerformance cp12 = new CorporatePerformance(1001, 2013, 12);
        CorporatePerformance cp21 = new CorporatePerformance(1002, 2013, 12);
        cp11.salesAmount = (long) 100;
        cp11.netProfit = (long) 10;
        cp12.salesAmount = (long) 200;
        cp11.netProfit = (long) -10;
        cp21.salesAmount = (long) 1000;
        cp11.netProfit = (long) 100;
        cp11.announcementDate = new MyDate(2015, 5, 15);
        cp12.announcementDate = new MyDate(2015, 2, 10);
        cp21.announcementDate = new MyDate(2014, 5, 14);
        Map<String, CorporatePerformance> cp_map = new HashMap<String, CorporatePerformance>();
        cp_map.put(cp11.getKeyString(), cp11);
        cp_map.put(cp12.getKeyString(), cp12);
        cp_map.put(cp21.getKeyString(), cp21);
        try {
            CorporatePerformance.updateMap(cp_map, c);
            Map<String, CorporatePerformance> latests = CorporatePerformance.selectLatests(c);
            CorporatePerformance tcp1 = latests.get("1001");
            CorporatePerformance tcp2 = latests.get("1002");
            assertEquals(tcp1, cp11);
            assertEquals(tcp2, cp21);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSelectPasts() {
        CorporatePerformance cp11 = new CorporatePerformance(1001, 2014, 12);
        CorporatePerformance cp12 = new CorporatePerformance(1001, 2013, 12);
        CorporatePerformance cp21 = new CorporatePerformance(1002, 2013, 12);
        cp11.salesAmount = (long) 100;
        cp11.netProfit = (long) 10;
        cp12.salesAmount = (long) 200;
        cp11.netProfit = (long) -10;
        cp21.salesAmount = (long) 1000;
        cp11.netProfit = (long) 100;
        cp11.announcementDate = new MyDate(2015, 5, 15);
        cp12.announcementDate = new MyDate(2015, 2, 10);
        cp21.announcementDate = new MyDate(2014, 5, 14);
        Map<String, CorporatePerformance> cp_map = new HashMap<String, CorporatePerformance>();
        cp_map.put(cp11.getKeyString(), cp11);
        cp_map.put(cp12.getKeyString(), cp12);
        cp_map.put(cp21.getKeyString(), cp21);
        try {
            CorporatePerformance.updateMap(cp_map, c);
            Map<String, CorporatePerformance> pasts =
                    CorporatePerformance.selectPasts(c, 1);
            CorporatePerformance tcp1 = pasts.get("1001");
            CorporatePerformance tcp2 = pasts.get("1002");
            assertEquals(tcp1, cp12);
            assertEquals(tcp2, null);
            CorporatePerformance bcp1 = CorporatePerformance.selectPastByStockId(1001, 1, c);
            assertEquals(bcp1, cp12);
            CorporatePerformance bcp3 = CorporatePerformance.selectPastByStockId(1003, 1, c);
            assertEquals(bcp3, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void tearDown() {
        try {
            Database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
