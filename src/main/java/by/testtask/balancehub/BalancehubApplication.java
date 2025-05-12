package by.testtask.balancehub;

import by.testtask.balancehub.utils.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BalancehubApplication {

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(BalancehubApplication.class, args);
	}

}
