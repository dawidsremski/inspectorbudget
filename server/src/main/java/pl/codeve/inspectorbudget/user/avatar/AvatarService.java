package pl.codeve.inspectorbudget.user.avatar;

import lombok.AllArgsConstructor;
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
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AvatarService {

    private AvatarRepository avatarRepository;
    private UserRepository userRepository;

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

    void setUser(Long avatarId, User user) {
        Optional<Avatar> avatarOptional = avatarRepository.findById(avatarId);
        user.setAvatar(avatarOptional.orElse(null));
        userRepository.save(user);
    }

    public List<Avatar> findAllByInUse(boolean inUse) {
        return avatarRepository.findAllByInUse(inUse);
    }

    public void delete(Avatar avatar) {
        avatarRepository.delete(avatar);
    }
}
