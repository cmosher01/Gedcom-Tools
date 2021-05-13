package nu.mine.mosher.asciigraphics;

import com.google.common.collect.ArrayTable;
import com.google.common.primitives.UnsignedInteger;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.IntStream;

import static nu.mine.mosher.asciigraphics.Grapheme.gr;

public class AsciiGraphics {
    private static final Grapheme SPACE = gr('\u0020');



    private final ArrayTable<Integer, Integer, Grapheme> canvas;



    private void put(final Grapheme g, final Coords at) {
        this.canvas.set(at.y().intValue(), at.x().intValue(), g);
    }

    private Grapheme at(final int x, final int y) {
        return Optional.ofNullable(this.canvas.at(y, x)).orElse(SPACE);
    }

    private static Iterable<Integer> range(final int n) {
        return () -> IntStream.range(0, n).iterator();
    }




    public AsciiGraphics(final UnsignedInteger width, final UnsignedInteger height) {
        this.canvas = ArrayTable.create(range(height.intValue()), range(width.intValue()));
    }

    public void hString(final Coords at, final List<Grapheme> s) {
        Coords a = at;
        for (final Grapheme g : s) {
            put(g, a);
            a = a.r();
        }
    }

    public void hLine(final Coords at, final int length, final char c) {
        hLine(at, length, c, c, c);
    }

    public void hLine(final Coords at, final int length, final char c, final char start, final char end) {
        // noinspection StatementWithEmptyBody
        if (length == 0) {
        } else if (length == 1 || length == -1) {
            put(gr(c), at);
        } else {
            put(gr(start), at);
            Coords a = at.h(length);
            for (int i = 0; i < Math.abs(length)-2; ++i) {
                put(gr(c), a);
                a = a.h(length);
            }
            put(gr(end), a);
        }
    }

    public void vLine(final Coords at, final int length, final char c) {
        vLine(at, length, c, c, c);
    }

    public void vLine(final Coords at, final int length, final char c, final char start, final char end) {
        // noinspection StatementWithEmptyBody
        if (length == 0) {
        } else if (length == 1 || length == -1) {
            put(gr(c), at);
        } else {
            put(gr(start), at);
            Coords a = at.v(length);
            for (int i = 0; i < Math.abs(length)-2; ++i) {
                put(gr(c), a);
                a = a.v(length);
            }
            put(gr(end), a);
        }
    }

    public void debugPrint(final PrintWriter p) {
        p.print(" ");
        for (int c = 0; c < this.canvas.columnKeyList().size(); ++c) {
            p.print(""+(c % 10));
        }
        p.println();

        final char TOMBSTONE = '\u220e';
        for (int r = 0; r < this.canvas.rowKeyList().size(); ++r) {
            p.print(""+(r % 10));
            for (int c = 0; c < this.canvas.columnKeyList().size(); ++c) {
                p.print(at(c, r));
            }
            p.println(TOMBSTONE);
        }
        p.println(TOMBSTONE);
    }

    public void print(final PrintWriter p) {
        for (int r = 0; r < this.canvas.rowKeyList().size(); ++r) {
            for (int c = 0; c < this.canvas.columnKeyList().size(); ++c) {
                p.print(at(c, r));
            }
            p.println();
        }
    }
}
