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

    private val fileMock: PsiFile = mockk()
    private val nodeMock: AbstractTreeNode<Any> = mockk()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun isComponent() {

        missingNameMustFail(FileId::isComponent)
        useMockFileForNode()

        every { fileMock.name } returns "nondescript.file"
        assertFalse(FileId.isComponent(nodeMock), "Unrelated file should return false")

        every { fileMock.name } returns "something_component.sass"
        assertFalse(FileId.isComponent(nodeMock), "Component supporting files should return false")

        every { fileMock.name } returns "something_component.dart"
        assertTrue(FileId.isComponent(nodeMock), "Component file should return true")
    }

    @Test
    fun isComponentAssoc() {

        missingNameMustFail(FileId::isComponentAssoc)
        useMockFileForNode()

        every { fileMock.name } returns "nondescript.file"
        assertFalse(FileId.isComponent(nodeMock), "Unrelated file should return false")

        every { fileMock.name } returns "something_component.dart"
        assertFalse(FileId.isComponentAssoc(nodeMock), "Component file should return false")

        every { fileMock.name } returns "something_component.sass"
        assertTrue(FileId.isComponentAssoc(nodeMock), "Component supporting files should return true")
    }

    @Test
    fun isDartFile() {

        every { nodeMock.value } returns null
        assertFalse(FileId.isDartFile(nodeMock), "Should return false if node file is null")

        useMockFileForNode()

        every { fileMock.name } returns ""
        assertFalse(FileId.isDartFile(nodeMock), "Should return false if name is absent")
        useMockFileForNode()

        every { fileMock.name } returns "not_a_dart.file"
        assertFalse(FileId.isDartFile(nodeMock), "Non-Dart file should return false")

        every { fileMock.name } returns "is_a.dart"
        assertTrue(FileId.isDartFile(nodeMock), "Dart file should return true")

        every { fileMock.name } returns "generated.g.dart"
        assertTrue(FileId.isDartFile(nodeMock), "Generated Dart file should return true in non-strict mode")

        every { fileMock.name } returns "generated.g.dart"
        assertFalse(
            FileId.isDartFile(nodeMock, strict = true), "Generated Dart file should return false in strict mode"
        )
    }

    @Test
    fun isGeneratedDartFile(){

        missingNameMustFail(FileId::isGeneratedDartFile)
        useMockFileForNode()

        every { fileMock.name } returns "is_a.dart"
        assertFalse(FileId.isGeneratedDartFile(nodeMock), "Regular Dart file should return false")

        every { fileMock.name } returns "generated.g.dart"
        assertTrue(FileId.isGeneratedDartFile(nodeMock), "Generated Dart file should return true")

        every { fileMock.name } returns "unrelated-to-anything.txt"
        assertFalse(
            FileId.isGeneratedDartFile(nodeMock), "Unrelated file should return false"
        )


    }

    @Test
    fun getIdentifier() {


        val testValues: Array<Array<String>> = arrayOf(
            arrayOf("filename_component.dart", "filename_component"),
            arrayOf("filename-component.sass", "filename-component"),
            arrayOf("filename.component.tar.gz", "filename.component"),
            arrayOf("", ""),
            arrayOf("filename.dart", "filename"),
            arrayOf("filename.g.dart", "filename"),
            arrayOf("complicated.file.name.dart", "complicated.file.name")
        )

        every { nodeMock.value } returns fileMock

        testValues.forEach {
            every { fileMock.name } returns it[0]
            kotlin.test.assertEquals(
                it[1],
                FileId.getIdentifier(nodeMock),
                "should return filename without extension"
            )
        }
    }

    private fun missingNameMustFail( call: (n: AbstractTreeNode<*>) -> Boolean) {

        every { nodeMock.value } returns null
        assertFalse(call(nodeMock), "should return false if node is null")

        useMockFileForNode()

        every { fileMock.name } returns ""
        assertFalse(call(nodeMock), "should return false if name is empty")
    }

    private fun useMockFileForNode() {
        every { nodeMock.value } returns fileMock
    }
}