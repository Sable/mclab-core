function [result] = u197_auto(n)
  x0 = char(32+80*[0.236048089737]*n);
  x1 = int8([0.956034271889]*n);
  x2 = ([0.62290169489]*n > .5);
  result = colon(x0,x1,x2);
end
