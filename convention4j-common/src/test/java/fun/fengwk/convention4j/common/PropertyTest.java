package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.Property;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author fengwk
 */
public class PropertyTest {

    @Test
    public void test() {
        assert Property.of(User::getAddr).dot(Address::getCity).getPath().equals("addr.city");
    }
    
    static class User {
        
        Address addr;

        Address getAddr() {
            return addr;
        }
        
    }
    
    static class Address {
        
        String city;
        String street;

        String getCity() {
            return city;
        }

        String getStreet() {
            return street;
        }
        
    }
    
}
