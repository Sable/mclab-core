%function [] = 	quick_test_Himreg()
%Quick test in image registration using entropy based similarity.

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

%clear start:
    close all; clear all;

%parameters:
    num_of_rots = 21; %number of rotations examined
    h = 0; %feature representation of an image = neighborhoods around the pixels of the image; neigbourhood: (2h+1)x(2h+1); h=0 <-> pixels 
    S0 = 101; %resize the original image to S0 x S0
    %noise parameters (see 'add_image_noise.m'):
        %I (no noise):
            %noise.name = 'no';
        %II (each pixel is 'flipped' with probability 'p'):
            noise.name = 'flip'; noise.p = 0.3;
        %III (noisy image = edge filtered version of the original one):
            %explicitly specify noise.threshold:
                %noise.name = 'sobel'; noise.threshold = 10/255;
            %noise.threshold is chosen automatically:    
                %noise.name = 'sobel';
    %method used to measure the similarity of 2 images (estimators of 'd=1' type do not directly cope with the problem):
        %base:
            cost_name = 'Shannon_kNN_k';     %h>=0
            %cost_name = 'Renyi_kNN_k';      %h>=0
            %cost_name = 'Renyi_kNN_1tok';   %h>=0
            %cost_name = 'Renyi_kNN_S';      %h>=0
            %cost_name = 'Renyi_weightedkNN';%h>=0
            %cost_name = 'Renyi_MST';        %h>=0, computationally intensive
            %cost_name = 'Tsallis_kNN_k';    %h>=0
            %cost_name = 'Shannon_Edgeworth';%h>=0
            %cost_name = 'Shannon_Voronoi';  %h>=1, computationally intensive
            %cost_name = 'Shannon_KDP';      %h>=0
            %cost_name = 'Renyi_expF';       %h>=0
            %cost_name = 'Tsallis_expF';     %h>=0
            %cost_name = 'Shannon_expF';     %h>=0
        %meta:
            %cost_name = 'ensemble';      %h>=0
            %cost_name = 'RPensemble';    %h>=0
            %cost_name = 'Tsallis_HRenyi';%h>=0
            %cost_name = 'Shannon_DKL_N'; %h>=0; for nrot=11, C becomes (naturally) degenerate; see 'HShannon_DKL_N_estimation.m': R = chol(C)
            %cost_name = 'Shannon_DKL_U'; %h>=0
         
%initialization:    
    rotations = linspace(-10,10,num_of_rots); %rotations (in degree) examined
    S = floor(S0/sqrt(2)); %maximal size that fits to an S0xS0 sized image under any rotation
    %target image:
        I_target_large = load('lena512.mat');%512x512
        I_target_large = imresize(I_target_large.lena512,[S0,S0])/255;%'/255': [0,255] -> '[0,1]';
        I_target_center = im_cut(I_target_large,S,S); %SxS
        %features of I_target_center:
            I_target_center_f = im2col(I_target_center,[2*h+1,2*h+1],'sliding');
    %add noise:
        figure;            
            subplot(2,1,1); imshow(I_target_center,[]); title('original image');
            I_target_large = add_image_noise(I_target_large,noise);
            subplot(2,1,2); imshow(im_cut(I_target_large,S,S),[]); title('noisy image to be registered (unrotated)');
    co = H_initialization(cost_name,1);
    %explicitly set the kNN method and its parameters:
        %co = H_initialization(cost_name,1,{'kNNmethod', 'ANN', 'k', 3, 'epsi', 0});
    sim_hat = zeros(length(rotations),1); %vector of estimated similarities
    nrot = 1; %rotation index
         
%estimate similarity for the possible transformations (rotations):
    for rot = rotations
        %rotated + cropped image:     
            I_transformed_large = imrotate(I_target_large,rot);
            I_transformed_center = im_cut(I_transformed_large,S,S);
            %features of I_transformed_center:
                I_transformed_center_f = im2col(I_transformed_center,[2*h+1,2*h+1],'sliding');
        %similarity(I_target_center,I_transformed_center):
            sim_hat(nrot) = -H_estimation([I_target_center_f;I_transformed_center_f],co); %joint entropy of the feature represenations of the images
        %disp:
            disp(strcat(['rotation index: ',num2str(nrot),'/',num2str(num_of_rots)]));
            nrot = nrot + 1;
    end
    
%plot:
    figure;
    plot(rotations,sim_hat);
    xlabel('Angle of rotation');
    ylabel('Estimated similarity: entropy');
    title('Ideally: maximal at the center');
