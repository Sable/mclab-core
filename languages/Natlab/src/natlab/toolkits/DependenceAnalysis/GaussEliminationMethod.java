// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.DependenceAnalysis;

public class GaussEliminationMethod {
	
	public static double[][] invert(double a[][]) {
	    int n = a.length;
	    long startInverse;
		
	    startInverse = System.currentTimeMillis();

	    double x[][] = new double[n][n];
	    double b[][] = new double[n][n];
	    int index[] = new int[n];
	    for (int i=0; i<n; ++i) b[i][i] = 1;

	 // Transform the matrix into an upper triangle
	    System.out.println("Inverse using Gauss elimination");
	    gaussian(a, index);
      
	 // Update the matrix b[i][j] with the ratios stored
	    for (int i=0; i<n-1; ++i)
	      for (int j=i+1; j<n; ++j)
	        for (int k=0; k<n; ++k)
	          b[index[j]][k]
	            -= a[index[j]][i]*b[index[i]][k];

	 // Perform backward substitutions
	    for (int i=0; i<n; ++i) {
	      x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
	      for (int j=n-2; j>=0; --j) {
	        x[j][i] = b[index[j]][i];
	        for (int k=j+1; k<n; ++k) {
	          x[j][i] -= a[index[j]][k]*x[k][i];
	        }
	        x[j][i] /= a[index[j]][j];
	      }
	    }
	    long endInverse = System.currentTimeMillis();
	    System.out.println("total time for InverseCalculation: " + (endInverse - startInverse));
	    System.out.println("--------------------------\n\n");

	  return x;
	  }

	// Method to carry out the partial-pivoting Gaussian
	// elimination.  Here index[] stores pivoting order.

	  public static void gaussian(double a[][],
	    int index[]) {
	    int n = index.length;
	    double c[] = new double[n];

	 // Initialize the index
	    for (int i=0; i<n; ++i) index[i] = i;

	 // Find the rescaling factors, one from each row
	    for (int i=0; i<n; ++i) {
	      double c1 = 0;
	      for (int j=0; j<n; ++j) {
	        double c0 = Math.abs(a[i][j]);
	        if (c0 > c1) c1 = c0;
	      }
	      c[i] = c1;
	    }

	 // Search the pivoting element from each column
	    int k = 0;
	    for (int j=0; j<n-1; ++j) {
	      double pi1 = 0;
	      for (int i=j; i<n; ++i) {
	        double pi0 = Math.abs(a[index[i]][j]);
	        pi0 /= c[index[i]];
	        if (pi0 > pi1) {
	          pi1 = pi0;
	          k = i;
	        }
	      }

	   // Interchange rows according to the pivoting order
	      int itmp = index[j];
	      index[j] = index[k];
	      index[k] = itmp;
	      for (int i=j+1; i<n; ++i) {
	        double pj = a[index[i]][j]/a[index[j]][j];

	     // Record pivoting ratios below the diagonal
	        a[index[i]][j] = pj;

	     // Modify other elements accordingly
	        for (int l=j+1; l<n; ++l)
	          a[index[i]][l] -= pj*a[index[j]][l];
	      }
	    }
	  }


}
