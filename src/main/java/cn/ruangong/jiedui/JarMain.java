package cn.ruangong.jiedui;

import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@Service
@RequestMapping("/size")
@Api(tags = "四则运算接口 API")
public class JarMain {
    @PostMapping("/yunsuan")
    @ApiOperation(value = "四则运算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fanwei",value = "输入范围"),
            @ApiImplicitParam(name = "number",value = "题目数量")
    })
    public String  sizeyunsuan(
            HttpServletResponse response,
            @RequestParam("fanwei")String fanwei,
            @RequestParam("number") String number
    ) throws UnsupportedEncodingException {
        if(fanwei == null || number == null || fanwei.equals("") || number.equals("")){
            return "两个参数都不能为空";
        }
        System.out.println("生成题目数量："+number+"    ||生成题目中数值（自然数、真分数和真分数分母）的范围"+fanwei);
        List<String> Exercises = new ArrayList<>();
        int number01 = Integer.parseInt(number);
        int fanwei01  = Integer.parseInt(fanwei);
        //只用一个运算符和两个运算数组合
        List<Integer> num1lsit = new ArrayList<>();
        List<Integer> num2lsit = new ArrayList<>();
        List<String> resultlist = new ArrayList<>();
        Random random = new Random();
        //生成运算数的时候可以顺序生成,等要显示的时候再根据序号随机抽取相同序号的组.
        for(int i = 1;i < fanwei01;i++){//直接不要0计算,甚至不要重复
            for(int j = 1;j < i;j++){
                num1lsit.add(new Integer(i));
                num2lsit.add(new Integer(j));
            }
        }
        //==========以上整数形式运算数生成完毕========
        for(int i = 0;i < num1lsit.size();i++){
            Exercises.add(num1lsit.get(i)+" + "+num2lsit.get(i));
            resultlist.add(String.valueOf(num1lsit.get(i)+num2lsit.get(i)));
            Exercises.add(num1lsit.get(i)+" - "+num2lsit.get(i));
//            if(num1lsit.get(i) - num2lsit.get(i) < 0){//没有这种情况
//                return;
//            }
            resultlist.add(String.valueOf(num1lsit.get(i)-num2lsit.get(i)));
            Exercises.add(num1lsit.get(i)+" x "+num2lsit.get(i));
            resultlist.add(String.valueOf(num1lsit.get(i)*num2lsit.get(i)));
            Exercises.add(num1lsit.get(i)+" / "+num2lsit.get(i));
            resultlist.add(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)));//储存约分后结果
