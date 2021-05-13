# Gedcom-Ansel

Copyright © 2018, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/Gedcom-Ansel.svg)](https://www.gnu.org/licenses/gpl.html)

ANSEL decoder, with GEDCOM extensions.

Use the name of the CharSet:

```
"x-gedcom-ansel"
```

# ANSEL conversion chart

|    | 8_  | 9_  | A_  | B_  | C_  | D_  | E_  | F_  |
|---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| _0 |                  |                  |                  | &#x02bb;<br>02BB | &#x00b0;<br>00B0 |                  | &#x0309;<br>0309 | &#x0327;<br>0327 |
| _1 |                  |                  | &#x0141;<br>0141 | &#x0142;<br>0142 | &#x2113;<br>2113 |                  | &#x0300;<br>0300 | &#x0328;<br>0328 |
| _2 |                  |                  | &#x00d8;<br>00D8 | &#x00f8;<br>00F8 | &#x2117;<br>2117 |                  | &#x0301;<br>0301 | &#x0323;<br>0323 |
| _3 |                  |                  | &#x0110;<br>0110 | &#x0111;<br>0111 | &#x00a9;<br>00A9 |                  | &#x0302;<br>0302 | &#x0324;<br>0324 |
| _4 |                  |                  | &#x00de;<br>00DE | &#x00fe;<br>00FE | &#x266f;<br>266F |                  | &#x0303;<br>0303 | &#x0325;<br>0325 |
| _5 |                  |                  | &#x00c6;<br>00C6 | &#x00e6;<br>00E6 | &#x00bf;<br>00BF |                  | &#x0304;<br>0304 | &#x0333;<br>0333 |
| _6 |                  |                  | &#x0152;<br>0152 | &#x0153;<br>0153 | &#x00a1;<br>00A1 |                  | &#x0306;<br>0306 | &#x0332;<br>0332 |
| _7 |                  |                  | &#x02b9;<br>02B9 | &#x02ba;<br>02BA | &#x00df;<br>00DF |                  | &#x0307;<br>0307 | &#x0326;<br>0326 |
| _8 | &#x0098;<br>0098 |                  | &#x00b7;<br>00B7 | &#x0131;<br>0131 | &#x20ac;<br>20AC |                  | &#x0308;<br>0308 | &#x031c;<br>031C |
| _9 | &#x009c;<br>009C |                  | &#x266d;<br>266D | &#x00a3;<br>00A3 |                  |                  | &#x030c;<br>030C | &#x032e;<br>032E |
| _A |                  |                  | &#x00ae;<br>00AE | &#x00f0;<br>00F0 |                  |                  | &#x030a;<br>030A | &#x0360;<br>0360 |
| _B |                  |                  | &#x00b1;<br>00B1 |                  |                  |                  | &#x0361;<br>0361 |                * |
| _C |                  |                  | &#x01a0;<br>01A0 | &#x01a1;<br>01A1 |                  |                  |                * | &#x0338;<br>0338 |
| _D | &#x200d;<br>200D |                  | &#x01af;<br>01AF | &#x01b0;<br>01B0 | &#x0065;<br>0065 |                  | &#x0315;<br>0315 |                  |
| _E | &#x200c;<br>200C |                  | &#x02bc;<br>02BC | &#x25a1;<br>25A1 | &#x006f;<br>006F |                  | &#x030b;<br>030B | &#x0313;<br>0313 |
| _F |                  |                  |                  | &#x25a0;<br>25A0 | &#x00df;<br>00DF |                  | &#x0310;<br>0310 |                  |

\* The right half of any double-width combining character is omitted, in favor of the full-width Unicode character.
