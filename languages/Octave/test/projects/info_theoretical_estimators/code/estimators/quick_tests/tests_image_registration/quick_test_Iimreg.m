%function [] = 	quick_test_Iimreg()
%Quick test in image registration using mutual information based similarity.

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
            cost_name = 'GV';   %h>=0
            %cost_name = 'HSIC'; %h>=0
            %cost_name = 'KCCA'; %h>=0
            %cost_name = 'KGV';  %h>=0
            %cost_name = 'Hoeffding'; %h=0
            %cost_name = 'SW1';   %h=0
            %cost_name = 'SWinf'; %h=0
            %cost_name = 'QMI_CS_KDE_direct'; %h=0;  computationally intensive
            %cost_name = 'QMI_CS_KDE_iChol';  %h>=0; computationally intensive
            %cost_name = 'QMI_ED_KDE_iChol';  %h>=0; computationally intensive
            %cost_name = 'dCov'; %h>=0
            %cost_name = 'dCor'; %h>=0
            %cost_name = 'Shannon_AP2'; %h=0
            %cost_name = 'Shannon_AP';  %h=0
        %meta:
            %cost_name = 'L2_DL2';
            %cost_name = 'Renyi_DRenyi';  %h>=0
            %cost_name = 'MMD_DMMD';      %h=0
            %cost_name = 'Renyi_HRenyi';  %h=0
            %cost_name = 'Shannon_HShannon'; %h>=0
            %cost_name = 'Tsallis_DTsallis'; %h>=0
            %cost_name = 'dCov_IHSIC';       %h>=0
            %cost_name = 'ApprCorrEntr';     %h=0; computationally intensive
            %cost_name = 'ChiSquare_DChiSquare'; %h>=0
	        %cost_name = 'Shannon_DKL'; %h>=0
         
%initialization: 
    ds = (2*h+1)^2 *ones(2,1);
    rotations = linspace(-10,10,num_of_rots); %rotations (in degree) examined
    S = floor(S0/sqrt(2)); %maximal size that fits to an S0xS0 sized image under any rotation
    %target image:
        I_target_large = load('lena512.mat');%512x512
        I_target_large = imresize(I_target_large.lena512,[S0,S0])/255;%'/255': [0,255] -> '[0,1]'
        I_target_center = im_cut(I_target_large,S,S); %SxS
        %features of I_target_center:
            I_target_center_f = im2col(I_target_center,[2*h+1,2*h+1],'sliding');
    %add noise:
        figure;            
            subplot(2,1,1); imshow(I_target_center,[]); title('original image');
            I_target_large = add_image_noise(I_target_large,noise);
            subplot(2,1,2); imshow(im_cut(I_target_large,S,S),[]); title('noisy image to be registered (unrotated)');
    co = I_initialization(cost_name,1);
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
            sim_hat(nrot) = I_estimation([I_target_center_f;I_transformed_center_f],ds,co); %mutual information of the feature representations of the images
            %sim_hat(nrot) = mean(sum((I_target_center_f-I_transformed_center_f).^2));%
        %disp:
            disp(strcat(['rotation index: ',num2str(nrot),'/',num2str(num_of_rots)]));
            nrot = nrot + 1;
    end
    
%plot:
    figure;
    plot(rotations,sim_hat);    
    xlabel('Angle of rotation');
    ylabel('Estimated similarity: mutual information');
    title('Ideally: maximal at the center');
