package nu.mine.mosher.gedcom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class GedcomDateFixerData {
    static final Map<String, Integer> mapMonthNameToNumber = Collections.unmodifiableMap(new HashMap<String, Integer>(24, 1) {{
        put("JAN", 1);
        put("FEB", 2);
        put("MAR", 3);
        put("APR", 4);
        put("MAY", 5);
        put("JUN", 6);
        put("JUL", 7);
        put("AUG", 8);
        put("SEP", 9);
        put("OCT", 10);
        put("NOV", 11);
        put("DEC", 12);
        put("JANUARY", 1);
        put("FEBRUARY", 2);
        put("MARCH", 3);
        put("APRIL", 4);
        put("JUNE", 6);
        put("JULY", 7);
        put("AUGUST", 8);
        put("SEPTEMBER", 9);
        put("OCTOBER", 10);
        put("NOVEMBER", 11);
        put("DECEMBER", 12);
    }});

    static final String[] monthName = {
        "UNKNOWN_MONTH", "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", };
    static final Pattern DATE_SLASHES = Pattern.compile("([0-9]+)/([0-9]+)/([0-9]+)");
    static final Pattern DATE_DMY = Pattern.compile("([0-9]+) ([A-Za-z]+) ([0-9]+)");
    static final Pattern DATE_MY = Pattern.compile("([A-Za-z]+) ([0-9]+)");
    static final Pattern DATE_M = Pattern.compile("([A-Za-z]+)");
    static final Pattern DATE_DM = Pattern.compile("([0-9]+) ([A-Za-z]+)");
    static final Pattern DATE_Y_OR_D = Pattern.compile("([0-9]+)");
    static final Pattern DATE_BET = Pattern.compile("(?:BET|BTW)\\.? (.*)(?:\u2013|-| AND )(.*)");
    static final Pattern DATE_BEF = Pattern.compile("(?:BEF\\.?|BEFORE) (.*)");
    static final Pattern DATE_AFT = Pattern.compile("(?:AFT\\.?|AFTER) (.*)");
    static final Pattern DATE_ABT = Pattern.compile("(?:ABT|C)\\.? (.*)");
    static final Pattern DATE_FROMTO = Pattern.compile("FROM (.*) TO (.*)");
    static final Pattern DATE_FROM = Pattern.compile("FROM (.*)");
    static final Pattern DATE_TO = Pattern.compile("TO (.*)");
    static final Pattern DATE_Y_TO_Y = Pattern.compile("([0-9]+)(?:[-\u2013])([0-9]+)");
}
