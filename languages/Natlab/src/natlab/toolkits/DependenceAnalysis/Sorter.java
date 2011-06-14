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
import java.util.Comparator;
import java.util.*;


public class Sorter {	
    
      LinkedList a;

      Comparator<Integer> c;


      Sorter(LinkedList a, Comparator<Integer> c) { this.a = a; this.c = c; }	
     
     //Sorter(int[] a, Comparator<Integer> c) { this.a = a; this.c = c; }


//TODO:Fix quick sort which is giving problems.
      void IntroSort(int f, int b) throws Exception
      {
      //  if (b - f > 31)
       // {
        //  int depth_limit = (int)Math.floor(2.5 * Math.ceil( (double)Math.log ( (double)b - f ) / Math.log(2)));

       //   introSort(f, b, depth_limit);
      //  }
      //  else {
        		
          //QuickSort(f, b-1);
        InsertionSort(f, b);
	   // }
      }


      private void introSort(int f, int b, int depth_limit) throws Exception
      {
        int size_threshold = 14;//24;

        if (depth_limit-- == 0)
          HeapSort(f, b);
        else if (b - f <= size_threshold) {
          	
          QuickSort(f, b-1);
          InsertionSort(f, b);
        } else
        {
          int p = partition(f, b);

          introSort(f, p, depth_limit);
          introSort(p, b, depth_limit);
        }
      }


      private int compare(int i1, int i2) { return i1-i2; }


      private int partition(int f, int b) throws Exception
      {
        int bot = f, mid = (b + f) / 2, top = b - 1;
        //int abot = a[bot], amid = a[mid], atop = a[top];
        int abot = ((UpperBound)((ProfiledData)a.get(bot)).getUBound()).getEnd();
        int amid=((UpperBound)((ProfiledData)a.get(mid)).getUBound()).getEnd();
        int atop=((UpperBound)((ProfiledData)a.get(top)).getUBound()).getEnd();

        //if (compare(abot, amid) < 0)
        if(abot<0 && amid<0)
        {
          //if (compare(atop, abot) < 0)//atop<abot<amid
          if(atop<0 && abot<0) 	
          { 
        	  //a[top] = amid; amid = a[mid] = abot; a[bot] = atop; 
        	  UpperBound ub=((ProfiledData)a.get(top)).getUBound();
        	  ub.setEnd(amid);
        	  amid=((UpperBound)((ProfiledData)a.get(mid)).getUBound()).getEnd();
        	  ub=((ProfiledData)a.get(mid)).getUBound();
        	  ub.setEnd(abot);
        	  ub=((ProfiledData)a.get(bot)).getUBound();
        	  ub.setEnd(atop);
        	  
          }
          //else if (compare(atop, amid) < 0) //abot<=atop<amid
          else if(atop<0 && amid<0)
          { 
        	 // a[top] = amid; amid = a[mid] = atop;
        	  UpperBound ub=((ProfiledData)a.get(top)).getUBound();
        	  ub.setEnd(amid);
        	  amid=((UpperBound)((ProfiledData)a.get(mid)).getUBound()).getEnd();
        	  ub=((ProfiledData)a.get(mid)).getUBound();
        	  ub.setEnd(atop);
        	  
          }
          //else abot<amid<=atop
        }
        else
        {
          //if (compare(amid, atop) > 0) //atop<amid<=abot
          if (amid > 0 && atop > 0) //atop<amid<=abot	
          { 
        	//  a[bot] = atop; a[top] = abot; 
        	  UpperBound ub=((ProfiledData)a.get(bot)).getUBound();
        	  ub.setEnd(atop);
        	  amid=((UpperBound)((ProfiledData)a.get(top)).getUBound()).getEnd();
        	  ub=((ProfiledData)a.get(mid)).getUBound();
        	  ub.setEnd(abot);        	  
          }
          //else if (compare(abot, atop) > 0) //amid<=atop<abot
          else if (abot > 0 && atop > 0)
          { 
        	  //a[bot] = amid; amid = a[mid] = atop; a[top] = abot;
        	  UpperBound ub=((ProfiledData)a.get(bot)).getUBound();
        	  ub.setEnd(amid);
        	  amid=((UpperBound)((ProfiledData)a.get(mid)).getUBound()).getEnd();
        	  ub=((ProfiledData)a.get(mid)).getUBound();
        	  ub.setEnd(atop);
        	  ub=((ProfiledData)a.get(top)).getUBound();
        	  ub.setEnd(abot);        	  
          }
          else //amid<=abot<=atop
          { //a[bot] = amid; amid = a[mid] = abot;
        	  UpperBound ub=((ProfiledData)a.get(bot)).getUBound();
        	  ub.setEnd(amid);
        	  amid=((UpperBound)((ProfiledData)a.get(mid)).getUBound()).getEnd();
        	  ub=((ProfiledData)a.get(mid)).getUBound();
        	  ub.setEnd(abot);   
        	  
          }
        }

        int i = bot, j = top;

        while (true)
        {
          //while (compare(a[++i], amid) < 0) ;
        	while (((UpperBound)((ProfiledData)a.get(++i)).getUBound()).getEnd() < 0 && amid < 0) ;

          //while (compare(amid, a[--j]) < 0) ;
        	while (amid < 0 && ((UpperBound)((ProfiledData)a.get(--j)).getUBound()).getEnd() < 0) ;	

          if (i < j)
          {
            //int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        	  int tmp=((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd();
        	  UpperBound ub=((ProfiledData)a.get(i)).getUBound();
        	  ub.setEnd(((UpperBound)((ProfiledData)a.get(j)).getUBound()).getEnd());
          }
          else
            return i;
        }
      }