//            Exercises.add(num2lsit.get(i)+" / "+num1lsit.get(i));//题目不能出现假分数
//            resultlist.add(yuefen.reduce(num2lsit.get(i),num1lsit.get(i)));

            //由于两个运算数还凑不够1万个（10范围内），所以加三个运算数的，有点懒，直接拿第二个运算数加上第一个运算数当第三个运算数
            Integer num3 = num1lsit.get(i)+num2lsit.get(i);//防止被当成字符串拼接
            Exercises.add(num1lsit.get(i)+" + "+num2lsit.get(i)+" + "+num3);
            resultlist.add(String.valueOf(num1lsit.get(i)+num2lsit.get(i)+num3));
            num3 = num1lsit.get(i)-num2lsit.get(i);//防止被当成字符串拼接
            Exercises.add(num1lsit.get(i)+" + "+num2lsit.get(i)+" + "+num3);
            resultlist.add(String.valueOf(num1lsit.get(i)+num2lsit.get(i)+num3));
            num3 = num1lsit.get(i)*num2lsit.get(i);//防止被当成字符串拼接
            Exercises.add(num1lsit.get(i)+" + "+num2lsit.get(i)+" + "+num3);
            resultlist.add(String.valueOf(num1lsit.get(i)+num2lsit.get(i)+num3));



        }
        //=============以上整数题目及答案生成完毕==========
        //分子分母也可以利用上述运算数数组
        for(int i = 0;i < num1lsit.size();i++){//num1list和num2list是长度相等的
            for(int j = 0;j < num1lsit.size();j++){
                Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j))));
                resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num1lsit.get(j),num2lsit.get(i)*num2lsit.get(j))));//分子与分子、分母与分母相乘后约分

                if((num1lsit.get(i)/num2lsit.get(i))/(num1lsit.get(j)/num2lsit.get(j)) < 1){//e1/e2结果是真分数才能作为题目，否则抛弃(项目要求)
                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" / "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num2lsit.get(j),num2lsit.get(i)*num1lsit.get(j))));
                }
                //除法可以调换运算数
                if((num1lsit.get(j)/num2lsit.get(j))/(num1lsit.get(i)/num2lsit.get(i)) < 1){//e1/e2结果是真分数才能作为题目，否则抛弃(项目要求)
//                    System.out.println("看看有没有错误"+i+"||"+yuefen.reduce(num1lsit.get(j),num2lsit.get(j))+" / "+yuefen.reduce(num1lsit.get(i),num2lsit.get(i))+" = "+daifenshu.jiahuadai((yuefen.reduce(num2lsit.get(i)*num1lsit.get(j),num1lsit.get(i)*num2lsit.get(j)))));
                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" / "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num2lsit.get(i)*num1lsit.get(j),num1lsit.get(i)*num2lsit.get(j))));
                }

                Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" + "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j))));
                resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num2lsit.get(j)+num2lsit.get(i)*num1lsit.get(j),num2lsit.get(i)*num2lsit.get(j))));

                if(num1lsit.get(i)*num2lsit.get(j)-num2lsit.get(i)*num1lsit.get(j) > 0){
                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" - "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num2lsit.get(j)-num2lsit.get(i)*num1lsit.get(j),num2lsit.get(i)*num2lsit.get(j))));
                }
                //减法可以调换
                if(num2lsit.get(i)*num1lsit.get(j)-num1lsit.get(i)*num2lsit.get(j) > 0){
                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" - "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num2lsit.get(i)*num1lsit.get(j)-num1lsit.get(i)*num2lsit.get(j),num2lsit.get(i)*num2lsit.get(j))));
                }

                //==============三个分数相运算=====================
                //三分数乘运算可以变出四种，每种1千条
                if(num1lsit.get(i) == num1lsit.get(j) && num2lsit.get(i) == num2lsit.get(j)){//相同运算符的可能造成以下四条重复

                }
                else{
                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num1lsit.get(j)*num1lsit.get(j),num2lsit.get(i)*num2lsit.get(j)*num2lsit.get(j))));

                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(j))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num1lsit.get(j)*num1lsit.get(i),num2lsit.get(i)*num2lsit.get(j)*num2lsit.get(j))));

                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num1lsit.get(j)*num1lsit.get(i),num2lsit.get(i)*num2lsit.get(j)*num2lsit.get(i))));

                    Exercises.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i),num2lsit.get(i)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(j)))+" x "+daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(j),num2lsit.get(i))));
                    resultlist.add(daifenshu.jiahuadai(yuefen.reduce(num1lsit.get(i)*num1lsit.get(j)*num1lsit.get(j),num2lsit.get(i)*num2lsit.get(j)*num2lsit.get(i))));

                }

            }
        }

//        for(int i = 0;i < Exercises.size();i++){
//            System.out.println(i+"||"+Exercises.get(i)+" = "+resultlist.get(i));//下面随机调用显示
//        }

        //生成一个不重复的随机数数组，范围是给定的0~能生成题目的数量
        List<Integer> randomlist =  new ArrayList<>();
        while(randomlist.size() < Exercises.size()){
            Integer s = random.nextInt(Exercises.size());//生成0~能生成题目的数量之间的随机整数
            if(!randomlist.contains(s)){
                randomlist.add(s);
            }
        }
//        for(Integer i : randomlist){
//            System.out.println(i);
//        }
        //==========（未随机）结果打印=====================
//        for(Integer i : randomlist){
//           System.out.println(i+"||"+Exercises.get(i)+" = "+resultlist.get(i));
//        }
        //========================================

//=================打印下原始运算数数组==================
//        for(int i = 0;i < num1lsit.size();i++){
//            System.out.println(num1lsit.get(i) +"=====>"+ num2lsit.get(i));
//        }
//======================================================

        //输出到文件
        try {
            BufferedWriter outanswer = new BufferedWriter(new FileWriter("D:\\ruangongjiedui\\target\\Answers.txt"));//这里修改为绝对路径
            BufferedWriter outquestion = new BufferedWriter(new FileWriter("D:\\ruangongjiedui\\target\\Exercises.txt"));
            if(number01 > Exercises.size()){//如果此程序对于给定的范围生成不了指定的number数量，就只打印能生成的所有题目/答案
                number01 = Exercises.size();
            }
            for(int i = 0;i < number01;i++){
                outanswer.write((i+1)+"."+resultlist.get(randomlist.get(i))+"\n");//write是覆盖写入
                outquestion.write((i+1)+"."+Exercises.get(randomlist.get(i))+" = \n");
            }
            outanswer.close();
            outquestion.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            System.out.println("IO读写失败");
        }

        //（x-1） * 2 +1

