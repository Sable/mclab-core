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

// idempotence:
#ifndef __KDPEE_HEADER__
#define __KDPEE_HEADER__

// Can be made to use single- or double-precision calculation
#ifdef KDPEE_PREC_SINGLE
	typedef  float floatval;
#else
	typedef double floatval;
#endif

/** 
* kdpee() is the entry function.
* 
* Note that the return value, and many of the arguments, use EITHER type 
* double OR float, depending on whether KDPEE_PREC_SINGLE was defined at
* compile time.
* 
* dimrefs is an array of pointers to your data dimension arrays
* n is the number of data (i.e. num values in each array in dimrefs)
* d is number of dimensions (i.e. size of dimrefs itself)
* mins and maxs specify the data range, arrays of size d
* zcut (typically 1.96) is the threshold for branch stopping
* keys should point to an array of indices which we can shuffle during our algo; 
*    on init it should contain the values (1..n)
*/
floatval kdpee(const floatval **dimrefs, const int n, const int d, floatval *mins, 
								floatval *maxs, const floatval zcut, int *keys);

// idempotence:
#endif
