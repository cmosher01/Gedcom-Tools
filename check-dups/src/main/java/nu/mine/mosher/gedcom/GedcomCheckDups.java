package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.logging.Jul;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.sql.CallableStatement;
import java.util.*;

// Created by Christopher Alan Mosher on 2020-06-12

public class GedcomCheckDups implements Gedcom.Processor {
    private final GedcomCheckDupsOptions options;


    public static void main(final String... args) throws InvalidLevel, IOException {
        Jul.log();
        final GedcomCheckDupsOptions options = new ArgParser<>(new GedcomCheckDupsOptions()).parse(args).verify();
        new Gedcom(options, new GedcomCheckDups(options)).main();
        System.out.flush();
        System.err.flush();
    }


    private GedcomCheckDups(final GedcomCheckDupsOptions options) {
        this.options = options;
    }


    @Override
    public boolean process(final GedcomTree tree) {
        final Map<String, Set<TreeNode<GedcomLine>>> map = new HashMap<>();
        this.options.ref.forEach(tree, n -> {
            final String value = n.getObject().getValue();
            final Set<TreeNode<GedcomLine>> nodes = map.computeIfAbsent(value, k -> new HashSet<>());
            nodes.add(n);
        });

        map.values().stream().filter(s -> 1 < s.size()).forEach(GedcomCheckDups::report);
        return false;
    }

    private static void report(final Set<TreeNode<GedcomLine>> nodes) {
        nodes.forEach(node -> Jul.log().warning(findContainingRecord(node).getObject() + ": " + node.getObject()));
    }

    private static TreeNode<GedcomLine> findContainingRecord(final TreeNode<GedcomLine> n) {
        if (n.parent().parent() == null) {
            return n;
        }
        return findContainingRecord(n.parent());
    }
}
