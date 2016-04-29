%function compileDir_simple(Cdir)
function compileDir_simple
%Modification (Zoltan Szabo, Sep. 2012): only the ITE-relevant files are compiled.

%if nargin<1
%    Cdir = pwd;
%end
%
%files = dir(fullfile(Cdir,'*.cpp'));
%oldDir = pwd;
%cd(Cdir);
%for j=1:length(files)
%    try
%        %cm = sprintf('mex %s',files(j).name);
%        cm = sprintf('mex -largeArrayDims %s',files(j).name);
%        disp(cm);
%        eval(cm);
%    catch
%        disp(lasterr);
%        disp('IGNORE if the file is a C++ file which is not a mex file (ie without a mexFunction inside)');
%    end
%end
%
%cd(oldDir);

names = {'mex_w_times_x_symmetric.cpp', 'sparsifyc.cpp', 'spmtimesd.cpp'};
for k = 1:length(names)
    cm = sprintf('mex -largeArrayDims %s',names{k});
    %disp(cm);
    eval(cm);
end