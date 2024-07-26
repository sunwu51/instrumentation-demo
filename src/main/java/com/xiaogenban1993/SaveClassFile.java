package com.xiaogenban1993;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * @author Frank
 * @date 2024/7/26 13:17
 */
public class SaveClassFile {
    public static void save(Instrumentation instrumentation, Class<?> clazz) throws UnmodifiableClassException {

        instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className.replace("/", ".").equals(clazz.getName())) {
                try {
                    try (FileOutputStream newBB = new FileOutputStream(clazz.getSimpleName() +".class")) {
                        newBB.write(classfileBuffer);
                    }
                    System.out.println("Successfully wrote modified bytecode to " + clazz.getSimpleName() +".class");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return classfileBuffer;
        }, true);

        instrumentation.retransformClasses(clazz);
    }
}
