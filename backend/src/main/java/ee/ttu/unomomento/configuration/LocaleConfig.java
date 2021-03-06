package ee.ttu.unomomento.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class LocaleConfig {
    private Locale locale = new Locale("ee", "EE");

    @Bean
    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("MessagesBundle", locale);
    }
}
