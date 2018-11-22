# dazzling
[![jcenter](https://api.bintray.com/packages/iota9star/nichijou/dazzling/images/download.svg)](https://bintray.com/iota9star/nichijou/dazzling/_latestVersion) [![Build Status](https://travis-ci.org/iota9star/dazzling-android-kt.svg?branch=master)](https://travis-ci.org/iota9star/dazzling-android-kt) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/8a916e4e09e04bea8bc7a3b439e673ed)](https://www.codacy.com/app/iota9star/dazzling-android-kt?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=iota9star/dazzling-android-kt&amp;utm_campaign=Badge_Grade)[![License](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16)

----
### 简介
&emsp;&emsp;dazzling 是一个漂亮、强大且简洁易用的调色板，由Kotlin实现。
#### 下载示例应用：[simple-release.apk](https://github.com/iota9star/dazzling-android-kt/raw/master/simple/release/simple-release.apk "simple-release.apk")
| ![1](https://github.com/iota9star/dazzling-android-kt/blob/master/simple/release/1.png "1") | ![2](https://github.com/iota9star/dazzling-android-kt/blob/master/simple/release/2.png "2") | ![3](https://github.com/iota9star/dazzling-android-kt/blob/master/simple/release/3.png "3") |
| :------: | :------: | :------: |
#### -> 引入到项目中
``` gradle
dependencies {
  // 其他
  implementation 'io.nichijou:dazzling:0.0.2'
}
```
#### -> 快速使用
``` kotlin
Dazzling.showNow(supportFragmentManager) { //BottomSheetDialogFragment对应的三个show方法
    isEnableAlpha = false // 是否支持alpha值，默认为true
    isEnableColorBar = false // 是否开启滑动条，默认为true
    presetColors = mutableListOf(Color.WHITE, Color.BLACK) // 设置的默认颜色，默认显示随机色
    preselectedColor = Color.YELLOW // 预先选中的颜色，默认为#7f7f7f
    backgroundColor = Color.WHITE // 调色板的背景色，默认为Color.WHITE
    randomSize = 16 // 显示随机颜色的个数，取值范围 > 0 默认为 8
    stepFactor = .5f // 颜色的色阶因子，取值范围 0.0f~2.0f 默认为 .2f
    onColorChecked { color ->
        // 当某个颜色被选中时就会触发一次回调
    }
    onOKPressed { color ->
        // 当确认按钮被触发时的回调，返回选中的颜色同时关闭调色板
    }
}
```
#### -> 提供的颜色API
``` kotlin
Int.setAlpha(alpha: Int) // 修改颜色的alpha值 0~255
Int.setRed(red: Int) // 修改颜色的red值 0~255
Int.setGreen(green: Int) // 修改颜色的green值 0~255
Int.setBlue(blue: Int) // 修改颜色的blue值 0~255
Int.stripAlpha() // 去除颜色的alpha值
Int.adjustAlpha(@FloatRange(from = 0.0, to = 1.0) factor: Float) // 修改颜色的alpha值 0.0~1.0
Int.saturationColor(@FloatRange(from = 0.0, to = 2.0) by: Float) // 修改颜色的饱和度
Int.brightenColor(@FloatRange(from = 0.0, to = 2.0) by: Float) // 修改颜色的亮度
Int.blendWith(@ColorInt color: Int, ratio: Float) // 按比例混合颜色
Int.titleColor() // 当前颜色背景适合的标题字体颜色
Int.bodyColor() // 当前颜色背景适合的内容字体颜色
Int.isColorLight() // 当前颜色是否为亮色
Int.isColorDark() // 当前颜色是否为暗色
Int.isColorLight(@ColorInt bgColor: Int) // 在有透明度时，看到的颜色是否为亮色
Int.stepColor(@FloatRange(from = 0.01, to = 2.00) factor: Float = 0.20f) // 返回颜色的色阶 0.01~2.00
Int.colorAlpha() // 当前颜色的alpha值 0~255
Int.toHexColor() // 当前颜色hex值 eg: Color.WHITE.toHexColor() -> FFFFFFFF
randomColor() // 返回一个随机颜色，不包含alpha值
randomColors(size: Int) // 返回一个指定数量的随机颜色集合
randomColors(range: IntRange) // 返回一个范围内数量的随机颜色集合
```
----
### Licenses
``` plain
   Copyright 2018 iota9star

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```