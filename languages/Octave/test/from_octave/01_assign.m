
## Level 1 (assignment)
a = 2; b = 5; c = 7;
get = a += b *= c += 1;
expect = 42;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif
get = b == 40 && c == 8;