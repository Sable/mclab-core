function [result] = u25_auto(n)
  x0 = char(32+80*[0.236048089737]*n);
  x1 = double([0.134364244112]*n);
  result = colon(x0,x1);
end
