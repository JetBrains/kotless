package io.kotless.parser.utils.psi.visitor

import io.kotless.parser.utils.psi.getTargets
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import java.util.*
import kotlin.collections.HashSet
import kotlin.reflect.KClass

/**
 * Implementation of Kotlin Visitor capable of following [KtReferenceExpression].
 *
 * [shouldFollowReference] can be used to control which references should be followed
 * [parentsWithReferences] used to get all parents including parents of all followed references
 * till that exact expression -- so it is [parents] following references upwards
 * [visitOnce] can be used to define if visitor should visit target more than once, if there
 * are different references targeting to one location
 */
abstract class KtReferenceFollowingVisitor(
    private val binding: BindingContext,
    private val visitOnce: Boolean = false
) : KtDefaultVisitor() {

    private val references: Stack<KtElement> = Stack()
    private val targets: Stack<KtElement> = Stack()
    private val seen: HashSet<KtElement> = HashSet()

    protected open fun shouldFollowReference(reference: KtElement, target: KtElement) = true

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        val targets = expression.getTargets(binding)
        for (target in targets) {
            visitReferenceTree(expression, target)
        }

        super.visitReferenceExpression(expression)
    }

    /**
     * Will invoke visitor on reference target if it should be followed (via [shouldFollowReference])
     *
     * Reference will be visited safely, seen nodes are tracked and will not be visited again -- so
     * no danger of cycle.
     */
    protected fun visitReferenceTree(reference: KtElement, target: KtElement) {
        if (target in this.targets || (visitOnce && target in seen) || !shouldFollowReference(reference, target)) return

        if (visitOnce) this.seen.add(target)

        this.references.push(reference)
        this.targets.push(target)
        target.accept(this)
        this.targets.pop()
        this.references.pop()
    }

    fun <T : Any> PsiElement.parentsWithReferences(klass: KClass<T>, filter: (T) -> Boolean = { true }): Sequence<T> = sequence {
        val toIterate = listOf(this@parentsWithReferences) + this@KtReferenceFollowingVisitor.references
        for (reference in toIterate) {
            yieldAll(reference.parents.filterIsInstance(klass.java).filter { filter(it) })
        }
    }
}
