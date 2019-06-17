package pl.codeve.inspectorbudget.user.avatar;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.codeve.inspectorbudget.user.User;
import pl.codeve.inspectorbudget.user.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@Service
public class AvatarService {

    private AvatarRepository avatarRepository;
    private UserRepository userRepository;

    AvatarService(AvatarRepository avatarRepository,
                  UserRepository userRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    Long uploadAvatar(MultipartFile multipartFile) throws IOException {

        ByteArrayOutputStream resizedAvatarImageOutputStream = new ByteArrayOutputStream();
        BufferedImage avatarImage = ImageIO.read(multipartFile.getInputStream());

        Thumbnails.of(avatarImage).crop(Positions.CENTER)
                .size(150, 150).outputFormat("jpeg")
                .toOutputStream(resizedAvatarImageOutputStream);

        String base64Avatar = Base64.getEncoder()
                .encodeToString(resizedAvatarImageOutputStream.toByteArray());

        Avatar avatar = new Avatar();
        avatar.setBase64Avatar(base64Avatar);
        avatarRepository.save(avatar);
        return avatar.getId();
    }

    Avatar getAvatar(Long avatarId) throws AvatarNotFoundException {
        return avatarRepository.findById(avatarId).orElseThrow(AvatarNotFoundException::new);
    }

    void setUser(Long avatarId, User user) throws AvatarNotFoundException {
        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(AvatarNotFoundException::new);
        user.setAvatar(avatar);
        userRepository.save(user);
    }
}
