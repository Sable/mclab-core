function [result] = u10_auto(n)
  x0 = double([0.134364244112]*n);
  x1 = char(32+80*[0.236048089737]*n);
  result = colon(x0,x1);
end
