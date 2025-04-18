package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.coooolfan.superprinter.service.FileInfoService;
import com.coooolfan.superprinter.vo.FileInfoUploadPreSignVO;
import com.coooolfan.superprinter.vo.response.FileInfoUploadPreSignResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RequestMapping("/api/file")
@RestController
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @SaCheckLogin
    @PostMapping
    public ResponseEntity<FileInfoUploadPreSignResponse> fileInfoUploadPreSign(
            @RequestBody FileInfoUploadPreSignVO vo) {
        return ResponseEntity.ok(fileInfoService.getFileInfoUploadPreSign(vo));
    }
}
