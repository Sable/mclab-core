package natlab;
import static natlab.NatlabParser.Terminals.*;
%%
%%
%layout annotated_natlab

%component annotated_base
%component annotated_class
%component annotated_class_bracketed
%component annotations_end

%start annotated_base

%%

%%inherit helper_beaver

%%embed
%name annotations
%host annotated_base, annotated_class, annotated_class_bracketed
%guest annotations_end
%start START_ANNOTATION
%end END_ANNOTATION

%%inherit natlab

%replace base, annotated_base
%replace class, annotated_class
%replace class_bracketed, annotated_class_bracketed
