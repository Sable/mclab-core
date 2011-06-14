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

/** 
 * The value of a *_NUMBER_LITERAL token.
 * Contains both String and Number representations.
 */
public abstract class NumericLiteralValue {
	private final String text;
	private final boolean isImaginary;

	public NumericLiteralValue(String text, boolean isImaginary) {
		this.text = text;
		this.isImaginary = isImaginary;
	}

	public String getText() {
		return text;
	}

	public boolean isImaginary() {
		return isImaginary;
	}

	public abstract Number getValue();

	public String toString() {
		return getValue() + (isImaginary ? "i" : "") + " as '" + text + "'";
	}

	protected static String stripImaginary(String text, boolean isImaginary) {
		return isImaginary ? text.substring(0, text.length() - 1) : text;
	}
}
