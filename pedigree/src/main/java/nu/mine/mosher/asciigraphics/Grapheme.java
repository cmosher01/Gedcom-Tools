package nu.mine.mosher.asciigraphics;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import static java.lang.Character.CONTROL;
import static java.lang.Character.FORMAT;
import static java.lang.Character.getType;

public class Grapheme {
    private final String cps;

    private Grapheme(final String codepointsOfOneGrapheme) {
        this.cps = filter(codepointsOfOneGrapheme);
    }

    private static String filter(final String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        s
            .codePoints()
            .filter(c -> !(getType(c) == FORMAT || getType(c) == CONTROL))
            .forEach(sb::appendCodePoint);
        return sb.toString();
    }

    private static void addGraphemes(final String s, final BreakIterator i, final Collection<Grapheme> gs) {
        int beg = i.first();
        int end = i.next();
        while (end != BreakIterator.DONE) {
            final Grapheme g = new Grapheme(s.substring(beg, end));
            if (0 < g.countOfCodePoints()) {
                gs.add(g);
            }
            beg = end;
            end = i.next();
        }
    }

    private int countOfCodePoints() {
        return this.cps.length();
    }



    /**
     * smart factory
     * @param s
     * @return
     */
    public static ArrayList<Grapheme> grs(final String s) {
        final ArrayList<Grapheme> gs = new ArrayList<>(s.length());

        final BreakIterator i = BreakIterator.getCharacterInstance(Locale.ENGLISH);
        i.setText(s);

        addGraphemes(s, i, gs);

        gs.trimToSize();
        return gs;
    }

    /**
     * dumb factory
     * @param c
     * @return
     */
    public static Grapheme gr(final char c) {
        return new Grapheme(""+c);
    }

    @Override
    public String toString() {
        return this.cps;
    }
}
