package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.date.DatePeriod;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.gedcom.model.Event;
import nu.mine.mosher.gedcom.model.Loader;
import nu.mine.mosher.gedcom.model.Person;
import nu.mine.mosher.gedcom.model.Source;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static nu.mine.mosher.gedcom.TagOrderMaps.*;
import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2017-08-25

public class GedcomSort implements Gedcom.Processor {
    private GedcomTree tree;

    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomSortOptions options = new ArgParser<>(new GedcomSortOptions()).parse(args).verify();
        new Gedcom(options, new GedcomSort()).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomSort() {
    }

    @Override
    public boolean process(final GedcomTree tree) {
        this.tree = tree;
        final Loader loader = new Loader(tree, "");
        loader.parse();
        sort(tree.getRoot(), loader);
        deepSort(tree.getRoot(), loader);
        return true;
    }



    private void sort(final TreeNode<GedcomLine> root, final Loader loader) {
        root.sort((node1, node2) -> compareTopLevelRecords(node1, node2, loader));
    }

    private int compareTopLevelRecords(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        int c = 0;
        if (c == 0) {
            c = compareTags(node1, node2, mapTopLevelOrder);
        }
        if (c == 0) {
            final GedcomTag tag = node1.getObject().getTag();
            if (tag.equals(GedcomTag.INDI)) {
                c = compareIndi(node1, node2, loader);
            } else if (tag.equals(GedcomTag.SOUR)) {
                c = compareSour(node1, node2, loader);
            } else if (tag.equals(GedcomTag.FAM)) {
                c = compareFam(node1, node2, loader);
            } else if (tag.equals(GedcomTag.NOTE)) {
                c = compareNote(node1, node2, loader);
            } else if (tag.equals(GedcomTag.OBJE)) {
                c = compareObje(node1, node2);
            }
        }
        return c;
    }

    private static int compareIndi(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        int c = 0;
        if (c == 0) {
            c = loader.lookUpPerson(node1).getNameSortable().compareToIgnoreCase(loader.lookUpPerson(node2).getNameSortable());
        }
        if (c == 0) {
            c = loader.lookUpPerson(node1).getBirth().compareTo(loader.lookUpPerson(node2).getBirth());
        }
        if (c == 0) {
            c = loader.lookUpPerson(node1).getID().compareTo(loader.lookUpPerson(node2).getID());
            if (c != 0) {
                log().warning("Two individuals have same name and birth: "+node1+" and "+node2);
            }
        }
        return c;
    }

    private int compareFam(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        final FamPair fp1 = indisForFamily(node1);
        final FamPair fp2 = indisForFamily(node2);

        // degenerate cases of families with less than two individuals:
        if (fp1 == null && fp2 != null) {
            return -1;
        }
        if (fp1 != null && fp2 == null) {
            return +1;
        }
        if (fp1 == null || fp2 == null) {
            return 0;
        }

        int c = 0;
        if (c == 0) {
            c = compareIndi(fp1.i1, fp2.i1, loader);
        }
        if (c == 0) {
            c = compareIndi(fp1.i2, fp2.i2, loader);
        }
        return c;
    }

    private static int compareSour(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        int c = 0;
        if (c == 0) {
            final Source s1 = loader.lookUpSource(node1);
            if (s1 == null) {
                log().severe("Cannot find source given in: "+node1);
                return -1;
            }
            final String t1 = s1.getTitle();
            final Source s2 = loader.lookUpSource(node2);
            if (s2 == null) {
                log().severe("Cannot find source given in: "+node2);
                return +1;
            }
            final String t2 = s2.getTitle();
            c = t1.compareToIgnoreCase(t2);
        }
        if (c == 0) {
            c = loader.lookUpSource(node1).getAuthor().compareToIgnoreCase(loader.lookUpSource(node2).getAuthor());
        }
        return c;
    }

    private static int compareNote(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        return node1.getObject().getValue().compareTo(node2.getObject().getValue());
    }

    private static class FamPair {
        TreeNode<GedcomLine> i1;
        TreeNode<GedcomLine> i2;
        FamPair(TreeNode<GedcomLine> i1, TreeNode<GedcomLine> i2) {
            this.i1 = i1;
            this.i2 = i2;
        }
    }

