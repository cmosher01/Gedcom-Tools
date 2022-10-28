package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.date.DatePeriod;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.gedcom.model.Event;
import nu.mine.mosher.gedcom.model.Loader;
import nu.mine.mosher.gedcom.model.Partnership;
import nu.mine.mosher.gedcom.model.Person;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;

import static nu.mine.mosher.logging.Jul.log;

// Created by Christopher Alan Mosher on 2018-10-02

public class GedcomEventsCsv implements Gedcom.Processor {
    private static final String DELIM = ",";

    private final GedcomEventsCsvOptions options;
    final Set<Event> exportedEvents = new HashSet<>();



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomEventsCsvOptions options = new ArgParser<>(new GedcomEventsCsvOptions()).parse(args).verify();
        new Gedcom(options, new GedcomEventsCsv(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomEventsCsv(final GedcomEventsCsvOptions options) {
        this.options = options;
    }



    @Override
    public boolean process(final GedcomTree tree) {
        final Loader loader = new Loader(tree, "");
        loader.parse();

        for (final Person person : loader.getAllPeople()) {
            if (!person.isPrivate() || this.options.prv) {
                writeEvents(person.getEvents(), person, Optional.empty());
                for (final Partnership partnership : person.getPartnerships()) {
                    if (!partnership.isPrivate() || this.options.prv) {
                        writeEvents(partnership.getEvents(), person, Optional.ofNullable(partnership.getPartner()));
                    }
                }
            }
        }
        return false;
    }

    private void writeEvents(final Collection<Event> events, final Person primary, final Optional<Person> secondary) {
        for (final Event event : events) {
            if (!event.isPrivate() || this.options.prv) {
                if (dateOrPlace(event)) {
                    if (!this.exportedEvents.contains(event)) {
                        final List<String> fields = new ArrayList<>(10);
                        fields.add(filter(primary.getNameSortedDisplay()));
                        if (secondary.isPresent()) {
                            fields.add(filter(secondary.get().getNameSortedDisplay()));
                        } else {
                            fields.add(filter(""));
                        }
                        fields.add(filter(event.getType()));
                        fields.add(filter(event.getDate().getTabularString()));
                        addPlaces(event.getPlace(), fields);
                        out(fields);
                        this.exportedEvents.add(event);
                    }
                }
            }
        }
    }

    private static void out(Collection<String> fields) {
        boolean first = true;
        for (final String field : fields) {
            if (first) {
                first = false;
            } else {
                System.out.print(DELIM);
            }
            System.out.print(field);
        }
        System.out.println();
    }

    private static void addPlaces(String place, List<String> fields) {
        ArrayDeque<String> parts = Arrays
                .stream(place.split(",", -1))
                .collect(Collector.of(
                        ArrayDeque::new,
                        ArrayDeque::addFirst,
                        (a, b) -> {b.addAll(a); return b;}));
        parts.stream().map(GedcomEventsCsv::filter).forEach(fields::add);
    }

    private static String filter(final String field) {
        return "\""+field.trim().replace("\"","\"\"")+"\"";
    }

    private static boolean dateOrPlace(final Event event) {
        return !event.getPlace().isEmpty() || !event.getDate().equals(DatePeriod.UNKNOWN);
    }
}
