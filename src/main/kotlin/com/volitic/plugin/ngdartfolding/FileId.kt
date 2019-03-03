package com.volitic.plugin.ngdartfolding

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.psi.PsiFile

import java.util.regex.Pattern

/**
 * FileId contains helper functions used to identify and name file nodes in a project tree.
 */
internal object FileId {

    // matching for component files
    private val ngNamePattern = Pattern.compile(
        "^(.*)" // base
                + "([_\\.-])" // separator
                + "(component)" // ng type
                + "\\." + "(.*)$", // file type
        Pattern.CASE_INSENSITIVE
    )

    // matching for .dart and .g.dart files
    private val dartPattern = Pattern.compile("^(.*?)(\\.g\\.dart|\\.dart)$", Pattern.CASE_INSENSITIVE)


    private fun nameFromNode(node: AbstractTreeNode<*>): String? {
        return (node.value as? PsiFile)?.name
    }

    /**
     * Returns the result of calling [fn] with [arg]. If [arg] is null then [default] is returned instead
     */
    private fun <T, R> defaultIfNull(arg: T?, default: R, fn: (T) -> R): R {
        if (arg == null) return default
        return fn.invoke(arg)
    }

    private fun isComponentType(node: AbstractTreeNode<*>): Boolean {
        return defaultIfNull(
            nameFromNode(node),
            false,
            { name -> ngNamePattern.matcher(name).matches() }
        )
    }

    fun isComponent(node: AbstractTreeNode<*>): Boolean {
        return isComponentType(node) && isDartFile(node)
    }

    fun isComponentAssoc(node: AbstractTreeNode<*>): Boolean {
        return isComponentType(node) && !isDartFile(node)
    }

    fun isDartFile(node: AbstractTreeNode<*>, strict: Boolean = false): Boolean {
        return if (strict) {
            !isGeneratedDartFile(node) && isDartFile(node)
        } else {
            defaultIfNull(
                nameFromNode(node),
                false,
                { name -> name.endsWith(".dart") }
            )
        }
    }

    fun isGeneratedDartFile(node: AbstractTreeNode<*>): Boolean {
        return defaultIfNull(
            nameFromNode(node),
            false,
            { name -> name.endsWith(".g.dart") }
        )
    }

    fun getIdentifier(node: AbstractTreeNode<*>): String {

        return defaultIfNull(
            nameFromNode(node),
            "",
            { name ->
                val ngMatcher = ngNamePattern.matcher(name)
                val dartMatcher = dartPattern.matcher(name)

                if (ngMatcher.find()) {
                    ngMatcher.group(1) + // base
                            ngMatcher.group(2) + //separator
                            ngMatcher.group(3) //type
                } else if (dartMatcher.find() && dartMatcher.group(1).isNotEmpty()) {
                    dartMatcher.group(1)
                } else {
                    name
                }
            }
        )
    }
}