    private FamPair indisForFamily(final TreeNode<GedcomLine> fam) {
        String h = "";
        String w = "";
        String c1 = "";
        String c2 = "";
        for (final TreeNode<GedcomLine> m : fam) {
            final GedcomTag t = m.getObject().getTag();
            if (t.equals(GedcomTag.HUSB)) {
                h = m.getObject().getPointer();
            } else if (t.equals(GedcomTag.WIFE)) {
                w = m.getObject().getPointer();
            } else if (t.equals(GedcomTag.CHIL)) {
                if (c1.isEmpty()) {
                    c1 = m.getObject().getPointer();
                } else if (c2.isEmpty()) {
                    c2 = m.getObject().getPointer();
                }
            }
        }
        String id1 = "";
        String id2 = "";
        if (!h.isEmpty() && !w.isEmpty()) {
            id1 = h;
            id2 = w;
        } else if (!h.isEmpty() && !c1.isEmpty()) {
            id1 = h;
            id2 = c1;
        } else if (!w.isEmpty() && !c1.isEmpty()) {
            id1 = w;
            id2 = c1;
        } else if (!c1.isEmpty() && !c2.isEmpty()) {
            id1 = c1;
            id2 = c2;
        } else {
            return null;
        }
        return new FamPair(this.tree.getNode(id1), this.tree.getNode(id2));
    }

    private int compareObje(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2) {
        return getObjectTitle(node1).compareToIgnoreCase(getObjectTitle(node2));
    }

    private String getObjectTitle(final TreeNode<GedcomLine> obje) {
        final GedcomLine line = obje.getObject();
        if (!line.isPointer()) {
            return line.getValue();
        }

        final TreeNode<GedcomLine> nodeRec = this.tree.getNode(line.getPointer());
        if (nodeRec == null) {
            // should never happen
            return line.getPointer();
        }

        // find first .OBJE.FILE.TITL (GEDCOM >= 5.5.1)
        for (final TreeNode<GedcomLine> nodeFile : nodeRec) {
            if (nodeFile.getObject().getTag().equals(GedcomTag.FILE)) {
                for (final TreeNode<GedcomLine> nodeTitl : nodeFile) {
                    if (nodeTitl.getObject().getTag().equals(GedcomTag.TITL)) {
                        return nodeTitl.getObject().getValue();
                    }
                }
            }
        }

        // fall back to GEDCOM <= 5.5 .OBJE.TITL
        for (final TreeNode<GedcomLine> nodeTitl : nodeRec) {
            if (nodeTitl.getObject().getTag().equals(GedcomTag.TITL)) {
                return nodeTitl.getObject().getValue();
            }
        }

        // last resort sort by pointer
        return nodeRec.getObject().getPointer();
    }



    private void deepSort(final TreeNode<GedcomLine> node, final Loader loader) {
        node.forEach(c -> deepSort(c, loader));

        if (node.getChildCount() > 0 && node.getObject() != null) {
            final GedcomTag tag = node.getObject().getTag();
            if (tag.equals(GedcomTag.INDI)) {
                node.sort((node1, node2) -> compareIndis(node1, node2, loader));
            } else if (tag.equals(GedcomTag.HEAD) && node.getObject().getLevel() == 0) {
                node.sort((node1, node2) -> compareTags(node1, node2, mapHeadOrder));
            } else if (tag.equals(GedcomTag.SOUR) && node.getObject().getLevel() == 0) {
                node.sort((node1, node2) -> compareTags(node1, node2, mapSourOrder));
            } else if (tag.equals(GedcomTag.SOUR) && node.getObject().getLevel() > 0) {
                node.sort((node1, node2) -> compareTags(node1, node2, mapCitationOrder));
            } else if (tag.equals(GedcomTag.FAM)) {
                node.sort((node1, node2) -> compareFams(node1, node2, loader));
            } else if (GedcomTag.setIndividualEvent.contains(tag) ||
                GedcomTag.setIndividualAttribute.contains(tag) ||
                GedcomTag.setFamilyEvent.contains(tag) ||
                tag.equals(GedcomTag.NAME)) {
                node.sort((node1, node2) -> compareEvents(node1, node2, mapEventOrder, loader));
            }
        }
    }

    private int compareEvents(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Map<GedcomTag, Integer> mapOrder, final Loader loader) {
        final Integer o1 = mapOrder.get(node1.getObject().getTag());
        final Integer o2 = mapOrder.get(node2.getObject().getTag());

        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return +1;
        }
        if (o1 < o2) {
            return -1;
        }
        if (o2 < o1) {
            return +1;
        }

        // same tags
        int c = 0;

        // TODO handle sorting other event sub-tags (beyond SOUR)
        if (node1.getObject().getTag().equals(GedcomTag.SOUR)) {
            final GedcomLine lin1 = node1.getObject();
            final GedcomLine lin2 = node2.getObject();

            if (this.tree.getNode(lin1.getPointer()) == null) {
                log().severe("Cannot find xref: "+lin1);
            }
            if (this.tree.getNode(lin2.getPointer()) == null) {
                log().severe("Cannot find xref: "+lin2);
            }
            c = compareSour(this.tree.getNode(lin1.getPointer()), this.tree.getNode(lin2.getPointer()), loader);
            if (c == 0) {
                // same sources, sort by page
                c = compareChildValues(node1, node2, GedcomTag.PAGE);
            }
        }

