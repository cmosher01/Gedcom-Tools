package nu.mine.mosher.gedcom.ansel;

import java.util.Objects;

public class GenerateMarkdownChart {
    public static void main(final String... args) {
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("|    | 8_  | 9_  | A_  | B_  | C_  | D_  | E_  | F_  |");
        System.out.println("|---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|");

        for (int lo = 0x00; lo < 0x10; ++lo) {
            System.out.print(String.format("| _%1X |",lo));
            for (int hi = 0x08; hi < 0x10; ++hi) {
                final int ansel = (hi << 4) | lo;
                final Integer c = AnselCharacterMap.map.get(ansel);
                if (Objects.isNull(c)) {
                    System.out.print(String.format("%19s","|"));
                } else {
                    if (0 <= c) {
                        System.out.print(String.format(" &#x%04x;<br>%04X |", c, c));
                    } else {
                        System.out.print(String.format("%19s","* |"));
                    }
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.flush();
    }
}
