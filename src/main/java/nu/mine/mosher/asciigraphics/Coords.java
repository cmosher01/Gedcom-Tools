package nu.mine.mosher.asciigraphics;

import com.google.common.primitives.UnsignedInteger;
import java.util.Objects;

import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.ZERO;
import static com.google.common.primitives.UnsignedInteger.valueOf;
import static java.util.Objects.*;

/**
 * Represents coordinates on the plane:
 *
 * <pre>
 *  012345 (column count == 6)
 * 0
 * 1
 * 2   *<--at:(3,2)
 * 3
 * 4
 * (row count == 5)
 *
 * x == column index
 * y == row index
 * </pre>
 */
public class Coords {
    public static final Coords ORIGIN = xy(ZERO, ZERO);

    private final UnsignedInteger x;
    private final UnsignedInteger y;

    public Coords(final UnsignedInteger x, final UnsignedInteger y) {
        this.x = Objects.requireNonNull(x);
        this.y = Objects.requireNonNull(y);
    }

    public static Coords xy(final UnsignedInteger x, final UnsignedInteger y) {
        return new Coords(x, y);
    }

    public UnsignedInteger x() {
        return this.x;
    }

    public UnsignedInteger y() {
        return this.y;
    }

    public Coords move(final int dx, final int dy) {
        return new Coords(delta(this.x,dx), delta(this.y,dy));
    }

    /**
     * @return left one
     */
    public Coords l() {
        return new Coords(this.x.minus(ONE), this.y);
    }

    /**
     * @return right one
     */
    public Coords r() {
        return new Coords(this.x.plus(ONE), this.y);
    }

    /**
     * @param sgn neg. or pos.
     * @return horizontal (left or right) one
     */
    public Coords h(final int sgn) {
        return sgn < 0 ? l() : r();
    }

    /**
     * @return up one
     */
    public Coords u() {
        return new Coords(this.x, this.y.minus(ONE));
    }

    /**
     * @return down one
     */
    public Coords d() {
        return new Coords(this.x, this.y.plus(ONE));
    }

    /**
     * @param sgn neg. or pos.
     * @return vertical (up or down) one
     */
    public Coords v(final int sgn) {
        return sgn < 0 ? u() : d();
    }

    private static UnsignedInteger delta(final UnsignedInteger u, final int du) {
        return du >= 0 ? u.plus(valueOf(du)) : u.minus(valueOf(du));
    }

    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Coords)) {
            return false;
        }
        final Coords that = (Coords)object;
        return Objects.equals(this.x, that.x) && Objects.equals(this.y, that.y);
    }

    @Override
    public int hashCode() {
        return hash(this.x, this.y);
    }
}
