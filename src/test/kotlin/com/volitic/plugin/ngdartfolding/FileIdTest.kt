package com.volitic.plugin.ngdartfolding

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.psi.PsiFile
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FileIdTest {

    private val psiFile: PsiFile = mockk()
    private val treeNode: AbstractTreeNode<Any> = mockk()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun isNgType() {

        every { treeNode.value } returns null
        assertFalse(FileId.isNgType(treeNode), "should return false if node is null")

        every { treeNode.value } returns psiFile

        every { psiFile.name } returns ""
        assertFalse(FileId.isNgType(treeNode), "should return false if name is empty")

        every { psiFile.name } returns "nondescript.file"
        assertFalse(FileId.isNgType(treeNode), "should return false if file name is non-angular")

        every { psiFile.name } returns "something_component.dart"
        assertTrue(FileId.isNgType(treeNode), "component file should return true")

        every { psiFile.name } returns "something_component.sass"
        assertTrue(FileId.isNgType(treeNode), "component supporting file should return true")
    }

    @Test
    fun isDartFile() {

        every { treeNode.value } returns null
        assertFalse(FileId.isDartFile(treeNode), "should return false if node is null")

        every { treeNode.value } returns psiFile

        every { psiFile.name } returns ""
        assertFalse(FileId.isDartFile(treeNode), "should return false if name is empty")

        every { psiFile.name } returns "not_a_dart.file"
        assertFalse(FileId.isDartFile(treeNode), "should return false if file name is not a dart file")

        every { psiFile.name } returns "is_a.dart"
        assertTrue(FileId.isDartFile(treeNode), "should return true if file is a dart file")
    }

    @Test
    fun getIdentifier() {


        val testValues: Array<Array<String>> = arrayOf(
            arrayOf("filename_component.dart", "filename_component"),
            arrayOf("filename-component.sass", "filename-component"),
            arrayOf("filename.component.tar.gz", "filename.component"),
            arrayOf("", "")
        )

        every { treeNode.value } returns psiFile

        testValues.forEach {
            every { psiFile.name } returns it[0]
            kotlin.test.assertEquals(it[1], FileId.getIdentifier(treeNode), "should return filename without extension")
        }
    }
}