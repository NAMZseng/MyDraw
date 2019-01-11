## MyDraw——基于SWT的绘图板程序
---
### 1. 功能介绍
 - 实现绘制图形的选择，如矩形，圆形、圆角矩形等；
 - 实现图形颜色及线条的选择；
 - 实现鼠标移动绘制图形时的虚线矩形框的视觉效果；
 - 实现文件的保存与打开；
 - 实现新增图形类的自动添加;
   (通过反射机制, 每当新添加图形类时，仅需在shapes文件夹下新建的图形类，并实现Shape接口即可。程序启动时，会自动为shapes文件夹下的新增图形类添加选择按钮）
 
### 2. 程序结构
 - shapes包: shape基本图形接口以及各个实现该接口的图形类；
 - text_8包：包含Mydraw、ClassUtil、Board三个类；
    - Mydraw: 实现绘图板的显示界面，包括各个功能按钮等；
    - ClassUtil: 一个工具类，用于查询shapes目录下所以的图形类；
    - Board: 负责所绘图形的维护，报告图形的添加、刷新重绘、图形文件的打开与保存等；
### 3. 开发环境
 - JDK1.8
 - file encodiing GBK