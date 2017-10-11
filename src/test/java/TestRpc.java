import com.wing.service.RpcTestService;
import com.wing.service.model.RpcTestModel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by wanghl on 2017/10/11.
 */
@Test
@ContextConfiguration(locations = { "classpath:rpc-invoke-config-client.xml" })
public class TestRpc extends AbstractTestNGSpringContextTests {
    @Test(threadPoolSize = 3, invocationCount = 6)
    void testStrConcat() throws IOException {
        RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService");
        String result = rpcTestService.strConcatUUID("wanghl");
        System.out.println("testStrConcat:"+result);
    }
    @Test(threadPoolSize = 3, invocationCount = 6)
    void testAddition() throws IOException {
        RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService");
        Integer result = rpcTestService.addition(100,101);
        System.out.println("testAddition:"+result);
    }
    @Test(threadPoolSize = 3, invocationCount = 6)
    void testGetRpcModel() throws IOException {
        RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService");
        RpcTestModel result = rpcTestService.getRpcModel(1l);
        System.out.println("testGetRpcModel:"+result.getId()+"|"+result.getModelName());
    }
}
