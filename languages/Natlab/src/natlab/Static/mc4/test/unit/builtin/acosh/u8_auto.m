function [result] = u8_auto(n)
  x0 = char(32+80*[0.236048089737,0.103166034231;0.396058242611,0.154972270802]*n);
  result = acosh(x0);
end
