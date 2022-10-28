package nu.mine.mosher.asciigraphics;

import org.junit.jupiter.api.*;

import java.text.Normalizer;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GraphemeTest {
    private static final String UTF16_DATA = "ḱṷ \ud83d\ude00 ṓn  \u0628\u064e\u064a\u0652\u067a\u064f x\ufeffy a\u200bb fo\u0002o";

    @Test
    void precomposed() {
        System.out.println(UTF16_DATA);
        dumpStringElements(UTF16_DATA);
        final ArrayList<Grapheme> uut = Grapheme.grs(UTF16_DATA);
        showGraphemes(uut);
        assertEquals(22, uut.size());
    }

    @Test
    @Disabled("Failing on Windows")
    void combining() {
        final String normalized = Normalizer.normalize(UTF16_DATA, Normalizer.Form.NFD);
        System.out.println(normalized);
        dumpStringElements(normalized);
        final ArrayList<Grapheme> uut = Grapheme.grs(UTF16_DATA);
        showGraphemes(uut);
        assertEquals(22, uut.size());
    }



    private static void dumpStringElements(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            final int ic = (int)c;
            final int charType = Character.getType(c);
            System.out.print("U+"+Integer.toHexString(ic).toUpperCase()+"["+charType+"]  ");
        }
        System.out.println();
    }

    private void showGraphemes(final ArrayList<Grapheme> graphemes) {
        graphemes.forEach(g -> System.out.println("--->"+g+"\u2592"));
        System.out.println("---------------------");
    }
}
