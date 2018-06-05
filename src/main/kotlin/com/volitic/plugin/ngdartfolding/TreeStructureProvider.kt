package com.volitic.plugin.ngdartfolding

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode

/**
 * TreeStructureProvider is where actual modifications to the project Tree happen. Component dart files are identified
 * along with any other files related to that component, and they are moved into their own group.
 */
class TreeStructureProvider : com.intellij.ide.projectView.TreeStructureProvider {

    override fun modify(parent: AbstractTreeNode<*>,
                        children: Collection<AbstractTreeNode<*>>,
                        viewSettings: ViewSettings): Collection<AbstractTreeNode<*>> {


        val groups = HashMap<String, NgDartGroup>()
        val ngRelated = ArrayList<PsiFileNode>()
        val childrenProcessed = ArrayList<AbstractTreeNode<*>>()

        children.forEach { node ->
            if (node is PsiFileNode
                    && FileId.isNgType(node)) {

                if (FileId.isDartFile(node)) {
                    val id = FileId.getIdentifier(node)
                    groups[id] = NgDartGroup(node) // create an NgDartGroup for this component

                } else {
                    ngRelated.add(node)
                }
            } else {
                childrenProcessed.add(node)
            }
        }

        ngRelated.forEach { related ->
            val group = groups[FileId.getIdentifier(related)]
            group?.addRelated(related) ?: childrenProcessed.add(related)
        }

        groups.forEach { _, group -> childrenProcessed.add(group.build()) }

        return childrenProcessed
    }
}
