package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-09-17

public class GedcomEventize implements Gedcom.Processor {
    private final GedcomEventizeOptions options;


    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomEventizeOptions options = new ArgParser<>(new GedcomEventizeOptions()).parse(args).verify();
        new Gedcom(options, new GedcomEventize(options)).main();
        System.out.flush();
        System.err.flush();
    }


    private GedcomEventize(final GedcomEventizeOptions options) {
        this.options = options;
    }


    @Override
    public boolean process(final GedcomTree tree) {
        this.options.ref.forEach(tree, this::moveValueToNote);
        if (this.options.typeEvent != null) {
            this.options.ref.forEach(tree, this::convertToEvent);
        }
        return true;
    }

    private void moveValueToNote(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        final String v = line.getValue();
        if (v.isEmpty()) {
            return;
        }

        node.addChild(new TreeNode<>(line.createChild(GedcomTag.NOTE, v)));
        node.setObject(line.replaceValue(""));
    }

    private void convertToEvent(final TreeNode<GedcomLine> node) {
        final GedcomLine line = node.getObject();
        node.addChild(new TreeNode<>(line.createChild(GedcomTag.TYPE, this.options.typeEvent)));
        node.setObject(GedcomLine.createEmpty(line.getLevel(), GedcomTag.EVEN));
    }
}
