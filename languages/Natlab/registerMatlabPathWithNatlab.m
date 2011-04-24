%%
% This script points the Natlab.jar to the Matlab Path.
% That is, it finds the set of directories that make up the path of the
% Matlab installation, and sets these direcoties as the path for Natlab.
%
% Note that the path is stored as a persistent preference by natlab
% the natlab.jar has to be in the same directory as this script


% find matlab path
p = path();
fprintf('found matlab path: %s\n\n',p);

% split path
c = regexp(p,';','split');

% reset path
system('java -jar natlab.jar -pref -set_matlab_path ""');

% add paths at a time - the command line cannot hold enough chars
N = 4000; % maximum path string we add each time
s = '';
for i = 1:numel(c)
    s = [s ';' c{i}];
    if (numel(s) > N) % if we have collected enough strings, add to path
        t = ['"', s(2:end), '"'];
        fprintf('adding %d chars: %s',length(t),t);
        system(['java -jar natlab.jar -pref -add_matlab_path ' t]);
        s = '';
    end
end
t = ['"', s(2:end), '"'];
fprintf('adding %d chars: %s',length(t),t);
system(['java -jar natlab.jar -pref -add_matlab_path ' t]);
s = '';

% show preferences
system('java -jar natlab.jar -pref -show_pref');




