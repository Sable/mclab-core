function [result] = u5_auto(n)
  x0 = double([0.134364244112,0.847433736937;0.763774618977,0.255069025739]*n);
  result = cotd(x0);
end
