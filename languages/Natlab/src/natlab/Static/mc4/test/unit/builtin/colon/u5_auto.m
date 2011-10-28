function [result] = u5_auto(n)
  x0 = single([0.844421851525]*n);
  x1 = ([0.62290169489]*n > .5);
  result = colon(x0,x1);
end
