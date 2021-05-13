package nu.mine.mosher.gedcom;

import java.util.regex.Matcher;

import static nu.mine.mosher.gedcom.GedcomDateFixerData.*;

class GedcomDateFixer {
    private final String value;

    public GedcomDateFixer(final String value) {
        this.value = fix(value);
    }

    public String get() {
        return this.value;
    }





    private static class DateYM {
        String s;
        int y;
        int m;
    }

    private static String fix(String v) {
        Matcher matcher;

        v = v.toUpperCase().trim();

        if (v.contains("#D")) {
            v = v.replaceAll("@+#DJULIAN@+ +", "");
        }

        if ((matcher = DATE_BET.matcher(v)).matches()) {
            final DateYM d2 = fixSingleDate(matcher.group(2));
            final DateYM d1 = fixSingleDate(matcher.group(1), d2.y, d2.m);
            if (d1.s.equals("0")) {
                v = "BEF " + d2.s;
            } else if (d2.s.equals("0")) {
                v = "AFT " + d1.s;
            } else {
                v = "BET " + d1.s + " AND " + d2.s;
            }
        } else if ((matcher = DATE_BEF.matcher(v)).matches()) {
            final DateYM d = fixSingleDate(matcher.group(1));
            v = "BEF " + d.s;
        } else if ((matcher = DATE_AFT.matcher(v)).matches()) {
            final DateYM d = fixSingleDate(matcher.group(1));
            v = "AFT " + d.s;
        } else if ((matcher = DATE_FROMTO.matcher(v)).matches()) {
            final DateYM d2 = fixSingleDate(matcher.group(2));
            final DateYM d1 = fixSingleDate(matcher.group(1), d2.y, d2.m);
            if (d1.s.equals("0")) {
                v = "TO " + d2.s;
            } else if (d2.s.equals("0")) {
                v = "FROM " + d1.s;
            } else {
                v = "FROM " + d1.s + " TO " + d2.s;
            }
        } else if ((matcher = DATE_TO.matcher(v)).matches()) {
            final DateYM d = fixSingleDate(matcher.group(1));
            v = "TO " + d.s;
        } else if ((matcher = DATE_FROM.matcher(v)).matches()) {
            final DateYM d = fixSingleDate(matcher.group(1));
            v = "FROM " + d.s;
        } else if ((matcher = DATE_ABT.matcher(v)).matches()) {
            final DateYM d = fixSingleDate(matcher.group(1));
            v = "ABT " + d.s;
        } else if ((matcher = DATE_Y_TO_Y.matcher(v)).matches()) {
            final DateYM d1 = fixSingleDate(matcher.group(1));
            final DateYM d2 = fixSingleDate(matcher.group(2));
            v = "FROM " + d1.s + " TO " + d2.s;
        } else {
            final DateYM d = fixSingleDate(v);
            if (!d.s.equals("0")) {
                v = d.s;
            }
        }

        return v;
    }

    private static DateYM fixSingleDate(final String date) {
        return fixSingleDate(date, 0, 0);
    }

    private static DateYM fixSingleDate(final String date, int hintYear, int hintMonth) {
        int year = 0, month = 0, day = 0;

        Matcher m;

        if ((m = DATE_SLASHES.matcher(date)).matches()) {
            int g1 = Integer.parseInt(m.group(1));
            int g2 = Integer.parseInt(m.group(2));
            int g3 = Integer.parseInt(m.group(3));
            /* y/m/d,  m/d/y,  or  d/m/y */
            if (g1 >= 31) {
                year = g1;
                month = g2;
                day = g3;
            } else if (g1 > 12) {
                day = g1;
                month = g2;
                year = g3;
            } else {
                /* TODO: warn if g2 <= 12 */
                month = g1;
                day = g2;
                year = g3;
            }
        } else if ((m = DATE_DMY.matcher(date)).matches()) {
            day = Integer.parseInt(m.group(1));
            month = fixMonth(m.group(2));
            year = Integer.parseInt(m.group(3));
        } else if ((m = DATE_MY.matcher(date)).matches()) {
            month = fixMonth(m.group(1));
            year = Integer.parseInt(m.group(2));
        } else if ((m = DATE_M.matcher(date)).matches()) {
            month = fixMonth(m.group(1));
        } else if ((m = DATE_DM.matcher(date)).matches()) {
            day = Integer.parseInt(m.group(1));
            month = fixMonth(m.group(2));
        } else if ((m = DATE_Y_OR_D.matcher(date)).matches()) {
            int x = Integer.parseInt(m.group(1));
            if (x <= 31) {
                day = x;
            } else {
                year = x;
                hintMonth = 0;
            }
        } else {
            hintMonth = 0;
            hintYear = 0;
        }

        if (year == 0) {
            year = hintYear;
        }
        if (month == 0) {
            month = hintMonth;
        }

        DateYM ret = new DateYM();
        if (month > 0 && day > 0) {
            ret.s = String.format("%02d %s %d", day, monthName[month], year);
        } else if (month > 0) {
            ret.s = String.format("%s %d", monthName[month], year);
        } else {
            ret.s = String.format("%d", year);
        }
        ret.m = month;
        ret.y = year;
        return ret;
    }

    private static int fixMonth(final String month) {
        final Integer m = mapMonthNameToNumber.get(month);
        if (m == null) {
            /* TODO: warning invalid month name */
            return 0;
        }
        return m;
    }
}
