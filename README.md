# Android-Assets
A sample for Android Assets

### Android工程文件下assets文件夹与res文件夹的区别
<br/>1.assets:不会在R.java文件下生成相应的标记，assets文件夹可以自己创建文件夹，必须使用AssetsManager类进行访问，存放到这里的资源在运行打包的时候都会打入程序安装包中。
<br/>2.res：会在R.java文件下生成标记，这里的资源会在运行打包操作的时候判断哪些被使用到了，没有被使用到的文件资源是不会打包到安装包中的。
res/raw和assets文件夹来存放不需要系统编译成二进制的文件，例如字体文件等。
