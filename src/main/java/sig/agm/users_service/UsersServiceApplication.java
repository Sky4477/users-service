package sig.agm.users_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import sig.agm.users_service.Model.AppUser;
import sig.agm.users_service.Service.Interface.AccoutService;

@SpringBootApplication
@EnableDiscoveryClient
public class UsersServiceApplication {
	@Value("${user.root.name}")
	private String rootName;

	@Value("${user.root.password}")
	private String rootPassword;

	@Value("${user.root.email}")
	private String rootEmail;

	public static void main(String[] args) {
		SpringApplication.run(UsersServiceApplication.class, args);
	}


	@Bean
	CommandLineRunner start(AccoutService service) throws Exception {
		return args -> {
			AppUser root =new AppUser(rootName, rootPassword, rootEmail);
			if (service.findUser(root.getUsername()) == null){
				service.createAccount(root);
				service.makeUserToRoots(root.getUsername());
			}else {
				System.out.println("user already exist");
			}

		};
	}

}
