package com.volitic.plugin.ngdartfolding

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.psi.PsiFile

import java.util.regex.Pattern

/**
 * FileId contains helper functions used to identify and name file nodes in a project tree.
 */
internal object FileId {

    private val ngNamePattern = Pattern.compile(
            "^(.*)" // base
            + "_" // separator
            + "(component)" // ng type
            + "\\." + "([^\\.]*)$", // file type
            Pattern.CASE_INSENSITIVE)

    private fun nameFromNode(node: AbstractTreeNode<*>):String?{
        return (node.value as? PsiFile)?.name
    }

    private fun <T, R> defaultIfNull(arg: T?, default: R, fn: (T) -> R): R {
        if(arg == null) return default
        return fn.invoke(arg)
    }

    fun isNgType(node: AbstractTreeNode<*>): Boolean {
        return defaultIfNull(
                nameFromNode(node),
                false,
                { name -> ngNamePattern.matcher(name).matches() }
        )
    }

    fun isDartFile(node: AbstractTreeNode<*>): Boolean {
        return defaultIfNull(
                nameFromNode(node),
                false,
                { name -> name.endsWith(".dart") }
        )
    }

    fun getIdentifier(node: AbstractTreeNode<*>): String {

        return defaultIfNull(
                nameFromNode(node),
                "\\", // if no name is found the identifier will be a single backslash
                { name ->
                    val matcher = ngNamePattern.matcher(name)

                    if (matcher.find()) {
                        "\\" + // to differentiate from actual file names
                                matcher.group(1) + // base
                                matcher.group(2) //type
                    } else {
                        name
                    }
                }
        )
    }
}