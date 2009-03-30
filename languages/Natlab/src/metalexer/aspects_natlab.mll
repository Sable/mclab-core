package natlab;
%%
%%
%layout aspects_natlab

%component aspects_base
%component aspect

%start aspects_base

%%

%%inherit helper_beaver

%%embed
%name aspects
%host aspects_base
%guest aspect
%start START_ASPECT
%end END
%pair OPEN_END, END

%%inherit natlab

%replace base, aspects_base