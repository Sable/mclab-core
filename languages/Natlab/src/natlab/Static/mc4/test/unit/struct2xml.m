% turns a struct into an xml tree.
% s - the matlab object to xmlify
% name - the name of the overall node for the tree
% parentNode (optional) - the tree gets attached to the given xml node
%
% the conversion turns
%   - structs into a node with a child node for every field, whose value is the
%     xml of the field value
%   - cell arrays into several nodes, whose value is the xml of the cells
%   - strings into text nodes
%   - numeric, logicals into text node with comma separated values
function [node] = struct2xml(s,name,parentNode)
  if (nargin < 3)
     doc = com.mathworks.xml.XMLUtils.createDocument(name);
     parentNode = doc.getDocumentElement();
     isparent = true;
  else
     doc = parentNode.getOwnerDocument();
     isparent = false;
  end
     
  
  % every field is a new child node
  if (isstruct(s))
    % only create the node if we're not the root
    if (~isparent)
      newNode = doc.createElement(name);
      parentNode.appendChild(newNode);
    else
      newNode = parentNode;
    end
    fields = fieldnames(s);
    for i = 1:numel(fields)
      field = fields{i};
      struct2xml(s.(field),field,newNode);
    end
  % chars -> node with text element
  elseif (ischar(s))
      newNode = doc.createElement(name);
      newNode.appendChild(doc.createTextNode(s));
      parentNode.appendChild(newNode);
  % logical, numbers -> text element with comma separated numbers
  elseif (islogical(s) || isnumeric(s))
      newNode = doc.createElement(name);
      text = '';
      for j = 1:numel(s)
          text = [text ',' mat2str(s(j))];
      end
      if (numel(text) > 1 && text(1) == ',')
          text = text(2:end);
      end
      newNode.appendChild(doc.createTextNode(text));
      parentNode.appendChild(newNode);
  elseif (iscell(s))
      for j = 1:numel(s)
         %newNode = doc.createElement(name);
         %parentNode.appendChild(newNode)
         struct2xml(s{j},[name],parentNode); %newNode);
      end
  end
  node = parentNode;
end


