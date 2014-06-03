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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import beaver.Symbol;

/**
 * A queue of comments shared by the scanner and the parser.
 */
public class CommentBuffer {
  private final Queue<Symbol> commentQueue;

  public CommentBuffer() {
    this.commentQueue = new LinkedList<>();
  }

  public void pushComment(Symbol comment) {
    commentQueue.add(comment);
  }

  public Symbol peekComment() {
    return commentQueue.peek();
  }

  public Symbol pollComment() {
    return commentQueue.poll();
  }

  public boolean isEmpty(){
    return commentQueue.isEmpty();
  }

  public List<Symbol> pollAllComments() {
    List<Symbol> allComments = new ArrayList<>();
    allComments.addAll(commentQueue);
    commentQueue.clear();
    return allComments;
  }
}
