package pl.codeve.inspectorbudget.storage;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path path;
    private StorageProperties storageProperties;

    public StorageService(StorageProperties storageProperties) throws StorageException {

        this.storageProperties = storageProperties;

        this.path = Paths.get("./storage")
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.path);
        } catch (Exception ex) {
            throw new StorageException("Could not create upload directory!", ex);
        }
    }

    public String upload(MultipartFile file) throws StorageException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName;
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to upload empty file " + originalFileName);
            }
            if (originalFileName.contains("..")) {
                throw new StorageException(
                        "Cannot upload file with relative path outside current directory "
                                + originalFileName);
            }
            try (InputStream inputStream = file.getInputStream()) {
                fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());;
                Files.copy(inputStream, this.path.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to upload file " + originalFileName, e);
        }

        return fileName;
    }

    private Path load(String filename) {
        return path.resolve(filename);
    }

    Resource loadAsResource(String fileName) throws FileNotFoundException {
        try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file" + fileName);
        }
    }
}
