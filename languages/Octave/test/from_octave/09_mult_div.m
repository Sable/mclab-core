## Level 9 (multiply, divide)
get = 3+4 * 5;
expect = 23;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 5 * 1:6;
expect = [5 6];
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 3>1 * 5;
expect = false;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 1&1 * 0;
expect = false;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 1|1 * 0;
expect = true;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 1&&1 * 0;
expect = false;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 1||1 * 0;
expect = true;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
a = 3;
a /= a * 2;
get = a;
expect = 0.5;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif











clear;

## Level 9 (multiply, divide)
get = 3 * 4 / 5;
expect = 2.4;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 3 ./ 4 .* 5;
expect = 3.75;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 2 * 4 \ 6;
expect = 0.75;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = 2 .\ 4 .* 6;
expect = 12;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
