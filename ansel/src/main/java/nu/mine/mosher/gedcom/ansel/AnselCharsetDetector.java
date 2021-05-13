package nu.mine.mosher.gedcom.ansel;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class AnselCharsetDetector {
    private static final Set<Integer> forbidden = ((Supplier<Set<Integer>>)() -> {
        final Set<Integer> s = new HashSet<>();
        for (int c = 0x80; c <= 0x87; ++c) {
            s.add(c);
        }
        for (int c = 0x90; c <= 0x9F; ++c) {
            s.add(c);
        }
        s.add(0xA0);
        s.add(0xAF);
        for (int c = 0xC9; c <= 0xCC; ++c) {
            s.add(c);
        }
        for (int c = 0xD0; c <= 0xDF; ++c) {
            s.add(c);
        }
        return Collections.unmodifiableSet(s);
    }).get();

    private static final Set<Integer> strong = ((Supplier<Set<Integer>>)() -> {
        final Set<Integer> s = new HashSet<>();
        // high prob. ANSEL, low prob. Win-1252
        s.add(0xA1);
        s.add(0xA2);
        s.add(0xA6);
        s.add(0xB1);
        s.add(0xB2);
        s.add(0xB3);
        s.add(0xB9);
        s.add(0xBA);
        return Collections.unmodifiableSet(s);
    }).get();

    private final Map<Integer, Integer> mapForbidden = new TreeMap<>();
    private int cForbidden;
    private final Map<Integer, Integer> mapAnsel = new TreeMap<>();
    private final Map<Integer, Integer> mapStrongAnsel = new TreeMap<>();
    private int cAnsel;

    private int cAscii;
    private int cTotal;

    public void handleData(final BufferedInputStream stream) throws IOException {
        handleData(stream, Integer.MAX_VALUE);
    }

    private static final int EOF = -1;

    public void handleData(final BufferedInputStream stream, int len) throws IOException {
        while (0 < len--) {
            final int b = stream.read();
            if (b == EOF) {
                len = 0;
            } else {
                ++this.cTotal;
                if (b < 0x80) {
                    ++this.cAscii;
                } else if (AnselCharacterMap.map.containsKey(b)) {
                    this.mapAnsel.merge(b, 1, Integer::sum);
                    ++this.cAnsel;
                    if (strong.contains(b)) {
                        this.mapStrongAnsel.merge(b, 1, Integer::sum);
                    }
                } else if (forbidden.contains(b)) {
                    this.mapForbidden.merge(b, 1, Integer::sum);
                    ++this.cForbidden;
                }
            }
        }
    }

    public boolean detected() {
        return
            this.cForbidden <= 0 && (
                1 <= this.mapStrongAnsel.size() ||
                2 <= this.mapAnsel.size());
    }

    public int getForbiddenFound() {
        return this.cForbidden;
    }

    private int getForbiddenValuesFound() {
        return this.mapForbidden.size();
    }

    public int getAnselFound() {
        return this.cAnsel;
    }

    public int getAnselValuesFound() {
        return this.mapAnsel.size();
    }

    public int getStrongAnselValuesFound() {
        return this.mapStrongAnsel.size();
    }

    public int getAsciiFound() {
        return this.cAscii;
    }

    public int getTotalBytes() {
        return this.cTotal;
    }

    public static void main(final String... args) throws IOException {
        if (args.length <= 0) {
            System.err.println("missing file name argument");
            System.exit(1);
        }

        for (final String arg : args) {
            final File f = new File(arg).getCanonicalFile();
            if (f.isFile()) {
                final BufferedInputStream s = new BufferedInputStream(new FileInputStream(f));

                final AnselCharsetDetector detector = new AnselCharsetDetector();
                detector.handleData(s);
                System.out.println(
                    String.format("%s: detected=%b, forbidden=%d/%d, indicative=%d/%d+/%d, ascii=%d, read=%d",
                        f,
                        detector.detected(),
                        detector.getForbiddenValuesFound(),
                        detector.getForbiddenFound(),
                        detector.getAnselValuesFound(),
                        detector.getStrongAnselValuesFound(),
                        detector.getAnselFound(),
                        detector.getAsciiFound(),
                        detector.getTotalBytes()));
            }
        }
    }
}