        return c;
    }

    private static int compareChildValues(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final GedcomTag tag) {
        return getChildValue(node1, tag).compareToIgnoreCase(getChildValue(node2, tag));
    }

    private static String getChildValue(final TreeNode<GedcomLine> node, final GedcomTag tag) {
        for (final TreeNode<GedcomLine> c : node) {
            if (c.getObject().getTag().equals(tag)) {
                return c.getObject().getValue();
            }
        }
        return "";
    }

    private int compareIndis(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        int c = 0;

        if (shouldNotBeSorted(node1, node2)) {
            return c;
        }

        c = compareTags(node1, node2, mapIndiOrder);

        if (c == 0) {
            final Event event1 = loader.lookUpEvent(node1);
            final Event event2 = loader.lookUpEvent(node2);
            if (event1 == null && event2 == null) {
                c = 0;
            } else if (event1 == null) {
                c = +1; // sort events before non-events
            } else if (event2 == null) {
                c = -1; // sort events before non-events
            } else {
                final DatePeriod d1 = event1.getDate();
                final DatePeriod d2 = event2.getDate();
                c = 0;
                if (d1 != null && d2 != null && !(d1.equals(DatePeriod.UNKNOWN) && d2.equals(DatePeriod.UNKNOWN))) {
                    c = d1.compareTo(d2);
                }
                if (c == 0) {
                    c = compareEventsHeuristically(node1, node2);
                }
            }
        }
        if (c == 0) {
            if (node1.getObject().getTag().equals(GedcomTag.OBJE) && node2.getObject().getTag().equals(GedcomTag.OBJE)) {
                c = compareObje(node1, node2);
            }
        }

        return c;
    }

    private static int compareEventsHeuristically(TreeNode<GedcomLine> node1, TreeNode<GedcomLine> node2) {
        final int order1 = heuristicPosition(node1.getObject().getTag());
        final int order2 = heuristicPosition(node2.getObject().getTag());
        return Integer.compare(order1, order2);
    }

    private static int heuristicPosition(final GedcomTag tag) {
        if (Objects.isNull(tag) || tag.equals(GedcomTag.UNKNOWN) || !mapPlaceableEventOrder.containsKey(tag)) {
            return NON_PLACEABLE_EVENT;
        }
        return mapPlaceableEventOrder.get(tag);
    }

    private static boolean shouldNotBeSorted(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2) {
        final GedcomTag tag = node1.getObject().getTag();
        final GedcomTag tag2 = node2.getObject().getTag();
        return tag.equals(tag2) && setDoNotSortEvents.contains(tag);
    }

    private static int compareFams(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Loader loader) {
        int c = 0;
        final Event event1 = loader.lookUpEvent(node1);
        final Event event2 = loader.lookUpEvent(node2);
        if (event1 == null && event2 == null) {
            c = compareTags(node1, node2, mapFamOrder);
            if (c == 0) {
                final GedcomLine line1 = node1.getObject();
                final GedcomLine line2 = node2.getObject();
                if (line1.getTag().equals(GedcomTag.CHIL)) {
                    final TreeNode<GedcomLine> indi1 = loader.getGedcom().getNode(line1.getPointer());
                    final Person person1 = loader.lookUpPerson(indi1);
                    final TreeNode<GedcomLine> indi2 = loader.getGedcom().getNode(line2.getPointer());
                    final Person person2 = loader.lookUpPerson(indi2);
                    c = person1.getBirth().compareTo(person2.getBirth());
                }
                if (c == 0) {
                    final String v1 = line1.isPointer() ? line1.getPointer() : line1.getValue();
                    final String v2 = line2.isPointer() ? line2.getPointer() : line2.getValue();
                    c = v1.compareTo(v2);
                }
            }
        } else if (event1 == null) {
            c = -1;
        } else if (event2 == null) {
            c = +1;
        } else {
            final DatePeriod d1 = event1.getDate();
            final DatePeriod d2 = event2.getDate();
            // TODO: heuristic sorting for family events with no dates (similar to inidividual events)
            if (d1 == null && d2 == null) {
                c = 0;
            } else if (d2 == null) {
                c = -1;
            } else if (d1 == null) {
                c = +1;
            } else {
                c = event1.getDate().compareTo(event2.getDate());
            }
        }
        return c;
    }

    private static int compareTags(final TreeNode<GedcomLine> node1, final TreeNode<GedcomLine> node2, final Map<GedcomTag, Integer> mapOrder) {
        final Integer o1 = mapOrder.get(node1.getObject().getTag());
        final Integer o2 = mapOrder.get(node2.getObject().getTag());

        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return +1;
        }
        if (o2 == null) {
            return -1;
        }
        if (o1 < o2) {
            return -1;
        }
        if (o2 < o1) {
            return +1;
        }
        return 0;
    }
}
