package nu.mine.mosher.gedcom;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

public class GedcomFixDateTest {
    @ParameterizedTest
    @CsvFileSource(resources = "GedcomFixDateTest.csv")
    public void test(final String input, final String expected) {
        assertEquals(expected, new GedcomDateFixer(input).get());
    }
}
