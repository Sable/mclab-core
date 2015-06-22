# mclab-core
[![Travis Build Status](https://travis-ci.org/Sable/mclab-core.svg?branch=master)](https://travis-ci.org/Sable/mclab-core)

This is the Java-based infrastructure of the McLab project, which aims to
provide compiler tools and infrastructure for MATLAB (and potentially other
scientific languages in the future). In practice this repository houses
development for the project's "static" branch, which includes the frontend
(parsing, static analysis, refactoring, etc.), and work towards static
compilation.

Installation Instructions
-------------------------
McLab can be built by running the following command from the McLab root directory. 
```
cd languages/Natlab/ && ant build
```
In order to generate the jar file  run the following command
```
cd languages/Natlab/ && ant jar
``` 

Additional information on installation, usage and extending the framework can be found in the [mclab-core wiki](https://github.com/Sable/mclab-core/wiki). 

Copyright and License
---------------------
Copyright 2008-2013 Amina Aslam, Toheed Aslam, Ismail Badawi, Andrew Bodzay,
Andrew Casey, Maxime Chevalier-Boisvert, Jesse Doherty, Anton Dubrau,
Rahul Garg, Vineet Kumar, Nurudeen Lameed, Jun Li, Xu Li, Soroush Radpour,
Olivier Savary Belanger, Laurie Hendren, Clark Verbrugge and McGill
University.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this work except in compliance with the License. You may obtain a copy
of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the LICENSE.
