package com.cambrian.mall.product;

import lombok.Data;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import static org.junit.Assert.assertEquals;

/**
 * @author kuma 2020-10-30
 */
public class BeanUtilsCopyTest {

    @Test
    public void copy() {

        Address address = new Address();
        address.setCountry("China");
        address.setCountryCode("+86");
        address.setDetail("xxx-xxxx");

        Demo demo = new Demo();
        demo.setId(111L);
        demo.setName("test");
        demo.setAddress(address);

        DemoTO demoTo = new DemoTO();
        AddressTO addressTo = new AddressTO();

        BeanUtils.copyProperties(address, addressTo);
        assertEquals(address.getCountryCode(), addressTo.getCountryCode());
        assertEquals(address.getCountry(), addressTo.getCountry());
        assertEquals(address.getDetail(), addressTo.getDetail());

        BeanUtils.copyProperties(demo, demoTo);
//        assertEquals(address.countryCode, demoTo.getAddress().countryCode);
    }

    @Data
    class Demo {
        Long id;
        String name;
        Address address;
    }

    @Data
    class DemoTO {
        Long id;
        String name;
        AddressTO address;
    }

    @Data
    class Address {
        String detail;
        String country;
        String countryCode;
    }

    @Data
    class AddressTO {
        String detail;
        String country;
        String countryCode;
    }
}
