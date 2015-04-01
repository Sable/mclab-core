// =========================================================================== //
//                                                                             //
// Copyright 2011-2014 Anton Dubrau and McGill University,                     //
//                     Alen Stojanov and ETH Zurich                            //
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

package natlab.tame.classes.reference;


public enum PrimitiveClassReference implements BuiltinClassReference {

    LOGICAL { public boolean isNumeric () { return false; } },
    CHAR    { public boolean isNumeric () { return false; } },
    SINGLE  { public boolean isFloat   () { return true;  } },
    DOUBLE  { public boolean isFloat   () { return true;  } },
    INT8    { public boolean isInt     () { return true;  } },
    UINT8   { public boolean isInt     () { return true;  } },
    INT16   { public boolean isInt     () { return true;  } },
    UINT16  { public boolean isInt     () { return true;  } },
    INT32   { public boolean isInt     () { return true;  } },
    UINT32  { public boolean isInt     () { return true;  } },
    INT64   { public boolean isInt     () { return true;  } },
    UINT64  { public boolean isInt     () { return true;  } };

    private String name = this.name().toLowerCase();

    public boolean isInt     () { return false; }
    public boolean isFloat   () { return false; }
    public boolean isNumeric () { return true;  }
    public boolean isMatrix  () { return true;  }

    @Override public String  getName  () { return name;      }
    @Override public String  toString () { return getName(); }
    @Override public boolean isBuiltin() { return true;      }
}
