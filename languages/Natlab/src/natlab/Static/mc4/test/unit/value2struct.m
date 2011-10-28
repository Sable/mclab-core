% turns a matlab value into a structure which is a tree describibing the
% object. meant as an intermediate step to turn matlab values into xml
function [s] = value2struct(mat)
  s.class = class(mat);
  s.size = size(mat);
  if (isnumeric(mat) || islogical(mat) || ischar(mat))
     s.value = mat;
  elseif (iscell(mat))
     elements = cell(1,numel(mat));
     for i = 1:numel(mat)
        elements{i} = value2struct(mat{i});
     end
     s.value = elements;
  elseif (isstruct(mat) || isobject(mat))
     if (isobject(mat))
        warning off MATLAB:structOnObject
        mat = struct(mat);
     end
     fields = fieldnames(mat);
     for i = 1:numel(mat)
        newStruct = struct();
        for j = 1:numel(fields)
           field = fields{j};
           newStruct.(field) = value2struct(mat(i).(field));
        end
        s.value{i} = newStruct;
     end
  elseif (isa(mat,'function_handle'))
     s.value = (functions(mat));
     % store the workspace for anoymous functions
     if isfield(s.value,'workspace')
        workspace = s.value.workspace{1};
        fields = fieldnames(workspace);
        space = struct();
        for i = 1:numel(fields)
           field = fields{1};
           space.(field) = value2struct(workspace.(field));
        end
        s.value.workspace = space;
     end
  else
     error(['value2struct cannot convert ' class(mat)])
  end
end

