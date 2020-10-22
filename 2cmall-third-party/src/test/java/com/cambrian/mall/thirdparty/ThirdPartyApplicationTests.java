package com.cambrian.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdPartyApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	OSSClient ossClient;

	@Test
	public void testFileUpload() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("images/sun.jpg");
		ossClient.putObject("2cmall", "sun.jpg", classPathResource.getInputStream());
		ossClient.shutdown();
		System.out.println("上传完成...");
	}

}
