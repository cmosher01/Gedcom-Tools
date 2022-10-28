package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.util.*;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-09-08

public class GedcomFixFtmPubl implements Gedcom.Processor {
    private final GedcomFixFtmPublOptions options;



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomFixFtmPublOptions options = new ArgParser<>(new GedcomFixFtmPublOptions()).parse(args).verify();
        new Gedcom(options, new GedcomFixFtmPubl(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomFixFtmPubl(final GedcomFixFtmPublOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        try {
            final GedcomDataRef ref = new GedcomDataRef(".SOUR.PUBL\"Name: .+;\"");
            ref.forEach(tree, this::processLine);
        } catch (GedcomDataRef.InvalidSyntax invalidSyntax) {
            throw new IllegalStateException(invalidSyntax);
        }
        return true;
    }

    private void processLine(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        final String v = line.getValue().replaceFirst("Name: (.+);", "$1");
        node.setObject(line.replaceValue(v));
    }
}
