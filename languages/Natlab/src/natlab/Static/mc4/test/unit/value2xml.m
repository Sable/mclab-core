function [node] = value2xml(mat,name,parentNode)
  error(nargchk(2,3,nargin))
  s = value2struct(mat);
  if (nargin == 2)
     node = struct2xml(s,name);
  elseif (nargin == 3)
     node = struct2xml(s,name,paretNode);
  end
end
