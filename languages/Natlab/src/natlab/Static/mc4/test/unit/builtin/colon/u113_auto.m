function [result] = u113_auto(n)
  x0 = int8([0.956034271889]*n);
  x1 = single([0.844421851525]*n);
  x2 = ([0.62290169489]*n > .5);
  result = colon(x0,x1,x2);
end
