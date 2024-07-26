# instrumentation demo project
一个javaagent和attach的demo程序，主要解决两个问题，来简化上手难度，可以在这个项目的基础上快速修改。
- 1 java8之前`attach`需要用到`tools.jar`，这里判断了java环境，并加载了`tools.jar`。
- 2 配置了`shade`打包。