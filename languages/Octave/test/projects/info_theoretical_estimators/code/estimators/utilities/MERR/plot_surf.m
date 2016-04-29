function [] = plot_surf(s,kp_v,rp_v,base_kp,base_rp,surf_name)
%function [] = plot_surf(s,kp_v,rp_v,base_kp,base_rp,surf_name)
%Plots the (log) validation/test surface.

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

figure;
    imagesc(log(s)); axis image; %log(): log of the surface; squares
    title(surf_name);
    %xlabel:
        xlabel(strcat('log_{',num2str(base_kp),'}(kernel parameter)'));
        [XTick,XTickLabel] = create_labels(kp_v,base_kp);
        set(gca,'XTick',XTick,'XTickLabel',XTickLabel);
    %ylabel:
        ylabel(strcat('log_{',num2str(base_rp),'}(ridge regularization parameter)'));
        [YTick,YTickLabel] = create_labels(rp_v,base_rp);
        set(gca,'YTick',YTick,'YTickLabel',YTickLabel);
    colorbar;
