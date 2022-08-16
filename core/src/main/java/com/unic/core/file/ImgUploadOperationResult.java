package com.unic.core.file;

import com.unic.core.operation.OperationResult;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-15 18:45:24
 */
@Data
public class ImgUploadOperationResult extends OperationResult {
    private Boolean success;
    private String path;
}
