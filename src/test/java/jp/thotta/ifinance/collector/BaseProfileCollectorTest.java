package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseProfileCollectorTest extends TestCase {
    List<Integer> stockIdList = new ArrayList<Integer>();
    BaseProfileCollectorImpl coll;
    Connection c;

    protected void setUp() {
        stockIdList.add(8060);
        stockIdList.add(5757);
        stockIdList.add(1333);
        stockIdList.add(3226);
        coll = new BaseProfileCollectorImpl(stockIdList);
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            CompanyProfile.dropTable(c);
            CompanyProfile.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testParseCompanyProfile() {
        CompanyProfile profile = coll.parseCompanyProfile(5757);
        assertTrue(profile.shareUnitNumber > 0);
        assertTrue(profile.independentEmployee > 0);
        assertEquals(profile.consolidateEmployee, null);
        assertTrue(profile.averageAge > 0);
        profile = coll.parseCompanyProfile(3226);
        System.out.println(profile);
    }

    public void testAppend() {
        Map<String, CompanyProfile> m = new HashMap<String, CompanyProfile>();
        try {
            coll.append(m);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        assertTrue(m.size() > 0);
        for (String k : m.keySet()) {
            System.out.println(k + ": " + m.get(k));
        }
    }

    public void testAppendDb() {
        try {
            coll.appendDb(c);
            Map<String, CompanyProfile> m = CompanyProfile.selectAll(c);
            Map<String, CompanyProfile> m2 = new HashMap<String, CompanyProfile>();
            coll.append(m2);
            for (String k : m.keySet()) {
                assertEquals(m2.get(k), m.get(k));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
