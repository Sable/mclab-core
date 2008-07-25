/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package natlab.toolkits;
/**
 * Adapted from soot.Unit.java
 * Change Log:
 *  - 2008.06 by Jun Li
 *  	- changed package name from soot to natlab 
 */
import java.io.Serializable;

/** A box which can contain values. 
 *
 * @see Value
 */
public interface ValueBox extends Serializable	// Host, 
{
    /** Sets the value contained in this box as given.  
     * Subject to canContainValue() checks. */
    public void setValue(String value);
    // public void setValue(Value value);

    /** Returns the value contained in this box. */
    // public Value getValue();
    public String getValue();

    /** Returns true if the given Value fits in this box. */
    // public boolean canContainValue(Value value);
    public boolean canContainValue(String value);

    // public void toString( UnitPrinter up );

}
