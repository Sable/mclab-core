function [result] = u190_auto(n)
  x0 = char(32+80*[0.236048089737]*n);
  x1 = double([0.134364244112]*n);
  x2 = char(32+80*[0.236048089737]*n);
  result = colon(x0,x1,x2);
end
