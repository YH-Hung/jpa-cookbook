package hle.jpacookbook.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinIOProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Integer connectTimeoutSec;
    private Integer writeTimeoutSec;
    private Integer readTimeoutSec;
}
