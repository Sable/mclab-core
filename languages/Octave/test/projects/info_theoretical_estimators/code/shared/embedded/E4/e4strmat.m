function s=e4strmat(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11)
%E4STRMAT Performs identically like char() and str2mat() (or like setstr()
%if there is only one argument in order to mantain compatibility between
% old and new versions of Matlab

global E4OPTION
prog  = E4OPTION(18);
ver_prog = E4OPTION(19);

arguments = [];
for i=1:nargin
  arguments = [arguments, 't',int2str(i), ','];
end
arguments = arguments(1:size(arguments,2)-1);

v = version;
p = findstr(version,'.');
v = str2num(v(1:p(1)-1));

if prog | ver_prog >= 6
   eval(['s=char(' arguments ');']);
else
   if nargin == 1
      eval(['s=setstr(' arguments ');']);
   else
      eval(['s=str2mat(' arguments ');']);    
   end
end   
