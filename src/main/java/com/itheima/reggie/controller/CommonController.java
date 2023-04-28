package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 处理文件的上传和下载，是基于本地磁盘
 *
 * @Author Lyc
 * @Date 2023/4/24 11:38
 * @Description: 处理文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    //读取yml中所配置的文件路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 图片的上传--->本地磁盘
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("文件原始名：{}", file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename();
        //此时只是存入在了一个本地磁盘的临时文件夹下 一旦服务关闭那么图片就已经消失了，所以要使用输出流来将这张图片来真正的写入到设置的磁盘目录之下
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info("使用UUID的文件名：{}", fileName);
        //创建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            //    如果不存在，那么就创建
            dir.mkdirs();
        }
        try {
            //将其写入到真正的磁盘目录之下 此时已经是持久化了 而不是临时文件了
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将文件名返回给客户端，因为客户端要根据这个文件名来构成对应的url来是实现在页面上对这个图片的马上回显（即一个从磁盘的下载读取过程）
        return R.success(fileName);
    }

    /**
     * 图片的下载，要实现的效果就是用户在上传之后，能够在页面马上显示出所上传的图片（这个请求是由前端所自动发送的），这其实就是一个读取（下载）上传到
     * 本地磁盘图片的过程
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        BufferedInputStream  inputStream = null;
        ServletOutputStream outputStream = null;

        try {
            //获得一个输出流来  读取磁盘中所上传的图片  这里的name就是上面上传时候所上传图片的名字
            inputStream = new BufferedInputStream(new FileInputStream(basePath + name));
            /*
            这个所获得到的图片是要回显给页面来展示的，所以需要使用response来得到一个输出流，而不是我们自己new 一个之前的普通的输出流
            并设置这个响应的格式声明为图片
             */
            response.setContentType("image/jpeg");
            outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int readLen;
            //循环读取只要不等于-1，那么一直读取
            while ((readLen = inputStream.read(bytes)) != -1){
                  outputStream.write(bytes,0,readLen);
                  outputStream.flush();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                //关闭外层流即可
                if (inputStream != null){
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
