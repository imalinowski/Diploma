package com.example.edge_entities.tasks

import com.example.edge_entities.EdgeDevice
import com.example.edge_entities.EdgeParams.MatrixMultiplyParams
import com.example.edge_entities.EdgeResult.MatrixMultiplyResult
import com.example.edge_entities.tasks.TaskStatus.IN_PROGRESS
import com.example.edge_entities.tasks.TaskStatus.NOT_STARTED
import com.example.edge_entities.tasks.TaskStatus.READY
import kotlin.math.max
import kotlin.math.min

const val MATRIX_MULTIPLY_NAME = "MatrixMultiply"

private typealias EdgeTaskMatrixMultiply = EdgeTask<MatrixMultiplySubTask, MatrixMultiplyResult>

open class MatrixMultiply(
    override val id: Int,
    protected val params: MatrixMultiplyParams
) : EdgeTaskMatrixMultiply {

    private val subTasks = mutableMapOf<EdgeDevice, MatrixMultiplySubTask>()

    override val name: String = "$MATRIX_MULTIPLY_NAME ${this.hashCode()}"

    protected open var status: TaskStatus = NOT_STARTED

    protected open var result = MatrixMultiplyResult(
        matrix = MutableList(params.matrixA.size) {
            MutableList(params.matrixB.size) { 0 }
        }
    )

    override fun parallel(devices: List<EdgeDevice>): Map<EdgeDevice, MatrixMultiplySubTask> {
        status = IN_PROGRESS
        val linesPartSize = max(params.matrixA.size / devices.size, 1)

        for (i in devices.indices) {
            if (linesPartSize * i > params.matrixA.size) {
                break
            }
            val subMatrixA = params.matrixA.subList(
                fromIndex = linesPartSize * i,
                toIndex = min((i + 1) * linesPartSize, params.matrixA.size)
            )
            val params = params.copy(matrixA = subMatrixA)
            val subTask = MatrixMultiplySubTask(
                id = params.getId(),
                firstLineIndex = linesPartSize * i,
                params = params,
                parentId = id
            )
            subTasks[devices[i]] = subTask
        }

        return subTasks
    }

    override fun completeSubTask(id: Int, result: MatrixMultiplyResult) {
        val task = subTasks.values.find { it.id == id }
        task?.let {
            task.completeTask(result)
            addToResult(task)
        }
        updateStatus()
    }

    override fun getCurrentStatus(): TaskStatus {
        return status
    }

    override fun getEndResult() = result

    override fun getInfo(): String {
        return "task : $name \n" +
            "matrixA size : ${params.matrixA.size}\n" +
            "matrixB size : ${params.matrixB.size}"
    }

    private fun updateStatus() {
        val notCompletedSubTasks = subTasks.values.count { it.status != READY }
        if (notCompletedSubTasks == 0) {
            status = READY
        }
    }

    private fun addToResult(task: MatrixMultiplySubTask) {
        val from = task.firstLineIndex
        val matrixSubResult = task.result.matrix
        val matrixResult = result.matrix.toMutableList()

        for (i in task.result.matrix.indices) {
            matrixResult[from + i] = matrixSubResult[i]
        }

        result = result.copy(matrix = matrixResult)
    }
}