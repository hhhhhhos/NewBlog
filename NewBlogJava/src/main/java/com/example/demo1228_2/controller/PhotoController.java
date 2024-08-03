package com.example.demo1228_2.controller;


import java.io.File;

import com.example.demo1228_2.Vo.MyFile;
import com.example.demo1228_2.component.GlobalProperties;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;

import static com.example.demo1228_2.config.Tool.PHOTO_SAVE_URL;
import static com.example.demo1228_2.config.Tool.tika;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-04-22
 */
@Slf4j
@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    GlobalProperties globalProperties;

    @GetMapping("/all")
    public R<List<MyFile>> getAll(@RequestParam(defaultValue = "") String path) {
        List<MyFile> fileNames = new ArrayList<>();
        File directory = new File(globalProperties.getPHOTO_SAVE_URL()+path);
        //log.info("当前路径:"+globalProperties.getPHOTO_SAVE_URL()+path);

        // 检查目录是否存在且是一个目录
        if (directory.exists() && directory.isDirectory()) {
            // 获取目录中的所有文件和目录
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    MyFile myFile = new MyFile();
                    myFile.setName(file.getName());
                    // 获取文件大小（字节）
                    long fileSizeBytes = file.length();
                    // 转换为MB（四舍五入到两位小数）
                    double fileSizeMB = (double) fileSizeBytes / (1024 * 1024);
                    // 格式化并打印结果，保留两位小数
                    String totalSpaceString = String.format("%.2f MB", fileSizeMB);
                    myFile.setSize(totalSpaceString);

                    if(file.getName().contains(".")){
                        fileNames.add(myFile);
                    }else{
                        fileNames.add(0, myFile);
                    }

                }
            } else {
                log.info("文件夹为空");
            }
        } else {
            log.info("指定路径不存在或不是一个目录");
            return R.error("目录不存在");
        }

        return R.success(fileNames);
    }

    /**
     * 上传一张图片(或文件) 保存在url
     * @param photo 图
     * @param url 保存地址
     * @return 布尔是否保存成功
     */
    @PostMapping("/addonebyadmin")
    public Boolean FindOneProduct(MultipartFile photo,String url){
        if (photo == null || url == null) return false;

        try {
            // 使用 Apache Tika 来检测文件类型
            String fileType = Tool.tika.detect(photo.getInputStream());
            log.info(fileType);
            log.info(photo.getOriginalFilename());

            if (fileType.startsWith("image/")) {
                String name = Tool.convertToWebp(photo, "");
                return Tool.moveFile(name, url + '/' + name);
            } else {
                // 处理非图片文件
                return Tool.saveFile2(photo,url);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 获取文件
     * @param url 文件路径
     * @return 文件
     */
    @PostMapping("/getfile")
    public ResponseEntity<Resource> getFile(String url){
        return Tool.getFile(url);
    }

    /**
* 创建文件夹在路径
* @param path 1
* @return 是否成功
*/
    @PostMapping("/folderbyadmin")
    public Boolean createfolder(@RequestParam String path){
        File saveDir = new File(PHOTO_SAVE_URL + path);
        if (!saveDir.exists()) {
            return saveDir.mkdirs();
        }else return false;
    }

    /**
     * 删除路径下所有图片
     * @param path 1
     * @return bool
     */
    @DeleteMapping("/folderbyadmin")
    public Boolean deleteall(@RequestParam String path){
        return Tool.deleteAllFilesInDirectory(path);
    }

    /**
     * 从列表删除路径下n张图片 a.webp 或 bb/a.webp
     * @param delete_list a.webp 或 bb/a.webp
     * @return bool
     */
    @DeleteMapping("/listbyadmin")
    public String deleteone(@RequestBody List<String> delete_list){
        int success = 0;
        int fail = 0;
        for(String pathAndName:delete_list){
            log.info("当前路径："+globalProperties.getPHOTO_SAVE_URL() + pathAndName);
            Path path = Paths.get(globalProperties.getPHOTO_SAVE_URL() + pathAndName);
            // 删单个文件
            if(!Files.isDirectory(path)){
                log.info("是文件");
                if(Tool.deleteOneWebp(pathAndName))
                    success++;
                else
                    fail++;
            // 删文件夹下所有
            }else{
                log.info("是文件夹子");
                if(Tool.deleteAllFilesInDirectory(globalProperties.getPHOTO_SAVE_URL() + pathAndName)){
                    success++;
                }
                else
                    fail++;
            }

        }
        return "成功删除"+success+"，失败"+fail;
    }

}