// =========================================================================== //
//                                                                             //
// Copyright 2008-2012 Andrew Casey, Jun Li, Jesse Doherty,                    //
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

package natlab.options;
import java.util.LinkedList;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/** Natlab command-line options parser base class.
 */
abstract class OptionsBase {
  private String pad(int initial, String opts, int tab, String desc) {
    StringBuilder sb = new StringBuilder();
    sb.append(Strings.repeat(" ", initial)).append(opts);
    int i = opts.length() + initial;
    if (tab <= opts.length()) {
      sb.append("\n");
      i = 0;
    }
    sb.append(Strings.repeat(" ", tab - i + 1));
    for (String s : Splitter.on(CharMatcher.WHITESPACE).split(desc)) {
      if (i + s.length() > 78) {
        sb.append("\n");
        i = 0;
        sb.append(Strings.repeat(" ", tab - i + 1));
      }
      sb.append(s).append(" ");
      i += s.length() + 1;
    }
    return sb.append("\n").toString();
  }

  protected String padOpt(String opts, String desc) {
    return pad(1, opts, 30, desc);
  }

  protected String padVal(String vals, String desc ) {
    return pad(4, vals, 32, desc);
  }

  private final LinkedList<String> options = new LinkedList<>();
  protected void pushOptions( String s ) {
    options.addFirst( s );
  }

  protected boolean hasMoreOptions() {
    return !options.isEmpty();
  }

  protected String nextOption() {
    return options.removeFirst();
  }

  protected LinkedList<String> files = new LinkedList<>();
  public LinkedList<String> getFiles() { return files; }
}
