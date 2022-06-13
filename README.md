# seetaface
* version 1.0
* auth wangxi
* email wangxxxi@163.com
##项目说明
* 本项目依据seetaface作为底层算法，利用jna封装为java调用
* 本项目暂不支持gpu,如果有gpu加速的需求,向seetaface官方购买替换底层so文件或者lib文件即可
* 项目结构说明
  * seetaface-java-all 支持linux和windows
  * seetaface-java-linux 支持linux
  * seetaface-java-window 支持windows
  * seetaface-java-model 模型文件
## 使用方法
###单线程人脸检测     
```
    //初始化人脸检测器
    FaceDetector detector = new FaceDetector();  
    //图片地址
    String path="/test.jpg";
    //人脸检测
    List<FaceRect> res = detector.detect(path);
    //输出检测结果
    System.out.println(res);
```
###除了上述使用图片地址检测，为方便使用，还提供了以下几种重载
```
    //根据图片类检测
    public List<FaceRect> detect(BufferedImage img);
    //根据输入流检测
    public List<FaceRect> detect(InputStream input);
    //根据图片字节码检测
    public List<FaceRect> detect(byte[] bytes);
```
###多线程人脸检测，与单线程使用方式相同，人脸检测器初始化的不同，也有以上的几种重载方式
```
    //初始化人脸检测器，指定线程数量
    FaceDetectorAsync detector = new FaceDetectorAsync(2);  
    //图片地址
    String path="/test.jpg";
    //人脸检测
    List<FaceRect> res = detector.detect(path);
    //输出检测结果
    System.out.println(res);
```
###单线程提取人脸特征，重载同以上
```
    //初始化人脸检测器
    FaceRecognizer recognizer = new FaceRecognizer();
    //图片路径
    String path="/test.jpg";
    //提取特征
    List<FaceFeature> res = recognizer.feature(path);
    //输出结果
    System.out.println(res); 
```
###多线程提取人脸特征，重载同以上
```
    //初始化人脸检测器，指定线程数量
    FaceRecognizerAsync recognizer = new FaceRecognizerAsync(2);
    //图片路径
    String path="/test.jpg";
    //提取特征
    List<FaceFeature> res = recognizer.feature(path);
    //输出结果
    System.out.println(res);
```
    