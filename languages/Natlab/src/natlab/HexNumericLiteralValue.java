// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.math.BigInteger;

/**
 * An integer literal specified in base 16.
 */
public class HexNumericLiteralValue extends IntNumericLiteralValue {
	public HexNumericLiteralValue(String text) {
		this(text, false);
	}

	public HexNumericLiteralValue(String text, boolean isImaginary) {
		super(text, parseHex(stripImaginary(text, isImaginary)), isImaginary);
	}

	private static BigInteger parseHex(String text) {
		if(!text.startsWith("0x")) {
			throw new NumberFormatException("Hex literals must begin with '0x': " + text);
		}
		return new BigInteger(text.substring(2), 16);
	}
}