//        //=========提供下载答案文件功能，将文件变成文件流塞进response去=================
//        String filename = "D:\\ruangongjiedui\\target\\Answers.txt";//注意服务器的地址可能跟这个不同，但是还是不建议采用相对地址
//        File file = new File(filename);
//        if(!file.exists()){
//            return "要下载的答案文件不存在";
//        }
//        response.reset();
//        response.setContentType("application/octet-stream");
//        response.setCharacterEncoding("utf-8");
////        response.setContentLength((int) file.length());
////        response.setHeader("Content-Disposition", "attachment;filename=" + filename );
//
//
//        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
//            byte[] buff = new byte[1024];
//            OutputStream os  = response.getOutputStream();
//            int i = 0;
//            while ((i = bis.read(buff)) != -1) {
//                os.write(buff, 0, i);
//                os.flush();
//            }
//        } catch (IOException e) {
//            return "下载答案文件失败";//下载文件是在response里塞进去文件流的，这里由于@RestController了，所以会显示文字
//        }
//
//        //=========提供下载作业题目文件功能，将文件变成文件流塞进response去=================
//        String filename02 = "D:\\ruangongjiedui\\target\\Exercises.txt";//注意服务器的地址可能跟这个不同，但是还是不建议采用相对地址
//        File file02 = new File(filename02);
//        if(!file02.exists()){
//            return "要下载的答案文件不存在";
//        }
////        response.reset();
////        response.setContentType("application/octet-stream");
////        response.setCharacterEncoding("utf-8");
//        response.setContentLength((int) (file02.length() + file.length()));
//        response.setHeader("Content-Disposition", "attachment;filename=" + filename );
//
//
//        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file02));) {
//            byte[] buff = new byte[1024];
//            OutputStream os  = response.getOutputStream();
//            int i = 0;
//            while ((i = bis.read(buff)) != -1) {
//                os.write(buff, 0, i);
//                os.flush();
//            }
//        } catch (IOException e) {
//            return "下载作业题目文件失败";//下载文件是在response里塞进去文件流的，这里由于@RestController了，所以会显示文字
//        }
//
//        return "下载作业题目文件和对应答案文件成功";

        //================提供多文件（题目和答案文件.txt）打包下载功能================
        List<String> names = new ArrayList<>();//要打包下载的文件的名字集合
        List<String> paths = new ArrayList<>();//要打包下载的文件的路径集合，要与上面一一对应
        String directoryPath = "D:/ruangongjiedui/target";//临时存放在本服务器上此zip文件的目录
        names.add("Exercises.txt");
        names.add("Answers.txt");
        paths.add("D:/ruangongjiedui/target/");
        paths.add("D:/ruangongjiedui/target/");
        File directoryFile=new File(directoryPath);
        if(!directoryFile.isDirectory() && !directoryFile.exists()){
            directoryFile.mkdirs();
        }
        //设置最终输出zip文件的目录+文件名
