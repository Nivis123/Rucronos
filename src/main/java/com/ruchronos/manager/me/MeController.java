package com.ruchronos.manager.me;
import com.ruchronos.manager.auth.*;import com.ruchronos.manager.device.*;import jakarta.validation.Valid;import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.net.URI;import java.util.List;
@RestController public class MeController {private final DeviceService devices;public MeController(DeviceService d){devices=d;}
 @GetMapping("/me") public UserDto me(Authentication a){var u=(TokenService.AuthenticatedUser)a.getPrincipal();return AuthService.user(u.username(),u.roles().get(0));}
 @GetMapping("/me/devices") public List<DeviceDtos.Device> list(Authentication a){return devices.list(uid(a));}
 @PostMapping("/me/devices") public ResponseEntity<DeviceDtos.Device> register(Authentication a,@Valid @RequestBody DeviceDtos.DeviceRegistration r){var x=devices.register(uid(a),r);return ResponseEntity.status(x.created()?201:200).location(URI.create("/api/v1/me/devices/"+x.device().id())).body(x.device());}
 @DeleteMapping("/me/devices/{deviceId}") public ResponseEntity<Void> delete(Authentication a,@PathVariable String deviceId){devices.delete(uid(a),deviceId);return ResponseEntity.noContent().build();}
 private static String uid(Authentication a){return ((TokenService.AuthenticatedUser)a.getPrincipal()).id();}
}
