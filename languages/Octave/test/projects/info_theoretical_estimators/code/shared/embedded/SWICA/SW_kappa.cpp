#include "mex.h"

/*
 * SW_kappa.c - accelerated computation of Schweizer-Wolff kappa (L-inf) measure of dependence 
 * using empirical copula (given ranks of data).  Approximation is obtained by summing the
 * empirical copula on a sparse regular grid.
 *
 * Sergey Kirshner (sergey@cs.ualberta.ca)
 * May 7, 2008
 */

#include <math.h>
#include <stdlib.h>

void mexFunction( int nlhs, mxArray *plhs[],
                  int nrhs, const mxArray *prhs[])
{
  double *r;        /* Input array of ranks */
  long N,d;         /* Dimensions of the array of ranks */ 
  long Nbins;       /* Number of bins used */
  double Nbinsd;  
  long Cbins;       /* Number of indices in each bin, Nbins*Cbins=N */
  double Cbinsd;
  double *kappa;    /* Output array of Schweizer-Wolff kappa */ 
  long i,j,k,l;     /* Index variables */
  long *r_sorted;   /* Temporary array of resorted ranks */ 
  double *ecc;      /* Column of empirical copula */
  double bound;     /* Upper bound on sigma (used for acceleration) */
  long *becd,*bec;  /* Binned empirical copula density and copula */
  long curr_bin_min;/* Index from which to start updating empirical copula column */
  long temp_index;
  double Linf;      /* Temporary absolute value of the difference between empirical copula
		       and product copula */

  /* SW_kappa takes three inputs:
     r     -- dxN array of ranks for data
     bound -- (optional, default=1) upper bound on sigma
     Cbins -- (optional, default=1) number of data points falling between a pair of horizontal or vertical gridpoints

     Output:
     kappa -- (d-1)x(d-1) array of Schweizer-Wolff kappa dependence measures (for all pairs of dimensions)
  */

  if( nrhs<1 )
    mexErrMsgTxt("SW_kappa: Need at least one input argument.");
  else {
    /* Getting a pointer to the rank array */
    r=mxGetPr(prhs[0]);
    
    /* Getting the number of rows and columns */
    d=mxGetM(prhs[0]); /* Number of rows */
    N=mxGetN(prhs[0]); /* Number of columns */

    if( nrhs>1 ) {
      /* Getting bound on SW */
      bound=(double)mxGetScalar(prhs[1]);
      if( nrhs>2 ) {
	/* Getting the number of points in each axis bin */
        Cbins=(long)mxGetScalar(prhs[2]);
      } else {
        Cbins=1;
      }
    } else {
      bound=1.0;
    }
  }
  
  /* Determining the number of evaluation points on the horizontal and vertical
     axes with empirical copula evaluated at (i*Cbins/N,j*Cbins/N), i,j=1,Nbins. */
   Nbins=N/Cbins;
  
  Nbinsd=(double)Nbins;
  Cbinsd=(double)Cbins;
  
  /* Allocating array of SW sigma */
  plhs[0]=mxCreateDoubleMatrix(d-1,d-1,mxREAL);
  kappa=mxGetPr(plhs[0]);
    
  /* Rescaling the bound to be used inside the sum over empirical copula values */ 
  bound/=4.0;
  bound*=Nbinsd;

  if( Cbins>1 ) {
  /* Allocating binned empirical copula arrays */
    becd=new long[Nbins];
    bec=new long[Nbins];
  }
  
  /* Allocating auxiliary arrays */
  r_sorted=new long[N];
  ecc=new double[Nbins-1];
  
  for( j=0; j<d-1; j++ ) {
    for( k=j+1; k<d; k++ ) {
      /* Considering pair of variables (j,k) */

      /* Arranging indices for dimension k along sorted indices for dimension j */
      /* Ranges 0..N-1 */
      for( i=0; i<N; i++ )
        r_sorted[(long)r[i*d+j]-1]=(long)r[i*d+k]-1;

      /* Zeroing out current empirical copula column */ 
      for( i=0; i<Nbins-1; i++ )
        ecc[i]=0.0;

      if( Cbins>1 ) {
	/* Zeroing out binned empirical copula density column */
	for( i=0; i<Nbins; i++ )
	  becd[i]=0;
      }

      for( i=1; i<Nbins; i++ ) {
	if( Cbins>1 ) {
	  /* Initializing starting point for empirical copula column update */
	  curr_bin_min=Nbins;
	  
	  /* Populating binned empirical copula density */
	  for( l=0; l<Cbins; l++ ) {
	    /* Determining which row of the binned empirical copula to assign data point
	       with U-rank (i-1)*Cbins+(l+1) */
	    temp_index=(r_sorted[(i-1)*Cbins+l])/Cbins;
	    
	    /* Updating the column of the binned empirical copula */
	    becd[temp_index]++;
	    
	    /* Recording the index of the lowest row that was changed */
	    if( temp_index<curr_bin_min )
	      curr_bin_min=temp_index;
	  }
	  
	  /* Computing current binned empirical copula column update */
	  bec[curr_bin_min]=becd[curr_bin_min];
	  for( l=curr_bin_min+1; l<Nbins; l++ )
	    bec[l]=bec[l-1]+becd[l];
	}

	if( Cbins>1 ) {
	  /* Updating current binned empirical copula column */
	  for( l=curr_bin_min; l<Nbins-1; l++ ) {
	    ecc[l]+=(double)bec[l];
	  }
	} else {
	  /* Updating current empirical copula column */
	  for( l=r_sorted[i-1]; l<N-1; l++ ) {
	    ecc[l]+=1.0;
	  }
	}
	  
	/* Updating sigma */
	for( l=1; l<Nbins; l++ ) {
	  if( Cbins>1 )
	    Linf=fabs(ecc[l-1]/Cbinsd-(double)i*(double)l/Nbinsd);
	  else
	    Linf=fabs(ecc[l-1]-(double)i*(double)l/Nbinsd);
	  if( kappa[(k-1)*(d-1)+j]<Linf ) {
	    /* New L-inf value is larger */
	    kappa[(k-1)*(d-1)+j]=Linf;
	  }
	}
	  
	if( Cbins>1 ) {
	  /* De-populating binned empirical copula density (much faster than zeroing out) */
	  for( l=0; l<Cbins; l++ )
	    becd[r_sorted[(i-1)*Cbins+l]/Cbins]--;
	}

	if( kappa[(k-1)*(d-1)+j]>bound )
	  /* Already exceeded the bound */
	  /* Stopping */
	  i=Nbins;
	}
      
      /* Rescaling sigma to fall in [0,1] interval */
      kappa[(k-1)*(d-1)+j]*=4.0/Nbinsd;
    }
  }
  
  delete r_sorted;
  delete ecc;

  if( Cbins>1 ) {
    /* Deallocating binned empirical copula arrays */
    delete becd;
    delete bec;
  }
  
  return;
}

