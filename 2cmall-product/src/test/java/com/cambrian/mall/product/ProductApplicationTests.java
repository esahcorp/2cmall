package com.cambrian.mall.product;

import com.cambrian.mall.product.entity.BrandEntity;
import com.cambrian.mall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApplicationTests {

	@Resource
	BrandService brandService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	@Transactional
	public void save() {
		BrandEntity brand= new BrandEntity();
		brand.setName("Apple");
		brand.setShowStatus(2);
		boolean success = brandService.save(brand);
		assertTrue(success);
		int status = jdbcTemplate.queryForObject( "select show_status from `2cmall_pms`.pms_brand where name = 'Apple'", Integer.class);
		assertEquals(2, status);
	}

}
