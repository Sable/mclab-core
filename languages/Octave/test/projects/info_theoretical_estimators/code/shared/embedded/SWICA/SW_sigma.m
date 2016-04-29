function sigma=SW_sigma(r,bound,Cbins);
%SW_sigma -- sample version of Schweizer-Wolff sigma measure of dependence
% SW_sigma computes Schweizer-Wolff sigma (L1) measure of dependence using
% empirical copula (given ranks of data). Approximation is obtained by summing
% the empirical copula on a sparse regular grid.
%
% SW_sigma accelerates the computation in two possible ways.  One, it
% provides an option of returning the minimum of the actual SW sigma or the
% upper bound, saving computation on not performing the sum over all N^2
% evaluations of empirical copula.  Two, if the number of data points is too
% large, an approximation is obtained by summing the empirical copula on 
% a sparse regular grid.
%
% See Kirshner, S. and Poczos, B., "ICA and ISA Using Schweizer-Wolff Measure 
% of Dependence," ICML-2008
% For basic information on empirical copulas, see "Introduction to Copulas",
% Nelsen 2006, pp. 207-209 and 219.
% Reference for Schweizer-Wolff sigma: "On nonparametric measures of
% dependence for random variables", B. Schweizer, E.F. Wolff, The Annals of
% Statistics, 1981, v. 9(4), pp. 879-885.
%
% INPUTS:
%   r     -- dxN array of ranks for data
%   bound -- (optional, default=1) upper bound on sigma
%   Cbins -- (optional, default=1) number of data points falling in a
%            horizonal or a vertical cell
%
% OUTPUTS:
%   sigma -- (d-1)x(d-1) upper triangular array of Schweizer-Wolff sigma
%            measures of dependence (for all pairs of variables)
%
%Sergey Kirshner (sergey@cs.ualberta.ca)
%May 7, 2008

if( nargin<1 )
  error( 'SW_sigma: Need at least one input argument.' );
end

if( nargin<3 )
  Cbins=1;
  if( nargin<2 )
    bound=1;
  end
end

% Determining the size of data
[d,N]=size(r);
  
% Determining the number of evaluation points on the horizontal and vertical
% axes with empirical copula evaluated at (i*Cbins/N,j*Cbins/N), i,j=1,Nbins.
Nbins=N/Cbins;
    
% Initializing sigma
sigma=zeros([d-1 d-1]);    

% Rescaling the bound to be used inside the sum over empirical copula values 
bound=bound*Nbins*(Nbins^2-1)/12;
  
for j=1:d-1
  for k=j+1:d
    % Considering pair of variables (j,k)

    % Arranging indices for dimension k along sorted indices for dimension j
    for i=1:N
      r_sorted(r(j,i))=r(k,i);
    end

    % Empirical copula column
    ecc=zeros([Nbins 1]);

    if( Cbins>1 )
      % Binned empirical copula density column
      becd=zeros([Nbins 1]);
    end

    for i=1:Nbins-1
      if( Cbins>1 )
        % Initializing starting point for empirical copula column update
        curr_bin_min=Nbins;
	  
        % Populating binned empirical copula density
        for l=1:Cbins
          % Determining which row of the binned empirical copula to assign
          % a data point
          temp_index=ceil(r_sorted((i-1)*Cbins+l)/Cbins);
	    
          % Updating the column of the binned empirical copula
          becd(temp_index)=becd(temp_index)+1;
	    
          % Recording the index of the lowest row that was changed
          if( temp_index<curr_bin_min )
            curr_bin_min=temp_index;
          end
        end
          
        % Computing current binned empirical copula column update
        bec(curr_bin_min:Nbins-1,1)=cumsum(becd(curr_bin_min:Nbins-1));
      end

      if( Cbins>1 )
        % Updating current binned empirical copula column
        ecc(curr_bin_min:Nbins-1)=ecc(curr_bin_min:Nbins-1)+bec(curr_bin_min:Nbins-1);
      else
        % Updating current empirical copula column
        ecc(r_sorted(i):Nbins-1)=ecc(r_sorted(i):Nbins-1)+ones([Nbins-r_sorted(i) 1]);
      end

      % Updating sigma    
      if( Cbins>1 )
        sigma(j,k-1)=sigma(j,k-1)+sum(abs(ecc(1:Nbins-1)/Cbins-i*[1:Nbins-1]'/Nbins));
      else
        sigma(j,k-1)=sigma(j,k-1)+sum(abs(ecc(1:Nbins-1)-i*[1:Nbins-1]'/Nbins));
      end
      
      if( Cbins>1 )
        % Depopulating binned empirical copula density (much faster than
        % zeroing out)
        for l=1:Cbins
          becd(ceil(r_sorted((i-1)*Cbins+l)/Cbins))=becd(ceil(r_sorted((i-1)*Cbins+l)/Cbins))-1;
        end
      end

      if( sigma(j,k-1)>bound )
        % Already exceeded the bound -- stopping
        break;
      end
    end    
  end
end

% Rescaling sigma to fall in [0,1] interval
sigma=12*sigma/(Nbins*(Nbins^2-1));
