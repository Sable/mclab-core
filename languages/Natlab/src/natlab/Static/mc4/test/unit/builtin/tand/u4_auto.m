function [result] = u4_auto(n)
  x0 = single([0.844421851525,0.75795440294;0.420571580831,0.258916750293]*n);
  result = tand(x0);
end
