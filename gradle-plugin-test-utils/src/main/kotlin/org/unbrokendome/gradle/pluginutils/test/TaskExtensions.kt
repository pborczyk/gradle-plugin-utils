package org.unbrokendome.gradle.pluginutils.test

import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.internal.TaskInternal
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.TaskOutputs
import org.gradle.internal.operations.BuildOperationDescriptor
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.workers.WorkerExecutor


/**
 * Executes a task.
 *
 * This works in a very limited way (e.g. it does not consider task dependencies), but it should be enough to have
 * Gradle run the task's actions in a somewhat realistic way.
 *
 * The [checkUpToDate] parameter can be used to control whether to run up-to-date checks, as defined by custom
 * [TaskOutputs.upToDateWhen] blocks. If the up-to-date checks are evaluated and the task should be considered
 * up-to-date, this function will set the [didWork][Task.getDidWork] flag on the task so it can be verified by a test.
 *
 * @receiver the [Task] to execute
 * @param checkUpToDate if `true`, run up-to-date checks first
 */
fun Task.execute(checkUpToDate: Boolean = true) {

    val services = (project as ProjectInternal).services

    val buildOperationExecutor = services[BuildOperationExecutor::class.java]
    val workerExecutor = services[WorkerExecutor::class.java]

    val buildOperation = buildOperationExecutor.start(BuildOperationDescriptor.displayName(name))

    try {
        if (checkUpToDate) {
            val upToDateSpec = (outputs as TaskOutputsInternal).upToDateSpec
            val upToDate = !upToDateSpec.isEmpty && upToDateSpec.isSatisfiedBy(this as TaskInternal)
            if (upToDate) {
                didWork = false
                return
            }
        }

        actions.forEach {
            it.execute(this)
        }

        workerExecutor.await()

    } finally {
        buildOperation.setResult(BuildResult(project.gradle, null))
    }
}


/**
 * Evaluates the task's [Task.onlyIf] specs to check if the task is skipped.
 *
 * @receiver the [Task] to check
 * @return `true` if the task is skipped
 */
fun Task.isSkipped(): Boolean {
    this as TaskInternal
    return onlyIf.isSatisfiedBy(this)
}

