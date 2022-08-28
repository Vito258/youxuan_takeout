package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Filter;

/**
 * 通用的管理层，主要用于文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    // 在注解文件中获取一个动态的路径，并指定为临时文件目录用来输出文件
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 管理文件上传的方法
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file )  {
   // 这里的file 是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
   // log.info(file.toString());

        //获得原始的文件名,不推荐使用原始文件名，因为如果重名就会覆盖
        String fileName = file.getOriginalFilename();

        //切割文件名从而获取后缀
        String[] f = fileName.split("\\.");

        //如果目录路径不存在，那么就创造一个
        File dir = new File(basePath); //获取路径
        if( !dir.exists() ){
            dir.mkdir();
        }

        //使用UUId 生成文件名，防止文件名重复
        //1.获取一段随机的字符串
        String s = UUID.randomUUID().toString();
        //2.拼接字符串，加上截取到的后缀 组成文件的全名
        String name = s + "." + f[1];
        //将文件转存到指定位置
        try {
            file.transferTo(new File(basePath+name));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(name);
    }

    /**
     * 提供给文件下载的方法
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response) {
        /**
         * 此时的电脑相当于一个服务器，上边文件上传的的输出路径相当于传递给了服务器
         */

        try{
            //通过输入流获取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath+name);

            //通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg/png");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) !=-1){
               outputStream.write(bytes,0,len);
               outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        }catch (IOException e){
            e.printStackTrace();
        }




    }

}
