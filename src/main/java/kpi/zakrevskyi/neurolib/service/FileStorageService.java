package kpi.zakrevskyi.neurolib.service;

import kpi.zakrevskyi.neurolib.domain.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file, FileType fileType, String ownerId);

    void deleteAllByOwner(FileType fileType, String ownerId);
}
