package com.volitic.plugin.ngdartfolding

import com.intellij.ide.projectView.impl.nodes.NestingTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode

import java.util.ArrayList

/**
 * A class that represents a group of related files to be folded together. For example a component dart file will be
 * grouped with its supporting html and css files as children.
 */
internal class FoldingGroup(private val primary: PsiFileNode) {

    private val relatedNodes = ArrayList<PsiFileNode>()

    fun addRelated(related: PsiFileNode) {
        relatedNodes.add(related)
    }

    fun build(): PsiFileNode {
        return if(relatedNodes.isNotEmpty()) {
            NestingTreeNode(primary, relatedNodes)
        } else {
            primary
        }
    }
}
