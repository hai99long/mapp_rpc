import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;

/**
 * Created by wanghl on 2016/10/13.
 */
public class TestJavassist {
    public void testPrint(String name){
        System.out.println(name);
    }

    public static  void main(String[] args) throws Exception{
        Class clazz = TestJavassist.class;
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get(clazz.getName());

            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method mt:declaredMethods) {
                String modifier = Modifier.toString(mt.getModifiers());
                Class<?> returnType = mt.getReturnType();
                String name = mt.getName();
                Class<?>[] parameterTypes = mt.getParameterTypes();

                System.out.print("\n"+modifier+"1 "+returnType.getName()+" "+name+" (");


                //CtMethod[] declaredMethods1 = cc.getDeclaredMethods();
                CtMethod ctm = cc.getDeclaredMethod(name);
                MethodInfo methodInfo = ctm.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attribute = (LocalVariableAttribute)codeAttribute.getAttribute(LocalVariableAttribute.tag);
                int pos = Modifier.isStatic(ctm.getModifiers()) ? 0 : 1;
                for (int i=0;i<ctm.getParameterTypes().length;i++) {
                    System.out.print(parameterTypes[i]+"2 "+attribute.variableName(i+pos));
                    if (i<ctm.getParameterTypes().length-1) {
                        System.out.print(",");
                    }
                }

                System.out.print(")");

                Class<?>[] exceptionTypes = mt.getExceptionTypes();
                if (exceptionTypes.length>0) {
                    System.out.print(" throws ");
                    int j=0;
                    for (Class<?> cl:exceptionTypes) {
                        System.out.print(cl.getName());
                        if (j<exceptionTypes.length-1) {
                            System.out.print(",");
                        }
                        j++;
                    }
                }
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
