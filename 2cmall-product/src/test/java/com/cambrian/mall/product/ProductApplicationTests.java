package com.cambrian.mall.product;

import com.cambrian.mall.product.dao.AttrGroupDao;
import com.cambrian.mall.product.dao.SkuSaleAttrValueDao;
import com.cambrian.mall.product.service.BrandService;
import com.cambrian.mall.product.vo.SkuItemSaleAttrVO;
import com.cambrian.mall.product.vo.SpuItemAttrGroupVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedissonClient redisson;

	@Autowired
	AttrGroupDao attrGroupDao;

	@Autowired
	SkuSaleAttrValueDao skuSaleAttrValueDao;

	@Test
	public void contextLoads() {
		assertNotNull(brandService);
		assertNotNull(jdbcTemplate);
		assertNotNull(stringRedisTemplate);
		assertNotNull(redisson);
	}

	@Test
	public void listGroupWithAttrValue() {
		List<SpuItemAttrGroupVO> items = attrGroupDao.listGroupWithAttrValue(225L, 3L);
		assertFalse(items.isEmpty());
		System.out.println(items);
		List<SpuItemAttrGroupVO> empty = attrGroupDao.listGroupWithAttrValue(225L, 100L);
		assertTrue(empty.isEmpty());
	}

	@Test
	public void listSaleAttrWithValueList() {
		List<SkuItemSaleAttrVO> attrs = skuSaleAttrValueDao.selectSaleAttrWithValueListBySpuId(3L);
		assertFalse(attrs.isEmpty());
		assertFalse(attrs.get(0).getAttrValues().isEmpty());
		assertFalse(attrs.get(0).getAttrValues().get(0).getSkus().isEmpty());
		System.out.println(attrs);
	}

}
