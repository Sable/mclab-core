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


    public static PrimitiveClassReference fromString (String s) {
        switch (s) {
            case "LOGICAL": return PrimitiveClassReference.LOGICAL;
            case "CHAR"   : return PrimitiveClassReference.CHAR;
            case "SINGLE" : return PrimitiveClassReference.SINGLE;
            case "DOUBLE" : return PrimitiveClassReference.DOUBLE;
            case "INT8"   : return PrimitiveClassReference.INT8;
            case "UINT8"  : return PrimitiveClassReference.UINT8;
            case "INT16"  : return PrimitiveClassReference.INT16;
            case "UINT16" : return PrimitiveClassReference.UINT16;
            case "INT32"  : return PrimitiveClassReference.INT32;
            case "UINT32" : return PrimitiveClassReference.UINT32;
            case "INT64"  : return PrimitiveClassReference.INT64;
            case "UINT64" : return PrimitiveClassReference.UINT64;
            default       : throw new UnsupportedOperationException("Unknown PrimitiveClassReference: " + s);
        }
    }

    @Override public String  getName  () { return name;      }
    @Override public String  toString () { return getName(); }
    @Override public boolean isBuiltin() { return true;      }
}
