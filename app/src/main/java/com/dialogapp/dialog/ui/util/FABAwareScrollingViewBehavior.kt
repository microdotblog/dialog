/*
 * Copyright 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dialogapp.dialog.ui.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FabAwareScrollingViewBehavior(context: Context, attrs: AttributeSet)
    : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return super.layoutDependsOn(parent, child, dependency) || dependency is FloatingActionButton
    }

    override fun onStartNestedScroll(@NonNull coordinatorLayout: CoordinatorLayout,
                                     @NonNull child: View, @NonNull directTargetChild: View,
                                     @NonNull target: View, axes: Int, type: Int): Boolean {
        // Ensure we react to vertical scrolling
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedPreScroll(@NonNull coordinatorLayout: CoordinatorLayout,
                                   @NonNull child: View, @NonNull target: View, dx: Int, dy: Int,
                                   @NonNull consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        if (dy > 0) {
            // User scrolled down -> hide the FAB
            val dependencies = coordinatorLayout.getDependencies(child)
            for (view in dependencies) {
                if (view is FloatingActionButton) {
                    view.hide()
                }
            }
        } else if (dy < 0) {
            // User scrolled up -> show the FAB
            val dependencies = coordinatorLayout.getDependencies(child)
            for (view in dependencies) {
                if (view is FloatingActionButton) {
                    view.show()
                }
            }
        }
    }
}