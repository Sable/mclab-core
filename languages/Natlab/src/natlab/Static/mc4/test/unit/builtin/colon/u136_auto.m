function [result] = u136_auto(n)
  x0 = int8([0.956034271889]*n);
  x1 = char(32+80*[0.236048089737]*n);
  x2 = char(32+80*[0.236048089737]*n);
  result = colon(x0,x1,x2);
end
