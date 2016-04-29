
function mag=diffraction(CELLS, SLITSIZE1, SLITSIZE2, T1, T2)
%-----------------------------------------------------------------------
%
%	This function M-file calculates the diffraction pattern of
%	monochromatic light through a transmission grating for
%	arbitrary slit sizes and slit transmission coefficients.
%
%	This MATLAB program is intended as a pedagogical example
%	of how diffraction and interference arise strictly from
%	the wave nature of light.
%
%	Invocation:
%		>> mag=diffraction(CELLS, SSIZE1, SSIZE2, T1, T2)
%
%		where
%
%		i. CELLS is the number of pairs of slits,
%
%		i. SSIZE1 is the width of slit 1 in meters,
%
%		i. SSIZE2 is the width of slit 2 in meters,
%
%		i. T1 is the transmission coefficient of slit 1,
%
%		i. T2 is the transmission coefficient of slit 2.
%
%		o. mag is the result.
%
%	Requirements:
%		0 < T1, T2 < 1.
%
%	Examples:
%		% One-slit diffraction.
%		>> mag=diffraction(1, 1e-5, 1e-5, 1, 0)
%
%		% Young's two-slit experiment.
%		>> mag=diffraction(2, 1e-5, 1e-5, 1, 0)
%
%	Source:
%		MATLAB 5 user contributed M-Files at
%		http://www.mathworks.com/support/ftp/.
%		("Functions Related to Physics" category.)
%
%	Author:
%		Ian Appelbaum (appeli@mit.edu).
%
%	Date:
%		December 1999.
%
%-----------------------------------------------------------------------

DISTANCE=5; % Distance from slit to screen in meters.
WAVELENGTH=633e-9; % Wavelength of light in meters
		     % (633 nm is HeNe laser line).
K=2*pi/WAVELENGTH; % Wavenumber.
CELLSIZE=SLITSIZE1+SLITSIZE2;

% The following constants are calculated from the inputs assuming that
% SLITX >> WAVELENGTH.

% Resolution of position of sources at slit.
SLITRES=WAVELENGTH/100;

% Resolution of pattern at screen.
SCREENRES=DISTANCE/(CELLS*10)*WAVELENGTH/mean([SLITSIZE1,SLITSIZE2]);

% Distance from center point to end of screen in meters.
SCREENLENGTH=3*DISTANCE*WAVELENGTH/mean([SLITSIZE1,SLITSIZE2]);

mag=zeros(0, 2);
for screenpt=0:SCREENRES:SCREENLENGTH,
    wavesum=0;
    for cellnum=0:(CELLS-1),
	for sourcept=0:SLITRES:SLITSIZE1, % First slit.
	    horizpos=(screenpt-(cellnum*CELLSIZE+sourcept));
	    x=abs(horizpos+i*DISTANCE);

	    % Add up the wave contribution from the first slit.
	    wavesum=wavesum+T1*exp(i*K*x);
	end;
	for sourcept=0:SLITRES:SLITSIZE2, % Second slit.
	    horizpos=(screenpt-(cellnum*CELLSIZE+ ...
	    SLITSIZE1+sourcept));
	    x=abs(horizpos+i*DISTANCE);

	    % Add up the wave contribution from the second slit.
	    wavesum=wavesum+T2*exp(i*K*x);
	end;
    end;
    intensity=(abs(wavesum))^2; % Intensity is electric field
				  % squared.

    % Normalize intensity so that it is approximately 1.
    newdata=[screenpt*100 intensity/(CELLS*CELLSIZE/SLITRES)];

    mag=[mag; newdata];
end;

end
