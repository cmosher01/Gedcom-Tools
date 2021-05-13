package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.gedcom.model.*;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.util.*;

import static nu.mine.mosher.gedcom.GedcomTag.*;
import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2019-09-26

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GedcomRedact implements Gedcom.Processor {
    private final GedcomRedactOptions options;
    private GedcomTree tree;
    private Loader model;
    private final List<TreeNode<GedcomLine>> deletes = new ArrayList<>(256);
    private final List<TreeNode<GedcomLine>> privateRecords = new ArrayList<>(256);

    public static void main(final String... args) throws InvalidLevel, IOException {
        final GedcomRedactOptions options = new ArgParser<>(new GedcomRedactOptions()).parse(args).verify();
        new Gedcom(options, new GedcomRedact(options)).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomRedact(final GedcomRedactOptions options) {
        this.options = options;
    }

    @Override
    public boolean process(final GedcomTree tree) {
        this.tree = tree;

        loadTreeModel();

        markIndisAndFams();
        redactPrivateRecords();
//        TODO:
//        markEvent();
//        redactPrivateEvents();
        deleteFlaggedNodes();

        return true;
    }

    private void loadTreeModel() {
        this.model = new Loader(this.tree, "");
        this.model.parse();
    }

    private void markIndisAndFams() {
        final TreeNode<GedcomLine> root = this.tree.getRoot();
        final ListIterator<TreeNode<GedcomLine>> c = root.childrenList();
        while (c.hasNext()) {
            final TreeNode<GedcomLine> cnode = c.next();
            final GedcomLine ln = cnode.getObject();
            boolean prv = false;
            switch (ln.getTag()) {
                case INDI:
                    prv = isPrivateIndi(cnode);
                    break;
                case FAM:
                    prv = isPrivateFam(cnode);
            }
            if (prv) {
                this.privateRecords.add(cnode);
            }
        }
    }

    private boolean isPrivateIndi(final TreeNode<GedcomLine> indi) {
        final Person person = this.model.lookUpPerson(indi);
        if (Objects.isNull(person)) {
            log().severe("Cannot find person for "+indi.getObject().getID());
            return false;
        }
        return person.isPrivate();
    }

    private boolean isPrivateFam(final TreeNode<GedcomLine> fam) {
        final Loader.Partnerships partnerships = this.model.lookUpFamily(fam);
        if (Objects.isNull(partnerships)) {
            log().severe("Cannot find family for "+fam.getObject().getID());
            return false;
        }
        return isPrivatePartnership(partnerships.husb) || isPrivatePartnership(partnerships.wife);
    }

    private boolean isPrivatePartnership(Optional<Partnership> partnership) {
        return partnership.isPresent() && partnership.get().isPrivate();
    }

    private void redactPrivateRecords() {
        this.privateRecords.forEach(this::redactPrivateRecord);
    }

    private static final Set<GedcomTag> KEEP = Set.of(FAMC, FAMS, HUSB, WIFE, CHIL);

    private void redactPrivateRecord(final TreeNode<GedcomLine> rec) {
        for (final TreeNode<GedcomLine> item : rec) {
            if (NAME.equals(item.getObject().getTag())) {
                item.setObject(item.getObject().replaceValue("[REDACTED]"));
                item.forEach(this.deletes::add);
            }
            else if (!KEEP.contains(item.getObject().getTag())) {
                this.deletes.add(item);
            }
        }
    }

    private void deleteFlaggedNodes() {
        this.deletes.forEach(TreeNode::removeFromParent);
    }
}
