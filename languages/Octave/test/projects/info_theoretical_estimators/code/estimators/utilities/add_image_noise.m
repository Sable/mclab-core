function [I2] = add_image_noise(I1,noise)
%function [I2] = add_image_noise(I1,noise)
%Adds noise to image I1; the result is I2.
%
%INPUT: 
%   noise : structure describing the image noise, possibilities:
%      noise.name = 'no'  : no noise, i.e., I2 = I1;
%      noise.name = 'flip': with probability noise.p each pixel of I1 is changed to 255 minus the pixel value.
%      noise.name = 'sobel'; edge-filtered (sobel) image, noise.threshold is chosen automatically
%      noise.name = 'sobel': noise.threshold = 10; edge-filtered (sobel) image, explicitly specify noise.threshold.
                
%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

value_max = 1;

switch noise.name 
    case 'no' %no noise
        I2 = I1;
    case 'flip' %P(i->1-i)=p, P(i->i)=1-p
        p = noise.p;
        mask = (rand(size(I1)) < p); 
        I2 = value_max*mask - 2 * (mask-1/2) .* I1;
    case 'sobel' %edge filtered image
        if any(strcmp('threshold',fieldnames(noise))) %noise.threshold: exists
            I2 = edge(I1,'sobel',noise.threshold);
        else
            I2 = edge(I1,'sobel');
        end
    otherwise
        error('Noise=?');
end
        