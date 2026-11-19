package com.ruchronos.manager.device;
import com.ruchronos.manager.problem.ApiException;import org.springframework.stereotype.Service;import java.time.Instant;import java.util.*;import java.util.concurrent.ConcurrentHashMap;
@Service public class DeviceService {
 private final Map<String,Map<String,DeviceDtos.Device>> data=new ConcurrentHashMap<>();
 public List<DeviceDtos.Device> list(String uid){return List.copyOf(data.computeIfAbsent(uid,k->new ConcurrentHashMap<>()).values());}
 public RegistrationResult register(String uid,DeviceDtos.DeviceRegistration in){var map=data.computeIfAbsent(uid,k->new ConcurrentHashMap<>());synchronized(map){var old=map.values().stream().filter(d->d.pushToken().equals(in.pushToken())).findFirst();Instant now=Instant.now();if(old.isPresent()){var o=old.get();var d=new DeviceDtos.Device(o.id(),o.id(),in.provider(),in.pushToken(),in.platform(),in.deviceName(),o.createdAt(),o.createdAt(),now);map.put(o.id(),d);return new RegistrationResult(d,false);}String id=UUID.randomUUID().toString();var d=new DeviceDtos.Device(id,id,in.provider(),in.pushToken(),in.platform(),in.deviceName(),now,now,now);map.put(d.id(),d);return new RegistrationResult(d,true);}}
 public void delete(String uid,String id){var map=data.get(uid);if(map==null||map.remove(id)==null)throw ApiException.notFound("Устройство не найдено");}
 public record RegistrationResult(DeviceDtos.Device device,boolean created){}
}
