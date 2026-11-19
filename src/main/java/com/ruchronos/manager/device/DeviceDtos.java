package com.ruchronos.manager.device;
import jakarta.validation.constraints.*;import java.time.Instant;
public final class DeviceDtos {private DeviceDtos(){}
 public record DeviceRegistration(@NotNull Provider provider,@NotBlank String pushToken,@NotNull Platform platform,String deviceName){}
 // id/createdAt preserve the uploaded OpenAPI shape; deviceId/registeredAt are aliases from the task brief.
 public record Device(String id,String deviceId,Provider provider,String pushToken,Platform platform,String deviceName,Instant createdAt,Instant registeredAt,Instant lastSeenAt){}
 public enum Provider{fcm,apns,rustore} public enum Platform{android,ios,web}
}
