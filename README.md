# FileBrowser
 Android 安卓 树形文件浏览器

持续更新中，敬请期待

使用文件操作框架(仅适用于Kotlin项目)

准备工作
1. 下载二进制文件

2. 
dependencies {


    implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
    
    //SAF框架（必须）
    implementation 'androidx.documentfile:documentfile:1.0.1'
    
    //GSON（必须）
    implementation 'com.google.code.gson:gson:2.9.0'
    
    ...
}

3. 初始化框架

AndroidFileManagerApplication.init(context)

4. 获取权限

Manifest文件添加权限：

<uses-permission
        android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />
<uses-permission
        android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />

//ExternalStoragePermissionsUtils.hasXXXXX为检查是否获取了某某权限

//获取外部存储读写权限
ExternalStoragePermissionsUtils.getExternalStoragePermission(this)

//获取所有文件访问权限
ExternalStoragePermissionsUtils.getAllFilesAccessPermission(this)

//获取Android/data权限
ExternalStoragePermissionsUtils.getApplicationExclusiveDirectoryPermission(this)

保存权限(Activity回调)：

//外部存储
override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ExternalStoragePermissionsUtils.saveExternalStoragePermission(this, permissions)
}

// Android/data
@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ExternalStoragePermissionsUtils.saveApplicationExclusiveDirectoryPermission(this, requestCode, data)
}

使用

1. 核心类【com.lumyuan.androidfilemanager.file.File】

主要方法：：

//构造方法

constructor(val path: String)

//文件是否存在

fun exists(): Boolean

//获取文件名

fun getName(): String

//获取上一级绝对路径

fun getParent(): String

//获取绝对路径

fun getPath(): String

//可读？

fun canRead(): Boolean

//可写？

fun canWrite(): Boolean

//是否为文件夹

fun isDirectory(): Boolean

//是否为文件

fun isFile(): Boolean

//最后一次更新时间

fun lastModified(): Long

//文件大小

fun length(): Long

//创建文件

fun createNewFile(): Boolean

//删除文件

fun delete(): Boolean

//获取当前文件夹子项绝对路径集合

fun list(): Array<String?>

//创建文件夹

fun mkDirs(): Boolean

//重命名

fun renameTo(name: String): Boolean

//打开当前文件输入流

fun openInputStream(): InputStream

//打开当前文件输出流

fun openOutputStream(): OutputStream
    
    
使用：

val file = File("xxxx")

val name = file.getName()

println(name)

......


后续将不断更新优化，欢迎star~
