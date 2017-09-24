# Dear Diary
## 简介
Dear Diary 是我刚接触 SQLite 时做的一个非常简单的日记应用。  

项目最开始使用是 Java ,后来改成了 Kotlin。界面模仿了 iOS 上的日记应用《[素记](https://itunes.apple.com/us/app/%E7%B4%A0%E8%AE%B0-%E6%97%A5%E8%AE%B0%E6%9C%AC-%E7%AC%94%E8%AE%B0%E6%9C%AC-%E8%AE%B0%E4%BA%8B%E6%9C%AC/id1070487377?mt=8)》，架构方面参考了谷歌的 [todo-mvp](https://github.com/googlesamples/android-architecture/tree/todo-mvp/)。   

这个应用的功能非常简单，就是写日记和查询日记，另外还有一个定时提醒功能，实现起来也不难。  
如果你是一个 Android 初学者（当然我也是），刚接触了 SQLite，不妨拿这个项目来练练手，也欢迎提出 Issues.   

## 软件截图
<div>
<img src="https://github.com/wenhaiz/DearDiary/blob/master/img/diary_list.png" alt="list" width="400" height="700"/>
<img src="https://github.com/wenhaiz/DearDiary/blob/master/img/edit.png" alt="edit" width="400" height="700"/>
</div>

<div>
<img src="https://github.com/wenhaiz/DearDiary/blob/master/img/query_result.png" alt="query" width="400" height="700"/>
<img src="https://github.com/wenhaiz/DearDiary/blob/master/img/settings.png" alt="settings" width="400" height="700"/>
</div>

## 更新日志 
### V 1.3.1  2017-09-24
- 使用 `Kotlin` 语言特性优化代码
- 重命名包名为 `com.wenhaiz.deardiary`
- 调整布局     

### V 1.3.0  2017-07-27
- 转换成 `Kotlin` 语言
- 修复 bug  

### V 1.2.1  2017-07-09
- 使用 `ButterKnife`
- 规范变量命名

### V 1.2.0  2017-05-16
- 基于 `MVP` 重构项目
- 替换 `ListView` 为 `RecyclerView`

### V 1.1.0  2016-12-10
- 优化了一些代码

### V 1.0.0  2016-12-05
- 主要功能：日记撰写、日记内容查询和写日记提醒
- 主要组件：数据储存 `SQLite`、适配器 `BaseAdapter`、`ListView`、`BroadcastReceiver`、`Activity`、基础布局和控件
