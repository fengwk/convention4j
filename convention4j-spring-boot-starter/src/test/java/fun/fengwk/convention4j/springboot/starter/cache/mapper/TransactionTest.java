package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fengwk
 */
@Component
public class TransactionTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGen;
    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test() {
        UserPO userDO = new UserPO();
        userDO.setUsername("username_");
        userDO.setEmail("email_");
        userDO.setMobile("mobile_");
        userDO.setPassword("password_");
        userDO.setAge(19);
        userDO.setCity("hangzhou");
        userDO.setId(idGen.next(getClass()));
        assert userMapper.insert(userDO) > 0;
        UserPO found = userMapper.findById(userDO.getId());
        found.setPassword("123");
        assert userMapper.updateByIdSelective(userDO) > 0;
        assert userMapper.findById(userDO.getId()) != null;
    }

}
