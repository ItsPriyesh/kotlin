/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.cfg.Label
import java.util.Collections
import org.jetbrains.kotlin.cfg.pseudocode.instructions.LexicalScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.psi.KtReturnExpression

public class ReturnValueInstruction(
        returnExpression: KtExpression,
        lexicalScope: LexicalScope,
        targetLabel: Label,
        public val returnedValue: PseudoValue
) : AbstractJumpInstruction(returnExpression, targetLabel, lexicalScope) {
    override val inputValues: List<PseudoValue> get() = Collections.singletonList(returnedValue)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitReturnValue(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R {
        return visitor.visitReturnValue(this)
    }

    override fun toString(): String {
        return "ret(*|$returnedValue) $targetLabel"
    }

    override fun createCopy(newLabel: Label, lexicalScope: LexicalScope): AbstractJumpInstruction {
        return ReturnValueInstruction((element as KtExpression), lexicalScope, newLabel, returnedValue)
    }

    public val returnExpressionIfAny: KtReturnExpression? = element as? KtReturnExpression
}
