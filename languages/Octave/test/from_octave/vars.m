
## Check that xyz is tagged as a variable in the parser.  Both
## expressions must remain on one line for this test to work as
## intended.
xyz(1) = 1; xyz /= 1;
get = xyz;
expect = 1;
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif

warning ("off", "Octave:num-to-str", "local");
a = [97 ... % comment
     'b'];
get = a;
expect = 'ab';
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif