package com.bumble.appyx.sandbox.client.workflow

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.sandbox.client.workflow.RootNode.NavTarget
import com.bumble.appyx.sandbox.client.workflow.RootNode.NavTarget.ChildOne
import com.bumble.appyx.sandbox.client.workflow.RootNode.NavTarget.ChildTwo
import kotlinx.parcelize.Parcelize

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = ChildOne,
        savedStateMap = buildContext.savedStateMap
    ),
    plugins: List<Plugin> = emptyList()
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack,
    plugins = plugins
) {

    suspend fun waitForChildTwoAttached(): ChildNodeTwo {
        return waitForChildAttached()
    }

    suspend fun attachChildTwo(): ChildNodeTwo {
        return attachWorkflow {
            backStack.push(ChildTwo)
        }
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        object ChildOne : NavTarget()

        @Parcelize
        object ChildTwo : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext) =
        when (navTarget) {
            is ChildOne -> ChildNodeOne(buildContext)
            is ChildTwo -> ChildNodeTwo(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { backStack.push(ChildOne) }) {
                    Text(text = "Push A")
                }
                Button(onClick = { backStack.push(ChildTwo) }) {
                    Text(text = "Push B")
                }
                Button(onClick = { backStack.pop() }) {
                    Text(text = "Pop")
                }
            }
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Children(navModel = backStack)
        }
    }
}
