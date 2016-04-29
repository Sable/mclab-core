if exist('OCTAVE_VERSION', 'builtin')    % standard test for octave as opposed to matlab
	mex                    -I../kdpee  private/kdpeemex.c ../src/kdpee.c
else
	if isunix
		mex CFLAGS='-std=gnu99 -D_GNU_SOURCE' -outdir private   -I../kdpee  private/kdpeemex.c ../src/kdpee.c
		%mex CFLAGS='-D_GNU_SOURCE' -outdir private   -I../kdpee  private/kdpeemex.c ../src/kdpee.c        %alternative-1
		%mex CFLAGS='-fPIC -D_GNU_SOURCE' -outdir private   -I../kdpee  private/kdpeemex.c ../src/kdpee.c  %alternative-2
	else ispc
		mex -outdir private   -I../kdpee  private/kdpeemex.c ../src/kdpee.c
	end
end

