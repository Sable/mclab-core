function [result] = u26_auto(n)
  x0 = char(32+80*[0.236048089737]*n);
  x1 = int8([0.956034271889]*n);
  result = colon(x0,x1);
end
