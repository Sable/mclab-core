/*
Copyright 2011 Jesse Doherty, Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  
*/

package natlab.toolkits.analysis.varorfun;
/**
 * Datums used by var or function analysis.
 */

public enum VFDatum
{
    TOP, VAR, PREFIX, FUN, LDVAR, BOT, WAR;
    public VFDatum merge(VFDatum ov){
        if( this == ov )
            return this;

        if( BOT == this ) 
            return ov;

	if (BOT == ov)
            return this;

	if (LDVAR == this) 
	    return ov;

	if (LDVAR == ov)
	    return this;
	if (this==WAR || ov == WAR )
		return WAR;

	return TOP;
    }
    public boolean isFunction(){return this==FUN;}
    public boolean isVariable(){return this==VAR;}
    public boolean isID(){return this==BOT || this==LDVAR;}
}