//        SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMddHHmmss");//精确到秒了应该不会重名吧？--不重名太多文件了。
//        String zipFileName = formatter.format(new Date())+"生成的软工四则运算"+number+"道范围为"+fanwei+"的作业题目和答案文件.zip";
        String zipFileName = "生成的软工四则运算"+number+"道范围为"+fanwei+"的作业题目和答案文件.zip";
        //======zip的路径+名字=========
        String strZipPath = directoryPath+"/"+zipFileName;//由于这里是/，所以我上面的路径都改为了/而不是原来的\\。

        ZipOutputStream zipStream = null;
        FileInputStream zipSource = null;
        BufferedInputStream bufferStream = null;
        //====zip文件本身===========
        File zipFile = new File(strZipPath);
        try{
            //构造最终压缩包的输出流
            zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i<paths.size() ;i++){
                //解码获取真实路径与文件名
                String realFileName = java.net.URLDecoder.decode(names.get(i),"UTF-8");
                String realFilePath = java.net.URLDecoder.decode(paths.get(i),"UTF-8");
                File file = new File(realFilePath+realFileName);
                if(file.exists()) {
                    zipSource = new FileInputStream(file);//将需要压缩的文件格式化为输入流
                    /**
                     * 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样这里的name就是文件名,
                     * 文件名和之前的重复就会导致文件被覆盖
                     */
                    ZipEntry zipEntry = new ZipEntry(realFileName);//在压缩目录中文件的名字
                    zipStream.putNextEntry(zipEntry);//定位该压缩条目位置，开始写入文件到压缩包中
                    bufferStream = new BufferedInputStream(zipSource, 1024 * 10);
                    int read = 0;
                    byte[] buf = new byte[1024 * 10];
                    while((read = bufferStream.read(buf, 0, 1024 * 10)) != -1) {
                        zipStream.write(buf, 0, read);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                if(null != bufferStream) {
                    bufferStream.close();
                }
                if(null != zipStream){
                    zipStream.flush();
                    zipStream.close();
                }
                if(null != zipSource){
                    zipSource.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //=========开始发送=================
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) (zipFile.length()));
        response.setHeader("Context-Type"," application/xmsdownload");
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);//这里中文变成_了我也暂时不知道如何解决。
//        System.out.println(zipFileName);
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(strZipPath));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载作业题目文件和对应答案压缩文件失败";//下载文件是在response里塞进去文件流的，这里由于@RestController了，所以会显示文字
        }
        return "下载作业题目文件和对应答案压缩文件成功";

    }
}


@Controller
@RequestMapping("/file")
class  bidui{
    @PostMapping("/bidui")
    @ApiOperation(value = "上传两个文件，进行文件比对，传回准确率")
    public String bidui(
            HttpServletResponse response,
            @ApiParam("zuoyefile")MultipartFile zuoyefile,
            @ApiParam("dananfile")MultipartFile dananfile
    ) throws IOException {
        if(zuoyefile == null || dananfile ==null){
            return "文件不能为空";
        }
        String dananfileContent = new String(dananfile.getBytes(), StandardCharsets.UTF_8);//将MultiPartile文本内容读取到字符串
        String[] dananlist = dananfileContent.split(".|\n");//根据\n和.分割字符串
        String zuoyefileContent = new String(zuoyefile.getBytes(), StandardCharsets.UTF_8);//将MultiPartile文本内容读取到字符串
        String[] zuoyelist = zuoyefileContent.split("\n|.");

//        System.out.println(dananlist);
//        System.out.println(dananfileContent);
        System.out.println(dananlist.length);
        for(int i = 0;i < dananlist.length;i++){//for-each不能用String[]，只能用在诸如List<String>
            System.out.println(dananlist[i]);
        }


        return "准确率为：";
    }
}

/**
 * 分数约分
 */
class yuefen{
    public static String reduce(Integer fenzi,Integer fenmu){
        int fz = Math.abs(fenzi); //取分子分母的绝对值
        int fm = Math.abs(fenmu);
        int mod = fz%fm;
        if(mod == 0){
            return  ""+(fz /fm);
        }
        while (mod > 0) { //求分子分母的最大公因数
            fz = fm;
            fm = mod;
            mod = fz%fm;
        }
        return (fenzi/fm)+"/"+(fenmu/fm); //分子分母都除以最大公因数
    }
}

/**
 * 假分数化成带分数，如20/3化成6'2/3 (英文单引号'),自带判断分数是否是假分数，真分数原样输出
 */
class daifenshu{
    public static String jiahuadai(String jiafenshu){
        if(!jiafenshu.contains("/")){//整除的结果就可能没有/
            return jiafenshu;
        }
        String[] str = jiafenshu.split("/");//根据/分割字符串，分割成只有两个元素的String数组
//        System.out.println(jiafenshu);
//        System.out.println(str[0]+"||"+str[1]);
        int num1 = Integer.parseInt(str[0]);
        int num2 = Integer.parseInt(str[1]);
        if(num1 < num2){//2/3输出2/3
            return jiafenshu;
        }
        else if(num1 == num2){//如1/1输出1
            return ""+num1;
        }
        else{
            int dainum = num1 / num2;//整数截断
            num1 = num1 % num2;
            return dainum+"'"+num1+"/"+num2;
        }
    }
}
