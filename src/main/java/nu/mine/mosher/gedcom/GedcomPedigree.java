package nu.mine.mosher.gedcom;

import com.google.common.primitives.UnsignedInteger;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;
import nu.mine.mosher.asciigraphics.AsciiGraphics;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.gedcom.model.Loader;
import nu.mine.mosher.gedcom.model.Person;

import static nu.mine.mosher.asciigraphics.Coords.xy;
import static nu.mine.mosher.asciigraphics.Grapheme.grs;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GedcomPedigree {
    public static void main(final String... args) throws IOException, InvalidLevel {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: gedcom-pedigree generation-count [input.ged  [@INDI@]]");
        }

        final Optional<Person> root;
        if (args.length == 1) {
            root = Optional.empty();
        } else {
            final Path pathInput = Paths.get(args[1]).toRealPath();
            final GedcomTree tree = Gedcom.readFile(new BufferedInputStream(Files.newInputStream(pathInput)));
            new GedcomConcatenator(tree).concatenate();
            final Loader loader = new Loader(tree, pathInput.toString());
            loader.parse();
            if (args.length == 2) {
                root = Optional.of(loader.getFirstPerson());
            } else {
                final String id = args[2].replaceAll("@", "");
                root = Optional.of(loader.lookUpPerson(tree.getNode(id)));
            }
        }

        run(root, UnsignedInteger.valueOf(args[0]));

        System.out.flush();
    }

    private static void run(final Optional<Person> root, final UnsignedInteger gens) {
        if (gens.compareTo(UnsignedInteger.valueOf(2)) < 0) {
            throw new IllegalArgumentException("must have at least 2 generations");
        }

        final int g = gens.intValue() - 1;
        final int h = 1 << (g + 1);

        int y = (h >> 1) - 1;
        int dy = 1 << (g - 1);

        final int len = root.map(person -> maxlen(person, 0, g)).orElse(25);

        final AsciiGraphics gr = new AsciiGraphics(UnsignedInteger.valueOf((g + 1) * (len + 2) - 1), UnsignedInteger.valueOf(h - 1));

        genx(0, y, dy, root, len + 2, gr);



        gr.print(new PrintWriter(System.out, true));
    }

    private static int maxlen(Person p, int m, final int g) {
        final int n = grs(p.getNameSortedDisplay()).size();
        if (n > m) {
            m = n;
        }
        if (0 < g) {
            Person x;
            x = p.getFather();
            if (Objects.nonNull(x)) {
                m = maxlen(x, m, g - 1);
            }
            x = p.getMother();
            if (Objects.nonNull(x)) {
                m = maxlen(x, m, g - 1);
            }
        }
        return m;
    }

    private static void genx(final int g, final int y, final int dy, final Optional<Person> person, int nlen, final AsciiGraphics gr) {
        final UnsignedInteger x = UnsignedInteger.valueOf(g * nlen);
        gr.hLine(xy(x, UnsignedInteger.valueOf(y)), nlen - 1, '\u2500');
        person.ifPresent(p -> gr.hString(xy(x, UnsignedInteger.valueOf(y)), grs(p.getNameSortedDisplay())));

        if (0 < dy) {
            geny(g, y, dy, person.map(Person::getFather), nlen, gr, x, '\u250C', -1);
            geny(g, y, dy, person.map(Person::getMother), nlen, gr, x, '\u2514', +1);
        }
    }

    private static void geny(final int g, final int y, final int dy, final Optional<Person> person, final int nlen, final AsciiGraphics gr, final UnsignedInteger x, final char end, final int sgn) {
        gr.vLine(xy(x.plus(UnsignedInteger.valueOf(nlen - 1)), UnsignedInteger.valueOf(y)), sgn * (dy + 1), '\u2502', '\u2524', end);
        genx(g + 1, y + sgn * dy, dy >> 1, person, nlen, gr);
    }
}
