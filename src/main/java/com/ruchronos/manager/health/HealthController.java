package com.ruchronos.manager.health;
import com.ruchronos.manager.config.AppProperties;import org.springframework.jdbc.datasource.DataSourceUtils;import org.springframework.web.bind.annotation.*;import javax.sql.DataSource;
@RestController public class HealthController {private final AppProperties p;private final DataSource ds;private final long started=System.nanoTime();public HealthController(AppProperties p,DataSource ds){this.p=p;this.ds=ds;}
 @GetMapping("/health") public Health health(){boolean db;var c=DataSourceUtils.getConnection(ds);try{db=c.isValid(1);}catch(Exception e){db=false;}finally{DataSourceUtils.releaseConnection(c,ds);}boolean k8s=p.health().k8sConnected();String status=!db?"DOWN":(k8s?"UP":"DEGRADED");return new Health(status,p.version(),(System.nanoTime()-started)/1_000_000_000L,db,k8s);}
 public record Health(String status,String version,long uptimeSeconds,boolean databaseConnected,boolean k8sConnected){}
}
