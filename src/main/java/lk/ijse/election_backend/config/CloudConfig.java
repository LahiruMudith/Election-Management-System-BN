package lk.ijse.election_backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dkidles6w",
                "api_key", "268278995782392",
                "api_secret", "q-nPFYWS5s1cDeWRWUyYggKJ5iE"
        ));
    }
}
