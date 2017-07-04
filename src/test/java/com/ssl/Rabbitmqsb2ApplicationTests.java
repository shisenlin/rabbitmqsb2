package com.ssl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rabbitmqsb2ApplicationTests {

	private RestTemplate restTemplate;
	@Before
	public void setUp(){
		restTemplate = new RestTemplate();
		
	}
	@Test
	public void contextLoads() {
	}

}
