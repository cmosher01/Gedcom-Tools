package nu.mine.mosher.mopper;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;

// TODO handle POSIX "-W" option
@SuppressWarnings("unused")
public final class ArgParser<T> {
    private final Options<T> opts;

    public ArgParser(final T optionProcessor) {
        this.opts = Options.compile(optionProcessor);
    }

    public T parse(final String... args) {
        final List<Arg> as = Arrays.stream(args).map(Arg::new).collect(toList());
        expandConcatenatedShortOptions(as);
        consumeValuesForArgs(as);
        handleDashDashTerminator(as);
        as.forEach(this.opts::process);
        return this.opts.getObject();
    }

    private void expandConcatenatedShortOptions(final List<Arg> as) {
        final ListIterator<Arg> i = as.listIterator();
        while (i.hasNext()) {
            final Arg a = i.next();
            // TODO if POSIXLY_CORRECT, non-option also terminates
            if (a.isTerminator()) {
                break;
            }
            if (a.isShortWithManyValues()) {
                splitArg(i, a.getFirstShortName(), a.getShortNameRest());
            }
        }
    }

    private void splitArg(final ListIterator<Arg> i, final String first, String rest) {
        if (!this.opts.optionTakesValues(first)) {
            rest = "-" + rest;
        }

        // remove "-aval", or "-abc"
        i.remove();

        // add "-a" "val", or "-a -bc"
        i.add(new Arg("-" + first));
        i.add(new Arg(rest));

        // reprocess, beginning at the "-a" just added
        i.previous();
        i.previous();
    }


    private void consumeValuesForArgs(final List<Arg> as) {
        final ListIterator<Arg> i = as.listIterator();
        while (i.hasNext()) {
            final Arg a = i.next();
            if (!i.hasNext() || a.isTerminator()) {
                break;
            }
            if (!a.getName().isEmpty()) {
                final Opt o = this.opts.get(a.getName());
                consumeValuesForArg(i, a, o.nValsRequired(), false);
                consumeValuesForArg(i, a, o.nValsOptional(), true);
            }
        }
    }

    private static void consumeValuesForArg(final ListIterator<Arg> i, final Arg a, int n, final boolean optional) {
        while (n-- > 0) {
            if (!i.hasNext()) {
                break;
            }
            final Arg next = i.next();
            if (next.isNamed() && optional) {
                // for optional values, stop at next option-type arg
                // TODO if POSIXLY_CORRECT, only allow optional args to be next to their option, without a space
                // and if it has a space, then treat it as the next option instead.
                i.previous();
                break;
            } else {
                if (a.getValue().isEmpty()) {
                    i.remove();
                    a.addValue(next);
                }
            }
        }
    }


    private static void handleDashDashTerminator(final List<Arg> as) {
        final AtomicBoolean terminated = new AtomicBoolean();
        // TODO if POSIXLY_CORRECT, non-option also terminates
        as.forEach(a -> {
            if (a.isTerminator()) {
                terminated.set(true);
            }
            if (terminated.get()) {
                a.terminate();
            }
        });
    }
}
