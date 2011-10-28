% runs all the unit tests
function genXml(exitOnFinish)
  addpath(pwd) % make the xml functions accessible from other dirs
  warning off
  for d1 = getSubDirs()
    d1 = d1{1};
    cd(d1)
    for d2 = getSubDirs()
      d2 = d2{1};
      cd(d2)
      % get all unit tests
      files = dir();
      for i = 1:numel(files)
        file = files(i);
        if (~file.isdir && (file.name(1) ~= upper(file.name(1))))
          fprintf('.')
          name = file.name(1:end-2);
          % run test and get result as struct
          s = runTest(name,d2);
          % store xml
          xmlwrite([name '.xml'], struct2xml(s,'result'))
        end
      end      
      cd('..')
    end
    cd('..')
  end


  % exit on finish
  if (nargin > 0)
    if exitOnFinish
      exit
    end
  end
end


function s = runTest(name,dir)
  try
    res = feval(name,1);
    s.success = 'true';
    s.result = value2struct(res);
  catch e
    s.success = 'false';
    s.error = value2struct(e);
  end
end


% returns a list of names of all subdirectories
function s = getSubDirs()
  s = {};
  dirs = dir();
  for i = 1:numel(dirs)
    d = dirs(i);
    if d.isdir && ~(isequal(d.name, '.') || isequal(d.name,'..'))
      s{end+1} = d.name;
    end
  end
end


