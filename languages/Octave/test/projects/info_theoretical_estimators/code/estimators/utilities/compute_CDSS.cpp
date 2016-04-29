#include "mex.h"
#include "math.h"

/* .cpp version of 'compute_CDSS.m' */

/* Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>. */

void mexFunction(
    int nlhs, mxArray *plhs[],
    int nrhs, const mxArray *prhs[])
{

    double *Y;    /* pointer to the input matrix (row vector) */    
    mwSize T;    
    
    Y = mxGetPr(prhs[0]); /* create a pointer to the input matrix */
    T = mxGetN(prhs[0]);  /* number of columns */
        
    double *H;    
    /* Allocating array for H */
    plhs[0] = mxCreateDoubleMatrix(1,1,mxREAL);
    H = mxGetPr(plhs[0]);    
    
    mwSize i,j,m;
    
    m = floor(sqrt(double(T)));
    *H = 0;
    for (i=1; i<T-m+1; i++) {   /* i: in Matlab sense*/
    for (j=i+1; j<i+m-1+1; j++) {   /* j: in Matlab sense*/
        *H = *H + pow(Y[j-1]-Y[i+m-1],2) * pow(Y[j-1]-Y[i-1],2) / pow(Y[i+m-1]-Y[i-1],5);
    }
    }
    *H = -log(30 * *H / (T*(T-m)));

}
