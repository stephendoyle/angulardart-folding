package com.volitic.plugin.ngdartfolding

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode

/**
 * TreeStructureProvider is where actual modifications to the project tree happen. Component dart files and their
 * associated supporting files are identified grouped together. The same is done for Dart files and their related
 * .g.dart part files.
 */
class TreeStructureProvider : com.intellij.ide.projectView.TreeStructureProvider {

    override fun modify(parent: AbstractTreeNode<*>,
                        children: Collection<AbstractTreeNode<*>>,
                        viewSettings: ViewSettings): Collection<AbstractTreeNode<*>> {


        val groups = HashMap<String, FoldingGroup>()
        val ngRelated = ArrayList<PsiFileNode>()
        val childrenProcessed = ArrayList<AbstractTreeNode<*>>()

        children.forEach { node ->
            if (node is PsiFileNode){

                if( FileId.isComponentAssoc(node) || FileId.isGeneratedDartFile(node)){
                    ngRelated.add(node)

                } else if(FileId.isDartFile(node, strict = true)){

                    val id = FileId.getIdentifier(node)
                    groups[id] = FoldingGroup(node) // create an FoldingGroup for this file

                } else {
                    childrenProcessed.add(node)
                }
            } else {
                childrenProcessed.add(node)
            }
        }

        ngRelated.forEach { related ->
            val group = groups[FileId.getIdentifier(related)]
            group?.addRelated(related) ?: childrenProcessed.add(related)
        }

        groups.forEach { _, group -> childrenProcessed.add( group.build()) }

        return childrenProcessed
    }
}
