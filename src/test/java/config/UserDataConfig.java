package config;

import org.aeonbits.owner.Config;


@Config.Sources("classpath:userdata.properties")
public interface UserDataConfig extends Config {

     String username();

     String password();

     String token();
}
