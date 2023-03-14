package com.android.example.gesture

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.android.example.gesture.ui.theme.GESTURETheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GESTURETheme {
//                DraggableText() // 드래그 테스트
//                SwipeableSample() // 스와이프 테스트
                TransformableSample() //MultiTouchTest
            }
        }
    }
}

@Composable
fun DraggableText() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Text(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {// x y 축 둘다 사용할 수 있음.
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    Log.e("DRAG", "X : $offsetX")
                    Log.e("DRAG", "Y : $offsetY")
                }
            },
//            .draggable(// x y 축 둘 중 하나만 사용가능
//                orientation = Orientation.Horizontal,
//                state = rememberDraggableState { delta ->
//                    offsetX += delta
//                }
//            ),
        text = "Drag me!"
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableSample() {
    val width = 96.dp
    val squareSize = 48.dp

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    Log.e("SWIPE", "sizePx : $sizePx")
    Log.e("SWIPE", "anchor : $anchors")
    Log.e("SWIPE", "currentValue : ${swipeableState.currentValue}")

    Box(
        modifier = Modifier
            .width(width)
            .swipeable(
                state = swipeableState,
                anchors = anchors, //currentValue가 0, 1값을 가지는데 그 가질 위치를 정함.
                thresholds = { from, to -> FractionalThreshold(0.8f) }, // 얼만큼 땡겨야 currentValue가 바뀌는지
                orientation = Orientation.Horizontal
            )
            .background(Color.LightGray)
    ) {
        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(squareSize)
                .background(Color.DarkGray)
        )
    }
}

@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
//        rotation += rotationChange
//        offset += offsetChange

        Log.e("MULTI_TOUCH","scale : $scale")
        Log.e("MULTI_TOUCH","rotation : $rotation")
//        Log.e("MULTI_TOUCH","offsetX : ${offset.x}")
//        Log.e("MULTI_TOUCH","offsetY : ${offset.y}")
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
    )
}