	private void swap(int i, int j)
	{
		int T;
		//T = a[i];
		//a[i] = a[j];
		//a[j] = T;
		T=((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd();
		UpperBound ub=((ProfiledData)a.get(i)).getUBound();
    	ub.setEnd(((UpperBound)((ProfiledData)a.get(j)).getUBound()).getEnd());
    	ub=((ProfiledData)a.get(j)).getUBound();
    	ub.setEnd(T);	  
		
	}

  private void QuickSort(int l, int r) throws Exception
   {
	int M = 4;
	int i;
	int j;
	int v;
    
	if ((r-l)>M)
	{
		i = (r+l)/2;
		//if (a[l]>a[i]) swap(l,i);	// Tri-Median Methode!
		//if (a[l]>a[r]) swap(l,r);
		//if (a[i]>a[r]) swap(i,r);
		
		if (((UpperBound)((ProfiledData)a.get(l)).getUBound()).getEnd()>((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd()) swap(l,i);	// Tri-Median Method!
		if (((UpperBound)((ProfiledData)a.get(l)).getUBound()).getEnd()>((UpperBound)((ProfiledData)a.get(r)).getUBound()).getEnd()) swap(l,r);
		if (((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd()>((UpperBound)((ProfiledData)a.get(r)).getUBound()).getEnd()) swap(i,r);
		 

		j = r-1;
		swap(i,j);
		i = l;
		v = ((UpperBound)((ProfiledData)a.get(j)).getUBound()).getEnd();
		for(;;)
		{
			//while(a[++i]<v);
			//while(a[--j]>v);
			while(((UpperBound)((ProfiledData)a.get(++i)).getUBound()).getEnd()<v);
			while(((UpperBound)((ProfiledData)a.get(--j)).getUBound()).getEnd()>v);
			if (j<i) break;
			swap (i,j);
			//pause(i,j);
                       // if (stopRequested) {
                         //   return;
                       // }
		}
		swap(i,r-1);
		//pause(i);
		QuickSort(l,j);
		QuickSort(i+1,r);
	}
}

	private void InsertionSort(int lo0, int hi0) throws Exception
	{
		int i;
		int j;
		int v;
	 
		for (i=lo0+1;i<=hi0;i++)
		{
			//v = a[i];
			v=((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd();
			j=i;
			//while ((j>lo0) && (a[j-1]>v))
			while ((j>lo0) && (((UpperBound)((ProfiledData)a.get(j-1)).getUBound()).getEnd()>v))
			{
				//a[j] = a[j-1];
				UpperBound ub=((ProfiledData)a.get(j)).getUBound();
		    	ub.setEnd(((UpperBound)((ProfiledData)a.get(j-1)).getUBound()).getEnd());
				
				//pause(i,j);
				j--;
			}
			//a[j] = v;
			UpperBound ub=((ProfiledData)a.get(j)).getUBound();
	    	ub.setEnd(v);
	 	}
	}


      void HeapSort(int f, int b) throws Exception
      {
        //pause();
  
        for (int i = (b + f) / 2; i >= f; i--) heapify(f, b, i);

        for (int i = b - 1; i > f; i--)
        {
          //int tmp = a[f]; a[f] = a[i]; a[i] = tmp;
          int tmp = ((UpperBound)((ProfiledData)a.get(f)).getUBound()).getEnd();
          UpperBound ub=((ProfiledData)a.get(f)).getUBound();
	      ub.setEnd(((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd());
	      ub=((ProfiledData)a.get(i)).getUBound();
	      ub.setEnd(tmp);
          heapify(f, i, f);
        }
      }


      private void heapify(int f, int b, int i)
      {
        //int pv = a[i], lv, rv, max = pv;
    	int pv = ((UpperBound)((ProfiledData)a.get(i)).getUBound()).getEnd(), lv, rv, max = pv;  
        int j = i, maxpt = j;

        while (true)
        {
          int l = 2 * j - f + 1, r = l + 1;

          //if (l < b && compare(lv = a[l], max) > 0) { maxpt = l; max = lv; }
          //if (l < b && (a[l]>0 && max > 0)) { maxpt = l; max = a[l]; }
          if (l < b && (((UpperBound)((ProfiledData)a.get(l)).getUBound()).getEnd()>0 && max > 0)) { maxpt = l; max = ((UpperBound)((ProfiledData)a.get(l)).getUBound()).getEnd(); }

          //if (r < b && compare(rv = a[r], max) > 0) { maxpt = r; max = rv; }
          //if (r < b && (a[r] > 0 && max > 0)) { maxpt = r; max = a[r]; }
          if (r < b && ( ((UpperBound)((ProfiledData)a.get(r)).getUBound()).getEnd() > 0 && max > 0)) { maxpt = r; max =  ((UpperBound)((ProfiledData)a.get(r)).getUBound()).getEnd(); }

          if (maxpt == j)
            break;

          //a[j] = max;
          UpperBound ub=((ProfiledData)a.get(j)).getUBound();
	      ub.setEnd(max);
          max = pv;
          j = maxpt;
        }

        if (j > i){
          //a[j] = pv;
          UpperBound ub=((ProfiledData)a.get(j)).getUBound();
	      ub.setEnd(pv);
      }//end of if
    }
}
  



