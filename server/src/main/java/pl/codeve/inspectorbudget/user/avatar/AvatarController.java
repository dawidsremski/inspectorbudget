package pl.codeve.inspectorbudget.user.avatar;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/api/user/avatar")
@AllArgsConstructor
public class AvatarController {

    private AvatarService avatarService;

    @PostMapping
    ResponseEntity<?> uploadAvatar(MultipartFile file) throws IOException {
        Long avatarId = avatarService.uploadAvatar(file);
        String avatarUrl = "/user/avatar?id=" + avatarId;
        return new ResponseEntity<>(new AvatarResponse(avatarId, avatarUrl), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<byte[]> getAvatar(@RequestParam Long id) {
        try {
            byte[] bytes = Base64.getDecoder()
                    .decode(avatarService.getAvatar(id).getBase64Avatar());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (AvatarNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
