function [result] = u192_auto(n)
  x0 = char(32+80*[0.236048089737]*n);
  x1 = int8([0.956034271889]*n);
  x2 = single([0.844421851525]*n);
  result = colon(x0,x1,x2);
end
