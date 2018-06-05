package com.volitic.plugin.ngdartfolding

import com.intellij.ide.projectView.impl.nodes.NestingTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode

import java.util.ArrayList

/**
 * A class that represents a group of related component filess. For instance a component dart file will be grouped with
 * it's supporting html and css files. Conventionally the dart file will be the primary node / group parent.
 */
internal class NgDartGroup(private val primary: PsiFileNode) {

    private val relatedNodes = ArrayList<PsiFileNode>()

    fun addRelated(related: PsiFileNode) {
        relatedNodes.add(related)
    }

    fun build(): NestingTreeNode {
        return NestingTreeNode(primary, relatedNodes)
    }
}
