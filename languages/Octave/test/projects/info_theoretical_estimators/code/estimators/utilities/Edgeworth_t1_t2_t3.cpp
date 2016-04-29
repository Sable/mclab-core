#include "mex.h"
#include "math.h"

/* .cpp version of 'Edgeworth_t1_t2_t3.m' */

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

    double *Y;    /* pointer to the input matrix */    
    mwSize d,T;   /* d:dimension, T:number of samples */
    
    Y = mxGetPr(prhs[0]); /* create a pointer to the input matrix */
    d = mxGetM(prhs[0]);  /* number of rows */
    T = mxGetN(prhs[0]);  /* number of columns */

    double *t1,*t2,*t3;    
    /* Allocating array for t1,t2,t3 */
    plhs[0] = mxCreateDoubleMatrix(1,1,mxREAL);
    t1 = mxGetPr(plhs[0]);    
    plhs[1] = mxCreateDoubleMatrix(1,1,mxREAL);
    t2 = mxGetPr(plhs[1]);    
    plhs[2] = mxCreateDoubleMatrix(1,1,mxREAL);
    t3 = mxGetPr(plhs[2]);    
    
    mwSize i,j,k,u;
    double kappa_iii,kappa_iij,kappa_ijk;
    
    *t1 = 0;
    for (i=1; i<d+1; i++) {      /* i: in Matlab sense */
        kappa_iii = 0;
        for (u=1; u<T+1; u++) {  /* u: in Matlab sense */
            kappa_iii = kappa_iii + pow(Y[(i-1)+(u-1)*d],3);
        }
        *t1 = *t1 + pow(kappa_iii/T,2);
    }
    
    *t2 = 0;
    /* i<j */
    for (i=1; i<(d-1)+1; i++) {   /* i: in Matlab sense */
    for (j=i+1; j<d+1; j++) {     /* j: in Matlab sense */
         kappa_iij = 0;
         for (u=1; u<T+1; u++) {  /* u: in Matlab sense */
             kappa_iij = kappa_iij + pow(Y[(i-1)+(u-1)*d],2) * Y[(j-1)+(u-1)*d];
         }
         *t2 = *t2 + pow(kappa_iij/T,2);           
    }        
    }
    
    /* j<i */
    for (j=1; j<(d-1)+1; j++) {  /* i: in Matlab sense */
    for (i=j+1; i<d+1; i++) {    /* j: in Matlab sense */
        kappa_iij = 0;
        for (u=1; u<T+1; u++) {  /* u: in Matlab sense */
            kappa_iij = kappa_iij + pow(Y[(i-1)+(u-1)*d],2) * Y[(j-1)+(u-1)*d];
        }
        *t2 = *t2 + pow(kappa_iij/T,2);           
    }        
    }
    *t2 = 3 * *t2;
    
    *t3 = 0;
    /* i<j<k */
    for (i=1; i<(d-2)+1; i++) {   /* i: in Matlab sense */
    for (j=i+1; j<(d-1)+1; j++) { /* j: in Matlab sense */
    for (k=j+1; k<d+1; k++) {     /* k: in Matlab sense */
        kappa_ijk = 0;
        for (u=1; u<T+1; u++) {   /* u: in Matlab sense */
            kappa_ijk = kappa_ijk + Y[(i-1)+(u-1)*d] * Y[(j-1)+(u-1)*d] *Y[(k-1)+(u-1)*d];
        }
        *t3 = *t3 + pow(kappa_ijk/T,2);
    }
    }
    }
    *t3 = *t3 / 6;
}
