package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.date.DatePeriod;
import nu.mine.mosher.gedcom.date.DateRange;
import nu.mine.mosher.gedcom.date.parser.GedcomDateValueParser;
import nu.mine.mosher.gedcom.date.parser.ParseException;
import nu.mine.mosher.gedcom.date.parser.TokenMgrError;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.io.StringReader;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-09-16

public class GedcomFixDate implements Gedcom.Processor {
    private final GedcomFixDateOptions options;



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomFixDateOptions options = new ArgParser<>(new GedcomFixDateOptions()).parse(args).verify();
        new Gedcom(options, new GedcomFixDate(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomFixDate(final GedcomFixDateOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        tree.getRoot().forAll(this::processNode);
        return true;
    }

    private void processNode(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        if (line != null && line.getTag().equals(GedcomTag.DATE)) {
            fixDateIfNeeded(node);
        }
    }

    private void fixDateIfNeeded(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        final String valueOriginal = line.getValue();
        final String valueNew = new GedcomDateFixer(valueOriginal).get();
        if (isValid(valueNew)) {
            if (!valueNew.equals(valueOriginal)) {
                node.setObject(line.replaceValue(valueNew));
            }
        } else {
            System.err.println("Unrecognized date string: \"" + valueOriginal + "\" (" + valueNew + ")");
        }
    }



    public static boolean isValid(final String sDate) {
        return verify(sDate) != null;
    }

    public static DatePeriod verify(final String sDate) {
        DatePeriod date = null;
        try {
            date = parse(sDate);
        } catch (final ParseException | DateRange.DatesOutOfOrder | TokenMgrError e) {
            date = null;
        }
        return date;
    }

    public static DatePeriod parse(final String sDate) throws DateRange.DatesOutOfOrder, ParseException {
        return new GedcomDateValueParser(new StringReader(sDate)).parse();
    }
}
