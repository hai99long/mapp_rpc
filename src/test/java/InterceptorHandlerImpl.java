
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InterceptorHandlerImpl implements InvocationHandler {

	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		System.out.println("start ...");
		//Object returnObj = method.invoke(obj, args);
		System.out.println("end ...");
		//return returnObj;
		return "";
	}

}
