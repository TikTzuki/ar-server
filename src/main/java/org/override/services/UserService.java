package org.override.services;

import com.google.gson.JsonSyntaxException;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.override.core.models.HyperEntity;
import org.override.core.models.HyperException;
import org.override.core.models.HyperRoute;
import org.override.core.models.HyperStatus;
import org.override.models.AuthenticationModel;
import org.override.models.ScanStudentRequest;
import org.override.models.UserModel;
import org.override.repositories.UserRepository;
import org.override.utils.RSAUtil;
import org.override.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Log4j2
@RestController
@RequestMapping("/user")
@ResponseBody
public class UserService {
    final UserRepository userRepository;
    @Autowired
    RankingService rankingService;

    @Value("${salt}")
    String salt;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserModel> createUser(@RequestBody AuthenticationModel.UserCreate userCreate) throws NoSuchAlgorithmException {
        String hasedPassword = DigestUtils.sha256Hex(userCreate.getPassword() + salt);
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String pub = RSAUtil.convertSecretKeyToString(keyPair.getPublic());
        String pvt = RSAUtil.convertSecretKeyToString(keyPair.getPrivate());
        UserModel user = new UserModel(userCreate.email, userCreate.name, hasedPassword, pub, pvt);
        try {
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody HyperEntity body) {
        return ResponseEntity.ok(handleLogin(body));
    }

    @RequestMapping(path = "scan-student", method = RequestMethod.POST)
    @ApiOperation(value = "Quét thông tin điểm sinh viên sgu", notes = """
            courses: Khóa
            subjects: Mã ngành
            from: thứ tự sinh vien bắt đầu.
            to: thứ tự sinh vien kết thúc.
            {
              "courses": [
                17, 18
              ],
              "subjects": [
                43, 44, 45, 47, 48
              ],
              "from": 0,
              "to": 10
            }
            """)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    public void scanStudent(@RequestBody ScanStudentRequest scanStudentRequest) {
        rankingService.scanStudents(scanStudentRequest);
    }

    public HyperEntity handleLogin(HyperEntity entity) {
        try {
            AuthenticationModel authenticationModel = AuthenticationModel.fromJson(entity.body);

            String hasedPassword = DigestUtils.sha256Hex(authenticationModel.password + salt);
            Example<UserModel> example = Example.of(new UserModel(authenticationModel.email, hasedPassword));
            Optional<UserModel> userOpt = userRepository.findOne(example);
            if (userOpt.isPresent()) {
                UserModel user = userOpt.get();
                user.setPassword("");
                user.setSecrectKey("");
                return HyperEntity.ok(user);
            } else
                return HyperEntity.notFound(
                        new HyperException(HyperException.NOT_FOUND, "body -> email", "email or password invalid")
                );
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return HyperEntity.unprocessableEntity(
                new HyperException(HyperException.BAD_REQUEST, "", "internal error")
        );
    }

    public HyperEntity auth(HyperEntity hyperEntity) {
        if (hyperEntity.route.equals(HyperRoute.LOGIN))
            return hyperEntity;
        String authorization = hyperEntity.headers.get("Authorization");

        if (authorization == null || !authorization.matches(".+:.+")) {
            return HyperEntity.unauthorized(
                    new HyperException(HyperException.UNAUTHORIZED)
            );
        }

        String[] authorizations = authorization.split(":");
        String userIdString = authorizations[0];
        String ivString = authorizations[1];

        if (!NumberUtils.isParsable(userIdString))
            return HyperEntity.unauthorized(new HyperException(HyperException.UNAUTHORIZED));

        Integer userId = Integer.parseInt(userIdString);
        Optional<UserModel> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            try {
                if (hyperEntity.body == null)
                    return hyperEntity;

                UserModel user = userOpt.get();
                String key = SecurityUtil.generateKey(user.getPublicKey(), user.getEmail());
                hyperEntity.body = SecurityUtil.decrypt(
                        hyperEntity.body,
                        key,
                        ivString
                );

                log.info(hyperEntity.body);

                return hyperEntity;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ignore) {
            }
        }
        return HyperEntity.unauthorized(
                new HyperException(HyperException.UNAUTHORIZED)
        );

    }

    public HyperEntity encryptResponse(HyperEntity request, HyperEntity response) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        if (request.route.equals(HyperRoute.LOGIN) || !response.status.equals(HyperStatus.OK))
            return response;
        String[] authorizations = request.headers.get("Authorization").split(":");
        String userIdString = authorizations[0];
        String ivString = authorizations[1];
        Integer userId = Integer.parseInt(userIdString);
        UserModel user = userRepository.findById(userId).get();
        String key = SecurityUtil.generateKey(user.getPublicKey(), user.getEmail());
        response.body = SecurityUtil.encrypt(
                response.body,
                key,
                ivString
        );
        return response;
    }
}
