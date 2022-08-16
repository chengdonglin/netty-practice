package com.unic.core.file;

import cn.hutool.core.io.FileUtil;
import com.unic.core.operation.Operation;
import com.unic.core.operation.OperationResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-15 18:37:48
 */
@Data
@AllArgsConstructor
public class ImgUploadOperation extends Operation {

    private String fileName;

    private byte[] bytes;

    @Override
    public OperationResult execute() {
        File file = FileUtil.writeBytes(bytes, new File("D:\\" + fileName));
        ImgUploadOperationResult result = new ImgUploadOperationResult();
        result.setSuccess(true);
        result.setPath(file.getAbsolutePath());
        return  result;
    }
}
