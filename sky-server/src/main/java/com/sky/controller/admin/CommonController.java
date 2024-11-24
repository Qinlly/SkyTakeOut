package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired  //spring扫描不到模块之外的bean,如Aliossperties,所以需要配置类通过Aliossperties生成aliOssUtil对象再注入
    private AliOssUtil aliOssUtil;
    /**
     * 上传文件
     *
     * @param file 文件
     * @return Result
     */
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file) {
        log.info("上传文件: {}",file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();

            //截取拓展名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            //构造新文件名
            String objectName = UUID.randomUUID().toString() + suffix;

            //请求路径
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("上传文件失败: {}",e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
