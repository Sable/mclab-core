/*
 *  Copyright (c) 2018. David Fernando Herrera, McGill University
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 *  OF ANY KIND, either express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 */
package natlab.tame.simplification;

import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashSet;

import ast.AssignStmt;
import ast.ASTNode;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;
import natlab.toolkits.rewrite.simplification.FullSimplification;

import natlab.tame.tir.TIRCallStmt;


public class InputCallSimplification extends AbstractSimplification {

    public InputCallSimplification(ASTNode<?> tree, VFPreorderAnalysis nameResolver) {
        super(tree,nameResolver);
    }
    @Override
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        LinkedHashSet<Class<? extends AbstractSimplification>> dependencies =
                new LinkedHashSet<Class<? extends AbstractSimplification>>();
        dependencies.add( SwitchSimplification.class );
        dependencies.add( FullSimplification.class );
        dependencies.add( ThreeAddressToIR.class );
        return dependencies;
    }

    @Override
    public void caseAssignStmt(AssignStmt node) {
        if(node instanceof TIRCallStmt){
            System.out.println("WHAT DAVID");
        }
        System.out.println("Davidh" + node);
    }
}
