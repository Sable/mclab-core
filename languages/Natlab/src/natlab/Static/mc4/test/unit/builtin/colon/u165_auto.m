function [result] = u165_auto(n)
  x0 = uint64([0.237964627092]*n);
  x1 = uint64([0.237964627092]*n);
  x2 = uint64([0.237964627092]*n);
  result = colon(x0,x1,x2);
end
