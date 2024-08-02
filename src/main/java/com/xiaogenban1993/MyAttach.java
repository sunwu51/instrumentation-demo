package com.xiaogenban1993;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Frank
 * @date 2024/7/26 19:19
 */
public class MyAttach {
    public static void main(String[] args) throws Exception {
        String jdkVersion = System.getProperty("java.version");
        String myLoaderName = "customerClassLoader";
        if (jdkVersion.startsWith("1.") && !MyAttach.class.getClassLoader().toString().equals(myLoaderName)) {
            MyClassLoader classLoader = new MyClassLoader(myLoaderName,
                    new URL[]{toolsJarUrl(), currentUrl()},
                    ClassLoader.getSystemClassLoader().getParent());
            Class<?> mainClass = Class.forName(MyAttach.class.getName(), true, classLoader);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
            return;
        }
        // Get the jvm process PID from args[0] or manual input
        // And get the spring http port from manual input
        String pid = null;
        Scanner scanner = new Scanner(System.in);

        if (args.length > 0) {
            pid = args[0].trim();
            try {
                Integer.parseInt(pid);
            } catch (Exception e) {
                System.err.println("The pid should be integer.");
                throw e;
            }
        } else {
            List<VirtualMachineDescriptor> jps = VirtualMachine.list();
            jps.sort(Comparator.comparing(VirtualMachineDescriptor::displayName));
            int i = 0;
            for (; i < jps.size(); i++) {
                System.out.printf("[%s] %s %s%n", i, jps.get(i).id(), jps.get(i).displayName());
            }
            System.out.printf("[%s] %s%n", i, "Custom PID");
            System.out.println(">>>>>>>>>>>>Please enter the serial number");

            while (true) {
                int index = scanner.nextInt();
                if (index < 0 || index > i) continue;
                if (index == i) {
                    System.out.println(">>>>>>>>>>>>Please enter the PID");
                    pid = String.valueOf(scanner.nextInt());
                    break;
                }
                pid = jps.get(index).id();
                break;
            }
        }
        System.out.printf("============The PID is %s%n", pid);

        System.out.println(">>>>>>>>>>>>Please enter the args");
        String arg = scanner.nextLine();
        arg = scanner.nextLine();
        String curJarPath = Paths.get(currentUrl().toURI()).toString();
        try {
            VirtualMachine jvm = VirtualMachine.attach(pid);
            jvm.loadAgent(curJarPath, arg);
            jvm.detach();
        } catch (Exception e) {
            if (!Objects.equals(e.getMessage(), "0")) {
                throw e;
            }
        }
        System.out.println("============Attach finish");
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.printf("thread: %s, args: %s, classLoader: %s%n", Thread.currentThread().getName(), args, MyAttach.class.getClassLoader());
    }


    private static URL toolsJarUrl() throws Exception {
        String javaHome = System.getProperty("java.home");
        File toolsJarFile = new File(javaHome, "../lib/tools.jar");
        if (!toolsJarFile.exists()) {
            throw new Exception("tools.jar not found at: " + toolsJarFile.getPath());
        }
        return toolsJarFile.toURI().toURL();
    }

    private static URL currentUrl() {
        ProtectionDomain domain = MyAttach.class.getProtectionDomain();
        CodeSource codeSource = domain.getCodeSource();
        return codeSource.getLocation();
    }
}
