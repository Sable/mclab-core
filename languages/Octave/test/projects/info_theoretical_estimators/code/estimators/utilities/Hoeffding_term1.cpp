#include "mex.h"
#include "math.h"

/* .cpp version of 'Hoeffding_term1.m' */

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

/* The computational routine(s) */
double max(double a,double b)
{
return (a>b)?a:b;
}

long factorial(int n)
{
  int c;
  long result = 1;
 
  for (c = 1; c <= n; c++)
    result = result * c;
 
  return result;
}

/* The gateway function */
void mexFunction(
    int nlhs, mxArray *plhs[],
    int nrhs, const mxArray *prhs[])
{

    double *U;    /* pointer to the input matrix */    
    mwSize d,T;    
    
    U = mxGetPr(prhs[0]); /* create a pointer to the input matrix */
    d = mxGetM(prhs[0]);  /* number of rows */
    T = mxGetN(prhs[0]);  /* number of columns */

    double *I;    
    /* Allocating array for I */
    plhs[0] = mxCreateDoubleMatrix(1,1,mxREAL);
    I = mxGetPr(plhs[0]);    
    
    mwSize j,k,i;
    double temp,term1;
    
    term1 = 0;
    for (j=1; j<T+1; j++) {   /* j: in Matlab sense*/
    for (k=1; k<T+1; k++) {   /* k: in Matlab sense*/
      temp = 1 - max(U[(j-1)*d],U[(k-1)*d]);  /*temp = 1-max(U(i,j),U(i,k)) */
      for (i=2; i<d+1; i++) {      /* i: in Matlab sense*/
          temp = temp * ( 1 - max(U[(i-1)+(j-1)*d],U[(i-1)+(k-1)*d]) );
      }
      term1 = term1 + temp;
    }
    }
    term1 = term1 / (T*T);

    *I = term1;
}
