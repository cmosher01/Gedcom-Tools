package nu.mine.mosher.asciigraphics;

import java.io.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.valueOf;
import static nu.mine.mosher.asciigraphics.Coords.ORIGIN;
import static nu.mine.mosher.asciigraphics.Coords.xy;
import static nu.mine.mosher.asciigraphics.Grapheme.grs;

public class AsciiGraphicsTest {
    AsciiGraphics uut;

    @BeforeEach
    void beforeEach() {
        uut = new AsciiGraphics(valueOf(10), valueOf(10));
    }

    @Test
    void nominal() {
        uut.hString(xy(valueOf(2), valueOf(3)), grs("Lo\u81CCpez"));
        show(uut);
    }

    @Test
    void at_0_0() {
        uut.hString(ORIGIN, grs("Pazmin\u83CCo"));
        show(uut);
    }

    @Test
    void emptyString() {
        uut.hString(xy(valueOf(89), valueOf(99)), grs(""));
        show(uut);
    }

    @Test
    void nominalHLine() {
        uut.hLine(xy(valueOf(3), ONE), 5, '-');
        show(uut);
    }

    @Test
    void nominalHLineSE() {
        uut.hLine(xy(valueOf(3), ONE), 5, '\u2500', '\u251c', '\u2524');
        show(uut);
    }

    @Test
    void nominalVLine() {
        uut.vLine(xy(valueOf(3), ONE), 5, '|');
        show(uut);
    }

    @Test
    void nominalVLineSE() {
        uut.vLine(xy(valueOf(3), ONE), 5, '\u2502', '\u252C', '\u2534');
        show(uut);
    }



    private static void show(AsciiGraphics uut) {
        final PrintWriter out = new PrintWriter(System.out);
        uut.debugPrint(out);
        out.flush();
    }
}
