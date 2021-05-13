package nu.mine.mosher.gedcom;

import java.util.*;

public final class TagOrderMaps {
    private TagOrderMaps() {
        throw  new IllegalStateException("never instantiated");
    }

    public static final Map<GedcomTag, Integer> mapTopLevelOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.HEAD, i++);
        put(GedcomTag.SUBN, i++);
        put(GedcomTag.SUBM, i++);

        put(GedcomTag.INDI, i++);
        put(GedcomTag.FAM, i++);

        put(GedcomTag.REPO, i++);
        put(GedcomTag.SOUR, i++);

        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);

        put(GedcomTag.TRLR, i++);
    }});

    public static final Map<GedcomTag, Integer> mapHeadOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.CHAR, i++);
        put(GedcomTag.GEDC, i++);

        put(GedcomTag.COPR, i++);
        put(GedcomTag.SUBM, i++);

        put(GedcomTag.SUBN, i++);
        put(GedcomTag.SOUR, i++);
        put(GedcomTag.DEST, i++);

        put(GedcomTag.FILE, i++);
        put(GedcomTag.DATE, i++);

        put(GedcomTag.LANG, i++);
        put(GedcomTag.PLAC, i++);
        put(GedcomTag.NOTE, i++);
    }});

    public static final Map<GedcomTag, Integer> mapEventOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.TYPE, i++);
        put(GedcomTag.DATE, i++);
        put(GedcomTag.PLAC, i++);
        put(GedcomTag.ADDR, i++);
        put(GedcomTag.PHON, i++);
        put(GedcomTag.AGE, i++);
        put(GedcomTag.AGNC, i++);
        put(GedcomTag.CAUS, i++);
        put(GedcomTag.SOUR, i++);
        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);
    }});

    public static final Map<GedcomTag, Integer> mapIndiOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.REFN, i++);
        put(GedcomTag.RIN, i++);
        put(GedcomTag.CHAN, i++);

        put(GedcomTag.RFN, i++);
        put(GedcomTag.AFN, i++);
        put(GedcomTag.RESN, i++);

        put(GedcomTag.NAME, i++);
        put(GedcomTag.ALIA, i++);
        put(GedcomTag.SEX, i++);
        put(GedcomTag.FAMC, i++);
        put(GedcomTag.FAMS, i++);
        put(GedcomTag.ASSO, i++);
        put(GedcomTag.DESI, i++);
        put(GedcomTag.ANCI, i++);

        put(GedcomTag.SOUR, i++);
        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);
        put(GedcomTag.SUBM, i++);
    }});

    public static final Integer NON_PLACEABLE_EVENT;
    public static final Map<GedcomTag, Integer> mapPlaceableEventOrder;
    static {
        final Map<GedcomTag, Integer> m = new HashMap<>();
        int i = 0;
        m.put(GedcomTag.BIRT, i++);
        m.put(GedcomTag.CHR, i++);
        m.put(GedcomTag.BAPM, i++);
        NON_PLACEABLE_EVENT = i++;
        m.put(GedcomTag.WILL, i++);
        m.put(GedcomTag.DEAT, i++);
        m.put(GedcomTag.CREM, i++);
        m.put(GedcomTag.BURI, i++);
        m.put(GedcomTag.PROB, i++);
        mapPlaceableEventOrder = Collections.unmodifiableMap(m);
    }

    public static final Map<GedcomTag, Integer> mapFamOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.REFN, i++);
        put(GedcomTag.RIN, i++);
        put(GedcomTag.CHAN, i++);

        put(GedcomTag.HUSB, i++);
        put(GedcomTag.WIFE, i++);
        put(GedcomTag.NCHI, i++);
        put(GedcomTag.CHIL, i++);

        put(GedcomTag.SOUR, i++);
        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);
        put(GedcomTag.SUBM, i++);
    }});

    public static final Map<GedcomTag, Integer> mapSourOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.REFN, i++);
        put(GedcomTag.RIN, i++);
        put(GedcomTag.CHAN, i++);

        put(GedcomTag.REPO, i++);

        put(GedcomTag.TITL, i++);
        put(GedcomTag.AUTH, i++);
        put(GedcomTag.PUBL, i++);
        put(GedcomTag.ABBR, i++);

        put(GedcomTag.DATA, i++);
        put(GedcomTag.TEXT, i++);

        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);
    }});

    public static final Map<GedcomTag, Integer> mapCitationOrder = Collections.unmodifiableMap(new HashMap<GedcomTag, Integer>() {{
        int i = 0;
        put(GedcomTag.PAGE, i++);
        put(GedcomTag.QUAY, i++);
        put(GedcomTag.EVEN, i++);
        put(GedcomTag.DATA, i++);
        put(GedcomTag.OBJE, i++);
        put(GedcomTag.NOTE, i++);
    }});

    /*
    Multiple BIRT records represent (not multiple births, but) conflicting assertions of the birth
    event, in priority order.

    GEDCOM 5.5.1, p. 20:
    "The occurrence of equal level numbers and equal tags within the
    same context imply that multiple opinions or multiple values of the data exist. The significance of
    the order in these cases is interpreted as the submitter's preference. The most preferred value
    being the first with the least preferred data listed in subsequent lines by order of decreasing
    preference. For example, a researcher who discovers conflicting evidence about a person's birth
    event would list the most credible information first and the least credible or least preferred items
    last."

    But that's not true for other events such as RESI or CENS, for example. Two CENS records usually
    imply two actual census events, not two opinions of one census event.
     */
    public static final Set<GedcomTag> setDoNotSortEvents = Collections.unmodifiableSet(new HashSet<GedcomTag>() {{
        add(GedcomTag.BIRT);
        add(GedcomTag.BAPM);
        add(GedcomTag.CHR);
        add(GedcomTag.BARM);
        add(GedcomTag.BASM);
        add(GedcomTag.DEAT);
        add(GedcomTag.CREM);
        add(GedcomTag.BURI);
        add(GedcomTag.NAME);
    }});
}
