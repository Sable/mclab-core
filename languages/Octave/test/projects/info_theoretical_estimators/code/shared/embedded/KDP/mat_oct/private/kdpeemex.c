/**
    This file is part of "kdpee", kd-partitioning entropy estimator.
    (c) 2008 Dan Stowell and Queen Mary University of London
    All rights reserved.

    kdpee is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    kdpee is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with kdpee.  If not, see <http://www.gnu.org/licenses/>.
*/

#include "math.h"
#include "mex.h"
#include "kdpee.h"

////////////////////////////////////////////////////////////////////////
// MATLAB ENTRY FUNCTION:
void mexFunction(int nlhs, mxArray *plhs[], int nrhs, const mxArray *prhs[])
{
	const mxArray *xData, *minData, *maxData;
	double *xValues, *mins, *maxs;
	int numdims, numdatums;
	double zcut;

	int *keys;
	const double **dimrefs;
	int i;
	double result;

	if(nrhs < 4){
		mexErrMsgTxt("kdpee error: 4 arguments required by the C implementation.\n");
	}

	// Get info about our input matrix
	xData = prhs[0];
	xValues = mxGetPr(xData); // indexed by whichdim * numdims + whichdatum
	numdims = mxGetN(xData);
	numdatums = mxGetM(xData);
	
	// mins and maxes
	minData = prhs[1];
	mins = mxGetPr(minData);
	maxData = prhs[2];
	maxs = mxGetPr(maxData);
	if( !(mxGetN(minData)==numdims ||  mxGetN(minData)==numdims)
				|| !(mxGetN(maxData)==numdims ||  mxGetN(maxData)==numdims) ){
    	mexErrMsgTxt("kdpee error: mins and maxs must have same dimension as the # dims in the dataset.\n");
	}
	
	zcut = mxGetScalar(prhs[3]);
	
	// Now we allocate space for the keys that we'll need to shuffle around
	keys = mxMalloc(numdatums * sizeof(int));
	// Here we create a set of pointers, one to each row of the input data
	dimrefs = mxMalloc(numdims * sizeof(double*));
	for(i = 0; i < numdims; ++i){
		dimrefs[i] = xValues + (i * numdatums);
	}
	
	// That's the matlab-specific stuff done, now let's call our calc and return it
	result = kdpee(dimrefs, numdatums, numdims, mins, maxs, zcut, keys);
	plhs[0] = mxCreateDoubleScalar(result);
	
	mxFree(keys);
	mxFree(dimrefs);
	
	return;
}
