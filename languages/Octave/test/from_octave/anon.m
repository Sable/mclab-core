## Test creation of anonymous functions

af_in_cell = {@(x) [1 2]};
get = af_in_cell{1}();
expect = [1, 2];
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif

R = @(rot) [cos(rot) -sin(rot) sin(rot) cos(rot)];
get = R(pi/2);
expect = [cos(pi/2), -sin(pi/2) sin(pi/2),cos(pi/2)];
if (any (size (expect) != size (get)))
  error ("wrong size: expected %d,%d but got %d,%d",
         size (expect), size (get));
elseif (any (any (expect != get)))
  error ("didn't get what was expected.");
endif