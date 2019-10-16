/*
 * Copyright 2019 Google, Inc.
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

package com.example.jetnews.ui.article

import android.content.Context
import android.content.Intent
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.AlertDialog
import androidx.ui.material.AppBarIcon
import androidx.ui.material.Button
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeTextStyle
import com.example.jetnews.data.posts
import com.example.jetnews.model.Post
import com.example.jetnews.ui.Icons
import com.example.jetnews.ui.Screen
import com.example.jetnews.ui.home.BookmarkButton
import com.example.jetnews.ui.home.isFavorite
import com.example.jetnews.ui.home.toggleBookmark
import com.example.jetnews.ui.navigateTo

@Composable
fun ArticleScreen(icons: Icons, postId: String) {

    val showDialog = +state { false }
    // getting the post from our list of posts by Id
    val post = posts.find { it.id == postId } ?: return

    if (showDialog.value) {
        FunctionalityNotAvailablePopup {
            showDialog.value = false
        }
    }

    FlexColumn {
        inflexible {
            TopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = +themeTextStyle { subtitle2 }
                    )
                },
                navigationIcon = {
                    AppBarIcon(icons.back) {
                        navigateTo(Screen.Home)
                    }
                }
            )
        }
        expanded(1f) {
            PostContent(post)
        }
        inflexible {
            BottomBar(post, icons) { showDialog.value = true }
        }
    }
}

@Composable
private fun BottomBar(post: Post, icons: Icons, onUnimplementedAction: () -> Unit) {
    val context = +ambient(ContextAmbient)
    Surface(elevation = 2.dp) {
        Container(height = 56.dp, expanded = true) {
            FlexRow {
                inflexible {
                    BottomBarAction(icons.heartOff) {
                        onUnimplementedAction()
                    }
                    BookmarkButton(
                        isBookmarked = isFavorite(postId = post.id),
                        onBookmark = { toggleBookmark(postId = post.id) },
                        icons = icons)
                    BottomBarAction(icons.share) {
                        SharePost(post, context)
                    }
                }
                expanded(flex = 1f) {
                    WidthSpacer(1.dp)
                }
                inflexible {
                    BottomBarAction(icons.textSettings) {
                        onUnimplementedAction()
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBarAction(
    image: Image,
    onClick: () -> Unit
) {
    Ripple(
        bounded = false,
        radius = 24.dp
    ) {
        Clickable(onClick = onClick) {
            Container(width = 48.dp, height = 48.dp) {
                SimpleImage(image)
            }
        }
    }
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onCloseRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = +themeTextStyle { body2 }
            )
        },
        confirmButton = {
            Button(
                text = "CLOSE",
                style = TextButtonStyle(),
                onClick = onDismiss
            )
        }
    )
}

private fun SharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, "Share post"))
}
